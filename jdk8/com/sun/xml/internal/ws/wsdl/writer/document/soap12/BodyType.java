package com.sun.xml.internal.ws.wsdl.writer.document.soap12;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;

public interface BodyType extends TypedXmlWriter {
   @XmlAttribute
   BodyType encodingStyle(String var1);

   @XmlAttribute
   BodyType namespace(String var1);

   @XmlAttribute
   BodyType use(String var1);

   @XmlAttribute
   BodyType parts(String var1);
}
