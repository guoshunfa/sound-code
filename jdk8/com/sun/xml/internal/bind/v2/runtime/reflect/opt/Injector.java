package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

import com.sun.xml.internal.bind.Util;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

final class Injector {
   private static final ReentrantReadWriteLock irwl = new ReentrantReadWriteLock();
   private static final Lock ir;
   private static final Lock iw;
   private static final Map<ClassLoader, WeakReference<Injector>> injectors;
   private static final Logger logger;
   private final Map<String, Class> classes = new HashMap();
   private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
   private final Lock r;
   private final Lock w;
   private final ClassLoader parent;
   private final boolean loadable;
   private static final Method defineClass;
   private static final Method resolveClass;
   private static final Method findLoadedClass;

   static Class inject(ClassLoader cl, String className, byte[] image) {
      Injector injector = get(cl);
      return injector != null ? injector.inject(className, image) : null;
   }

   static Class find(ClassLoader cl, String className) {
      Injector injector = get(cl);
      return injector != null ? injector.find(className) : null;
   }

   private static Injector get(ClassLoader cl) {
      Injector injector = null;
      ir.lock();

      WeakReference wr;
      try {
         wr = (WeakReference)injectors.get(cl);
      } finally {
         ir.unlock();
      }

      if (wr != null) {
         injector = (Injector)wr.get();
      }

      if (injector == null) {
         try {
            wr = new WeakReference(injector = new Injector(cl));
            iw.lock();

            try {
               if (!injectors.containsKey(cl)) {
                  injectors.put(cl, wr);
               }
            } finally {
               iw.unlock();
            }
         } catch (SecurityException var12) {
            logger.log(Level.FINE, (String)"Unable to set up a back-door for the injector", (Throwable)var12);
            return null;
         }
      }

      return injector;
   }

   private Injector(ClassLoader parent) {
      this.r = this.rwl.readLock();
      this.w = this.rwl.writeLock();
      this.parent = parent;

      assert parent != null;

      boolean loadableCheck = false;

      try {
         loadableCheck = parent.loadClass(Accessor.class.getName()) == Accessor.class;
      } catch (ClassNotFoundException var4) {
      }

      this.loadable = loadableCheck;
   }

   private Class inject(String className, byte[] image) {
      if (!this.loadable) {
         return null;
      } else {
         boolean wlocked = false;
         boolean rlocked = false;

         try {
            this.r.lock();
            rlocked = true;
            Class c = (Class)this.classes.get(className);
            this.r.unlock();
            rlocked = false;
            Class var6;
            Throwable t;
            if (c == null) {
               try {
                  c = (Class)findLoadedClass.invoke(this.parent, className.replace('/', '.'));
               } catch (IllegalArgumentException var18) {
                  logger.log(Level.FINE, (String)("Unable to find " + className), (Throwable)var18);
               } catch (IllegalAccessException var19) {
                  logger.log(Level.FINE, (String)("Unable to find " + className), (Throwable)var19);
               } catch (InvocationTargetException var20) {
                  t = var20.getTargetException();
                  logger.log(Level.FINE, "Unable to find " + className, t);
               }

               if (c != null) {
                  this.w.lock();
                  wlocked = true;
                  this.classes.put(className, c);
                  this.w.unlock();
                  wlocked = false;
                  var6 = c;
                  return var6;
               }
            }

            if (c == null) {
               this.r.lock();
               rlocked = true;
               c = (Class)this.classes.get(className);
               this.r.unlock();
               rlocked = false;
               if (c == null) {
                  try {
                     c = (Class)defineClass.invoke(this.parent, className.replace('/', '.'), image, 0, image.length);
                     resolveClass.invoke(this.parent, c);
                  } catch (IllegalAccessException var21) {
                     logger.log(Level.FINE, (String)("Unable to inject " + className), (Throwable)var21);
                     t = null;
                     return t;
                  } catch (InvocationTargetException var22) {
                     t = var22.getTargetException();
                     if (t instanceof LinkageError) {
                        logger.log(Level.FINE, "duplicate class definition bug occured? Please report this : " + className, t);
                     } else {
                        logger.log(Level.FINE, "Unable to inject " + className, t);
                     }

                     Object var8 = null;
                     return (Class)var8;
                  } catch (SecurityException var23) {
                     logger.log(Level.FINE, (String)("Unable to inject " + className), (Throwable)var23);
                     t = null;
                     return t;
                  } catch (LinkageError var24) {
                     logger.log(Level.FINE, (String)("Unable to inject " + className), (Throwable)var24);
                     t = null;
                     return t;
                  }

                  this.w.lock();
                  wlocked = true;
                  if (!this.classes.containsKey(className)) {
                     this.classes.put(className, c);
                  }

                  this.w.unlock();
                  wlocked = false;
               }
            }

            var6 = c;
            return var6;
         } finally {
            if (rlocked) {
               this.r.unlock();
            }

            if (wlocked) {
               this.w.unlock();
            }

         }
      }
   }

   private Class find(String className) {
      this.r.lock();

      Class var2;
      try {
         var2 = (Class)this.classes.get(className);
      } finally {
         this.r.unlock();
      }

      return var2;
   }

   static {
      ir = irwl.readLock();
      iw = irwl.writeLock();
      injectors = new WeakHashMap();
      logger = Util.getClassLogger();

      try {
         defineClass = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, Integer.TYPE, Integer.TYPE);
         resolveClass = ClassLoader.class.getDeclaredMethod("resolveClass", Class.class);
         findLoadedClass = ClassLoader.class.getDeclaredMethod("findLoadedClass", String.class);
      } catch (NoSuchMethodException var1) {
         throw new NoSuchMethodError(var1.getMessage());
      }

      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            Injector.defineClass.setAccessible(true);
            Injector.resolveClass.setAccessible(true);
            Injector.findLoadedClass.setAccessible(true);
            return null;
         }
      });
   }
}
