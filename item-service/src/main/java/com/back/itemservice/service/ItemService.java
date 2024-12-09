package com.back.itemservice.service;


import com.back.itemservice.config.AES128Config;
import com.back.itemservice.domain.Item;
import com.back.itemservice.dto.*;
import com.back.itemservice.repository.ItemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final AES128Config     aes128Config;
    private final JavaMailSender   mailSender;
    private final FeignItemService feignItemService;




    @Transactional
    @CacheEvict(cacheNames = "itemCache", allEntries = true, cacheManager = "cacheManager")
    public ItemResponseDto saveItem(ItemRequestDto saveRequestDto) {
        if (itemRepository.existsByItemName(saveRequestDto.getItemName())) {
            throw new IllegalArgumentException("이미 등록된 상품명입니다");
        }
        Item save = itemRepository.save(Item.dtoToEntity(saveRequestDto));
        return new ItemResponseDto(save);
    }

    @Cacheable(cacheNames = "itemCache", key = "'item:' + args[0]", cacheManager = "cacheManager")
    public ItemDetailResponseDto getItemDetail(Long itemId) {
        ItemDetailResponseDto dtoItem = itemRepository.findById(itemId)
                .map(ItemDetailResponseDto::entityFromDTO)
                .orElseThrow(() -> new IllegalArgumentException("Item not Found"));
        return dtoItem;
    }


    @Cacheable(cacheNames = "itemAllCache", cacheManager = "cacheManager")
    public List<ItemResponseDto> getItems(int page,
                                          int size) {
        Pageable   pageable = PageRequest.of(page - 1, size);
        Page<Item> itemPage = itemRepository.findAllByOrderByCreatedAtDesc(pageable);

        return itemPage.getContent()
                .stream()
                .map(ItemResponseDto::new)
                .collect(Collectors.toList());
    }


    @Transactional
    @CachePut(cacheNames = "itemCache", key = "'item:' + args[0]", cacheManager = "cacheManager")
    @CacheEvict(cacheNames = "itemAllCache", allEntries = true)
    public ItemDetailResponseDto updateItem(Long itemId,
                                            ItemRequestDto requestDto) {
        log.info("itemId = {}, requestDto = {}", itemId, requestDto);

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Item Not Found"));
        item.updateItemDetails(requestDto);
        Item updatedItem = itemRepository.save(item);

        return ItemDetailResponseDto.entityFromDTO(updatedItem);
    }

    public ItemResponseDto restockItem(Long itemId,
                                       Integer reStockQuantity) throws InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Item Not Found"));
        item.addQuantity(reStockQuantity);
        itemRepository.saveAndFlush(item);

        // itemId이 같은 위시리스트를 가져옴
        List<WishList> wishLists = feignItemService.eurekaWishListByItemId(itemId);

        List<Long> userIdWishList = new ArrayList<>();

        for (WishList wishList : wishLists) {
            userIdWishList.add(wishList.getUserId());
        }

        Queue<User> userQueue = new ArrayDeque<>();
        if (!wishLists.isEmpty()) {
            userQueue = feignItemService.eurekaGetUserByQueue(userIdWishList);
        }

        while (!userQueue.isEmpty()) {
            User   user  = userQueue.poll();
            String email = aes128Config.decryptAes(user.getEmail());
            reStockMailSend(email, item.getItemName());
        }
        return new ItemResponseDto(item);
    }


    @Async
    public void reStockMailSend(String emailParam,
                                String itemName) {
        SimpleMailMessage email   = new SimpleMailMessage();
        String            subject = "[ 상품 재입고 알림 입니다. ]";
        String            message = "<h2>위시 리스트에 등록하신 " + itemName + " 이 재입고 되었습니다.</h2> \n 감사합니다";

        email.setTo(emailParam);
        email.setSubject(subject);
        email.setText(message);
        mailSender.send(email);
    }

    @Transactional
    @CacheEvict(value = "itemAllCache", allEntries = true, cacheManager = "cacheManager")
    public void deleteItem(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Item Not Found"));
        itemRepository.delete(item);
    }

    // Feign을 이용한 통신

    public ItemDetailResponseDto getEurekaItemDetail(Long itemId) {
        return itemRepository.findById(itemId)
                .map(ItemDetailResponseDto::entityFromDTO)
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