package com.sun.xml.internal.ws.runtime.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "tubeFactoryListCType",
   propOrder = {"tubeFactoryConfigs", "any"}
)
public class TubeFactoryList {
   @XmlElement(
      name = "tube-factory",
      required = true
   )
   protected List<TubeFactoryConfig> tubeFactoryConfigs;
   @XmlAnyElement(
      lax = true
   )
   protected List<Object> any;
   @XmlAnyAttribute
   private Map<QName, String> otherAttributes = new HashMap();

   public List<TubeFactoryConfig> getTubeFactoryConfigs() {
      if (this.tubeFactoryConfigs == null) {
         this.tubeFactoryConfigs = new ArrayList();
      }

      return this.tubeFactoryConfigs;
   }

   public List<Object> getAny() {
      if (this.any == null) {
         this.any = new ArrayList();
      }

      return this.any;
   }

   public Map<QName, String> getOtherAttributes() {
      return this.otherAttributes;
   }
}
