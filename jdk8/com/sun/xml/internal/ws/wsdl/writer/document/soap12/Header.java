package com.sun.xml.internal.ws.wsdl.writer.document.soap12;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;
import javax.xml.namespace.QName;

@XmlElement("header")
public interface Header extends TypedXmlWriter, BodyType {
   @XmlAttribute
   Header message(QName var1);

   @XmlElement
   HeaderFault headerFault();

   @XmlAttribute
   BodyType part(String var1);
}
