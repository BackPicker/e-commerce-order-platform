package com.back.itemservice.service;


import com.back.itemservice.config.AES128Config;
import com.back.itemservice.domain.Item;
import com.back.itemservice.dto.*;
import com.back.itemservice.exception.ItemNotFoundException;
import com.back.itemservice.repository.ItemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final AES128Config               aes128Config;
    private final JavaMailSender             mailSender;
    private final FeignItemToUserService     feignItemToUserService;
    private final FeignItemToWishListService feignItemToWishListService;

    @Transactional
    @CacheEvict(cacheNames = "items", allEntries = true)
    public ItemResponseDto saveItem(ItemRequestDto saveRequestDto) {
        if (itemRepository.existsByItemName(saveRequestDto.getItemName())) {
            throw new IllegalArgumentException("이미 등록된 상품명입니다");
        }
        Item save = itemRepository.save(Item.dtoToEntity(saveRequestDto));
        return new ItemResponseDto(save);
    }

    @Cacheable(cacheNames = "items", key = "#itemId")
    public ItemDetailResponseDto getItemDetail(Long itemId) {
        Item item = findItemById(itemId);
        return ItemDetailResponseDto.entityFromDTO(item);
    }

    @Cacheable(cacheNames = "itemsList")
    public List<ItemResponseDto> getItems(int page, int size) {
        PageRequest pageable = PageRequest.of(page - 1, size);
        Page<Item> itemPage = itemRepository.findAllByOrderByCreatedAtDesc(pageable);
        return itemPage.getContent()
                .stream()
                .map(ItemResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    @CachePut(cacheNames = "items", key = "#itemId")
    @CacheEvict(cacheNames = "itemsList", allEntries = true)
    public ItemDetailResponseDto updateItem(Long itemId, ItemRequestDto requestDto) {
        Item item = findItemById(itemId);
        item.updateItemDetails(requestDto);
        Item updatedItem = itemRepository.save(item);
        return ItemDetailResponseDto.entityFromDTO(updatedItem);
    }

    @Transactional
    public ItemResponseDto restockItem(Long itemId, Integer reStockQuantity) {
        Item item = findItemById(itemId);
        item.addQuantity(reStockQuantity);
        itemRepository.saveAndFlush(item);
        sendRestockNotifications(item);
        return new ItemResponseDto(item);
    }

    private void sendRestockNotifications(Item item) {
        List<WishList> wishLists = feignItemToWishListService.eurekaWishListByItemId(item.getId());
        List<Long> userIdWishList = wishLists.stream()
                .map(WishList::getUserId)
                .collect(Collectors.toList());

        if (!userIdWishList.isEmpty()) {
            Queue<User> userQueue = feignItemToUserService.eurekaGetUserByQueue(userIdWishList);
            userQueue.forEach(user -> sendRestockEmail(user, item.getItemName()));
        }
    }

    private void sendRestockEmail(User user, String itemName) {
        try {
            String email = aes128Config.decryptAes(user.getEmail());
            reStockMailSend(email, itemName);
        } catch (Exception e) {
            log.error("Failed to send restock email to user: " + user.getId(), e);
        }
    }

    @Async
    public void reStockMailSend(String emailParam, String itemName) {
        SimpleMailMessage email   = new SimpleMailMessage();
        String            subject = "[ 상품 재입고 알림 입니다. ]";
        String            message = "<h2>위시 리스트에 등록하신 " + itemName + " 이 재입고 되었습니다.</h2> \n 감사합니다";

        email.setTo(emailParam);
        email.setSubject(subject);
        email.setText(message);
        mailSender.send(email);
    }

    @Transactional
    @CacheEvict(cacheNames = {"items", "itemsList"}, allEntries = true)
    public void deleteItem(Long itemId) {
        Item item = findItemById(itemId);
        itemRepository.delete(item);
    }

    private Item findItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Item with id " + itemId + " not found"));
    }

    // Feign을 이용한 통신
    public Item getEurekaItemDetail(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not Found"));
    }

    public ItemQuantityResponseDto getEurekaItemQuantity(Long itemId) {
        Integer itemQuantity = itemRepository.findItemQuantityByItemId(itemId);
        return new ItemQuantityResponseDto(itemQuantity);
    }

    public void eurekaReduceItemQuantity(Long itemId,
                                         Integer quantity) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Item Not Found"));
        item.reduceQuantity(quantity);
        itemRepository.saveAndFlush(item);
    }

    public void eurekaAddItemQuantity(Long itemId,
                                      Integer orderCount) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Item Not Found"));
        item.addQuantity(orderCount);
        itemRepository.saveAndFlush(item);
    }

}