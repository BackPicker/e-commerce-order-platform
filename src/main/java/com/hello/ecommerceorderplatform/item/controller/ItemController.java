package com.hello.ecommerceorderplatform.item.controller;


import com.hello.ecommerceorderplatform.item.dto.ItemListResponseDto;
import com.hello.ecommerceorderplatform.item.dto.ItemResponseDto;
import com.hello.ecommerceorderplatform.item.dto.ItemSearchCondition;
import com.hello.ecommerceorderplatform.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    /**
     * 상품 등록
     */
    public void itemSave() {

    }

    /**
     * 등록되어 있는 상품의 리스트를 보여줌
     */
    @ResponseBody
    @GetMapping("/list")
    public Page<ItemResponseDto> itemList(ItemSearchCondition searchCondition, Pageable pageable) {
        return itemService.itemList(searchCondition, pageable);
    }

    /**
     * 상품 클릭 시 상세 정보를 제공해야 한다
     */
    @GetMapping("/{itemId}")
    public ItemListResponseDto getItemDetail(
            @PathVariable
            Long itemId) {
        return itemService.getItemDetail(itemId);
    }


}
