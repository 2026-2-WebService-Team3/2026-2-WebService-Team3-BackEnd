package com.example.campus.service;

import com.example.campus.dto.AttachmentFile;
import com.example.campus.dto.AttachmentResponse;
import com.example.campus.entity.Attachment;
import com.example.campus.entity.LostItem;
import com.example.campus.repository.AttachmentRepository;
import com.example.campus.repository.LostItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final LostItemRepository lostItemRepository;
    private final AttachmentRepository attachmentRepository;
    private final FileStorageService fileStorageService;

    @Transactional
    public List<AttachmentResponse> upload(Long itemId, List<MultipartFile> files) {
        LostItem lostItem = lostItemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "신고를 찾을 수 없습니다. ID: " + itemId));

        List<Attachment> created = new ArrayList<>();
        try {
            for (MultipartFile file : files) {
                if (file.isEmpty()) {
                    continue;
                }
                Attachment attachment = fileStorageService.store(file);
                created.add(attachment);
                lostItem.addAttachment(attachment);
                attachmentRepository.save(attachment);
            }
            if (created.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "업로드할 파일이 없습니다.");
            }
            attachmentRepository.flush();
        } catch (RuntimeException e) {
            // 하나라도 실패하면 이번 요청에서 저장한 파일을 삭제
            created.forEach(fileStorageService::deleteIfExists);
            throw e;
        }

        return created.stream()
                .map(AttachmentResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public AttachmentFile load(Long id) {
        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "첨부파일이 없습니다. ID: " + id));

        return new AttachmentFile(
                attachment.getOriginalFilename(),
                attachment.getContentType(),
                fileStorageService.load(attachment));
    }
}
