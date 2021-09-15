package com.sun.jndi.toolkit.ctx;

import java.util.Enumeration;
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
import javax.naming.spi.NamingManager;
import javax.naming.spi.ResolveResult;
import javax.naming.spi.Resolver;

public abstract class PartialCompositeContext implements Context, Resolver {
   protected static final int _PARTIAL = 1;
   protected static final int _COMPONENT = 2;
   protected static final int _ATOMIC = 3;
   protected int _contextType = 1;
   static final CompositeName _EMPTY_NAME = new CompositeName();
   static CompositeName _NNS_NAME;

   protected PartialCompositeContext() {
   }

   protected abstract ResolveResult p_resolveToClass(Name var1, Class<?> var2, Continuation var3) throws NamingException;

   protected abstract Object p_lookup(Name var1, Continuation var2) throws NamingException;

   protected abstract Object p_lookupLink(Name var1, Continuation var2) throws NamingException;

   protected abstract NamingEnumeration<NameClassPair> p_list(Name var1, Continuation var2) throws NamingException;

   protected abstract NamingEnumeration<Binding> p_listBindings(Name var1, Continuation var2) throws NamingException;

   protected abstract void p_bind(Name var1, Object var2, Continuation var3) throws NamingException;

   protected abstract void p_rebind(Name var1, Object var2, Continuation var3) throws NamingException;

   protected abstract void p_unbind(Name var1, Continuation var2) throws NamingException;

   protected abstract void p_destroySubcontext(Name var1, Continuation var2) throws NamingException;

   protected abstract Context p_createSubcontext(Name var1, Continuation var2) throws NamingException;

   protected abstract void p_rename(Name var1, Name var2, Continuation var3) throws NamingException;

   protected abstract NameParser p_getNameParser(Name var1, Continuation var2) throws NamingException;

   protected Hashtable<?, ?> p_getEnvironment() throws NamingException {
      return this.getEnvironment();
   }

   public ResolveResult resolveToClass(String var1, Class<? extends Context> var2) throws NamingException {
      return this.resolveToClass((Name)(new CompositeName(var1)), var2);
   }

   public ResolveResult resolveToClass(Name var1, Class<? extends Context> var2) throws NamingException {
      PartialCompositeContext var3 = this;
      Hashtable var4 = this.p_getEnvironment();
      Continuation var5 = new Continuation(var1, var4);
      Name var7 = var1;

      ResolveResult var6;
      try {
         for(var6 = var3.p_resolveToClass(var7, var2, var5); var5.isContinue(); var6 = var3.p_resolveToClass(var7, var2, var5)) {
            var7 = var5.getRemainingName();
            var3 = getPCContext(var5);
         }
      } catch (CannotProceedException var10) {
         Context var9 = NamingManager.getContinuationContext(var10);
         if (!(var9 instanceof Resolver)) {
            throw var10;
         }

         var6 = ((Resolver)var9).resolveToClass(var10.getRemainingName(), var2);
      }

      return var6;
   }

   public Object lookup(String var1) throws NamingException {
      return this.lookup((Name)(new CompositeName(var1)));
   }

   public Object lookup(Name var1) throws NamingException {
      PartialCompositeContext var2 = this;
      Hashtable var3 = this.p_getEnvironment();
      Continuation var4 = new Continuation(var1, var3);
      Name var6 = var1;

      Object var5;
      try {
         for(var5 = var2.p_lookup(var6, var4); var4.isContinue(); var5 = var2.p_lookup(var6, var4)) {
            var6 = var4.getRemainingName();
            var2 = getPCContext(var4);
         }
      } catch (CannotProceedException var9) {
         Context var8 = NamingManager.getContinuationContext(var9);
         var5 = var8.lookup(var9.getRemainingName());
      }

      return var5;
   }

   public void bind(String var1, Object var2) throws NamingException {
      this.bind((Name)(new CompositeName(var1)), var2);
   }

   public void bind(Name var1, Object var2) throws NamingException {
      PartialCompositeContext var3 = this;
      Name var4 = var1;
      Hashtable var5 = this.p_getEnvironment();
      Continuation var6 = new Continuation(var1, var5);

      try {
         var3.p_bind(var4, var2, var6);

         while(var6.isContinue()) {
            var4 = var6.getRemainingName();
            var3 = getPCContext(var6);
            var3.p_bind(var4, var2, var6);
         }
      } catch (CannotProceedException var9) {
         Context var8 = NamingManager.getContinuationContext(var9);
         var8.bind(var9.getRemainingName(), var2);
      }

   }

   public void rebind(String var1, Object var2) throws NamingException {
      this.rebind((Name)(new CompositeName(var1)), var2);
   }

   public void rebind(Name var1, Object var2) throws NamingException {
      PartialCompositeContext var3 = this;
      Name var4 = var1;
      Hashtable var5 = this.p_getEnvironment();
      Continuation var6 = new Continuation(var1, var5);

      try {
         var3.p_rebind(var4, var2, var6);

         while(var6.isContinue()) {
            var4 = var6.getRemainingName();
            var3 = getPCContext(var6);
            var3.p_rebind(var4, var2, var6);
         }
      } catch (CannotProceedException var9) {
         Context var8 = NamingManager.getContinuationContext(var9);
         var8.rebind(var9.getRemainingName(), var2);
      }

   }

   public void unbind(String var1) throws NamingException {
      this.unbind((Name)(new CompositeName(var1)));
   }

   public void unbind(Name var1) throws NamingException {
      PartialCompositeContext var2 = this;
      Name var3 = var1;
      Hashtable var4 = this.p_getEnvironment();
      Continuation var5 = new Continuation(var1, var4);

      try {
         var2.p_unbind(var3, var5);

         while(var5.isContinue()) {
            var3 = var5.getRemainingName();
            var2 = getPCContext(var5);
            var2.p_unbind(var3, var5);
         }
      } catch (CannotProceedException var8) {
         Context var7 = NamingManager.getContinuationContext(var8);
         var7.unbind(var8.getRemainingName());
      }

   }

   public void rename(String var1, String var2) throws NamingException {
      this.rename((Name)(new CompositeName(var1)), (Name)(new CompositeName(var2)));
   }

   public void rename(Name var1, Name var2) throws NamingException {
      PartialCompositeContext var3 = this;
      Name var4 = var1;
      Hashtable var5 = this.p_getEnvironment();
      Continuation var6 = new Continuation(var1, var5);

      try {
         var3.p_rename(var4, var2, var6);

         while(var6.isContinue()) {
            var4 = var6.getRemainingName();
            var3 = getPCContext(var6);
            var3.p_rename(var4, var2, var6);
         }
      } catch (CannotProceedException var9) {
         Context var8 = NamingManager.getContinuationContext(var9);
         if (var9.getRemainingNewName() != null) {
            var2 = var9.getRemainingNewName();
         }

         var8.rename(var9.getRemainingName(), var2);
      }

   }

   public NamingEnumeration<NameClassPair> list(String var1) throws NamingException {
      return this.list((Name)(new CompositeName(var1)));
   }

   public NamingEnumeration<NameClassPair> list(Name var1) throws NamingException {
      PartialCompositeContext var2 = this;
      Name var3 = var1;
      Hashtable var5 = this.p_getEnvironment();
      Continuation var6 = new Continuation(var1, var5);

      NamingEnumeration var4;
      try {
         for(var4 = var2.p_list(var3, var6); var6.isContinue(); var4 = var2.p_list(var3, var6)) {
            var3 = var6.getRemainingName();
            var2 = getPCContext(var6);
         }
      } catch (CannotProceedException var9) {
         Context var8 = NamingManager.getContinuationContext(var9);
         var4 = var8.list(var9.getRemainingName());
      }

      return var4;
   }

   public NamingEnumeration<Binding> listBindings(String var1) throws NamingException {
      return this.listBindings((Name)(new CompositeName(var1)));
   }

   public NamingEnumeration<Binding> listBindings(Name var1) throws NamingException {
      PartialCompositeContext var2 = this;
      Name var3 = var1;
      Hashtable var5 = this.p_getEnvironment();
      Continuation var6 = new Continuation(var1, var5);

      NamingEnumeration var4;
      try {
         for(var4 = var2.p_listBindings(var3, var6); var6.isContinue(); var4 = var2.p_listBindings(var3, var6)) {
            var3 = var6.getRemainingName();
            var2 = getPCContext(var6);
         }
      } catch (CannotProceedException var9) {
         Context var8 = NamingManager.getContinuationContext(var9);
         var4 = var8.listBindings(var9.getRemainingName());
      }

      return var4;
   }

   public void destroySubcontext(String var1) throws NamingException {
      this.destroySubcontext((Name)(new CompositeName(var1)));
   }

   public void destroySubcontext(Name var1) throws NamingException {
      PartialCompositeContext var2 = this;
      Name var3 = var1;
      Hashtable var4 = this.p_getEnvironment();
      Continuation var5 = new Continuation(var1, var4);

      try {
         var2.p_destroySubcontext(var3, var5);

         while(var5.isContinue()) {
            var3 = var5.getRemainingName();
            var2 = getPCContext(var5);
            var2.p_destroySubcontext(var3, var5);
         }
      } catch (CannotProceedException var8) {
         Context var7 = NamingManager.getContinuationContext(var8);
         var7.destroySubcontext(var8.getRemainingName());
      }

   }

   public Context createSubcontext(String var1) throws NamingException {
      return this.createSubcontext((Name)(new CompositeName(var1)));
   }

   public Context createSubcontext(Name var1) throws NamingException {
      PartialCompositeContext var2 = this;
      Name var3 = var1;
      Hashtable var5 = this.p_getEnvironment();
      Continuation var6 = new Continuation(var1, var5);

      Context var4;
      try {
         for(var4 = var2.p_createSubcontext(var3, var6); var6.isContinue(); var4 = var2.p_createSubcontext(var3, var6)) {
            var3 = var6.getRemainingName();
            var2 = getPCContext(var6);
         }
      } catch (CannotProceedException var9) {
         Context var8 = NamingManager.getContinuationContext(var9);
         var4 = var8.createSubcontext(var9.getRemainingName());
      }

      return var4;
   }

   public Object lookupLink(String var1) throws NamingException {
      return this.lookupLink((Name)(new CompositeName(var1)));
   }

   public Object lookupLink(Name var1) throws NamingException {
      PartialCompositeContext var2 = this;
      Hashtable var3 = this.p_getEnvironment();
      Continuation var4 = new Continuation(var1, var3);
      Name var6 = var1;

      Object var5;
      try {
         for(var5 = var2.p_lookupLink(var6, var4); var4.isContinue(); var5 = var2.p_lookupLink(var6, var4)) {
            var6 = var4.getRemainingName();
            var2 = getPCContext(var4);
         }
      } catch (CannotProceedException var9) {
         Context var8 = NamingManager.getContinuationContext(var9);
         var5 = var8.lookupLink(var9.getRemainingName());
      }

      return var5;
   }

   public NameParser getNameParser(String var1) throws NamingException {
      return this.getNameParser((Name)(new CompositeName(var1)));
   }

   public NameParser getNameParser(Name var1) throws NamingException {
      PartialCompositeContext var2 = this;
      Name var3 = var1;
      Hashtable var5 = this.p_getEnvironment();
      Continuation var6 = new Continuation(var1, var5);

      NameParser var4;
      try {
         for(var4 = var2.p_getNameParser(var3, var6); var6.isContinue(); var4 = var2.p_getNameParser(var3, var6)) {
            var3 = var6.getRemainingName();
            var2 = getPCContext(var6);
         }
      } catch (CannotProceedException var9) {
         Context var8 = NamingManager.getContinuationContext(var9);
         var4 = var8.getNameParser(var9.getRemainingName());
      }

      return var4;
   }

   public String composeName(String var1, String var2) throws NamingException {
      Name var3 = this.composeName((Name)(new CompositeName(var1)), (Name)(new CompositeName(var2)));
      return var3.toString();
   }

   public Name composeName(Name var1, Name var2) throws NamingException {
      Name var3 = (Name)var2.clone();
      if (var1 == null) {
         return var3;
      } else {
         var3.addAll(var1);
         String var4 = (String)this.p_getEnvironment().get("java.naming.provider.compose.elideEmpty");
         if (var4 != null && var4.equalsIgnoreCase("true")) {
            int var5 = var2.size();
            if (!allEmpty(var2) && !allEmpty(var1)) {
               if (var3.get(var5 - 1).equals("")) {
                  var3.remove(var5 - 1);
               } else if (var3.get(var5).equals("")) {
                  var3.remove(var5);
               }
            }

            return var3;
         } else {
            return var3;
         }
      }
   }

   protected static boolean allEmpty(Name var0) {
      Enumeration var1 = var0.getAll();

      do {
         if (!var1.hasMoreElements()) {
            return true;
         }
      } while(((String)var1.nextElement()).isEmpty());

      return false;
   }

   protected static PartialCompositeContext getPCContext(Continuation var0) throws NamingException {
      Object var1 = var0.getResolvedObj();
      Object var2 = null;
      if (var1 instanceof PartialCompositeContext) {
         return (PartialCompositeContext)var1;
      } else {
         throw var0.fillInException(new CannotProceedException());
      }
   }

   static {
      try {
         _NNS_NAME = new CompositeName("/");
      } catch (InvalidNameException var1) {
      }

   }
}
