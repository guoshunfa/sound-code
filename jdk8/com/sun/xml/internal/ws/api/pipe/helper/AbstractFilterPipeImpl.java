package com.sun.xml.internal.ws.api.pipe.helper;

import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Pipe;
import com.sun.xml.internal.ws.api.pipe.PipeCloner;

public abstract class AbstractFilterPipeImpl extends AbstractPipeImpl {
   protected final Pipe next;

   protected AbstractFilterPipeImpl(Pipe next) {
      this.next = next;

      assert next != null;

   }

   protected AbstractFilterPipeImpl(AbstractFilterPipeImpl that, PipeCloner cloner) {
      super(that, cloner);
      this.next = cloner.copy(that.next);

      assert this.next != null;

   }

   public Packet process(Packet packet) {
      return this.next.process(packet);
   }

   public void preDestroy() {
      this.next.preDestroy();
   }
}
