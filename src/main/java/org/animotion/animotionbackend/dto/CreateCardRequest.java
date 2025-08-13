package org.animotion.animotionbackend.dto;

import lombok.Data;

@Data
public class CreateCardRequest {
    private String title;
    private String columnId;
    private String projectId; // We need this for the security check
}