package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(
   name = "soap-binding-style"
)
@XmlEnum
public enum SoapBindingStyle {
   DOCUMENT,
   RPC;

   public String value() {
      return this.name();
   }

   public static SoapBindingStyle fromValue(String v) {
      return valueOf(v);
   }
}
