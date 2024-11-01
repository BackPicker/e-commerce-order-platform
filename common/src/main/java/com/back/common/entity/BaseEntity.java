package com.back.common.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @CreatedDate
    @Column(updatable = false)
    protected LocalDateTime createdAt;

    @LastModifiedDate
    protected LocalDateTime modifiedAt;

    @JsonIgnore
    public String getCreatedAtAsString() {
        return createdAt != null ? createdAt.format(DATE_TIME_FORMATTER) : "N/A";
    }

    @JsonIgnore
    public String getModifiedAtAsString() {
        return modifiedAt != null ? modifiedAt.format(DATE_TIME_FORMATTER) : "N/A";
    }
}