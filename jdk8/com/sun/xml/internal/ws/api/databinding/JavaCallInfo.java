package com.sun.xml.internal.ws.api.databinding;

import java.lang.reflect.Method;

public class JavaCallInfo implements com.oracle.webservices.internal.api.databinding.JavaCallInfo {
   private Method method;
   private Object[] parameters;
   private Object returnValue;
   private Throwable exception;

   public JavaCallInfo() {
   }

   public JavaCallInfo(Method m, Object[] args) {
      this.method = m;
      this.parameters = args;
   }

   public Method getMethod() {
      return this.method;
   }

   public void setMethod(Method method) {
      this.method = method;
   }

   public Object[] getParameters() {
      return this.parameters;
   }

   public void setParameters(Object[] parameters) {
      this.parameters = parameters;
   }

   public Object getReturnValue() {
      return this.returnValue;
   }

   public void setReturnValue(Object returnValue) {
      this.returnValue = returnValue;
   }

   public Throwable getException() {
      return this.exception;
   }

   public void setException(Throwable exception) {
      this.exception = exception;
   }
}
