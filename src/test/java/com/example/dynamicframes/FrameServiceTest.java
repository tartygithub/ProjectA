package com.example.dynamicframes;

import com.example.dynamicframes.dto.FieldDTO;
import com.example.dynamicframes.dto.FrameDTO;
import com.example.dynamicframes.service.FrameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class FrameServiceTest {

    @Autowired
    private FrameService frameService;

    private FrameDTO rootFrame;

    @BeforeEach
    public void setup() {
        // Create root frame
        rootFrame = frameService.createFrame(FrameDTO.builder()
                .name("Root Service Frame")
                .description("Parent Frame Description")
                .displayOrder(1)
                .build());
    }

    @Test
    public void testCreateFrameAndRetrieveTree() {
        // Create sub frame
        FrameDTO subFrame = frameService.createFrame(FrameDTO.builder()
                .name("Sub Service Frame")
                .description("Child Frame Description")
                .displayOrder(1)
                .parentId(rootFrame.getId())
                .build());

        // Create field
        FieldDTO field = frameService.createField(FieldDTO.builder()
                .name("testField")
                .label("Test Field")
                .type("text")
                .required(true)
                .frameId(subFrame.getId())
                .build());

        List<FrameDTO> tree = frameService.getRootFramesTree();
        assertFalse(tree.isEmpty());

        FrameDTO rootInTree = tree.stream()
                .filter(f -> f.getId().equals(rootFrame.getId()))
                .findFirst()
                .orElse(null);

        assertNotNull(rootInTree);
        assertEquals("Root Service Frame", rootInTree.getName());
        assertEquals(1, rootInTree.getSubFrames().size());

        FrameDTO childInTree = rootInTree.getSubFrames().get(0);
        assertEquals("Sub Service Frame", childInTree.getName());
        assertEquals(1, childInTree.getFields().size());
        assertEquals("testField", childInTree.getFields().get(0).getName());
    }

    @Test
    public void testImportExportConfiguration() {
        FrameDTO customRoot = FrameDTO.builder()
                .name("Imported Root")
                .description("Description")
                .displayOrder(0)
                .fields(Collections.singletonList(FieldDTO.builder()
                        .name("impField")
                        .label("Imported Field")
                        .type("number")
                        .required(false)
                        .build()))
                .build();

        frameService.importConfiguration(Collections.singletonList(customRoot));

        List<FrameDTO> tree = frameService.getRootFramesTree();
        assertEquals(1, tree.size());
        assertEquals("Imported Root", tree.get(0).getName());
        assertEquals(1, tree.get(0).getFields().size());
        assertEquals("impField", tree.get(0).getFields().get(0).getName());
    }
}
