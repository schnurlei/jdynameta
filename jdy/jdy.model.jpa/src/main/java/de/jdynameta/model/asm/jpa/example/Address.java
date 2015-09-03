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
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author rainer
 */
@Entity(name="Address")
@Table(name = "ADDRESS")
public class Address implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
    @Basic(optional = false)
    @Column(name = "ADDRESSID")
	private String addressid;
	@Basic(optional = false)
    @Column(name = "STREET")
	private String street;
	@Basic(optional = false)
    @Column(name = "ZIPCODE")
	private String zipcode;
    @Column(name = "CITY", nullable = false, length = 100)
	private String city;
	@OneToMany(mappedBy = "invoiceaddressAddressid")
	private Collection<Customer> customerCollection;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "privateaddressAddressid")
	private Collection<Customer> customerCollection1;

	public Address() {
	}

	public Address(String addressid) {
		this.addressid = addressid;
	}

	public Address(String addressid, String street, String zipcode, String city) {
		this.addressid = addressid;
		this.street = street;
		this.zipcode = zipcode;
		this.city = city;
	}

	public String getAddressid() {
		return addressid;
	}

	public void setAddressid(String addressid) {
		this.addressid = addressid;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public Collection<Customer> getCustomerCollection() {
		return customerCollection;
	}

	public void setCustomerCollection(Collection<Customer> customerCollection) {
		this.customerCollection = customerCollection;
	}

	public Collection<Customer> getCustomerCollection1() {
		return customerCollection1;
	}

	public void setCustomerCollection1(Collection<Customer> customerCollection1) {
		this.customerCollection1 = customerCollection1;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (addressid != null ? addressid.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are not set
		if (!(object instanceof Address)) {
			return false;
		}
		Address other = (Address) object;
		if ((this.addressid == null && other.addressid != null) || (this.addressid != null && !this.addressid.equals(other.addressid))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "de.jdynameta.model.asm.jpa.Address[ addressid=" + addressid + " ]";
	}
	
}
