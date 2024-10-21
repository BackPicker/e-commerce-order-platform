package com.hello.ecommerceorderplatform.wishlist.service;

import com.hello.ecommerceorderplatform.item.domain.Item;
import com.hello.ecommerceorderplatform.item.exception.InvalidQuantityException;
import com.hello.ecommerceorderplatform.item.exception.ItemNotFoundException;
import com.hello.ecommerceorderplatform.item.repository.ItemRepository;
import com.hello.ecommerceorderplatform.user.domain.User;
import com.hello.ecommerceorderplatform.wishlist.domain.WishList;
import com.hello.ecommerceorderplatform.wishlist.domain.WishListItem;
import com.hello.ecommerceorderplatform.wishlist.dto.WishListItemDto;
import com.hello.ecommerceorderplatform.wishlist.dto.WishListResponseDto;
import com.hello.ecommerceorderplatform.wishlist.repository.WishListItemRepository;
import com.hello.ecommerceorderplatform.wishlist.repository.WishListRepository;
import com.hello.ecommerceorderplatform.wishlist.repository.WishListRepositoryImpl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WishListService {

    private final WishListRepository     wishListRepository;
    private final WishListRepositoryImpl wishListRepositoryImpl;
    private final WishListItemRepository wishListItemRepository;
    private final ItemRepository         itemRepository;

    /**
     * 위시 리스트 조회
     */
    public WishListResponseDto getWishListItems(User user) {
        WishList wishList   = getOrCreateWishList(user);
        long     totalPrice = calculateTotalPrice(wishList);
        return new WishListResponseDto(user.getUsername(), wishList, totalPrice);
    }


    private WishList getOrCreateWishList(User user) {
        if (!wishListRepository.existsById(user.getId())) {
            WishList wishList = new WishList(user);
            wishListRepository.save(wishList);
            return wishList;
        } else {
            return wishListRepository.findByUser(user);
        }
    }

    private long calculateTotalPrice(WishList wishList) {
        return wishList.getWishListItemList()
                .stream()
                .mapToLong(item -> item.totalWishListPrice(item.getItem(), item.getWishListItemQuantity()))
                .sum();
    }

    /**
     * 위시 리스트에 Item 담기
     */
    @Transactional
    public void addWishListItem(User user, WishListItemDto wishListItemDto) {
        if (wishListItemDto.getQuantity() == null || wishListItemDto.getQuantity() <= 0) {
            throw new InvalidQuantityException("수량은 1 이상이어야 합니다.");
        }

        WishList wishList = getOrCreateWishList(user);
        Item item = itemRepository.findById(wishListItemDto.getItemId())
                .orElseThrow(() -> new ItemNotFoundException("아이템을 찾을 수 없습니다."));

        WishListItem wishListItem = new WishListItem(wishListItemDto.getQuantity(), item);
        wishList.addWishListItem(wishListItem);
        wishListRepository.save(wishList);
    }

    /**
     * 위시 리스트 내부 Item 개수 수정
     */

    @Transactional
    public void updateWishListItem(User user, Long itemId, WishListItemDto wishListItemDto) {
        if (wishListItemDto.getQuantity() == null || wishListItemDto.getQuantity() <= 0) {
            throw new InvalidQuantityException("수량은 1 이상이어야 합니다.");
        }

        WishList wishList = getOrCreateWishList(user);
        WishListItem wishListItem = wishList.getWishListItemList()
                .stream()
                .filter(item -> item.getId()
                        .equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ItemNotFoundException("아이템을 찾을 수 없습니다."));

        wishListItem.setWishListItemQuantity(wishListItemDto.getQuantity());
        wishListItemRepository.save(wishListItem);
    }


    /**
     * 위시 리스트 Item 취소
     */

    @Transactional
    public void removeWishListItem(User user, Long itemId) {
        WishList wishList = getOrCreateWishList(user);
        WishListItem wishListItem = wishList.getWishListItemList()
                .stream()
                .filter(item -> item.getId()
                        .equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ItemNotFoundException("아이템을 찾을 수 없습니다."));

        wishList.removeWishListItem(wishListItem);
        wishListRepository.save(wishList);
    }


}
