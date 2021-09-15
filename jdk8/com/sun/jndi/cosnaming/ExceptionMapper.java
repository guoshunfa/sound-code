package com.sun.jndi.cosnaming;

import com.sun.jndi.toolkit.corba.CorbaUtils;
import javax.naming.CannotProceedException;
import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.ContextNotEmptyException;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.NamingManager;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextPackage.AlreadyBound;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotEmpty;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.CosNaming.NamingContextPackage.NotFoundReason;

public final class ExceptionMapper {
   private static final boolean debug = false;

   private ExceptionMapper() {
   }

   public static final NamingException mapException(Exception var0, CNCtx var1, NameComponent[] var2) throws NamingException {
      if (var0 instanceof NamingException) {
         return (NamingException)var0;
      } else if (var0 instanceof RuntimeException) {
         throw (RuntimeException)var0;
      } else {
         Object var3;
         if (var0 instanceof NotFound) {
            if (var1.federation) {
               return tryFed((NotFound)var0, var1, var2);
            }

            var3 = new NameNotFoundException();
         } else if (var0 instanceof CannotProceed) {
            var3 = new CannotProceedException();
            NamingContext var4 = ((CannotProceed)var0).cxt;
            NameComponent[] var5 = ((CannotProceed)var0).rest_of_name;
            if (var2 != null && var2.length > var5.length) {
               NameComponent[] var6 = new NameComponent[var2.length - var5.length];
               System.arraycopy(var2, 0, var6, 0, var6.length);
               ((NamingException)var3).setResolvedObj(new CNCtx(var1._orb, var1.orbTracker, var4, var1._env, var1.makeFullName(var6)));
            } else {
               ((NamingException)var3).setResolvedObj(var1);
            }

            ((NamingException)var3).setRemainingName(CNNameParser.cosNameToName(var5));
         } else if (var0 instanceof InvalidName) {
            var3 = new InvalidNameException();
         } else if (var0 instanceof AlreadyBound) {
            var3 = new NameAlreadyBoundException();
         } else if (var0 instanceof NotEmpty) {
            var3 = new ContextNotEmptyException();
         } else {
            var3 = new NamingException("Unknown reasons");
         }

         ((NamingException)var3).setRootCause(var0);
         return (NamingException)var3;
      }
   }

   private static final NamingException tryFed(NotFound var0, CNCtx var1, NameComponent[] var2) throws NamingException {
      NameComponent[] var3 = var0.rest_of_name;
      if (var3.length == 1 && var2 != null) {
         NameComponent var4 = var2[var2.length - 1];
         if (!var3[0].id.equals(var4.id) || var3[0].kind == null || !var3[0].kind.equals(var4.kind)) {
            NameNotFoundException var17 = new NameNotFoundException();
            var17.setRemainingName(CNNameParser.cosNameToName(var3));
            var17.setRootCause(var0);
            throw var17;
         }
      }

      NameComponent[] var15 = null;
      boolean var5 = false;
      if (var2 != null && var2.length >= var3.length) {
         int var16;
         if (var0.why == NotFoundReason.not_context) {
            var16 = var2.length - (var3.length - 1);
            if (var3.length == 1) {
               var3 = null;
            } else {
               NameComponent[] var6 = new NameComponent[var3.length - 1];
               System.arraycopy(var3, 1, var6, 0, var6.length);
               var3 = var6;
            }
         } else {
            var16 = var2.length - var3.length;
         }

         if (var16 > 0) {
            var15 = new NameComponent[var16];
            System.arraycopy(var2, 0, var15, 0, var16);
         }
      }

      CannotProceedException var18 = new CannotProceedException();
      var18.setRootCause(var0);
      if (var3 != null && var3.length > 0) {
         var18.setRemainingName(CNNameParser.cosNameToName(var3));
      }

      var18.setEnvironment(var1._env);
      final Object var7 = var15 != null ? var1.callResolve(var15) : var1;
      if (var7 instanceof Context) {
         RefAddr var19 = new RefAddr("nns") {
            private static final long serialVersionUID = 669984699392133792L;

            public Object getContent() {
               return var7;
            }
         };
         Reference var20 = new Reference("java.lang.Object", var19);
         CompositeName var10 = new CompositeName();
         var10.add("");
         var18.setResolvedObj(var20);
         var18.setAltName(var10);
         var18.setAltNameCtx((Context)var7);
         return var18;
      } else {
         Name var8 = CNNameParser.cosNameToName(var15);
         final Object var9 = null;

         try {
            if (CorbaUtils.isObjectFactoryTrusted(var7)) {
               var9 = NamingManager.getObjectInstance(var7, var8, var1, var1._env);
            }
         } catch (NamingException var13) {
            throw var13;
         } catch (Exception var14) {
            NamingException var11 = new NamingException("problem generating object using object factory");
            var11.setRootCause(var14);
            throw var11;
         }

         if (var9 instanceof Context) {
            var18.setResolvedObj(var9);
         } else {
            var8.add("");
            var18.setAltName(var8);
            RefAddr var21 = new RefAddr("nns") {
               private static final long serialVersionUID = -785132553978269772L;

               public Object getContent() {
                  return var9;
               }
            };
            Reference var12 = new Reference("java.lang.Object", var21);
            var18.setResolvedObj(var12);
            var18.setAltNameCtx(var1);
         }

         return var18;
      }
   }
}
