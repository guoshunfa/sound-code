package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;

@XmlElement("schema")
public interface Schema extends SchemaTop, TypedXmlWriter {
   @XmlElement
   Annotation annotation();

   @XmlElement("import")
   Import _import();

   @XmlAttribute
   Schema targetNamespace(String var1);

   @XmlAttribute(
      ns = "http://www.w3.org/XML/1998/namespace"
   )
   Schema lang(String var1);

   @XmlAttribute
   Schema id(String var1);

   @XmlAttribute
   Schema elementFormDefault(String var1);

   @XmlAttribute
   Schema attributeFormDefault(String var1);

   @XmlAttribute
   Schema blockDefault(String[] var1);

   @XmlAttribute
   Schema blockDefault(String var1);

   @XmlAttribute
   Schema finalDefault(String[] var1);

   @XmlAttribute
   Schema finalDefault(String var1);

   @XmlAttribute
   Schema version(String var1);
}
