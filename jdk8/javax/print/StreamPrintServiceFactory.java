package javax.print;

import java.io.OutputStream;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import sun.awt.AppContext;

public abstract class StreamPrintServiceFactory {
   private static StreamPrintServiceFactory.Services getServices() {
      StreamPrintServiceFactory.Services var0 = (StreamPrintServiceFactory.Services)AppContext.getAppContext().get(StreamPrintServiceFactory.Services.class);
      if (var0 == null) {
         var0 = new StreamPrintServiceFactory.Services();
         AppContext.getAppContext().put(StreamPrintServiceFactory.Services.class, var0);
      }

      return var0;
   }

   private static ArrayList getListOfFactories() {
      return getServices().listOfFactories;
   }

   private static ArrayList initListOfFactories() {
      ArrayList var0 = new ArrayList();
      getServices().listOfFactories = var0;
      return var0;
   }

   public static StreamPrintServiceFactory[] lookupStreamPrintServiceFactories(DocFlavor var0, String var1) {
      ArrayList var2 = getFactories(var0, var1);
      return (StreamPrintServiceFactory[])((StreamPrintServiceFactory[])var2.toArray(new StreamPrintServiceFactory[var2.size()]));
   }

   public abstract String getOutputFormat();

   public abstract DocFlavor[] getSupportedDocFlavors();

   public abstract StreamPrintService getPrintService(OutputStream var1);

   private static ArrayList getAllFactories() {
      Class var0 = StreamPrintServiceFactory.class;
      synchronized(StreamPrintServiceFactory.class) {
         ArrayList var1 = getListOfFactories();
         if (var1 != null) {
            return var1;
         } else {
            var1 = initListOfFactories();

            try {
               AccessController.doPrivileged(new PrivilegedExceptionAction() {
                  public Object run() {
                     Iterator var1 = ServiceLoader.load(StreamPrintServiceFactory.class).iterator();
                     ArrayList var2 = StreamPrintServiceFactory.getListOfFactories();

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

   private static boolean isMember(DocFlavor var0, DocFlavor[] var1) {
      for(int var2 = 0; var2 < var1.length; ++var2) {
         if (var0.equals(var1[var2])) {
            return true;
         }
      }

      return false;
   }

   private static ArrayList getFactories(DocFlavor var0, String var1) {
      if (var0 == null && var1 == null) {
         return getAllFactories();
      } else {
         ArrayList var2 = new ArrayList();
         Iterator var3 = getAllFactories().iterator();

         while(true) {
            StreamPrintServiceFactory var4;
            do {
               do {
                  if (!var3.hasNext()) {
                     return var2;
                  }

                  var4 = (StreamPrintServiceFactory)var3.next();
               } while(var1 != null && !var1.equalsIgnoreCase(var4.getOutputFormat()));
            } while(var0 != null && !isMember(var0, var4.getSupportedDocFlavors()));

            var2.add(var4);
         }
      }
   }

   static class Services {
      private ArrayList listOfFactories = null;
   }
}
