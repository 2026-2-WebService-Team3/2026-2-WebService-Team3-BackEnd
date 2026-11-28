package com.example.campus.controller;

import com.example.campus.dto.LostItemRequest;
import com.example.campus.dto.LostItemResponse;
import com.example.campus.entity.Category;
import com.example.campus.service.LostItemService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class LostItemController {

    private final LostItemService lostItemService;

    @Operation(summary = "신고 목록 조회", description = "분실·습득 신고를 조회합니다. 카테고리를 지정하면 해당 카테고리만 조회합니다.")
    @GetMapping
    public ResponseEntity<List<LostItemResponse>> findAll(
            @RequestParam(required = false) Category category) {
        return ResponseEntity.ok(lostItemService.findAll(category));
    }

    @Operation(summary = "신고 상세 조회", description = "ID에 해당하는 신고를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<LostItemResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(lostItemService.findById(id));
    }

    @Operation(summary = "신고 등록", description = "분실 또는 습득 신고를 등록합니다. 초기 상태는 미반환입니다.")
    @PostMapping
    public ResponseEntity<LostItemResponse> create(
            @Valid @RequestBody LostItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(lostItemService.create(request));
    }

    @Operation(summary = "신고 수정", description = "신고 내용을 수정합니다. 반환 상태는 변경되지 않습니다.")
    @PutMapping("/{id}")
    public ResponseEntity<LostItemResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody LostItemRequest request) {
        return ResponseEntity.ok(lostItemService.update(id, request));
    }

    @Operation(summary = "신고 삭제", description = "신고를 삭제합니다. 존재하지 않는 ID도 204를 반환합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        lostItemService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "반환 처리", description = "신고 상태를 반환 완료로 변경합니다. 이미 반환된 신고는 409를 반환합니다.")
    @PatchMapping("/{id}/return")
    public ResponseEntity<LostItemResponse> markAsReturned(@PathVariable Long id) {
        return ResponseEntity.ok(lostItemService.markAsReturned(id));
    }
}