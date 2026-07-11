package com.example.dynamicframes.controller;

import com.example.dynamicframes.dto.FieldDTO;
import com.example.dynamicframes.dto.FrameDTO;
import com.example.dynamicframes.service.FrameService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/submit")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FormSubmitController {

    private final FrameService frameService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> submitForm(@RequestBody Map<String, Object> submissionData) {
        List<FrameDTO> roots = frameService.getRootFramesTree();
        Map<String, String> errors = new HashMap<>();

        // Validate recursively
        for (FrameDTO root : roots) {
            validateFrameFields(root, submissionData, errors);
        }

        Map<String, Object> response = new HashMap<>();
        if (!errors.isEmpty()) {
            response.put("success", false);
            response.put("errors", errors);
            return ResponseEntity.badRequest().body(response);
        }

        response.put("success", true);
        response.put("message", "Form submitted successfully!");
        response.put("data", submissionData);
        return ResponseEntity.ok(response);
    }

    private void validateFrameFields(FrameDTO frame, Map<String, Object> data, Map<String, String> errors) {
        if (frame.getFields() != null) {
            for (FieldDTO field : frame.getFields()) {
                Object val = data.get(field.getName());
                String strValue = val == null ? "" : val.toString().trim();

                // Check required
                if (field.isRequired() && strValue.isEmpty()) {
                    errors.put(field.getName(), field.getLabel() + " is required.");
                    continue;
                }

                // Check email format if email type and not empty
                if ("email".equalsIgnoreCase(field.getType()) && !strValue.isEmpty()) {
                    String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
                    if (!Pattern.matches(emailRegex, strValue)) {
                        errors.put(field.getName(), field.getLabel() + " must be a valid email address.");
                        continue;
                    }
                }

                // Check regex if defined and not empty
                if (field.getValidationRegex() != null && !field.getValidationRegex().trim().isEmpty() && !strValue.isEmpty()) {
                    try {
                        if (!Pattern.matches(field.getValidationRegex(), strValue)) {
                            errors.put(field.getName(), field.getLabel() + " format is invalid.");
                        }
                    } catch (Exception e) {
                        // ignore malformed regex silently or log
                    }
                }
            }
        }

        if (frame.getSubFrames() != null) {
            for (FrameDTO subFrame : frame.getSubFrames()) {
                validateFrameFields(subFrame, data, errors);
            }
        }
    }
}
