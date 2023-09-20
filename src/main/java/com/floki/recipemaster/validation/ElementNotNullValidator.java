package com.floki.recipemaster.validation;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Collection;
import java.util.Objects;

public class ElementNotNullValidator implements ConstraintValidator<ElementsNotNull, Collection<?>> {
    @Override
    public boolean isValid(Collection<?> value, ConstraintValidatorContext context) {
        return value == null || value.stream().allMatch(Objects::nonNull);
    }
}