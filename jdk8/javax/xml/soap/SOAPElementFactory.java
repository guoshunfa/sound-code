package javax.xml.soap;

/** @deprecated */
public class SOAPElementFactory {
   private SOAPFactory soapFactory;

   private SOAPElementFactory(SOAPFactory soapFactory) {
      this.soapFactory = soapFactory;
   }

   /** @deprecated */
   public SOAPElement create(Name name) throws SOAPException {
      return this.soapFactory.createElement(name);
   }

   /** @deprecated */
   public SOAPElement create(String localName) throws SOAPException {
      return this.soapFactory.createElement(localName);
   }

   /** @deprecated */
   public SOAPElement create(String localName, String prefix, String uri) throws SOAPException {
      return this.soapFactory.createElement(localName, prefix, uri);
   }

   public static SOAPElementFactory newInstance() throws SOAPException {
      try {
         return new SOAPElementFactory(SOAPFactory.newInstance());
      } catch (Exception var1) {
         throw new SOAPException("Unable to create SOAP Element Factory: " + var1.getMessage());
      }
   }
}
