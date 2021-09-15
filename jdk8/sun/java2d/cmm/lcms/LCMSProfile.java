package sun.java2d.cmm.lcms;

import java.util.Arrays;
import java.util.HashMap;
import sun.java2d.cmm.Profile;

final class LCMSProfile extends Profile {
   private final LCMSProfile.TagCache tagCache;
   private final Object disposerReferent;

   LCMSProfile(long var1, Object var3) {
      super(var1);
      this.disposerReferent = var3;
      this.tagCache = new LCMSProfile.TagCache(this);
   }

   final long getLcmsPtr() {
      return this.getNativePtr();
   }

   LCMSProfile.TagData getTag(int var1) {
      return this.tagCache.getTag(var1);
   }

   void clearTagCache() {
      this.tagCache.clear();
   }

   static class TagData {
      private int signature;
      private byte[] data;

      TagData(int var1, byte[] var2) {
         this.signature = var1;
         this.data = var2;
      }

      int getSize() {
         return this.data.length;
      }

      byte[] getData() {
         return Arrays.copyOf(this.data, this.data.length);
      }

      void copyDataTo(byte[] var1) {
         System.arraycopy(this.data, 0, var1, 0, this.data.length);
      }

      int getSignature() {
         return this.signature;
      }
   }

   static class TagCache {
      final LCMSProfile profile;
      private HashMap<Integer, LCMSProfile.TagData> tags;

      TagCache(LCMSProfile var1) {
         this.profile = var1;
         this.tags = new HashMap();
      }

      LCMSProfile.TagData getTag(int var1) {
         LCMSProfile.TagData var2 = (LCMSProfile.TagData)this.tags.get(var1);
         if (var2 == null) {
            byte[] var3 = LCMS.getTagNative(this.profile.getNativePtr(), var1);
            if (var3 != null) {
               var2 = new LCMSProfile.TagData(var1, var3);
               this.tags.put(var1, var2);
            }
         }

         return var2;
      }

      void clear() {
         this.tags.clear();
      }
   }
}
