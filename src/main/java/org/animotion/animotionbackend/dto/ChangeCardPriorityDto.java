package org.animotion.animotionbackend.dto;

import lombok.Data;

@Data
public class ChangeCardPriorityDto {
    private String priorityId;
    private String cardId;
}
