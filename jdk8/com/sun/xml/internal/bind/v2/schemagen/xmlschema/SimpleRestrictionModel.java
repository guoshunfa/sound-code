package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;
import javax.xml.namespace.QName;

public interface SimpleRestrictionModel extends SimpleTypeHost, TypedXmlWriter {
   @XmlAttribute
   SimpleRestrictionModel base(QName var1);

   @XmlElement
   NoFixedFacet enumeration();
}
