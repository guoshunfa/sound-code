package com.sun.xml.internal.ws.api;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import javax.xml.ws.WebServiceException;

public abstract class BindingIDFactory {
   @Nullable
   public abstract BindingID parse(@NotNull String var1) throws WebServiceException;

   @Nullable
   public BindingID create(@NotNull String transport, @NotNull SOAPVersion soapVersion) throws WebServiceException {
      return null;
   }
}
