package com.sun.corba.se.impl.interceptors;

import com.sun.corba.se.impl.logging.InterceptorsSystemException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import org.omg.PortableInterceptor.ClientRequestInterceptor;
import org.omg.PortableInterceptor.IORInterceptor;
import org.omg.PortableInterceptor.Interceptor;
import org.omg.PortableInterceptor.ServerRequestInterceptor;
import org.omg.PortableInterceptor.ORBInitInfoPackage.DuplicateName;

public class InterceptorList {
   static final int INTERCEPTOR_TYPE_CLIENT = 0;
   static final int INTERCEPTOR_TYPE_SERVER = 1;
   static final int INTERCEPTOR_TYPE_IOR = 2;
   static final int NUM_INTERCEPTOR_TYPES = 3;
   static final Class[] classTypes = new Class[]{ClientRequestInterceptor.class, ServerRequestInterceptor.class, IORInterceptor.class};
   private boolean locked = false;
   private InterceptorsSystemException wrapper;
   private Interceptor[][] interceptors = new Interceptor[3][];

   InterceptorList(InterceptorsSystemException var1) {
      this.wrapper = var1;
      this.initInterceptorArrays();
   }

   void register_interceptor(Interceptor var1, int var2) throws DuplicateName {
      if (this.locked) {
         throw this.wrapper.interceptorListLocked();
      } else {
         String var3 = var1.name();
         boolean var4 = var3.equals("");
         boolean var5 = false;
         Interceptor[] var6 = this.interceptors[var2];
         if (!var4) {
            int var7 = var6.length;

            for(int var8 = 0; var8 < var7; ++var8) {
               Interceptor var9 = var6[var8];
               if (var9.name().equals(var3)) {
                  var5 = true;
                  break;
               }
            }
         }

         if (!var5) {
            this.growInterceptorArray(var2);
            this.interceptors[var2][this.interceptors[var2].length - 1] = var1;
         } else {
            throw new DuplicateName(var3);
         }
      }
   }

   void lock() {
      this.locked = true;
   }

   Interceptor[] getInterceptors(int var1) {
      return this.interceptors[var1];
   }

   boolean hasInterceptorsOfType(int var1) {
      return this.interceptors[var1].length > 0;
   }

   private void initInterceptorArrays() {
      for(int var1 = 0; var1 < 3; ++var1) {
         Class var2 = classTypes[var1];
         this.interceptors[var1] = (Interceptor[])((Interceptor[])Array.newInstance(var2, 0));
      }

   }

   private void growInterceptorArray(int var1) {
      Class var2 = classTypes[var1];
      int var3 = this.interceptors[var1].length;
      Interceptor[] var4 = (Interceptor[])((Interceptor[])Array.newInstance(var2, var3 + 1));
      System.arraycopy(this.interceptors[var1], 0, var4, 0, var3);
      this.interceptors[var1] = var4;
   }

   void destroyAll() {
      int var1 = this.interceptors.length;

      for(int var2 = 0; var2 < var1; ++var2) {
         int var3 = this.interceptors[var2].length;

         for(int var4 = 0; var4 < var3; ++var4) {
            this.interceptors[var2][var4].destroy();
         }
      }

   }

   void sortInterceptors() {
      ArrayList var1 = null;
      ArrayList var2 = null;
      int var3 = this.interceptors.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         int var5 = this.interceptors[var4].length;
         if (var5 > 0) {
            var1 = new ArrayList();
            var2 = new ArrayList();
         }

         for(int var6 = 0; var6 < var5; ++var6) {
            Interceptor var7 = this.interceptors[var4][var6];
            if (var7 instanceof Comparable) {
               var1.add(var7);
            } else {
               var2.add(var7);
            }
         }

         if (var5 > 0 && var1.size() > 0) {
            Collections.sort(var1);
            Iterator var9 = var1.iterator();
            Iterator var10 = var2.iterator();

            for(int var8 = 0; var8 < var5; ++var8) {
               if (var9.hasNext()) {
                  this.interceptors[var4][var8] = (Interceptor)var9.next();
               } else {
                  if (!var10.hasNext()) {
                     throw this.wrapper.sortSizeMismatch();
                  }

                  this.interceptors[var4][var8] = (Interceptor)var10.next();
               }
            }
         }
      }

   }
}
