package com.sun.xml.internal.ws.wsdl.writer.document;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;
import javax.xml.namespace.QName;

@XmlElement("part")
public interface Part extends TypedXmlWriter, OpenAtts {
   @XmlAttribute
   Part element(QName var1);

   @XmlAttribute
   Part type(QName var1);

   @XmlAttribute
   Part name(String var1);
}
