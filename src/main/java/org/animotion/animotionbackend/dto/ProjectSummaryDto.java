package org.animotion.animotionbackend.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectSummaryDto {
    private String id;
    private String name;
    private String ownerId;
}
