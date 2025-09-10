package org.animotion.animotionbackend.dto;

import lombok.Data;
import org.animotion.animotionbackend.entity.TaskPriority;

import java.util.List;

@Data
public class NewCardMessage {
    private String projectId;
    private String title;
    private String columnId;
    private String description;
    private List<String> assigneeId;
    private TaskPriority priority;
}
