package com.sun.xml.internal.ws.util;

import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

public abstract class Pool<T> {
   private volatile WeakReference<ConcurrentLinkedQueue<T>> queue;

   public final T take() {
      T t = this.getQueue().poll();
      return t == null ? this.create() : t;
   }

   private ConcurrentLinkedQueue<T> getQueue() {
      WeakReference<ConcurrentLinkedQueue<T>> q = this.queue;
      ConcurrentLinkedQueue d;
      if (q != null) {
         d = (ConcurrentLinkedQueue)q.get();
         if (d != null) {
            return d;
         }
      }

      d = new ConcurrentLinkedQueue();
      this.queue = new WeakReference(d);
      return d;
   }

   public final void recycle(T t) {
      this.getQueue().offer(t);
   }

   protected abstract T create();

   public static final class TubePool extends Pool<Tube> {
      private final Tube master;

      public TubePool(Tube master) {
         this.master = master;
         this.recycle(master);
      }

      protected Tube create() {
         return TubeCloner.clone(this.master);
      }

      /** @deprecated */
      @Deprecated
      public final Tube takeMaster() {
         return this.master;
      }
   }

   public static final class Unmarshaller extends Pool<javax.xml.bind.Unmarshaller> {
      private final JAXBContext context;

      public Unmarshaller(JAXBContext context) {
         this.context = context;
      }

      protected javax.xml.bind.Unmarshaller create() {
         try {
            return this.context.createUnmarshaller();
         } catch (JAXBException var2) {
            throw new AssertionError(var2);
         }
      }
   }

   public static final class Marshaller extends Pool<javax.xml.bind.Marshaller> {
      private final JAXBContext context;

      public Marshaller(JAXBContext context) {
         this.context = context;
      }

      protected javax.xml.bind.Marshaller create() {
         try {
            return this.context.createMarshaller();
         } catch (JAXBException var2) {
            throw new AssertionError(var2);
         }
      }
   }
}
