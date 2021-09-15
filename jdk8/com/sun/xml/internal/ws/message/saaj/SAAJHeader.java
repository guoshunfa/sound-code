package com.sun.xml.internal.ws.message.saaj;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.message.DOMHeader;
import javax.xml.soap.SOAPHeaderElement;

public final class SAAJHeader extends DOMHeader<SOAPHeaderElement> {
   public SAAJHeader(SOAPHeaderElement header) {
      super(header);
   }

   @NotNull
   public String getRole(@NotNull SOAPVersion soapVersion) {
      String v = this.getAttribute(soapVersion.nsUri, soapVersion.roleAttributeName);
      if (v == null || v.equals("")) {
         v = soapVersion.implicitRole;
      }

      return v;
   }
}
