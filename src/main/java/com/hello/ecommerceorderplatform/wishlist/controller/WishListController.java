package com.hello.ecommerceorderplatform.wishlist.controller;

import com.hello.ecommerceorderplatform.user.domain.User;
import com.hello.ecommerceorderplatform.user.security.UserDetailsImpl;
import com.hello.ecommerceorderplatform.wishlist.dto.WishListItemDto;
import com.hello.ecommerceorderplatform.wishlist.dto.WishListResponseDto;
import com.hello.ecommerceorderplatform.wishlist.service.WishListService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishListController {

    private final WishListService wishListService;

    /**
     * 위시 리스트 조회
     */
    @GetMapping
    public WishListResponseDto getWishListItems() {
        User user = getCurrentUser();
        return wishListService.getWishListItems(user);
    }

    /**
     * 위시 리스트에 Item 담기
     */
    @PostMapping
    public void addWishListItem(
            @RequestBody WishListItemDto wishListItemDto) {
        User user = getCurrentUser();
        log.info("wishListItemDto = {}", wishListItemDto);
        wishListService.addWishListItem(user, wishListItemDto);
    }


    /**
     * 위시 리스트 내부 Item 개수 수정
     */

    @PutMapping("/{itemId}")
    public void updateWishListItem(
            @PathVariable Long itemId,
            @RequestBody WishListItemDto wishListItemDto) {
        User user = getCurrentUser();
        wishListService.updateWishListItem(user, itemId, wishListItemDto);
    }


    /**
     * 위시 리스트 Item 취소
     */
    @DeleteMapping("/{itemId}")
    public void removeWishListItem(
            @PathVariable Long itemId) {
        User user = getCurrentUser();
        wishListService.removeWishListItem(user, itemId);
    }


    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User            user        = userDetails.getUser();
        log.info("user = {}", user);
        return user;
    }

}
