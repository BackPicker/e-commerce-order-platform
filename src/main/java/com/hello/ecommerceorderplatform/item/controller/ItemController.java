package com.hello.ecommerceorderplatform.item.controller;


import com.hello.ecommerceorderplatform.item.dto.ItemDetailResponseDto;
import com.hello.ecommerceorderplatform.item.dto.ItemRequestDto;
import com.hello.ecommerceorderplatform.item.dto.ItemResponseDto;
import com.hello.ecommerceorderplatform.item.dto.ItemSearchCondition;
import com.hello.ecommerceorderplatform.item.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    // 상품 등록
    @Secured("ROLE_ADMIN")
    @PostMapping("/add")
    public ResponseEntity<Void> saveItem(
            @RequestBody
            @Valid
            ItemRequestDto saveRequestDto) {
        itemService.saveItem(saveRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .build();
    }

    // 전체 ITEM 목록 조회
    @GetMapping("/list")
    public Page<ItemResponseDto> itemList(ItemSearchCondition searchCondition, Pageable pageable) {
        return itemService.itemList(searchCondition, pageable);
    }

    // 상품 상세 조회
    @GetMapping("/{itemId}")
    public ItemDetailResponseDto getItemDetail(
            @PathVariable Long itemId) {
        return itemService.getItemDetail(itemId);
    }

    // 상품 수정
    @Secured("ROLE_ADMIN")
    @PutMapping("/{itemId}")
    public ResponseEntity<Void> updateItem(
            @PathVariable("itemId") Long itemId,
            @RequestBody
            ItemRequestDto requestDto) {
        itemService.updateItemDetail(itemId, requestDto);
        return ResponseEntity.noContent()
                .build();
    }

    // 상품 삭제
    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItemDetail(
            @PathVariable("itemId") Long itemId) {
        itemService.deleteItemDetail(itemId);

        return ResponseEntity.noContent()
                .build();
    }
}