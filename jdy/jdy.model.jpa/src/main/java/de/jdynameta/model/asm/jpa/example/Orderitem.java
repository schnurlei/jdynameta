/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.jdynameta.model.asm.jpa.example;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author rainer
 */
@Entity
@Table(name = "ORDERITEM")
public class Orderitem implements Serializable {
	private static final long serialVersionUID = 1L;
	@EmbeddedId
	protected OrderitemPK orderitemPK;
	@Basic(optional = false)
    @Column(name = "ITEMCOUNT")
	private int itemcount;
	// @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
	@Basic(optional = false)
    @Column(name = "PRICE")
	private BigDecimal price;
	@JoinColumn(name = "PLANTORDER_ORDERNR", referencedColumnName = "ORDERNR", insertable = false, updatable = false)
    @ManyToOne(optional = false)
	private Plantorder plantorder;
	@JoinColumn(name = "PLANT_BOTANICNAME", referencedColumnName = "BOTANICNAME")
    @ManyToOne(optional = false)
	private Plant plantBotanicname;

	public Orderitem() {
	}

	public Orderitem(OrderitemPK orderitemPK) {
		this.orderitemPK = orderitemPK;
	}

	public Orderitem(OrderitemPK orderitemPK, int itemcount, BigDecimal price) {
		this.orderitemPK = orderitemPK;
		this.itemcount = itemcount;
		this.price = price;
	}

	public Orderitem(int itemnr, long plantorderOrdernr) {
		this.orderitemPK = new OrderitemPK(itemnr, plantorderOrdernr);
	}

	public OrderitemPK getOrderitemPK() {
		return orderitemPK;
	}

	public void setOrderitemPK(OrderitemPK orderitemPK) {
		this.orderitemPK = orderitemPK;
	}

	public int getItemcount() {
		return itemcount;
	}

	public void setItemcount(int itemcount) {
		this.itemcount = itemcount;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Plantorder getPlantorder() {
		return plantorder;
	}

	public void setPlantorder(Plantorder plantorder) {
		this.plantorder = plantorder;
	}

	public Plant getPlantBotanicname() {
		return plantBotanicname;
	}

	public void setPlantBotanicname(Plant plantBotanicname) {
		this.plantBotanicname = plantBotanicname;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (orderitemPK != null ? orderitemPK.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are not set
		if (!(object instanceof Orderitem)) {
			return false;
		}
		Orderitem other = (Orderitem) object;
		if ((this.orderitemPK == null && other.orderitemPK != null) || (this.orderitemPK != null && !this.orderitemPK.equals(other.orderitemPK))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "de.jdynameta.model.asm.jpa.Orderitem[ orderitemPK=" + orderitemPK + " ]";
	}
	
}
