package com.sun.xml.internal.ws.api.pipe;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.helper.PipeAdapter;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import java.io.PrintStream;

/** @deprecated */
public final class ServerPipeAssemblerContext extends ServerTubeAssemblerContext {
   public ServerPipeAssemblerContext(@Nullable SEIModel seiModel, @Nullable WSDLPort wsdlModel, @NotNull WSEndpoint endpoint, @NotNull Tube terminal, boolean isSynchronous) {
      super(seiModel, wsdlModel, endpoint, terminal, isSynchronous);
   }

   @NotNull
   public Pipe createServerMUPipe(@NotNull Pipe next) {
      return PipeAdapter.adapt(super.createServerMUTube(PipeAdapter.adapt(next)));
   }

   public Pipe createDumpPipe(String name, PrintStream out, Pipe next) {
      return PipeAdapter.adapt(super.createDumpTube(name, out, PipeAdapter.adapt(next)));
   }

   @NotNull
   public Pipe createMonitoringPipe(@NotNull Pipe next) {
      return PipeAdapter.adapt(super.createMonitoringTube(PipeAdapter.adapt(next)));
   }

   @NotNull
   public Pipe createSecurityPipe(@NotNull Pipe next) {
      return PipeAdapter.adapt(super.createSecurityTube(PipeAdapter.adapt(next)));
   }

   @NotNull
   public Pipe createValidationPipe(@NotNull Pipe next) {
      return PipeAdapter.adapt(super.createValidationTube(PipeAdapter.adapt(next)));
   }

   @NotNull
   public Pipe createHandlerPipe(@NotNull Pipe next) {
      return PipeAdapter.adapt(super.createHandlerTube(PipeAdapter.adapt(next)));
   }

   @NotNull
   public Pipe getTerminalPipe() {
      return PipeAdapter.adapt(super.getTerminalTube());
   }

   public Pipe createWsaPipe(Pipe next) {
      return PipeAdapter.adapt(super.createWsaTube(PipeAdapter.adapt(next)));
   }
}
