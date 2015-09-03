package de.jdynameta.base.metainfo.util;

import de.jdynameta.base.metainfo.AssociationInfo;
import de.jdynameta.base.metainfo.AttributeInfo;

public class AttributeVisibilityAdaptor implements AttributeVisibility
{
    @Override
    public boolean isAttributeVisible(AttributeInfo aAttrInfo)
    {
        return true;
    }

    @Override
    public boolean isAssociationVisible(AssociationInfo aAttrInfo)
    {
        return true;
    }
}
