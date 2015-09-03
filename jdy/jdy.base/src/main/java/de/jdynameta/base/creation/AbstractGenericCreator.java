package de.jdynameta.base.creation;

import de.jdynameta.base.metainfo.AssociationInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ObjectReferenceAttributeInfo;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.objectlist.ChangeableObjectList;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.value.ChangeableTypedValueObject;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ObjectCreationException;
import de.jdynameta.base.value.TypedValueObject;
import de.jdynameta.base.value.defaultimpl.TypedHashedValueObject;

@SuppressWarnings("serial")
public abstract class AbstractGenericCreator extends AbstractObjectCreator<ChangeableTypedValueObject> {

    public AbstractGenericCreator() {
        super();
    }

    @Override
    public ChangeableTypedValueObject createNewObjectFor(ClassInfo aClassinfo)
            throws ObjectCreationException {
        return new TypedHashedValueObject(aClassinfo);
    }

    @Override
    protected void setListForAssoc(AssociationInfo aAssocInfo, ChangeableTypedValueObject aObjoSetVals, TypedValueObject aObjToGetVals)
            throws ObjectCreationException {
        ObjectList<? extends TypedValueObject> aListToGet = (ObjectList<? extends TypedValueObject>) aObjToGetVals.getValue(aAssocInfo);
        ChangeableObjectList<ChangeableTypedValueObject> newObjList = new ChangeableObjectList<>();
        for (TypedValueObject tCreatedObjFromValueObj : aListToGet) {
            newObjList.addObject(createObjectFor(tCreatedObjFromValueObj));
        }
        aObjoSetVals.setValue(aAssocInfo, newObjList);

    }

    @Override
    protected void setEmptyList(AssociationInfo aAssocInfo, ChangeableTypedValueObject aObjoSetVals) throws ObjectCreationException {
        ChangeableObjectList<ChangeableTypedValueObject> newObjList = new ChangeableObjectList<>();
        aObjoSetVals.setValue(aAssocInfo, newObjList);
    }

    @Override
    protected void setObjectReferenceVal(ObjectReferenceAttributeInfo aRefInfo,
            ChangeableTypedValueObject aObjoSetVals, TypedValueObject aObjToHandle)
            throws JdyPersistentException, ObjectCreationException {
        ChangeableTypedValueObject newObject = null;
        if (aObjToHandle != null) {
            if (areAllValuesSetForClassInfo(aObjToHandle)) {
                newObject = createObjectFor(aObjToHandle);
            } else {
                newObject = createProxyObjectFor(aObjToHandle);
            }
        }
        aObjoSetVals.setValue(aRefInfo, newObject);
    }

    protected abstract ChangeableTypedValueObject createProxyObjectFor(TypedValueObject aObjToHandle);

    @Override
    protected void setPrimitiveVal(PrimitiveAttributeInfo aInfo, ChangeableTypedValueObject aObjoSetVals, Object aValueToHandle)
            throws JdyPersistentException {
        aObjoSetVals.setValue(aInfo, aValueToHandle);
    }

}
