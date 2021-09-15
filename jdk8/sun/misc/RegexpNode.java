package sun.misc;

import java.io.PrintStream;

class RegexpNode {
   char c;
   RegexpNode firstchild;
   RegexpNode nextsibling;
   int depth;
   boolean exact;
   Object result;
   String re = null;

   RegexpNode() {
      this.c = '#';
      this.depth = 0;
   }

   RegexpNode(char var1, int var2) {
      this.c = var1;
      this.depth = var2;
   }

   RegexpNode add(char var1) {
      RegexpNode var2 = this.firstchild;
      if (var2 == null) {
         var2 = new RegexpNode(var1, this.depth + 1);
      } else {
         while(true) {
            if (var2 == null) {
               var2 = new RegexpNode(var1, this.depth + 1);
               var2.nextsibling = this.firstchild;
               break;
            }

            if (var2.c == var1) {
               return var2;
            }

            var2 = var2.nextsibling;
         }
      }

      this.firstchild = var2;
      return var2;
   }

   RegexpNode find(char var1) {
      for(RegexpNode var2 = this.firstchild; var2 != null; var2 = var2.nextsibling) {
         if (var2.c == var1) {
            return var2;
         }
      }

      return null;
   }

   void print(PrintStream var1) {
      if (this.nextsibling != null) {
         RegexpNode var2 = this;
         var1.print("(");

         while(var2 != null) {
            var1.write(var2.c);
            if (var2.firstchild != null) {
               var2.firstchild.print(var1);
            }

            var2 = var2.nextsibling;
            var1.write(var2 != null ? 124 : 41);
         }
      } else {
         var1.write(this.c);
         if (this.firstchild != null) {
            this.firstchild.print(var1);
         }
      }

   }
}
