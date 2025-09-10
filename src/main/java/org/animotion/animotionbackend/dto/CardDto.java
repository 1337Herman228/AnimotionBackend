package org.animotion.animotionbackend.dto;


import lombok.Builder;
import lombok.Data;
import org.animotion.animotionbackend.entity.TaskPriority;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class CardDto {
    private String id;
    private String title;
    private String description;
    private String projectId;
    private String columnId;
    private List<MemberDto> appointedMembers;
    private TaskPriority priority;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
