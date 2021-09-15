package com.sun.java.browser.dom;

import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

public interface DOMAccessor {
   Document getDocument(Object var1) throws DOMException;

   DOMImplementation getDOMImplementation();
}
