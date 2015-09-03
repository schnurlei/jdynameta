/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.spatentaten.spatentatenserver.entity;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 *
 * @author rainer
 */
@Converter(autoApply = true)
public class LandkreisConverter implements AttributeConverter<Landkreis, String> {

    @Override
    public String convertToDatabaseColumn(Landkreis attribute) {
        return attribute.name();
    }

    @Override
    public Landkreis convertToEntityAttribute(String dbData) {
        return Landkreis.valueOf(dbData);
    }
    
    
}
