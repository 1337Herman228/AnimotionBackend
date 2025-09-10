package org.animotion.animotionbackend.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.animotion.animotionbackend.entity.Card;

import java.util.List;

@Data
public class MoveCardMessage {
    private String projectId;
    private ReducedColumnDto sourceColumn;
    private ReducedColumnDto destinationColumn;
    private Card card;
}
