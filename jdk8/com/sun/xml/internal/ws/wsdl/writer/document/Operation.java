package com.sun.xml.internal.ws.wsdl.writer.document;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;

@XmlElement("operation")
public interface Operation extends TypedXmlWriter, Documented {
   @XmlElement
   ParamType input();

   @XmlElement
   ParamType output();

   @XmlElement
   FaultType fault();

   @XmlAttribute
   Operation name(String var1);

   @XmlAttribute
   Operation parameterOrder(String var1);
}
