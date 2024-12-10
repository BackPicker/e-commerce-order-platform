package com.back.wishlistservice.service;

import com.back.common.dto.ResponseMessage;
import com.back.wishlistservice.domain.WishList;
import com.back.wishlistservice.dto.Item;
import com.back.wishlistservice.dto.WishListRequestDto;
import com.back.wishlistservice.dto.WishListResponseDto;
import com.back.wishlistservice.exception.WishListNotFoundException;
import com.back.wishlistservice.repository.WishListRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WishListService {

    private final WishListRepository wishListRepository;
    private final FeignWishListService feignWishListService;

    @Transactional(readOnly = true)
    public ResponseEntity<ResponseMessage> getWishListItems(Long userId) {
        log.info("사용자 {}의 위시리스트 아이템 조회 시작", userId);
        List<WishListResponseDto> responseDtos = wishListRepository.findAllByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(WishListResponseDto::new)
                .collect(Collectors.toList());
        log.info("사용자 {}의 위시리스트 아이템 {}개 조회 완료", userId, responseDtos.size());
        return ResponseEntity.ok(ResponseMessage.builder()
                                         .data(responseDtos)
                                         .resultMessage("WishList 불러오기 성공")
                                         .build());
    }

    @Transactional
    public ResponseEntity<ResponseMessage> addWishListItem(Long userId, WishListRequestDto wishListItemDto) {
        log.info("사용자 {}의 위시리스트에 아이템 추가 시작: itemId={}, quantity={}", userId, wishListItemDto.getItemId(), wishListItemDto.getQuantity());
        Item     item     = feignWishListService.getItem(wishListItemDto.getItemId());
        WishList wishList = processWishListItem(userId, item, wishListItemDto.getQuantity());
        wishListRepository.save(wishList);
        log.info("사용자 {}의 위시리스트에 아이템 추가 완료: itemId={}", userId, item.getItemId());
        return ResponseEntity.ok(ResponseMessage.builder()
                                         .resultMessage("위시리스트에 아이템이 추가되었습니다")
                                         .build());
    }

    @Transactional
    public ResponseEntity<ResponseMessage> updateWishListItem(Long wishListItemId, Long userId, WishListRequestDto wishListItemDto) {
        log.info("사용자 {}의 위시리스트 아이템 {} 업데이트 시작", userId, wishListItemId);
        Integer quantity = wishListItemDto.getQuantity();
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("수량은 1 이상이어야 합니다");
        }
        WishList wishList = wishListRepository.findById(wishListItemId)
                .orElseThrow(() -> new WishListNotFoundException("위시리스트를 찾을 수 없습니다, WishListId = " + wishListItemId));
        wishList.updateQuantity(quantity);
        wishListRepository.saveAndFlush(wishList);
        log.info("사용자 {}의 위시리스트 아이템 {} 업데이트 완료", userId, wishListItemId);
        return ResponseEntity.ok(ResponseMessage.builder()
                                         .resultMessage("위시리스트 아이템이 업데이트되었습니다")
                                         .build());
    }

    @Transactional
    public ResponseEntity<ResponseMessage> removeWishListItem(Long wishListItemId) {
        log.info("위시리스트 아이템 {} 제거 시작", wishListItemId);
        WishList wishList = wishListRepository.findById(wishListItemId)
                .orElseThrow(() -> new WishListNotFoundException("상품이 존재하지 않습니다"));
        wishListRepository.delete(wishList);
        log.info("위시리스트 아이템 {} 제거 완료", wishListItemId);
        return ResponseEntity.ok(ResponseMessage.builder()
                                         .resultMessage("WishList에서 Item을 성공적으로 삭제했습니다")
                                         .build());
    }

    @Transactional(readOnly = true)
    public List<WishList> eurekaWishListByItemId(Long itemId) {
        log.info("아이템 ID {}로 위시리스트 아이템 조회 시작", itemId);
        Item item = feignWishListService.getItem(itemId);
        List<WishList> wishLists = wishListRepository.findAllByItemId(item.getItemId());
        log.info("아이템 ID {}에 대한 위시리스트 아이템 {}개 조회 완료", itemId, wishLists.size());
        return wishLists;
    }

    private WishList processWishListItem(Long userId, Item item, Integer quantity) {
        if (wishListRepository.existsByUserIdAndItemId(userId, item.getItemId())) {
            log.info("사용자 {}의 아이템 {} 위시리스트 항목 업데이트", userId, item.getItemId());
            WishList wishList    = wishListRepository.findByUserIdAndItemId(userId, item.getItemId());
            Integer  sumQuantity = wishList.getWishListItemQuantity() + quantity;
            wishList.updateQuantity(sumQuantity);
            return wishList;
        } else {
            log.info("사용자 {}의 아이템 {} 위시리스트 항목 새로 생성", userId, item.getItemId());
            return new WishList(userId, item.getItemId(), quantity);
        }
    }
}