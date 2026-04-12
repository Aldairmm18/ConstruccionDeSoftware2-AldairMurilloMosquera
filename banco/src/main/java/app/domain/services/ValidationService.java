package app.domain.services;

import app.domain.Exceptions.InvalidAmountException;

import java.math.BigDecimal;

/**
 * Service for COMMON VALIDATIONS
 * Handles: Reusable validation logic across the system
 */
public class ValidationService {

    private static final BigDecimal MAX_AMOUNT = new BigDecimal("999999999.99");
    private static final BigDecimal MIN_AMOUNT = BigDecimal.ZERO;
    private static final int MAX_DECIMAL_PLACES = 2;

    // ==================== AMOUNT VALIDATIONS ====================

    public void validateAmount(BigDecimal amount) {
        validateAmountNotNull(amount);
        validateAmountPositive(amount);
        validateAmountBelowMax(amount);
        validateDecimalPlaces(amount);
    }

    public void validateAmountNotNull(BigDecimal amount) {
        if (amount == null) {
            throw new InvalidAmountException("El monto no puede ser nulo");
        }
    }

    public void validateAmountPositive(BigDecimal amount) {
        if (amount.compareTo(MIN_AMOUNT) <= 0) {
            throw new InvalidAmountException(
                "El monto debe ser mayor a 0. Valor recibido: " + amount);
        }
    }

    public void validateAmountBelowMax(BigDecimal amount) {
        if (amount.compareTo(MAX_AMOUNT) > 0) {
            throw new InvalidAmountException(
                String.format("El monto excede el límite permitido de %s", MAX_AMOUNT));
        }
    }

    public void validateDecimalPlaces(BigDecimal amount) {
        if (amount.scale() > MAX_DECIMAL_PLACES) {
            throw new InvalidAmountException(
                String.format("El monto no puede tener más de %d decimales",
                    MAX_DECIMAL_PLACES));
        }
    }

    // ==================== STRING VALIDATIONS ====================

    public void validateNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(
                fieldName + " no puede estar vacío");
        }
    }

    public void validateLength(String value, String fieldName, int minLength, int maxLength) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " no puede ser nulo");
        }

        if (value.length() < minLength || value.length() > maxLength) {
            throw new IllegalArgumentException(
                String.format("%s debe tener entre %d y %d caracteres",
                    fieldName, minLength, maxLength));
        }
    }

    public void validatePattern(String value, String fieldName, String pattern, String errorMessage) {
        if (value == null || !value.matches(pattern)) {
            throw new IllegalArgumentException(
                fieldName + ": " + errorMessage);
        }
    }

    // ==================== NUMERIC VALIDATIONS ====================

    public void validateRange(int value, String fieldName, int min, int max) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(
                String.format("%s debe estar entre %d y %d", fieldName, min, max));
        }
    }

    public void validatePositiveInteger(int value, String fieldName) {
        if (value <= 0) {
            throw new IllegalArgumentException(
                fieldName + " debe ser mayor a 0");
        }
    }
}
