package com.example.dynamicframes.service;

import com.example.dynamicframes.dto.FieldDTO;
import com.example.dynamicframes.dto.FrameDTO;
import com.example.dynamicframes.model.FieldEntity;
import com.example.dynamicframes.model.FrameEntity;
import com.example.dynamicframes.repository.FieldRepository;
import com.example.dynamicframes.repository.FrameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FrameService {

    private final FrameRepository frameRepository;
    private final FieldRepository fieldRepository;

    @Transactional(readOnly = true)
    public List<FrameDTO> getRootFramesTree() {
        List<FrameEntity> roots = frameRepository.findRootFrames();
        return roots.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<FrameDTO> getFrameById(Long id) {
        return frameRepository.findById(id).map(this::convertToDTO);
    }

    @Transactional
    public FrameDTO createFrame(FrameDTO frameDTO) {
        FrameEntity entity = FrameEntity.builder()
                .name(frameDTO.getName())
                .description(frameDTO.getDescription())
                .displayOrder(frameDTO.getDisplayOrder() != null ? frameDTO.getDisplayOrder() : 0)
                .build();

        if (frameDTO.getParentId() != null) {
            FrameEntity parent = frameRepository.findById(frameDTO.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent Frame not found with ID: " + frameDTO.getParentId()));
            entity.setParentFrame(parent);
            if (parent.getSubFrames() == null) {
                parent.setSubFrames(new ArrayList<>());
            }
            parent.getSubFrames().add(entity);
        }

        FrameEntity saved = frameRepository.save(entity);
        return convertToDTO(saved);
    }

    @Transactional
    public FrameDTO updateFrame(Long id, FrameDTO frameDTO) {
        FrameEntity entity = frameRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Frame not found with ID: " + id));

        entity.setName(frameDTO.getName());
        entity.setDescription(frameDTO.getDescription());
        entity.setDisplayOrder(frameDTO.getDisplayOrder() != null ? frameDTO.getDisplayOrder() : 0);

        if (frameDTO.getParentId() != null) {
            if (frameDTO.getParentId().equals(id)) {
                throw new IllegalArgumentException("A frame cannot be its own parent.");
            }
            FrameEntity parent = frameRepository.findById(frameDTO.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent Frame not found with ID: " + frameDTO.getParentId()));

            if (entity.getParentFrame() != null) {
                entity.getParentFrame().getSubFrames().remove(entity);
            }
            entity.setParentFrame(parent);
            if (parent.getSubFrames() == null) {
                parent.setSubFrames(new ArrayList<>());
            }
            parent.getSubFrames().add(entity);
        } else {
            if (entity.getParentFrame() != null) {
                entity.getParentFrame().getSubFrames().remove(entity);
            }
            entity.setParentFrame(null);
        }

        FrameEntity saved = frameRepository.save(entity);
        return convertToDTO(saved);
    }

    @Transactional
    public void deleteFrame(Long id) {
        FrameEntity entity = frameRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Frame not found with ID: " + id));

        if (entity.getParentFrame() != null) {
            entity.getParentFrame().getSubFrames().remove(entity);
        }
        frameRepository.delete(entity);
    }

    @Transactional
    public FieldDTO createField(FieldDTO fieldDTO) {
        FrameEntity frame = frameRepository.findById(fieldDTO.getFrameId())
                .orElseThrow(() -> new IllegalArgumentException("Frame not found with ID: " + fieldDTO.getFrameId()));

        FieldEntity field = FieldEntity.builder()
                .name(fieldDTO.getName())
                .label(fieldDTO.getLabel())
                .type(fieldDTO.getType())
                .displayOrder(fieldDTO.getDisplayOrder() != null ? fieldDTO.getDisplayOrder() : 0)
                .required(fieldDTO.isRequired())
                .placeholder(fieldDTO.getPlaceholder())
                .options(fieldDTO.getOptions())
                .validationRegex(fieldDTO.getValidationRegex())
                .defaultValue(fieldDTO.getDefaultValue())
                .frame(frame)
                .build();

        if (frame.getFields() == null) {
            frame.setFields(new ArrayList<>());
        }
        frame.getFields().add(field);

        FieldEntity saved = fieldRepository.save(field);
        return convertToFieldDTO(saved);
    }

    @Transactional
    public FieldDTO updateField(Long id, FieldDTO fieldDTO) {
        FieldEntity field = fieldRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Field not found with ID: " + id));

        if (fieldDTO.getFrameId() != null && !field.getFrame().getId().equals(fieldDTO.getFrameId())) {
            FrameEntity newFrame = frameRepository.findById(fieldDTO.getFrameId())
                    .orElseThrow(() -> new IllegalArgumentException("Frame not found with ID: " + fieldDTO.getFrameId()));

            field.getFrame().getFields().remove(field);
            field.setFrame(newFrame);
            if (newFrame.getFields() == null) {
                newFrame.setFields(new ArrayList<>());
            }
            newFrame.getFields().add(field);
        }

        field.setName(fieldDTO.getName());
        field.setLabel(fieldDTO.getLabel());
        field.setType(fieldDTO.getType());
        field.setDisplayOrder(fieldDTO.getDisplayOrder() != null ? fieldDTO.getDisplayOrder() : 0);
        field.setRequired(fieldDTO.isRequired());
        field.setPlaceholder(fieldDTO.getPlaceholder());
        field.setOptions(fieldDTO.getOptions());
        field.setValidationRegex(fieldDTO.getValidationRegex());
        field.setDefaultValue(fieldDTO.getDefaultValue());

        FieldEntity saved = fieldRepository.save(field);
        return convertToFieldDTO(saved);
    }

    @Transactional
    public void deleteField(Long id) {
        FieldEntity field = fieldRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Field not found with ID: " + id));

        if (field.getFrame() != null) {
            field.getFrame().getFields().remove(field);
        }
        fieldRepository.delete(field);
    }

    @Transactional
    public void importConfiguration(List<FrameDTO> frames) {
        // Clear existing configurations completely to prevent conflicts
        fieldRepository.deleteAll();
        frameRepository.deleteAll();
        fieldRepository.flush();
        frameRepository.flush();

        for (FrameDTO rootDTO : frames) {
            saveFrameTreeRecursive(rootDTO, null);
        }
    }

    private void saveFrameTreeRecursive(FrameDTO dto, FrameEntity parent) {
        FrameEntity frameEntity = FrameEntity.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .displayOrder(dto.getDisplayOrder() != null ? dto.getDisplayOrder() : 0)
                .parentFrame(parent)
                .build();

        FrameEntity savedFrame = frameRepository.save(frameEntity);

        if (dto.getFields() != null) {
            for (FieldDTO fieldDTO : dto.getFields()) {
                FieldEntity fieldEntity = FieldEntity.builder()
                        .name(fieldDTO.getName())
                        .label(fieldDTO.getLabel())
                        .type(fieldDTO.getType())
                        .displayOrder(fieldDTO.getDisplayOrder() != null ? fieldDTO.getDisplayOrder() : 0)
                        .required(fieldDTO.isRequired())
                        .placeholder(fieldDTO.getPlaceholder())
                        .options(fieldDTO.getOptions())
                        .validationRegex(fieldDTO.getValidationRegex())
                        .defaultValue(fieldDTO.getDefaultValue())
                        .frame(savedFrame)
                        .build();
                fieldRepository.save(fieldEntity);
                if (savedFrame.getFields() == null) {
                    savedFrame.setFields(new ArrayList<>());
                }
                savedFrame.getFields().add(fieldEntity);
            }
        }

        if (dto.getSubFrames() != null) {
            for (FrameDTO subDTO : dto.getSubFrames()) {
                saveFrameTreeRecursive(subDTO, savedFrame);
            }
        }
    }

    public FrameDTO convertToDTO(FrameEntity entity) {
        if (entity == null) return null;

        List<FrameDTO> subFrameDTOs = new ArrayList<>();
        if (entity.getSubFrames() != null) {
            subFrameDTOs = entity.getSubFrames().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        }

        List<FieldDTO> fieldDTOs = new ArrayList<>();
        if (entity.getFields() != null) {
            fieldDTOs = entity.getFields().stream()
                    .map(this::convertToFieldDTO)
                    .collect(Collectors.toList());
        }

        return FrameDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .displayOrder(entity.getDisplayOrder())
                .parentId(entity.getParentFrame() != null ? entity.getParentFrame().getId() : null)
                .subFrames(subFrameDTOs)
                .fields(fieldDTOs)
                .build();
    }

    public FieldDTO convertToFieldDTO(FieldEntity entity) {
        if (entity == null) return null;
        return FieldDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .label(entity.getLabel())
                .type(entity.getType())
                .displayOrder(entity.getDisplayOrder())
                .required(entity.isRequired())
                .placeholder(entity.getPlaceholder())
                .options(entity.getOptions())
                .validationRegex(entity.getValidationRegex())
                .defaultValue(entity.getDefaultValue())
                .frameId(entity.getFrame() != null ? entity.getFrame().getId() : null)
                .build();
    }
}
