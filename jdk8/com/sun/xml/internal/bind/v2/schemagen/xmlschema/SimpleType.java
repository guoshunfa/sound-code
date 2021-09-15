package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;

@XmlElement("simpleType")
public interface SimpleType extends Annotated, SimpleDerivation, TypedXmlWriter {
   @XmlAttribute("final")
   SimpleType _final(String var1);

   @XmlAttribute("final")
   SimpleType _final(String[] var1);

   @XmlAttribute
   SimpleType name(String var1);
}
