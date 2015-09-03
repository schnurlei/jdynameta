/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.jdynameta.model.asm.jpa;

import de.jdynameta.model.asm.jpa.info.JpaAsmAnntotationInfo;
import de.jdynameta.model.asm.jpa.info.JpaAsmClassInfo;
import de.jdynameta.model.asm.jpa.info.JpaAsmFieldInfo;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 *
 * @author rainer
 */
public class JpaAsmClassReader extends ClassVisitor
{
    private final JpaAsmClassInfo jpaInfo;

    public JpaAsmClassReader()
    {
        super(Opcodes.ASM4);
        jpaInfo = new JpaAsmClassInfo();
    }

    public JpaAsmClassInfo getJpaInfo()
    {
        return jpaInfo;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces)
    {
        super.visit(version, access, name, signature, superName, interfaces);
        jpaInfo.setClassName(name);
        jpaInfo.setSuperClassName(superName);
    }

    @Override
    public void visitSource(String source, String debug)
    {
        super.visitSource(source, debug);
    }

    @Override
    public void visitOuterClass(String owner, String name, String desc)
    {
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible)
    {
        JpaAsmAnntotationInfo annotationInfo = this.jpaInfo.addAnnotation(desc);
        if (annotationInfo != null)
        {
            //System.out.println("Annotation " + desc);
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
        System.out.println("Attribute " + attr);
    }

    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access)
    {
        System.out.println("visitInnerClass " + name);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value)
    {
        JpaAsmFieldInfo fieldInfo = new JpaAsmFieldInfo(name, desc, signature, value, access);
        this.jpaInfo.addField(fieldInfo);
        return new JpaFieldReader(fieldInfo);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
    {
        //			System.out.println("Method " + name + desc);
        return null;
    }

    @Override
    public void visitEnd()
    {
    }

}
