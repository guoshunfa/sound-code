package com.sun.org.apache.xerces.internal.xs;

public interface XSNotationDeclaration extends XSObject {
   String getSystemId();

   String getPublicId();

   XSAnnotation getAnnotation();

   XSObjectList getAnnotations();
}
