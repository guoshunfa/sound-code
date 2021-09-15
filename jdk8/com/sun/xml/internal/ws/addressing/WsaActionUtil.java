package com.sun.xml.internal.ws.addressing;

import com.sun.xml.internal.ws.api.model.CheckedException;
import com.sun.xml.internal.ws.api.model.JavaMethod;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

public class WsaActionUtil {
   private static final Logger LOGGER = Logger.getLogger(WsaActionUtil.class.getName());

   public static final String getDefaultFaultAction(JavaMethod method, CheckedException ce) {
      String tns = method.getOwner().getTargetNamespace();
      String delim = getDelimiter(tns);
      if (tns.endsWith(delim)) {
         tns = tns.substring(0, tns.length() - 1);
      }

      return tns + delim + method.getOwner().getPortTypeName().getLocalPart() + delim + method.getOperationName() + delim + "Fault" + delim + ce.getExceptionClass().getSimpleName();
   }

   private static String getDelimiter(String tns) {
      String delim = "/";

      try {
         URI uri = new URI(tns);
         if (uri.getScheme() != null && uri.getScheme().equalsIgnoreCase("urn")) {
            delim = ":";
         }
      } catch (URISyntaxException var3) {
         LOGGER.warning("TargetNamespace of WebService is not a valid URI");
      }

      return delim;
   }
}
