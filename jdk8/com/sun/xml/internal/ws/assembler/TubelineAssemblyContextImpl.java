package com.sun.xml.internal.ws.assembler;

import com.sun.istack.internal.logging.Logger;
import com.sun.xml.internal.ws.api.pipe.Pipe;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.helper.PipeAdapter;
import com.sun.xml.internal.ws.assembler.dev.TubelineAssemblyContext;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

class TubelineAssemblyContextImpl implements TubelineAssemblyContext {
   private static final Logger LOGGER = Logger.getLogger(TubelineAssemblyContextImpl.class);
   private Tube head;
   private Pipe adaptedHead;
   private List<Tube> tubes = new LinkedList();

   public Tube getTubelineHead() {
      return this.head;
   }

   public Pipe getAdaptedTubelineHead() {
      if (this.adaptedHead == null) {
         this.adaptedHead = PipeAdapter.adapt(this.head);
      }

      return this.adaptedHead;
   }

   boolean setTubelineHead(Tube newHead) {
      if (newHead != this.head && newHead != this.adaptedHead) {
         this.head = newHead;
         this.tubes.add(this.head);
         this.adaptedHead = null;
         if (LOGGER.isLoggable(Level.FINER)) {
            LOGGER.finer(MessageFormat.format("Added '{0}' tube instance to the tubeline.", newHead == null ? null : newHead.getClass().getName()));
         }

         return true;
      } else {
         return false;
      }
   }

   public <T> T getImplementation(Class<T> type) {
      Iterator var2 = this.tubes.iterator();

      Tube tube;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         tube = (Tube)var2.next();
      } while(!type.isInstance(tube));

      return type.cast(tube);
   }
}
