package com.sun.org.apache.xerces.internal.xs;

public interface XSAttributeUse extends XSObject {
   boolean getRequired();

   XSAttributeDeclaration getAttrDeclaration();

   short getConstraintType();

   String getConstraintValue();

   Object getActualVC() throws XSException;

   short getActualVCType() throws XSException;

   ShortList getItemValueTypes() throws XSException;

   XSObjectList getAnnotations();
}
