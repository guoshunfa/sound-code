package com.sun.xml.internal.ws.api.pipe;

import com.sun.istack.internal.NotNull;

public interface TubelineAssembler {
   @NotNull
   Tube createClient(@NotNull ClientTubeAssemblerContext var1);

   @NotNull
   Tube createServer(@NotNull ServerTubeAssemblerContext var1);
}
