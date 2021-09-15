package com.sun.xml.internal.ws.api.pipe;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.message.Packet;

public interface Tube {
   @NotNull
   NextAction processRequest(@NotNull Packet var1);

   @NotNull
   NextAction processResponse(@NotNull Packet var1);

   @NotNull
   NextAction processException(@NotNull Throwable var1);

   void preDestroy();

   Tube copy(TubeCloner var1);
}
