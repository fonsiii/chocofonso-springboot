package com.vs2dam.azarquiel.chocofonso_springboot.validation;

import com.vs2dam.azarquiel.chocofonso_springboot.dto.UpdateAddressDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.StringUtils;

public class RequiredBillingAddressValidator implements ConstraintValidator<RequiredBillingAddress, UpdateAddressDTO> {

    @Override
    public void initialize(RequiredBillingAddress constraintAnnotation) {
        // No es necesario inicializar nada
    }

    @Override
    public boolean isValid(UpdateAddressDTO dto, ConstraintValidatorContext context) {
        if (dto.getSameAsShipping() == null || !dto.getSameAsShipping()) {
            return StringUtils.hasText(dto.getBillingAddress()) &&
                    StringUtils.hasText(dto.getBillingCity()) &&
                    StringUtils.hasText(dto.getBillingPostalCode());
        }
        return true; // Si sameAsShipping es true, la validación pasa
    }
}