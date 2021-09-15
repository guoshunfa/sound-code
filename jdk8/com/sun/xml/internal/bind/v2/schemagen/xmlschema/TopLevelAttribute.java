package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;

@XmlElement("attribute")
public interface TopLevelAttribute extends Annotated, AttributeType, FixedOrDefault, TypedXmlWriter {
   @XmlAttribute
   TopLevelAttribute name(String var1);
}
