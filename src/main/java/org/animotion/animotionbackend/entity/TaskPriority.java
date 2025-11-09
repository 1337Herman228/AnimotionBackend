package org.animotion.animotionbackend.entity;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "task_priority")
public class TaskPriority {
    @Id
    private String id;
    // If is null, every project can be set this priority
    private String projectId;
    private String value;
    private String label;
    private String color;
}
