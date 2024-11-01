package com.back.ecommerceorderplatform.wishlist.controller;

import com.back.common.dto.ResponseMessage;
import com.back.ecommerceorderplatform.user.domain.User;
import com.back.ecommerceorderplatform.user.security.UserDetailsImpl;
import com.back.ecommerceorderplatform.wishlist.dto.WishListItemDeleteRequestDto;
import com.back.ecommerceorderplatform.wishlist.dto.WishListItemDto;
import com.back.ecommerceorderplatform.wishlist.dto.WishListResponseDto;
import com.back.ecommerceorderplatform.wishlist.service.WishListService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishListController {

    private final WishListService wishListService;

    @GetMapping
    public ResponseEntity<WishListResponseDto> getWishListItems() {
        User                user     = getCurrentUser();
        WishListResponseDto response = wishListService.getWishListItems(user);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ResponseMessage> addWishListItem(
            @Valid
            @RequestBody
            WishListItemDto wishListItemDto) {
        User user = getCurrentUser();
        wishListService.addWishListItem(user, wishListItemDto);
        log.info("사용자 {}의 위시리스트에 항목 {}가 추가되었습니다.", user.getId(), wishListItemDto.getItemId());

        ResponseMessage responseMessage = ResponseMessage.builder()
                .statusCode(201)
                .resultMessage("위시리스트 항목 추가 성공")
                .build();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(responseMessage);
    }


    @PutMapping
    public ResponseEntity<ResponseMessage> updateWishListItem(
            @Valid
            @RequestBody
            WishListItemDto wishListItemDto) {
        User user = getCurrentUser();

        wishListService.updateWishListItem(user, wishListItemDto);
        log.info("사용자 {}의 위시리스트에서 item ID {}인 항목이 수정되었습니다.", user.getId(), wishListItemDto.getItemId());

        ResponseMessage responseMessage = ResponseMessage.builder()
                .statusCode(200)
                .resultMessage("위시리스트 항목 수정 성공")
                .build();

        return ResponseEntity.ok(responseMessage);
    }


    @DeleteMapping
    public ResponseEntity<Void> removeWishListItem(
            @RequestBody
            WishListItemDeleteRequestDto wishListItemDeleteRequestDto) {
        User user = getCurrentUser();

        wishListService.removeWishListItem(user, wishListItemDeleteRequestDto.getItemId());
        log.info("사용자 {}의 위시리스트에서 item ID {}인 항목이 삭제되었습니다.", user.getId(), wishListItemDeleteRequestDto.getItemId());

        return ResponseEntity.noContent()
                .build(); // 204 No Content
    }


    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        return ((UserDetailsImpl) authentication.getPrincipal()).getUser();
    }
}
