package com.sun.xml.internal.ws.api.pipe;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.pipe.helper.PipeAdapter;

public abstract class TransportPipeFactory {
   public abstract Pipe doCreate(@NotNull ClientPipeAssemblerContext var1);

   /** @deprecated */
   public static Pipe create(@Nullable ClassLoader classLoader, @NotNull ClientPipeAssemblerContext context) {
      return PipeAdapter.adapt(TransportTubeFactory.create(classLoader, context));
   }
}
