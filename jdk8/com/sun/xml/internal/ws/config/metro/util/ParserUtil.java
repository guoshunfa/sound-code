package com.sun.xml.internal.ws.config.metro.util;

import com.sun.istack.internal.logging.Logger;
import javax.xml.ws.WebServiceException;

public class ParserUtil {
   private static final Logger LOGGER = Logger.getLogger(ParserUtil.class);

   private ParserUtil() {
   }

   public static boolean parseBooleanValue(String value) throws WebServiceException {
      if (!"true".equals(value) && !"1".equals(value)) {
         if (!"false".equals(value) && !"0".equals(value)) {
            throw (WebServiceException)LOGGER.logSevereException(new WebServiceException("invalid boolean value"));
         } else {
            return false;
         }
      } else {
         return true;
      }
   }
}
