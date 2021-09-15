package com.sun.org.apache.xerces.internal.xs;

public interface XSAttributeGroupDefinition extends XSObject {
   XSObjectList getAttributeUses();

   XSWildcard getAttributeWildcard();

   XSAnnotation getAnnotation();

   XSObjectList getAnnotations();
}
