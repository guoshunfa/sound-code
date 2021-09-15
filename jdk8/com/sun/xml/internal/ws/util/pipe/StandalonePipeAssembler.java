package com.sun.xml.internal.ws.util.pipe;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.pipe.ClientPipeAssemblerContext;
import com.sun.xml.internal.ws.api.pipe.Pipe;
import com.sun.xml.internal.ws.api.pipe.PipelineAssembler;
import com.sun.xml.internal.ws.api.pipe.ServerPipeAssemblerContext;

public class StandalonePipeAssembler implements PipelineAssembler {
   private static final boolean dump;

   @NotNull
   public Pipe createClient(ClientPipeAssemblerContext context) {
      Pipe head = context.createTransportPipe();
      head = context.createSecurityPipe(head);
      if (dump) {
         head = context.createDumpPipe("client", System.out, head);
      }

      head = context.createWsaPipe(head);
      head = context.createClientMUPipe(head);
      return context.createHandlerPipe(head);
   }

   public Pipe createServer(ServerPipeAssemblerContext context) {
      Pipe head = context.getTerminalPipe();
      head = context.createHandlerPipe(head);
      head = context.createMonitoringPipe(head);
      head = context.createServerMUPipe(head);
      head = context.createWsaPipe(head);
      head = context.createSecurityPipe(head);
      return head;
   }

   static {
      boolean b = false;

      try {
         b = Boolean.getBoolean(StandalonePipeAssembler.class.getName() + ".dump");
      } catch (Throwable var2) {
      }

      dump = b;
   }
}
