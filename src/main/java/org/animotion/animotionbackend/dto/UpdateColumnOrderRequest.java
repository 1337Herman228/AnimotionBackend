package org.animotion.animotionbackend.dto;

import lombok.Data;
import java.util.List;

@Data
public class UpdateColumnOrderRequest {
    private List<String> columnOrder;
}
