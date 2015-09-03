/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.jdynameta.model.asm.jpa.example;

import java.io.Serializable;
import java.util.Collection;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import de.jdynameta.base.view.DbDomainValue;

/**
 *
 * @author rainer
 */
@Entity
@Table(name = "PLANT")
public class Plant implements Serializable {
	
	public enum PlantFamily implements DbDomainValue<String>
	{
		Iridaceae("Iridaceae", "Iridaceae"), Malvaceae("Malvaceae", "Malvaceae"), Geraniaceae("Geraniaceae", "Geraniaceae");

		private final String	domValue;
		private final String	representation;

		private PlantFamily(String domValue, String representation)
		{
			this.domValue = domValue;
			this.representation = representation;
		}

		@Override
		public String getDbValue()
		{
			return domValue;
		}

		@Override
		public String getRepresentation()
		{
			return representation;
		}
	}
	
	private static final long serialVersionUID = 1L;
	@Id
    @Basic(optional = false)
    @Column(name = "BOTANICNAME")
	private String botanicname;
	@Column(name = "HEIGTHINCM")
	private Integer heigthincm;
	@Column(name = "PLANTFAMILY")
	@Enumerated
	private PlantFamily plantfamily;
	@Column(name = "COLOR")
	private String color;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "plantBotanicname")
	private Collection<Orderitem> orderitemCollection;

	public Plant() {
	}

	public Plant(String botanicname) {
		this.botanicname = botanicname;
	}

	public String getBotanicname() {
		return botanicname;
	}

	public void setBotanicname(String botanicname) {
		this.botanicname = botanicname;
	}

	public Integer getHeigthincm() {
		return heigthincm;
	}

	public void setHeigthincm(Integer heigthincm) {
		this.heigthincm = heigthincm;
	}

	public PlantFamily getPlantfamily() {
		return plantfamily;
	}

	public void setPlantfamily(PlantFamily plantfamily) {
		this.plantfamily = plantfamily;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public Collection<Orderitem> getOrderitemCollection() {
		return orderitemCollection;
	}

	public void setOrderitemCollection(Collection<Orderitem> orderitemCollection) {
		this.orderitemCollection = orderitemCollection;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (botanicname != null ? botanicname.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are not set
		if (!(object instanceof Plant)) {
			return false;
		}
		Plant other = (Plant) object;
		if ((this.botanicname == null && other.botanicname != null) || (this.botanicname != null && !this.botanicname.equals(other.botanicname))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "de.jdynameta.model.asm.jpa.Plant[ botanicname=" + botanicname + " ]";
	}
	
}
