package com.back.wishlistservice.controller;


import com.back.common.dto.ResponseMessage;
import com.back.wishlistservice.dto.WishListRequestDto;
import com.back.wishlistservice.service.WishListService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishListController {

    private final WishListService wishListService;

    static final Long USER_ID = 1L;

    @GetMapping
    public ResponseEntity<ResponseMessage> getWishListItems(HttpServletRequest request) {
        // Long userId = new ParseRequestUtil().extractUserIdFromRequest(request);

        return wishListService.getWishListItems(USER_ID);
    }

    @PostMapping
    public ResponseEntity<ResponseMessage> addWishListItem(
            @RequestBody
            WishListRequestDto wishListDto) {
        // Long userId = new ParseRequestUtil().extractUserIdFromRequest(request);

        wishListService.addWishListItem(USER_ID, wishListDto);

        ResponseMessage responseMessage = ResponseMessage.builder()
                .statusCode(201)
                .resultMessage("위시리스트 항목 추가 성공")
                .build();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(responseMessage);
    }


    @PutMapping("/{wishListItemId}")
    public ResponseEntity<ResponseMessage> updateWishListItem(
            @PathVariable("wishListItemId")
            Long wishListItemId,
            @RequestBody
            WishListRequestDto wishListDto) {
        // Long userId = new ParseRequestUtil().extractUserIdFromRequest(request);

        wishListService.updateWishListItem(wishListItemId, USER_ID, wishListDto);

        ResponseMessage responseMessage = ResponseMessage.builder()
                .statusCode(200)
                .resultMessage("위시리스트 항목 수정 성공")
                .build();

        return ResponseEntity.ok(responseMessage);
    }


    @DeleteMapping("/{wishListItemId}")
    public ResponseMessage removeWishListItem(
            @PathVariable("wishListItemId")
            Long wishListItemId) {

        return wishListService.removeWishListItem(wishListItemId);
    }

}