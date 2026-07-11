package com.example.dynamicframes.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FieldDTO {
    private Long id;
    private String name;
    private String label;
    private String type; // text, number, select, checkbox, textarea, date, email
    private Integer displayOrder;
    private boolean required;
    private String placeholder;
    private String options; // comma-separated options
    private String validationRegex;
    private String defaultValue;
    private Long frameId;
}
