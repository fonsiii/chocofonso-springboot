package com.vs2dam.azarquiel.chocofonso_springboot.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class EstadoReviewsConverter implements AttributeConverter<EstadoPublicacion,
        String> {

    @Override
    public String convertToDatabaseColumn(EstadoPublicacion estado) {
        return estado == null ? null : estado.name().toLowerCase(); // enum -> bd
    }

    @Override
    public EstadoPublicacion convertToEntityAttribute(String dbData) {
        return dbData == null ? null : EstadoPublicacion.valueOf(dbData.toUpperCase()); // bd
        // -> enum
    }
}
