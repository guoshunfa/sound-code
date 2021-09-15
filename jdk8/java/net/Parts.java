package java.net;

class Parts {
   String path;
   String query;
   String ref;

   Parts(String var1) {
      int var2 = var1.indexOf(35);
      this.ref = var2 < 0 ? null : var1.substring(var2 + 1);
      var1 = var2 < 0 ? var1 : var1.substring(0, var2);
      int var3 = var1.lastIndexOf(63);
      if (var3 != -1) {
         this.query = var1.substring(var3 + 1);
         this.path = var1.substring(0, var3);
      } else {
         this.path = var1;
      }

   }

   String getPath() {
      return this.path;
   }

   String getQuery() {
      return this.query;
   }

   String getRef() {
      return this.ref;
   }
}
