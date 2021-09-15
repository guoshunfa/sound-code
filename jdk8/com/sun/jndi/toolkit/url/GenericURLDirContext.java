package com.sun.jndi.toolkit.url;

import java.util.Hashtable;
import javax.naming.CannotProceedException;
import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.spi.DirectoryManager;
import javax.naming.spi.ResolveResult;

public abstract class GenericURLDirContext extends GenericURLContext implements DirContext {
   protected GenericURLDirContext(Hashtable<?, ?> var1) {
      super(var1);
   }

   protected DirContext getContinuationDirContext(Name var1) throws NamingException {
      Object var2 = this.lookup(var1.get(0));
      CannotProceedException var3 = new CannotProceedException();
      var3.setResolvedObj(var2);
      var3.setEnvironment(this.myEnv);
      return DirectoryManager.getContinuationDirContext(var3);
   }

   public Attributes getAttributes(String var1) throws NamingException {
      ResolveResult var2 = this.getRootURLContext(var1, this.myEnv);
      DirContext var3 = (DirContext)var2.getResolvedObj();

      Attributes var4;
      try {
         var4 = var3.getAttributes(var2.getRemainingName());
      } finally {
         var3.close();
      }

      return var4;
   }

   public Attributes getAttributes(Name var1) throws NamingException {
      if (var1.size() == 1) {
         return this.getAttributes(var1.get(0));
      } else {
         DirContext var2 = this.getContinuationDirContext(var1);

         Attributes var3;
         try {
            var3 = var2.getAttributes(var1.getSuffix(1));
         } finally {
            var2.close();
         }

         return var3;
      }
   }

   public Attributes getAttributes(String var1, String[] var2) throws NamingException {
      ResolveResult var3 = this.getRootURLContext(var1, this.myEnv);
      DirContext var4 = (DirContext)var3.getResolvedObj();

      Attributes var5;
      try {
         var5 = var4.getAttributes(var3.getRemainingName(), var2);
      } finally {
         var4.close();
      }

      return var5;
   }

   public Attributes getAttributes(Name var1, String[] var2) throws NamingException {
      if (var1.size() == 1) {
         return this.getAttributes(var1.get(0), var2);
      } else {
         DirContext var3 = this.getContinuationDirContext(var1);

         Attributes var4;
         try {
            var4 = var3.getAttributes(var1.getSuffix(1), var2);
         } finally {
            var3.close();
         }

         return var4;
      }
   }

   public void modifyAttributes(String var1, int var2, Attributes var3) throws NamingException {
      ResolveResult var4 = this.getRootURLContext(var1, this.myEnv);
      DirContext var5 = (DirContext)var4.getResolvedObj();

      try {
         var5.modifyAttributes(var4.getRemainingName(), var2, var3);
      } finally {
         var5.close();
      }

   }

   public void modifyAttributes(Name var1, int var2, Attributes var3) throws NamingException {
      if (var1.size() == 1) {
         this.modifyAttributes(var1.get(0), var2, var3);
      } else {
         DirContext var4 = this.getContinuationDirContext(var1);

         try {
            var4.modifyAttributes(var1.getSuffix(1), var2, var3);
         } finally {
            var4.close();
         }
      }

   }

   public void modifyAttributes(String var1, ModificationItem[] var2) throws NamingException {
      ResolveResult var3 = this.getRootURLContext(var1, this.myEnv);
      DirContext var4 = (DirContext)var3.getResolvedObj();

      try {
         var4.modifyAttributes(var3.getRemainingName(), var2);
      } finally {
         var4.close();
      }

   }

   public void modifyAttributes(Name var1, ModificationItem[] var2) throws NamingException {
      if (var1.size() == 1) {
         this.modifyAttributes(var1.get(0), var2);
      } else {
         DirContext var3 = this.getContinuationDirContext(var1);

         try {
            var3.modifyAttributes(var1.getSuffix(1), var2);
         } finally {
            var3.close();
         }
      }

   }

   public void bind(String var1, Object var2, Attributes var3) throws NamingException {
      ResolveResult var4 = this.getRootURLContext(var1, this.myEnv);
      DirContext var5 = (DirContext)var4.getResolvedObj();

      try {
         var5.bind(var4.getRemainingName(), var2, var3);
      } finally {
         var5.close();
      }

   }

   public void bind(Name var1, Object var2, Attributes var3) throws NamingException {
      if (var1.size() == 1) {
         this.bind(var1.get(0), var2, var3);
      } else {
         DirContext var4 = this.getContinuationDirContext(var1);

         try {
            var4.bind(var1.getSuffix(1), var2, var3);
         } finally {
            var4.close();
         }
      }

   }

   public void rebind(String var1, Object var2, Attributes var3) throws NamingException {
      ResolveResult var4 = this.getRootURLContext(var1, this.myEnv);
      DirContext var5 = (DirContext)var4.getResolvedObj();

      try {
         var5.rebind(var4.getRemainingName(), var2, var3);
      } finally {
         var5.close();
      }

   }

   public void rebind(Name var1, Object var2, Attributes var3) throws NamingException {
      if (var1.size() == 1) {
         this.rebind(var1.get(0), var2, var3);
      } else {
         DirContext var4 = this.getContinuationDirContext(var1);

         try {
            var4.rebind(var1.getSuffix(1), var2, var3);
         } finally {
            var4.close();
         }
      }

   }

   public DirContext createSubcontext(String var1, Attributes var2) throws NamingException {
      ResolveResult var3 = this.getRootURLContext(var1, this.myEnv);
      DirContext var4 = (DirContext)var3.getResolvedObj();

      DirContext var5;
      try {
         var5 = var4.createSubcontext(var3.getRemainingName(), var2);
      } finally {
         var4.close();
      }

      return var5;
   }

   public DirContext createSubcontext(Name var1, Attributes var2) throws NamingException {
      if (var1.size() == 1) {
         return this.createSubcontext(var1.get(0), var2);
      } else {
         DirContext var3 = this.getContinuationDirContext(var1);

         DirContext var4;
         try {
            var4 = var3.createSubcontext(var1.getSuffix(1), var2);
         } finally {
            var3.close();
         }

         return var4;
      }
   }

   public DirContext getSchema(String var1) throws NamingException {
      ResolveResult var2 = this.getRootURLContext(var1, this.myEnv);
      DirContext var3 = (DirContext)var2.getResolvedObj();
      return var3.getSchema(var2.getRemainingName());
   }

   public DirContext getSchema(Name var1) throws NamingException {
      if (var1.size() == 1) {
         return this.getSchema(var1.get(0));
      } else {
         DirContext var2 = this.getContinuationDirContext(var1);

         DirContext var3;
         try {
            var3 = var2.getSchema(var1.getSuffix(1));
         } finally {
            var2.close();
         }

         return var3;
      }
   }

   public DirContext getSchemaClassDefinition(String var1) throws NamingException {
      ResolveResult var2 = this.getRootURLContext(var1, this.myEnv);
      DirContext var3 = (DirContext)var2.getResolvedObj();

      DirContext var4;
      try {
         var4 = var3.getSchemaClassDefinition(var2.getRemainingName());
      } finally {
         var3.close();
      }

      return var4;
   }

   public DirContext getSchemaClassDefinition(Name var1) throws NamingException {
      if (var1.size() == 1) {
         return this.getSchemaClassDefinition(var1.get(0));
      } else {
         DirContext var2 = this.getContinuationDirContext(var1);

         DirContext var3;
         try {
            var3 = var2.getSchemaClassDefinition(var1.getSuffix(1));
         } finally {
            var2.close();
         }

         return var3;
      }
   }

   public NamingEnumeration<SearchResult> search(String var1, Attributes var2) throws NamingException {
      ResolveResult var3 = this.getRootURLContext(var1, this.myEnv);
      DirContext var4 = (DirContext)var3.getResolvedObj();

      NamingEnumeration var5;
      try {
         var5 = var4.search(var3.getRemainingName(), var2);
      } finally {
         var4.close();
      }

      return var5;
   }

   public NamingEnumeration<SearchResult> search(Name var1, Attributes var2) throws NamingException {
      if (var1.size() == 1) {
         return this.search(var1.get(0), var2);
      } else {
         DirContext var3 = this.getContinuationDirContext(var1);

         NamingEnumeration var4;
         try {
            var4 = var3.search(var1.getSuffix(1), var2);
         } finally {
            var3.close();
         }

         return var4;
      }
   }

   public NamingEnumeration<SearchResult> search(String var1, Attributes var2, String[] var3) throws NamingException {
      ResolveResult var4 = this.getRootURLContext(var1, this.myEnv);
      DirContext var5 = (DirContext)var4.getResolvedObj();

      NamingEnumeration var6;
      try {
         var6 = var5.search(var4.getRemainingName(), var2, var3);
      } finally {
         var5.close();
      }

      return var6;
   }

   public NamingEnumeration<SearchResult> search(Name var1, Attributes var2, String[] var3) throws NamingException {
      if (var1.size() == 1) {
         return this.search(var1.get(0), var2, var3);
      } else {
         DirContext var4 = this.getContinuationDirContext(var1);

         NamingEnumeration var5;
         try {
            var5 = var4.search(var1.getSuffix(1), var2, var3);
         } finally {
            var4.close();
         }

         return var5;
      }
   }

   public NamingEnumeration<SearchResult> search(String var1, String var2, SearchControls var3) throws NamingException {
      ResolveResult var4 = this.getRootURLContext(var1, this.myEnv);
      DirContext var5 = (DirContext)var4.getResolvedObj();

      NamingEnumeration var6;
      try {
         var6 = var5.search(var4.getRemainingName(), var2, var3);
      } finally {
         var5.close();
      }

      return var6;
   }

   public NamingEnumeration<SearchResult> search(Name var1, String var2, SearchControls var3) throws NamingException {
      if (var1.size() == 1) {
         return this.search(var1.get(0), var2, var3);
      } else {
         DirContext var4 = this.getContinuationDirContext(var1);

         NamingEnumeration var5;
         try {
            var5 = var4.search(var1.getSuffix(1), var2, var3);
         } finally {
            var4.close();
         }

         return var5;
      }
   }

   public NamingEnumeration<SearchResult> search(String var1, String var2, Object[] var3, SearchControls var4) throws NamingException {
      ResolveResult var5 = this.getRootURLContext(var1, this.myEnv);
      DirContext var6 = (DirContext)var5.getResolvedObj();

      NamingEnumeration var7;
      try {
         var7 = var6.search(var5.getRemainingName(), var2, var3, var4);
      } finally {
         var6.close();
      }

      return var7;
   }

   public NamingEnumeration<SearchResult> search(Name var1, String var2, Object[] var3, SearchControls var4) throws NamingException {
      if (var1.size() == 1) {
         return this.search(var1.get(0), var2, var3, var4);
      } else {
         DirContext var5 = this.getContinuationDirContext(var1);

         NamingEnumeration var6;
         try {
            var6 = var5.search(var1.getSuffix(1), var2, var3, var4);
         } finally {
            var5.close();
         }

         return var6;
      }
   }
}
