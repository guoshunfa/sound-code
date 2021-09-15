package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(
   name = "existing-annotations-type"
)
@XmlEnum
public enum ExistingAnnotationsType {
   @XmlEnumValue("merge")
   MERGE("merge"),
   @XmlEnumValue("ignore")
   IGNORE("ignore");

   private final String value;

   private ExistingAnnotationsType(String v) {
      this.value = v;
   }

   public String value() {
      return this.value;
   }

   public static ExistingAnnotationsType fromValue(String v) {
      ExistingAnnotationsType[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         ExistingAnnotationsType c = var1[var3];
         if (c.value.equals(v)) {
            return c;
         }
      }

      throw new IllegalArgumentException(v);
   }
}
