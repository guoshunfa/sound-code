package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import java.lang.annotation.Annotation;
import javax.jws.WebParam;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = ""
)
@XmlRootElement(
   name = "web-param"
)
public class XmlWebParam implements WebParam {
   @XmlAttribute(
      name = "header"
   )
   protected Boolean header;
   @XmlAttribute(
      name = "mode"
   )
   protected WebParamMode mode;
   @XmlAttribute(
      name = "name"
   )
   protected String name;
   @XmlAttribute(
      name = "part-name"
   )
   protected String partName;
   @XmlAttribute(
      name = "target-namespace"
   )
   protected String targetNamespace;

   public boolean isHeader() {
      return this.header == null ? false : this.header;
   }

   public void setHeader(Boolean value) {
      this.header = value;
   }

   public WebParamMode getMode() {
      return this.mode == null ? WebParamMode.IN : this.mode;
   }

   public void setMode(WebParamMode value) {
      this.mode = value;
   }

   public String getName() {
      return this.name == null ? "" : this.name;
   }

   public void setName(String value) {
      this.name = value;
   }

   public String getPartName() {
      return this.partName == null ? "" : this.partName;
   }

   public void setPartName(String value) {
      this.partName = value;
   }

   public String getTargetNamespace() {
      return this.targetNamespace == null ? "" : this.targetNamespace;
   }

   public void setTargetNamespace(String value) {
      this.targetNamespace = value;
   }

   public String name() {
      return Util.nullSafe(this.name);
   }

   public String partName() {
      return Util.nullSafe(this.partName);
   }

   public String targetNamespace() {
      return Util.nullSafe(this.targetNamespace);
   }

   public WebParam.Mode mode() {
      return (WebParam.Mode)Util.nullSafe((Enum)this.mode, (Enum)WebParam.Mode.IN);
   }

   public boolean header() {
      return (Boolean)Util.nullSafe((Object)this.header, (Object)false);
   }

   public Class<? extends Annotation> annotationType() {
      return WebParam.class;
   }
}
