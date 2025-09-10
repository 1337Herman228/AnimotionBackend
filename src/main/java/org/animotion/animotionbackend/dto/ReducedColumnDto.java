package org.animotion.animotionbackend.dto;

import lombok.Data;

import java.util.List;

@Data
public class ReducedColumnDto {
    public String id;
    public List<String> cardOrder;
}
