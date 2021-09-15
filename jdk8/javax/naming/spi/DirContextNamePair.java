package javax.naming.spi;

import javax.naming.Name;
import javax.naming.directory.DirContext;

class DirContextNamePair {
   DirContext ctx;
   Name name;

   DirContextNamePair(DirContext var1, Name var2) {
      this.ctx = var1;
      this.name = var2;
   }

   DirContext getDirContext() {
      return this.ctx;
   }

   Name getName() {
      return this.name;
   }
}
