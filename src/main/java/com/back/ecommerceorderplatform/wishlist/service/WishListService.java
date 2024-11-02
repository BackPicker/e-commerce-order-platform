package com.back.ecommerceorderplatform.wishlist.service;

import com.back.ecommerceorderplatform.user.domain.User;
import com.back.ecommerceorderplatform.wishlist.domain.WishList;
import com.back.ecommerceorderplatform.wishlist.domain.WishListItem;
import com.back.ecommerceorderplatform.wishlist.dto.WishListItemDto;
import com.back.ecommerceorderplatform.wishlist.dto.WishListResponseDto;
import com.back.ecommerceorderplatform.wishlist.repository.WishListItemRepository;
import com.back.ecommerceorderplatform.wishlist.repository.WishListItemRepositoryImpl;
import com.back.ecommerceorderplatform.wishlist.repository.WishListRepository;
import com.back.ecommerceorderplatform.wishlist.repository.WishListRepositoryImpl;
import com.back.itemservice.domain.Item;
import com.back.itemservice.exception.InvalidQuantityException;
import com.back.itemservice.exception.ItemNotFoundException;
import com.back.itemservice.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class WishListService {

    private final WishListRepository     wishListRepository;
    private final WishListRepositoryImpl wishListRepositoryImpl;

    private final WishListItemRepository     wishListItemRepository;
    private final WishListItemRepositoryImpl wishListItemRepositoryImpl;

    private final ItemRepository itemRepository;

    /**
     * 공용 메서드
     */
    @Transactional
    public WishList getOrCreateWishList(User user) {
        return wishListRepositoryImpl.findByUserId(user.getId())
                .orElseGet(() -> wishListRepository.save(new WishList(user)));
    }


    @Transactional
    public WishListResponseDto getWishListItems(User user) {
        WishList wishList   = getOrCreateWishList(user);
        long     totalPrice = calculateTotalPrice(wishList);
        return new WishListResponseDto(user.getUsername(), wishList, totalPrice);
    }


    private long calculateTotalPrice(WishList wishList) {
        return wishList.getWishListItemList()
                .stream()
                .mapToLong(item -> item.totalWishListPrice(item.getItem(), item.getWishListItemQuantity()))
                .sum();
    }


    @Transactional
    public void addWishListItem(User user,
                                WishListItemDto wishListItemDto) {
        log.info("wishListItemDto = {}", wishListItemDto);
        validateQuantity(wishListItemDto.getQuantity());

        // 아이템을 찾기
        Item item = (Item) itemRepository.findById(wishListItemDto.getItemId())
                .orElseThrow(() -> new ItemNotFoundException("아이템을 찾을 수 없습니다."));
        log.info("item = {}", item);

        // 위시리스트 가져오기
        WishList wishList = getOrCreateWishList(user);

        // WishListItem 생성
        WishListItem wishListItem = new WishListItem(wishListItemDto.getQuantity(), item);
        wishListItem.setWishList(wishList);

        // 위시리스트에 아이템 추가
        wishListItemRepository.save(wishListItem);
        wishList.addWishListItem(wishListItem);

        // 위시리스트 저장
        wishListRepository.save(wishList); // 이 호출로 인해 연관된 wishListItem이 저장됨

    }


    @Transactional
    public void removeWishListItem(User user,
                                   Long itemId) {
        // 위시리스트 가져오기
        WishList wishList = getOrCreateWishList(user);
        // 가져온 위시리스트로 wishListItem 가져오기
        WishListItem wishListItem = findWishListItem(wishList, itemId);
        wishList.removeWishListItem(wishListItem);
        wishListRepository.save(wishList);
    }

    public void removeWishList(Long userId) {
        // 위시리스트 항목 목록을 안전하게 복사
        List<WishListItem> itemsToRemove = new ArrayList<>(wishListItemRepositoryImpl.findByUserId(userId)
                .get()
                .getWishListItemList());

        // 각 항목에 대해 삭제 로직 실행
        for (WishListItem item : itemsToRemove) {
            wishListItemRepository.delete(item);
        }
    }


    @Transactional
    public void updateWishListItem(User user,
                                   WishListItemDto wishListItemDto) {

        validateQuantity(wishListItemDto.getQuantity());

        WishList wishList = wishListItemRepositoryImpl.findByUserId(user.getId())
                .orElseThrow(() -> new NoSuchElementException("회원을 찾을 수 없습니다."));

        WishListItem wishListItem = findWishListItem(wishList, wishListItemDto.getItemId());
        wishListItem.setWishListItemQuantity(wishListItemDto.getQuantity());
        wishListItemRepository.save(wishListItem);
    }

    private WishListItem findWishListItem(WishList wishList,
                                          Long itemId) {
        log.info("wishList = {}, itemId = {}", wishList, itemId);

        // 위시리스트 아이템 리스트 출력
        wishList.getWishListItemList()
                .forEach(item -> log.info("아이템 ID: {}", item.getItem()
                        .getId()));

        return wishList.getWishListItemList()
                .stream()
                .filter(item -> item.getItem()
                        .getId()
                        .equals(itemId))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("아이템을 찾을 수 없습니다. itemId: {}", itemId);
                    return new ItemNotFoundException("아이템을 찾을 수 없습니다.");
                });
    }


    private void validateQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new InvalidQuantityException("수량은 1 이상이어야 합니다.");
        }
    }


}
