package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(
   name = "web-param-mode"
)
@XmlEnum
public enum WebParamMode {
   IN,
   OUT,
   INOUT;

   public String value() {
      return this.name();
   }

   public static WebParamMode fromValue(String v) {
      return valueOf(v);
   }
}
