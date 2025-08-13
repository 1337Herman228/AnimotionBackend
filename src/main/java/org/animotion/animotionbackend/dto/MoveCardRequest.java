package org.animotion.animotionbackend.dto;

import lombok.Data;

@Data
public class MoveCardRequest {
    private String cardId;
    private String sourceColumnId;
    private String destinationColumnId;
    private int destinationIndex;
}