package sun.java2d.pipe;

public class RegionSpanIterator implements SpanIterator {
   RegionIterator ri;
   int lox;
   int loy;
   int hix;
   int hiy;
   int curloy;
   int curhiy;
   boolean done = false;
   boolean isrect;

   public RegionSpanIterator(Region var1) {
      int[] var2 = new int[4];
      var1.getBounds(var2);
      this.lox = var2[0];
      this.loy = var2[1];
      this.hix = var2[2];
      this.hiy = var2[3];
      this.isrect = var1.isRectangular();
      this.ri = var1.getIterator();
   }

   public void getPathBox(int[] var1) {
      var1[0] = this.lox;
      var1[1] = this.loy;
      var1[2] = this.hix;
      var1[3] = this.hiy;
   }

   public void intersectClipBox(int var1, int var2, int var3, int var4) {
      if (var1 > this.lox) {
         this.lox = var1;
      }

      if (var2 > this.loy) {
         this.loy = var2;
      }

      if (var3 < this.hix) {
         this.hix = var3;
      }

      if (var4 < this.hiy) {
         this.hiy = var4;
      }

      this.done = this.lox >= this.hix || this.loy >= this.hiy;
   }

   public boolean nextSpan(int[] var1) {
      if (this.done) {
         return false;
      } else if (this.isrect) {
         this.getPathBox(var1);
         this.done = true;
         return true;
      } else {
         int var4 = this.curloy;
         int var5 = this.curhiy;

         do {
            while(this.ri.nextXBand(var1)) {
               int var2 = var1[0];
               int var3 = var1[2];
               if (var2 < this.lox) {
                  var2 = this.lox;
               }

               if (var3 > this.hix) {
                  var3 = this.hix;
               }

               if (var2 < var3 && var4 < var5) {
                  var1[0] = var2;
                  var1[1] = this.curloy = var4;
                  var1[2] = var3;
                  var1[3] = this.curhiy = var5;
                  return true;
               }
            }

            if (!this.ri.nextYRange(var1)) {
               this.done = true;
               return false;
            }

            var4 = var1[1];
            var5 = var1[3];
            if (var4 < this.loy) {
               var4 = this.loy;
            }

            if (var5 > this.hiy) {
               var5 = this.hiy;
            }
         } while(var4 < this.hiy);

         this.done = true;
         return false;
      }
   }

   public void skipDownTo(int var1) {
      this.loy = var1;
   }

   public long getNativeIterator() {
      return 0L;
   }
}
