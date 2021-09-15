package javax.xml.soap;

import javax.xml.namespace.QName;
import org.w3c.dom.Element;

public abstract class SOAPFactory {
   private static final String SOAP_FACTORY_PROPERTY = "javax.xml.soap.SOAPFactory";
   static final String DEFAULT_SOAP_FACTORY = "com.sun.xml.internal.messaging.saaj.soap.ver1_1.SOAPFactory1_1Impl";

   public SOAPElement createElement(Element domElement) throws SOAPException {
      throw new UnsupportedOperationException("createElement(org.w3c.dom.Element) must be overridden by all subclasses of SOAPFactory.");
   }

   public abstract SOAPElement createElement(Name var1) throws SOAPException;

   public SOAPElement createElement(QName qname) throws SOAPException {
      throw new UnsupportedOperationException("createElement(QName) must be overridden by all subclasses of SOAPFactory.");
   }

   public abstract SOAPElement createElement(String var1) throws SOAPException;

   public abstract SOAPElement createElement(String var1, String var2, String var3) throws SOAPException;

   public abstract Detail createDetail() throws SOAPException;

   public abstract SOAPFault createFault(String var1, QName var2) throws SOAPException;

   public abstract SOAPFault createFault() throws SOAPException;

   public abstract Name createName(String var1, String var2, String var3) throws SOAPException;

   public abstract Name createName(String var1) throws SOAPException;

   public static SOAPFactory newInstance() throws SOAPException {
      try {
         SOAPFactory factory = (SOAPFactory)FactoryFinder.find("javax.xml.soap.SOAPFactory", "com.sun.xml.internal.messaging.saaj.soap.ver1_1.SOAPFactory1_1Impl", false);
         return factory != null ? factory : newInstance("SOAP 1.1 Protocol");
      } catch (Exception var1) {
         throw new SOAPException("Unable to create SOAP Factory: " + var1.getMessage());
      }
   }

   public static SOAPFactory newInstance(String protocol) throws SOAPException {
      return SAAJMetaFactory.getInstance().newSOAPFactory(protocol);
   }
}
