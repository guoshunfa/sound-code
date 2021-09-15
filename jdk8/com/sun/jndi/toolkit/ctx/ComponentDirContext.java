package com.sun.jndi.toolkit.ctx;

import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

public abstract class ComponentDirContext extends PartialCompositeDirContext {
   protected ComponentDirContext() {
      this._contextType = 2;
   }

   protected abstract Attributes c_getAttributes(Name var1, String[] var2, Continuation var3) throws NamingException;

   protected abstract void c_modifyAttributes(Name var1, int var2, Attributes var3, Continuation var4) throws NamingException;

   protected abstract void c_modifyAttributes(Name var1, ModificationItem[] var2, Continuation var3) throws NamingException;

   protected abstract void c_bind(Name var1, Object var2, Attributes var3, Continuation var4) throws NamingException;

   protected abstract void c_rebind(Name var1, Object var2, Attributes var3, Continuation var4) throws NamingException;

   protected abstract DirContext c_createSubcontext(Name var1, Attributes var2, Continuation var3) throws NamingException;

   protected abstract NamingEnumeration<SearchResult> c_search(Name var1, Attributes var2, String[] var3, Continuation var4) throws NamingException;

   protected abstract NamingEnumeration<SearchResult> c_search(Name var1, String var2, SearchControls var3, Continuation var4) throws NamingException;

   protected abstract NamingEnumeration<SearchResult> c_search(Name var1, String var2, Object[] var3, SearchControls var4, Continuation var5) throws NamingException;

   protected abstract DirContext c_getSchema(Name var1, Continuation var2) throws NamingException;

   protected abstract DirContext c_getSchemaClassDefinition(Name var1, Continuation var2) throws NamingException;

   protected Attributes c_getAttributes_nns(Name var1, String[] var2, Continuation var3) throws NamingException {
      this.c_processJunction_nns(var1, var3);
      return null;
   }

   protected void c_modifyAttributes_nns(Name var1, int var2, Attributes var3, Continuation var4) throws NamingException {
      this.c_processJunction_nns(var1, var4);
   }

   protected void c_modifyAttributes_nns(Name var1, ModificationItem[] var2, Continuation var3) throws NamingException {
      this.c_processJunction_nns(var1, var3);
   }

   protected void c_bind_nns(Name var1, Object var2, Attributes var3, Continuation var4) throws NamingException {
      this.c_processJunction_nns(var1, var4);
   }

   protected void c_rebind_nns(Name var1, Object var2, Attributes var3, Continuation var4) throws NamingException {
      this.c_processJunction_nns(var1, var4);
   }

   protected DirContext c_createSubcontext_nns(Name var1, Attributes var2, Continuation var3) throws NamingException {
      this.c_processJunction_nns(var1, var3);
      return null;
   }

   protected NamingEnumeration<SearchResult> c_search_nns(Name var1, Attributes var2, String[] var3, Continuation var4) throws NamingException {
      this.c_processJunction_nns(var1, var4);
      return null;
   }

   protected NamingEnumeration<SearchResult> c_search_nns(Name var1, String var2, SearchControls var3, Continuation var4) throws NamingException {
      this.c_processJunction_nns(var1, var4);
      return null;
   }

   protected NamingEnumeration<SearchResult> c_search_nns(Name var1, String var2, Object[] var3, SearchControls var4, Continuation var5) throws NamingException {
      this.c_processJunction_nns(var1, var5);
      return null;
   }

   protected DirContext c_getSchema_nns(Name var1, Continuation var2) throws NamingException {
      this.c_processJunction_nns(var1, var2);
      return null;
   }

   protected DirContext c_getSchemaClassDefinition_nns(Name var1, Continuation var2) throws NamingException {
      this.c_processJunction_nns(var1, var2);
      return null;
   }

   protected Attributes p_getAttributes(Name var1, String[] var2, Continuation var3) throws NamingException {
      HeadTail var4 = this.p_resolveIntermediate(var1, var3);
      Attributes var5 = null;
      switch(var4.getStatus()) {
      case 2:
         var5 = this.c_getAttributes(var4.getHead(), var2, var3);
         break;
      case 3:
         var5 = this.c_getAttributes_nns(var4.getHead(), var2, var3);
      }

      return var5;
   }

   protected void p_modifyAttributes(Name var1, int var2, Attributes var3, Continuation var4) throws NamingException {
      HeadTail var5 = this.p_resolveIntermediate(var1, var4);
      switch(var5.getStatus()) {
      case 2:
         this.c_modifyAttributes(var5.getHead(), var2, var3, var4);
         break;
      case 3:
         this.c_modifyAttributes_nns(var5.getHead(), var2, var3, var4);
      }

   }

   protected void p_modifyAttributes(Name var1, ModificationItem[] var2, Continuation var3) throws NamingException {
      HeadTail var4 = this.p_resolveIntermediate(var1, var3);
      switch(var4.getStatus()) {
      case 2:
         this.c_modifyAttributes(var4.getHead(), var2, var3);
         break;
      case 3:
         this.c_modifyAttributes_nns(var4.getHead(), var2, var3);
      }

   }

   protected void p_bind(Name var1, Object var2, Attributes var3, Continuation var4) throws NamingException {
      HeadTail var5 = this.p_resolveIntermediate(var1, var4);
      switch(var5.getStatus()) {
      case 2:
         this.c_bind(var5.getHead(), var2, var3, var4);
         break;
      case 3:
         this.c_bind_nns(var5.getHead(), var2, var3, var4);
      }

   }

   protected void p_rebind(Name var1, Object var2, Attributes var3, Continuation var4) throws NamingException {
      HeadTail var5 = this.p_resolveIntermediate(var1, var4);
      switch(var5.getStatus()) {
      case 2:
         this.c_rebind(var5.getHead(), var2, var3, var4);
         break;
      case 3:
         this.c_rebind_nns(var5.getHead(), var2, var3, var4);
      }

   }

   protected DirContext p_createSubcontext(Name var1, Attributes var2, Continuation var3) throws NamingException {
      HeadTail var4 = this.p_resolveIntermediate(var1, var3);
      DirContext var5 = null;
      switch(var4.getStatus()) {
      case 2:
         var5 = this.c_createSubcontext(var4.getHead(), var2, var3);
         break;
      case 3:
         var5 = this.c_createSubcontext_nns(var4.getHead(), var2, var3);
      }

      return var5;
   }

   protected NamingEnumeration<SearchResult> p_search(Name var1, Attributes var2, String[] var3, Continuation var4) throws NamingException {
      HeadTail var5 = this.p_resolveIntermediate(var1, var4);
      NamingEnumeration var6 = null;
      switch(var5.getStatus()) {
      case 2:
         var6 = this.c_search(var5.getHead(), var2, var3, var4);
         break;
      case 3:
         var6 = this.c_search_nns(var5.getHead(), var2, var3, var4);
      }

      return var6;
   }

   protected NamingEnumeration<SearchResult> p_search(Name var1, String var2, SearchControls var3, Continuation var4) throws NamingException {
      HeadTail var5 = this.p_resolveIntermediate(var1, var4);
      NamingEnumeration var6 = null;
      switch(var5.getStatus()) {
      case 2:
         var6 = this.c_search(var5.getHead(), var2, var3, var4);
         break;
      case 3:
         var6 = this.c_search_nns(var5.getHead(), var2, var3, var4);
      }

      return var6;
   }

   protected NamingEnumeration<SearchResult> p_search(Name var1, String var2, Object[] var3, SearchControls var4, Continuation var5) throws NamingException {
      HeadTail var6 = this.p_resolveIntermediate(var1, var5);
      NamingEnumeration var7 = null;
      switch(var6.getStatus()) {
      case 2:
         var7 = this.c_search(var6.getHead(), var2, var3, var4, var5);
         break;
      case 3:
         var7 = this.c_search_nns(var6.getHead(), var2, var3, var4, var5);
      }

      return var7;
   }

   protected DirContext p_getSchema(Name var1, Continuation var2) throws NamingException {
      DirContext var3 = null;
      HeadTail var4 = this.p_resolveIntermediate(var1, var2);
      switch(var4.getStatus()) {
      case 2:
         var3 = this.c_getSchema(var4.getHead(), var2);
         break;
      case 3:
         var3 = this.c_getSchema_nns(var4.getHead(), var2);
      }

      return var3;
   }

   protected DirContext p_getSchemaClassDefinition(Name var1, Continuation var2) throws NamingException {
      DirContext var3 = null;
      HeadTail var4 = this.p_resolveIntermediate(var1, var2);
      switch(var4.getStatus()) {
      case 2:
         var3 = this.c_getSchemaClassDefinition(var4.getHead(), var2);
         break;
      case 3:
         var3 = this.c_getSchemaClassDefinition_nns(var4.getHead(), var2);
      }

      return var3;
   }
}
