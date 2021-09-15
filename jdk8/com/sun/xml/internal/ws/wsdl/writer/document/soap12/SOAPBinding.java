package com.sun.xml.internal.ws.wsdl.writer.document.soap12;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;

@XmlElement("binding")
public interface SOAPBinding extends TypedXmlWriter {
   @XmlAttribute
   SOAPBinding transport(String var1);

   @XmlAttribute
   SOAPBinding style(String var1);
}
