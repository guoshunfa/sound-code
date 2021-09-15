package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.ws.soap.Addressing;
import javax.xml.ws.soap.AddressingFeature;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = ""
)
@XmlRootElement(
   name = "addressing"
)
public class XmlAddressing implements Addressing {
   @XmlAttribute(
      name = "enabled"
   )
   protected Boolean enabled;
   @XmlAttribute(
      name = "required"
   )
   protected Boolean required;

   public Boolean getEnabled() {
      return this.enabled();
   }

   public void setEnabled(Boolean enabled) {
      this.enabled = enabled;
   }

   public Boolean getRequired() {
      return this.required();
   }

   public void setRequired(Boolean required) {
      this.required = required;
   }

   public boolean enabled() {
      return (Boolean)Util.nullSafe((Object)this.enabled, (Object)true);
   }

   public boolean required() {
      return (Boolean)Util.nullSafe((Object)this.required, (Object)false);
   }

   public AddressingFeature.Responses responses() {
      return AddressingFeature.Responses.ALL;
   }

   public Class<? extends Annotation> annotationType() {
      return Addressing.class;
   }
}
