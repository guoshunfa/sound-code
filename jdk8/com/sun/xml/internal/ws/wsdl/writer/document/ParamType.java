package com.sun.xml.internal.ws.wsdl.writer.document;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import javax.xml.namespace.QName;

public interface ParamType extends TypedXmlWriter, Documented {
   @XmlAttribute
   ParamType message(QName var1);

   @XmlAttribute
   ParamType name(String var1);
}
