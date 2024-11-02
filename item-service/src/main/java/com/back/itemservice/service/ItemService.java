package com.back.itemservice.service;


import com.back.itemservice.domain.Item;
import com.back.itemservice.dto.ItemDetailResponseDto;
import com.back.itemservice.dto.ItemRequestDto;
import com.back.itemservice.dto.ItemResponseDto;
import com.back.itemservice.dto.ItemSearchCondition;
import com.back.itemservice.repository.ItemRepository;
import com.back.itemservice.repository.ItemRepositoryImpl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepositoryImpl itemRepositoryImpl;
    private final ItemRepository     itemRepository;

    @Transactional
    @Cacheable(cacheNames = "itemCache", key = "'item:'+#result.id")
    public Item saveItem(ItemRequestDto saveRequestDto) {
        if (itemRepositoryImpl.existByItemName(saveRequestDto.getItemName())) {
            throw new IllegalArgumentException("이미 등록된 상품명입니다");
        }
        Item save = itemRepository.save(Item.dtoToEntity(saveRequestDto));
        log.info("save = {}", save);
        return save;
    }


    @Cacheable(cacheNames = "itemAllCache",
            key = "'item:itemName:' + #searchCondition.itemName + ':quantityLoe:' + #searchCondition.itemQuantityLoe + ':priceLoe:' + #searchCondition.itemPriceLoe + ':priceGoe:' + #searchCondition.itemPriceGoe")
    public Page<ItemResponseDto> itemList(ItemSearchCondition searchCondition,
                                          Pageable pageable) {
        return itemRepositoryImpl.itemList(searchCondition, pageable);
    }


    @Cacheable(cacheNames = "itemCache", key = "'item:' + args[0]", cacheManager = "cacheManager")
    public ItemDetailResponseDto getItemDetail(Long itemId) {

        return itemRepository.findById(itemId)
                .map(ItemDetailResponseDto::entityFromDTO)
                .orElseThrow(() -> new IllegalArgumentException("Item not Found"));
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

        // Item을 ItemDetailResponseDto로 변환하여 반환
        return ItemDetailResponseDto.entityFromDTO(updatedItem);
    }

    @Transactional
    @CacheEvict(value = "itemAllCache", allEntries = true, cacheManager = "cacheManager")
    public void deleteItem(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Item Not Found"));
        itemRepository.delete(item);
    }

}