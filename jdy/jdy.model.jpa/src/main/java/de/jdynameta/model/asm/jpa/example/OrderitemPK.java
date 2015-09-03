/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.jdynameta.model.asm.jpa.example;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author rainer
 */
@Embeddable
public class OrderitemPK implements Serializable {
	@Basic(optional = false)
    @Column(name = "ITEMNR")
	private int itemnr;
	@Basic(optional = false)
    @Column(name = "PLANTORDER_ORDERNR")
	private long plantorderOrdernr;

	public OrderitemPK() {
	}

	public OrderitemPK(int itemnr, long plantorderOrdernr) {
		this.itemnr = itemnr;
		this.plantorderOrdernr = plantorderOrdernr;
	}

	public int getItemnr() {
		return itemnr;
	}

	public void setItemnr(int itemnr) {
		this.itemnr = itemnr;
	}

	public long getPlantorderOrdernr() {
		return plantorderOrdernr;
	}

	public void setPlantorderOrdernr(long plantorderOrdernr) {
		this.plantorderOrdernr = plantorderOrdernr;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (int) itemnr;
		hash += (int) plantorderOrdernr;
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are not set
		if (!(object instanceof OrderitemPK)) {
			return false;
		}
		OrderitemPK other = (OrderitemPK) object;
		if (this.itemnr != other.itemnr) {
			return false;
		}
		if (this.plantorderOrdernr != other.plantorderOrdernr) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "de.jdynameta.model.asm.jpa.OrderitemPK[ itemnr=" + itemnr + ", plantorderOrdernr=" + plantorderOrdernr + " ]";
	}
	
}
