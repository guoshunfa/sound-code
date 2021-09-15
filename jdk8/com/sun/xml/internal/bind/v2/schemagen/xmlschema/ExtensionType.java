package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import javax.xml.namespace.QName;

public interface ExtensionType extends Annotated, TypedXmlWriter {
   @XmlAttribute
   ExtensionType base(QName var1);
}
