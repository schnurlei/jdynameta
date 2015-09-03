/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.jdynameta.model.asm.jpa.info;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author rainer
 */
public class JpaAsmAnntotationInfo
{

    private final Map<String, Object> nameValueMap;
    private final Class<?> annotaionClass;

    JpaAsmAnntotationInfo(Class<?> anAnnotationClass)
    {
        this.annotaionClass = anAnnotationClass;
        this.nameValueMap = new HashMap<>();
    }

    public void addValue(String name, Object value)
    {
        this.nameValueMap.put(name, value);
    }

    public Class<?> getAnnotaionClass()
    {
        return annotaionClass;
    }

    public Object getValue(String aValueName)
    {
        return this.nameValueMap.get(aValueName);
    }

}
