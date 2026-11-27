package com.example.campus.service;

import com.example.campus.dto.LostItemRequest;
import com.example.campus.dto.LostItemResponse;
import com.example.campus.entity.Category;
import com.example.campus.entity.ItemStatus;
import com.example.campus.entity.ItemType;
import com.example.campus.entity.LostItem;
import com.example.campus.repository.LostItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Locale;

@Service
public class LostItemService {

    private final LostItemRepository lostItemRepository;

    public LostItemService(LostItemRepository lostItemRepository) {
        this.lostItemRepository = lostItemRepository;
    }

    public List<LostItemResponse> findAll(Category category) {

        List<LostItem> items = category == null
                ? lostItemRepository.findAll()
                : lostItemRepository.findByCategory(category);

        return items.stream()
                .map(LostItemResponse::from)
                .toList();
    }

    public LostItemResponse findById(Long id) {
        return LostItemResponse.from(getLostItem(id));
    }

    @Transactional
    public LostItemResponse create(LostItemRequest request) {

        LostItem newLostItem = lostItemRepository.save(new LostItem(normalize(request)));

        return LostItemResponse.from(newLostItem);
    }

    @Transactional
    public LostItemResponse update(
            Long id, LostItemRequest request) {

        LostItem lostItem = getLostItem(id);

        lostItem.update(normalize(request));
        lostItemRepository.flush();

        return LostItemResponse.from(lostItem);
    }

    @Transactional
    public void delete(Long id) {

        if (!lostItemRepository.existsById(id)) {
            return;
        }

        lostItemRepository.deleteById(id);
    }

    @Transactional
    public LostItemResponse markAsReturned(Long id) {

        LostItem lostItem = getLostItem(id);

        if (lostItem.getStatus() == ItemStatus.RETURNED) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "이미 반환 처리된 물품입니다."
            );
        }

        lostItem.markAsReturned();
        lostItemRepository.flush();

        return LostItemResponse.from(lostItem);
    }

    public List<LostItemResponse> search(
            String keyword,
            ItemType type,
            Category category,
            String location,
            ItemStatus status) {

        List<LostItem> items;

        if (type != null && category != null) {

            items = lostItemRepository
                    .findByTypeAndCategory(
                            type, category);
        } else if (hasText(keyword)) {

            items = lostItemRepository
                    .findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                            keyword.trim(), keyword.trim());
        } else if (type != null) {

            items = lostItemRepository.findByType(type);
        } else if (category != null) {

            items = lostItemRepository.findByCategory(category);
        } else if (hasText(location)) {

            items = lostItemRepository
                    .findByLocationContainingIgnoreCase(location.trim());
        } else if (status != null) {

            items = lostItemRepository.findByStatus(status);
        } else {

            items = lostItemRepository.findAll();
        }


        return items.stream()

                .filter(item ->
                        !hasText(keyword)
                                || containsIgnoreCase(item.getTitle(), keyword)
                                || containsIgnoreCase(item.getDescription(), keyword))

                .filter(item ->
                        type == null
                                || item.getType() == type)

                .filter(item ->
                        category == null
                                || item.getCategory() == category)

                .filter(item ->
                        !hasText(location)
                                || containsIgnoreCase(item.getLocation(), location))

                .filter(item ->
                        status == null
                                || item.getStatus() == status)

                .map(LostItemResponse::from)
                .toList();
    }

    // 10. 신고 존재 여부 확인
    private LostItem getLostItem(Long id) {

        return lostItemRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "신고를 찾을 수 없습니다. ID: " + id
                        )
                );
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private boolean containsIgnoreCase(
            String text, String keyword) {
        return text.toLowerCase(Locale.ROOT)
                .contains(keyword.trim().toLowerCase(Locale.ROOT));
    }

    private LostItemRequest normalize(LostItemRequest request) {
        return new LostItemRequest(
                request.title().strip(),
                request.description().strip(),
                request.type(),
                request.category(),
                request.location().strip(),
                request.contact().strip()
        );
    }

}