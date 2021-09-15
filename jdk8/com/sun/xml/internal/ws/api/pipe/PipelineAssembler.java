package com.sun.xml.internal.ws.api.pipe;

import com.sun.istack.internal.NotNull;

public interface PipelineAssembler {
   @NotNull
   Pipe createClient(@NotNull ClientPipeAssemblerContext var1);

   @NotNull
   Pipe createServer(@NotNull ServerPipeAssemblerContext var1);
}
