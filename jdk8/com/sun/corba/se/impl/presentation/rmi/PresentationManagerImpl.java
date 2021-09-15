package com.sun.corba.se.impl.presentation.rmi;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.graph.Graph;
import com.sun.corba.se.impl.orbutil.graph.GraphImpl;
import com.sun.corba.se.impl.orbutil.graph.Node;
import com.sun.corba.se.spi.orbutil.proxy.InvocationHandlerFactory;
import com.sun.corba.se.spi.presentation.rmi.DynamicMethodMarshaller;
import com.sun.corba.se.spi.presentation.rmi.IDLNameTranslator;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager;
import java.lang.reflect.Method;
import java.rmi.Remote;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.rmi.CORBA.Tie;

public final class PresentationManagerImpl implements PresentationManager {
   private Map classToClassData;
   private Map methodToDMM;
   private PresentationManager.StubFactoryFactory staticStubFactoryFactory;
   private PresentationManager.StubFactoryFactory dynamicStubFactoryFactory;
   private ORBUtilSystemException wrapper = null;
   private boolean useDynamicStubs;

   public PresentationManagerImpl(boolean var1) {
      this.useDynamicStubs = var1;
      this.wrapper = ORBUtilSystemException.get("rpc.presentation");
      this.classToClassData = new HashMap();
      this.methodToDMM = new HashMap();
   }

   public synchronized DynamicMethodMarshaller getDynamicMethodMarshaller(Method var1) {
      if (var1 == null) {
         return null;
      } else {
         Object var2 = (DynamicMethodMarshaller)this.methodToDMM.get(var1);
         if (var2 == null) {
            var2 = new DynamicMethodMarshallerImpl(var1);
            this.methodToDMM.put(var1, var2);
         }

         return (DynamicMethodMarshaller)var2;
      }
   }

   public synchronized PresentationManager.ClassData getClassData(Class var1) {
      Object var2 = (PresentationManager.ClassData)this.classToClassData.get(var1);
      if (var2 == null) {
         var2 = new PresentationManagerImpl.ClassDataImpl(var1);
         this.classToClassData.put(var1, var2);
      }

      return (PresentationManager.ClassData)var2;
   }

   public PresentationManager.StubFactoryFactory getStubFactoryFactory(boolean var1) {
      return var1 ? this.dynamicStubFactoryFactory : this.staticStubFactoryFactory;
   }

   public void setStubFactoryFactory(boolean var1, PresentationManager.StubFactoryFactory var2) {
      if (var1) {
         this.dynamicStubFactoryFactory = var2;
      } else {
         this.staticStubFactoryFactory = var2;
      }

   }

   public Tie getTie() {
      return this.dynamicStubFactoryFactory.getTie((Class)null);
   }

   public boolean useDynamicStubs() {
      return this.useDynamicStubs;
   }

   private Set getRootSet(Class var1, PresentationManagerImpl.NodeImpl var2, Graph var3) {
      Set var4 = null;
      if (var1.isInterface()) {
         var3.add(var2);
         var4 = var3.getRoots();
      } else {
         Class var5 = var1;

         HashSet var6;
         for(var6 = new HashSet(); var5 != null && !var5.equals(Object.class); var5 = var5.getSuperclass()) {
            PresentationManagerImpl.NodeImpl var7 = new PresentationManagerImpl.NodeImpl(var5);
            var3.add(var7);
            var6.add(var7);
         }

         var3.getRoots();
         var3.removeAll(var6);
         var4 = var3.getRoots();
      }

      return var4;
   }

   private Class[] getInterfaces(Set var1) {
      Class[] var2 = new Class[var1.size()];
      Iterator var3 = var1.iterator();

      PresentationManagerImpl.NodeImpl var5;
      for(int var4 = 0; var3.hasNext(); var2[var4++] = var5.getInterface()) {
         var5 = (PresentationManagerImpl.NodeImpl)var3.next();
      }

      return var2;
   }

   private String[] makeTypeIds(PresentationManagerImpl.NodeImpl var1, Graph var2, Set var3) {
      HashSet var4 = new HashSet(var2);
      var4.removeAll(var3);
      ArrayList var5 = new ArrayList();
      if (var3.size() > 1) {
         var5.add(var1.getTypeId());
      }

      this.addNodes(var5, var3);
      this.addNodes(var5, var4);
      return (String[])((String[])var5.toArray(new String[var5.size()]));
   }

   private void addNodes(List var1, Set var2) {
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         PresentationManagerImpl.NodeImpl var4 = (PresentationManagerImpl.NodeImpl)var3.next();
         String var5 = var4.getTypeId();
         var1.add(var5);
      }

   }

   private static class NodeImpl implements Node {
      private Class interf;

      public Class getInterface() {
         return this.interf;
      }

      public NodeImpl(Class var1) {
         this.interf = var1;
      }

      public String getTypeId() {
         return "RMI:" + this.interf.getName() + ":0000000000000000";
      }

      public Set getChildren() {
         HashSet var1 = new HashSet();
         Class[] var2 = this.interf.getInterfaces();

         for(int var3 = 0; var3 < var2.length; ++var3) {
            Class var4 = var2[var3];
            if (Remote.class.isAssignableFrom(var4) && !Remote.class.equals(var4)) {
               var1.add(new PresentationManagerImpl.NodeImpl(var4));
            }
         }

         return var1;
      }

      public String toString() {
         return "NodeImpl[" + this.interf + "]";
      }

      public int hashCode() {
         return this.interf.hashCode();
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (!(var1 instanceof PresentationManagerImpl.NodeImpl)) {
            return false;
         } else {
            PresentationManagerImpl.NodeImpl var2 = (PresentationManagerImpl.NodeImpl)var1;
            return var2.interf.equals(this.interf);
         }
      }
   }

   private class ClassDataImpl implements PresentationManager.ClassData {
      private Class cls;
      private IDLNameTranslator nameTranslator;
      private String[] typeIds;
      private PresentationManager.StubFactory sfactory;
      private InvocationHandlerFactory ihfactory;
      private Map dictionary;

      public ClassDataImpl(Class var2) {
         this.cls = var2;
         GraphImpl var3 = new GraphImpl();
         PresentationManagerImpl.NodeImpl var4 = new PresentationManagerImpl.NodeImpl(var2);
         Set var5 = PresentationManagerImpl.this.getRootSet(var2, var4, var3);
         Class[] var6 = PresentationManagerImpl.this.getInterfaces(var5);
         this.nameTranslator = IDLNameTranslatorImpl.get(var6);
         this.typeIds = PresentationManagerImpl.this.makeTypeIds(var4, var3, var5);
         this.ihfactory = new InvocationHandlerFactoryImpl(PresentationManagerImpl.this, this);
         this.dictionary = new HashMap();
      }

      public Class getMyClass() {
         return this.cls;
      }

      public IDLNameTranslator getIDLNameTranslator() {
         return this.nameTranslator;
      }

      public String[] getTypeIds() {
         return this.typeIds;
      }

      public InvocationHandlerFactory getInvocationHandlerFactory() {
         return this.ihfactory;
      }

      public Map getDictionary() {
         return this.dictionary;
      }
   }
}
