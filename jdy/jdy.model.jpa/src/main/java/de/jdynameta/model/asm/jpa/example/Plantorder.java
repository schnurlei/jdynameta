/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.jdynameta.model.asm.jpa.example;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author rainer
 */
@Entity
@Table(name = "PLANTORDER")
public class Plantorder implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
    @Basic(optional = false)
    @Column(name = "ORDERNR")
	private Long ordernr;
	@Basic(optional = false)
    @Column(name = "ORDERDATE")
    @Temporal(TemporalType.TIMESTAMP)
	private Date orderdate;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "plantorder")
	private Collection<Orderitem> orderitemCollection;

	public Plantorder() {
	}

	public Plantorder(Long ordernr) {
		this.ordernr = ordernr;
	}

	public Plantorder(Long ordernr, Date orderdate) {
		this.ordernr = ordernr;
		this.orderdate = orderdate;
	}

	public Long getOrdernr() {
		return ordernr;
	}

	public void setOrdernr(Long ordernr) {
		this.ordernr = ordernr;
	}

	public Date getOrderdate() {
		return orderdate;
	}

	public void setOrderdate(Date orderdate) {
		this.orderdate = orderdate;
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
		hash += (ordernr != null ? ordernr.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are not set
		if (!(object instanceof Plantorder)) {
			return false;
		}
		Plantorder other = (Plantorder) object;
		if ((this.ordernr == null && other.ordernr != null) || (this.ordernr != null && !this.ordernr.equals(other.ordernr))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "de.jdynameta.model.asm.jpa.Plantorder[ ordernr=" + ordernr + " ]";
	}
	
}
