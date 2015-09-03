/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.jdynameta.model.asm.jpa.info;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.MappedSuperclass;

/**
 *
 * @author rainer
 */
public class JpaAsmClassInfo extends JpaAsmInfo
{
    private String className;
    private String superClassName;
    private final List<JpaAsmFieldInfo> allFields;

    public JpaAsmClassInfo()
    {
        super(new Class<?>[]
        {
            Entity.class, Table.class, MappedSuperclass.class, Embeddable.class
        });
        this.allFields = new ArrayList<>();
    }

    public boolean isJpaClass()
    {
        return getEntityInfo() != null || getMappedSuperclassInfo() != null || getEmbeddableInfo() != null;
    }

    public JpaAsmAnntotationInfo getEntityInfo()
    {
        return getAnntotationInfo(Entity.class);
    }

    public JpaAsmAnntotationInfo getMappedSuperclassInfo()
    {
        return getAnntotationInfo(MappedSuperclass.class);
    }

    public JpaAsmAnntotationInfo getEmbeddableInfo()
    {
        return getAnntotationInfo(Embeddable.class);
    }

    public String getTableName()
    {
        JpaAsmAnntotationInfo tableInfo = getAnntotationInfo(Table.class);
        String tableName = (tableInfo != null) ? tableInfo.getValue("name").toString() : null;

        return (tableName != null && !tableName.isEmpty()) ? tableName : getSimpleClassName();
    }

    public JpaAsmAnntotationInfo getTableInfo()
    {
        return getAnntotationInfo(Table.class);
    }

    public String getSimpleClassName()
    {
        return className.substring(className.lastIndexOf('/') + 1);
    }

    public String getPackageName()
    {
        return className.substring(0, className.lastIndexOf('/')).replace('/', '.');
    }

    public String getClassName()
    {
        return className;
    }

    public String getSuperClassName()
    {
        return superClassName;
    }

    public String getSuperSimpleClassName()
    {
        return getSuperClassName().substring(getSuperClassName().lastIndexOf('/') + 1);
    }

    public void setClassName(String name)
    {
        this.className = name;
    }

    public void setSuperClassName(String superName)
    {
        this.superClassName = superName;
    }

    public void addField(JpaAsmFieldInfo aFieldInfo)
    {
        this.allFields.add(aFieldInfo);
    }

    public List<JpaAsmFieldInfo> getAllFields()
    {
        return allFields;
    }

}
