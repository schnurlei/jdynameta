package de.jdynameta.base.test;

import java.util.Date;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.objectlist.DefaultObjectList;

/**
 * PlantOrderImpl
 *
 * @author Copyright &copy;
 * @author Rainer Schneider
 * @version
 */
public class PlantOrderImpl extends de.jdynameta.base.value.defaultimpl.ReflectionValueObject
        implements de.jdynameta.base.test.PlantOrder

{
    private ObjectList itemsColl = new DefaultObjectList();
    private java.lang.Long orderNr;
    private java.util.Date orderDate;

    /**
     * Constructor
     */
    public PlantOrderImpl()
    {
        super();
    }

    /**
     * Get the orderNr
     *
     * @generated
     * @return get the orderNr
     */
    @Override
    public Long getOrderNr()
    {
        return orderNr;
    }

    /**
     * set the orderNr
     *
     * @generated
     */
    @Override
    public void setOrderNr(Long aOrderNr)
    {
        orderNr = aOrderNr;
    }

    /**
     * Get the orderDate
     *
     * @generated
     * @return get the orderDate
     */
    @Override
    public Date getOrderDate()
    {
        return orderDate;
    }

    /**
     * set the orderDate
     *
     * @generated
     */
    @Override
    public void setOrderDate(Date aOrderDate)
    {
        orderDate = aOrderDate;
    }

    /**
     * Get all OrderItem
     *
     * @return get the Collection ofOrderItem
     */
    @Override
    public de.jdynameta.base.objectlist.ObjectList getItemsColl()
    {
        return itemsColl;
    }

    /**
     * Set aCollection of all OrderItem
     *
     */
    @Override
    public void setItemsColl(ObjectList aOrderItemColl)
    {
        itemsColl = aOrderItemColl;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object
     */
    @Override
    public boolean equals(Object compareObj)
    {
        PlantOrderImpl typeObj = (PlantOrderImpl) compareObj;
        return typeObj != null
                && (((getOrderNr() != null
                && typeObj.getOrderNr() != null
                && this.getOrderNr().equals(typeObj.getOrderNr())))
                || (getOrderNr() == null
                && typeObj.getOrderNr() == null
                && this == typeObj));
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */

    @Override
    public int hashCode()
    {
        return ((orderNr != null) ? orderNr.hashCode() : super.hashCode());
    }

}
