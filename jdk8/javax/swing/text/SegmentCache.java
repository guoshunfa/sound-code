package javax.swing.text;

import java.util.ArrayList;
import java.util.List;

class SegmentCache {
   private static SegmentCache sharedCache = new SegmentCache();
   private List<Segment> segments = new ArrayList(11);

   public static SegmentCache getSharedInstance() {
      return sharedCache;
   }

   public static Segment getSharedSegment() {
      return getSharedInstance().getSegment();
   }

   public static void releaseSharedSegment(Segment var0) {
      getSharedInstance().releaseSegment(var0);
   }

   public SegmentCache() {
   }

   public Segment getSegment() {
      synchronized(this) {
         int var2 = this.segments.size();
         if (var2 > 0) {
            return (Segment)this.segments.remove(var2 - 1);
         }
      }

      return new SegmentCache.CachedSegment();
   }

   public void releaseSegment(Segment var1) {
      if (var1 instanceof SegmentCache.CachedSegment) {
         synchronized(this) {
            var1.array = null;
            var1.count = 0;
            this.segments.add(var1);
         }
      }

   }

   private static class CachedSegment extends Segment {
      private CachedSegment() {
      }

      // $FF: synthetic method
      CachedSegment(Object var1) {
         this();
      }
   }
}
