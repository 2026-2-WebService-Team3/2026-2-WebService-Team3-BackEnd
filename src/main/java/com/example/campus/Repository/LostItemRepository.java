package com.example.campus.Repository;

import com.example.campus.Entity.LostItem;
import com.example.campus.Entity.ItemType;
import com.example.campus.Entity.ItemStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LostItemRepository extends JpaRepository<LostItem, Long> {

    List<LostItem> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String title, String description);

    List<LostItem> findByType(ItemType type);

    List<LostItem> findByCategoryIgnoreCase(String category);

    List<LostItem> findByLocationContainingIgnoreCase(String location);
    List<LostItem> findByStatus(ItemStatus status);
    List<LostItem> findByTypeAndCategoryIgnoreCase(
            ItemType type, String category);

}