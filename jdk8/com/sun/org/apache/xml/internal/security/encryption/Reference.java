package com.sun.org.apache.xml.internal.security.encryption;

import java.util.Iterator;
import org.w3c.dom.Element;

public interface Reference {
   String getType();

   String getURI();

   void setURI(String var1);

   Iterator<Element> getElementRetrievalInformation();

   void addElementRetrievalInformation(Element var1);

   void removeElementRetrievalInformation(Element var1);
}
