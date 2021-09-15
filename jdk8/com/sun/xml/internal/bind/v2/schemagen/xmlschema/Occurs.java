package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;

public interface Occurs extends TypedXmlWriter {
   @XmlAttribute
   Occurs minOccurs(int var1);

   @XmlAttribute
   Occurs maxOccurs(String var1);

   @XmlAttribute
   Occurs maxOccurs(int var1);
}
