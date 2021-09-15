package com.sun.xml.internal.ws.wsdl.writer.document;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;
import com.sun.xml.internal.ws.wsdl.writer.document.soap.SOAPBinding;
import javax.xml.namespace.QName;

@XmlElement("binding")
public interface Binding extends TypedXmlWriter, StartWithExtensionsType {
   @XmlAttribute
   Binding type(QName var1);

   @XmlAttribute
   Binding name(String var1);

   @XmlElement
   BindingOperationType operation();

   @XmlElement(
      value = "binding",
      ns = "http://schemas.xmlsoap.org/wsdl/soap/"
   )
   SOAPBinding soapBinding();

   @XmlElement(
      value = "binding",
      ns = "http://schemas.xmlsoap.org/wsdl/soap12/"
   )
   com.sun.xml.internal.ws.wsdl.writer.document.soap12.SOAPBinding soap12Binding();
}
