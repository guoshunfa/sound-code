package com.sun.org.apache.xerces.internal.xs;

public interface XSParticle extends XSObject {
   int getMinOccurs();

   int getMaxOccurs();

   boolean getMaxOccursUnbounded();

   XSTerm getTerm();

   XSObjectList getAnnotations();
}
