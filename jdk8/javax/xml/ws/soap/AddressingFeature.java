package javax.xml.ws.soap;

import javax.xml.ws.WebServiceFeature;

public final class AddressingFeature extends WebServiceFeature {
   public static final String ID = "http://www.w3.org/2005/08/addressing/module";
   protected boolean required;
   private final AddressingFeature.Responses responses;

   public AddressingFeature() {
      this(true, false, AddressingFeature.Responses.ALL);
   }

   public AddressingFeature(boolean enabled) {
      this(enabled, false, AddressingFeature.Responses.ALL);
   }

   public AddressingFeature(boolean enabled, boolean required) {
      this(enabled, required, AddressingFeature.Responses.ALL);
   }

   public AddressingFeature(boolean enabled, boolean required, AddressingFeature.Responses responses) {
      this.enabled = enabled;
      this.required = required;
      this.responses = responses;
   }

   public String getID() {
      return "http://www.w3.org/2005/08/addressing/module";
   }

   public boolean isRequired() {
      return this.required;
   }

   public AddressingFeature.Responses getResponses() {
      return this.responses;
   }

   public static enum Responses {
      ANONYMOUS,
      NON_ANONYMOUS,
      ALL;
   }
}
