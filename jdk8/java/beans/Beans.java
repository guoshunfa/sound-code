package java.beans;

import com.sun.beans.finder.ClassFinder;
import java.applet.Applet;
import java.beans.beancontext.BeanContext;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Modifier;
import java.net.URL;

public class Beans {
   public static Object instantiate(ClassLoader var0, String var1) throws IOException, ClassNotFoundException {
      return instantiate(var0, var1, (BeanContext)null, (AppletInitializer)null);
   }

   public static Object instantiate(ClassLoader var0, String var1, BeanContext var2) throws IOException, ClassNotFoundException {
      return instantiate(var0, var1, var2, (AppletInitializer)null);
   }

   public static Object instantiate(ClassLoader var0, String var1, BeanContext var2, AppletInitializer var3) throws IOException, ClassNotFoundException {
      Object var5 = null;
      Object var6 = null;
      boolean var7 = false;
      IOException var8 = null;
      if (var0 == null) {
         try {
            var0 = ClassLoader.getSystemClassLoader();
         } catch (SecurityException var22) {
         }
      }

      String var9 = var1.replace('.', '/').concat(".ser");
      InputStream var4;
      if (var0 == null) {
         var4 = ClassLoader.getSystemResourceAsStream(var9);
      } else {
         var4 = var0.getResourceAsStream(var9);
      }

      if (var4 != null) {
         try {
            if (var0 == null) {
               var5 = new ObjectInputStream(var4);
            } else {
               var5 = new ObjectInputStreamWithLoader(var4, var0);
            }

            var6 = ((ObjectInputStream)var5).readObject();
            var7 = true;
            ((ObjectInputStream)var5).close();
         } catch (IOException var20) {
            var4.close();
            var8 = var20;
         } catch (ClassNotFoundException var21) {
            var4.close();
            throw var21;
         }
      }

      if (var6 == null) {
         Class var10;
         try {
            var10 = ClassFinder.findClass(var1, var0);
         } catch (ClassNotFoundException var23) {
            if (var8 != null) {
               throw var8;
            }

            throw var23;
         }

         if (!Modifier.isPublic(var10.getModifiers())) {
            throw new ClassNotFoundException("" + var10 + " : no public access");
         }

         try {
            var6 = var10.newInstance();
         } catch (Exception var19) {
            throw new ClassNotFoundException("" + var10 + " : " + var19, var19);
         }
      }

      if (var6 != null) {
         BeansAppletStub var24 = null;
         if (var6 instanceof Applet) {
            Applet var11 = (Applet)var6;
            boolean var12 = var3 == null;
            if (var12) {
               String var13;
               if (var7) {
                  var13 = var1.replace('.', '/').concat(".ser");
               } else {
                  var13 = var1.replace('.', '/').concat(".class");
               }

               URL var14 = null;
               URL var15 = null;
               URL var16 = null;
               if (var0 == null) {
                  var14 = ClassLoader.getSystemResource(var13);
               } else {
                  var14 = var0.getResource(var13);
               }

               if (var14 != null) {
                  String var17 = var14.toExternalForm();
                  if (var17.endsWith(var13)) {
                     int var18 = var17.length() - var13.length();
                     var15 = new URL(var17.substring(0, var18));
                     var16 = var15;
                     var18 = var17.lastIndexOf(47);
                     if (var18 >= 0) {
                        var16 = new URL(var17.substring(0, var18 + 1));
                     }
                  }
               }

               BeansAppletContext var25 = new BeansAppletContext(var11);
               var24 = new BeansAppletStub(var11, var25, var15, var16);
               var11.setStub(var24);
            } else {
               var3.initialize(var11, var2);
            }

            if (var2 != null) {
               unsafeBeanContextAdd(var2, var6);
            }

            if (!var7) {
               var11.setSize(100, 100);
               var11.init();
            }

            if (var12) {
               ((BeansAppletStub)var24).active = true;
            } else {
               var3.activate(var11);
            }
         } else if (var2 != null) {
            unsafeBeanContextAdd(var2, var6);
         }
      }

      return var6;
   }

   private static void unsafeBeanContextAdd(BeanContext var0, Object var1) {
      var0.add(var1);
   }

   public static Object getInstanceOf(Object var0, Class<?> var1) {
      return var0;
   }

   public static boolean isInstanceOf(Object var0, Class<?> var1) {
      return Introspector.isSubclass(var0.getClass(), var1);
   }

   public static boolean isDesignTime() {
      return ThreadGroupContext.getContext().isDesignTime();
   }

   public static boolean isGuiAvailable() {
      return ThreadGroupContext.getContext().isGuiAvailable();
   }

   public static void setDesignTime(boolean var0) throws SecurityException {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkPropertiesAccess();
      }

      ThreadGroupContext.getContext().setDesignTime(var0);
   }

   public static void setGuiAvailable(boolean var0) throws SecurityException {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkPropertiesAccess();
      }

      ThreadGroupContext.getContext().setGuiAvailable(var0);
   }
}
