package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;

public interface ComplexTypeModel extends AttrDecls, TypeDefParticle, TypedXmlWriter {
   @XmlElement
   SimpleContent simpleContent();

   @XmlElement
   ComplexContent complexContent();

   @XmlAttribute
   ComplexTypeModel mixed(boolean var1);
}
