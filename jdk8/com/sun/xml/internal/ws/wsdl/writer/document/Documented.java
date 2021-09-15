package com.sun.xml.internal.ws.wsdl.writer.document;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlElement;

public interface Documented extends TypedXmlWriter {
   @XmlElement
   Documented documentation(String var1);
}
