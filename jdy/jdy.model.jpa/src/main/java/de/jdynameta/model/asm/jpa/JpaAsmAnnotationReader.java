/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.jdynameta.model.asm.jpa;

import de.jdynameta.model.asm.jpa.info.JpaAsmAnntotationInfo;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;

/**
 *
 */
public class JpaAsmAnnotationReader extends AnnotationVisitor
{
    private final String type;
    private final JpaAsmAnntotationInfo annotationInfo;

    public JpaAsmAnnotationReader(String aType, JpaAsmAnntotationInfo anAnnotaion)
    {
        super(Opcodes.ASM4);
        assert (anAnnotaion != null);
        this.annotationInfo = anAnnotaion;
        this.type = aType;
    }

    @Override
    public void visit(String name, Object value)
    {
        super.visit(name, value); 
        annotationInfo.addValue(name, value);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String name, String desc)
    {
        return super.visitAnnotation(name, desc); 
    }

    @Override
    public AnnotationVisitor visitArray(String name)
    {
        //			System.out.println(this.type +" Annotation visitArray");
        return super.visitArray(name); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void visitEnd()
    {
        //			System.out.println(this.type +" Annotation visitEnd");
    }

    @Override
    public void visitEnum(String name, String desc, String value)
    {
        System.out.println(this.type + " Annotation visitEnum" + name + desc + value);
    }

}
