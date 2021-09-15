package com.sun.xml.internal.ws.wsdl.writer.document;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;

@XmlElement("portType")
public interface PortType extends TypedXmlWriter, Documented {
   @XmlAttribute
   PortType name(String var1);

   @XmlElement
   Operation operation();
}
