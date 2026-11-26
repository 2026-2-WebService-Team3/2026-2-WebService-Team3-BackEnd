package com.example.campus.service;

import com.example.campus.DTO.LostItemRequest;
import com.example.campus.DTO.LostItemResponse;

import com.example.campus.Entity.LostItem;
import com.example.campus.Entity.ItemType;
import com.example.campus.Entity.ItemStatus;

import com.example.campus.Repository.LostItemRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Locale;

@Service
@Transactional(readOnly = true)
public class LostItemService {

    private final LostItemRepository lostItemRepository;


    public LostItemService(LostItemRepository lostItemRepository) {
        this.lostItemRepository = lostItemRepository;
    }


    public List<LostItemResponse> findAll() {

        return lostItemRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }


    public LostItemResponse findById(Long id) {

        LostItem lostItem = getLostItem(id);

        return toResponse(lostItem);
    }


    @Transactional
    public LostItemResponse create(LostItemRequest request) {

        LostItem lostItem = new LostItem();

        lostItem.setTitle(request.getTitle());
        lostItem.setDescription(request.getDescription());
        lostItem.setType(request.getType());
        lostItem.setCategory(request.getCategory());
        lostItem.setLocation(request.getLocation());
        lostItem.setContact(request.getContact());

        lostItem.setStatus(ItemStatus.UNRETURNED);

        LostItem savedItem = lostItemRepository.save(lostItem);

        return toResponse(savedItem);
    }

    @Transactional
    public LostItemResponse update(
            Long id, LostItemRequest request) {

        LostItem lostItem = getLostItem(id);

        lostItem.setTitle(request.getTitle());
        lostItem.setDescription(request.getDescription());
        lostItem.setType(request.getType());
        lostItem.setCategory(request.getCategory());
        lostItem.setLocation(request.getLocation());
        lostItem.setContact(request.getContact());

        LostItem updatedItem =
                lostItemRepository.saveAndFlush(lostItem);

        return toResponse(updatedItem);
    }


    @Transactional
    public void delete(Long id) {

        LostItem lostItem = getLostItem(id);

        lostItemRepository.delete(lostItem);
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

        lostItem.setStatus(ItemStatus.RETURNED);

        LostItem returnedItem =
                lostItemRepository.saveAndFlush(lostItem);

        return toResponse(returnedItem);
    }


    public List<LostItemResponse> search(
            String keyword,
            ItemType type,
            String category,
            String location,
            ItemStatus status) {

        List<LostItem> items;

        if (type != null && hasText(category)) {

            items = lostItemRepository
                    .findByTypeAndCategoryIgnoreCase(
                            type, category.trim());
        }

        else if (hasText(keyword)) {

            items = lostItemRepository
                    .findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                            keyword.trim(), keyword.trim());
        }

        else if (type != null) {

            items = lostItemRepository.findByType(type);
        }

        else if (hasText(category)) {

            items = lostItemRepository
                    .findByCategoryIgnoreCase(category.trim());
        }

        else if (hasText(location)) {

            items = lostItemRepository
                    .findByLocationContainingIgnoreCase(location.trim());
        }


        else if (status != null) {

            items = lostItemRepository.findByStatus(status);
        }

        else {

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
                        !hasText(category)
                                || item.getCategory().equalsIgnoreCase(category.trim()))

                .filter(item ->
                        !hasText(location)
                                || containsIgnoreCase(item.getLocation(), location))

                .filter(item ->
                        status == null
                                || item.getStatus() == status)

                .map(this::toResponse)
                .toList();
    }



    private boolean hasText(String value) {

        return value != null && !value.isBlank();
    }


    private boolean containsIgnoreCase(
            String text, String keyword) {

        return text.toLowerCase(Locale.ROOT)
                .contains(keyword.trim().toLowerCase(Locale.ROOT));
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



    private LostItemResponse toResponse(LostItem lostItem) {

        return new LostItemResponse(
                lostItem.getId(),
                lostItem.getTitle(),
                lostItem.getDescription(),
                lostItem.getType(),
                lostItem.getStatus(),
                lostItem.getCategory(),
                lostItem.getLocation(),
                lostItem.getContact(),
                lostItem.getCreatedAt(),
                lostItem.getUpdatedAt()
        );
    }
}