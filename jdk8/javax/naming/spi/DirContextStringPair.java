package javax.naming.spi;

import javax.naming.directory.DirContext;

class DirContextStringPair {
   DirContext ctx;
   String str;

   DirContextStringPair(DirContext var1, String var2) {
      this.ctx = var1;
      this.str = var2;
   }

   DirContext getDirContext() {
      return this.ctx;
   }

   String getString() {
      return this.str;
   }
}
