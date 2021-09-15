package com.sun.xml.internal.ws.policy.privateutil;

public class ServiceConfigurationError extends Error {
   public ServiceConfigurationError(String message) {
      super(message);
   }

   public ServiceConfigurationError(Throwable throwable) {
      super(throwable);
   }
}
