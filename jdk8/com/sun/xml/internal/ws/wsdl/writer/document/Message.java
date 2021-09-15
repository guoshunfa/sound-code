package com.sun.xml.internal.ws.wsdl.writer.document;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;

@XmlElement("message")
public interface Message extends TypedXmlWriter, Documented {
   @XmlAttribute
   Message name(String var1);

   @XmlElement
   Part part();
}
