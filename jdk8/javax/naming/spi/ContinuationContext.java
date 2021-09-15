package javax.naming.spi;

import java.util.Hashtable;
import javax.naming.Binding;
import javax.naming.CannotProceedException;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

class ContinuationContext implements Context, Resolver {
   protected CannotProceedException cpe;
   protected Hashtable<?, ?> env;
   protected Context contCtx = null;

   protected ContinuationContext(CannotProceedException var1, Hashtable<?, ?> var2) {
      this.cpe = var1;
      this.env = var2;
   }

   protected Context getTargetContext() throws NamingException {
      if (this.contCtx == null) {
         if (this.cpe.getResolvedObj() == null) {
            throw (NamingException)this.cpe.fillInStackTrace();
         }

         this.contCtx = NamingManager.getContext(this.cpe.getResolvedObj(), this.cpe.getAltName(), this.cpe.getAltNameCtx(), this.env);
         if (this.contCtx == null) {
            throw (NamingException)this.cpe.fillInStackTrace();
         }
      }

      return this.contCtx;
   }

   public Object lookup(Name var1) throws NamingException {
      Context var2 = this.getTargetContext();
      return var2.lookup(var1);
   }

   public Object lookup(String var1) throws NamingException {
      Context var2 = this.getTargetContext();
      return var2.lookup(var1);
   }

   public void bind(Name var1, Object var2) throws NamingException {
      Context var3 = this.getTargetContext();
      var3.bind(var1, var2);
   }

   public void bind(String var1, Object var2) throws NamingException {
      Context var3 = this.getTargetContext();
      var3.bind(var1, var2);
   }

   public void rebind(Name var1, Object var2) throws NamingException {
      Context var3 = this.getTargetContext();
      var3.rebind(var1, var2);
   }

   public void rebind(String var1, Object var2) throws NamingException {
      Context var3 = this.getTargetContext();
      var3.rebind(var1, var2);
   }

   public void unbind(Name var1) throws NamingException {
      Context var2 = this.getTargetContext();
      var2.unbind(var1);
   }

   public void unbind(String var1) throws NamingException {
      Context var2 = this.getTargetContext();
      var2.unbind(var1);
   }

   public void rename(Name var1, Name var2) throws NamingException {
      Context var3 = this.getTargetContext();
      var3.rename(var1, var2);
   }

   public void rename(String var1, String var2) throws NamingException {
      Context var3 = this.getTargetContext();
      var3.rename(var1, var2);
   }

   public NamingEnumeration<NameClassPair> list(Name var1) throws NamingException {
      Context var2 = this.getTargetContext();
      return var2.list(var1);
   }

   public NamingEnumeration<NameClassPair> list(String var1) throws NamingException {
      Context var2 = this.getTargetContext();
      return var2.list(var1);
   }

   public NamingEnumeration<Binding> listBindings(Name var1) throws NamingException {
      Context var2 = this.getTargetContext();
      return var2.listBindings(var1);
   }

   public NamingEnumeration<Binding> listBindings(String var1) throws NamingException {
      Context var2 = this.getTargetContext();
      return var2.listBindings(var1);
   }

   public void destroySubcontext(Name var1) throws NamingException {
      Context var2 = this.getTargetContext();
      var2.destroySubcontext(var1);
   }

   public void destroySubcontext(String var1) throws NamingException {
      Context var2 = this.getTargetContext();
      var2.destroySubcontext(var1);
   }

   public Context createSubcontext(Name var1) throws NamingException {
      Context var2 = this.getTargetContext();
      return var2.createSubcontext(var1);
   }

   public Context createSubcontext(String var1) throws NamingException {
      Context var2 = this.getTargetContext();
      return var2.createSubcontext(var1);
   }

   public Object lookupLink(Name var1) throws NamingException {
      Context var2 = this.getTargetContext();
      return var2.lookupLink(var1);
   }

   public Object lookupLink(String var1) throws NamingException {
      Context var2 = this.getTargetContext();
      return var2.lookupLink(var1);
   }

   public NameParser getNameParser(Name var1) throws NamingException {
      Context var2 = this.getTargetContext();
      return var2.getNameParser(var1);
   }

   public NameParser getNameParser(String var1) throws NamingException {
      Context var2 = this.getTargetContext();
      return var2.getNameParser(var1);
   }

   public Name composeName(Name var1, Name var2) throws NamingException {
      Context var3 = this.getTargetContext();
      return var3.composeName(var1, var2);
   }

   public String composeName(String var1, String var2) throws NamingException {
      Context var3 = this.getTargetContext();
      return var3.composeName(var1, var2);
   }

   public Object addToEnvironment(String var1, Object var2) throws NamingException {
      Context var3 = this.getTargetContext();
      return var3.addToEnvironment(var1, var2);
   }

   public Object removeFromEnvironment(String var1) throws NamingException {
      Context var2 = this.getTargetContext();
      return var2.removeFromEnvironment(var1);
   }

   public Hashtable<?, ?> getEnvironment() throws NamingException {
      Context var1 = this.getTargetContext();
      return var1.getEnvironment();
   }

   public String getNameInNamespace() throws NamingException {
      Context var1 = this.getTargetContext();
      return var1.getNameInNamespace();
   }

   public ResolveResult resolveToClass(Name var1, Class<? extends Context> var2) throws NamingException {
      if (this.cpe.getResolvedObj() == null) {
         throw (NamingException)this.cpe.fillInStackTrace();
      } else {
         Resolver var3 = NamingManager.getResolver(this.cpe.getResolvedObj(), this.cpe.getAltName(), this.cpe.getAltNameCtx(), this.env);
         if (var3 == null) {
            throw (NamingException)this.cpe.fillInStackTrace();
         } else {
            return var3.resolveToClass(var1, var2);
         }
      }
   }

   public ResolveResult resolveToClass(String var1, Class<? extends Context> var2) throws NamingException {
      if (this.cpe.getResolvedObj() == null) {
         throw (NamingException)this.cpe.fillInStackTrace();
      } else {
         Resolver var3 = NamingManager.getResolver(this.cpe.getResolvedObj(), this.cpe.getAltName(), this.cpe.getAltNameCtx(), this.env);
         if (var3 == null) {
            throw (NamingException)this.cpe.fillInStackTrace();
         } else {
            return var3.resolveToClass(var1, var2);
         }
      }
   }

   public void close() throws NamingException {
      this.cpe = null;
      this.env = null;
      if (this.contCtx != null) {
         this.contCtx.close();
         this.contCtx = null;
      }

   }
}
