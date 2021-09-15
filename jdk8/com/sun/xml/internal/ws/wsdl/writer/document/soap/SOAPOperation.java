package com.sun.xml.internal.ws.wsdl.writer.document.soap;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;

@XmlElement("operation")
public interface SOAPOperation extends TypedXmlWriter {
   @XmlAttribute
   SOAPOperation soapAction(String var1);

   @XmlAttribute
   SOAPOperation style(String var1);
}
