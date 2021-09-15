package com.sun.org.apache.xerces.internal.xs;

public interface XSModelGroupDefinition extends XSObject {
   XSModelGroup getModelGroup();

   XSAnnotation getAnnotation();

   XSObjectList getAnnotations();
}
