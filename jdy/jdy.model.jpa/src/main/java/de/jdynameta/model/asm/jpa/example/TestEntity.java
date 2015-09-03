/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.jdynameta.model.asm.jpa.example;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author rainer
 */
@Entity(name="NAME_TestEntity")
@Table(name="tbl_TestEntity")
public class TestEntity implements Serializable
{
	private static final long serialVersionUID = 1L;
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Enumerated
	private TestEnum enumValue;

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public TestEnum getEnumValue()
	{
		return enumValue;
	}

	public void setEnumValue(TestEnum enumValue)
	{
		this.enumValue = enumValue;
	}

	@Override
	public int hashCode()
	{
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object)
	{
		// TODO: Warning - this method won't work in the case the id fields are not set
		if (!(object instanceof TestEntity)) {
			return false;
		}
		TestEntity other = (TestEntity) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString()
	{
		return "de.jdynameta.jdymodel.jpa.TestEntity[ id=" + id + " ]";
	}
	
}
