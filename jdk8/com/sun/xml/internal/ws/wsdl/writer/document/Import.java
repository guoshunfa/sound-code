package com.sun.xml.internal.ws.wsdl.writer.document;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;

@XmlElement("import")
public interface Import extends TypedXmlWriter, Documented {
   @XmlAttribute
   Import location(String var1);

   @XmlAttribute
   Import namespace(String var1);
}
