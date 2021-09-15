package com.sun.xml.internal.ws.wsdl.writer.document;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import javax.xml.namespace.QName;

public interface FaultType extends TypedXmlWriter, Documented {
   @XmlAttribute
   FaultType message(QName var1);

   @XmlAttribute
   FaultType name(String var1);
}
