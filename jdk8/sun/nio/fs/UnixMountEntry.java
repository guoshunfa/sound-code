package sun.nio.fs;

class UnixMountEntry {
   private byte[] name;
   private byte[] dir;
   private byte[] fstype;
   private byte[] opts;
   private long dev;
   private volatile String fstypeAsString;
   private volatile String optionsAsString;

   String name() {
      return Util.toString(this.name);
   }

   String fstype() {
      if (this.fstypeAsString == null) {
         this.fstypeAsString = Util.toString(this.fstype);
      }

      return this.fstypeAsString;
   }

   byte[] dir() {
      return this.dir;
   }

   long dev() {
      return this.dev;
   }

   boolean hasOption(String var1) {
      if (this.optionsAsString == null) {
         this.optionsAsString = Util.toString(this.opts);
      }

      String[] var2 = Util.split(this.optionsAsString, ',');
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String var5 = var2[var4];
         if (var5.equals(var1)) {
            return true;
         }
      }

      return false;
   }

   boolean isIgnored() {
      return this.hasOption("ignore");
   }

   boolean isReadOnly() {
      return this.hasOption("ro");
   }
}
