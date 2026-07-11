package com.example.dynamicframes.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "fields")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FieldEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String label;

    @Column(nullable = false)
    private String type; // e.g. text, number, select, checkbox, textarea, date, email

    @Column(name = "display_order")
    private Integer displayOrder = 0;

    private boolean required = false;

    private String placeholder;

    @Column(length = 1000)
    private String options; // comma-separated options for select/radio etc.

    @Column(name = "validation_regex")
    private String validationRegex;

    @Column(name = "default_value")
    private String defaultValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "frame_id", nullable = false)
    private FrameEntity frame;
}
