package sun.net.httpserver;

import java.util.Iterator;
import java.util.LinkedList;

class ContextList {
   static final int MAX_CONTEXTS = 50;
   LinkedList<HttpContextImpl> list = new LinkedList();

   public synchronized void add(HttpContextImpl var1) {
      assert var1.getPath() != null;

      this.list.add(var1);
   }

   public synchronized int size() {
      return this.list.size();
   }

   synchronized HttpContextImpl findContext(String var1, String var2) {
      return this.findContext(var1, var2, false);
   }

   synchronized HttpContextImpl findContext(String var1, String var2, boolean var3) {
      var1 = var1.toLowerCase();
      String var4 = "";
      HttpContextImpl var5 = null;
      Iterator var6 = this.list.iterator();

      while(true) {
         HttpContextImpl var7;
         String var8;
         do {
            do {
               do {
                  if (!var6.hasNext()) {
                     return var5;
                  }

                  var7 = (HttpContextImpl)var6.next();
               } while(!var7.getProtocol().equals(var1));

               var8 = var7.getPath();
            } while(var3 && !var8.equals(var2));
         } while(!var3 && !var2.startsWith(var8));

         if (var8.length() > var4.length()) {
            var4 = var8;
            var5 = var7;
         }
      }
   }

   public synchronized void remove(String var1, String var2) throws IllegalArgumentException {
      HttpContextImpl var3 = this.findContext(var1, var2, true);
      if (var3 == null) {
         throw new IllegalArgumentException("cannot remove element from list");
      } else {
         this.list.remove(var3);
      }
   }

   public synchronized void remove(HttpContextImpl var1) throws IllegalArgumentException {
      Iterator var2 = this.list.iterator();

      HttpContextImpl var3;
      do {
         if (!var2.hasNext()) {
            throw new IllegalArgumentException("no such context in list");
         }

         var3 = (HttpContextImpl)var2.next();
      } while(!var3.equals(var1));

      this.list.remove(var3);
   }
}
