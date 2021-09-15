package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;

public interface NoFixedFacet extends Annotated, TypedXmlWriter {
   @XmlAttribute
   NoFixedFacet value(String var1);
}
