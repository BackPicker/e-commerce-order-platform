package com.back.itemservice.repository;

import com.back.itemservice.dto.ItemResponseDto;
import com.back.itemservice.dto.ItemSearchCondition;
import com.back.itemservice.dto.QItemResponseDto;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.back.itemservice.domain.QItem.item;


@Slf4j
@Repository
public class ItemRepositoryImpl {

    private final JPAQueryFactory factory;

    public ItemRepositoryImpl(EntityManager entityManager) {
        this.factory = new JPAQueryFactory(entityManager);
    }

    /**
     * item 수량 확인
     */
    public boolean existByItemName(String itemName) {
        Long count = factory.select(item.count())
                .from(item)
                .where(item.itemName.eq(itemName))
                .fetchOne();
        return count != null && count > 0;
    }

    /**
     * ItemList 확인
     */
    public Page<ItemResponseDto> itemList(ItemSearchCondition searchCondition,
                                          Pageable pageable) {
        List<ItemResponseDto> content = factory.select(new QItemResponseDto(item.itemName, item.category, item.price, item.quantity))
                .from(item)
                .where(itemNameCheck(searchCondition.getItemName()), itemQuantityLoeCheck(searchCondition.getItemQuantityLoe()),
                        itemPriceLoeCheck(searchCondition.getItemPriceLoe()), itemPriceGoeCheck(searchCondition.getItemPriceGoe()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long totalCount = Optional.ofNullable(factory.select(item.count())
                        .from(item)
                        .where(itemNameCheck(searchCondition.getItemName()), itemQuantityLoeCheck(searchCondition.getItemQuantityLoe()),
                                itemPriceLoeCheck(searchCondition.getItemPriceLoe()), itemPriceGoeCheck(searchCondition.getItemPriceGoe()))
                        .fetchOne())
                .orElse(0L);
        return new CustomPageImpl<>(content, pageable, totalCount);
    }

    private BooleanExpression itemNameCheck(String itemName) {
        return itemName != null && !itemName.isEmpty() ? item.itemName.like("%" + itemName + "%") : null;
    }

    private BooleanExpression itemQuantityLoeCheck(Integer itemQuantityLoe) {
        return itemQuantityLoe != null ? item.quantity.loe(itemQuantityLoe) : null;
    }

    private BooleanExpression itemPriceLoeCheck(Integer itemPriceLoe) {
        return itemPriceLoe != null ? item.price.loe(itemPriceLoe) : null;
    }

    private Predicate itemPriceGoeCheck(Integer itemPriceGoe) {
        return itemPriceGoe != null ? item.price.goe(itemPriceGoe) : null;
    }

    public void addItemQuantity(Long itemId,
                                int orderCount) {
        factory.update(item)
                .set(item.quantity, item.quantity.add(orderCount))
                .where(item.id.eq(itemId))
                .execute();
    }
}
