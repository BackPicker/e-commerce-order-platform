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
import com.hello.ecommerceorderplatform.wishlist.repository.WishListItemRepositoryImpl;
import com.hello.ecommerceorderplatform.wishlist.repository.WishListRepository;
import com.hello.ecommerceorderplatform.wishlist.repository.WishListRepositoryImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    @Transactional
    public WishListResponseDto getWishListItems(User user) {
        WishList wishList   = getOrCreateWishList(user);
        long     totalPrice = calculateTotalPrice(wishList);
        return new WishListResponseDto(user.getUsername(), wishList, totalPrice);
    }

    @Transactional
    public WishList getOrCreateWishList(User user) {
        return wishListRepositoryImpl.findByUserId(user.getId())
                .orElseGet(() -> wishListRepository.save(new WishList(user)));
    }

    private long calculateTotalPrice(WishList wishList) {
        return wishList.getWishListItemList()
                .stream()
                .mapToLong(item -> item.totalWishListPrice(item.getItem(), item.getWishListItemQuantity()))
                .sum();
    }

    // addWishListItem item 저장
    @Transactional
    public void addWishListItem(User user, WishListItemDto wishListItemDto) {
        log.info("wishListItemDto = {}", wishListItemDto);
        validateQuantity(wishListItemDto.getQuantity());

        // 아이템을 찾기
        Item item = itemRepository.findById(wishListItemDto.getItemId())
                .orElseThrow(() -> new ItemNotFoundException("아이템을 찾을 수 없습니다."));
        log.info("item = {}", item);

        // 위시리스트 가져오기
        WishList wishList = getOrCreateWishList(user);

        // WishListItem 생성
        WishListItem wishListItem = new WishListItem(wishListItemDto.getQuantity(), item);
        wishListItem.setWishList(wishList);


        log.info("wishListItem = {}", wishListItem);

        // 위시리스트에 아이템 추가
        wishListItemRepository.save(wishListItem);
        wishList.addWishListItem(wishListItem);
        log.info("위시리스트에 아이템 추가 = {}", wishListItem);

        // 위시리스트 저장
        log.info("위시리스트 저장");
        wishListRepository.save(wishList); // 이 호출로 인해 연관된 wishListItem이 저장됨

    }


    /**
     * WishList 삭제
     *
     * @param itemId
     */
    @Transactional
    public void removeWishListItem(User user, Long itemId) {
        WishList wishList = getOrCreateWishList(user);
        WishListItem wishListItem = findWishListItem(wishList, itemId);
        wishList.removeWishListItem(wishListItem);
        wishListRepository.save(wishList);
    }

    public void removeWishList(Long userId) {
        // 위시리스트 가져오기
        WishList wishList = wishListRepositoryImpl.findByUserId(userId)
                .get();

        // 위시리스트가 존재하면 아이템 삭제
        if (wishList != null) {
            // 모든 WishListItem 삭제
            for (WishListItem wishListItem : new ArrayList<>(wishList.getWishListItemList())) {
                wishList.removeWishListItem(wishListItem);
            }
            // 위시리스트 삭제
            wishListRepository.delete(wishList);
        }
    }

    private void validateQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new InvalidQuantityException("수량은 1 이상이어야 합니다.");
        }
    }

    private WishListItem findWishListItem(WishList wishList, Long itemId) {
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


    @Transactional
    public void updateWishListItem(User user, Long itemId, WishListItemDto wishListItemDto) {
        validateQuantity(wishListItemDto.getQuantity());

        WishList wishList = wishListItemRepositoryImpl.findByUserId(user.getId())
                .orElseThrow(() -> new NoSuchElementException("회원을 찾을 수 없습니다."));

        WishListItem wishListItem = findWishListItem(wishList, itemId);
        wishListItem.setWishListItemQuantity(wishListItemDto.getQuantity());
        wishListItemRepository.save(wishListItem);
    }


}
