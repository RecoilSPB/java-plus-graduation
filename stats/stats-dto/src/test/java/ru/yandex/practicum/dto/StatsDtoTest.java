package ru.yandex.practicum.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class StatsDtoTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void testValidationFailsWhenAppIsBlank() {
        StatsDto hitDto = new StatsDto("", "some-uri", "192.168.0.1", LocalDateTime.now());

        Set<ConstraintViolation<StatsDto>> violations = validator.validate(hitDto);
        assertFalse(violations.isEmpty());
        assertEquals("Идентификатор сервиса для которого записывается информация не должен быть пустым.",
                violations.iterator().next().getMessage());
    }

    @Test
    void testValidationFailsWhenUriIsBlank() {
        StatsDto hitDto = new StatsDto("app-name", "", "192.168.0.1", LocalDateTime.now());

        Set<ConstraintViolation<StatsDto>> violations = validator.validate(hitDto);
        assertFalse(violations.isEmpty());
        assertEquals("URI для которого был осуществлен запрос не должен быть пустым.",
                violations.iterator().next().getMessage());
    }

    @Test
    void testValidationFailsWhenIpIsBlank() {
        StatsDto hitDto = new StatsDto("app-name", "some-uri", "", LocalDateTime.now());

        Set<ConstraintViolation<StatsDto>> violations = validator.validate(hitDto);
        assertFalse(violations.isEmpty());
        assertEquals("IP-адрес пользователя, осуществившего запрос не можен быть пустым",
                violations.iterator().next().getMessage());
    }

    @Test
    void testValidationPassesWithValidData() {
        StatsDto hitDto = new StatsDto("app-name", "some-uri", "192.168.0.1", LocalDateTime.now());

        Set<ConstraintViolation<StatsDto>> violations = validator.validate(hitDto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testCreateHitDtoWithValidData() {
        StatsDto hitDto = new StatsDto("app-name", "some-uri", "192.168.0.1", LocalDateTime.now());

        assertNotNull(hitDto);
        assertEquals("app-name", hitDto.getApp());
        assertEquals("some-uri", hitDto.getUri());
        assertEquals("192.168.0.1", hitDto.getIp());
    }
}
