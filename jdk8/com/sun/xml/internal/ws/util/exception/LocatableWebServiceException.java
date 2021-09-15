package com.sun.xml.internal.ws.util.exception;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.resources.UtilMessages;
import java.util.Arrays;
import java.util.List;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamReader;
import javax.xml.ws.WebServiceException;
import org.xml.sax.Locator;
import org.xml.sax.helpers.LocatorImpl;

public class LocatableWebServiceException extends WebServiceException {
   private final Locator[] location;

   public LocatableWebServiceException(String message, Locator... location) {
      this(message, (Throwable)null, (Locator[])location);
   }

   public LocatableWebServiceException(String message, Throwable cause, Locator... location) {
      super(appendLocationInfo(message, location), cause);
      this.location = location;
   }

   public LocatableWebServiceException(Throwable cause, Locator... location) {
      this(cause.toString(), cause, location);
   }

   public LocatableWebServiceException(String message, XMLStreamReader locationSource) {
      this(message, toLocation(locationSource));
   }

   public LocatableWebServiceException(String message, Throwable cause, XMLStreamReader locationSource) {
      this(message, cause, toLocation(locationSource));
   }

   public LocatableWebServiceException(Throwable cause, XMLStreamReader locationSource) {
      this(cause, toLocation(locationSource));
   }

   @NotNull
   public List<Locator> getLocation() {
      return Arrays.asList(this.location);
   }

   private static String appendLocationInfo(String message, Locator[] location) {
      StringBuilder buf = new StringBuilder(message);
      Locator[] var3 = location;
      int var4 = location.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Locator loc = var3[var5];
         buf.append('\n').append(UtilMessages.UTIL_LOCATION(loc.getLineNumber(), loc.getSystemId()));
      }

      return buf.toString();
   }

   private static Locator toLocation(XMLStreamReader xsr) {
      LocatorImpl loc = new LocatorImpl();
      Location in = xsr.getLocation();
      loc.setSystemId(in.getSystemId());
      loc.setPublicId(in.getPublicId());
      loc.setLineNumber(in.getLineNumber());
      loc.setColumnNumber(in.getColumnNumber());
      return loc;
   }
}
