package com.sun.jndi.toolkit.ctx;

import java.util.Hashtable;
import javax.naming.Binding;
import javax.naming.CannotProceedException;
import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.NotContextException;
import javax.naming.OperationNotSupportedException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.spi.DirectoryManager;

public abstract class PartialCompositeDirContext extends AtomicContext implements DirContext {
   protected PartialCompositeDirContext() {
      this._contextType = 1;
   }

   protected abstract Attributes p_getAttributes(Name var1, String[] var2, Continuation var3) throws NamingException;

   protected abstract void p_modifyAttributes(Name var1, int var2, Attributes var3, Continuation var4) throws NamingException;

   protected abstract void p_modifyAttributes(Name var1, ModificationItem[] var2, Continuation var3) throws NamingException;

   protected abstract void p_bind(Name var1, Object var2, Attributes var3, Continuation var4) throws NamingException;

   protected abstract void p_rebind(Name var1, Object var2, Attributes var3, Continuation var4) throws NamingException;

   protected abstract DirContext p_createSubcontext(Name var1, Attributes var2, Continuation var3) throws NamingException;

   protected abstract NamingEnumeration<SearchResult> p_search(Name var1, Attributes var2, String[] var3, Continuation var4) throws NamingException;

   protected abstract NamingEnumeration<SearchResult> p_search(Name var1, String var2, SearchControls var3, Continuation var4) throws NamingException;

   protected abstract NamingEnumeration<SearchResult> p_search(Name var1, String var2, Object[] var3, SearchControls var4, Continuation var5) throws NamingException;

   protected abstract DirContext p_getSchema(Name var1, Continuation var2) throws NamingException;

   protected abstract DirContext p_getSchemaClassDefinition(Name var1, Continuation var2) throws NamingException;

   public Attributes getAttributes(String var1) throws NamingException {
      return this.getAttributes((String)var1, (String[])null);
   }

   public Attributes getAttributes(Name var1) throws NamingException {
      return this.getAttributes((Name)var1, (String[])null);
   }

   public Attributes getAttributes(String var1, String[] var2) throws NamingException {
      return this.getAttributes((Name)(new CompositeName(var1)), var2);
   }

   public Attributes getAttributes(Name var1, String[] var2) throws NamingException {
      PartialCompositeDirContext var3 = this;
      Hashtable var4 = this.p_getEnvironment();
      Continuation var5 = new Continuation(var1, var4);
      Name var7 = var1;

      Attributes var6;
      try {
         for(var6 = var3.p_getAttributes(var7, var2, var5); var5.isContinue(); var6 = var3.p_getAttributes(var7, var2, var5)) {
            var7 = var5.getRemainingName();
            var3 = getPCDirContext(var5);
         }
      } catch (CannotProceedException var10) {
         DirContext var9 = DirectoryManager.getContinuationDirContext(var10);
         var6 = var9.getAttributes(var10.getRemainingName(), var2);
      }

      return var6;
   }

   public void modifyAttributes(String var1, int var2, Attributes var3) throws NamingException {
      this.modifyAttributes((Name)(new CompositeName(var1)), var2, var3);
   }

   public void modifyAttributes(Name var1, int var2, Attributes var3) throws NamingException {
      PartialCompositeDirContext var4 = this;
      Hashtable var5 = this.p_getEnvironment();
      Continuation var6 = new Continuation(var1, var5);
      Name var7 = var1;

      try {
         var4.p_modifyAttributes(var7, var2, var3, var6);

         while(var6.isContinue()) {
            var7 = var6.getRemainingName();
            var4 = getPCDirContext(var6);
            var4.p_modifyAttributes(var7, var2, var3, var6);
         }
      } catch (CannotProceedException var10) {
         DirContext var9 = DirectoryManager.getContinuationDirContext(var10);
         var9.modifyAttributes(var10.getRemainingName(), var2, var3);
      }

   }

   public void modifyAttributes(String var1, ModificationItem[] var2) throws NamingException {
      this.modifyAttributes((Name)(new CompositeName(var1)), var2);
   }

   public void modifyAttributes(Name var1, ModificationItem[] var2) throws NamingException {
      PartialCompositeDirContext var3 = this;
      Hashtable var4 = this.p_getEnvironment();
      Continuation var5 = new Continuation(var1, var4);
      Name var6 = var1;

      try {
         var3.p_modifyAttributes(var6, var2, var5);

         while(var5.isContinue()) {
            var6 = var5.getRemainingName();
            var3 = getPCDirContext(var5);
            var3.p_modifyAttributes(var6, var2, var5);
         }
      } catch (CannotProceedException var9) {
         DirContext var8 = DirectoryManager.getContinuationDirContext(var9);
         var8.modifyAttributes(var9.getRemainingName(), var2);
      }

   }

   public void bind(String var1, Object var2, Attributes var3) throws NamingException {
      this.bind((Name)(new CompositeName(var1)), var2, var3);
   }

   public void bind(Name var1, Object var2, Attributes var3) throws NamingException {
      PartialCompositeDirContext var4 = this;
      Hashtable var5 = this.p_getEnvironment();
      Continuation var6 = new Continuation(var1, var5);
      Name var7 = var1;

      try {
         var4.p_bind(var7, var2, var3, var6);

         while(var6.isContinue()) {
            var7 = var6.getRemainingName();
            var4 = getPCDirContext(var6);
            var4.p_bind(var7, var2, var3, var6);
         }
      } catch (CannotProceedException var10) {
         DirContext var9 = DirectoryManager.getContinuationDirContext(var10);
         var9.bind(var10.getRemainingName(), var2, var3);
      }

   }

   public void rebind(String var1, Object var2, Attributes var3) throws NamingException {
      this.rebind((Name)(new CompositeName(var1)), var2, var3);
   }

   public void rebind(Name var1, Object var2, Attributes var3) throws NamingException {
      PartialCompositeDirContext var4 = this;
      Hashtable var5 = this.p_getEnvironment();
      Continuation var6 = new Continuation(var1, var5);
      Name var7 = var1;

      try {
         var4.p_rebind(var7, var2, var3, var6);

         while(var6.isContinue()) {
            var7 = var6.getRemainingName();
            var4 = getPCDirContext(var6);
            var4.p_rebind(var7, var2, var3, var6);
         }
      } catch (CannotProceedException var10) {
         DirContext var9 = DirectoryManager.getContinuationDirContext(var10);
         var9.rebind(var10.getRemainingName(), var2, var3);
      }

   }

   public DirContext createSubcontext(String var1, Attributes var2) throws NamingException {
      return this.createSubcontext((Name)(new CompositeName(var1)), var2);
   }

   public DirContext createSubcontext(Name var1, Attributes var2) throws NamingException {
      PartialCompositeDirContext var3 = this;
      Hashtable var4 = this.p_getEnvironment();
      Continuation var5 = new Continuation(var1, var4);
      Name var7 = var1;

      DirContext var6;
      try {
         for(var6 = var3.p_createSubcontext(var7, var2, var5); var5.isContinue(); var6 = var3.p_createSubcontext(var7, var2, var5)) {
            var7 = var5.getRemainingName();
            var3 = getPCDirContext(var5);
         }
      } catch (CannotProceedException var10) {
         DirContext var9 = DirectoryManager.getContinuationDirContext(var10);
         var6 = var9.createSubcontext(var10.getRemainingName(), var2);
      }

      return var6;
   }

   public NamingEnumeration<SearchResult> search(String var1, Attributes var2) throws NamingException {
      return this.search((String)var1, (Attributes)var2, (String[])null);
   }

   public NamingEnumeration<SearchResult> search(Name var1, Attributes var2) throws NamingException {
      return this.search((Name)var1, (Attributes)var2, (String[])null);
   }

   public NamingEnumeration<SearchResult> search(String var1, Attributes var2, String[] var3) throws NamingException {
      return this.search((Name)(new CompositeName(var1)), (Attributes)var2, (String[])var3);
   }

   public NamingEnumeration<SearchResult> search(Name var1, Attributes var2, String[] var3) throws NamingException {
      PartialCompositeDirContext var4 = this;
      Hashtable var5 = this.p_getEnvironment();
      Continuation var6 = new Continuation(var1, var5);
      Name var8 = var1;

      NamingEnumeration var7;
      try {
         for(var7 = var4.p_search(var8, var2, var3, var6); var6.isContinue(); var7 = var4.p_search(var8, var2, var3, var6)) {
            var8 = var6.getRemainingName();
            var4 = getPCDirContext(var6);
         }
      } catch (CannotProceedException var11) {
         DirContext var10 = DirectoryManager.getContinuationDirContext(var11);
         var7 = var10.search(var11.getRemainingName(), var2, var3);
      }

      return var7;
   }

   public NamingEnumeration<SearchResult> search(String var1, String var2, SearchControls var3) throws NamingException {
      return this.search((Name)(new CompositeName(var1)), (String)var2, (SearchControls)var3);
   }

   public NamingEnumeration<SearchResult> search(Name var1, String var2, SearchControls var3) throws NamingException {
      PartialCompositeDirContext var4 = this;
      Hashtable var5 = this.p_getEnvironment();
      Continuation var6 = new Continuation(var1, var5);
      Name var8 = var1;

      NamingEnumeration var7;
      try {
         for(var7 = var4.p_search(var8, var2, var3, var6); var6.isContinue(); var7 = var4.p_search(var8, var2, var3, var6)) {
            var8 = var6.getRemainingName();
            var4 = getPCDirContext(var6);
         }
      } catch (CannotProceedException var11) {
         DirContext var10 = DirectoryManager.getContinuationDirContext(var11);
         var7 = var10.search(var11.getRemainingName(), var2, var3);
      }

      return var7;
   }

   public NamingEnumeration<SearchResult> search(String var1, String var2, Object[] var3, SearchControls var4) throws NamingException {
      return this.search((Name)(new CompositeName(var1)), var2, var3, var4);
   }

   public NamingEnumeration<SearchResult> search(Name var1, String var2, Object[] var3, SearchControls var4) throws NamingException {
      PartialCompositeDirContext var5 = this;
      Hashtable var6 = this.p_getEnvironment();
      Continuation var7 = new Continuation(var1, var6);
      Name var9 = var1;

      NamingEnumeration var8;
      try {
         for(var8 = var5.p_search(var9, var2, var3, var4, var7); var7.isContinue(); var8 = var5.p_search(var9, var2, var3, var4, var7)) {
            var9 = var7.getRemainingName();
            var5 = getPCDirContext(var7);
         }
      } catch (CannotProceedException var12) {
         DirContext var11 = DirectoryManager.getContinuationDirContext(var12);
         var8 = var11.search(var12.getRemainingName(), var2, var3, var4);
      }

      return var8;
   }

   public DirContext getSchema(String var1) throws NamingException {
      return this.getSchema((Name)(new CompositeName(var1)));
   }

   public DirContext getSchema(Name var1) throws NamingException {
      PartialCompositeDirContext var2 = this;
      Hashtable var3 = this.p_getEnvironment();
      Continuation var4 = new Continuation(var1, var3);
      Name var6 = var1;

      DirContext var5;
      try {
         for(var5 = var2.p_getSchema(var6, var4); var4.isContinue(); var5 = var2.p_getSchema(var6, var4)) {
            var6 = var4.getRemainingName();
            var2 = getPCDirContext(var4);
         }
      } catch (CannotProceedException var9) {
         DirContext var8 = DirectoryManager.getContinuationDirContext(var9);
         var5 = var8.getSchema(var9.getRemainingName());
      }

      return var5;
   }

   public DirContext getSchemaClassDefinition(String var1) throws NamingException {
      return this.getSchemaClassDefinition((Name)(new CompositeName(var1)));
   }

   public DirContext getSchemaClassDefinition(Name var1) throws NamingException {
      PartialCompositeDirContext var2 = this;
      Hashtable var3 = this.p_getEnvironment();
      Continuation var4 = new Continuation(var1, var3);
      Name var6 = var1;

      DirContext var5;
      try {
         for(var5 = var2.p_getSchemaClassDefinition(var6, var4); var4.isContinue(); var5 = var2.p_getSchemaClassDefinition(var6, var4)) {
            var6 = var4.getRemainingName();
            var2 = getPCDirContext(var4);
         }
      } catch (CannotProceedException var9) {
         DirContext var8 = DirectoryManager.getContinuationDirContext(var9);
         var5 = var8.getSchemaClassDefinition(var9.getRemainingName());
      }

      return var5;
   }

   protected static PartialCompositeDirContext getPCDirContext(Continuation var0) throws NamingException {
      PartialCompositeContext var1 = PartialCompositeContext.getPCContext(var0);
      if (!(var1 instanceof PartialCompositeDirContext)) {
         throw var0.fillInException(new NotContextException("Resolved object is not a DirContext."));
      } else {
         return (PartialCompositeDirContext)var1;
      }
   }

   protected StringHeadTail c_parseComponent(String var1, Continuation var2) throws NamingException {
      OperationNotSupportedException var3 = new OperationNotSupportedException();
      throw var2.fillInException(var3);
   }

   protected Object a_lookup(String var1, Continuation var2) throws NamingException {
      OperationNotSupportedException var3 = new OperationNotSupportedException();
      throw var2.fillInException(var3);
   }

   protected Object a_lookupLink(String var1, Continuation var2) throws NamingException {
      OperationNotSupportedException var3 = new OperationNotSupportedException();
      throw var2.fillInException(var3);
   }

   protected NamingEnumeration<NameClassPair> a_list(Continuation var1) throws NamingException {
      OperationNotSupportedException var2 = new OperationNotSupportedException();
      throw var1.fillInException(var2);
   }

   protected NamingEnumeration<Binding> a_listBindings(Continuation var1) throws NamingException {
      OperationNotSupportedException var2 = new OperationNotSupportedException();
      throw var1.fillInException(var2);
   }

   protected void a_bind(String var1, Object var2, Continuation var3) throws NamingException {
      OperationNotSupportedException var4 = new OperationNotSupportedException();
      throw var3.fillInException(var4);
   }

   protected void a_rebind(String var1, Object var2, Continuation var3) throws NamingException {
      OperationNotSupportedException var4 = new OperationNotSupportedException();
      throw var3.fillInException(var4);
   }

   protected void a_unbind(String var1, Continuation var2) throws NamingException {
      OperationNotSupportedException var3 = new OperationNotSupportedException();
      throw var2.fillInException(var3);
   }

   protected void a_destroySubcontext(String var1, Continuation var2) throws NamingException {
      OperationNotSupportedException var3 = new OperationNotSupportedException();
      throw var2.fillInException(var3);
   }

   protected Context a_createSubcontext(String var1, Continuation var2) throws NamingException {
      OperationNotSupportedException var3 = new OperationNotSupportedException();
      throw var2.fillInException(var3);
   }

   protected void a_rename(String var1, Name var2, Continuation var3) throws NamingException {
      OperationNotSupportedException var4 = new OperationNotSupportedException();
      throw var3.fillInException(var4);
   }

   protected NameParser a_getNameParser(Continuation var1) throws NamingException {
      OperationNotSupportedException var2 = new OperationNotSupportedException();
      throw var1.fillInException(var2);
   }
}
