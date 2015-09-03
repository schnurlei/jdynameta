package de.jdynameta.base.test;

import java.math.BigDecimal;
import java.lang.Long;

/**
 * OrderItemImpl
 *
 * @author Copyright &copy;
 * @author Rainer Schneider
 * @version
 */
public class OrderItemImpl extends de.jdynameta.base.value.defaultimpl.ReflectionValueObject
        implements de.jdynameta.base.test.OrderItem

{
    private java.lang.Long itemNr;
    private java.lang.Long count;
    private java.math.BigDecimal price;
    private Plant plant;
    private Address privateAddress;
    private Address invoiceAddress;
    private PlantOrder plantOrder;

    /**
     * Constructor
     */
    public OrderItemImpl()
    {
        super();
    }

    /**
     * Get the itemNr
     *
     * @generated
     * @return get the itemNr
     */
    @Override
    public Long getItemNr()
    {
        return itemNr;
    }

    /**
     * set the itemNr
     *
     * @generated
     */
    @Override
    public void setItemNr(Long aItemNr)
    {
        itemNr = aItemNr;
    }

    /**
     * Get the count
     *
     * @generated
     * @return get the count
     */
    @Override
    public Long getCount()
    {
        return count;
    }

    /**
     * set the count
     *
     * @generated
     */
    @Override
    public void setCount(Long aCount)
    {
        count = aCount;
    }

    /**
     * Get the price
     *
     * @generated
     * @return get the price
     */
    @Override
    public BigDecimal getPrice()
    {
        return price;
    }

    /**
     * set the price
     *
     * @generated
     */
    @Override
    public void setPrice(BigDecimal aPrice)
    {
        price = aPrice;
    }

    /**
     * Get the plant
     *
     * @generated
     * @return get the plant
     */
    @Override
    public Plant getPlant()
    {
        return plant;
    }

    /**
     * set the plant
     *
     * @generated
     */
    @Override
    public void setPlant(Plant aPlant)
    {
        plant = aPlant;
    }

    /**
     * Get the privateAddress
     *
     * @generated
     * @return get the privateAddress
     */
    @Override
    public Address getPrivateAddress()
    {
        return privateAddress;
    }

    /**
     * set the privateAddress
     *
     * @generated
     */
    @Override
    public void setPrivateAddress(Address aPrivateAddress)
    {
        privateAddress = aPrivateAddress;
    }

    /**
     * Get the invoiceAddress
     *
     * @generated
     * @return get the invoiceAddress
     */
    @Override
    public Address getInvoiceAddress()
    {
        return invoiceAddress;
    }

    /**
     * set the invoiceAddress
     *
     * @generated
     */
    @Override
    public void setInvoiceAddress(Address aInvoiceAddress)
    {
        invoiceAddress = aInvoiceAddress;
    }

    /**
     * Get the plantOrder
     *
     * @generated
     * @return get the plantOrder
     */
    @Override
    public PlantOrder getPlantOrder()
    {
        return plantOrder;
    }

    /**
     * set the plantOrder
     *
     * @generated
     */
    @Override
    public void setPlantOrder(PlantOrder aPlantOrder)
    {
        plantOrder = aPlantOrder;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object
     */
    @Override
    public boolean equals(Object compareObj)
    {
        OrderItemImpl typeObj = (OrderItemImpl) compareObj;
        return typeObj != null
                && (((getItemNr() != null
                && typeObj.getItemNr() != null
                && this.getItemNr().equals(typeObj.getItemNr()))
                && (getPlantOrder() != null
                && typeObj.getPlantOrder() != null
                && typeObj.getPlantOrder().equals(typeObj.getPlantOrder())))
                || (getItemNr() == null
                && typeObj.getItemNr() == null
                && getPlantOrder() == null
                && typeObj.getPlantOrder() == null
                && this == typeObj));
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */

    @Override
    public int hashCode()
    {
        return ((itemNr != null) ? itemNr.hashCode() : super.hashCode())
                ^ ((plantOrder != null) ? plantOrder.hashCode() : super.hashCode());
    }

}
