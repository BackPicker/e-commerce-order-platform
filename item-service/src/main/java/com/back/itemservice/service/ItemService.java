package com.back.itemservice.service;


import com.back.itemservice.domain.Item;
import com.back.itemservice.dto.ItemDetailResponseDto;
import com.back.itemservice.dto.ItemQuantityResponseDto;
import com.back.itemservice.dto.ItemRequestDto;
import com.back.itemservice.dto.ItemResponseDto;
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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

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

    @Transactional
    @CacheEvict(value = "itemAllCache", allEntries = true, cacheManager = "cacheManager")
    public void deleteItem(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Item Not Found"));
        itemRepository.delete(item);
    }

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