package com.example.campus.DTO;

import com.example.campus.Entity.ItemType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class LostItemRequest {

    @NotBlank(message = "제목을 입력해주세요.")
    @Size(max = 100)
    private String title;

    @NotBlank(message = "상세 설명을 입력해주세요.")
    @Size(max = 2000)
    private String description;

    @NotNull(message = "신고 유형을 선택해주세요.")
    private ItemType type;

    @NotBlank(message = "카테고리를 선택해주세요.")
    @Size(max = 50)
    private String category;

    @NotBlank(message = "장소를 입력해주세요.")
    @Size(max = 100)
    private String location;

    @NotBlank(message = "연락 방법을 입력해주세요.")
    @Size(max = 100)
    private String contact;


    public LostItemRequest() {
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
    public String getCategory() {
        return category;
    }
    public String getLocation() {
        return location;
    }
    public String getContact() {
        return contact;
    }



    public void setTitle(String title) {
        this.title = title;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public void setType(ItemType type) {
        this.type = type;
    }
    public void setCategory(String category) {
        this.category = category;
    }

    public void setLocation(String location) {
        this.location = location;
    }
    public void setContact(String contact) {
        this.contact = contact;
    }
}