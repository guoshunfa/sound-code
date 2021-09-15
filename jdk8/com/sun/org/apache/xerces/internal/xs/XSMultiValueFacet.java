package com.sun.org.apache.xerces.internal.xs;

public interface XSMultiValueFacet extends XSObject {
   short getFacetKind();

   StringList getLexicalFacetValues();

   XSObjectList getAnnotations();
}
