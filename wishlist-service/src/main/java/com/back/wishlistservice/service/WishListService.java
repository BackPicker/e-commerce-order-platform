package com.back.wishlistservice.service;

import com.back.common.dto.ResponseMessage;
import com.back.wishlistservice.domain.WishList;
import com.back.wishlistservice.dto.Item;
import com.back.wishlistservice.dto.WishListRequestDto;
import com.back.wishlistservice.dto.WishListResponseDto;
import com.back.wishlistservice.exception.WishListNotFoundException;
import com.back.wishlistservice.repository.WishListRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WishListService {

    private static final String ITEM_ADDED_MESSAGE     = "위시리스트에 아이템이 추가되었습니다";
    private static final String ITEM_NOT_FOUND_MESSAGE = "아이템을 찾을 수 없습니다";

    private final WishListRepository wishListRepository;
    private final FeignWishListService feignWishListService;

    @Transactional(readOnly = true)
    public ResponseEntity<ResponseMessage> getWishListItems(Long userId) {
        log.info("사용자 {}의 위시리스트 아이템 조회 시작", userId);
        List<WishListResponseDto> responseDtos = wishListRepository.findAllByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(WishListResponseDto::new)
                .toList();
        log.info("사용자 {}의 위시리스트 아이템 {}개 조회 완료", userId, responseDtos.size());
        return ResponseEntity.ok(ResponseMessage.builder()
                                         .data(responseDtos)
                                         .resultMessage("WishList 불러오기 성공")
                                         .build());
    }


    // 사용자의 위시리스트에 아이템 추가 메서드
    public ResponseEntity<ResponseMessage> addWishListItem(Long userId, WishListRequestDto wishListItemDto) {
        log.info("사용자 {}의 위시리스트에 아이템 추가 시작: itemId={}, quantity={}", userId, wishListItemDto.getItemId(), wishListItemDto.getQuantity());
        try {
            // 아이템 정보 조회
            Item item = feignWishListService.getItem(wishListItemDto.getItemId());

            // 위시리스트 항목 처리
            WishList wishList = processWishListItem(userId, item, wishListItemDto.getQuantity());

            // 위시리스트 저장
            wishListRepository.save(wishList);

            log.info("사용자 {}의 위시리스트에 아이템 추가 완료: itemId={}", userId, item.getId());
            return ResponseEntity.ok(ResponseMessage.builder()
                                             .resultMessage(ITEM_ADDED_MESSAGE)
                                             .build());
        } catch (FeignException e) {
            log.error("아이템 조회 중 오류 발생: itemId={}, error={}", wishListItemDto.getItemId(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseMessage.builder()
                                  .resultMessage(ITEM_NOT_FOUND_MESSAGE)
                                  .build());
        } catch (Exception e) {
            log.error("예기치 않은 오류 발생: userId={}, itemId={}, error={}", userId, wishListItemDto.getItemId(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseMessage.builder()
                                  .resultMessage("내부 서버 오류가 발생했습니다")
                                  .build());
        }
    }

    // 위시리스트 항목 처리 메서드
    private WishList processWishListItem(Long userId, Item item, Integer quantity) {
        return wishListRepository.findByUserIdAndItemId(userId, item.getId())
                .map(wishList -> {
                    log.info("기존 위시리스트 항목 업데이트: userId={}, itemId={}", userId, item.getId());
                    int newQuantity = wishList.getWishListItemQuantity() + quantity;
                    wishList.updateQuantity(newQuantity);
                    return wishList;
                })
                .orElseGet(() -> {
                    log.info("새로운 위시리스트 항목 생성: userId={}, itemId={}", userId, item.getId());
                    return new WishList(userId, item.getId(), quantity);
                });
    }

    @Transactional
    public ResponseEntity<ResponseMessage> updateWishListItem(Long wishListItemId, Long userId, WishListRequestDto wishListItemDto) {
        log.info("사용자 {}의 위시리스트 아이템 {} 업데이트 시작", userId, wishListItemId);
        try {
            WishList wishList = wishListRepository.findByIdAndUserId(wishListItemId, userId)
                    .orElseThrow(() -> new WishListNotFoundException("위시리스트를 찾을 수 없습니다, WishListId = " + wishListItemId));
            wishList.updateQuantity(wishListItemDto.getQuantity());
            wishListRepository.save(wishList);
            log.info("사용자 {}의 위시리스트 아이템 {} 업데이트 완료", userId, wishListItemId);
            return ResponseEntity.ok(ResponseMessage.builder()
                                             .resultMessage("위시리스트 아이템이 업데이트되었습니다")
                                             .build());
        } catch (WishListNotFoundException e) {
            log.error("위시리스트 아이템 업데이트 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseMessage.builder()
                                  .resultMessage(e.getMessage())
                                  .build());
        }
    }


    @Transactional
    public ResponseEntity<ResponseMessage> removeWishListItem(Long wishListItemId) {
        log.info("위시리스트 아이템 {} 제거 시작", wishListItemId);
        try {
            WishList wishList = wishListRepository.findById(wishListItemId)
                    .orElseThrow(() -> {
                        log.warn("위시리스트를 찾을 수 없음, ID : {}", wishListItemId);
                        return new WishListNotFoundException("상품이 존재하지 않습니다");
                    });
            wishListRepository.delete(wishList);
            boolean stillExists = wishListRepository.existsById(wishListItemId);
            if (stillExists) {
                log.warn("위시리스트 아이템이 여전히 존재함, ID : {}", wishListItemId);
                throw new RuntimeException("위시리스트 아이템 삭제 실패");
            }
            log.info("위시리스트 아이템 {} 제거 완료", wishListItemId);
            return ResponseEntity.ok(ResponseMessage.builder()
                                             .resultMessage("WishList에서 Item을 성공적으로 삭제했습니다")
                                             .build());
        } catch (WishListNotFoundException e) {
            log.error("위시리스트 아이템 제거 실패 - 아이템을 찾을 수 없음, ID : {}", wishListItemId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseMessage.builder()
                                  .resultMessage(e.getMessage())
                                  .build());
        } catch (Exception e) {
            log.error("위시리스트 아이템 제거 중 예상치 못한 오류 발생 - ID: {}", wishListItemId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseMessage.builder()
                                  .resultMessage("위시리스트 아이템 삭제 중 오류가 발생했습니다")
                                  .build());
        }
    }


    @Transactional(readOnly = true)
    public List<WishList> eurekaWishListByItemId(Long itemId) {
        log.info("아이템 ID {}로 위시리스트 아이템 조회 시작", itemId);
        try {
            Item item = feignWishListService.getItem(itemId);
            log.debug("Feign 클라이언트를 통해 아이템 정보 조회 완료 - 아이템 ID: {}, 아이템 이름: {}", item.getId(), item.getItemName());

            List<WishList> wishLists = wishListRepository.findAllByItemId(item.getId());
            log.info("아이템 ID {}에 대한 위시리스트 아이템 {}개 조회 완료", itemId, wishLists.size());

            if (wishLists.isEmpty()) {
                log.warn("아이템 ID {}에 대한 위시리스트가 없습니다", itemId);
            } else {
                log.debug("조회된 위시리스트: {}", wishLists);
            }
            return wishLists;
        } catch (FeignException e) {
            log.error("Feign 클라이언트를 통한 아이템 정보 조회 중 오류 발생 - 아이템 ID: {}", itemId, e);
            throw new RuntimeException("아이템 정보 조회 중 오류가 발생했습니다", e);
        } catch (Exception e) {
            log.error("위시리스트 조회 중 예상치 못한 오류 발생 - 아이템 ID: {}", itemId, e);
            throw new RuntimeException("위시리스트 조회 중 오류가 발생했습니다", e);
        }
    }


}