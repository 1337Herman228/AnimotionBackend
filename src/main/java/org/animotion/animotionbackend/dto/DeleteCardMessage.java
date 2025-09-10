package org.animotion.animotionbackend.dto;

import lombok.Data;

@Data
public class DeleteCardMessage {
    private String columnId;
    private String projectId;
    private String deletedCardId;
}
