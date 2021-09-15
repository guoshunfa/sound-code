package com.sun.xml.internal.ws.assembler.dev;

import com.sun.xml.internal.ws.api.pipe.Tube;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class TubelineAssemblyDecorator {
   public static TubelineAssemblyDecorator composite(Iterable<TubelineAssemblyDecorator> decorators) {
      return new TubelineAssemblyDecorator.CompositeTubelineAssemblyDecorator(decorators);
   }

   public Tube decorateClient(Tube tube, ClientTubelineAssemblyContext context) {
      return tube;
   }

   public Tube decorateClientHead(Tube tube, ClientTubelineAssemblyContext context) {
      return tube;
   }

   public Tube decorateClientTail(Tube tube, ClientTubelineAssemblyContext context) {
      return tube;
   }

   public Tube decorateServer(Tube tube, ServerTubelineAssemblyContext context) {
      return tube;
   }

   public Tube decorateServerTail(Tube tube, ServerTubelineAssemblyContext context) {
      return tube;
   }

   public Tube decorateServerHead(Tube tube, ServerTubelineAssemblyContext context) {
      return tube;
   }

   private static class CompositeTubelineAssemblyDecorator extends TubelineAssemblyDecorator {
      private Collection<TubelineAssemblyDecorator> decorators = new ArrayList();

      public CompositeTubelineAssemblyDecorator(Iterable<TubelineAssemblyDecorator> decorators) {
         Iterator var2 = decorators.iterator();

         while(var2.hasNext()) {
            TubelineAssemblyDecorator decorator = (TubelineAssemblyDecorator)var2.next();
            this.decorators.add(decorator);
         }

      }

      public Tube decorateClient(Tube tube, ClientTubelineAssemblyContext context) {
         TubelineAssemblyDecorator decorator;
         for(Iterator var3 = this.decorators.iterator(); var3.hasNext(); tube = decorator.decorateClient(tube, context)) {
            decorator = (TubelineAssemblyDecorator)var3.next();
         }

         return tube;
      }

      public Tube decorateClientHead(Tube tube, ClientTubelineAssemblyContext context) {
         TubelineAssemblyDecorator decorator;
         for(Iterator var3 = this.decorators.iterator(); var3.hasNext(); tube = decorator.decorateClientHead(tube, context)) {
            decorator = (TubelineAssemblyDecorator)var3.next();
         }

         return tube;
      }

      public Tube decorateClientTail(Tube tube, ClientTubelineAssemblyContext context) {
         TubelineAssemblyDecorator decorator;
         for(Iterator var3 = this.decorators.iterator(); var3.hasNext(); tube = decorator.decorateClientTail(tube, context)) {
            decorator = (TubelineAssemblyDecorator)var3.next();
         }

         return tube;
      }

      public Tube decorateServer(Tube tube, ServerTubelineAssemblyContext context) {
         TubelineAssemblyDecorator decorator;
         for(Iterator var3 = this.decorators.iterator(); var3.hasNext(); tube = decorator.decorateServer(tube, context)) {
            decorator = (TubelineAssemblyDecorator)var3.next();
         }

         return tube;
      }

      public Tube decorateServerTail(Tube tube, ServerTubelineAssemblyContext context) {
         TubelineAssemblyDecorator decorator;
         for(Iterator var3 = this.decorators.iterator(); var3.hasNext(); tube = decorator.decorateServerTail(tube, context)) {
            decorator = (TubelineAssemblyDecorator)var3.next();
         }

         return tube;
      }

      public Tube decorateServerHead(Tube tube, ServerTubelineAssemblyContext context) {
         TubelineAssemblyDecorator decorator;
         for(Iterator var3 = this.decorators.iterator(); var3.hasNext(); tube = decorator.decorateServerHead(tube, context)) {
            decorator = (TubelineAssemblyDecorator)var3.next();
         }

         return tube;
      }
   }
}
