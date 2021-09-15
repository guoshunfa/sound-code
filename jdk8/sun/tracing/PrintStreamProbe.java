package sun.tracing;

class PrintStreamProbe extends ProbeSkeleton {
   private PrintStreamProvider provider;
   private String name;

   PrintStreamProbe(PrintStreamProvider var1, String var2, Class<?>[] var3) {
      super(var3);
      this.provider = var1;
      this.name = var2;
   }

   public boolean isEnabled() {
      return true;
   }

   public void uncheckedTrigger(Object[] var1) {
      StringBuffer var2 = new StringBuffer();
      var2.append(this.provider.getName());
      var2.append(".");
      var2.append(this.name);
      var2.append("(");
      boolean var3 = true;
      Object[] var4 = var1;
      int var5 = var1.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Object var7 = var4[var6];
         if (!var3) {
            var2.append(",");
         } else {
            var3 = false;
         }

         var2.append(var7.toString());
      }

      var2.append(")");
      this.provider.getStream().println(var2.toString());
   }
}
