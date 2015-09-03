package de.jdynameta.servlet.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.jdynameta.base.metainfo.AttributeHandler;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ObjectReferenceAttributeInfo;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.base.value.defaultimpl.TypedHashedValueObject;

public class RequestParameterHandler implements AttributeHandler
{
    private final Map paraMap;
    TypedHashedValueObject result;
    private final List<AttributeInfo> aspectPath;

    public RequestParameterHandler(Map aRequestParaMap, ClassInfo aConcreteClass, List<AttributeInfo> anAspectPath)
    {
        super();
        this.result = new TypedHashedValueObject();
        this.result.setClassInfo(aConcreteClass);
        this.paraMap = aRequestParaMap;
        this.aspectPath = anAspectPath;
    }

    @Override
    public void handleObjectReference(ObjectReferenceAttributeInfo aInfo, ValueObject objToHandle)
            throws JdyPersistentException
    {

        if (aInfo.isKey())
        {
            List<AttributeInfo> refAspectPath = new ArrayList<>();
            if (this.aspectPath != null)
            {
                refAspectPath.addAll(this.aspectPath);
            }
            refAspectPath.add(aInfo);
            RequestParameterHandler refHandler = new RequestParameterHandler(paraMap, aInfo.getReferencedClass(), refAspectPath);
            aInfo.getReferencedClass().handleAttributes(refHandler, null);
            result.setValue(aInfo, refHandler.result);
        }
    }

    @Override
    public void handlePrimitiveAttribute(PrimitiveAttributeInfo aInfo, Object objToHandle)
            throws JdyPersistentException
    {
        String parameterName = createParameterName(this.aspectPath, aInfo);
        if (aInfo.isKey())
        {
            String[] values = (String[]) this.paraMap.get(parameterName);

            if (values == null || values.length == 0)
            {
                throw new JdyPersistentException("Missing value for type in attr value: " + aInfo.getInternalName());
            }
            if (values[0] == null || values[0].trim().length() == 0)
            {
                result.setValue(aInfo, null);
            } else
            {
                this.result.setValue(aInfo, aInfo.getType().handlePrimitiveKey(new StringValueGetVisitor(values[0])));

            }
        }
    }

    private String createParameterName(List<AttributeInfo> aAspectPath, PrimitiveAttributeInfo aInfo)
    {
        String result = "";
        if (this.aspectPath != null)
        {
            for (AttributeInfo attributeInfo : aAspectPath)
            {
                result += attributeInfo.getInternalName() + ".";
            }
        }

        result += aInfo.getInternalName();
        return result;
    }
}
