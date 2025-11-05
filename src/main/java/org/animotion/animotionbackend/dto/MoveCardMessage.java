package org.animotion.animotionbackend.dto;

import lombok.Data;

import java.util.List;

@Data
public class MoveCardMessage {
    private String correlationId;
    private String projectId;
    private ReducedColumnDto sourceColumn;
    private ReducedColumnDto destinationColumn;
    private String cardId;
    private List<String> queryKey;
}
