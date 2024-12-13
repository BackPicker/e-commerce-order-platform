package com.back.itemservice.controller;


import com.back.common.dto.ResponseMessage;
import com.back.itemservice.domain.Item;
import com.back.itemservice.dto.*;
import com.back.itemservice.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping("/add")
    public ResponseMessage itemSave(
            @RequestBody
            ItemRequestDto saveRequestDto) {
        ItemResponseDto itemResponseDto = itemService.saveItem(saveRequestDto);
        return ResponseMessage.builder()
                .data(itemResponseDto)
                .statusCode(HttpStatus.CREATED.value())
                .detailMessage("상품 저장이 완료되었습니다")
                .build();
    }

    @GetMapping("/list")
    public ResponseMessage getItems(
            @RequestParam(defaultValue = "1")
            int page,
            @RequestParam(defaultValue = "10")
            int size) {
        List<ItemResponseDto> items = itemService.getItems(page, size);
        return ResponseMessage.builder()
                .data(items)
                .statusCode(HttpStatus.OK.value())
                .detailMessage("상품 목록을 성공적으로 조회했습니다")
                .build();
    }

    @GetMapping("/{itemId}")
    public ResponseMessage getItemDetail(
            @PathVariable
            Long itemId) {
        ItemDetailResponseDto item = itemService.getItemDetail(itemId);
        return ResponseMessage.builder()
                .data(item)
                .statusCode(HttpStatus.OK.value())
                .detailMessage("상품 상세 정보를 성공적으로 조회했습니다")
                .build();
    }

    @PutMapping("/{itemId}")
    public ResponseMessage updateItem(
            @PathVariable
            Long itemId,
            @RequestBody
            ItemRequestDto requestDto) {
        ItemDetailResponseDto updatedItem = itemService.updateItem(itemId, requestDto);
        return ResponseMessage.builder()
                .data(updatedItem)
                .statusCode(HttpStatus.OK.value())
                .detailMessage("상품이 성공적으로 수정되었습니다")
                .build();
    }

    @PutMapping("/{itemId}/restock")
    public ResponseMessage restockItem(
            @PathVariable
            Long itemId,
            @RequestBody
            ReStockItemDTO reStockItemDTO) {
        ItemResponseDto itemResponseDto = itemService.restockItem(itemId, reStockItemDTO.getReStockQuantity());
        return ResponseMessage.builder()
                .data(itemResponseDto)
                .statusCode(HttpStatus.OK.value())
                .detailMessage("상품이 성공적으로 재입고되었습니다")
                .build();
    }

    @DeleteMapping("/{itemId}")
    public ResponseMessage deleteItemDetail(
            @PathVariable
            Long itemId) {
        itemService.deleteItem(itemId);
        return ResponseMessage.builder()
                .statusCode(HttpStatus.OK.value())
                .detailMessage("상품이 성공적으로 삭제되었습니다")
                .build();
    }

    // Eureka

    // 하나 가져오기
    @GetMapping("/eureka/{itemId}")
    public Item getEurekaItemDetail(
            @PathVariable("itemId")
            Long itemId) {
        log.info("getEurekaItemDetail 접근");

        return itemService.getEurekaItemDetail(itemId);
    }

    @GetMapping("/eureka/{itemId}/quantity")
    public ItemQuantityResponseDto getEurekaItemQuantity(
            @PathVariable("itemId")
            Long itemId) {
        return itemService.getEurekaItemQuantity(itemId);

    }

    // 상품 감소
    @PutMapping("/eureka/{itemId}/reduce/{quantity}")
    public void eurekaReduceItemQuantity(
            @PathVariable("itemId")
            Long itemId,
            @PathVariable("quantity")
            Integer quantity) {
        itemService.eurekaReduceItemQuantity(itemId, quantity);
    }    // 상품 수정

    @PutMapping("/eureka/{itemId}/add//{orderCount}")
    public void addItemQuantity(
            @PathVariable("itemId")
            Long itemId,
            @PathVariable("quantity")
            Integer orderCount) {
        itemService.eurekaAddItemQuantity(itemId, orderCount);
    }


}