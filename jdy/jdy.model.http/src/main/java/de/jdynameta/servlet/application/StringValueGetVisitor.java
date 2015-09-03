package de.jdynameta.servlet.application;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.databind.util.ISO8601Utils;

import de.jdynameta.base.metainfo.primitive.BlobByteArrayHolder;
import de.jdynameta.base.metainfo.primitive.BlobType;
import de.jdynameta.base.metainfo.primitive.BooleanType;
import de.jdynameta.base.metainfo.primitive.CurrencyType;
import de.jdynameta.base.metainfo.primitive.FloatType;
import de.jdynameta.base.metainfo.primitive.LongType;
import de.jdynameta.base.metainfo.primitive.PrimitiveTypeGetVisitor;
import de.jdynameta.base.metainfo.primitive.TextType;
import de.jdynameta.base.metainfo.primitive.TimeStampType;
import de.jdynameta.base.metainfo.primitive.VarCharType;
import de.jdynameta.base.value.JdyPersistentException;

public class StringValueGetVisitor implements PrimitiveTypeGetVisitor
{
    private final String attrValue;

    public StringValueGetVisitor(String anAttrValue)
    {
        super();
        this.attrValue = anAttrValue;
    }

    @Override
    public Boolean handleValue(BooleanType aType) throws JdyPersistentException
    {
        return Boolean.valueOf(attrValue);
    }

    @Override
    public BigDecimal handleValue(CurrencyType aType)
            throws JdyPersistentException
    {
        return new BigDecimal(attrValue);
    }

    @Override
    public Date handleValue(TimeStampType aType) throws JdyPersistentException
    {
        System.out.println(attrValue);
        return ISO8601Utils.parse(attrValue);
    }

    @Override
    public Double handleValue(FloatType aType) throws JdyPersistentException
    {
        return new Double(attrValue);
    }

    @Override
    public Long handleValue(LongType aType) throws JdyPersistentException
    {
        return new Long(attrValue);
    }
    
    @Override
    public String handleValue(TextType aType) throws JdyPersistentException
    {
        return attrValue;
    }

    @Override
    public String handleValue(VarCharType aType) throws JdyPersistentException
    {
        return attrValue;
    }

    @Override
    public BlobByteArrayHolder handleValue(BlobType aType) throws JdyPersistentException
    {
        // TODO Auto-generated method stub
        return null;
    }
}
