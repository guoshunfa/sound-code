package com.sun.xml.internal.ws.policy.spi;

import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.sourcemodel.AssertionData;

public final class AssertionCreationException extends PolicyException {
   private final AssertionData assertionData;

   public AssertionCreationException(AssertionData assertionData, String message) {
      super(message);
      this.assertionData = assertionData;
   }

   public AssertionCreationException(AssertionData assertionData, String message, Throwable cause) {
      super(message, cause);
      this.assertionData = assertionData;
   }

   public AssertionCreationException(AssertionData assertionData, Throwable cause) {
      super(cause);
      this.assertionData = assertionData;
   }

   public AssertionData getAssertionData() {
      return this.assertionData;
   }
}
