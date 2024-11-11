package com.back.itemservice.service;

import com.back.itemservice.domain.Item;
import com.back.itemservice.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisItemUpdaterService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ItemRepository                itemRepository;

    // 1시간마다 실행
    @Scheduled(cron = "0 0 * * * *")
    public void updateInventoryFromRedisToDb() {
        Set<String> keys = redisTemplate.keys("itemId:*:quantity");

        if (keys != null && !keys.isEmpty()) {
            for (String cacheKey : keys) {
                try {
                    Integer redisQuantity = (Integer) redisTemplate.opsForValue()
                            .get(cacheKey);
                    if (redisQuantity != null) {
                        Long itemId = extractItemIdFromCacheKey(cacheKey);
                        updateItemQuantityInDb(itemId, redisQuantity);
                    }
                } catch (Exception e) {
                    log.error("Redis에서 재고 정보를 가져오는 데 실패했습니다, cacheKey: {}", cacheKey, e);
                }
            }
        } else {
            log.info("Redis에 저장된 재고 정보가 없습니다.");
        }
    }

    // cacheKey에서 itemId를 추출
    private Long extractItemIdFromCacheKey(String cacheKey) {
        // "itemId:{itemId}:quantity" 형식에서 itemId 추출
        String[] parts = cacheKey.split(":");
        if (parts.length > 1) {
            return Long.valueOf(parts[1]);
        }
        throw new IllegalArgumentException("잘못된 cacheKey 형식입니다.");
    }

    // Redis에 저장된 재고 정보를 DB에 업데이트
    private void updateItemQuantityInDb(Long itemId,
                                        Integer redisQuantity) {
        try {
            // DB에서 해당 itemId를 가진 Item 조회
            Item item = itemRepository.findById(itemId)
                    .orElseThrow(() -> new RuntimeException("Item을 찾을 수 없습니다. itemId: " + itemId));

            // Redis에서 가져온 재고 정보를 DB에 업데이트
            item.updateQuantity(redisQuantity);
            itemRepository.saveAndFlush(item);
            log.info("Item ID {}의 재고 정보를 DB에 업데이트했습니다. 재고: {}", itemId, redisQuantity);

        } catch (Exception e) {
            log.error("Item ID {}의 재고를 DB에 업데이트하는 데 실패했습니다.", itemId, e);
        }
    }
}