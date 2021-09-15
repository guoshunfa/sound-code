package com.sun.xml.internal.ws.model;

import com.sun.xml.internal.bind.api.Bridge;
import com.sun.xml.internal.ws.addressing.WsaActionUtil;
import com.sun.xml.internal.ws.api.model.CheckedException;
import com.sun.xml.internal.ws.api.model.ExceptionType;
import com.sun.xml.internal.ws.api.model.JavaMethod;
import com.sun.xml.internal.ws.spi.db.TypeInfo;
import com.sun.xml.internal.ws.spi.db.XMLBridge;

public final class CheckedExceptionImpl implements CheckedException {
   private final Class exceptionClass;
   private final TypeInfo detail;
   private final ExceptionType exceptionType;
   private final JavaMethodImpl javaMethod;
   private String messageName;
   private String faultAction = "";

   public CheckedExceptionImpl(JavaMethodImpl jm, Class exceptionClass, TypeInfo detail, ExceptionType exceptionType) {
      this.detail = detail;
      this.exceptionType = exceptionType;
      this.exceptionClass = exceptionClass;
      this.javaMethod = jm;
   }

   public AbstractSEIModelImpl getOwner() {
      return this.javaMethod.owner;
   }

   public JavaMethod getParent() {
      return this.javaMethod;
   }

   public Class getExceptionClass() {
      return this.exceptionClass;
   }

   public Class getDetailBean() {
      return (Class)this.detail.type;
   }

   /** @deprecated */
   public Bridge getBridge() {
      return null;
   }

   public XMLBridge getBond() {
      return this.getOwner().getXMLBridge(this.detail);
   }

   public TypeInfo getDetailType() {
      return this.detail;
   }

   public ExceptionType getExceptionType() {
      return this.exceptionType;
   }

   public String getMessageName() {
      return this.messageName;
   }

   public void setMessageName(String messageName) {
      this.messageName = messageName;
   }

   public String getFaultAction() {
      return this.faultAction;
   }

   public void setFaultAction(String faultAction) {
      this.faultAction = faultAction;
   }

   public String getDefaultFaultAction() {
      return WsaActionUtil.getDefaultFaultAction(this.javaMethod, this);
   }
}
