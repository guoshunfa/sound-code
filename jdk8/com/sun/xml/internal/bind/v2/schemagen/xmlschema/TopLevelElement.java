package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;
import javax.xml.namespace.QName;

@XmlElement("element")
public interface TopLevelElement extends Element, TypedXmlWriter {
   @XmlAttribute("final")
   TopLevelElement _final(String[] var1);

   @XmlAttribute("final")
   TopLevelElement _final(String var1);

   @XmlAttribute("abstract")
   TopLevelElement _abstract(boolean var1);

   @XmlAttribute
   TopLevelElement substitutionGroup(QName var1);

   @XmlAttribute
   TopLevelElement name(String var1);
}
