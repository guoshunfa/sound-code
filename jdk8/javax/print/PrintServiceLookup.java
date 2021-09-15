package javax.print;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import javax.print.attribute.AttributeSet;
import sun.awt.AppContext;

public abstract class PrintServiceLookup {
   private static PrintServiceLookup.Services getServicesForContext() {
      PrintServiceLookup.Services var0 = (PrintServiceLookup.Services)AppContext.getAppContext().get(PrintServiceLookup.Services.class);
      if (var0 == null) {
         var0 = new PrintServiceLookup.Services();
         AppContext.getAppContext().put(PrintServiceLookup.Services.class, var0);
      }

      return var0;
   }

   private static ArrayList getListOfLookupServices() {
      return getServicesForContext().listOfLookupServices;
   }

   private static ArrayList initListOfLookupServices() {
      ArrayList var0 = new ArrayList();
      getServicesForContext().listOfLookupServices = var0;
      return var0;
   }

   private static ArrayList getRegisteredServices() {
      return getServicesForContext().registeredServices;
   }

   private static ArrayList initRegisteredServices() {
      ArrayList var0 = new ArrayList();
      getServicesForContext().registeredServices = var0;
      return var0;
   }

   public static final PrintService[] lookupPrintServices(DocFlavor var0, AttributeSet var1) {
      ArrayList var2 = getServices(var0, var1);
      return (PrintService[])((PrintService[])var2.toArray(new PrintService[var2.size()]));
   }

   public static final MultiDocPrintService[] lookupMultiDocPrintServices(DocFlavor[] var0, AttributeSet var1) {
      ArrayList var2 = getMultiDocServices(var0, var1);
      return (MultiDocPrintService[])((MultiDocPrintService[])var2.toArray(new MultiDocPrintService[var2.size()]));
   }

   public static final PrintService lookupDefaultPrintService() {
      Iterator var0 = getAllLookupServices().iterator();

      while(var0.hasNext()) {
         try {
            PrintServiceLookup var1 = (PrintServiceLookup)var0.next();
            PrintService var2 = var1.getDefaultPrintService();
            if (var2 != null) {
               return var2;
            }
         } catch (Exception var3) {
         }
      }

      return null;
   }

   public static boolean registerServiceProvider(PrintServiceLookup var0) {
      Class var1 = PrintServiceLookup.class;
      synchronized(PrintServiceLookup.class) {
         Iterator var2 = getAllLookupServices().iterator();

         while(var2.hasNext()) {
            try {
               Object var3 = var2.next();
               if (var3.getClass() == var0.getClass()) {
                  boolean var10000 = false;
                  return var10000;
               }
            } catch (Exception var5) {
            }
         }

         getListOfLookupServices().add(var0);
         return true;
      }
   }

   public static boolean registerService(PrintService var0) {
      Class var1 = PrintServiceLookup.class;
      synchronized(PrintServiceLookup.class) {
         if (var0 instanceof StreamPrintService) {
            return false;
         } else {
            ArrayList var2 = getRegisteredServices();
            if (var2 == null) {
               var2 = initRegisteredServices();
            } else if (var2.contains(var0)) {
               return false;
            }

            var2.add(var0);
            return true;
         }
      }
   }

   public abstract PrintService[] getPrintServices(DocFlavor var1, AttributeSet var2);

   public abstract PrintService[] getPrintServices();

   public abstract MultiDocPrintService[] getMultiDocPrintServices(DocFlavor[] var1, AttributeSet var2);

   public abstract PrintService getDefaultPrintService();

   private static ArrayList getAllLookupServices() {
      Class var0 = PrintServiceLookup.class;
      synchronized(PrintServiceLookup.class) {
         ArrayList var1 = getListOfLookupServices();
         if (var1 != null) {
            return var1;
         } else {
            var1 = initListOfLookupServices();

            try {
               AccessController.doPrivileged(new PrivilegedExceptionAction() {
                  public Object run() {
                     Iterator var1 = ServiceLoader.load(PrintServiceLookup.class).iterator();
                     ArrayList var2 = PrintServiceLookup.getListOfLookupServices();

                     while(var1.hasNext()) {
                        try {
                           var2.add(var1.next());
                        } catch (ServiceConfigurationError var4) {
                           if (System.getSecurityManager() == null) {
                              throw var4;
                           }

                           var4.printStackTrace();
                        }
                     }

                     return null;
                  }
               });
            } catch (PrivilegedActionException var4) {
            }

            return var1;
         }
      }
   }

   private static ArrayList getServices(DocFlavor var0, AttributeSet var1) {
      ArrayList var2 = new ArrayList();
      Iterator var3 = getAllLookupServices().iterator();

      PrintService[] var5;
      int var6;
      while(var3.hasNext()) {
         try {
            PrintServiceLookup var4 = (PrintServiceLookup)var3.next();
            var5 = null;
            if (var0 == null && var1 == null) {
               try {
                  var5 = var4.getPrintServices();
               } catch (Throwable var8) {
               }
            } else {
               var5 = var4.getPrintServices(var0, var1);
            }

            if (var5 != null) {
               for(var6 = 0; var6 < var5.length; ++var6) {
                  var2.add(var5[var6]);
               }
            }
         } catch (Exception var9) {
         }
      }

      ArrayList var10 = null;

      try {
         SecurityManager var11 = System.getSecurityManager();
         if (var11 != null) {
            var11.checkPrintJobAccess();
         }

         var10 = getRegisteredServices();
      } catch (SecurityException var7) {
      }

      if (var10 != null) {
         var5 = (PrintService[])((PrintService[])var10.toArray(new PrintService[var10.size()]));

         for(var6 = 0; var6 < var5.length; ++var6) {
            if (!var2.contains(var5[var6])) {
               if (var0 == null && var1 == null) {
                  var2.add(var5[var6]);
               } else if ((var0 != null && var5[var6].isDocFlavorSupported(var0) || var0 == null) && null == var5[var6].getUnsupportedAttributes(var0, var1)) {
                  var2.add(var5[var6]);
               }
            }
         }
      }

      return var2;
   }

   private static ArrayList getMultiDocServices(DocFlavor[] var0, AttributeSet var1) {
      ArrayList var2 = new ArrayList();
      Iterator var3 = getAllLookupServices().iterator();

      int var6;
      while(var3.hasNext()) {
         try {
            PrintServiceLookup var4 = (PrintServiceLookup)var3.next();
            MultiDocPrintService[] var5 = var4.getMultiDocPrintServices(var0, var1);
            if (var5 != null) {
               for(var6 = 0; var6 < var5.length; ++var6) {
                  var2.add(var5[var6]);
               }
            }
         } catch (Exception var10) {
         }
      }

      ArrayList var11 = null;

      try {
         SecurityManager var12 = System.getSecurityManager();
         if (var12 != null) {
            var12.checkPrintJobAccess();
         }

         var11 = getRegisteredServices();
      } catch (Exception var9) {
      }

      if (var11 != null) {
         PrintService[] var13 = (PrintService[])((PrintService[])var11.toArray(new PrintService[var11.size()]));

         for(var6 = 0; var6 < var13.length; ++var6) {
            if (var13[var6] instanceof MultiDocPrintService && !var2.contains(var13[var6])) {
               if (var0 != null && var0.length != 0) {
                  boolean var7 = true;

                  for(int var8 = 0; var8 < var0.length; ++var8) {
                     if (!var13[var6].isDocFlavorSupported(var0[var8])) {
                        var7 = false;
                        break;
                     }

                     if (var13[var6].getUnsupportedAttributes(var0[var8], var1) != null) {
                        var7 = false;
                        break;
                     }
                  }

                  if (var7) {
                     var2.add(var13[var6]);
                  }
               } else {
                  var2.add(var13[var6]);
               }
            }
         }
      }

      return var2;
   }

   static class Services {
      private ArrayList listOfLookupServices = null;
      private ArrayList registeredServices = null;
   }
}
