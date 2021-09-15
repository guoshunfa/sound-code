package com.sun.xml.internal.ws.runtime.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "",
   propOrder = {"tubelines", "any"}
)
@XmlRootElement(
   name = "metro"
)
public class MetroConfig {
   protected Tubelines tubelines;
   @XmlAnyElement(
      lax = true
   )
   protected List<Object> any;
   @XmlAttribute(
      required = true
   )
   protected String version;
   @XmlAnyAttribute
   private Map<QName, String> otherAttributes = new HashMap();

   public Tubelines getTubelines() {
      return this.tubelines;
   }

   public void setTubelines(Tubelines value) {
      this.tubelines = value;
   }

   public List<Object> getAny() {
      if (this.any == null) {
         this.any = new ArrayList();
      }

      return this.any;
   }

   public String getVersion() {
      return this.version;
   }

   public void setVersion(String value) {
      this.version = value;
   }

   public Map<QName, String> getOtherAttributes() {
      return this.otherAttributes;
   }
}
