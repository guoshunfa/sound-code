package sun.java2d.pipe;

public class RegionClipSpanIterator implements SpanIterator {
   Region rgn;
   SpanIterator spanIter;
   RegionIterator resetState;
   RegionIterator lwm;
   RegionIterator row;
   RegionIterator box;
   int spanlox;
   int spanhix;
   int spanloy;
   int spanhiy;
   int lwmloy;
   int lwmhiy;
   int rgnlox;
   int rgnloy;
   int rgnhix;
   int rgnhiy;
   int rgnbndslox;
   int rgnbndsloy;
   int rgnbndshix;
   int rgnbndshiy;
   int[] rgnbox = new int[4];
   int[] spanbox = new int[4];
   boolean doNextSpan;
   boolean doNextBox;
   boolean done = false;

   public RegionClipSpanIterator(Region var1, SpanIterator var2) {
      this.spanIter = var2;
      this.resetState = var1.getIterator();
      this.lwm = this.resetState.createCopy();
      if (!this.lwm.nextYRange(this.rgnbox)) {
         this.done = true;
      } else {
         this.rgnloy = this.lwmloy = this.rgnbox[1];
         this.rgnhiy = this.lwmhiy = this.rgnbox[3];
         var1.getBounds(this.rgnbox);
         this.rgnbndslox = this.rgnbox[0];
         this.rgnbndsloy = this.rgnbox[1];
         this.rgnbndshix = this.rgnbox[2];
         this.rgnbndshiy = this.rgnbox[3];
         if (this.rgnbndslox < this.rgnbndshix && this.rgnbndsloy < this.rgnbndshiy) {
            this.rgn = var1;
            this.row = this.lwm.createCopy();
            this.box = this.row.createCopy();
            this.doNextSpan = true;
            this.doNextBox = false;
         } else {
            this.done = true;
         }
      }
   }

   public void getPathBox(int[] var1) {
      int[] var2 = new int[4];
      this.rgn.getBounds(var2);
      this.spanIter.getPathBox(var1);
      if (var1[0] < var2[0]) {
         var1[0] = var2[0];
      }

      if (var1[1] < var2[1]) {
         var1[1] = var2[1];
      }

      if (var1[2] > var2[2]) {
         var1[2] = var2[2];
      }

      if (var1[3] > var2[3]) {
         var1[3] = var2[3];
      }

   }

   public void intersectClipBox(int var1, int var2, int var3, int var4) {
      this.spanIter.intersectClipBox(var1, var2, var3, var4);
   }

   public boolean nextSpan(int[] var1) {
      if (this.done) {
         return false;
      } else {
         boolean var6 = false;

         while(true) {
            do {
               do {
                  do {
                     do {
                        while(!this.doNextSpan) {
                           boolean var7;
                           if (var6) {
                              var6 = false;
                              var7 = this.row.nextYRange(this.rgnbox);
                              if (var7) {
                                 this.rgnloy = this.rgnbox[1];
                                 this.rgnhiy = this.rgnbox[3];
                              }

                              if (var7 && this.rgnloy < this.spanhiy) {
                                 this.box.copyStateFrom(this.row);
                                 this.doNextBox = true;
                              } else {
                                 this.doNextSpan = true;
                              }
                           } else if (this.doNextBox) {
                              var7 = this.box.nextXBand(this.rgnbox);
                              if (var7) {
                                 this.rgnlox = this.rgnbox[0];
                                 this.rgnhix = this.rgnbox[2];
                              }

                              if (var7 && this.rgnlox < this.spanhix) {
                                 this.doNextBox = this.rgnhix <= this.spanlox;
                              } else {
                                 this.doNextBox = false;
                                 if (this.rgnhiy >= this.spanhiy) {
                                    this.doNextSpan = true;
                                 } else {
                                    var6 = true;
                                 }
                              }
                           } else {
                              this.doNextBox = true;
                              int var2;
                              if (this.spanlox > this.rgnlox) {
                                 var2 = this.spanlox;
                              } else {
                                 var2 = this.rgnlox;
                              }

                              int var3;
                              if (this.spanloy > this.rgnloy) {
                                 var3 = this.spanloy;
                              } else {
                                 var3 = this.rgnloy;
                              }

                              int var4;
                              if (this.spanhix < this.rgnhix) {
                                 var4 = this.spanhix;
                              } else {
                                 var4 = this.rgnhix;
                              }

                              int var5;
                              if (this.spanhiy < this.rgnhiy) {
                                 var5 = this.spanhiy;
                              } else {
                                 var5 = this.rgnhiy;
                              }

                              if (var2 < var4 && var3 < var5) {
                                 var1[0] = var2;
                                 var1[1] = var3;
                                 var1[2] = var4;
                                 var1[3] = var5;
                                 return true;
                              }
                           }
                        }

                        if (!this.spanIter.nextSpan(this.spanbox)) {
                           this.done = true;
                           return false;
                        }

                        this.spanlox = this.spanbox[0];
                     } while(this.spanlox >= this.rgnbndshix);

                     this.spanloy = this.spanbox[1];
                  } while(this.spanloy >= this.rgnbndshiy);

                  this.spanhix = this.spanbox[2];
               } while(this.spanhix <= this.rgnbndslox);

               this.spanhiy = this.spanbox[3];
            } while(this.spanhiy <= this.rgnbndsloy);

            if (this.lwmloy > this.spanloy) {
               this.lwm.copyStateFrom(this.resetState);
               this.lwm.nextYRange(this.rgnbox);
               this.lwmloy = this.rgnbox[1];
               this.lwmhiy = this.rgnbox[3];
            }

            while(this.lwmhiy <= this.spanloy && this.lwm.nextYRange(this.rgnbox)) {
               this.lwmloy = this.rgnbox[1];
               this.lwmhiy = this.rgnbox[3];
            }

            if (this.lwmhiy > this.spanloy && this.lwmloy < this.spanhiy) {
               if (this.rgnloy != this.lwmloy) {
                  this.row.copyStateFrom(this.lwm);
                  this.rgnloy = this.lwmloy;
                  this.rgnhiy = this.lwmhiy;
               }

               this.box.copyStateFrom(this.row);
               this.doNextBox = true;
               this.doNextSpan = false;
            }
         }
      }
   }

   public void skipDownTo(int var1) {
      this.spanIter.skipDownTo(var1);
   }

   public long getNativeIterator() {
      return 0L;
   }

   protected void finalize() {
   }
}
