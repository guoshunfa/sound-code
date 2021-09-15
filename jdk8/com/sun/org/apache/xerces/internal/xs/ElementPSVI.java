package com.sun.org.apache.xerces.internal.xs;

public interface ElementPSVI extends ItemPSVI {
   XSElementDeclaration getElementDeclaration();

   XSNotationDeclaration getNotation();

   boolean getNil();

   XSModel getSchemaInformation();
}
