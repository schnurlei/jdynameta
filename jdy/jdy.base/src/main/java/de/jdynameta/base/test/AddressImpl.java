package de.jdynameta.base.test;

/**
 * AddressImpl
 *
 * @author Copyright &copy;
 * @author Rainer Schneider
 * @version
 */
public class AddressImpl extends de.jdynameta.base.value.defaultimpl.ReflectionValueObject
        implements de.jdynameta.base.test.Address

{
    private java.lang.String addressId;
    private java.lang.String street;
    private java.lang.String zipCode;
    private java.lang.String city;

    /**
     * Constructor
     */
    public AddressImpl()
    {
        super();
    }

    /**
     * Get the addressId
     *
     * @generated
     * @return get the addressId
     */
    @Override
    public String getAddressId()
    {
        return addressId;
    }

    /**
     * set the addressId
     *
     * @generated
     */
    @Override
    public void setAddressId(String aAddressId)
    {
        addressId = (aAddressId != null) ? aAddressId.trim() : null;
    }

    /**
     * Get the street
     *
     * @generated
     * @return get the street
     */
    @Override
    public String getStreet()
    {
        return street;
    }

    /**
     * set the street
     *
     * @generated
     */
    @Override
    public void setStreet(String aStreet)
    {
        street = (aStreet != null) ? aStreet.trim() : null;
    }

    /**
     * Get the zipCode
     *
     * @generated
     * @return get the zipCode
     */
    @Override
    public String getZipCode()
    {
        return zipCode;
    }

    /**
     * set the zipCode
     *
     * @generated
     */
    @Override
    public void setZipCode(String aZipCode)
    {
        zipCode = (aZipCode != null) ? aZipCode.trim() : null;
    }

    /**
     * Get the city
     *
     * @generated
     * @return get the city
     */
    @Override
    public String getCity()
    {
        return city;
    }

    /**
     * set the city
     *
     * @generated
     */
    @Override
    public void setCity(String aCity)
    {
        city = (aCity != null) ? aCity.trim() : null;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object
     */
    @Override
    public boolean equals(Object compareObj)
    {
        AddressImpl typeObj = (AddressImpl) compareObj;
        return typeObj != null
                && (((getAddressId() != null
                && typeObj.getAddressId() != null
                && this.getAddressId().equals(typeObj.getAddressId())))
                || (getAddressId() == null
                && typeObj.getAddressId() == null
                && this == typeObj));
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */

    @Override
    public int hashCode()
    {
        return ((addressId != null) ? addressId.hashCode() : super.hashCode());
    }

}
