package com.example.dynamicframes;

import com.example.dynamicframes.dto.FieldDTO;
import com.example.dynamicframes.dto.FrameDTO;
import com.example.dynamicframes.service.FrameService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final FrameService frameService;

    @Override
    public void run(String... args) throws Exception {
        // Only initialize if there are no frames existing
        if (frameService.getRootFramesTree().isEmpty()) {
            initDefaultConfiguration();
        }
    }

    private void initDefaultConfiguration() {
        // Level 1: User Profile Form (Main Frame)
        FrameDTO userProfile = frameService.createFrame(FrameDTO.builder()
                .name("User Profile Information")
                .description("Please fill in your primary details and configure your profile options.")
                .displayOrder(1)
                .build());

        // Add fields to main profile frame
        frameService.createField(FieldDTO.builder()
                .name("fullName")
                .label("Full Name")
                .type("text")
                .displayOrder(1)
                .required(true)
                .placeholder("Enter your full name")
                .frameId(userProfile.getId())
                .build());

        frameService.createField(FieldDTO.builder()
                .name("email")
                .label("Email Address")
                .type("email")
                .displayOrder(2)
                .required(true)
                .placeholder("username@example.com")
                .validationRegex("^[A-Za-z0-9+_.-]+@(.+)$")
                .frameId(userProfile.getId())
                .build());

        // Level 2: Sub-frame (Contact Details) under User Profile
        FrameDTO contactDetails = frameService.createFrame(FrameDTO.builder()
                .name("Contact Details")
                .description("Provide physical address and communication channels.")
                .displayOrder(1)
                .parentId(userProfile.getId())
                .build());

        frameService.createField(FieldDTO.builder()
                .name("phoneNumber")
                .label("Phone Number")
                .type("text")
                .displayOrder(1)
                .required(true)
                .placeholder("+1 (555) 000-0000")
                .validationRegex("^\\+?[0-9\\s\\-\\(]{7,20}$")
                .frameId(contactDetails.getId())
                .build());

        // Level 3: Nested sub-frame (Billing Address) under Contact Details
        FrameDTO billingAddress = frameService.createFrame(FrameDTO.builder()
                .name("Billing Address Details")
                .description("Enter your secondary/billing address elements.")
                .displayOrder(1)
                .parentId(contactDetails.getId())
                .build());

        frameService.createField(FieldDTO.builder()
                .name("street")
                .label("Street Address")
                .type("text")
                .displayOrder(1)
                .required(true)
                .placeholder("123 Main St")
                .frameId(billingAddress.getId())
                .build());

        frameService.createField(FieldDTO.builder()
                .name("country")
                .label("Country")
                .type("select")
                .displayOrder(2)
                .required(true)
                .options("United States,Canada,United Kingdom,Germany,France,Other")
                .defaultValue("United States")
                .frameId(billingAddress.getId())
                .build());

        // Level 2: Another Sub-frame (Preferences) under User Profile
        FrameDTO preferences = frameService.createFrame(FrameDTO.builder()
                .name("Account Preferences")
                .description("Configure application behavior and notification schedules.")
                .displayOrder(2)
                .parentId(userProfile.getId())
                .build());

        frameService.createField(FieldDTO.builder()
                .name("newsletter")
                .label("Subscribe to Newsletter")
                .type("checkbox")
                .displayOrder(1)
                .defaultValue("true")
                .frameId(preferences.getId())
                .build());

        frameService.createField(FieldDTO.builder()
                .name("theme")
                .label("Preferred Theme")
                .type("select")
                .displayOrder(2)
                .options("Light,Dark,System Default")
                .defaultValue("Light")
                .frameId(preferences.getId())
                .build());
    }
}
