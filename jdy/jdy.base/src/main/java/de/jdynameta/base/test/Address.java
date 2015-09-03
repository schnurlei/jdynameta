package de.jdynameta.base.test;

/**
 * Address
 *
 * @author Copyright &copy;
 * @author Rainer Schneider
 * @version
 */
public interface Address extends de.jdynameta.base.value.ValueObject

{

    /**
     * Get the addressId
     *
     * @generated
     * @return get the addressId
     */
    public String getAddressId();

    /**
     * set the addressId
     *
     * @param aAddressId
     * @generated
#     * @param addressId
     */
    public void setAddressId(String aAddressId);

    /**
     * Get the street
     *
     * @generated
     * @return get the street
     */
    public String getStreet();

    /**
     * set the street
     *
     * @param aStreet
     * @generated
     */
    public void setStreet(String aStreet);

    /**
     * Get the zipCode
     *
     * @generated
     * @return get the zipCode
     */
    public String getZipCode();

    /**
     * set the zipCode
     *
     * @param aZipCode
     * @generated
     */
    public void setZipCode(String aZipCode);

    /**
     * Get the city
     *
     * @generated
     * @return get the city
     */
    public String getCity();

    /**
     * set the city
     *
     * @param aCity
     * @generated
     */
    public void setCity(String aCity);

}
