package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.ws.soap.MTOM;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = ""
)
@XmlRootElement(
   name = "mtom"
)
public class XmlMTOM implements MTOM {
   @XmlAttribute(
      name = "enabled"
   )
   protected Boolean enabled;
   @XmlAttribute(
      name = "threshold"
   )
   protected Integer threshold;

   public boolean isEnabled() {
      return this.enabled == null ? true : this.enabled;
   }

   public void setEnabled(Boolean value) {
      this.enabled = value;
   }

   public int getThreshold() {
      return this.threshold == null ? 0 : this.threshold;
   }

   public void setThreshold(Integer value) {
      this.threshold = value;
   }

   public boolean enabled() {
      return (Boolean)Util.nullSafe((Object)this.enabled, (Object)Boolean.TRUE);
   }

   public int threshold() {
      return (Integer)Util.nullSafe((Object)this.threshold, (int)0);
   }

   public Class<? extends Annotation> annotationType() {
      return MTOM.class;
   }
}
