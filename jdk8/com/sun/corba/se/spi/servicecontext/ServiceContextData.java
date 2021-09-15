package com.sun.corba.se.spi.servicecontext;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORB;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA_2_3.portable.InputStream;

public class ServiceContextData {
   private Class scClass;
   private Constructor scConstructor;
   private int scId;

   private void dprint(String var1) {
      ORBUtility.dprint((Object)this, var1);
   }

   private void throwBadParam(String var1, Throwable var2) {
      BAD_PARAM var3 = new BAD_PARAM(var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      throw var3;
   }

   public ServiceContextData(Class var1) {
      if (ORB.ORBInitDebug) {
         this.dprint("ServiceContextData constructor called for class " + var1);
      }

      this.scClass = var1;

      try {
         if (ORB.ORBInitDebug) {
            this.dprint("Finding constructor for " + var1);
         }

         Class[] var2 = new Class[]{InputStream.class, GIOPVersion.class};

         try {
            this.scConstructor = var1.getConstructor(var2);
         } catch (NoSuchMethodException var10) {
            this.throwBadParam("Class does not have an InputStream constructor", var10);
         }

         if (ORB.ORBInitDebug) {
            this.dprint("Finding SERVICE_CONTEXT_ID field in " + var1);
         }

         Field var3 = null;

         try {
            var3 = var1.getField("SERVICE_CONTEXT_ID");
         } catch (NoSuchFieldException var8) {
            this.throwBadParam("Class does not have a SERVICE_CONTEXT_ID member", var8);
         } catch (SecurityException var9) {
            this.throwBadParam("Could not access SERVICE_CONTEXT_ID member", var9);
         }

         if (ORB.ORBInitDebug) {
            this.dprint("Checking modifiers of SERVICE_CONTEXT_ID field in " + var1);
         }

         int var4 = var3.getModifiers();
         if (!Modifier.isPublic(var4) || !Modifier.isStatic(var4) || !Modifier.isFinal(var4)) {
            this.throwBadParam("SERVICE_CONTEXT_ID field is not public static final", (Throwable)null);
         }

         if (ORB.ORBInitDebug) {
            this.dprint("Getting value of SERVICE_CONTEXT_ID in " + var1);
         }

         try {
            this.scId = var3.getInt((Object)null);
         } catch (IllegalArgumentException var6) {
            this.throwBadParam("SERVICE_CONTEXT_ID not convertible to int", var6);
         } catch (IllegalAccessException var7) {
            this.throwBadParam("Could not access value of SERVICE_CONTEXT_ID", var7);
         }
      } catch (BAD_PARAM var11) {
         if (ORB.ORBInitDebug) {
            this.dprint("Exception in ServiceContextData constructor: " + var11);
         }

         throw var11;
      } catch (Throwable var12) {
         if (ORB.ORBInitDebug) {
            this.dprint("Unexpected Exception in ServiceContextData constructor: " + var12);
         }
      }

      if (ORB.ORBInitDebug) {
         this.dprint("ServiceContextData constructor completed");
      }

   }

   public ServiceContext makeServiceContext(InputStream var1, GIOPVersion var2) {
      Object[] var3 = new Object[]{var1, var2};
      ServiceContext var4 = null;

      try {
         var4 = (ServiceContext)((ServiceContext)this.scConstructor.newInstance(var3));
      } catch (IllegalArgumentException var6) {
         this.throwBadParam("InputStream constructor argument error", var6);
      } catch (IllegalAccessException var7) {
         this.throwBadParam("InputStream constructor argument error", var7);
      } catch (InstantiationException var8) {
         this.throwBadParam("InputStream constructor called for abstract class", var8);
      } catch (InvocationTargetException var9) {
         this.throwBadParam("InputStream constructor threw exception " + var9.getTargetException(), var9);
      }

      return var4;
   }

   int getId() {
      return this.scId;
   }

   public String toString() {
      return "ServiceContextData[ scClass=" + this.scClass + " scConstructor=" + this.scConstructor + " scId=" + this.scId + " ]";
   }
}
