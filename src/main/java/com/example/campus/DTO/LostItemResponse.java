package com.example.campus.DTO;

import com.example.campus.Entity.ItemType;
import com.example.campus.Entity.ItemStatus;

import java.time.LocalDateTime;

public class LostItemResponse {

    private final Long id;
    private final String title;
    private final String description;
    private final ItemType type;
    private final ItemStatus status;
    private final String category;
    private final String location;
    private final String contact;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public LostItemResponse(
            Long id,
            String title,
            String description,
            ItemType type,
            ItemStatus status,
            String category,
            String location,
            String contact,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    )
    {
        this.id = id;
        this.title = title;
        this.description = description;
        this.type = type;
        this.status = status;
        this.category = category;
        this.location = location;
        this.contact = contact;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }


    public Long getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }

    public ItemType getType() {
        return type;
    }
    public ItemStatus getStatus() {
        return status;
    }

    public String getCategory() {
        return category;
    }
    public String getLocation() {
        return location;
    }

    public String getContact() {
        return contact;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}