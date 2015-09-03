package de.jdynameta.base.test;

/**
 * PlantImpl
 *
 * @author Copyright &copy;
 * @author Rainer Schneider
 * @version
 */
public class PlantImpl extends de.jdynameta.base.value.defaultimpl.ReflectionValueObject
        implements de.jdynameta.base.test.Plant

{
    private java.lang.String botanicName;
    private java.lang.Long heigthInCm;
    private java.lang.String plantFamily;
    private java.lang.String color;

    /**
     * Constructor
     */
    public PlantImpl()
    {
        super();
    }

    /**
     * Get the botanicName
     *
     * @generated
     * @return get the botanicName
     */
    @Override
    public String getBotanicName()
    {
        return botanicName;
    }

    /**
     * set the botanicName
     *
     * @generated
     */
    @Override
    public void setBotanicName(String aBotanicName)
    {
        botanicName = (aBotanicName != null) ? aBotanicName.trim() : null;
    }

    /**
     * Get the heigthInCm
     *
     * @generated
     * @return get the heigthInCm
     */
    @Override
    public Long getHeigthInCm()
    {
        return heigthInCm;
    }

    /**
     * set the heigthInCm
     *
     * @generated
     */
    @Override
    public void setHeigthInCm(Long aHeigthInCm)
    {
        heigthInCm = aHeigthInCm;
    }

    /**
     * Get the plantFamily
     *
     * @generated
     * @return get the plantFamily
     */
    @Override
    public String getPlantFamily()
    {
        return plantFamily;
    }

    /**
     * set the plantFamily
     *
     * @generated
     */
    @Override
    public void setPlantFamily(String aPlantFamily)
    {
        plantFamily = (aPlantFamily != null) ? aPlantFamily.trim() : null;
    }

    /**
     * Get the color
     *
     * @generated
     * @return get the color
     */
    @Override
    public String getColor()
    {
        return color;
    }

    /**
     * set the color
     *
     * @generated
     */
    @Override
    public void setColor(String aColor)
    {
        color = (aColor != null) ? aColor.trim() : null;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object
     */
    @Override
    public boolean equals(Object compareObj)
    {
        PlantImpl typeObj = (PlantImpl) compareObj;
        return typeObj != null
                && (((getBotanicName() != null
                && typeObj.getBotanicName() != null
                && this.getBotanicName().equals(typeObj.getBotanicName())))
                || (getBotanicName() == null
                && typeObj.getBotanicName() == null
                && this == typeObj));
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */

    @Override
    public int hashCode()
    {
        return ((botanicName != null) ? botanicName.hashCode() : super.hashCode());
    }

}
