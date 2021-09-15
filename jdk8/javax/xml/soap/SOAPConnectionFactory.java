package javax.xml.soap;

public abstract class SOAPConnectionFactory {
   static final String DEFAULT_SOAP_CONNECTION_FACTORY = "com.sun.xml.internal.messaging.saaj.client.p2p.HttpSOAPConnectionFactory";
   private static final String SF_PROPERTY = "javax.xml.soap.SOAPConnectionFactory";

   public static SOAPConnectionFactory newInstance() throws SOAPException, UnsupportedOperationException {
      try {
         return (SOAPConnectionFactory)FactoryFinder.find("javax.xml.soap.SOAPConnectionFactory", "com.sun.xml.internal.messaging.saaj.client.p2p.HttpSOAPConnectionFactory");
      } catch (Exception var1) {
         throw new SOAPException("Unable to create SOAP connection factory: " + var1.getMessage());
      }
   }

   public abstract SOAPConnection createConnection() throws SOAPException;
}
