package com.sun.jndi.toolkit.ctx;

import javax.naming.Binding;
import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.LinkRef;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameNotFoundException;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.Reference;

public abstract class AtomicContext extends ComponentContext {
   private static int debug = 0;

   protected AtomicContext() {
      this._contextType = 3;
   }

   protected abstract Object a_lookup(String var1, Continuation var2) throws NamingException;

   protected abstract Object a_lookupLink(String var1, Continuation var2) throws NamingException;

   protected abstract NamingEnumeration<NameClassPair> a_list(Continuation var1) throws NamingException;

   protected abstract NamingEnumeration<Binding> a_listBindings(Continuation var1) throws NamingException;

   protected abstract void a_bind(String var1, Object var2, Continuation var3) throws NamingException;

   protected abstract void a_rebind(String var1, Object var2, Continuation var3) throws NamingException;

   protected abstract void a_unbind(String var1, Continuation var2) throws NamingException;

   protected abstract void a_destroySubcontext(String var1, Continuation var2) throws NamingException;

   protected abstract Context a_createSubcontext(String var1, Continuation var2) throws NamingException;

   protected abstract void a_rename(String var1, Name var2, Continuation var3) throws NamingException;

   protected abstract NameParser a_getNameParser(Continuation var1) throws NamingException;

   protected abstract StringHeadTail c_parseComponent(String var1, Continuation var2) throws NamingException;

   protected Object a_resolveIntermediate_nns(String var1, Continuation var2) throws NamingException {
      try {
         final Object var3 = this.a_lookup(var1, var2);
         if (var3 != null && this.getClass().isInstance(var3)) {
            var2.setContinueNNS(var3, (String)var1, this);
            return null;
         } else if (var3 != null && !(var3 instanceof Context)) {
            RefAddr var4 = new RefAddr("nns") {
               private static final long serialVersionUID = -3399518522645918499L;

               public Object getContent() {
                  return var3;
               }
            };
            Reference var5 = new Reference("java.lang.Object", var4);
            CompositeName var6 = new CompositeName();
            var6.add(var1);
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

   protected Object a_lookup_nns(String var1, Continuation var2) throws NamingException {
      this.a_processJunction_nns(var1, var2);
      return null;
   }

   protected Object a_lookupLink_nns(String var1, Continuation var2) throws NamingException {
      this.a_processJunction_nns(var1, var2);
      return null;
   }

   protected NamingEnumeration<NameClassPair> a_list_nns(Continuation var1) throws NamingException {
      this.a_processJunction_nns(var1);
      return null;
   }

   protected NamingEnumeration<Binding> a_listBindings_nns(Continuation var1) throws NamingException {
      this.a_processJunction_nns(var1);
      return null;
   }

   protected void a_bind_nns(String var1, Object var2, Continuation var3) throws NamingException {
      this.a_processJunction_nns(var1, var3);
   }

   protected void a_rebind_nns(String var1, Object var2, Continuation var3) throws NamingException {
      this.a_processJunction_nns(var1, var3);
   }

   protected void a_unbind_nns(String var1, Continuation var2) throws NamingException {
      this.a_processJunction_nns(var1, var2);
   }

   protected Context a_createSubcontext_nns(String var1, Continuation var2) throws NamingException {
      this.a_processJunction_nns(var1, var2);
      return null;
   }

   protected void a_destroySubcontext_nns(String var1, Continuation var2) throws NamingException {
      this.a_processJunction_nns(var1, var2);
   }

   protected void a_rename_nns(String var1, Name var2, Continuation var3) throws NamingException {
      this.a_processJunction_nns(var1, var3);
   }

   protected NameParser a_getNameParser_nns(Continuation var1) throws NamingException {
      this.a_processJunction_nns(var1);
      return null;
   }

   protected boolean isEmpty(String var1) {
      return var1 == null || var1.equals("");
   }

   protected Object c_lookup(Name var1, Continuation var2) throws NamingException {
      Object var3 = null;
      if (this.resolve_to_penultimate_context(var1, var2)) {
         var3 = this.a_lookup(var1.toString(), var2);
         if (var3 != null && var3 instanceof LinkRef) {
            var2.setContinue(var3, var1, this);
            var3 = null;
         }
      }

      return var3;
   }

   protected Object c_lookupLink(Name var1, Continuation var2) throws NamingException {
      return this.resolve_to_penultimate_context(var1, var2) ? this.a_lookupLink(var1.toString(), var2) : null;
   }

   protected NamingEnumeration<NameClassPair> c_list(Name var1, Continuation var2) throws NamingException {
      return this.resolve_to_context(var1, var2) ? this.a_list(var2) : null;
   }

   protected NamingEnumeration<Binding> c_listBindings(Name var1, Continuation var2) throws NamingException {
      return this.resolve_to_context(var1, var2) ? this.a_listBindings(var2) : null;
   }

   protected void c_bind(Name var1, Object var2, Continuation var3) throws NamingException {
      if (this.resolve_to_penultimate_context(var1, var3)) {
         this.a_bind(var1.toString(), var2, var3);
      }

   }

   protected void c_rebind(Name var1, Object var2, Continuation var3) throws NamingException {
      if (this.resolve_to_penultimate_context(var1, var3)) {
         this.a_rebind(var1.toString(), var2, var3);
      }

   }

   protected void c_unbind(Name var1, Continuation var2) throws NamingException {
      if (this.resolve_to_penultimate_context(var1, var2)) {
         this.a_unbind(var1.toString(), var2);
      }

   }

   protected void c_destroySubcontext(Name var1, Continuation var2) throws NamingException {
      if (this.resolve_to_penultimate_context(var1, var2)) {
         this.a_destroySubcontext(var1.toString(), var2);
      }

   }

   protected Context c_createSubcontext(Name var1, Continuation var2) throws NamingException {
      return this.resolve_to_penultimate_context(var1, var2) ? this.a_createSubcontext(var1.toString(), var2) : null;
   }

   protected void c_rename(Name var1, Name var2, Continuation var3) throws NamingException {
      if (this.resolve_to_penultimate_context(var1, var3)) {
         this.a_rename(var1.toString(), var2, var3);
      }

   }

   protected NameParser c_getNameParser(Name var1, Continuation var2) throws NamingException {
      return this.resolve_to_context(var1, var2) ? this.a_getNameParser(var2) : null;
   }

   protected Object c_resolveIntermediate_nns(Name var1, Continuation var2) throws NamingException {
      if (this._contextType == 3) {
         Object var3 = null;
         if (this.resolve_to_penultimate_context_nns(var1, var2)) {
            var3 = this.a_resolveIntermediate_nns(var1.toString(), var2);
            if (var3 != null && var3 instanceof LinkRef) {
               var2.setContinue(var3, var1, this);
               var3 = null;
            }
         }

         return var3;
      } else {
         return super.c_resolveIntermediate_nns(var1, var2);
      }
   }

   protected Object c_lookup_nns(Name var1, Continuation var2) throws NamingException {
      if (this._contextType == 3) {
         Object var3 = null;
         if (this.resolve_to_penultimate_context_nns(var1, var2)) {
            var3 = this.a_lookup_nns(var1.toString(), var2);
            if (var3 != null && var3 instanceof LinkRef) {
               var2.setContinue(var3, var1, this);
               var3 = null;
            }
         }

         return var3;
      } else {
         return super.c_lookup_nns(var1, var2);
      }
   }

   protected Object c_lookupLink_nns(Name var1, Continuation var2) throws NamingException {
      if (this._contextType == 3) {
         this.resolve_to_nns_and_continue(var1, var2);
         return null;
      } else {
         return super.c_lookupLink_nns(var1, var2);
      }
   }

   protected NamingEnumeration<NameClassPair> c_list_nns(Name var1, Continuation var2) throws NamingException {
      if (this._contextType == 3) {
         this.resolve_to_nns_and_continue(var1, var2);
         return null;
      } else {
         return super.c_list_nns(var1, var2);
      }
   }

   protected NamingEnumeration<Binding> c_listBindings_nns(Name var1, Continuation var2) throws NamingException {
      if (this._contextType == 3) {
         this.resolve_to_nns_and_continue(var1, var2);
         return null;
      } else {
         return super.c_listBindings_nns(var1, var2);
      }
   }

   protected void c_bind_nns(Name var1, Object var2, Continuation var3) throws NamingException {
      if (this._contextType == 3) {
         if (this.resolve_to_penultimate_context_nns(var1, var3)) {
            this.a_bind_nns(var1.toString(), var2, var3);
         }
      } else {
         super.c_bind_nns(var1, var2, var3);
      }

   }

   protected void c_rebind_nns(Name var1, Object var2, Continuation var3) throws NamingException {
      if (this._contextType == 3) {
         if (this.resolve_to_penultimate_context_nns(var1, var3)) {
            this.a_rebind_nns(var1.toString(), var2, var3);
         }
      } else {
         super.c_rebind_nns(var1, var2, var3);
      }

   }

   protected void c_unbind_nns(Name var1, Continuation var2) throws NamingException {
      if (this._contextType == 3) {
         if (this.resolve_to_penultimate_context_nns(var1, var2)) {
            this.a_unbind_nns(var1.toString(), var2);
         }
      } else {
         super.c_unbind_nns(var1, var2);
      }

   }

   protected Context c_createSubcontext_nns(Name var1, Continuation var2) throws NamingException {
      if (this._contextType == 3) {
         return this.resolve_to_penultimate_context_nns(var1, var2) ? this.a_createSubcontext_nns(var1.toString(), var2) : null;
      } else {
         return super.c_createSubcontext_nns(var1, var2);
      }
   }

   protected void c_destroySubcontext_nns(Name var1, Continuation var2) throws NamingException {
      if (this._contextType == 3) {
         if (this.resolve_to_penultimate_context_nns(var1, var2)) {
            this.a_destroySubcontext_nns(var1.toString(), var2);
         }
      } else {
         super.c_destroySubcontext_nns(var1, var2);
      }

   }

   protected void c_rename_nns(Name var1, Name var2, Continuation var3) throws NamingException {
      if (this._contextType == 3) {
         if (this.resolve_to_penultimate_context_nns(var1, var3)) {
            this.a_rename_nns(var1.toString(), var2, var3);
         }
      } else {
         super.c_rename_nns(var1, var2, var3);
      }

   }

   protected NameParser c_getNameParser_nns(Name var1, Continuation var2) throws NamingException {
      if (this._contextType == 3) {
         this.resolve_to_nns_and_continue(var1, var2);
         return null;
      } else {
         return super.c_getNameParser_nns(var1, var2);
      }
   }

   protected void a_processJunction_nns(String var1, Continuation var2) throws NamingException {
      if (var1.equals("")) {
         NameNotFoundException var5 = new NameNotFoundException();
         var2.setErrorNNS(this, (String)var1);
         throw var2.fillInException(var5);
      } else {
         try {
            Object var3 = this.a_lookup(var1, var2);
            if (var2.isContinue()) {
               var2.appendRemainingComponent("");
            } else {
               var2.setContinueNNS(var3, (String)var1, this);
            }

         } catch (NamingException var4) {
            var4.appendRemainingComponent("");
            throw var4;
         }
      }
   }

   protected void a_processJunction_nns(Continuation var1) throws NamingException {
      RefAddr var2 = new RefAddr("nns") {
         private static final long serialVersionUID = 3449785852664978312L;

         public Object getContent() {
            return AtomicContext.this;
         }
      };
      Reference var3 = new Reference("java.lang.Object", var2);
      var1.setContinue(var3, _NNS_NAME, this);
   }

   protected boolean resolve_to_context(Name var1, Continuation var2) throws NamingException {
      String var3 = var1.toString();
      StringHeadTail var4 = this.c_parseComponent(var3, var2);
      String var5 = var4.getTail();
      String var6 = var4.getHead();
      if (debug > 0) {
         System.out.println("RESOLVE TO CONTEXT(" + var3 + ") = {" + var6 + ", " + var5 + "}");
      }

      if (var6 == null) {
         InvalidNameException var9 = new InvalidNameException();
         throw var2.fillInException(var9);
      } else if (!this.isEmpty(var6)) {
         try {
            Object var7 = this.a_lookup(var6, var2);
            if (var7 != null) {
               var2.setContinue(var7, (String)var6, this, (String)(var5 == null ? "" : var5));
            } else if (var2.isContinue()) {
               var2.appendRemainingComponent(var5);
            }

            return false;
         } catch (NamingException var8) {
            var8.appendRemainingComponent(var5);
            throw var8;
         }
      } else {
         var2.setSuccess();
         return true;
      }
   }

   protected boolean resolve_to_penultimate_context(Name var1, Continuation var2) throws NamingException {
      String var3 = var1.toString();
      if (debug > 0) {
         System.out.println("RESOLVE TO PENULTIMATE" + var3);
      }

      StringHeadTail var4 = this.c_parseComponent(var3, var2);
      String var5 = var4.getTail();
      String var6 = var4.getHead();
      if (var6 == null) {
         InvalidNameException var9 = new InvalidNameException();
         throw var2.fillInException(var9);
      } else if (!this.isEmpty(var5)) {
         try {
            Object var7 = this.a_lookup(var6, var2);
            if (var7 != null) {
               var2.setContinue(var7, (String)var6, this, (String)var5);
            } else if (var2.isContinue()) {
               var2.appendRemainingComponent(var5);
            }

            return false;
         } catch (NamingException var8) {
            var8.appendRemainingComponent(var5);
            throw var8;
         }
      } else {
         var2.setSuccess();
         return true;
      }
   }

   protected boolean resolve_to_penultimate_context_nns(Name var1, Continuation var2) throws NamingException {
      try {
         if (debug > 0) {
            System.out.println("RESOLVE TO PENULTIMATE NNS" + var1.toString());
         }

         boolean var3 = this.resolve_to_penultimate_context(var1, var2);
         if (var2.isContinue()) {
            var2.appendRemainingComponent("");
         }

         return var3;
      } catch (NamingException var4) {
         var4.appendRemainingComponent("");
         throw var4;
      }
   }

   protected void resolve_to_nns_and_continue(Name var1, Continuation var2) throws NamingException {
      if (debug > 0) {
         System.out.println("RESOLVE TO NNS AND CONTINUE" + var1.toString());
      }

      if (this.resolve_to_penultimate_context_nns(var1, var2)) {
         Object var3 = this.a_lookup_nns(var1.toString(), var2);
         if (var3 != null) {
            var2.setContinue(var3, var1, this);
         }
      }

   }
}
