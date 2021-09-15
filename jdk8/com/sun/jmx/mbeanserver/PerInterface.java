package com.sun.jmx.mbeanserver;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.management.AttributeNotFoundException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.ReflectionException;

final class PerInterface<M> {
   private final Class<?> mbeanInterface;
   private final MBeanIntrospector<M> introspector;
   private final MBeanInfo mbeanInfo;
   private final Map<String, M> getters = Util.newMap();
   private final Map<String, M> setters = Util.newMap();
   private final Map<String, List<PerInterface<M>.MethodAndSig>> ops = Util.newMap();

   PerInterface(Class<?> var1, MBeanIntrospector<M> var2, MBeanAnalyzer<M> var3, MBeanInfo var4) {
      this.mbeanInterface = var1;
      this.introspector = var2;
      this.mbeanInfo = var4;
      var3.visit(new PerInterface.InitMaps());
   }

   Class<?> getMBeanInterface() {
      return this.mbeanInterface;
   }

   MBeanInfo getMBeanInfo() {
      return this.mbeanInfo;
   }

   boolean isMXBean() {
      return this.introspector.isMXBean();
   }

   Object getAttribute(Object var1, String var2, Object var3) throws AttributeNotFoundException, MBeanException, ReflectionException {
      Object var4 = this.getters.get(var2);
      if (var4 == null) {
         String var5;
         if (this.setters.containsKey(var2)) {
            var5 = "Write-only attribute: " + var2;
         } else {
            var5 = "No such attribute: " + var2;
         }

         throw new AttributeNotFoundException(var5);
      } else {
         return this.introspector.invokeM(var4, var1, (Object[])null, var3);
      }
   }

   void setAttribute(Object var1, String var2, Object var3, Object var4) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
      Object var5 = this.setters.get(var2);
      if (var5 == null) {
         String var6;
         if (this.getters.containsKey(var2)) {
            var6 = "Read-only attribute: " + var2;
         } else {
            var6 = "No such attribute: " + var2;
         }

         throw new AttributeNotFoundException(var6);
      } else {
         this.introspector.invokeSetter(var2, var5, var1, var3, var4);
      }
   }

   Object invoke(Object var1, String var2, Object[] var3, String[] var4, Object var5) throws MBeanException, ReflectionException {
      List var6 = (List)this.ops.get(var2);
      if (var6 == null) {
         String var10 = "No such operation: " + var2;
         return this.noSuchMethod(var10, var1, var2, var3, var4, var5);
      } else {
         if (var4 == null) {
            var4 = new String[0];
         }

         PerInterface.MethodAndSig var7 = null;
         Iterator var8 = var6.iterator();

         while(var8.hasNext()) {
            PerInterface.MethodAndSig var9 = (PerInterface.MethodAndSig)var8.next();
            if (Arrays.equals((Object[])var9.signature, (Object[])var4)) {
               var7 = var9;
               break;
            }
         }

         if (var7 == null) {
            String var11 = this.sigString(var4);
            String var12;
            if (var6.size() == 1) {
               var12 = "Signature mismatch for operation " + var2 + ": " + var11 + " should be " + this.sigString(((PerInterface.MethodAndSig)var6.get(0)).signature);
            } else {
               var12 = "Operation " + var2 + " exists but not with this signature: " + var11;
            }

            return this.noSuchMethod(var12, var1, var2, var3, var4, var5);
         } else {
            return this.introspector.invokeM(var7.method, var1, var3, var5);
         }
      }
   }

   private Object noSuchMethod(String var1, Object var2, String var3, Object[] var4, String[] var5, Object var6) throws MBeanException, ReflectionException {
      NoSuchMethodException var7 = new NoSuchMethodException(var3 + this.sigString(var5));
      ReflectionException var8 = new ReflectionException(var7, var1);
      if (this.introspector.isMXBean()) {
         throw var8;
      } else {
         GetPropertyAction var9 = new GetPropertyAction("jmx.invoke.getters");

         String var10;
         try {
            var10 = (String)AccessController.doPrivileged((PrivilegedAction)var9);
         } catch (Exception var16) {
            var10 = null;
         }

         if (var10 == null) {
            throw var8;
         } else {
            byte var11 = 0;
            Map var12 = null;
            if (var5 != null && var5.length != 0) {
               if (var5.length == 1 && var3.startsWith("set")) {
                  var11 = 3;
                  var12 = this.setters;
               }
            } else {
               if (var3.startsWith("get")) {
                  var11 = 3;
               } else if (var3.startsWith("is")) {
                  var11 = 2;
               }

               if (var11 != 0) {
                  var12 = this.getters;
               }
            }

            if (var11 != 0) {
               String var13 = var3.substring(var11);
               Object var14 = var12.get(var13);
               if (var14 != null && this.introspector.getName(var14).equals(var3)) {
                  String[] var15 = this.introspector.getSignature(var14);
                  if (var5 == null && var15.length == 0 || Arrays.equals((Object[])var5, (Object[])var15)) {
                     return this.introspector.invokeM(var14, var2, var4, var6);
                  }
               }
            }

            throw var8;
         }
      }
   }

   private String sigString(String[] var1) {
      StringBuilder var2 = new StringBuilder("(");
      if (var1 != null) {
         String[] var3 = var1;
         int var4 = var1.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            String var6 = var3[var5];
            if (var2.length() > 1) {
               var2.append(", ");
            }

            var2.append(var6);
         }
      }

      return var2.append(")").toString();
   }

   private class MethodAndSig {
      M method;
      String[] signature;

      private MethodAndSig() {
      }

      // $FF: synthetic method
      MethodAndSig(Object var2) {
         this();
      }
   }

   private class InitMaps implements MBeanAnalyzer.MBeanVisitor<M> {
      private InitMaps() {
      }

      public void visitAttribute(String var1, M var2, M var3) {
         Object var4;
         if (var2 != null) {
            PerInterface.this.introspector.checkMethod(var2);
            var4 = PerInterface.this.getters.put(var1, var2);

            assert var4 == null;
         }

         if (var3 != null) {
            PerInterface.this.introspector.checkMethod(var3);
            var4 = PerInterface.this.setters.put(var1, var3);

            assert var4 == null;
         }

      }

      public void visitOperation(String var1, M var2) {
         PerInterface.this.introspector.checkMethod(var2);
         String[] var3 = PerInterface.this.introspector.getSignature(var2);
         PerInterface.MethodAndSig var4 = PerInterface.this.new MethodAndSig();
         var4.method = var2;
         var4.signature = var3;
         List var5 = (List)PerInterface.this.ops.get(var1);
         if (var5 == null) {
            var5 = Collections.singletonList(var4);
         } else {
            if (var5.size() == 1) {
               var5 = Util.newList(var5);
            }

            var5.add(var4);
         }

         PerInterface.this.ops.put(var1, var5);
      }

      // $FF: synthetic method
      InitMaps(Object var2) {
         this();
      }
   }
}
