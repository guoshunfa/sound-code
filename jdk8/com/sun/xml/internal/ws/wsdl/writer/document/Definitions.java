package com.sun.xml.internal.ws.wsdl.writer.document;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;

@XmlElement("definitions")
public interface Definitions extends TypedXmlWriter, Documented {
   @XmlAttribute
   Definitions name(String var1);

   @XmlAttribute
   Definitions targetNamespace(String var1);

   @XmlElement
   Service service();

   @XmlElement
   Binding binding();

   @XmlElement
   PortType portType();

   @XmlElement
   Message message();

   @XmlElement
   Types types();

   @XmlElement("import")
   Import _import();
}
