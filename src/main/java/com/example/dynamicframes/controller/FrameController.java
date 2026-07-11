package com.example.dynamicframes.controller;

import com.example.dynamicframes.dto.FieldDTO;
import com.example.dynamicframes.dto.FrameDTO;
import com.example.dynamicframes.service.FrameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/frames")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FrameController {

    private final FrameService frameService;

    @GetMapping
    public ResponseEntity<List<FrameDTO>> getRootFramesTree() {
        return ResponseEntity.ok(frameService.getRootFramesTree());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FrameDTO> getFrameById(@PathVariable Long id) {
        return frameService.getFrameById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<FrameDTO> createFrame(@RequestBody FrameDTO frameDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(frameService.createFrame(frameDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FrameDTO> updateFrame(@PathVariable Long id, @RequestBody FrameDTO frameDTO) {
        try {
            return ResponseEntity.ok(frameService.updateFrame(id, frameDTO));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFrame(@PathVariable Long id) {
        try {
            frameService.deleteFrame(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
