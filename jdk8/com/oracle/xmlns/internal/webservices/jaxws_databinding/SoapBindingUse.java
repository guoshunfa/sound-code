package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(
   name = "soap-binding-use"
)
@XmlEnum
public enum SoapBindingUse {
   LITERAL,
   ENCODED;

   public String value() {
      return this.name();
   }

   public static SoapBindingUse fromValue(String v) {
      return valueOf(v);
   }
}
