package com.sun.xml.internal.ws.model.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLObject;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamReader;
import org.xml.sax.Locator;
import org.xml.sax.helpers.LocatorImpl;

abstract class AbstractObjectImpl implements WSDLObject {
   private final int lineNumber;
   private final String systemId;

   AbstractObjectImpl(XMLStreamReader xsr) {
      Location loc = xsr.getLocation();
      this.lineNumber = loc.getLineNumber();
      this.systemId = loc.getSystemId();
   }

   AbstractObjectImpl(String systemId, int lineNumber) {
      this.systemId = systemId;
      this.lineNumber = lineNumber;
   }

   @NotNull
   public final Locator getLocation() {
      LocatorImpl loc = new LocatorImpl();
      loc.setSystemId(this.systemId);
      loc.setLineNumber(this.lineNumber);
      return loc;
   }
}
