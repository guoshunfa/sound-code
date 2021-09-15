package com.sun.corba.se.impl.ior;

import com.sun.corba.se.spi.ior.MakeImmutable;
import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;

public class FreezableList extends AbstractList {
   private List delegate;
   private boolean immutable;

   public boolean equals(Object var1) {
      if (var1 == null) {
         return false;
      } else if (!(var1 instanceof FreezableList)) {
         return false;
      } else {
         FreezableList var2 = (FreezableList)var1;
         return this.delegate.equals(var2.delegate) && this.immutable == var2.immutable;
      }
   }

   public int hashCode() {
      return this.delegate.hashCode();
   }

   public FreezableList(List var1, boolean var2) {
      this.delegate = null;
      this.immutable = false;
      this.delegate = var1;
      this.immutable = var2;
   }

   public FreezableList(List var1) {
      this(var1, false);
   }

   public void makeImmutable() {
      this.immutable = true;
   }

   public boolean isImmutable() {
      return this.immutable;
   }

   public void makeElementsImmutable() {
      Iterator var1 = this.iterator();

      while(var1.hasNext()) {
         Object var2 = var1.next();
         if (var2 instanceof MakeImmutable) {
            MakeImmutable var3 = (MakeImmutable)var2;
            var3.makeImmutable();
         }
      }

   }

   public int size() {
      return this.delegate.size();
   }

   public Object get(int var1) {
      return this.delegate.get(var1);
   }

   public Object set(int var1, Object var2) {
      if (this.immutable) {
         throw new UnsupportedOperationException();
      } else {
         return this.delegate.set(var1, var2);
      }
   }

   public void add(int var1, Object var2) {
      if (this.immutable) {
         throw new UnsupportedOperationException();
      } else {
         this.delegate.add(var1, var2);
      }
   }

   public Object remove(int var1) {
      if (this.immutable) {
         throw new UnsupportedOperationException();
      } else {
         return this.delegate.remove(var1);
      }
   }

   public List subList(int var1, int var2) {
      List var3 = this.delegate.subList(var1, var2);
      FreezableList var4 = new FreezableList(var3, this.immutable);
      return var4;
   }
}
