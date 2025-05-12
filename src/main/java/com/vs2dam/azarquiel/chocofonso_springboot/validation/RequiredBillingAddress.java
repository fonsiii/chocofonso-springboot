package com.vs2dam.azarquiel.chocofonso_springboot.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = RequiredBillingAddressValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiredBillingAddress {
    String message() default "La dirección, ciudad y código postal de facturación son obligatorios si no es la misma que la de envío.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}