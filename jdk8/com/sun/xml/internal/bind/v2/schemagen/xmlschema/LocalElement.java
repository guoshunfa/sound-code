package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;
import javax.xml.namespace.QName;

@XmlElement("element")
public interface LocalElement extends Element, Occurs, TypedXmlWriter {
   @XmlAttribute
   LocalElement form(String var1);

   @XmlAttribute
   LocalElement name(String var1);

   @XmlAttribute
   LocalElement ref(QName var1);
}
