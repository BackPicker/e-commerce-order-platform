package com.hello.ecommerceorderplatform.item.service;


import com.hello.ecommerceorderplatform.item.dto.ItemDetailResponseDto;
import com.hello.ecommerceorderplatform.item.dto.ItemResponseDto;
import com.hello.ecommerceorderplatform.item.dto.ItemSearchCondition;
import com.hello.ecommerceorderplatform.item.repository.ItemRepositorympl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepositorympl itemRepositorympl;

    public Page<ItemResponseDto> itemList(ItemSearchCondition searchCondition, Pageable pageable) {
        return itemRepositorympl.itemList(searchCondition, pageable);
    }

    public ItemDetailResponseDto getItemDetail(Long itemId) {
        return itemRepositorympl.getItemDetail(itemId);
    }
}