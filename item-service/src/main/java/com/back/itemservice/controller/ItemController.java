package com.back.itemservice.controller;


import com.back.common.dto.ResponseMessage;
import com.back.itemservice.domain.Item;
import com.back.itemservice.dto.ItemDetailResponseDto;
import com.back.itemservice.dto.ItemRequestDto;
import com.back.itemservice.dto.ItemResponseDto;
import com.back.itemservice.dto.ItemSearchCondition;
import com.back.itemservice.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/list")
    public Page<ItemResponseDto> itemList(ItemSearchCondition searchCondition,
                                          Pageable pageable) {
        return itemService.itemList(searchCondition, pageable);
    }


    @PostMapping("/add")
    public ResponseEntity<ResponseMessage> saveItem(
            @RequestBody
            @Valid
            ItemRequestDto saveRequestDto) {
        Item saveItem = itemService.saveItem(saveRequestDto);

        ResponseMessage responseMessage = ResponseMessage.builder()
                .data(saveItem)
                .statusCode(201)
                .detailMessage("상품 저장이 완료되었습니다")
                .build();

        return ResponseEntity.ok(responseMessage);
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