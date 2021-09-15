package com.sun.xml.internal.ws.client.sei;

import com.sun.xml.internal.ws.model.ParameterImpl;
import com.sun.xml.internal.ws.spi.db.PropertyAccessor;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.ws.Holder;
import javax.xml.ws.WebServiceException;

public abstract class ValueSetter {
   private static final ValueSetter RETURN_VALUE = new ValueSetter.ReturnValue();
   private static final ValueSetter[] POOL = new ValueSetter[16];
   static final ValueSetter SINGLE_VALUE;

   private ValueSetter() {
   }

   abstract Object put(Object var1, Object[] var2);

   static ValueSetter getSync(ParameterImpl p) {
      int idx = p.getIndex();
      if (idx == -1) {
         return RETURN_VALUE;
      } else {
         return (ValueSetter)(idx < POOL.length ? POOL[idx] : new ValueSetter.Param(idx));
      }
   }

   // $FF: synthetic method
   ValueSetter(Object x0) {
      this();
   }

   static {
      for(int i = 0; i < POOL.length; ++i) {
         POOL[i] = new ValueSetter.Param(i);
      }

      SINGLE_VALUE = new ValueSetter.SingleValue();
   }

   static final class AsyncBeanValueSetter extends ValueSetter {
      private final PropertyAccessor accessor;

      AsyncBeanValueSetter(ParameterImpl p, Class wrapper) {
         super(null);
         QName name = p.getName();

         try {
            this.accessor = p.getOwner().getBindingContext().getElementPropertyAccessor(wrapper, name.getNamespaceURI(), name.getLocalPart());
         } catch (JAXBException var5) {
            throw new WebServiceException(wrapper + " do not have a property of the name " + name, var5);
         }
      }

      Object put(Object obj, Object[] args) {
         assert args != null;

         assert args.length == 1;

         assert args[0] != null;

         Object bean = args[0];

         try {
            this.accessor.set(bean, obj);
            return null;
         } catch (Exception var5) {
            throw new WebServiceException(var5);
         }
      }
   }

   private static final class SingleValue extends ValueSetter {
      private SingleValue() {
         super(null);
      }

      Object put(Object obj, Object[] args) {
         args[0] = obj;
         return null;
      }

      // $FF: synthetic method
      SingleValue(Object x0) {
         this();
      }
   }

   static final class Param extends ValueSetter {
      private final int idx;

      public Param(int idx) {
         super(null);
         this.idx = idx;
      }

      Object put(Object obj, Object[] args) {
         Object arg = args[this.idx];
         if (arg != null) {
            assert arg instanceof Holder;

            ((Holder)arg).value = obj;
         }

         return null;
      }
   }

   private static final class ReturnValue extends ValueSetter {
      private ReturnValue() {
         super(null);
      }

      Object put(Object obj, Object[] args) {
         return obj;
      }

      // $FF: synthetic method
      ReturnValue(Object x0) {
         this();
      }
   }
}
