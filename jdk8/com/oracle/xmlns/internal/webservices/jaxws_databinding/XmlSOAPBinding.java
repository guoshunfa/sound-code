package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import java.lang.annotation.Annotation;
import javax.jws.soap.SOAPBinding;
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
   name = "soap-binding"
)
public class XmlSOAPBinding implements SOAPBinding {
   @XmlAttribute(
      name = "style"
   )
   protected SoapBindingStyle style;
   @XmlAttribute(
      name = "use"
   )
   protected SoapBindingUse use;
   @XmlAttribute(
      name = "parameter-style"
   )
   protected SoapBindingParameterStyle parameterStyle;

   public SoapBindingStyle getStyle() {
      return this.style == null ? SoapBindingStyle.DOCUMENT : this.style;
   }

   public void setStyle(SoapBindingStyle value) {
      this.style = value;
   }

   public SoapBindingUse getUse() {
      return this.use == null ? SoapBindingUse.LITERAL : this.use;
   }

   public void setUse(SoapBindingUse value) {
      this.use = value;
   }

   public SoapBindingParameterStyle getParameterStyle() {
      return this.parameterStyle == null ? SoapBindingParameterStyle.WRAPPED : this.parameterStyle;
   }

   public void setParameterStyle(SoapBindingParameterStyle value) {
      this.parameterStyle = value;
   }

   public SOAPBinding.Style style() {
      return (SOAPBinding.Style)Util.nullSafe((Enum)this.style, (Enum)SOAPBinding.Style.DOCUMENT);
   }

   public SOAPBinding.Use use() {
      return (SOAPBinding.Use)Util.nullSafe((Enum)this.use, (Enum)SOAPBinding.Use.LITERAL);
   }

   public SOAPBinding.ParameterStyle parameterStyle() {
      return (SOAPBinding.ParameterStyle)Util.nullSafe((Enum)this.parameterStyle, (Enum)SOAPBinding.ParameterStyle.WRAPPED);
   }

   public Class<? extends Annotation> annotationType() {
      return SOAPBinding.class;
   }
}
