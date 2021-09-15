package java.rmi;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public final class Naming {
   private Naming() {
   }

   public static Remote lookup(String var0) throws NotBoundException, MalformedURLException, RemoteException {
      Naming.ParsedNamingURL var1 = parseURL(var0);
      Registry var2 = getRegistry(var1);
      return (Remote)(var1.name == null ? var2 : var2.lookup(var1.name));
   }

   public static void bind(String var0, Remote var1) throws AlreadyBoundException, MalformedURLException, RemoteException {
      Naming.ParsedNamingURL var2 = parseURL(var0);
      Registry var3 = getRegistry(var2);
      if (var1 == null) {
         throw new NullPointerException("cannot bind to null");
      } else {
         var3.bind(var2.name, var1);
      }
   }

   public static void unbind(String var0) throws RemoteException, NotBoundException, MalformedURLException {
      Naming.ParsedNamingURL var1 = parseURL(var0);
      Registry var2 = getRegistry(var1);
      var2.unbind(var1.name);
   }

   public static void rebind(String var0, Remote var1) throws RemoteException, MalformedURLException {
      Naming.ParsedNamingURL var2 = parseURL(var0);
      Registry var3 = getRegistry(var2);
      if (var1 == null) {
         throw new NullPointerException("cannot bind to null");
      } else {
         var3.rebind(var2.name, var1);
      }
   }

   public static String[] list(String var0) throws RemoteException, MalformedURLException {
      Naming.ParsedNamingURL var1 = parseURL(var0);
      Registry var2 = getRegistry(var1);
      String var3 = "";
      if (var1.port > 0 || !var1.host.equals("")) {
         var3 = var3 + "//" + var1.host;
      }

      if (var1.port > 0) {
         var3 = var3 + ":" + var1.port;
      }

      var3 = var3 + "/";
      String[] var4 = var2.list();

      for(int var5 = 0; var5 < var4.length; ++var5) {
         var4[var5] = var3 + var4[var5];
      }

      return var4;
   }

   private static Registry getRegistry(Naming.ParsedNamingURL var0) throws RemoteException {
      return LocateRegistry.getRegistry(var0.host, var0.port);
   }

   private static Naming.ParsedNamingURL parseURL(String var0) throws MalformedURLException {
      try {
         return intParseURL(var0);
      } catch (URISyntaxException var10) {
         MalformedURLException var2 = new MalformedURLException("invalid URL String: " + var0);
         var2.initCause(var10);
         int var3 = var0.indexOf(58);
         int var4 = var0.indexOf("//:");
         if (var4 < 0) {
            throw var2;
         } else if (var4 != 0 && (var3 <= 0 || var4 != var3 + 1)) {
            throw var2;
         } else {
            int var5 = var4 + 2;
            String var6 = var0.substring(0, var5) + "localhost" + var0.substring(var5);

            try {
               return intParseURL(var6);
            } catch (URISyntaxException var8) {
               throw var2;
            } catch (MalformedURLException var9) {
               throw var9;
            }
         }
      }
   }

   private static Naming.ParsedNamingURL intParseURL(String var0) throws MalformedURLException, URISyntaxException {
      URI var1 = new URI(var0);
      if (var1.isOpaque()) {
         throw new MalformedURLException("not a hierarchical URL: " + var0);
      } else if (var1.getFragment() != null) {
         throw new MalformedURLException("invalid character, '#', in URL name: " + var0);
      } else if (var1.getQuery() != null) {
         throw new MalformedURLException("invalid character, '?', in URL name: " + var0);
      } else if (var1.getUserInfo() != null) {
         throw new MalformedURLException("invalid character, '@', in URL host: " + var0);
      } else {
         String var2 = var1.getScheme();
         if (var2 != null && !var2.equals("rmi")) {
            throw new MalformedURLException("invalid URL scheme: " + var0);
         } else {
            String var3 = var1.getPath();
            if (var3 != null) {
               if (var3.startsWith("/")) {
                  var3 = var3.substring(1);
               }

               if (var3.length() == 0) {
                  var3 = null;
               }
            }

            String var4 = var1.getHost();
            if (var4 == null) {
               var4 = "";

               try {
                  var1.parseServerAuthority();
               } catch (URISyntaxException var9) {
                  String var6 = var1.getAuthority();
                  if (var6 == null || !var6.startsWith(":")) {
                     throw new MalformedURLException("invalid authority: " + var0);
                  }

                  var6 = "localhost" + var6;

                  try {
                     var1 = new URI((String)null, var6, (String)null, (String)null, (String)null);
                     var1.parseServerAuthority();
                  } catch (URISyntaxException var8) {
                     throw new MalformedURLException("invalid authority: " + var0);
                  }
               }
            }

            int var5 = var1.getPort();
            if (var5 == -1) {
               var5 = 1099;
            }

            return new Naming.ParsedNamingURL(var4, var5, var3);
         }
      }
   }

   private static class ParsedNamingURL {
      String host;
      int port;
      String name;

      ParsedNamingURL(String var1, int var2, String var3) {
         this.host = var1;
         this.port = var2;
         this.name = var3;
      }
   }
}
