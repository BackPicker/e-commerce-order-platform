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
     * 모든 WishList 를 불러오기
     * @return
     */
    @GetMapping
    public WishListResponseDto getWishListItems() {
        User user = getCurrentUser();
        return wishListService.getWishListItems(user);
    }

    /**
     * WishList 하나 가져오기
     * @param wishListItemDto
     */
    @PostMapping
    public void addWishListItem(
            @RequestBody WishListItemDto wishListItemDto) {

        User user = getCurrentUser();
        wishListService.addWishListItem(user, wishListItemDto);
    }

    /**
     * WishList 수정
     */
    @PutMapping("/{itemId}")
    public void updateWishListItem(
            @PathVariable Long itemId,
            @RequestBody WishListItemDto wishListItemDto) {
        User user = getCurrentUser();


        wishListService.updateWishListItem(user, itemId, wishListItemDto);
    }

    /**
     * WishList 삭제
     * @param itemId
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
        return ((UserDetailsImpl) authentication.getPrincipal()).getUser();
    }
}
