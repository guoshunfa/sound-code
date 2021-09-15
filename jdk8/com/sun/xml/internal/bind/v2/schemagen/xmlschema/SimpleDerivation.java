package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlElement;

public interface SimpleDerivation extends TypedXmlWriter {
   @XmlElement
   SimpleRestriction restriction();

   @XmlElement
   Union union();

   @XmlElement
   List list();
}
