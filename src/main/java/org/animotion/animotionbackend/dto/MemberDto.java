package org.animotion.animotionbackend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberDto {
    private String id;
    private String name;
    private String email;
    private String image;
}
