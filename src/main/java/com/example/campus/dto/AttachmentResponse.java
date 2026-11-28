package com.example.campus.dto;

import com.example.campus.entity.Attachment;

public record AttachmentResponse(
        Long id,
        String originalFilename,
        String contentType,
        long fileSize
) {
    public static AttachmentResponse from(Attachment attachment) {
        return new AttachmentResponse(
                attachment.getId(),
                attachment.getOriginalFilename(),
                attachment.getContentType(),
                attachment.getFileSize()
        );
    }
}
