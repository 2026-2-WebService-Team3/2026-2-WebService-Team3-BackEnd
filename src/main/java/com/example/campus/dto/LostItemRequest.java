package com.example.campus.dto;

import com.example.campus.entity.Category;
import com.example.campus.entity.ItemType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LostItemRequest(

        @NotBlank(message = "제목을 입력해주세요.")
        @Size(max = 100, message = "제목은 100자 이하로 입력해주세요.")
        String title,

        @NotBlank(message = "상세 설명을 입력해주세요.")
        @Size(max = 2000, message = "상세 설명은 2000자 이하로 입력해주세요.")
        String description,

        @NotNull(message = "신고 유형을 선택해주세요.")
        ItemType type,

        @NotNull(message = "카테고리를 선택해주세요.")
        Category category,

        @NotBlank(message = "장소를 입력해주세요.")
        @Size(max = 100, message = "장소는 100자 이하로 입력해주세요.")
        String location,

        @NotBlank(message = "연락 방법을 입력해주세요.")
        @Size(max = 100, message = "연락 방법은 100자 이하로 입력해주세요.")
        String contact
) {
}