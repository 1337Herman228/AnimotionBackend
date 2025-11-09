package org.animotion.animotionbackend.dto;


import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class ColumnDto {
    private String id;
    private String title;
    private List<String> cardOrder;
    private List<CardDto> cards;
    private String projectId;
}