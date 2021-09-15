package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;

@XmlElement("annotation")
public interface Annotation extends TypedXmlWriter {
   @XmlElement
   Appinfo appinfo();

   @XmlElement
   Documentation documentation();

   @XmlAttribute
   Annotation id(String var1);
}
