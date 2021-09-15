package javax.xml.bind;

/** @deprecated */
public interface Validator {
   /** @deprecated */
   void setEventHandler(ValidationEventHandler var1) throws JAXBException;

   /** @deprecated */
   ValidationEventHandler getEventHandler() throws JAXBException;

   /** @deprecated */
   boolean validate(Object var1) throws JAXBException;

   /** @deprecated */
   boolean validateRoot(Object var1) throws JAXBException;

   /** @deprecated */
   void setProperty(String var1, Object var2) throws PropertyException;

   /** @deprecated */
   Object getProperty(String var1) throws PropertyException;
}
