package com.example.campus.service;

import com.example.campus.entity.Attachment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
public class FileStorageService {

    // 허용 확장자와 응답에 사용할 MIME 타입
    private static final Map<String, String> ALLOWED = Map.of(
            "jpg", "image/jpeg",
            "jpeg", "image/jpeg",
            "png", "image/png",
            "gif", "image/gif"
    );

    private final Path uploadDir;

    public FileStorageService(@Value("${app.upload-dir}") String path) throws IOException {
        uploadDir = Path.of(path).toAbsolutePath().normalize();
        Files.createDirectories(uploadDir);
    }

    // 파일을 검증해 디스크에 저장하고, DB에 저장할 메타데이터를 반환
    public Attachment store(MultipartFile file) {
        String original = file.getOriginalFilename();
        if (original == null || original.isBlank()
                || original.contains("/") || original.contains("\\") || original.contains("..")) {
            throw badRequest("잘못된 파일 이름입니다.");
        }
        if (original.length() > 240) {
            throw badRequest("파일 이름이 너무 깁니다.");
        }

        String extension = original.substring(original.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
        if (!original.contains(".") || !ALLOWED.containsKey(extension)) {
            throw badRequest("허용 형식: jpg, jpeg, png, gif");
        }

        // 원본명 대신 UUID를 사용해 같은 이름의 파일이 덮어써지지 않게 함
        String stored = UUID.randomUUID() + "." + extension;
        Path target = resolve(stored);

        try (InputStream input = file.getInputStream()) {
            Files.copy(input, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            // 파일은 트랜잭션 롤백 대상이 아니므로 직접 정리
            try {
                Files.deleteIfExists(target);
            } catch (IOException ignored) {
            }
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 저장 실패", e);
        }

        // 요청의 Content-Type은 신뢰하지 않고 검증된 확장자로 결정
        return new Attachment(original, stored, ALLOWED.get(extension), file.getSize());
    }

    public Resource load(Attachment attachment) {
        Path target = resolve(attachment.getStoredFilename());
        if (!Files.isRegularFile(target)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "파일이 없습니다.");
        }
        return new PathResource(target);
    }

    public void deleteIfExists(Attachment attachment) {
        try {
            Files.deleteIfExists(resolve(attachment.getStoredFilename()));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 삭제 실패", e);
        }
    }

    private Path resolve(String storedFilename) {
        Path target = uploadDir.resolve(storedFilename).normalize();
        if (!target.startsWith(uploadDir)) {
            throw badRequest("잘못된 파일 경로입니다.");
        }
        return target;
    }

    private ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
}
