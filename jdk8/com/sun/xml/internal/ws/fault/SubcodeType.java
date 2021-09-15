package com.sun.xml.internal.ws.fault;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "SubcodeType",
   namespace = "http://www.w3.org/2003/05/soap-envelope",
   propOrder = {"Value", "Subcode"}
)
class SubcodeType {
   @XmlTransient
   private static final String ns = "http://www.w3.org/2003/05/soap-envelope";
   @XmlElement(
      namespace = "http://www.w3.org/2003/05/soap-envelope"
   )
   private QName Value;
   @XmlElements({@XmlElement(
   namespace = "http://www.w3.org/2003/05/soap-envelope"
)})
   private SubcodeType Subcode;

   public SubcodeType(QName value) {
      this.Value = value;
   }

   public SubcodeType() {
   }

   QName getValue() {
      return this.Value;
   }

   SubcodeType getSubcode() {
      return this.Subcode;
   }

   void setSubcode(SubcodeType subcode) {
      this.Subcode = subcode;
   }
}
