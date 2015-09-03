package de.jdynameta.base.value;

public interface WrappedValueObject<TWrappedObject extends TypedValueObject> extends ChangeableTypedValueObject
{
    public TWrappedObject getWrappedValueObject();

}
