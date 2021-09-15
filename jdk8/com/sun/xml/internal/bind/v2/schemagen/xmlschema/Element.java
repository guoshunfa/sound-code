package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import javax.xml.namespace.QName;

public interface Element extends Annotated, ComplexTypeHost, FixedOrDefault, SimpleTypeHost, TypedXmlWriter {
   @XmlAttribute
   Element type(QName var1);

   @XmlAttribute
   Element block(String[] var1);

   @XmlAttribute
   Element block(String var1);

   @XmlAttribute
   Element nillable(boolean var1);
}
