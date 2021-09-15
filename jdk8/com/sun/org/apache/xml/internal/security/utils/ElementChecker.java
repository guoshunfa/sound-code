package com.sun.org.apache.xml.internal.security.utils;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/** @deprecated */
@Deprecated
public interface ElementChecker {
   void guaranteeThatElementInCorrectSpace(ElementProxy var1, Element var2) throws XMLSecurityException;

   boolean isNamespaceElement(Node var1, String var2, String var3);
}
