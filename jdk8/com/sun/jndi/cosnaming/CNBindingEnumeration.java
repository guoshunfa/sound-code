package com.sun.jndi.cosnaming;

import com.sun.jndi.toolkit.corba.CorbaUtils;
import java.util.Hashtable;
import java.util.NoSuchElementException;
import javax.naming.Binding;
import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.spi.NamingManager;
import org.omg.CosNaming.BindingIterator;
import org.omg.CosNaming.BindingIteratorHolder;
import org.omg.CosNaming.BindingListHolder;
import org.omg.CosNaming.NameComponent;

final class CNBindingEnumeration implements NamingEnumeration<Binding> {
   private static final int DEFAULT_BATCHSIZE = 100;
   private BindingListHolder _bindingList;
   private BindingIterator _bindingIter;
   private int counter;
   private int batchsize = 100;
   private CNCtx _ctx;
   private Hashtable<?, ?> _env;
   private boolean more = false;
   private boolean isLookedUpCtx = false;

   CNBindingEnumeration(CNCtx var1, boolean var2, Hashtable<?, ?> var3) {
      String var4 = var3 != null ? (String)var3.get("java.naming.batchsize") : null;
      if (var4 != null) {
         try {
            this.batchsize = Integer.parseInt(var4);
         } catch (NumberFormatException var6) {
            throw new IllegalArgumentException("Batch size not numeric: " + var4);
         }
      }

      this._ctx = var1;
      this._ctx.incEnumCount();
      this.isLookedUpCtx = var2;
      this._env = var3;
      this._bindingList = new BindingListHolder();
      BindingIteratorHolder var5 = new BindingIteratorHolder();
      this._ctx._nc.list(0, this._bindingList, var5);
      this._bindingIter = var5.value;
      if (this._bindingIter != null) {
         this.more = this._bindingIter.next_n(this.batchsize, this._bindingList);
      } else {
         this.more = false;
      }

      this.counter = 0;
   }

   public Binding next() throws NamingException {
      if (this.more && this.counter >= this._bindingList.value.length) {
         this.getMore();
      }

      if (this.more && this.counter < this._bindingList.value.length) {
         org.omg.CosNaming.Binding var1 = this._bindingList.value[this.counter];
         ++this.counter;
         return this.mapBinding(var1);
      } else {
         throw new NoSuchElementException();
      }
   }

   public boolean hasMore() throws NamingException {
      return this.more ? this.counter < this._bindingList.value.length || this.getMore() : false;
   }

   public boolean hasMoreElements() {
      try {
         return this.hasMore();
      } catch (NamingException var2) {
         return false;
      }
   }

   public Binding nextElement() {
      try {
         return this.next();
      } catch (NamingException var2) {
         throw new NoSuchElementException();
      }
   }

   public void close() throws NamingException {
      this.more = false;
      if (this._bindingIter != null) {
         this._bindingIter.destroy();
         this._bindingIter = null;
      }

      if (this._ctx != null) {
         this._ctx.decEnumCount();
         if (this.isLookedUpCtx) {
            this._ctx.close();
         }

         this._ctx = null;
      }

   }

   protected void finalize() {
      try {
         this.close();
      } catch (NamingException var2) {
      }

   }

   private boolean getMore() throws NamingException {
      try {
         this.more = this._bindingIter.next_n(this.batchsize, this._bindingList);
         this.counter = 0;
      } catch (Exception var3) {
         this.more = false;
         NamingException var2 = new NamingException("Problem getting binding list");
         var2.setRootCause(var3);
         throw var2;
      }

      return this.more;
   }

   private Binding mapBinding(org.omg.CosNaming.Binding var1) throws NamingException {
      Object var2 = this._ctx.callResolve(var1.binding_name);
      Name var3 = CNNameParser.cosNameToName(var1.binding_name);

      try {
         if (CorbaUtils.isObjectFactoryTrusted(var2)) {
            var2 = NamingManager.getObjectInstance(var2, var3, this._ctx, this._env);
         }
      } catch (NamingException var8) {
         throw var8;
      } catch (Exception var9) {
         NamingException var5 = new NamingException("problem generating object using object factory");
         var5.setRootCause(var9);
         throw var5;
      }

      String var4 = var3.toString();
      Binding var10 = new Binding(var4, var2);
      NameComponent[] var6 = this._ctx.makeFullName(var1.binding_name);
      String var7 = CNNameParser.cosNameToInsString(var6);
      var10.setNameInNamespace(var7);
      return var10;
   }
}
