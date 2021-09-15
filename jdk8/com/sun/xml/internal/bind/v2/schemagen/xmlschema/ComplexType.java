package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;

@XmlElement("complexType")
public interface ComplexType extends Annotated, ComplexTypeModel, TypedXmlWriter {
   @XmlAttribute("final")
   ComplexType _final(String[] var1);

   @XmlAttribute("final")
   ComplexType _final(String var1);

   @XmlAttribute
   ComplexType block(String[] var1);

   @XmlAttribute
   ComplexType block(String var1);

   @XmlAttribute("abstract")
   ComplexType _abstract(boolean var1);

   @XmlAttribute
   ComplexType name(String var1);
}
