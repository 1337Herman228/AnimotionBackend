package org.animotion.animotionbackend.dto;

import lombok.Data;

@Data
public class UpdateCardRequest {
    private String title;
    private String description;
}