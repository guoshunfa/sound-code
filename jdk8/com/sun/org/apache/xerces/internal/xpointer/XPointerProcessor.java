package com.sun.org.apache.xerces.internal.xpointer;

import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XNIException;

public interface XPointerProcessor {
   int EVENT_ELEMENT_START = 0;
   int EVENT_ELEMENT_END = 1;
   int EVENT_ELEMENT_EMPTY = 2;

   void parseXPointer(String var1) throws XNIException;

   boolean resolveXPointer(QName var1, XMLAttributes var2, Augmentations var3, int var4) throws XNIException;

   boolean isFragmentResolved() throws XNIException;

   boolean isXPointerResolved() throws XNIException;
}
