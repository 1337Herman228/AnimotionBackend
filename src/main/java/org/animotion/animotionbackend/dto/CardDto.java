package org.animotion.animotionbackend.dto;


import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class CardDto {
    private String id;
    private String title;
    private String description;
    private String projectId;
    private String columnId;
    private String assigneeId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
