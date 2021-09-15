package javax.xml.soap;

import java.io.IOException;
import java.io.InputStream;

public abstract class MessageFactory {
   static final String DEFAULT_MESSAGE_FACTORY = "com.sun.xml.internal.messaging.saaj.soap.ver1_1.SOAPMessageFactory1_1Impl";
   private static final String MESSAGE_FACTORY_PROPERTY = "javax.xml.soap.MessageFactory";

   public static MessageFactory newInstance() throws SOAPException {
      try {
         MessageFactory factory = (MessageFactory)FactoryFinder.find("javax.xml.soap.MessageFactory", "com.sun.xml.internal.messaging.saaj.soap.ver1_1.SOAPMessageFactory1_1Impl", false);
         return factory != null ? factory : newInstance("SOAP 1.1 Protocol");
      } catch (Exception var1) {
         throw new SOAPException("Unable to create message factory for SOAP: " + var1.getMessage());
      }
   }

   public static MessageFactory newInstance(String protocol) throws SOAPException {
      return SAAJMetaFactory.getInstance().newMessageFactory(protocol);
   }

   public abstract SOAPMessage createMessage() throws SOAPException;

   public abstract SOAPMessage createMessage(MimeHeaders var1, InputStream var2) throws IOException, SOAPException;
}
