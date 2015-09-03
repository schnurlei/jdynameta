package de.jdynameta.base.test;

/**
 * CustomerImpl
 *
 * @author Copyright &copy;
 * @author Rainer Schneider
 * @version
 */
public class CustomerImpl extends de.jdynameta.base.value.defaultimpl.ReflectionValueObject
        implements de.jdynameta.base.test.Customer

{
    private java.lang.String customerId;
    private java.lang.String firstName;
    private java.lang.String middleName;
    private java.lang.String lastName;

    /**
     * Constructor
     */
    public CustomerImpl()
    {
        super();
    }

    /**
     * Get the customerId
     *
     * @generated
     * @return get the customerId
     */
    @Override
    public String getCustomerId()
    {
        return customerId;
    }

    /**
     * set the customerId
     *
     * @generated
     */
    @Override
    public void setCustomerId(String aCustomerId)
    {
        customerId = (aCustomerId != null) ? aCustomerId.trim() : null;
    }

    /**
     * Get the firstName
     *
     * @generated
     * @return get the firstName
     */
    @Override
    public String getFirstName()
    {
        return firstName;
    }

    /**
     * set the firstName
     *
     * @generated
     */
    @Override
    public void setFirstName(String aFirstName)
    {
        firstName = (aFirstName != null) ? aFirstName.trim() : null;
    }

    /**
     * Get the middleName
     *
     * @generated
     * @return get the middleName
     */
    @Override
    public String getMiddleName()
    {
        return middleName;
    }

    /**
     * set the middleName
     *
     * @generated
     */
    @Override
    public void setMiddleName(String aMiddleName)
    {
        middleName = (aMiddleName != null) ? aMiddleName.trim() : null;
    }

    /**
     * Get the lastName
     *
     * @generated
     * @return get the lastName
     */
    @Override
    public String getLastName()
    {
        return lastName;
    }

    /**
     * set the lastName
     *
     * @generated
     */
    @Override
    public void setLastName(String aLastName)
    {
        lastName = (aLastName != null) ? aLastName.trim() : null;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object
     */
    @Override
    public boolean equals(Object compareObj)
    {
        CustomerImpl typeObj = (CustomerImpl) compareObj;
        return typeObj != null
                && (((getCustomerId() != null
                && typeObj.getCustomerId() != null
                && this.getCustomerId().equals(typeObj.getCustomerId())))
                || (getCustomerId() == null
                && typeObj.getCustomerId() == null
                && this == typeObj));
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */

    @Override
    public int hashCode()
    {
        return ((customerId != null) ? customerId.hashCode() : super.hashCode());
    }

}
