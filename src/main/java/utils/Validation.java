package utils;

import java.math.BigDecimal;
import java.util.Objects;

public class Validation {

    public static String requireNotBlank(String value, String fieldName){
        requireNotNull(value, fieldName);
        if (value.isBlank())
            throw new IllegalArgumentException(fieldName + " can't be blank");
        return value;
    }

    public static BigDecimal requireValidWeight(BigDecimal value, String fieldName) {
        requireNotNull(value, fieldName + " weight");
        if (value.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException(fieldName + " weight must be greater than 0");
        return value;
    }

    public static <T> T requireNotNull(T object, String fieldName){
        return Objects.requireNonNull(object, fieldName + " can't be null");
    }
}
