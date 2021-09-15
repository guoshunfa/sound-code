package com.sun.jmx.mbeanserver;

import java.lang.reflect.Method;
import java.util.Map;
import javax.management.Attribute;
import javax.management.MBeanServerConnection;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

public class MXBeanProxy {
   private final Map<Method, MXBeanProxy.Handler> handlerMap = Util.newMap();

   public MXBeanProxy(Class<?> var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Null parameter");
      } else {
         MBeanAnalyzer var2;
         try {
            var2 = MXBeanIntrospector.getInstance().getAnalyzer(var1);
         } catch (NotCompliantMBeanException var4) {
            throw new IllegalArgumentException(var4);
         }

         var2.visit(new MXBeanProxy.Visitor());
      }
   }

   public Object invoke(MBeanServerConnection var1, ObjectName var2, Method var3, Object[] var4) throws Throwable {
      MXBeanProxy.Handler var5 = (MXBeanProxy.Handler)this.handlerMap.get(var3);
      ConvertingMethod var6 = var5.getConvertingMethod();
      MXBeanLookup var7 = MXBeanLookup.lookupFor(var1);
      MXBeanLookup var8 = MXBeanLookup.getLookup();

      Object var11;
      try {
         MXBeanLookup.setLookup(var7);
         Object[] var9 = var6.toOpenParameters(var7, var4);
         Object var10 = var5.invoke(var1, var2, var9);
         var11 = var6.fromOpenReturnValue(var7, var10);
      } finally {
         MXBeanLookup.setLookup(var8);
      }

      return var11;
   }

   private static class InvokeHandler extends MXBeanProxy.Handler {
      private final String[] signature;

      InvokeHandler(String var1, String[] var2, ConvertingMethod var3) {
         super(var1, var3);
         this.signature = var2;
      }

      Object invoke(MBeanServerConnection var1, ObjectName var2, Object[] var3) throws Exception {
         return var1.invoke(var2, this.getName(), var3, this.signature);
      }
   }

   private static class SetHandler extends MXBeanProxy.Handler {
      SetHandler(String var1, ConvertingMethod var2) {
         super(var1, var2);
      }

      Object invoke(MBeanServerConnection var1, ObjectName var2, Object[] var3) throws Exception {
         assert var3.length == 1;

         Attribute var4 = new Attribute(this.getName(), var3[0]);
         var1.setAttribute(var2, var4);
         return null;
      }
   }

   private static class GetHandler extends MXBeanProxy.Handler {
      GetHandler(String var1, ConvertingMethod var2) {
         super(var1, var2);
      }

      Object invoke(MBeanServerConnection var1, ObjectName var2, Object[] var3) throws Exception {
         assert var3 == null || var3.length == 0;

         return var1.getAttribute(var2, this.getName());
      }
   }

   private abstract static class Handler {
      private final String name;
      private final ConvertingMethod convertingMethod;

      Handler(String var1, ConvertingMethod var2) {
         this.name = var1;
         this.convertingMethod = var2;
      }

      String getName() {
         return this.name;
      }

      ConvertingMethod getConvertingMethod() {
         return this.convertingMethod;
      }

      abstract Object invoke(MBeanServerConnection var1, ObjectName var2, Object[] var3) throws Exception;
   }

   private class Visitor implements MBeanAnalyzer.MBeanVisitor<ConvertingMethod> {
      private Visitor() {
      }

      public void visitAttribute(String var1, ConvertingMethod var2, ConvertingMethod var3) {
         Method var4;
         if (var2 != null) {
            var2.checkCallToOpen();
            var4 = var2.getMethod();
            MXBeanProxy.this.handlerMap.put(var4, new MXBeanProxy.GetHandler(var1, var2));
         }

         if (var3 != null) {
            var4 = var3.getMethod();
            MXBeanProxy.this.handlerMap.put(var4, new MXBeanProxy.SetHandler(var1, var3));
         }

      }

      public void visitOperation(String var1, ConvertingMethod var2) {
         var2.checkCallToOpen();
         Method var3 = var2.getMethod();
         String[] var4 = var2.getOpenSignature();
         MXBeanProxy.this.handlerMap.put(var3, new MXBeanProxy.InvokeHandler(var1, var4, var2));
      }

      // $FF: synthetic method
      Visitor(Object var2) {
         this();
      }
   }
}
