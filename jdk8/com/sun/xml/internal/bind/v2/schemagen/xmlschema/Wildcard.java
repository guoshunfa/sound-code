package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;

public interface Wildcard extends Annotated, TypedXmlWriter {
   @XmlAttribute
   Wildcard processContents(String var1);

   @XmlAttribute
   Wildcard namespace(String[] var1);

   @XmlAttribute
   Wildcard namespace(String var1);
}
