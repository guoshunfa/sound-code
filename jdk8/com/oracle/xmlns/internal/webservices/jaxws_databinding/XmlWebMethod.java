package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import java.lang.annotation.Annotation;
import javax.jws.WebMethod;
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
   name = "web-method"
)
public class XmlWebMethod implements WebMethod {
   @XmlAttribute(
      name = "action"
   )
   protected String action;
   @XmlAttribute(
      name = "exclude"
   )
   protected Boolean exclude;
   @XmlAttribute(
      name = "operation-name"
   )
   protected String operationName;

   public String getAction() {
      return this.action == null ? "" : this.action;
   }

   public void setAction(String value) {
      this.action = value;
   }

   public boolean isExclude() {
      return this.exclude == null ? false : this.exclude;
   }

   public void setExclude(Boolean value) {
      this.exclude = value;
   }

   public String getOperationName() {
      return this.operationName == null ? "" : this.operationName;
   }

   public void setOperationName(String value) {
      this.operationName = value;
   }

   public String operationName() {
      return Util.nullSafe(this.operationName);
   }

   public String action() {
      return Util.nullSafe(this.action);
   }

   public boolean exclude() {
      return (Boolean)Util.nullSafe((Object)this.exclude, (Object)false);
   }

   public Class<? extends Annotation> annotationType() {
      return WebMethod.class;
   }
}
