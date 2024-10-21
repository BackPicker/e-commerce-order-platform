package com.hello.ecommerceorderplatform.item.service;


import com.hello.ecommerceorderplatform.item.dto.ItemListResponseDto;
import com.hello.ecommerceorderplatform.item.dto.ItemResponseDto;
import com.hello.ecommerceorderplatform.item.dto.ItemSearchCondition;
import com.hello.ecommerceorderplatform.item.repository.ItemRepositorympl;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepositorympl itemRepositorympl;


    @Cacheable(cacheNames = "Items", key = "'item:page:' + (#pageable.getPageNumber()!= null ? #pageable.getPageNumber() : 'null') + ':size:' + #pageable.getPageSize()", cacheManager = "cacheManager")
    public Page<ItemResponseDto> itemList(ItemSearchCondition searchCondition, Pageable pageable) {
        return itemRepositorympl.itemList(searchCondition, pageable);
    }

    public ItemListResponseDto getItemDetail(Long itemId) {
        return itemRepositorympl.getItemDetail(itemId);
    }
}