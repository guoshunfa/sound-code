package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;

public interface FixedOrDefault extends TypedXmlWriter {
   @XmlAttribute("default")
   FixedOrDefault _default(String var1);

   @XmlAttribute
   FixedOrDefault fixed(String var1);
}
