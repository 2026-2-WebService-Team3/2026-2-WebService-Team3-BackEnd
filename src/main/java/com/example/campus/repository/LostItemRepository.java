package com.example.campus.repository;

import com.example.campus.entity.Category;
import com.example.campus.entity.LostItem;
import com.example.campus.entity.ItemType;
import com.example.campus.entity.ItemStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LostItemRepository extends JpaRepository<LostItem, Long> {

    List<LostItem> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String title, String description);

    List<LostItem> findByType(ItemType type);

    List<LostItem> findByCategory(Category category);

    List<LostItem> findByLocationContainingIgnoreCase(String location);

    List<LostItem> findByStatus(ItemStatus status);
    List<LostItem> findByTypeAndCategory(
            ItemType type, Category category);

}