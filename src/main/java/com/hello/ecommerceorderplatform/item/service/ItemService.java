package com.hello.ecommerceorderplatform.item.service;


import com.hello.ecommerceorderplatform.item.domain.Item;
import com.hello.ecommerceorderplatform.item.dto.ItemDetailResponseDto;
import com.hello.ecommerceorderplatform.item.dto.ItemRequestDto;
import com.hello.ecommerceorderplatform.item.dto.ItemResponseDto;
import com.hello.ecommerceorderplatform.item.dto.ItemSearchCondition;
import com.hello.ecommerceorderplatform.item.repository.ItemRepository;
import com.hello.ecommerceorderplatform.item.repository.ItemRepositoryImpl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepositoryImpl itemRepositoryImpl;
    private final ItemRepository     itemRepository;

    /**
     * 상품 등록
     */
    @Transactional
    @CacheEvict(value = "Items", allEntries = true, cacheManager = "cacheManager")
    public void saveItem(ItemRequestDto saveRequestDto) {
        if (itemRepositoryImpl.existByItemName(saveRequestDto.getItemName())) {
            throw new IllegalArgumentException("이미 등록된 상품명입니다");
        }
        Item item = new Item(saveRequestDto);
        itemRepository.save(item);
    }

    /**
     * 전체 ITEM 목록 조회
     */
    @Cacheable(cacheNames = "Items", key = "'item:page:' + #pageable.getPageNumber() + ':size:' + #pageable.getPageSize()", cacheManager = "cacheManager")
    public Page<ItemResponseDto> itemList(ItemSearchCondition searchCondition, Pageable pageable) {
        return itemRepositoryImpl.itemList(searchCondition, pageable);
    }


    /**
     * 상품 상세 조회
     */
    @Transactional
    @Cacheable(cacheNames = "getItemDetail", key = "'item:itemNo:' + #itemId", cacheManager = "cacheManager")
    public ItemDetailResponseDto getItemDetail(Long itemId) {
        return itemRepositoryImpl.getItemDetail(itemId)
                .orElseThrow(() -> new NoSuchElementException("Item Not Found"));

    }

    /**
     * 상품 수정
     */
    @Transactional
    @CacheEvict(value = "Items", allEntries = true, cacheManager = "cacheManager")
    public void updateItemDetail(Long itemId, ItemRequestDto requestDto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Item Not Found"));
        item.updateItemDetails(requestDto);
        itemRepository.save(item);
    }

    /**
     * 상품 삭제
     *
     * @param itemId
     * @return
     */
    @Transactional
    @CacheEvict(value = "Items", allEntries = true, cacheManager = "cacheManager")
    public void deleteItemDetail(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Item Not Found"));
        itemRepository.delete(item);
    }
}