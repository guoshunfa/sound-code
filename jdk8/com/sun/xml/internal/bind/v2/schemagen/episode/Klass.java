package com.sun.xml.internal.bind.v2.schemagen.episode;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;

public interface Klass extends TypedXmlWriter {
   @XmlAttribute
   void ref(String var1);
}
