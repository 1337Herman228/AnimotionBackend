package org.animotion.animotionbackend.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;
@Data
@Document(collection = "columns")
public class Column {
    @Id
    private String id;
    private String title;
    private String projectId;
    private List<String> cardOrder;
}