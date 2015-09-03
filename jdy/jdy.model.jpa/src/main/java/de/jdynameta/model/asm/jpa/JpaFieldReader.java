/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.jdynameta.model.asm.jpa;

import de.jdynameta.model.asm.jpa.info.JpaAsmAnntotationInfo;
import de.jdynameta.model.asm.jpa.info.JpaAsmFieldInfo;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;

/**
 *
 */
public class JpaFieldReader extends FieldVisitor
{
    private final JpaAsmFieldInfo fieldInfo;

    public JpaFieldReader(JpaAsmFieldInfo aFieldInfo)
    {
        super(Opcodes.ASM4);
        this.fieldInfo = aFieldInfo;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible)
    {
        JpaAsmAnntotationInfo annotationInfo = fieldInfo.addAnnotation(desc);
        if (annotationInfo != null)
        {
            return new JpaAsmAnnotationReader("Class", annotationInfo);
        } else
        {
            System.out.println("**********Invalid Annotation " + desc);
            return super.visitAnnotation(desc, visible);
        }
    }

    @Override
    public void visitAttribute(Attribute attr)
    {
        super.visitAttribute(attr);
    }

    @Override
    public void visitEnd()
    {
        super.visitEnd();
    }

}
