package de.jdynameta.base.objectlist;

public interface EditableObjectList<TListType> extends ObjectList<TListType>
{
	public void addObject(TListType anObject);
	
	public void removeObject(TListType anObject);

}
