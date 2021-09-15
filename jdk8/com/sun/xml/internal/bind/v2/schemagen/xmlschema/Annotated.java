package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;

public interface Annotated extends TypedXmlWriter {
   @XmlElement
   Annotation annotation();

   @XmlAttribute
   Annotated id(String var1);
}
