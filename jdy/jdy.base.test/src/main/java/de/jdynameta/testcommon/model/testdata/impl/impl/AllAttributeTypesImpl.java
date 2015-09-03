/**
 *
 * Copyright 2011 (C) Rainer Schneider,Roggenburg <schnurlei@googlemail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package de.jdynameta.testcommon.model.testdata.impl.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import de.jdynameta.base.metainfo.primitive.BlobByteArrayHolder;

/**
 * AllAttributeTypesImpl
 *
 * @author Copyright &copy;
 * @author Rainer Schneider
 * @version
 */
public class AllAttributeTypesImpl extends de.jdynameta.base.value.defaultimpl.ReflectionValueObject

{
    private java.lang.Long integerData;
    private java.lang.Boolean booleanData;
    private de.jdynameta.base.metainfo.primitive.BlobByteArrayHolder blobData;
    private java.lang.String clobData;
    private java.math.BigDecimal currencyData;
    private Date dateData;
    private java.lang.Double floatData;
    private java.lang.Long longData;
    private java.lang.String textData;
    private java.sql.Timestamp timestampData;
    private java.sql.Timestamp timeData;
    private java.lang.String varCharData;

    /**
     * Constructor
     */
    public AllAttributeTypesImpl()
    {
        super(new de.jdynameta.base.value.DefaultClassNameCreator());
    }

    /**
     * Get the integerData
     *
     * @generated
     * @return get the integerData
     */
    public Long getIntegerData()
    {
        return integerData;
    }

    /**
     * set the integerData
     *
     * @param aIntegerData
     * @generated
     */
    public void setIntegerData(Long aIntegerData)
    {
        integerData = aIntegerData;
    }

    /**
     * Get the booleanData
     *
     * @generated
     * @return get the booleanData
     */
    public Boolean getBooleanData()
    {
        return booleanData;
    }

    /**
     * set the booleanData
     *
     * @param aBooleanData
     * @generated
     */
    public void setBooleanData(Boolean aBooleanData)
    {
        booleanData = aBooleanData;
    }

    /**
     * Get the blobData
     *
     * @generated
     * @return get the blobData
     */
    public BlobByteArrayHolder getBlobData()
    {
        return blobData;
    }

    /**
     * set the blobData
     *
     * @generated
     * @param blobData
     */
    public void setBlobData(BlobByteArrayHolder aBlobData)
    {
        blobData = aBlobData;
    }

    /**
     * Get the clobData
     *
     * @generated
     * @return get the clobData
     */
    public String getClobData()
    {
        return clobData;
    }

    /**
     * set the clobData
     *
     * @generated
     * @param clobData
     */
    public void setClobData(String aClobData)
    {
        clobData = (aClobData != null) ? aClobData.trim() : null;
    }

    /**
     * Get the currencyData
     *
     * @generated
     * @return get the currencyData
     */
    public BigDecimal getCurrencyData()
    {
        return currencyData;
    }

    /**
     * set the currencyData
     *
     * @generated
     * @param currencyData
     */
    public void setCurrencyData(BigDecimal aCurrencyData)
    {
        currencyData = aCurrencyData;
    }

    /**
     * Get the dateData
     *
     * @generated
     * @return get the dateData
     */
    public Date getDateData()
    {
        return dateData;
    }

    /**
     * set the dateData
     *
     * @generated
     * @param dateData
     */
    public void setDateData(Date aDateData)
    {
        dateData = aDateData;
    }

    /**
     * Get the floatData
     *
     * @generated
     * @return get the floatData
     */
    public Double getFloatData()
    {
        return floatData;
    }

    /**
     * set the floatData
     *
     * @generated
     * @param floatData
     */
    public void setFloatData(Double aFloatData)
    {
        floatData = aFloatData;
    }

    /**
     * Get the longData
     *
     * @generated
     * @return get the longData
     */
    public Long getLongData()
    {
        return longData;
    }

    /**
     * set the longData
     *
     * @generated
     * @param longData
     */
    public void setLongData(Long aLongData)
    {
        longData = aLongData;
    }

    /**
     * Get the textData
     *
     * @generated
     * @return get the textData
     */
    public String getTextData()
    {
        return textData;
    }

    /**
     * set the textData
     *
     * @generated
     * @param textData
     */
    public void setTextData(String aTextData)
    {
        textData = (aTextData != null) ? aTextData.trim() : null;
    }

    /**
     * Get the timestampData
     *
     * @generated
     * @return get the timestampData
     */
    public Timestamp getTimestampData()
    {
        return timestampData;
    }

    /**
     * set the timestampData
     *
     * @generated
     * @param timestampData
     */
    public void setTimestampData(Timestamp aTimestampData)
    {
        timestampData = aTimestampData;
    }

    /**
     * Get the timeData
     *
     * @generated
     * @return get the timeData
     */
    public Timestamp getTimeData()
    {
        return timeData;
    }

    /**
     * set the timeData
     *
     * @generated
     * @param timeData
     */
    public void setTimeData(Timestamp aTimeData)
    {
        timeData = aTimeData;
    }

    /**
     * Get the varCharData
     *
     * @generated
     * @return get the varCharData
     */
    public String getVarCharData()
    {
        return varCharData;
    }

    /**
     * set the varCharData
     *
     * @generated
     * @param varCharData
     */
    public void setVarCharData(String aVarCharData)
    {
        varCharData = (aVarCharData != null) ? aVarCharData.trim() : null;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object
     */
    @Override
    public boolean equals(Object compareObj)
    {
        AllAttributeTypesImpl typeObj = (AllAttributeTypesImpl) compareObj;
        return typeObj != null
                && (((getIntegerData() != null
                && typeObj.getIntegerData() != null
                && this.getIntegerData().equals(typeObj.getIntegerData())))
                || (getIntegerData() == null
                && typeObj.getIntegerData() == null
                && this == typeObj));
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */

    @Override
    public int hashCode()
    {
        return ((integerData != null) ? integerData.hashCode() : super.hashCode());
    }

}
