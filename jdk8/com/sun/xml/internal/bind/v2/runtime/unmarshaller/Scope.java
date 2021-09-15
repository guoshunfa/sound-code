package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;
import org.xml.sax.SAXException;

public final class Scope<BeanT, PropT, ItemT, PackT> {
   public final UnmarshallingContext context;
   private BeanT bean;
   private Accessor<BeanT, PropT> acc;
   private PackT pack;
   private Lister<BeanT, PropT, ItemT, PackT> lister;

   Scope(UnmarshallingContext context) {
      this.context = context;
   }

   public boolean hasStarted() {
      return this.bean != null;
   }

   public void reset() {
      if (this.bean == null) {
         assert this.clean();

      } else {
         this.bean = null;
         this.acc = null;
         this.pack = null;
         this.lister = null;
      }
   }

   public void finish() throws AccessorException {
      if (this.hasStarted()) {
         this.lister.endPacking(this.pack, this.bean, this.acc);
         this.reset();
      }

      assert this.clean();

   }

   private boolean clean() {
      return this.bean == null && this.acc == null && this.pack == null && this.lister == null;
   }

   public void add(Accessor<BeanT, PropT> acc, Lister<BeanT, PropT, ItemT, PackT> lister, ItemT value) throws SAXException {
      try {
         if (!this.hasStarted()) {
            this.bean = this.context.getCurrentState().getTarget();
            this.acc = acc;
            this.lister = lister;
            this.pack = lister.startPacking(this.bean, acc);
         }

         lister.addToPack(this.pack, value);
      } catch (AccessorException var5) {
         Loader.handleGenericException(var5, true);
         this.lister = Lister.getErrorInstance();
         this.acc = Accessor.getErrorInstance();
      }

   }

   public void start(Accessor<BeanT, PropT> acc, Lister<BeanT, PropT, ItemT, PackT> lister) throws SAXException {
      try {
         if (!this.hasStarted()) {
            this.bean = this.context.getCurrentState().getTarget();
            this.acc = acc;
            this.lister = lister;
            this.pack = lister.startPacking(this.bean, acc);
         }
      } catch (AccessorException var4) {
         Loader.handleGenericException(var4, true);
         this.lister = Lister.getErrorInstance();
         this.acc = Accessor.getErrorInstance();
      }

   }
}
