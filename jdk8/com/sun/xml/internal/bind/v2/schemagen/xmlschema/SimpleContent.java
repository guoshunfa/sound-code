package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlElement;

@XmlElement("simpleContent")
public interface SimpleContent extends Annotated, TypedXmlWriter {
   @XmlElement
   SimpleExtension extension();

   @XmlElement
   SimpleRestriction restriction();
}
