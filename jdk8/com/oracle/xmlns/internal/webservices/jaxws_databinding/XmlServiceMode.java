package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = ""
)
@XmlRootElement(
   name = "service-mode"
)
public class XmlServiceMode implements ServiceMode {
   @XmlAttribute(
      name = "value"
   )
   protected String value;

   public String getValue() {
      return this.value == null ? "PAYLOAD" : this.value;
   }

   public void setValue(String value) {
      this.value = value;
   }

   public Service.Mode value() {
      return Service.Mode.valueOf((String)Util.nullSafe((Object)this.value, (Object)"PAYLOAD"));
   }

   public Class<? extends Annotation> annotationType() {
      return ServiceMode.class;
   }
}
