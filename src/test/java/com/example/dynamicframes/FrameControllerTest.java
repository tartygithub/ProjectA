package com.example.dynamicframes;

import com.example.dynamicframes.dto.FieldDTO;
import com.example.dynamicframes.dto.FrameDTO;
import com.example.dynamicframes.service.FrameService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class FrameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FrameService frameService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetRootFramesTree() throws Exception {
        mockMvc.perform(get("/api/frames"))
                .andExpect(status().isOk());
    }

    @Test
    public void testSubmitFormWithValidationErrors() throws Exception {
        // Clear all & Create a simple form configuration with a required field
        frameService.importConfiguration(java.util.Collections.emptyList());

        FrameDTO root = frameService.createFrame(FrameDTO.builder()
                .name("Root Form")
                .displayOrder(1)
                .build());

        frameService.createField(FieldDTO.builder()
                .name("requiredPhone")
                .label("Phone Number")
                .type("text")
                .required(true)
                .validationRegex("^\\d{10}$")
                .frameId(root.getId())
                .build());

        // Test 1: Empty body should fail due to required constraint
        Map<String, Object> invalidPayload = new HashMap<>();
        mockMvc.perform(post("/api/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidPayload)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errors.requiredPhone").exists());

        // Test 2: Invalid format matching regex should fail
        invalidPayload.put("requiredPhone", "123-abc");
        mockMvc.perform(post("/api/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidPayload)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errors.requiredPhone").exists());

        // Test 3: Valid field matching validation rules should succeed
        Map<String, Object> validPayload = new HashMap<>();
        validPayload.put("requiredPhone", "1234567890");
        mockMvc.perform(post("/api/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validPayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
