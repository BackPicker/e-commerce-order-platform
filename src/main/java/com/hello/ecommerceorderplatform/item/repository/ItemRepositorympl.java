package com.hello.ecommerceorderplatform.item.repository;

import com.hello.ecommerceorderplatform.item.domain.Item;
import com.hello.ecommerceorderplatform.item.dto.*;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.hello.ecommerceorderplatform.item.domain.QItem.item;

@Repository
public class ItemRepositorympl {

    private final JPAQueryFactory factory;

    public ItemRepositorympl(EntityManager entityManager) {
        this.factory = new JPAQueryFactory(entityManager);
    }

    public Page<ItemResponseDto> itemList(ItemSearchCondition searchCondition, Pageable pageable) {

        List<ItemResponseDto> content = factory.select(new QItemResponseDto(item.itemName, item.category, item.price, item.quantity))
                .from(item)
                .where(itemNameCheck(searchCondition.getItemName()), itemQuantityLoeCheck( searchCondition.getItemQuantityLoe()),
                        itemPriceLoeCheck(searchCondition.getItemPriceLoe()), itemPriceGoeCheck(searchCondition.getItemPriceGoe()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Item> count = factory.selectFrom(item)
                .where(itemNameCheck(searchCondition.getItemName()), itemQuantityLoeCheck(searchCondition.getItemQuantityLoe()),
                        itemPriceLoeCheck(searchCondition.getItemPriceLoe()), itemPriceGoeCheck(searchCondition.getItemPriceGoe()));
        return PageableExecutionUtils.getPage(content, pageable, count.fetch()::size);
    }

    public ItemDetailResponseDto getItemDetail(Long itemId) {
        return factory.select(new QItemDetailResponseDto(item.itemName, item.category, item.price, item.quantity, item.description)) // description 추가
                .from(item)
                .where(item.id.eq(itemId))
                .fetchOne();
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
}
