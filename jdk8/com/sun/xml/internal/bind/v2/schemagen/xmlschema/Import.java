package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;

@XmlElement("import")
public interface Import extends Annotated, TypedXmlWriter {
   @XmlAttribute
   Import namespace(String var1);

   @XmlAttribute
   Import schemaLocation(String var1);
}
