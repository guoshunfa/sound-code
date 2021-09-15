package com.sun.xml.internal.ws.api.pipe.helper;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Fiber;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.xml.internal.ws.api.pipe.Pipe;
import com.sun.xml.internal.ws.api.pipe.PipeCloner;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;

public class PipeAdapter extends AbstractTubeImpl {
   private final Pipe next;

   public static Tube adapt(Pipe p) {
      return (Tube)(p instanceof Tube ? (Tube)p : new PipeAdapter(p));
   }

   public static Pipe adapt(Tube p) {
      class TubeAdapter extends AbstractPipeImpl {
         private final Tube t;

         public TubeAdapter(Tube t) {
            this.t = t;
         }

         private TubeAdapter(TubeAdapter that, PipeCloner cloner) {
            super(that, cloner);
            this.t = cloner.copy(that.t);
         }

         public Packet process(Packet request) {
            return Fiber.current().runSync(this.t, request);
         }

         public Pipe copy(PipeCloner cloner) {
            return new TubeAdapter(this, cloner);
         }
      }

      return (Pipe)(p instanceof Pipe ? (Pipe)p : new TubeAdapter(p));
   }

   private PipeAdapter(Pipe next) {
      this.next = next;
   }

   private PipeAdapter(PipeAdapter that, TubeCloner cloner) {
      super(that, cloner);
      this.next = ((PipeCloner)cloner).copy(that.next);
   }

   @NotNull
   public NextAction processRequest(@NotNull Packet p) {
      return this.doReturnWith(this.next.process(p));
   }

   @NotNull
   public NextAction processResponse(@NotNull Packet p) {
      throw new IllegalStateException();
   }

   @NotNull
   public NextAction processException(@NotNull Throwable t) {
      throw new IllegalStateException();
   }

   public void preDestroy() {
      this.next.preDestroy();
   }

   public PipeAdapter copy(TubeCloner cloner) {
      return new PipeAdapter(this, cloner);
   }

   public String toString() {
      return super.toString() + "[" + this.next.toString() + "]";
   }
}
