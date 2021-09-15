package sun.java2d.cmm;

import java.awt.color.CMMException;

public class Profile {
   private final long nativePtr;

   protected Profile(long var1) {
      this.nativePtr = var1;
   }

   protected final long getNativePtr() {
      if (this.nativePtr == 0L) {
         throw new CMMException("Invalid profile: ptr is null");
      } else {
         return this.nativePtr;
      }
   }
}
