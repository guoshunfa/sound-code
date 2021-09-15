package java.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final class URLPermission extends Permission {
   private static final long serialVersionUID = -2702463814894478682L;
   private transient String scheme;
   private transient String ssp;
   private transient String path;
   private transient List<String> methods;
   private transient List<String> requestHeaders;
   private transient URLPermission.Authority authority;
   private String actions;

   public URLPermission(String var1, String var2) {
      super(var1);
      this.init(var2);
   }

   private void init(String var1) {
      this.parseURI(this.getName());
      int var2 = var1.indexOf(58);
      if (var1.lastIndexOf(58) != var2) {
         throw new IllegalArgumentException("Invalid actions string: \"" + var1 + "\"");
      } else {
         String var3;
         String var4;
         if (var2 == -1) {
            var3 = var1;
            var4 = "";
         } else {
            var3 = var1.substring(0, var2);
            var4 = var1.substring(var2 + 1);
         }

         List var5 = this.normalizeMethods(var3);
         Collections.sort(var5);
         this.methods = Collections.unmodifiableList(var5);
         var5 = this.normalizeHeaders(var4);
         Collections.sort(var5);
         this.requestHeaders = Collections.unmodifiableList(var5);
         this.actions = this.actions();
      }
   }

   public URLPermission(String var1) {
      this(var1, "*:*");
   }

   public String getActions() {
      return this.actions;
   }

   public boolean implies(Permission var1) {
      if (!(var1 instanceof URLPermission)) {
         return false;
      } else {
         URLPermission var2 = (URLPermission)var1;
         if (!((String)this.methods.get(0)).equals("*") && Collections.indexOfSubList(this.methods, var2.methods) == -1) {
            return false;
         } else if (this.requestHeaders.isEmpty() && !var2.requestHeaders.isEmpty()) {
            return false;
         } else if (!this.requestHeaders.isEmpty() && !((String)this.requestHeaders.get(0)).equals("*") && Collections.indexOfSubList(this.requestHeaders, var2.requestHeaders) == -1) {
            return false;
         } else if (!this.scheme.equals(var2.scheme)) {
            return false;
         } else if (this.ssp.equals("*")) {
            return true;
         } else if (!this.authority.implies(var2.authority)) {
            return false;
         } else if (this.path == null) {
            return var2.path == null;
         } else if (var2.path == null) {
            return false;
         } else {
            String var3;
            if (this.path.endsWith("/-")) {
               var3 = this.path.substring(0, this.path.length() - 1);
               return var2.path.startsWith(var3);
            } else if (this.path.endsWith("/*")) {
               var3 = this.path.substring(0, this.path.length() - 1);
               if (!var2.path.startsWith(var3)) {
                  return false;
               } else {
                  String var4 = var2.path.substring(var3.length());
                  if (var4.indexOf(47) != -1) {
                     return false;
                  } else {
                     return !var4.equals("-");
                  }
               }
            } else {
               return this.path.equals(var2.path);
            }
         }
      }
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof URLPermission)) {
         return false;
      } else {
         URLPermission var2 = (URLPermission)var1;
         if (!this.scheme.equals(var2.scheme)) {
            return false;
         } else if (!this.getActions().equals(var2.getActions())) {
            return false;
         } else if (!this.authority.equals(var2.authority)) {
            return false;
         } else if (this.path != null) {
            return this.path.equals(var2.path);
         } else {
            return var2.path == null;
         }
      }
   }

   public int hashCode() {
      return this.getActions().hashCode() + this.scheme.hashCode() + this.authority.hashCode() + (this.path == null ? 0 : this.path.hashCode());
   }

   private List<String> normalizeMethods(String var1) {
      ArrayList var2 = new ArrayList();
      StringBuilder var3 = new StringBuilder();

      for(int var4 = 0; var4 < var1.length(); ++var4) {
         char var5 = var1.charAt(var4);
         if (var5 == ',') {
            String var6 = var3.toString();
            if (var6.length() > 0) {
               var2.add(var6);
            }

            var3 = new StringBuilder();
         } else {
            if (var5 == ' ' || var5 == '\t') {
               throw new IllegalArgumentException("White space not allowed in methods: \"" + var1 + "\"");
            }

            if (var5 >= 'a' && var5 <= 'z') {
               var5 = (char)(var5 - 32);
            }

            var3.append(var5);
         }
      }

      String var7 = var3.toString();
      if (var7.length() > 0) {
         var2.add(var7);
      }

      return var2;
   }

   private List<String> normalizeHeaders(String var1) {
      ArrayList var2 = new ArrayList();
      StringBuilder var3 = new StringBuilder();
      boolean var4 = true;

      for(int var5 = 0; var5 < var1.length(); ++var5) {
         char var6 = var1.charAt(var5);
         if (var6 >= 'a' && var6 <= 'z') {
            if (var4) {
               var6 = (char)(var6 - 32);
               var4 = false;
            }

            var3.append(var6);
         } else {
            if (var6 == ' ' || var6 == '\t') {
               throw new IllegalArgumentException("White space not allowed in headers: \"" + var1 + "\"");
            }

            if (var6 == '-') {
               var4 = true;
               var3.append(var6);
            } else if (var6 == ',') {
               String var7 = var3.toString();
               if (var7.length() > 0) {
                  var2.add(var7);
               }

               var3 = new StringBuilder();
               var4 = true;
            } else {
               var4 = false;
               var3.append(var6);
            }
         }
      }

      String var8 = var3.toString();
      if (var8.length() > 0) {
         var2.add(var8);
      }

      return var2;
   }

   private void parseURI(String var1) {
      int var2 = var1.length();
      int var3 = var1.indexOf(58);
      if (var3 != -1 && var3 + 1 != var2) {
         this.scheme = var1.substring(0, var3).toLowerCase();
         this.ssp = var1.substring(var3 + 1);
         if (!this.ssp.startsWith("//")) {
            if (!this.ssp.equals("*")) {
               throw new IllegalArgumentException("Invalid URL string: \"" + var1 + "\"");
            } else {
               this.authority = new URLPermission.Authority(this.scheme, "*");
            }
         } else {
            String var4 = this.ssp.substring(2);
            var3 = var4.indexOf(47);
            String var5;
            if (var3 == -1) {
               this.path = "";
               var5 = var4;
            } else {
               var5 = var4.substring(0, var3);
               this.path = var4.substring(var3);
            }

            this.authority = new URLPermission.Authority(this.scheme, var5.toLowerCase());
         }
      } else {
         throw new IllegalArgumentException("Invalid URL string: \"" + var1 + "\"");
      }
   }

   private String actions() {
      StringBuilder var1 = new StringBuilder();
      Iterator var2 = this.methods.iterator();

      String var3;
      while(var2.hasNext()) {
         var3 = (String)var2.next();
         var1.append(var3);
      }

      var1.append(":");
      var2 = this.requestHeaders.iterator();

      while(var2.hasNext()) {
         var3 = (String)var2.next();
         var1.append(var3);
      }

      return var1.toString();
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      ObjectInputStream.GetField var2 = var1.readFields();
      String var3 = (String)var2.get("actions", (Object)null);
      this.init(var3);
   }

   static class Authority {
      HostPortrange p;

      Authority(String var1, String var2) {
         int var3 = var2.indexOf(64);
         if (var3 == -1) {
            this.p = new HostPortrange(var1, var2);
         } else {
            this.p = new HostPortrange(var1, var2.substring(var3 + 1));
         }

      }

      boolean implies(URLPermission.Authority var1) {
         return this.impliesHostrange(var1) && this.impliesPortrange(var1);
      }

      private boolean impliesHostrange(URLPermission.Authority var1) {
         String var2 = this.p.hostname();
         String var3 = var1.p.hostname();
         if (this.p.wildcard() && var2.equals("")) {
            return true;
         } else if (var1.p.wildcard() && var3.equals("")) {
            return false;
         } else if (var2.equals(var3)) {
            return true;
         } else {
            return this.p.wildcard() ? var3.endsWith(var2) : false;
         }
      }

      private boolean impliesPortrange(URLPermission.Authority var1) {
         int[] var2 = this.p.portrange();
         int[] var3 = var1.p.portrange();
         if (var2[0] == -1) {
            return true;
         } else {
            return var2[0] <= var3[0] && var2[1] >= var3[1];
         }
      }

      boolean equals(URLPermission.Authority var1) {
         return this.p.equals(var1.p);
      }

      public int hashCode() {
         return this.p.hashCode();
      }
   }
}
