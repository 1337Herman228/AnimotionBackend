package org.animotion.animotionbackend.entity;


import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Document(collection = "cards")
public class Card {
    @Id
    private String id;
    private String title;
    private String description;
    private String columnId;
    private String projectId;
    private String assigneeId;

    @CreatedDate // Spring Data will automatically set created date
    private LocalDateTime createdAt;

    @LastModifiedDate // Spring Data will automatically set last update date
    private LocalDateTime updatedAt;
}
