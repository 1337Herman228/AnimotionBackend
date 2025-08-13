package org.animotion.animotionbackend.dto;


import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class FullProjectDto {
    private String id;
    private String name;
    private String ownerId;
    private List<UserDto> members;
    private List<ColumnDto> columns;
}
