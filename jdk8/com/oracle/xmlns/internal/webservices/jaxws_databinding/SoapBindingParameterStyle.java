package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(
   name = "soap-binding-parameter-style"
)
@XmlEnum
public enum SoapBindingParameterStyle {
   BARE,
   WRAPPED;

   public String value() {
      return this.name();
   }

   public static SoapBindingParameterStyle fromValue(String v) {
      return valueOf(v);
   }
}
