package java.net;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import sun.util.logging.PlatformLogger;

public class CookieManager extends CookieHandler {
   private CookiePolicy policyCallback;
   private CookieStore cookieJar;

   public CookieManager() {
      this((CookieStore)null, (CookiePolicy)null);
   }

   public CookieManager(CookieStore var1, CookiePolicy var2) {
      this.cookieJar = null;
      this.policyCallback = var2 == null ? CookiePolicy.ACCEPT_ORIGINAL_SERVER : var2;
      if (var1 == null) {
         this.cookieJar = new InMemoryCookieStore();
      } else {
         this.cookieJar = var1;
      }

   }

   public void setCookiePolicy(CookiePolicy var1) {
      if (var1 != null) {
         this.policyCallback = var1;
      }

   }

   public CookieStore getCookieStore() {
      return this.cookieJar;
   }

   public Map<String, List<String>> get(URI var1, Map<String, List<String>> var2) throws IOException {
      if (var1 != null && var2 != null) {
         HashMap var3 = new HashMap();
         if (this.cookieJar == null) {
            return Collections.unmodifiableMap(var3);
         } else {
            boolean var4 = "https".equalsIgnoreCase(var1.getScheme());
            ArrayList var5 = new ArrayList();
            String var6 = var1.getPath();
            if (var6 == null || var6.isEmpty()) {
               var6 = "/";
            }

            Iterator var7 = this.cookieJar.get(var1).iterator();

            while(true) {
               while(true) {
                  HttpCookie var8;
                  String var9;
                  do {
                     do {
                        do {
                           if (!var7.hasNext()) {
                              List var11 = this.sortByPath(var5);
                              var3.put("Cookie", var11);
                              return Collections.unmodifiableMap(var3);
                           }

                           var8 = (HttpCookie)var7.next();
                        } while(!this.pathMatches(var6, var8.getPath()));
                     } while(!var4 && var8.getSecure());

                     if (!var8.isHttpOnly()) {
                        break;
                     }

                     var9 = var1.getScheme();
                  } while(!"http".equalsIgnoreCase(var9) && !"https".equalsIgnoreCase(var9));

                  var9 = var8.getPortlist();
                  if (var9 != null && !var9.isEmpty()) {
                     int var10 = var1.getPort();
                     if (var10 == -1) {
                        var10 = "https".equals(var1.getScheme()) ? 443 : 80;
                     }

                     if (isInPortList(var9, var10)) {
                        var5.add(var8);
                     }
                  } else {
                     var5.add(var8);
                  }
               }
            }
         }
      } else {
         throw new IllegalArgumentException("Argument is null");
      }
   }

   public void put(URI var1, Map<String, List<String>> var2) throws IOException {
      if (var1 != null && var2 != null) {
         if (this.cookieJar != null) {
            PlatformLogger var3 = PlatformLogger.getLogger("java.net.CookieManager");
            Iterator var4 = var2.keySet().iterator();

            while(true) {
               String var5;
               do {
                  do {
                     if (!var4.hasNext()) {
                        return;
                     }

                     var5 = (String)var4.next();
                  } while(var5 == null);
               } while(!var5.equalsIgnoreCase("Set-Cookie2") && !var5.equalsIgnoreCase("Set-Cookie"));

               Iterator var6 = ((List)var2.get(var5)).iterator();

               while(var6.hasNext()) {
                  String var7 = (String)var6.next();

                  try {
                     List var8;
                     try {
                        var8 = HttpCookie.parse(var7);
                     } catch (IllegalArgumentException var13) {
                        var8 = Collections.emptyList();
                        if (var3.isLoggable(PlatformLogger.Level.SEVERE)) {
                           var3.severe("Invalid cookie for " + var1 + ": " + var7);
                        }
                     }

                     Iterator var9 = var8.iterator();

                     while(var9.hasNext()) {
                        HttpCookie var10 = (HttpCookie)var9.next();
                        String var11;
                        int var12;
                        if (var10.getPath() == null) {
                           var11 = var1.getPath();
                           if (!var11.endsWith("/")) {
                              var12 = var11.lastIndexOf("/");
                              if (var12 > 0) {
                                 var11 = var11.substring(0, var12 + 1);
                              } else {
                                 var11 = "/";
                              }
                           }

                           var10.setPath(var11);
                        }

                        if (var10.getDomain() == null) {
                           var11 = var1.getHost();
                           if (var11 != null && !var11.contains(".")) {
                              var11 = var11 + ".local";
                           }

                           var10.setDomain(var11);
                        }

                        var11 = var10.getPortlist();
                        if (var11 != null) {
                           var12 = var1.getPort();
                           if (var12 == -1) {
                              var12 = "https".equals(var1.getScheme()) ? 443 : 80;
                           }

                           if (var11.isEmpty()) {
                              var10.setPortlist("" + var12);
                              if (this.shouldAcceptInternal(var1, var10)) {
                                 this.cookieJar.add(var1, var10);
                              }
                           } else if (isInPortList(var11, var12) && this.shouldAcceptInternal(var1, var10)) {
                              this.cookieJar.add(var1, var10);
                           }
                        } else if (this.shouldAcceptInternal(var1, var10)) {
                           this.cookieJar.add(var1, var10);
                        }
                     }
                  } catch (IllegalArgumentException var14) {
                  }
               }
            }
         }
      } else {
         throw new IllegalArgumentException("Argument is null");
      }
   }

   private boolean shouldAcceptInternal(URI var1, HttpCookie var2) {
      try {
         return this.policyCallback.shouldAccept(var1, var2);
      } catch (Exception var4) {
         return false;
      }
   }

   private static boolean isInPortList(String var0, int var1) {
      int var2 = var0.indexOf(",");

      int var7;
      for(boolean var3 = true; var2 > 0; var2 = var0.indexOf(",")) {
         try {
            var7 = Integer.parseInt(var0.substring(0, var2));
            if (var7 == var1) {
               return true;
            }
         } catch (NumberFormatException var6) {
         }

         var0 = var0.substring(var2 + 1);
      }

      if (!var0.isEmpty()) {
         try {
            var7 = Integer.parseInt(var0);
            if (var7 == var1) {
               return true;
            }
         } catch (NumberFormatException var5) {
         }
      }

      return false;
   }

   private boolean pathMatches(String var1, String var2) {
      if (var1 == var2) {
         return true;
      } else if (var1 != null && var2 != null) {
         return var1.startsWith(var2);
      } else {
         return false;
      }
   }

   private List<String> sortByPath(List<HttpCookie> var1) {
      Collections.sort(var1, new CookieManager.CookiePathComparator());
      ArrayList var2 = new ArrayList();

      HttpCookie var4;
      for(Iterator var3 = var1.iterator(); var3.hasNext(); var2.add(var4.toString())) {
         var4 = (HttpCookie)var3.next();
         if (var1.indexOf(var4) == 0 && var4.getVersion() > 0) {
            var2.add("$Version=\"1\"");
         }
      }

      return var2;
   }

   static class CookiePathComparator implements Comparator<HttpCookie> {
      public int compare(HttpCookie var1, HttpCookie var2) {
         if (var1 == var2) {
            return 0;
         } else if (var1 == null) {
            return -1;
         } else if (var2 == null) {
            return 1;
         } else if (!var1.getName().equals(var2.getName())) {
            return 0;
         } else if (var1.getPath().startsWith(var2.getPath())) {
            return -1;
         } else {
            return var2.getPath().startsWith(var1.getPath()) ? 1 : 0;
         }
      }
   }
}
