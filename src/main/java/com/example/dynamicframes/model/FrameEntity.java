package com.example.dynamicframes.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "frames")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FrameEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(name = "display_order")
    private Integer displayOrder = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private FrameEntity parentFrame;

    @OneToMany(mappedBy = "parentFrame", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("displayOrder ASC")
    @Builder.Default
    private List<FrameEntity> subFrames = new ArrayList<>();

    @OneToMany(mappedBy = "frame", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("displayOrder ASC")
    @Builder.Default
    private List<FieldEntity> fields = new ArrayList<>();
}
