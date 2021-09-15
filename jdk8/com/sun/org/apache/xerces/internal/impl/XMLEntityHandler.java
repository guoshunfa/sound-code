package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import java.io.IOException;

public interface XMLEntityHandler {
   void startEntity(String var1, XMLResourceIdentifier var2, String var3, Augmentations var4) throws XNIException;

   void endEntity(String var1, Augmentations var2) throws IOException, XNIException;
}
