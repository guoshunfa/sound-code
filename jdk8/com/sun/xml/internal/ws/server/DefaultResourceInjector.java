package com.sun.xml.internal.ws.server;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.server.ResourceInjector;
import com.sun.xml.internal.ws.api.server.WSWebServiceContext;
import com.sun.xml.internal.ws.util.InjectionPlan;
import javax.xml.ws.WebServiceContext;

public final class DefaultResourceInjector extends ResourceInjector {
   public void inject(@NotNull WSWebServiceContext context, @NotNull Object instance) {
      InjectionPlan.buildInjectionPlan(instance.getClass(), WebServiceContext.class, false).inject(instance, (Object)context);
   }
}
