package javax.naming;

import com.sun.naming.internal.ResourceManager;
import java.util.Hashtable;
import javax.naming.spi.NamingManager;

public class InitialContext implements Context {
   protected Hashtable<Object, Object> myProps = null;
   protected Context defaultInitCtx = null;
   protected boolean gotDefault = false;

   protected InitialContext(boolean var1) throws NamingException {
      if (!var1) {
         this.init((Hashtable)null);
      }

   }

   public InitialContext() throws NamingException {
      this.init((Hashtable)null);
   }

   public InitialContext(Hashtable<?, ?> var1) throws NamingException {
      if (var1 != null) {
         var1 = (Hashtable)var1.clone();
      }

      this.init(var1);
   }

   protected void init(Hashtable<?, ?> var1) throws NamingException {
      this.myProps = ResourceManager.getInitialEnvironment(var1);
      if (this.myProps.get("java.naming.factory.initial") != null) {
         this.getDefaultInitCtx();
      }

   }

   public static <T> T doLookup(Name var0) throws NamingException {
      return (new InitialContext()).lookup(var0);
   }

   public static <T> T doLookup(String var0) throws NamingException {
      return (new InitialContext()).lookup(var0);
   }

   private static String getURLScheme(String var0) {
      int var1 = var0.indexOf(58);
      int var2 = var0.indexOf(47);
      return var1 <= 0 || var2 != -1 && var1 >= var2 ? null : var0.substring(0, var1);
   }

   protected Context getDefaultInitCtx() throws NamingException {
      if (!this.gotDefault) {
         this.defaultInitCtx = NamingManager.getInitialContext(this.myProps);
         this.gotDefault = true;
      }

      if (this.defaultInitCtx == null) {
         throw new NoInitialContextException();
      } else {
         return this.defaultInitCtx;
      }
   }

   protected Context getURLOrDefaultInitCtx(String var1) throws NamingException {
      if (NamingManager.hasInitialContextFactoryBuilder()) {
         return this.getDefaultInitCtx();
      } else {
         String var2 = getURLScheme(var1);
         if (var2 != null) {
            Context var3 = NamingManager.getURLContext(var2, this.myProps);
            if (var3 != null) {
               return var3;
            }
         }

         return this.getDefaultInitCtx();
      }
   }

   protected Context getURLOrDefaultInitCtx(Name var1) throws NamingException {
      if (NamingManager.hasInitialContextFactoryBuilder()) {
         return this.getDefaultInitCtx();
      } else {
         if (var1.size() > 0) {
            String var2 = var1.get(0);
            String var3 = getURLScheme(var2);
            if (var3 != null) {
               Context var4 = NamingManager.getURLContext(var3, this.myProps);
               if (var4 != null) {
                  return var4;
               }
            }
         }

         return this.getDefaultInitCtx();
      }
   }

   public Object lookup(String var1) throws NamingException {
      return this.getURLOrDefaultInitCtx(var1).lookup(var1);
   }

   public Object lookup(Name var1) throws NamingException {
      return this.getURLOrDefaultInitCtx(var1).lookup(var1);
   }

   public void bind(String var1, Object var2) throws NamingException {
      this.getURLOrDefaultInitCtx(var1).bind(var1, var2);
   }

   public void bind(Name var1, Object var2) throws NamingException {
      this.getURLOrDefaultInitCtx(var1).bind(var1, var2);
   }

   public void rebind(String var1, Object var2) throws NamingException {
      this.getURLOrDefaultInitCtx(var1).rebind(var1, var2);
   }

   public void rebind(Name var1, Object var2) throws NamingException {
      this.getURLOrDefaultInitCtx(var1).rebind(var1, var2);
   }

   public void unbind(String var1) throws NamingException {
      this.getURLOrDefaultInitCtx(var1).unbind(var1);
   }

   public void unbind(Name var1) throws NamingException {
      this.getURLOrDefaultInitCtx(var1).unbind(var1);
   }

   public void rename(String var1, String var2) throws NamingException {
      this.getURLOrDefaultInitCtx(var1).rename(var1, var2);
   }

   public void rename(Name var1, Name var2) throws NamingException {
      this.getURLOrDefaultInitCtx(var1).rename(var1, var2);
   }

   public NamingEnumeration<NameClassPair> list(String var1) throws NamingException {
      return this.getURLOrDefaultInitCtx(var1).list(var1);
   }

   public NamingEnumeration<NameClassPair> list(Name var1) throws NamingException {
      return this.getURLOrDefaultInitCtx(var1).list(var1);
   }

   public NamingEnumeration<Binding> listBindings(String var1) throws NamingException {
      return this.getURLOrDefaultInitCtx(var1).listBindings(var1);
   }

   public NamingEnumeration<Binding> listBindings(Name var1) throws NamingException {
      return this.getURLOrDefaultInitCtx(var1).listBindings(var1);
   }

   public void destroySubcontext(String var1) throws NamingException {
      this.getURLOrDefaultInitCtx(var1).destroySubcontext(var1);
   }

   public void destroySubcontext(Name var1) throws NamingException {
      this.getURLOrDefaultInitCtx(var1).destroySubcontext(var1);
   }

   public Context createSubcontext(String var1) throws NamingException {
      return this.getURLOrDefaultInitCtx(var1).createSubcontext(var1);
   }

   public Context createSubcontext(Name var1) throws NamingException {
      return this.getURLOrDefaultInitCtx(var1).createSubcontext(var1);
   }

   public Object lookupLink(String var1) throws NamingException {
      return this.getURLOrDefaultInitCtx(var1).lookupLink(var1);
   }

   public Object lookupLink(Name var1) throws NamingException {
      return this.getURLOrDefaultInitCtx(var1).lookupLink(var1);
   }

   public NameParser getNameParser(String var1) throws NamingException {
      return this.getURLOrDefaultInitCtx(var1).getNameParser(var1);
   }

   public NameParser getNameParser(Name var1) throws NamingException {
      return this.getURLOrDefaultInitCtx(var1).getNameParser(var1);
   }

   public String composeName(String var1, String var2) throws NamingException {
      return var1;
   }

   public Name composeName(Name var1, Name var2) throws NamingException {
      return (Name)var1.clone();
   }

   public Object addToEnvironment(String var1, Object var2) throws NamingException {
      this.myProps.put(var1, var2);
      return this.getDefaultInitCtx().addToEnvironment(var1, var2);
   }

   public Object removeFromEnvironment(String var1) throws NamingException {
      this.myProps.remove(var1);
      return this.getDefaultInitCtx().removeFromEnvironment(var1);
   }

   public Hashtable<?, ?> getEnvironment() throws NamingException {
      return this.getDefaultInitCtx().getEnvironment();
   }

   public void close() throws NamingException {
      this.myProps = null;
      if (this.defaultInitCtx != null) {
         this.defaultInitCtx.close();
         this.defaultInitCtx = null;
      }

      this.gotDefault = false;
   }

   public String getNameInNamespace() throws NamingException {
      return this.getDefaultInitCtx().getNameInNamespace();
   }
}
