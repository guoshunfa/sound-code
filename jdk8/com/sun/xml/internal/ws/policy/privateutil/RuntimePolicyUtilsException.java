package com.sun.xml.internal.ws.policy.privateutil;

public final class RuntimePolicyUtilsException extends RuntimeException {
   RuntimePolicyUtilsException(String message) {
      super(message);
   }

   RuntimePolicyUtilsException(String message, Throwable cause) {
      super(message, cause);
   }
}
