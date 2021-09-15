package com.sun.xml.internal.bind.v2.model.annotation;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

class Init {
   static Quick[] getAll() {
      return new Quick[]{new XmlAttributeQuick((Locatable)null, (XmlAttribute)null), new XmlElementQuick((Locatable)null, (XmlElement)null), new XmlElementDeclQuick((Locatable)null, (XmlElementDecl)null), new XmlElementRefQuick((Locatable)null, (XmlElementRef)null), new XmlElementRefsQuick((Locatable)null, (XmlElementRefs)null), new XmlEnumQuick((Locatable)null, (XmlEnum)null), new XmlRootElementQuick((Locatable)null, (XmlRootElement)null), new XmlSchemaQuick((Locatable)null, (XmlSchema)null), new XmlSchemaTypeQuick((Locatable)null, (XmlSchemaType)null), new XmlTransientQuick((Locatable)null, (XmlTransient)null), new XmlTypeQuick((Locatable)null, (XmlType)null), new XmlValueQuick((Locatable)null, (XmlValue)null)};
   }
}
