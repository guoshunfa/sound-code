package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.ws.ResponseWrapper;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = ""
)
@XmlRootElement(
   name = "response-wrapper"
)
public class XmlResponseWrapper implements ResponseWrapper {
   @XmlAttribute(
      name = "local-name"
   )
   protected String localName;
   @XmlAttribute(
      name = "target-namespace"
   )
   protected String targetNamespace;
   @XmlAttribute(
      name = "class-name"
   )
   protected String className;
   @XmlAttribute(
      name = "part-name"
   )
   protected String partName;

   public String getLocalName() {
      return this.localName == null ? "" : this.localName;
   }

   public void setLocalName(String value) {
      this.localName = value;
   }

   public String getTargetNamespace() {
      return this.targetNamespace == null ? "" : this.targetNamespace;
   }

   public void setTargetNamespace(String value) {
      this.targetNamespace = value;
   }

   public String getClassName() {
      return this.className == null ? "" : this.className;
   }

   public void setClassName(String value) {
      this.className = value;
   }

   public String getPartName() {
      return this.partName;
   }

   public void setPartName(String partName) {
      this.partName = partName;
   }

   public String localName() {
      return Util.nullSafe(this.localName);
   }

   public String targetNamespace() {
      return Util.nullSafe(this.targetNamespace);
   }

   public String className() {
      return Util.nullSafe(this.className);
   }

   public String partName() {
      return Util.nullSafe(this.partName);
   }

   public Class<? extends Annotation> annotationType() {
      return ResponseWrapper.class;
   }
}
