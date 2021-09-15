package com.sun.xml.internal.messaging.saaj.util;

import java.util.Iterator;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.MimeHeaders;

public class MimeHeadersUtil {
   public static MimeHeaders copy(MimeHeaders headers) {
      MimeHeaders newHeaders = new MimeHeaders();
      Iterator eachHeader = headers.getAllHeaders();

      while(eachHeader.hasNext()) {
         MimeHeader currentHeader = (MimeHeader)eachHeader.next();
         newHeaders.addHeader(currentHeader.getName(), currentHeader.getValue());
      }

      return newHeaders;
   }
}
