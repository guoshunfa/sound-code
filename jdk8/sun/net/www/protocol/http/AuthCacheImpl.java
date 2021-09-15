package sun.net.www.protocol.http;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;

public class AuthCacheImpl implements AuthCache {
   HashMap<String, LinkedList<AuthCacheValue>> hashtable = new HashMap();

   public void setMap(HashMap<String, LinkedList<AuthCacheValue>> var1) {
      this.hashtable = var1;
   }

   public synchronized void put(String var1, AuthCacheValue var2) {
      LinkedList var3 = (LinkedList)this.hashtable.get(var1);
      String var4 = var2.getPath();
      if (var3 == null) {
         var3 = new LinkedList();
         this.hashtable.put(var1, var3);
      }

      ListIterator var5 = var3.listIterator();

      while(true) {
         AuthenticationInfo var6;
         do {
            if (!var5.hasNext()) {
               var5.add(var2);
               return;
            }

            var6 = (AuthenticationInfo)var5.next();
         } while(var6.path != null && !var6.path.startsWith(var4));

         var5.remove();
      }
   }

   public synchronized AuthCacheValue get(String var1, String var2) {
      Object var3 = null;
      LinkedList var4 = (LinkedList)this.hashtable.get(var1);
      if (var4 != null && var4.size() != 0) {
         if (var2 == null) {
            return (AuthenticationInfo)var4.get(0);
         } else {
            ListIterator var5 = var4.listIterator();

            AuthenticationInfo var6;
            do {
               if (!var5.hasNext()) {
                  return null;
               }

               var6 = (AuthenticationInfo)var5.next();
            } while(!var2.startsWith(var6.path));

            return var6;
         }
      } else {
         return null;
      }
   }

   public synchronized void remove(String var1, AuthCacheValue var2) {
      LinkedList var3 = (LinkedList)this.hashtable.get(var1);
      if (var3 != null) {
         if (var2 == null) {
            var3.clear();
         } else {
            ListIterator var4 = var3.listIterator();

            while(var4.hasNext()) {
               AuthenticationInfo var5 = (AuthenticationInfo)var4.next();
               if (var2.equals(var5)) {
                  var4.remove();
               }
            }

         }
      }
   }
}
