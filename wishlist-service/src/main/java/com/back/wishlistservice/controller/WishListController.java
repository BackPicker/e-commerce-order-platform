package com.back.wishlistservice.controller;


import com.back.common.dto.ResponseMessage;
import com.back.common.utils.ParseRequestUtil;
import com.back.wishlistservice.domain.WishList;
import com.back.wishlistservice.dto.WishListRequestDto;
import com.back.wishlistservice.service.WishListService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishListController {

    private final WishListService wishListService;

    @GetMapping
    public ResponseEntity<ResponseMessage> getWishListItems(HttpServletRequest request) {
        Long userId = new ParseRequestUtil().extractUserIdFromRequest(request);

        return wishListService.getWishListItems(userId);
    }

    @PostMapping
    public ResponseEntity<ResponseMessage> addWishListItem(
            @RequestBody
            WishListRequestDto wishListDto,
            HttpServletRequest request) {
        Long userId = new ParseRequestUtil().extractUserIdFromRequest(request);

        wishListService.addWishListItem(userId, wishListDto);

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
            WishListRequestDto wishListDto,
            HttpServletRequest request) {
        Long userId = new ParseRequestUtil().extractUserIdFromRequest(request);

        wishListService.updateWishListItem(wishListItemId, userId, wishListDto);

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

    /// /////////////////////////////////
    @GetMapping("/api/wishlist/eureka/getUser/{itemId}/")
    public List<WishList> eurekaWishListByItemId(
            @PathVariable("itemId")
            Long itemId) {
        return wishListService.eurekaWishListByItemId(itemId);
    }

}
