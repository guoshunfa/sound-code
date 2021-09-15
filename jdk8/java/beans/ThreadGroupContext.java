package java.beans;

import com.sun.beans.finder.BeanInfoFinder;
import com.sun.beans.finder.PropertyEditorFinder;
import java.awt.GraphicsEnvironment;
import java.util.Map;
import java.util.WeakHashMap;

final class ThreadGroupContext {
   private static final WeakIdentityMap<ThreadGroupContext> contexts = new WeakIdentityMap<ThreadGroupContext>() {
      protected ThreadGroupContext create(Object var1) {
         return new ThreadGroupContext();
      }
   };
   private volatile boolean isDesignTime;
   private volatile Boolean isGuiAvailable;
   private Map<Class<?>, BeanInfo> beanInfoCache;
   private BeanInfoFinder beanInfoFinder;
   private PropertyEditorFinder propertyEditorFinder;

   static ThreadGroupContext getContext() {
      return (ThreadGroupContext)contexts.get(Thread.currentThread().getThreadGroup());
   }

   private ThreadGroupContext() {
   }

   boolean isDesignTime() {
      return this.isDesignTime;
   }

   void setDesignTime(boolean var1) {
      this.isDesignTime = var1;
   }

   boolean isGuiAvailable() {
      Boolean var1 = this.isGuiAvailable;
      return var1 != null ? var1 : !GraphicsEnvironment.isHeadless();
   }

   void setGuiAvailable(boolean var1) {
      this.isGuiAvailable = var1;
   }

   BeanInfo getBeanInfo(Class<?> var1) {
      return this.beanInfoCache != null ? (BeanInfo)this.beanInfoCache.get(var1) : null;
   }

   BeanInfo putBeanInfo(Class<?> var1, BeanInfo var2) {
      if (this.beanInfoCache == null) {
         this.beanInfoCache = new WeakHashMap();
      }

      return (BeanInfo)this.beanInfoCache.put(var1, var2);
   }

   void removeBeanInfo(Class<?> var1) {
      if (this.beanInfoCache != null) {
         this.beanInfoCache.remove(var1);
      }

   }

   void clearBeanInfoCache() {
      if (this.beanInfoCache != null) {
         this.beanInfoCache.clear();
      }

   }

   synchronized BeanInfoFinder getBeanInfoFinder() {
      if (this.beanInfoFinder == null) {
         this.beanInfoFinder = new BeanInfoFinder();
      }

      return this.beanInfoFinder;
   }

   synchronized PropertyEditorFinder getPropertyEditorFinder() {
      if (this.propertyEditorFinder == null) {
         this.propertyEditorFinder = new PropertyEditorFinder();
      }

      return this.propertyEditorFinder;
   }

   // $FF: synthetic method
   ThreadGroupContext(Object var1) {
      this();
   }
}
