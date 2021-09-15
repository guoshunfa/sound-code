package com.sun.java.browser.dom;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

public abstract class DOMServiceProvider {
   public abstract boolean canHandle(Object var1);

   public abstract Document getDocument(Object var1) throws DOMUnsupportedException;

   public abstract DOMImplementation getDOMImplementation();
}
