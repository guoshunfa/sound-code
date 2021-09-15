package sun.nio.ch;

class OptionKey {
   private int level;
   private int name;

   OptionKey(int var1, int var2) {
      this.level = var1;
      this.name = var2;
   }

   int level() {
      return this.level;
   }

   int name() {
      return this.name;
   }
}
