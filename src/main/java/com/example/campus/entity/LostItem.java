package com.example.campus.entity;

import com.example.campus.dto.LostItemRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "lost_item")
@EntityListeners(AuditingEntityListener.class)
public class LostItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemStatus status = ItemStatus.UNRETURNED;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Category category;

    @Column(nullable = false, length = 100)
    private String location;

    @Column(nullable = false, length = 100)
    private String contact;

    @Column(nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @LastModifiedDate
    private LocalDateTime updatedAt;

    public LostItem(LostItemRequest request) {
        this.title = request.title();
        this.description = request.description();
        this.type = request.type();
        this.category = request.category();
        this.location = request.location();
        this.contact = request.contact();
    }

    public void update(LostItemRequest request) {
        this.title = request.title();
        this.description = request.description();
        this.type = request.type();
        this.category = request.category();
        this.location = request.location();
        this.contact = request.contact();
    }

    public void markAsReturned() {
        if (status == ItemStatus.RETURNED) {
            return;
//            throw new AlreadyReturnedException();
        }
        status = ItemStatus.RETURNED;
    }
}