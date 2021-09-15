package com.sun.jndi.toolkit.url;

import java.net.MalformedURLException;
import java.util.Hashtable;
import javax.naming.Binding;
import javax.naming.CannotProceedException;
import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.OperationNotSupportedException;
import javax.naming.spi.NamingManager;
import javax.naming.spi.ResolveResult;

public abstract class GenericURLContext implements Context {
   protected Hashtable<String, Object> myEnv = null;

   public GenericURLContext(Hashtable<?, ?> var1) {
      this.myEnv = (Hashtable)((Hashtable)(var1 == null ? null : var1.clone()));
   }

   public void close() throws NamingException {
      this.myEnv = null;
   }

   public String getNameInNamespace() throws NamingException {
      return "";
   }

   protected abstract ResolveResult getRootURLContext(String var1, Hashtable<?, ?> var2) throws NamingException;

   protected Name getURLSuffix(String var1, String var2) throws NamingException {
      String var3 = var2.substring(var1.length());
      if (var3.length() == 0) {
         return new CompositeName();
      } else {
         if (var3.charAt(0) == '/') {
            var3 = var3.substring(1);
         }

         try {
            return (new CompositeName()).add(UrlUtil.decode(var3));
         } catch (MalformedURLException var5) {
            throw new InvalidNameException(var5.getMessage());
         }
      }
   }

   protected String getURLPrefix(String var1) throws NamingException {
      int var2 = var1.indexOf(":");
      if (var2 < 0) {
         throw new OperationNotSupportedException("Invalid URL: " + var1);
      } else {
         ++var2;
         if (var1.startsWith("//", var2)) {
            var2 += 2;
            int var3 = var1.indexOf("/", var2);
            if (var3 >= 0) {
               var2 = var3;
            } else {
               var2 = var1.length();
            }
         }

         return var1.substring(0, var2);
      }
   }

   protected boolean urlEquals(String var1, String var2) {
      return var1.equals(var2);
   }

   protected Context getContinuationContext(Name var1) throws NamingException {
      Object var2 = this.lookup(var1.get(0));
      CannotProceedException var3 = new CannotProceedException();
      var3.setResolvedObj(var2);
      var3.setEnvironment(this.myEnv);
      return NamingManager.getContinuationContext(var3);
   }

   public Object lookup(String var1) throws NamingException {
      ResolveResult var2 = this.getRootURLContext(var1, this.myEnv);
      Context var3 = (Context)var2.getResolvedObj();

      Object var4;
      try {
         var4 = var3.lookup(var2.getRemainingName());
      } finally {
         var3.close();
      }

      return var4;
   }

   public Object lookup(Name var1) throws NamingException {
      if (var1.size() == 1) {
         return this.lookup(var1.get(0));
      } else {
         Context var2 = this.getContinuationContext(var1);

         Object var3;
         try {
            var3 = var2.lookup(var1.getSuffix(1));
         } finally {
            var2.close();
         }

         return var3;
      }
   }

   public void bind(String var1, Object var2) throws NamingException {
      ResolveResult var3 = this.getRootURLContext(var1, this.myEnv);
      Context var4 = (Context)var3.getResolvedObj();

      try {
         var4.bind(var3.getRemainingName(), var2);
      } finally {
         var4.close();
      }

   }

   public void bind(Name var1, Object var2) throws NamingException {
      if (var1.size() == 1) {
         this.bind(var1.get(0), var2);
      } else {
         Context var3 = this.getContinuationContext(var1);

         try {
            var3.bind(var1.getSuffix(1), var2);
         } finally {
            var3.close();
         }
      }

   }

   public void rebind(String var1, Object var2) throws NamingException {
      ResolveResult var3 = this.getRootURLContext(var1, this.myEnv);
      Context var4 = (Context)var3.getResolvedObj();

      try {
         var4.rebind(var3.getRemainingName(), var2);
      } finally {
         var4.close();
      }

   }

   public void rebind(Name var1, Object var2) throws NamingException {
      if (var1.size() == 1) {
         this.rebind(var1.get(0), var2);
      } else {
         Context var3 = this.getContinuationContext(var1);

         try {
            var3.rebind(var1.getSuffix(1), var2);
         } finally {
            var3.close();
         }
      }

   }

   public void unbind(String var1) throws NamingException {
      ResolveResult var2 = this.getRootURLContext(var1, this.myEnv);
      Context var3 = (Context)var2.getResolvedObj();

      try {
         var3.unbind(var2.getRemainingName());
      } finally {
         var3.close();
      }

   }

   public void unbind(Name var1) throws NamingException {
      if (var1.size() == 1) {
         this.unbind(var1.get(0));
      } else {
         Context var2 = this.getContinuationContext(var1);

         try {
            var2.unbind(var1.getSuffix(1));
         } finally {
            var2.close();
         }
      }

   }

   public void rename(String var1, String var2) throws NamingException {
      String var3 = this.getURLPrefix(var1);
      String var4 = this.getURLPrefix(var2);
      if (!this.urlEquals(var3, var4)) {
         throw new OperationNotSupportedException("Renaming using different URL prefixes not supported : " + var1 + " " + var2);
      } else {
         ResolveResult var5 = this.getRootURLContext(var1, this.myEnv);
         Context var6 = (Context)var5.getResolvedObj();

         try {
            var6.rename(var5.getRemainingName(), this.getURLSuffix(var4, var2));
         } finally {
            var6.close();
         }

      }
   }

   public void rename(Name var1, Name var2) throws NamingException {
      if (var1.size() == 1) {
         if (var2.size() != 1) {
            throw new OperationNotSupportedException("Renaming to a Name with more components not supported: " + var2);
         }

         this.rename(var1.get(0), var2.get(0));
      } else {
         if (!this.urlEquals(var1.get(0), var2.get(0))) {
            throw new OperationNotSupportedException("Renaming using different URLs as first components not supported: " + var1 + " " + var2);
         }

         Context var3 = this.getContinuationContext(var1);

         try {
            var3.rename(var1.getSuffix(1), var2.getSuffix(1));
         } finally {
            var3.close();
         }
      }

   }

   public NamingEnumeration<NameClassPair> list(String var1) throws NamingException {
      ResolveResult var2 = this.getRootURLContext(var1, this.myEnv);
      Context var3 = (Context)var2.getResolvedObj();

      NamingEnumeration var4;
      try {
         var4 = var3.list(var2.getRemainingName());
      } finally {
         var3.close();
      }

      return var4;
   }

   public NamingEnumeration<NameClassPair> list(Name var1) throws NamingException {
      if (var1.size() == 1) {
         return this.list(var1.get(0));
      } else {
         Context var2 = this.getContinuationContext(var1);

         NamingEnumeration var3;
         try {
            var3 = var2.list(var1.getSuffix(1));
         } finally {
            var2.close();
         }

         return var3;
      }
   }

   public NamingEnumeration<Binding> listBindings(String var1) throws NamingException {
      ResolveResult var2 = this.getRootURLContext(var1, this.myEnv);
      Context var3 = (Context)var2.getResolvedObj();

      NamingEnumeration var4;
      try {
         var4 = var3.listBindings(var2.getRemainingName());
      } finally {
         var3.close();
      }

      return var4;
   }

   public NamingEnumeration<Binding> listBindings(Name var1) throws NamingException {
      if (var1.size() == 1) {
         return this.listBindings(var1.get(0));
      } else {
         Context var2 = this.getContinuationContext(var1);

         NamingEnumeration var3;
         try {
            var3 = var2.listBindings(var1.getSuffix(1));
         } finally {
            var2.close();
         }

         return var3;
      }
   }

   public void destroySubcontext(String var1) throws NamingException {
      ResolveResult var2 = this.getRootURLContext(var1, this.myEnv);
      Context var3 = (Context)var2.getResolvedObj();

      try {
         var3.destroySubcontext(var2.getRemainingName());
      } finally {
         var3.close();
      }

   }

   public void destroySubcontext(Name var1) throws NamingException {
      if (var1.size() == 1) {
         this.destroySubcontext(var1.get(0));
      } else {
         Context var2 = this.getContinuationContext(var1);

         try {
            var2.destroySubcontext(var1.getSuffix(1));
         } finally {
            var2.close();
         }
      }

   }

   public Context createSubcontext(String var1) throws NamingException {
      ResolveResult var2 = this.getRootURLContext(var1, this.myEnv);
      Context var3 = (Context)var2.getResolvedObj();

      Context var4;
      try {
         var4 = var3.createSubcontext(var2.getRemainingName());
      } finally {
         var3.close();
      }

      return var4;
   }

   public Context createSubcontext(Name var1) throws NamingException {
      if (var1.size() == 1) {
         return this.createSubcontext(var1.get(0));
      } else {
         Context var2 = this.getContinuationContext(var1);

         Context var3;
         try {
            var3 = var2.createSubcontext(var1.getSuffix(1));
         } finally {
            var2.close();
         }

         return var3;
      }
   }

   public Object lookupLink(String var1) throws NamingException {
      ResolveResult var2 = this.getRootURLContext(var1, this.myEnv);
      Context var3 = (Context)var2.getResolvedObj();

      Object var4;
      try {
         var4 = var3.lookupLink(var2.getRemainingName());
      } finally {
         var3.close();
      }

      return var4;
   }

   public Object lookupLink(Name var1) throws NamingException {
      if (var1.size() == 1) {
         return this.lookupLink(var1.get(0));
      } else {
         Context var2 = this.getContinuationContext(var1);

         Object var3;
         try {
            var3 = var2.lookupLink(var1.getSuffix(1));
         } finally {
            var2.close();
         }

         return var3;
      }
   }

   public NameParser getNameParser(String var1) throws NamingException {
      ResolveResult var2 = this.getRootURLContext(var1, this.myEnv);
      Context var3 = (Context)var2.getResolvedObj();

      NameParser var4;
      try {
         var4 = var3.getNameParser(var2.getRemainingName());
      } finally {
         var3.close();
      }

      return var4;
   }

   public NameParser getNameParser(Name var1) throws NamingException {
      if (var1.size() == 1) {
         return this.getNameParser(var1.get(0));
      } else {
         Context var2 = this.getContinuationContext(var1);

         NameParser var3;
         try {
            var3 = var2.getNameParser(var1.getSuffix(1));
         } finally {
            var2.close();
         }

         return var3;
      }
   }

   public String composeName(String var1, String var2) throws NamingException {
      if (var2.equals("")) {
         return var1;
      } else {
         return var1.equals("") ? var2 : var2 + "/" + var1;
      }
   }

   public Name composeName(Name var1, Name var2) throws NamingException {
      Name var3 = (Name)var2.clone();
      var3.addAll(var1);
      return var3;
   }

   public Object removeFromEnvironment(String var1) throws NamingException {
      return this.myEnv == null ? null : this.myEnv.remove(var1);
   }

   public Object addToEnvironment(String var1, Object var2) throws NamingException {
      if (this.myEnv == null) {
         this.myEnv = new Hashtable(11, 0.75F);
      }

      return this.myEnv.put(var1, var2);
   }

   public Hashtable<String, Object> getEnvironment() throws NamingException {
      return this.myEnv == null ? new Hashtable(5, 0.75F) : (Hashtable)this.myEnv.clone();
   }
}
