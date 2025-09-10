package org.animotion.animotionbackend.dto;

import lombok.Data;

import java.util.List;

@Data
public class MoveColumnMessage {
    private String projectId;
    private List<String> columnOrder;
}
