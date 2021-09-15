package com.sun.xml.internal.ws.db.glassfish;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.api.RawAccessor;
import com.sun.xml.internal.ws.spi.db.DatabindingException;
import com.sun.xml.internal.ws.spi.db.PropertyAccessor;

public class RawAccessorWrapper implements PropertyAccessor {
   private RawAccessor accessor;

   public RawAccessorWrapper(RawAccessor a) {
      this.accessor = a;
   }

   public boolean equals(Object obj) {
      return this.accessor.equals(obj);
   }

   public Object get(Object bean) throws DatabindingException {
      try {
         return this.accessor.get(bean);
      } catch (AccessorException var3) {
         throw new DatabindingException(var3);
      }
   }

   public int hashCode() {
      return this.accessor.hashCode();
   }

   public void set(Object bean, Object value) throws DatabindingException {
      try {
         this.accessor.set(bean, value);
      } catch (AccessorException var4) {
         throw new DatabindingException(var4);
      }
   }

   public String toString() {
      return this.accessor.toString();
   }
}
