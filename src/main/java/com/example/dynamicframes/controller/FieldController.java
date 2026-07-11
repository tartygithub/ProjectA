package com.example.dynamicframes.controller;

import com.example.dynamicframes.dto.FieldDTO;
import com.example.dynamicframes.service.FrameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fields")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FieldController {

    private final FrameService frameService;

    @PostMapping
    public ResponseEntity<FieldDTO> createField(@RequestBody FieldDTO fieldDTO) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(frameService.createField(fieldDTO));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<FieldDTO> updateField(@PathVariable Long id, @RequestBody FieldDTO fieldDTO) {
        try {
            return ResponseEntity.ok(frameService.updateField(id, fieldDTO));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteField(@PathVariable Long id) {
        try {
            frameService.deleteField(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
