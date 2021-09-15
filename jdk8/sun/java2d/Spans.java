package sun.java2d;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class Spans {
   private static final int kMaxAddsSinceSort = 256;
   private List mSpans = new Vector(256);
   private int mAddsSinceSort = 0;

   public void add(float var1, float var2) {
      if (this.mSpans != null) {
         this.mSpans.add(new Spans.Span(var1, var2));
         if (++this.mAddsSinceSort >= 256) {
            this.sortAndCollapse();
         }
      }

   }

   public void addInfinite() {
      this.mSpans = null;
   }

   public boolean intersects(float var1, float var2) {
      boolean var3;
      if (this.mSpans != null) {
         if (this.mAddsSinceSort > 0) {
            this.sortAndCollapse();
         }

         int var4 = Collections.binarySearch(this.mSpans, new Spans.Span(var1, var2), Spans.SpanIntersection.instance);
         var3 = var4 >= 0;
      } else {
         var3 = true;
      }

      return var3;
   }

   private void sortAndCollapse() {
      Collections.sort(this.mSpans);
      this.mAddsSinceSort = 0;
      Iterator var1 = this.mSpans.iterator();
      Spans.Span var2 = null;
      if (var1.hasNext()) {
         var2 = (Spans.Span)var1.next();
      }

      while(var1.hasNext()) {
         Spans.Span var3 = (Spans.Span)var1.next();
         if (var2.subsume(var3)) {
            var1.remove();
         } else {
            var2 = var3;
         }
      }

   }

   static class SpanIntersection implements Comparator {
      static final Spans.SpanIntersection instance = new Spans.SpanIntersection();

      private SpanIntersection() {
      }

      public int compare(Object var1, Object var2) {
         Spans.Span var4 = (Spans.Span)var1;
         Spans.Span var5 = (Spans.Span)var2;
         byte var3;
         if (var4.getEnd() <= var5.getStart()) {
            var3 = -1;
         } else if (var4.getStart() >= var5.getEnd()) {
            var3 = 1;
         } else {
            var3 = 0;
         }

         return var3;
      }
   }

   static class Span implements Comparable {
      private float mStart;
      private float mEnd;

      Span(float var1, float var2) {
         this.mStart = var1;
         this.mEnd = var2;
      }

      final float getStart() {
         return this.mStart;
      }

      final float getEnd() {
         return this.mEnd;
      }

      final void setStart(float var1) {
         this.mStart = var1;
      }

      final void setEnd(float var1) {
         this.mEnd = var1;
      }

      boolean subsume(Spans.Span var1) {
         boolean var2 = this.contains(var1.mStart);
         if (var2 && var1.mEnd > this.mEnd) {
            this.mEnd = var1.mEnd;
         }

         return var2;
      }

      boolean contains(float var1) {
         return this.mStart <= var1 && var1 < this.mEnd;
      }

      public int compareTo(Object var1) {
         Spans.Span var2 = (Spans.Span)var1;
         float var3 = var2.getStart();
         byte var4;
         if (this.mStart < var3) {
            var4 = -1;
         } else if (this.mStart > var3) {
            var4 = 1;
         } else {
            var4 = 0;
         }

         return var4;
      }

      public String toString() {
         return "Span: " + this.mStart + " to " + this.mEnd;
      }
   }
}
