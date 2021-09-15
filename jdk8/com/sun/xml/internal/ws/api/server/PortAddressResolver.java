package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import javax.xml.namespace.QName;

public abstract class PortAddressResolver {
   @Nullable
   public abstract String getAddressFor(@NotNull QName var1, @NotNull String var2);

   @Nullable
   public String getAddressFor(@NotNull QName serviceName, @NotNull String portName, String currentAddress) {
      return this.getAddressFor(serviceName, portName);
   }
}
