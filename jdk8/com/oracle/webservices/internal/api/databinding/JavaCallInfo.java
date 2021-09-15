package com.oracle.webservices.internal.api.databinding;

import java.lang.reflect.Method;

public interface JavaCallInfo {
   Method getMethod();

   Object[] getParameters();

   Object getReturnValue();

   void setReturnValue(Object var1);

   Throwable getException();

   void setException(Throwable var1);
}
