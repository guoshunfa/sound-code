package com.sun.org.apache.xerces.internal.xs;

public interface PSVIProvider {
   ElementPSVI getElementPSVI();

   AttributePSVI getAttributePSVI(int var1);

   AttributePSVI getAttributePSVIByName(String var1, String var2);
}
