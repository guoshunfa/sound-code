package javax.xml.soap;

public abstract class SAAJMetaFactory {
   private static final String META_FACTORY_CLASS_PROPERTY = "javax.xml.soap.MetaFactory";
   static final String DEFAULT_META_FACTORY_CLASS = "com.sun.xml.internal.messaging.saaj.soap.SAAJMetaFactoryImpl";

   static SAAJMetaFactory getInstance() throws SOAPException {
      try {
         SAAJMetaFactory instance = (SAAJMetaFactory)FactoryFinder.find("javax.xml.soap.MetaFactory", "com.sun.xml.internal.messaging.saaj.soap.SAAJMetaFactoryImpl");
         return instance;
      } catch (Exception var1) {
         throw new SOAPException("Unable to create SAAJ meta-factory" + var1.getMessage());
      }
   }

   protected SAAJMetaFactory() {
   }

   protected abstract MessageFactory newMessageFactory(String var1) throws SOAPException;

   protected abstract SOAPFactory newSOAPFactory(String var1) throws SOAPException;
}
