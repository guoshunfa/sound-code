package sun.security.tools.policytool;

class Perm {
   public final String CLASS;
   public final String FULL_CLASS;
   public final String[] TARGETS;
   public final String[] ACTIONS;

   public Perm(String var1, String var2, String[] var3, String[] var4) {
      this.CLASS = var1;
      this.FULL_CLASS = var2;
      this.TARGETS = var3;
      this.ACTIONS = var4;
   }
}
