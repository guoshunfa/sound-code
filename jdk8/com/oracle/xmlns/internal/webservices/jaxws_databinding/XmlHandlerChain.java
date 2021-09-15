package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import java.lang.annotation.Annotation;
import javax.jws.HandlerChain;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = ""
)
@XmlRootElement(
   name = "handler-chain"
)
public class XmlHandlerChain implements HandlerChain {
   @XmlAttribute(
      name = "file"
   )
   protected String file;

   public String getFile() {
      return this.file;
   }

   public void setFile(String value) {
      this.file = value;
   }

   public String file() {
      return Util.nullSafe(this.file);
   }

   public String name() {
      return "";
   }

   public Class<? extends Annotation> annotationType() {
      return HandlerChain.class;
   }
}
