package com.vs2dam.azarquiel.chocofonso_springboot.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class EstadoProductoConverter implements AttributeConverter<EstadoProducto, String> {

    @Override
    public String convertToDatabaseColumn(EstadoProducto estado) {
        return estado == null ? null : estado.name().toLowerCase(); // enum -> bd
    }

    @Override
    public EstadoProducto convertToEntityAttribute(String dbData) {
        return dbData == null ? null : EstadoProducto.valueOf(dbData.toUpperCase()); // bd -> enum
    }
}
