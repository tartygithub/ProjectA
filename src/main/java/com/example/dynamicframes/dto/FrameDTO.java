package com.example.dynamicframes.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FrameDTO {
    private Long id;
    private String name;
    private String description;
    private Integer displayOrder;
    private Long parentId;
    private List<FrameDTO> subFrames;
    private List<FieldDTO> fields;
}
