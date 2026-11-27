package com.example.campus.controller;

import com.example.campus.dto.AttachmentFile;
import com.example.campus.dto.AttachmentResponse;
import com.example.campus.service.AttachmentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AttachmentController {

    // 미리보기를 허용하는 이미지 MIME 타입. 저장 시 서버가 확장자 기준으로 정한 값이다.
    private static final Set<String> PREVIEWABLE = Set.of("image/jpeg", "image/png", "image/gif");

    private final AttachmentService attachmentService;

    /*
     [사진 업로드]
     - multipart/form-data 요청의 files Part를 받으며, 여러 장을 보낼 수 있어 List<MultipartFile>을 사용한다.
     - Controller는 요청 수신과 응답만 담당하고, 검증·저장은 Service가 처리한다.
     */
    @Operation(summary = "사진 업로드", description = "신고에 사진을 추가합니다. jpg, jpeg, png, gif만 가능하며 파일당 10MB 이하입니다.")
    @PostMapping(value = "/items/{itemId}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<AttachmentResponse>> upload(
            @PathVariable Long itemId,
            @RequestParam(name = "files", required = false) List<MultipartFile> files) {
        // 파일을 선택하지 않으면 null이므로 빈 리스트로 대체한다. (Service에서 400 처리)
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(attachmentService.upload(itemId, files == null ? List.of() : files));
    }

    /*
     [이미지 미리보기]
     - Content-Disposition을 inline으로 지정해 브라우저가 저장하지 않고 화면에 표시하게 한다.
     - 오류: 없는 id → 404, 이미지가 아닌 파일 → 415, 디스크에 파일 없음 → 404, 숫자가 아닌 id → 400
     */
    @Operation(summary = "사진 미리보기", description = "브라우저에서 바로 표시할 수 있게 사진을 반환합니다.")
    @GetMapping("/attachments/{id}/view")
    public ResponseEntity<Resource> view(@PathVariable Long id) {
        AttachmentFile file = attachmentService.load(id);

        // 임의의 파일을 이미지 응답으로 제공하지 않도록 MIME 타입을 확인한다.
        if (!PREVIEWABLE.contains(file.contentType())) {
            throw new ResponseStatusException(
                    HttpStatus.UNSUPPORTED_MEDIA_TYPE, "이미지 파일만 미리보기할 수 있습니다.");
        }

        // nosniff: 브라우저가 내용을 추측해 다른 타입으로 해석하지 못하게 한다.
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        disposition(ContentDisposition.inline(), file))
                .header("X-Content-Type-Options", "nosniff")
                .body(file.resource());
    }

    /*
     [첨부파일 다운로드]
     - Controller(요청 수신) → Service(메타데이터 조회) → FileStorage(실제 파일 읽기) → HTTP 응답
     - Content-Disposition을 attachment로 지정하고, UUID 저장명이 아닌 원본 파일명을 UTF-8로 지정한다.
     */
    @Operation(summary = "사진 다운로드", description = "원본 파일명으로 사진을 내려받습니다.")
    @GetMapping("/attachments/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable Long id) {
        AttachmentFile file = attachmentService.load(id);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        disposition(ContentDisposition.attachment(), file))
                .body(file.resource());
    }

    private String disposition(ContentDisposition.Builder builder, AttachmentFile file) {
        return builder.filename(file.originalFilename(), StandardCharsets.UTF_8)
                .build()
                .toString();
    }
}
