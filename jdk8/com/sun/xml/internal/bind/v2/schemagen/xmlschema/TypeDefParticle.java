package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlElement;

public interface TypeDefParticle extends TypedXmlWriter {
   @XmlElement
   ExplicitGroup all();

   @XmlElement
   ExplicitGroup sequence();

   @XmlElement
   ExplicitGroup choice();
}
