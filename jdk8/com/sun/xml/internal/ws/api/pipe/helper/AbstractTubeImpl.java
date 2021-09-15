package com.sun.xml.internal.ws.api.pipe.helper;

import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Fiber;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.xml.internal.ws.api.pipe.Pipe;
import com.sun.xml.internal.ws.api.pipe.PipeCloner;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;

public abstract class AbstractTubeImpl implements Tube, Pipe {
   protected AbstractTubeImpl() {
   }

   protected AbstractTubeImpl(AbstractTubeImpl that, TubeCloner cloner) {
      cloner.add(that, this);
   }

   protected final NextAction doInvoke(Tube next, Packet packet) {
      NextAction na = new NextAction();
      na.invoke(next, packet);
      return na;
   }

   protected final NextAction doInvokeAndForget(Tube next, Packet packet) {
      NextAction na = new NextAction();
      na.invokeAndForget(next, packet);
      return na;
   }

   protected final NextAction doReturnWith(Packet response) {
      NextAction na = new NextAction();
      na.returnWith(response);
      return na;
   }

   protected final NextAction doThrow(Packet response, Throwable t) {
      NextAction na = new NextAction();
      na.throwException(response, t);
      return na;
   }

   /** @deprecated */
   @Deprecated
   protected final NextAction doSuspend() {
      NextAction na = new NextAction();
      na.suspend();
      return na;
   }

   protected final NextAction doSuspend(Runnable onExitRunnable) {
      NextAction na = new NextAction();
      na.suspend(onExitRunnable);
      return na;
   }

   /** @deprecated */
   @Deprecated
   protected final NextAction doSuspend(Tube next) {
      NextAction na = new NextAction();
      na.suspend(next);
      return na;
   }

   protected final NextAction doSuspend(Tube next, Runnable onExitRunnable) {
      NextAction na = new NextAction();
      na.suspend(next, onExitRunnable);
      return na;
   }

   protected final NextAction doThrow(Throwable t) {
      NextAction na = new NextAction();
      na.throwException(t);
      return na;
   }

   public Packet process(Packet p) {
      return Fiber.current().runSync(this, p);
   }

   public final AbstractTubeImpl copy(PipeCloner cloner) {
      return this.copy((TubeCloner)cloner);
   }

   public abstract AbstractTubeImpl copy(TubeCloner var1);
}
