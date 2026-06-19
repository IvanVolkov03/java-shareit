package ru.practicum.shareit.user.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UserDtoTest {

    private ObjectMapper objectMapper;
    private Validator validator;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testSerialize() throws Exception {
        UserDto userDto = new UserDto(1L, "John Doe", "john@example.com");

        String json = objectMapper.writeValueAsString(userDto);

        assertThat(json).contains("John Doe");
        assertThat(json).contains("john@example.com");
    }

    @Test
    void testDeserialize() throws Exception {
        String jsonContent = "{\"id\":1,\"name\":\"John Doe\",\"email\":\"john@example.com\"}";

        UserDto userDto = objectMapper.readValue(jsonContent, UserDto.class);

        assertThat(userDto.getId()).isEqualTo(1L);
        assertThat(userDto.getName()).isEqualTo("John Doe");
        assertThat(userDto.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void testValidation_blankEmail() {
        UserDto userDto = new UserDto(1L, "John Doe", "");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        assertThat(violations).isNotEmpty();
        assertThat(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Email cannot be empty") ||
                        v.getMessage().contains("email is required")))
                .isTrue();
    }

    @Test
    void testValidation_invalidEmail() {
        UserDto userDto = new UserDto(1L, "John Doe", "invalid-email");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        assertThat(violations).isNotEmpty();
        assertThat(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Invalid email format") ||
                        v.getMessage().contains("email")))
                .isTrue();
    }

    @Test
    void testValidation_blankName() {
        UserDto userDto = new UserDto(1L, "", "john@example.com");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        assertThat(violations).isNotEmpty();
        assertThat(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Name cannot be empty") ||
                        v.getMessage().contains("name")))
                .isTrue();
    }

    @Test
    void testValidation_validUser() {
        UserDto userDto = new UserDto(1L, "John Doe", "john@example.com");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        assertThat(violations).isEmpty();
    }
}