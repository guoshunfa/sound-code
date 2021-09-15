package com.sun.xml.internal.ws.wsdl.writer.document.xsd;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;
import com.sun.xml.internal.ws.wsdl.writer.document.Documented;

@XmlElement("import")
public interface Import extends TypedXmlWriter, Documented {
   @XmlAttribute
   Import schemaLocation(String var1);

   @XmlAttribute
   Import namespace(String var1);
}
