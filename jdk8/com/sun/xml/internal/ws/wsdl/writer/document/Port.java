package com.sun.xml.internal.ws.wsdl.writer.document;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;
import javax.xml.namespace.QName;

@XmlElement("port")
public interface Port extends TypedXmlWriter, Documented {
   @XmlAttribute
   Port name(String var1);

   @XmlAttribute
   Port arrayType(String var1);

   @XmlAttribute
   Port binding(QName var1);
}
