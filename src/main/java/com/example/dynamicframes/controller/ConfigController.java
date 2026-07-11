package com.example.dynamicframes.controller;

import com.example.dynamicframes.dto.FrameDTO;
import com.example.dynamicframes.service.FrameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/config")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ConfigController {

    private final FrameService frameService;

    @GetMapping("/export")
    public ResponseEntity<List<FrameDTO>> exportConfiguration() {
        return ResponseEntity.ok(frameService.getRootFramesTree());
    }

    @PostMapping("/import")
    public ResponseEntity<Void> importConfiguration(@RequestBody List<FrameDTO> frames) {
        try {
            frameService.importConfiguration(frames);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
