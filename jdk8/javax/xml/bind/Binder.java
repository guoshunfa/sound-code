package javax.xml.bind;

import javax.xml.validation.Schema;

public abstract class Binder<XmlNode> {
   public abstract Object unmarshal(XmlNode var1) throws JAXBException;

   public abstract <T> JAXBElement<T> unmarshal(XmlNode var1, Class<T> var2) throws JAXBException;

   public abstract void marshal(Object var1, XmlNode var2) throws JAXBException;

   public abstract XmlNode getXMLNode(Object var1);

   public abstract Object getJAXBNode(XmlNode var1);

   public abstract XmlNode updateXML(Object var1) throws JAXBException;

   public abstract XmlNode updateXML(Object var1, XmlNode var2) throws JAXBException;

   public abstract Object updateJAXB(XmlNode var1) throws JAXBException;

   public abstract void setSchema(Schema var1);

   public abstract Schema getSchema();

   public abstract void setEventHandler(ValidationEventHandler var1) throws JAXBException;

   public abstract ValidationEventHandler getEventHandler() throws JAXBException;

   public abstract void setProperty(String var1, Object var2) throws PropertyException;

   public abstract Object getProperty(String var1) throws PropertyException;
}
