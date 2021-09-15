package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import javax.xml.transform.Source;

public final class XMLInputSourceAdaptor implements Source {
   public final XMLInputSource fSource;

   public XMLInputSourceAdaptor(XMLInputSource core) {
      this.fSource = core;
   }

   public void setSystemId(String systemId) {
      this.fSource.setSystemId(systemId);
   }

   public String getSystemId() {
      try {
         return XMLEntityManager.expandSystemId(this.fSource.getSystemId(), this.fSource.getBaseSystemId(), false);
      } catch (URI.MalformedURIException var2) {
         return this.fSource.getSystemId();
      }
   }
}
