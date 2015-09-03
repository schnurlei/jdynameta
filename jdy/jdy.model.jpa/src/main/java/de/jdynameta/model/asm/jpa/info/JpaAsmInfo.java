/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.jdynameta.model.asm.jpa.info;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author rainer
 */
public class JpaAsmInfo
{

    private final Class<?>[] possibleAnotations;
    private final List<JpaAsmAnntotationInfo> annotations;

    public JpaAsmInfo(Class<?>[] allAnotationClasses)
    {
        this.annotations = new ArrayList<>();

        this.possibleAnotations = allAnotationClasses;
        for (Class<?> curClass : allAnotationClasses)
        {
            if (!curClass.isAnnotation())
            {
                throw new RuntimeException("No Annotation class " + curClass);
            }
        }
    }

    public JpaAsmAnntotationInfo getAnntotationInfo(Class<?> annotation)
    {

        JpaAsmAnntotationInfo infoForAnnotation = null;

        for (JpaAsmAnntotationInfo info : annotations)
        {
            if (info.getAnnotaionClass() == annotation)
            {
                infoForAnnotation = info;
            }
        }

        return infoForAnnotation;
    }

    public Class<?> getAnnotationClass(String desc)
    {

        Class<?> result = null;

        for (Class<?> annotationClass : possibleAnotations)
        {
            if (desc.endsWith(annotationClass.getName().replace('.', '/') + ";"))
            {
                result = annotationClass;
            }
        }
        return result;
    }

    public JpaAsmAnntotationInfo addAnnotation(Class<?> annotationClass)
    {

        JpaAsmAnntotationInfo annotation = new JpaAsmAnntotationInfo(annotationClass);
        this.annotations.add(annotation);

        return annotation;
    }

    public JpaAsmAnntotationInfo addAnnotation(String anAnnotationName)
    {

        Class<?> annotationClass = getAnnotationClass(anAnnotationName);

        if (annotationClass != null)
        {

            JpaAsmAnntotationInfo annotation = addAnnotation(annotationClass);
            return annotation;
        } else
        {
            return null;
        }
    }
}
