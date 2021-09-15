package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.NotNull;

public abstract class ContainerResolver {
   private static final ThreadLocalContainerResolver DEFAULT = new ThreadLocalContainerResolver();
   private static volatile ContainerResolver theResolver;

   public static void setInstance(ContainerResolver resolver) {
      if (resolver == null) {
         resolver = DEFAULT;
      }

      theResolver = (ContainerResolver)resolver;
   }

   @NotNull
   public static ContainerResolver getInstance() {
      return theResolver;
   }

   public static ThreadLocalContainerResolver getDefault() {
      return DEFAULT;
   }

   @NotNull
   public abstract Container getContainer();

   static {
      theResolver = DEFAULT;
   }
}
