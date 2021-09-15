package com.sun.jndi.toolkit.ctx;

import javax.naming.Binding;
import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.LinkRef;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ResolveResult;

public abstract class ComponentContext extends PartialCompositeContext {
   private static int debug = 0;
   protected static final byte USE_CONTINUATION = 1;
   protected static final byte TERMINAL_COMPONENT = 2;
   protected static final byte TERMINAL_NNS_COMPONENT = 3;

   protected ComponentContext() {
      this._contextType = 2;
   }

   protected abstract Object c_lookup(Name var1, Continuation var2) throws NamingException;

   protected abstract Object c_lookupLink(Name var1, Continuation var2) throws NamingException;

   protected abstract NamingEnumeration<NameClassPair> c_list(Name var1, Continuation var2) throws NamingException;

   protected abstract NamingEnumeration<Binding> c_listBindings(Name var1, Continuation var2) throws NamingException;

   protected abstract void c_bind(Name var1, Object var2, Continuation var3) throws NamingException;

   protected abstract void c_rebind(Name var1, Object var2, Continuation var3) throws NamingException;

   protected abstract void c_unbind(Name var1, Continuation var2) throws NamingException;

   protected abstract void c_destroySubcontext(Name var1, Continuation var2) throws NamingException;

   protected abstract Context c_createSubcontext(Name var1, Continuation var2) throws NamingException;

   protected abstract void c_rename(Name var1, Name var2, Continuation var3) throws NamingException;

   protected abstract NameParser c_getNameParser(Name var1, Continuation var2) throws NamingException;

   protected HeadTail p_parseComponent(Name var1, Continuation var2) throws NamingException {
      byte var3;
      if (!var1.isEmpty() && !var1.get(0).equals("")) {
         var3 = 1;
      } else {
         var3 = 0;
      }

      Name var4;
      Name var5;
      if (var1 instanceof CompositeName) {
         var4 = var1.getPrefix(var3);
         var5 = var1.getSuffix(var3);
      } else {
         var4 = (new CompositeName()).add(var1.toString());
         var5 = null;
      }

      if (debug > 2) {
         System.err.println("ORIG: " + var1);
         System.err.println("PREFIX: " + var1);
         System.err.println("SUFFIX: " + null);
      }

      return new HeadTail(var4, var5);
   }

   protected Object c_resolveIntermediate_nns(Name var1, Continuation var2) throws NamingException {
      try {
         final Object var3 = this.c_lookup(var1, var2);
         if (var3 != null && this.getClass().isInstance(var3)) {
            var2.setContinueNNS(var3, (Name)var1, this);
            return null;
         } else if (var3 != null && !(var3 instanceof Context)) {
            RefAddr var4 = new RefAddr("nns") {
               private static final long serialVersionUID = -8831204798861786362L;

               public Object getContent() {
                  return var3;
               }
            };
            Reference var5 = new Reference("java.lang.Object", var4);
            CompositeName var6 = (CompositeName)var1.clone();
            var6.add("");
            var2.setContinue(var5, var6, this);
            return null;
         } else {
            return var3;
         }
      } catch (NamingException var7) {
         var7.appendRemainingComponent("");
         throw var7;
      }
   }

   protected Object c_lookup_nns(Name var1, Continuation var2) throws NamingException {
      this.c_processJunction_nns(var1, var2);
      return null;
   }

   protected Object c_lookupLink_nns(Name var1, Continuation var2) throws NamingException {
      this.c_processJunction_nns(var1, var2);
      return null;
   }

   protected NamingEnumeration<NameClassPair> c_list_nns(Name var1, Continuation var2) throws NamingException {
      this.c_processJunction_nns(var1, var2);
      return null;
   }

   protected NamingEnumeration<Binding> c_listBindings_nns(Name var1, Continuation var2) throws NamingException {
      this.c_processJunction_nns(var1, var2);
      return null;
   }

   protected void c_bind_nns(Name var1, Object var2, Continuation var3) throws NamingException {
      this.c_processJunction_nns(var1, var3);
   }

   protected void c_rebind_nns(Name var1, Object var2, Continuation var3) throws NamingException {
      this.c_processJunction_nns(var1, var3);
   }

   protected void c_unbind_nns(Name var1, Continuation var2) throws NamingException {
      this.c_processJunction_nns(var1, var2);
   }

   protected Context c_createSubcontext_nns(Name var1, Continuation var2) throws NamingException {
      this.c_processJunction_nns(var1, var2);
      return null;
   }

   protected void c_destroySubcontext_nns(Name var1, Continuation var2) throws NamingException {
      this.c_processJunction_nns(var1, var2);
   }

   protected void c_rename_nns(Name var1, Name var2, Continuation var3) throws NamingException {
      this.c_processJunction_nns(var1, var3);
   }

   protected NameParser c_getNameParser_nns(Name var1, Continuation var2) throws NamingException {
      this.c_processJunction_nns(var1, var2);
      return null;
   }

   protected void c_processJunction_nns(Name var1, Continuation var2) throws NamingException {
      if (var1.isEmpty()) {
         RefAddr var6 = new RefAddr("nns") {
            private static final long serialVersionUID = -1389472957988053402L;

            public Object getContent() {
               return ComponentContext.this;
            }
         };
         Reference var4 = new Reference("java.lang.Object", var6);
         var2.setContinue(var4, _NNS_NAME, this);
      } else {
         try {
            Object var3 = this.c_lookup(var1, var2);
            if (var2.isContinue()) {
               var2.appendRemainingComponent("");
            } else {
               var2.setContinueNNS(var3, (Name)var1, this);
            }

         } catch (NamingException var5) {
            var5.appendRemainingComponent("");
            throw var5;
         }
      }
   }

   protected HeadTail p_resolveIntermediate(Name var1, Continuation var2) throws NamingException {
      byte var3 = 1;
      var2.setSuccess();
      HeadTail var4 = this.p_parseComponent(var1, var2);
      Name var5 = var4.getTail();
      Name var6 = var4.getHead();
      if (var5 != null && !var5.isEmpty()) {
         Object var7;
         if (!var5.get(0).equals("")) {
            try {
               var7 = this.c_resolveIntermediate_nns(var6, var2);
               if (var7 != null) {
                  var2.setContinue(var7, (Name)var6, this, (Name)var5);
               } else if (var2.isContinue()) {
                  this.checkAndAdjustRemainingName(var2.getRemainingName());
                  var2.appendRemainingName(var5);
               }
            } catch (NamingException var11) {
               this.checkAndAdjustRemainingName(var11.getRemainingName());
               var11.appendRemainingName(var5);
               throw var11;
            }
         } else if (var5.size() == 1) {
            var3 = 3;
         } else if (!var6.isEmpty() && !this.isAllEmpty(var5)) {
            try {
               var7 = this.c_resolveIntermediate_nns(var6, var2);
               if (var7 != null) {
                  var2.setContinue(var7, (Name)var6, this, (Name)var5);
               } else if (var2.isContinue()) {
                  this.checkAndAdjustRemainingName(var2.getRemainingName());
                  var2.appendRemainingName(var5);
               }
            } catch (NamingException var9) {
               this.checkAndAdjustRemainingName(var9.getRemainingName());
               var9.appendRemainingName(var5);
               throw var9;
            }
         } else {
            Name var12 = var5.getSuffix(1);

            try {
               Object var8 = this.c_lookup_nns(var6, var2);
               if (var8 != null) {
                  var2.setContinue(var8, (Name)var6, this, (Name)var12);
               } else if (var2.isContinue()) {
                  var2.appendRemainingName(var12);
               }
            } catch (NamingException var10) {
               var10.appendRemainingName(var12);
               throw var10;
            }
         }
      } else {
         var3 = 2;
      }

      var4.setStatus(var3);
      return var4;
   }

   void checkAndAdjustRemainingName(Name var1) throws InvalidNameException {
      int var2;
      if (var1 != null && (var2 = var1.size()) > 1 && var1.get(var2 - 1).equals("")) {
         var1.remove(var2 - 1);
      }

   }

   protected boolean isAllEmpty(Name var1) {
      int var2 = var1.size();

      for(int var3 = 0; var3 < var2; ++var3) {
         if (!var1.get(var3).equals("")) {
            return false;
         }
      }

      return true;
   }

   protected ResolveResult p_resolveToClass(Name var1, Class<?> var2, Continuation var3) throws NamingException {
      if (var2.isInstance(this)) {
         var3.setSuccess();
         return new ResolveResult(this, var1);
      } else {
         ResolveResult var4 = null;
         HeadTail var5 = this.p_resolveIntermediate(var1, var3);
         switch(var5.getStatus()) {
         case 2:
            var3.setSuccess();
            break;
         case 3:
            Object var6 = this.p_lookup(var1, var3);
            if (!var3.isContinue() && var2.isInstance(var6)) {
               var4 = new ResolveResult(var6, _EMPTY_NAME);
            }
         }

         return var4;
      }
   }

   protected Object p_lookup(Name var1, Continuation var2) throws NamingException {
      Object var3 = null;
      HeadTail var4 = this.p_resolveIntermediate(var1, var2);
      switch(var4.getStatus()) {
      case 2:
         var3 = this.c_lookup(var4.getHead(), var2);
         if (var3 instanceof LinkRef) {
            var2.setContinue(var3, var4.getHead(), this);
            var3 = null;
         }
         break;
      case 3:
         var3 = this.c_lookup_nns(var4.getHead(), var2);
         if (var3 instanceof LinkRef) {
            var2.setContinue(var3, var4.getHead(), this);
            var3 = null;
         }
      }

      return var3;
   }

   protected NamingEnumeration<NameClassPair> p_list(Name var1, Continuation var2) throws NamingException {
      NamingEnumeration var3 = null;
      HeadTail var4 = this.p_resolveIntermediate(var1, var2);
      switch(var4.getStatus()) {
      case 2:
         if (debug > 0) {
            System.out.println("c_list(" + var4.getHead() + ")");
         }

         var3 = this.c_list(var4.getHead(), var2);
         break;
      case 3:
         if (debug > 0) {
            System.out.println("c_list_nns(" + var4.getHead() + ")");
         }

         var3 = this.c_list_nns(var4.getHead(), var2);
      }

      return var3;
   }

   protected NamingEnumeration<Binding> p_listBindings(Name var1, Continuation var2) throws NamingException {
      NamingEnumeration var3 = null;
      HeadTail var4 = this.p_resolveIntermediate(var1, var2);
      switch(var4.getStatus()) {
      case 2:
         var3 = this.c_listBindings(var4.getHead(), var2);
         break;
      case 3:
         var3 = this.c_listBindings_nns(var4.getHead(), var2);
      }

      return var3;
   }

   protected void p_bind(Name var1, Object var2, Continuation var3) throws NamingException {
      HeadTail var4 = this.p_resolveIntermediate(var1, var3);
      switch(var4.getStatus()) {
      case 2:
         this.c_bind(var4.getHead(), var2, var3);
         break;
      case 3:
         this.c_bind_nns(var4.getHead(), var2, var3);
      }

   }

   protected void p_rebind(Name var1, Object var2, Continuation var3) throws NamingException {
      HeadTail var4 = this.p_resolveIntermediate(var1, var3);
      switch(var4.getStatus()) {
      case 2:
         this.c_rebind(var4.getHead(), var2, var3);
         break;
      case 3:
         this.c_rebind_nns(var4.getHead(), var2, var3);
      }

   }

   protected void p_unbind(Name var1, Continuation var2) throws NamingException {
      HeadTail var3 = this.p_resolveIntermediate(var1, var2);
      switch(var3.getStatus()) {
      case 2:
         this.c_unbind(var3.getHead(), var2);
         break;
      case 3:
         this.c_unbind_nns(var3.getHead(), var2);
      }

   }

   protected void p_destroySubcontext(Name var1, Continuation var2) throws NamingException {
      HeadTail var3 = this.p_resolveIntermediate(var1, var2);
      switch(var3.getStatus()) {
      case 2:
         this.c_destroySubcontext(var3.getHead(), var2);
         break;
      case 3:
         this.c_destroySubcontext_nns(var3.getHead(), var2);
      }

   }

   protected Context p_createSubcontext(Name var1, Continuation var2) throws NamingException {
      Context var3 = null;
      HeadTail var4 = this.p_resolveIntermediate(var1, var2);
      switch(var4.getStatus()) {
      case 2:
         var3 = this.c_createSubcontext(var4.getHead(), var2);
         break;
      case 3:
         var3 = this.c_createSubcontext_nns(var4.getHead(), var2);
      }

      return var3;
   }

   protected void p_rename(Name var1, Name var2, Continuation var3) throws NamingException {
      HeadTail var4 = this.p_resolveIntermediate(var1, var3);
      switch(var4.getStatus()) {
      case 2:
         this.c_rename(var4.getHead(), var2, var3);
         break;
      case 3:
         this.c_rename_nns(var4.getHead(), var2, var3);
      }

   }

   protected NameParser p_getNameParser(Name var1, Continuation var2) throws NamingException {
      NameParser var3 = null;
      HeadTail var4 = this.p_resolveIntermediate(var1, var2);
      switch(var4.getStatus()) {
      case 2:
         var3 = this.c_getNameParser(var4.getHead(), var2);
         break;
      case 3:
         var3 = this.c_getNameParser_nns(var4.getHead(), var2);
      }

      return var3;
   }

   protected Object p_lookupLink(Name var1, Continuation var2) throws NamingException {
      Object var3 = null;
      HeadTail var4 = this.p_resolveIntermediate(var1, var2);
      switch(var4.getStatus()) {
      case 2:
         var3 = this.c_lookupLink(var4.getHead(), var2);
         break;
      case 3:
         var3 = this.c_lookupLink_nns(var4.getHead(), var2);
      }

      return var3;
   }
}
