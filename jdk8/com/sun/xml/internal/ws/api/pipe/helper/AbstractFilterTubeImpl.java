package com.sun.xml.internal.ws.api.pipe.helper;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;

public abstract class AbstractFilterTubeImpl extends AbstractTubeImpl {
   protected final Tube next;

   protected AbstractFilterTubeImpl(Tube next) {
      this.next = next;
   }

   protected AbstractFilterTubeImpl(AbstractFilterTubeImpl that, TubeCloner cloner) {
      super(that, cloner);
      if (that.next != null) {
         this.next = cloner.copy(that.next);
      } else {
         this.next = null;
      }

   }

   @NotNull
   public NextAction processRequest(Packet request) {
      return this.doInvoke(this.next, request);
   }

   @NotNull
   public NextAction processResponse(Packet response) {
      return this.doReturnWith(response);
   }

   @NotNull
   public NextAction processException(Throwable t) {
      return this.doThrow(t);
   }

   public void preDestroy() {
      if (this.next != null) {
         this.next.preDestroy();
      }

   }
}
