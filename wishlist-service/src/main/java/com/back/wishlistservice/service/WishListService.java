package com.back.wishlistservice.service;

import com.back.common.dto.ResponseMessage;
import com.back.wishlistservice.domain.WishList;
import com.back.wishlistservice.dto.Item;
import com.back.wishlistservice.dto.WishListRequestDto;
import com.back.wishlistservice.dto.WishListResponseDto;
import com.back.wishlistservice.repository.WishListRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WishListService {

    private final WishListRepository         wishListRepository;
    private final FeignWishListToItemService feignWishListToItemService;


    @Transactional
    public ResponseEntity<ResponseMessage> getWishListItems(Long userId) {
        List<WishList>            wishLists    = wishListRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
        List<WishListResponseDto> responseDtos = new ArrayList<>();
        for (WishList wishList : wishLists) {
            responseDtos.add(new WishListResponseDto(wishList));
        }
        ResponseMessage build = ResponseMessage.builder()
                .data(responseDtos)
                .resultMessage("WishList 불러오기 성공")
                .build();
        return ResponseEntity.ok(build);
    }

    public void addWishListItem(Long userId,
                                WishListRequestDto wishListItemDto) {
        Long    paramItemId   = wishListItemDto.getItemId();
        Integer paramQuantity = wishListItemDto.getQuantity();
        Item item = feignWishListToItemService.getItem(paramItemId);

        if (wishListRepository.existsByUserIdAndItemId(userId, item.getItemId())) {
            log.info("wishList 가 존재해서 수량을 update 합니다");
            WishList wishList    = wishListRepository.findByUserIdAndItemId(userId, item.getItemId());
            Integer  sumQuantity = wishList.getWishListItemQuantity() + paramQuantity;
            wishList.updateQuantity(sumQuantity);
            wishListRepository.saveAndFlush(wishList);
        } else {
            log.info("wishList가 존재하지 않아 생성합니다");
            WishList wishList = new WishList(userId, item.getItemId(), paramQuantity);
            wishListRepository.save(wishList);
        }
    }

    @Transactional
    public void updateWishListItem(Long wishListItemId,
                                   Long userId,
                                   WishListRequestDto wishListItemDto) {
        Integer quantity = wishListItemDto.getQuantity();

        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("수량은 1 이상이어야 합니다");
        }

        WishList wishList = wishListRepository.findById(wishListItemId)
                .orElseThrow(() -> new IllegalArgumentException("위시리스트를 찾을 수 없습니다, WishListId = " + wishListItemId));
        wishList.updateQuantity(quantity);
        wishListRepository.saveAndFlush(wishList);
    }


    @Transactional
    public ResponseMessage removeWishListItem(Long wishListItemId) {
        WishList wishList = wishListRepository.findById(wishListItemId)
                .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다"));

        wishListRepository.delete(wishList);

        ResponseMessage build = ResponseMessage.builder()
                .resultMessage("WishList 에서 Item 을 성공적으로 삭제했습니다")
                .build();
        return build;

    }


/*
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
*/




/*
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

*/


}
