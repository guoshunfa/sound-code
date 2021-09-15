package com.sun.xml.internal.ws.util;

public class ServiceConfigurationError extends Error {
   public ServiceConfigurationError(String msg) {
      super(msg);
   }

   public ServiceConfigurationError(Throwable x) {
      super(x);
   }
}
