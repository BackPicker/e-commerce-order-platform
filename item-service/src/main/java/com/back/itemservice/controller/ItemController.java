package com.back.itemservice.controller;


import com.back.common.dto.ResponseMessage;
import com.back.itemservice.dto.ItemDetailResponseDto;
import com.back.itemservice.dto.ItemRequestDto;
import com.back.itemservice.dto.ItemResponseDto;
import com.back.itemservice.repository.ItemRepository;
import com.back.itemservice.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService    itemService;
    private final ItemRepository itemRepository;

    @PostMapping("/add")
    public ResponseEntity<ResponseMessage> itemSave(
            @RequestBody
            ItemRequestDto saveRequestDto) {
        ItemResponseDto itemResponseDto = itemService.saveItem(saveRequestDto);

        ResponseMessage responseMessage = ResponseMessage.builder()
                .data(itemResponseDto)
                .statusCode(201)
                .detailMessage("상품 저장이 완료되었습니다")
                .build();

        return ResponseEntity.ok(responseMessage);
    }

    @GetMapping("/list")
    public List<ItemResponseDto> getItems(
            @RequestParam(defaultValue = "1")
            int page,
            @RequestParam(defaultValue = "10")
            int size) {

        return itemService.getItems(page, size);
    }

    @GetMapping("/{itemId}")
    public ItemDetailResponseDto getItemDetail(
            @PathVariable
            Long itemId) {

        return itemService.getItemDetail(itemId);
    }

    @PutMapping("/{itemId}")
    public ResponseEntity<ResponseMessage> updateItem(
            @PathVariable("itemId")
            Long itemId,
            @RequestBody
            ItemRequestDto requestDto) {
        ItemDetailResponseDto updatedItem = itemService.updateItem(itemId, requestDto);

        ResponseMessage responseMessage = ResponseMessage.builder()
                .data(updatedItem)
                .statusCode(204)
                .detailMessage("Item 이 수정되었습니다")
                .build();
        return ResponseEntity.status(204)
                .body(responseMessage);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<ResponseMessage> deleteItemDetail(
            @PathVariable("itemId")
            Long itemId) {
        itemService.deleteItem(itemId);

        ResponseMessage responseMessage = ResponseMessage.builder()
                .data(null)
                .statusCode(200)
                .resultMessage("Item 삭제가 완료되었습니다")
                .build();
        return ResponseEntity.ok(responseMessage);
    }
}