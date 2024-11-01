package com.back.ecommerceorderplatform.item.service;


import com.back.ecommerceorderplatform.item.domain.Item;
import com.back.ecommerceorderplatform.item.dto.ItemDetailResponseDto;
import com.back.ecommerceorderplatform.item.dto.ItemRequestDto;
import com.back.ecommerceorderplatform.item.dto.ItemResponseDto;
import com.back.ecommerceorderplatform.item.dto.ItemSearchCondition;
import com.back.ecommerceorderplatform.item.repository.ItemRepository;
import com.back.ecommerceorderplatform.item.repository.ItemRepositoryImpl;
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
        return itemRepository.save(new Item(saveRequestDto));
    }


    @Cacheable(cacheNames = "itemAllCache",
            key = "'item:itemName:' + #searchCondition.itemName + ':quantityLoe:' + #searchCondition.itemQuantityLoe + ':priceLoe:' + #searchCondition.itemPriceLoe + ':priceGoe:' + #searchCondition.itemPriceGoe")
    public Page<ItemResponseDto> itemList(ItemSearchCondition searchCondition,
                                          Pageable pageable) {
        return itemRepositoryImpl.itemList(searchCondition, pageable);
    }


    @Transactional
    @Cacheable(cacheNames = "itemCache", key = "'item:' + args[0]", cacheManager = "cacheManager")
    public ItemDetailResponseDto getItemDetail(Long itemId) {
        ItemDetailResponseDto detailItem = itemRepositoryImpl.getItemDetail(itemId)
                .orElseThrow(() -> new NoSuchElementException("Item Not Found"));
        return detailItem;
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
        return ItemDetailResponseDto.from(updatedItem);
    }

    @Transactional
    @CacheEvict(value = "itemAllCache", allEntries = true, cacheManager = "cacheManager")
    public void deleteItem(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Item Not Found"));
        itemRepository.delete(item);
    }

}