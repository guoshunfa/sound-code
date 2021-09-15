package com.sun.jndi.toolkit.ctx;

import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

public abstract class AtomicDirContext extends ComponentDirContext {
   protected AtomicDirContext() {
      this._contextType = 3;
   }

   protected abstract Attributes a_getAttributes(String var1, String[] var2, Continuation var3) throws NamingException;

   protected abstract void a_modifyAttributes(String var1, int var2, Attributes var3, Continuation var4) throws NamingException;

   protected abstract void a_modifyAttributes(String var1, ModificationItem[] var2, Continuation var3) throws NamingException;

   protected abstract void a_bind(String var1, Object var2, Attributes var3, Continuation var4) throws NamingException;

   protected abstract void a_rebind(String var1, Object var2, Attributes var3, Continuation var4) throws NamingException;

   protected abstract DirContext a_createSubcontext(String var1, Attributes var2, Continuation var3) throws NamingException;

   protected abstract NamingEnumeration<SearchResult> a_search(Attributes var1, String[] var2, Continuation var3) throws NamingException;

   protected abstract NamingEnumeration<SearchResult> a_search(String var1, String var2, Object[] var3, SearchControls var4, Continuation var5) throws NamingException;

   protected abstract NamingEnumeration<SearchResult> a_search(String var1, String var2, SearchControls var3, Continuation var4) throws NamingException;

   protected abstract DirContext a_getSchema(Continuation var1) throws NamingException;

   protected abstract DirContext a_getSchemaClassDefinition(Continuation var1) throws NamingException;

   protected Attributes a_getAttributes_nns(String var1, String[] var2, Continuation var3) throws NamingException {
      this.a_processJunction_nns(var1, var3);
      return null;
   }

   protected void a_modifyAttributes_nns(String var1, int var2, Attributes var3, Continuation var4) throws NamingException {
      this.a_processJunction_nns(var1, var4);
   }

   protected void a_modifyAttributes_nns(String var1, ModificationItem[] var2, Continuation var3) throws NamingException {
      this.a_processJunction_nns(var1, var3);
   }

   protected void a_bind_nns(String var1, Object var2, Attributes var3, Continuation var4) throws NamingException {
      this.a_processJunction_nns(var1, var4);
   }

   protected void a_rebind_nns(String var1, Object var2, Attributes var3, Continuation var4) throws NamingException {
      this.a_processJunction_nns(var1, var4);
   }

   protected DirContext a_createSubcontext_nns(String var1, Attributes var2, Continuation var3) throws NamingException {
      this.a_processJunction_nns(var1, var3);
      return null;
   }

   protected NamingEnumeration<SearchResult> a_search_nns(Attributes var1, String[] var2, Continuation var3) throws NamingException {
      this.a_processJunction_nns(var3);
      return null;
   }

   protected NamingEnumeration<SearchResult> a_search_nns(String var1, String var2, Object[] var3, SearchControls var4, Continuation var5) throws NamingException {
      this.a_processJunction_nns(var1, var5);
      return null;
   }

   protected NamingEnumeration<SearchResult> a_search_nns(String var1, String var2, SearchControls var3, Continuation var4) throws NamingException {
      this.a_processJunction_nns(var1, var4);
      return null;
   }

   protected DirContext a_getSchema_nns(Continuation var1) throws NamingException {
      this.a_processJunction_nns(var1);
      return null;
   }

   protected DirContext a_getSchemaDefinition_nns(Continuation var1) throws NamingException {
      this.a_processJunction_nns(var1);
      return null;
   }

   protected Attributes c_getAttributes(Name var1, String[] var2, Continuation var3) throws NamingException {
      return this.resolve_to_penultimate_context(var1, var3) ? this.a_getAttributes(var1.toString(), var2, var3) : null;
   }

   protected void c_modifyAttributes(Name var1, int var2, Attributes var3, Continuation var4) throws NamingException {
      if (this.resolve_to_penultimate_context(var1, var4)) {
         this.a_modifyAttributes(var1.toString(), var2, var3, var4);
      }

   }

   protected void c_modifyAttributes(Name var1, ModificationItem[] var2, Continuation var3) throws NamingException {
      if (this.resolve_to_penultimate_context(var1, var3)) {
         this.a_modifyAttributes(var1.toString(), var2, var3);
      }

   }

   protected void c_bind(Name var1, Object var2, Attributes var3, Continuation var4) throws NamingException {
      if (this.resolve_to_penultimate_context(var1, var4)) {
         this.a_bind(var1.toString(), var2, var3, var4);
      }

   }

   protected void c_rebind(Name var1, Object var2, Attributes var3, Continuation var4) throws NamingException {
      if (this.resolve_to_penultimate_context(var1, var4)) {
         this.a_rebind(var1.toString(), var2, var3, var4);
      }

   }

   protected DirContext c_createSubcontext(Name var1, Attributes var2, Continuation var3) throws NamingException {
      return this.resolve_to_penultimate_context(var1, var3) ? this.a_createSubcontext(var1.toString(), var2, var3) : null;
   }

   protected NamingEnumeration<SearchResult> c_search(Name var1, Attributes var2, String[] var3, Continuation var4) throws NamingException {
      return this.resolve_to_context(var1, var4) ? this.a_search(var2, var3, var4) : null;
   }

   protected NamingEnumeration<SearchResult> c_search(Name var1, String var2, SearchControls var3, Continuation var4) throws NamingException {
      return this.resolve_to_penultimate_context(var1, var4) ? this.a_search(var1.toString(), var2, var3, var4) : null;
   }

   protected NamingEnumeration<SearchResult> c_search(Name var1, String var2, Object[] var3, SearchControls var4, Continuation var5) throws NamingException {
      return this.resolve_to_penultimate_context(var1, var5) ? this.a_search(var1.toString(), var2, var3, var4, var5) : null;
   }

   protected DirContext c_getSchema(Name var1, Continuation var2) throws NamingException {
      return this.resolve_to_context(var1, var2) ? this.a_getSchema(var2) : null;
   }

   protected DirContext c_getSchemaClassDefinition(Name var1, Continuation var2) throws NamingException {
      return this.resolve_to_context(var1, var2) ? this.a_getSchemaClassDefinition(var2) : null;
   }

   protected Attributes c_getAttributes_nns(Name var1, String[] var2, Continuation var3) throws NamingException {
      return this.resolve_to_penultimate_context_nns(var1, var3) ? this.a_getAttributes_nns(var1.toString(), var2, var3) : null;
   }

   protected void c_modifyAttributes_nns(Name var1, int var2, Attributes var3, Continuation var4) throws NamingException {
      if (this.resolve_to_penultimate_context_nns(var1, var4)) {
         this.a_modifyAttributes_nns(var1.toString(), var2, var3, var4);
      }

   }

   protected void c_modifyAttributes_nns(Name var1, ModificationItem[] var2, Continuation var3) throws NamingException {
      if (this.resolve_to_penultimate_context_nns(var1, var3)) {
         this.a_modifyAttributes_nns(var1.toString(), var2, var3);
      }

   }

   protected void c_bind_nns(Name var1, Object var2, Attributes var3, Continuation var4) throws NamingException {
      if (this.resolve_to_penultimate_context_nns(var1, var4)) {
         this.a_bind_nns(var1.toString(), var2, var3, var4);
      }

   }

   protected void c_rebind_nns(Name var1, Object var2, Attributes var3, Continuation var4) throws NamingException {
      if (this.resolve_to_penultimate_context_nns(var1, var4)) {
         this.a_rebind_nns(var1.toString(), var2, var3, var4);
      }

   }

   protected DirContext c_createSubcontext_nns(Name var1, Attributes var2, Continuation var3) throws NamingException {
      return this.resolve_to_penultimate_context_nns(var1, var3) ? this.a_createSubcontext_nns(var1.toString(), var2, var3) : null;
   }

   protected NamingEnumeration<SearchResult> c_search_nns(Name var1, Attributes var2, String[] var3, Continuation var4) throws NamingException {
      this.resolve_to_nns_and_continue(var1, var4);
      return null;
   }

   protected NamingEnumeration<SearchResult> c_search_nns(Name var1, String var2, SearchControls var3, Continuation var4) throws NamingException {
      return this.resolve_to_penultimate_context_nns(var1, var4) ? this.a_search_nns(var1.toString(), var2, var3, var4) : null;
   }

   protected NamingEnumeration<SearchResult> c_search_nns(Name var1, String var2, Object[] var3, SearchControls var4, Continuation var5) throws NamingException {
      return this.resolve_to_penultimate_context_nns(var1, var5) ? this.a_search_nns(var1.toString(), var2, var3, var4, var5) : null;
   }

   protected DirContext c_getSchema_nns(Name var1, Continuation var2) throws NamingException {
      this.resolve_to_nns_and_continue(var1, var2);
      return null;
   }

   protected DirContext c_getSchemaClassDefinition_nns(Name var1, Continuation var2) throws NamingException {
      this.resolve_to_nns_and_continue(var1, var2);
      return null;
   }
}
