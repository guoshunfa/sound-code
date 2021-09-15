package javax.xml.bind;

import javax.xml.namespace.QName;

public abstract class JAXBIntrospector {
   public abstract boolean isElement(Object var1);

   public abstract QName getElementName(Object var1);

   public static Object getValue(Object jaxbElement) {
      return jaxbElement instanceof JAXBElement ? ((JAXBElement)jaxbElement).getValue() : jaxbElement;
   }
}
