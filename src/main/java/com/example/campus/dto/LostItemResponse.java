package com.example.campus.dto;

import com.example.campus.entity.Category;
import com.example.campus.entity.ItemStatus;
import com.example.campus.entity.ItemType;
import com.example.campus.entity.LostItem;

import java.time.LocalDateTime;

public record LostItemResponse(
        Long id,
        String title,
        String description,
        ItemType type,
        ItemStatus status,
        Category category,
        String location,
        String contact,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static LostItemResponse from(LostItem lostItem) {
        return new LostItemResponse(
                lostItem.getId(),
                lostItem.getTitle(),
                lostItem.getDescription(),
                lostItem.getType(),
                lostItem.getStatus(),
                lostItem.getCategory(),
                lostItem.getLocation(),
                lostItem.getContact(),
                lostItem.getCreatedAt(),
                lostItem.getUpdatedAt()
        );
    }
}