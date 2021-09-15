package com.sun.org.apache.xerces.internal.xs;

public interface XSFacet extends XSObject {
   short getFacetKind();

   String getLexicalFacetValue();

   boolean getFixed();

   XSAnnotation getAnnotation();

   XSObjectList getAnnotations();
}
