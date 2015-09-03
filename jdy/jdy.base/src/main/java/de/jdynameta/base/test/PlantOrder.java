package de.jdynameta.base.test;

import java.util.Date;
import de.jdynameta.base.objectlist.ObjectList;

/**
 * PlantOrder
 *
 * @author Copyright &copy;
 * @author Rainer Schneider
 * @version
 */
public interface PlantOrder extends de.jdynameta.base.value.ValueObject
{

    /**
     * Get the orderNr
     *
     * @generated
     * @return get the orderNr
     */
    public Long getOrderNr();

    /**
     * set the orderNr
     *
     * @param aOrderNr
     * @generated
     */
    public void setOrderNr(Long aOrderNr);

    /**
     * Get the orderDate
     *
     * @generated
     * @return get the orderDate
     */
    public Date getOrderDate();

    /**
     * set the orderDate
     *
     * @param aOrderDate
     * @generated
     */
    public void setOrderDate(Date aOrderDate);

    /**
     * Get all ItemsColl
     *
     * @generated
     * @return get the Collection ofItemsColl
     */
    public ObjectList getItemsColl();

    /**
     * Set aCollection of all ItemsColl
     *
     * @param aItemsCollColl
     * @generated
     */
    public void setItemsColl(ObjectList aItemsCollColl);

}
