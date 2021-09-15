package java.net;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

class InMemoryCookieStore implements CookieStore {
   private List<HttpCookie> cookieJar = null;
   private Map<String, List<HttpCookie>> domainIndex = null;
   private Map<URI, List<HttpCookie>> uriIndex = null;
   private ReentrantLock lock = null;

   public InMemoryCookieStore() {
      this.cookieJar = new ArrayList();
      this.domainIndex = new HashMap();
      this.uriIndex = new HashMap();
      this.lock = new ReentrantLock(false);
   }

   public void add(URI var1, HttpCookie var2) {
      if (var2 == null) {
         throw new NullPointerException("cookie is null");
      } else {
         this.lock.lock();

         try {
            this.cookieJar.remove(var2);
            if (var2.getMaxAge() != 0L) {
               this.cookieJar.add(var2);
               if (var2.getDomain() != null) {
                  this.addIndex(this.domainIndex, var2.getDomain(), var2);
               }

               if (var1 != null) {
                  this.addIndex(this.uriIndex, this.getEffectiveURI(var1), var2);
               }
            }
         } finally {
            this.lock.unlock();
         }

      }
   }

   public List<HttpCookie> get(URI var1) {
      if (var1 == null) {
         throw new NullPointerException("uri is null");
      } else {
         ArrayList var2 = new ArrayList();
         boolean var3 = "https".equalsIgnoreCase(var1.getScheme());
         this.lock.lock();

         try {
            this.getInternal1(var2, this.domainIndex, var1.getHost(), var3);
            this.getInternal2(var2, this.uriIndex, this.getEffectiveURI(var1), var3);
         } finally {
            this.lock.unlock();
         }

         return var2;
      }
   }

   public List<HttpCookie> getCookies() {
      this.lock.lock();

      List var1;
      try {
         Iterator var2 = this.cookieJar.iterator();

         while(var2.hasNext()) {
            if (((HttpCookie)var2.next()).hasExpired()) {
               var2.remove();
            }
         }
      } finally {
         var1 = Collections.unmodifiableList(this.cookieJar);
         this.lock.unlock();
      }

      return var1;
   }

   public List<URI> getURIs() {
      ArrayList var1 = new ArrayList();
      this.lock.lock();

      try {
         Iterator var2 = this.uriIndex.keySet().iterator();

         while(var2.hasNext()) {
            URI var3 = (URI)var2.next();
            List var4 = (List)this.uriIndex.get(var3);
            if (var4 == null || var4.size() == 0) {
               var2.remove();
            }
         }
      } finally {
         var1.addAll(this.uriIndex.keySet());
         this.lock.unlock();
      }

      return var1;
   }

   public boolean remove(URI var1, HttpCookie var2) {
      if (var2 == null) {
         throw new NullPointerException("cookie is null");
      } else {
         boolean var3 = false;
         this.lock.lock();

         try {
            var3 = this.cookieJar.remove(var2);
         } finally {
            this.lock.unlock();
         }

         return var3;
      }
   }

   public boolean removeAll() {
      this.lock.lock();

      boolean var1;
      try {
         if (!this.cookieJar.isEmpty()) {
            this.cookieJar.clear();
            this.domainIndex.clear();
            this.uriIndex.clear();
            return true;
         }

         var1 = false;
      } finally {
         this.lock.unlock();
      }

      return var1;
   }

   private boolean netscapeDomainMatches(String var1, String var2) {
      if (var1 != null && var2 != null) {
         boolean var3 = ".local".equalsIgnoreCase(var1);
         int var4 = var1.indexOf(46);
         if (var4 == 0) {
            var4 = var1.indexOf(46, 1);
         }

         if (!var3 && (var4 == -1 || var4 == var1.length() - 1)) {
            return false;
         } else {
            int var5 = var2.indexOf(46);
            if (var5 == -1 && var3) {
               return true;
            } else {
               int var6 = var1.length();
               int var7 = var2.length() - var6;
               if (var7 == 0) {
                  return var2.equalsIgnoreCase(var1);
               } else if (var7 > 0) {
                  var2.substring(0, var7);
                  String var9 = var2.substring(var7);
                  return var9.equalsIgnoreCase(var1);
               } else if (var7 != -1) {
                  return false;
               } else {
                  return var1.charAt(0) == '.' && var2.equalsIgnoreCase(var1.substring(1));
               }
            }
         }
      } else {
         return false;
      }
   }

   private void getInternal1(List<HttpCookie> var1, Map<String, List<HttpCookie>> var2, String var3, boolean var4) {
      ArrayList var5 = new ArrayList();
      Iterator var6 = var2.entrySet().iterator();

      label66:
      while(var6.hasNext()) {
         Map.Entry var7 = (Map.Entry)var6.next();
         String var8 = (String)var7.getKey();
         List var9 = (List)var7.getValue();
         Iterator var10 = var9.iterator();

         while(true) {
            HttpCookie var11;
            label55:
            do {
               while(true) {
                  while(true) {
                     do {
                        if (!var10.hasNext()) {
                           var10 = var5.iterator();

                           while(var10.hasNext()) {
                              var11 = (HttpCookie)var10.next();
                              var9.remove(var11);
                              this.cookieJar.remove(var11);
                           }

                           var5.clear();
                           continue label66;
                        }

                        var11 = (HttpCookie)var10.next();
                     } while((var11.getVersion() != 0 || !this.netscapeDomainMatches(var8, var3)) && (var11.getVersion() != 1 || !HttpCookie.domainMatches(var8, var3)));

                     if (this.cookieJar.indexOf(var11) != -1) {
                        if (!var11.hasExpired()) {
                           continue label55;
                        }

                        var5.add(var11);
                     } else {
                        var5.add(var11);
                     }
                  }
               }
            } while(!var4 && var11.getSecure());

            if (!var1.contains(var11)) {
               var1.add(var11);
            }
         }
      }

   }

   private <T> void getInternal2(List<HttpCookie> var1, Map<T, List<HttpCookie>> var2, Comparable<T> var3, boolean var4) {
      Iterator var5 = var2.keySet().iterator();

      label53:
      while(true) {
         List var7;
         do {
            Object var6;
            do {
               if (!var5.hasNext()) {
                  return;
               }

               var6 = var5.next();
            } while(var3.compareTo(var6) != 0);

            var7 = (List)var2.get(var6);
         } while(var7 == null);

         Iterator var8 = var7.iterator();

         while(true) {
            HttpCookie var9;
            label49:
            do {
               while(true) {
                  while(true) {
                     if (!var8.hasNext()) {
                        continue label53;
                     }

                     var9 = (HttpCookie)var8.next();
                     if (this.cookieJar.indexOf(var9) != -1) {
                        if (!var9.hasExpired()) {
                           continue label49;
                        }

                        var8.remove();
                        this.cookieJar.remove(var9);
                     } else {
                        var8.remove();
                     }
                  }
               }
            } while(!var4 && var9.getSecure());

            if (!var1.contains(var9)) {
               var1.add(var9);
            }
         }
      }
   }

   private <T> void addIndex(Map<T, List<HttpCookie>> var1, T var2, HttpCookie var3) {
      if (var2 != null) {
         List var4 = (List)var1.get(var2);
         if (var4 != null) {
            var4.remove(var3);
            var4.add(var3);
         } else {
            ArrayList var5 = new ArrayList();
            var5.add(var3);
            var1.put(var2, var5);
         }
      }

   }

   private URI getEffectiveURI(URI var1) {
      URI var2 = null;

      try {
         var2 = new URI("http", var1.getHost(), (String)null, (String)null, (String)null);
      } catch (URISyntaxException var4) {
         var2 = var1;
      }

      return var2;
   }
}
