package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.server.DefaultResourceInjector;

public abstract class ResourceInjector {
   public static final ResourceInjector STANDALONE = new DefaultResourceInjector();

   public abstract void inject(@NotNull WSWebServiceContext var1, @NotNull Object var2);
}
