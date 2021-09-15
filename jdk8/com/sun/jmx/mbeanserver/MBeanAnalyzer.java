package com.sun.jmx.mbeanserver;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.management.NotCompliantMBeanException;

class MBeanAnalyzer<M> {
   private Map<String, List<M>> opMap = Util.newInsertionOrderMap();
   private Map<String, MBeanAnalyzer.AttrMethods<M>> attrMap = Util.newInsertionOrderMap();

   void visit(MBeanAnalyzer.MBeanVisitor<M> var1) {
      Iterator var2 = this.attrMap.entrySet().iterator();

      Map.Entry var3;
      while(var2.hasNext()) {
         var3 = (Map.Entry)var2.next();
         String var4 = (String)var3.getKey();
         MBeanAnalyzer.AttrMethods var5 = (MBeanAnalyzer.AttrMethods)var3.getValue();
         var1.visitAttribute(var4, var5.getter, var5.setter);
      }

      var2 = this.opMap.entrySet().iterator();

      while(var2.hasNext()) {
         var3 = (Map.Entry)var2.next();
         Iterator var6 = ((List)var3.getValue()).iterator();

         while(var6.hasNext()) {
            Object var7 = var6.next();
            var1.visitOperation((String)var3.getKey(), var7);
         }
      }

   }

   static <M> MBeanAnalyzer<M> analyzer(Class<?> var0, MBeanIntrospector<M> var1) throws NotCompliantMBeanException {
      return new MBeanAnalyzer(var0, var1);
   }

   private MBeanAnalyzer(Class<?> var1, MBeanIntrospector<M> var2) throws NotCompliantMBeanException {
      if (!var1.isInterface()) {
         throw new NotCompliantMBeanException("Not an interface: " + var1.getName());
      } else if (!Modifier.isPublic(var1.getModifiers()) && !Introspector.ALLOW_NONPUBLIC_MBEAN) {
         throw new NotCompliantMBeanException("Interface is not public: " + var1.getName());
      } else {
         try {
            this.initMaps(var1, var2);
         } catch (Exception var4) {
            throw Introspector.throwException(var1, var4);
         }
      }
   }

   private void initMaps(Class<?> var1, MBeanIntrospector<M> var2) throws Exception {
      List var3 = var2.getMethods(var1);
      List var4 = eliminateCovariantMethods(var3);
      Iterator var5 = var4.iterator();

      while(true) {
         while(var5.hasNext()) {
            Method var6 = (Method)var5.next();
            String var7 = var6.getName();
            int var8 = var6.getParameterTypes().length;
            Object var9 = var2.mFrom(var6);
            String var10 = "";
            if (var7.startsWith("get")) {
               var10 = var7.substring(3);
            } else if (var7.startsWith("is") && var6.getReturnType() == Boolean.TYPE) {
               var10 = var7.substring(2);
            }

            String var12;
            MBeanAnalyzer.AttrMethods var16;
            if (var10.length() != 0 && var8 == 0 && var6.getReturnType() != Void.TYPE) {
               var16 = (MBeanAnalyzer.AttrMethods)this.attrMap.get(var10);
               if (var16 == null) {
                  var16 = new MBeanAnalyzer.AttrMethods();
               } else if (var16.getter != null) {
                  var12 = "Attribute " + var10 + " has more than one getter";
                  throw new NotCompliantMBeanException(var12);
               }

               var16.getter = var9;
               this.attrMap.put(var10, var16);
            } else if (var7.startsWith("set") && var7.length() > 3 && var8 == 1 && var6.getReturnType() == Void.TYPE) {
               var10 = var7.substring(3);
               var16 = (MBeanAnalyzer.AttrMethods)this.attrMap.get(var10);
               if (var16 == null) {
                  var16 = new MBeanAnalyzer.AttrMethods();
               } else if (var16.setter != null) {
                  var12 = "Attribute " + var10 + " has more than one setter";
                  throw new NotCompliantMBeanException(var12);
               }

               var16.setter = var9;
               this.attrMap.put(var10, var16);
            } else {
               List var11 = (List)this.opMap.get(var7);
               if (var11 == null) {
                  var11 = Util.newList();
               }

               var11.add(var9);
               this.opMap.put(var7, var11);
            }
         }

         var5 = this.attrMap.entrySet().iterator();

         Map.Entry var13;
         MBeanAnalyzer.AttrMethods var14;
         do {
            if (!var5.hasNext()) {
               return;
            }

            var13 = (Map.Entry)var5.next();
            var14 = (MBeanAnalyzer.AttrMethods)var13.getValue();
         } while(var2.consistent(var14.getter, var14.setter));

         String var15 = "Getter and setter for " + (String)var13.getKey() + " have inconsistent types";
         throw new NotCompliantMBeanException(var15);
      }
   }

   static List<Method> eliminateCovariantMethods(List<Method> var0) {
      int var1 = var0.size();
      Method[] var2 = (Method[])var0.toArray(new Method[var1]);
      Arrays.sort(var2, MBeanAnalyzer.MethodOrder.instance);
      Set var3 = Util.newSet();

      for(int var4 = 1; var4 < var1; ++var4) {
         Method var5 = var2[var4 - 1];
         Method var6 = var2[var4];
         if (var5.getName().equals(var6.getName()) && Arrays.equals((Object[])var5.getParameterTypes(), (Object[])var6.getParameterTypes()) && !var3.add(var5)) {
            throw new RuntimeException("Internal error: duplicate Method");
         }
      }

      List var7 = Util.newList(var0);
      var7.removeAll(var3);
      return var7;
   }

   private static class MethodOrder implements Comparator<Method> {
      public static final MBeanAnalyzer.MethodOrder instance = new MBeanAnalyzer.MethodOrder();

      public int compare(Method var1, Method var2) {
         int var3 = var1.getName().compareTo(var2.getName());
         if (var3 != 0) {
            return var3;
         } else {
            Class[] var4 = var1.getParameterTypes();
            Class[] var5 = var2.getParameterTypes();
            if (var4.length != var5.length) {
               return var4.length - var5.length;
            } else if (!Arrays.equals((Object[])var4, (Object[])var5)) {
               return Arrays.toString((Object[])var4).compareTo(Arrays.toString((Object[])var5));
            } else {
               Class var6 = var1.getReturnType();
               Class var7 = var2.getReturnType();
               if (var6 == var7) {
                  return 0;
               } else {
                  return var6.isAssignableFrom(var7) ? -1 : 1;
               }
            }
         }
      }
   }

   private static class AttrMethods<M> {
      M getter;
      M setter;

      private AttrMethods() {
      }

      // $FF: synthetic method
      AttrMethods(Object var1) {
         this();
      }
   }

   interface MBeanVisitor<M> {
      void visitAttribute(String var1, M var2, M var3);

      void visitOperation(String var1, M var2);
   }
}
