package sun.nio.fs;

class UnixFileKey {
   private final long st_dev;
   private final long st_ino;

   UnixFileKey(long var1, long var3) {
      this.st_dev = var1;
      this.st_ino = var3;
   }

   public int hashCode() {
      return (int)(this.st_dev ^ this.st_dev >>> 32) + (int)(this.st_ino ^ this.st_ino >>> 32);
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof UnixFileKey)) {
         return false;
      } else {
         UnixFileKey var2 = (UnixFileKey)var1;
         return this.st_dev == var2.st_dev && this.st_ino == var2.st_ino;
      }
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("(dev=").append(Long.toHexString(this.st_dev)).append(",ino=").append(this.st_ino).append(')');
      return var1.toString();
   }
}
