/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.jdynameta.model.asm.jpa.info;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.objectweb.asm.Opcodes;

/**
 *
 * @author rainer
 */
public class JpaAsmFieldInfo extends JpaAsmInfo
{
	private String fieldName;
	private final String name;
	private final String desc;
	private final String signature;
	private final Object value;
	private final int access;

	public JpaAsmFieldInfo(String aName, String aDesc, String aSignature, Object aValue, int aAccess) {
		super( new Class<?>[] {Id.class, Basic.class, Column.class, JoinColumn.class, Version.class, Transient.class, Enumerated.class,
			OneToOne.class, OneToMany.class, ManyToOne.class, ManyToMany.class, GeneratedValue.class,EmbeddedId.class, Embedded.class,
			javax.validation.constraints.Min.class, Max.class, 
			DecimalMin.class, DecimalMax.class, Digits.class, NotNull.class, Pattern.class, Size.class
		});
		
		this.name = aName;
		this.desc = aDesc;
		this.signature = aSignature;
		this.value = aValue;
		this.access = aAccess;
	}

	public JpaAsmAnntotationInfo getIdInfo() 
	{
		return getAnntotationInfo(Id.class);
	}


	public JpaAsmAnntotationInfo getOneToOneInfo() 
	{
		return getAnntotationInfo(OneToOne.class);
	}

	public JpaAsmAnntotationInfo getOneToManyInfo() 
	{
		return getAnntotationInfo(OneToMany.class);
	}

	public JpaAsmAnntotationInfo getManyToManyInfo() 
	{
		return getAnntotationInfo(ManyToMany.class);
	}

	public JpaAsmAnntotationInfo getManyToOneInfo() 
	{
		return getAnntotationInfo(ManyToOne.class);
	}
	
	public JpaAsmAnntotationInfo getGeneratedInfo() 
	{
		return getAnntotationInfo(GeneratedValue.class);
	}
	
	
	public JpaAsmAnntotationInfo getEnumeratedInfo() 
	{
		return getAnntotationInfo(Enumerated.class);
	}
	
	public JpaAsmAnntotationInfo getTransientInfo() 
	{
		return getAnntotationInfo(Transient.class);
	}

	public JpaAsmAnntotationInfo getEmbeddedIdInfo() 
	{
		return getAnntotationInfo(EmbeddedId.class);
	}

	public JpaAsmAnntotationInfo getEmbeddedInfo() 
	{
		return getAnntotationInfo(Embedded.class);
	}

	public String getColumnName() 
	{
		JpaAsmAnntotationInfo colInfo = getAnntotationInfo(Column.class);
		String colName = (colInfo != null) ? colInfo.getValue("name").toString() : null;
		return (colName != null && !colName.isEmpty()) ? colName : getName();
	}
	
	public boolean isOptional() 
	{
		JpaAsmAnntotationInfo basicInfo = getAnntotationInfo(Basic.class);
		Object optional = (basicInfo != null) ? basicInfo.getValue("optional") : null;
		return (optional != null && optional.equals("true")) ? true : false;
	}
	
	
	public boolean isNullable() 
	{
		JpaAsmAnntotationInfo columnInfo = getAnntotationInfo(Column.class);
		String nullable = (columnInfo != null) ? columnInfo.getValue("nullable").toString() : null;
		return (nullable != null && nullable.equals("true")) ? true : false;
	}

	public String getName()
	{
		return name;
	}
	
	
	public String getDesc()
	{
		return desc;
	}
	
	public String getDescType()
	{
		return getDesc().substring(1, getDesc().length()-1);
	}
	
	public String getDescSimpleType()
	{
		return getDescType().substring(getDescType().lastIndexOf('/')+1);
	}

	public String getSignature()
	{
		return signature;
	}
	
	public boolean isStatic() {
		return (this.access & Opcodes.ACC_STATIC) != 0;
	}

	public boolean isEnum() {
		return (this.access & Opcodes.ACC_ENUM) != 0;
	}
	
}
