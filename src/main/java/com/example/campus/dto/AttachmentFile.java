package com.example.campus.dto;

import org.springframework.core.io.Resource;

// 파일 응답(미리보기·다운로드)에 필요한 정보
public record AttachmentFile(
        String originalFilename,
        String contentType,
        Resource resource
) {
}
