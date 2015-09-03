package de.jdynameta.base.test;

import java.math.BigDecimal;
import java.lang.Long;

/**
 * OrderItem
 *
 * @author Copyright &copy;
 * @author Rainer Schneider
 * @version
 */
public interface OrderItem extends de.jdynameta.base.value.ValueObject

{

    /**
     * Get the itemNr
     *
     * @generated
     * @return get the itemNr
     */
    public Long getItemNr();

    /**
     * set the itemNr
     *
     * @param aItemNr
     * @generated
     */
    public void setItemNr(Long aItemNr);

    /**
     * Get the count
     *
     * @generated
     * @return get the count
     */
    public Long getCount();

    /**
     * set the count
     *
     * @param aCount
     * @generated
     */
    public void setCount(Long aCount);

    /**
     * Get the price
     *
     * @generated
     * @return get the price
     */
    public BigDecimal getPrice();

    /**
     * set the price
     *
     * @param aPrice
     * @generated
     */
    public void setPrice(BigDecimal aPrice);

    /**
     * Get the plant
     *
     * @generated
     * @return get the plant
     */
    public Plant getPlant();

    /**
     * set the plant
     *
     * @param aPlant
     * @generated
     */
    public void setPlant(Plant aPlant);

    /**
     * Get the privateAddress
     *
     * @generated
     * @return get the privateAddress
     */
    public Address getPrivateAddress();

    /**
     * set the privateAddress
     *
     * @param aPrivateAddress
     * @generated
     */
    public void setPrivateAddress(Address aPrivateAddress);

    /**
     * Get the invoiceAddress
     *
     * @generated
     * @return get the invoiceAddress
     */
    public Address getInvoiceAddress();

    /**
     * set the invoiceAddress
     *
     * @param aInvoiceAddress
     * @generated
     */
    public void setInvoiceAddress(Address aInvoiceAddress);

    /**
     * Get the plantOrder
     *
     * @generated
     * @return get the plantOrder
     */
    public PlantOrder getPlantOrder();

    /**
     * set the plantOrder
     *
     * @param aPlantOrder
     * @generated
     */
    public void setPlantOrder(PlantOrder aPlantOrder);

}
