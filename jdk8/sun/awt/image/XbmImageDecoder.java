package sun.awt.image;

import java.awt.image.IndexColorModel;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class XbmImageDecoder extends ImageDecoder {
   private static byte[] XbmColormap = new byte[]{-1, -1, -1, 0, 0, 0};
   private static int XbmHints = 30;

   public XbmImageDecoder(InputStreamImageSource var1, InputStream var2) {
      super(var1, var2);
      if (!(this.input instanceof BufferedInputStream)) {
         this.input = new BufferedInputStream(this.input, 80);
      }

   }

   private static void error(String var0) throws ImageFormatException {
      throw new ImageFormatException(var0);
   }

   public void produceImage() throws IOException, ImageFormatException {
      char[] var1 = new char[80];
      int var3 = 0;
      byte var4 = 0;
      int var5 = 0;
      int var6 = 0;
      int var7 = 0;
      int var8 = 0;
      boolean var9 = true;
      byte[] var10 = null;
      IndexColorModel var11 = null;

      int var2;
      while(!this.aborted && (var2 = this.input.read()) != -1) {
         if ((97 > var2 || var2 > 122) && (65 > var2 || var2 > 90) && (48 > var2 || var2 > 57) && var2 != 35 && var2 != 95) {
            if (var3 > 0) {
               int var12 = var3;
               var3 = 0;
               if (var9) {
                  if (var12 != 7 || var1[0] != '#' || var1[1] != 'd' || var1[2] != 'e' || var1[3] != 'f' || var1[4] != 'i' || var1[5] != 'n' || var1[6] != 'e') {
                     error("Not an XBM file");
                  }

                  var9 = false;
               }

               if (var1[var12 - 1] == 'h') {
                  var4 = 1;
               } else if (var1[var12 - 1] == 't' && var12 > 1 && var1[var12 - 2] == 'h') {
                  var4 = 2;
               } else {
                  int var13;
                  int var14;
                  char var15;
                  if (var12 > 2 && var4 < 0 && var1[0] == '0' && var1[1] == 'x') {
                     var13 = 0;

                     for(var14 = 2; var14 < var12; ++var14) {
                        var15 = var1[var14];
                        if ('0' <= var15 && var15 <= '9') {
                           var2 = var15 - 48;
                        } else if ('A' <= var15 && var15 <= 'Z') {
                           var2 = var15 - 65 + 10;
                        } else if ('a' <= var15 && var15 <= 'z') {
                           var2 = var15 - 97 + 10;
                        } else {
                           var2 = 0;
                        }

                        var13 = var13 * 16 + var2;
                     }

                     for(var14 = 1; var14 <= 128; var14 <<= 1) {
                        if (var7 < var6) {
                           if ((var13 & var14) != 0) {
                              var10[var7] = 1;
                           } else {
                              var10[var7] = 0;
                           }
                        }

                        ++var7;
                     }

                     if (var7 >= var6) {
                        if (this.setPixels(0, var8, var6, 1, var11, var10, 0, var6) <= 0) {
                           return;
                        }

                        var7 = 0;
                        if (var8++ >= var5) {
                           break;
                        }
                     }
                  } else {
                     var13 = 0;

                     for(var14 = 0; var14 < var12; ++var14) {
                        if ('0' > (var15 = var1[var14]) || var15 > '9') {
                           var13 = -1;
                           break;
                        }

                        var13 = var13 * 10 + var15 - 48;
                     }

                     if (var13 > 0 && var4 > 0) {
                        if (var4 == 1) {
                           var6 = var13;
                        } else {
                           var5 = var13;
                        }

                        if (var6 != 0 && var5 != 0) {
                           var11 = new IndexColorModel(8, 2, XbmColormap, 0, false, 0);
                           this.setDimensions(var6, var5);
                           this.setColorModel(var11);
                           this.setHints(XbmHints);
                           this.headerComplete();
                           var10 = new byte[var6];
                           var4 = -1;
                        } else {
                           var4 = 0;
                        }
                     }
                  }
               }
            }
         } else if (var3 < 78) {
            var1[var3++] = (char)var2;
         }
      }

      this.input.close();
      this.imageComplete(3, true);
   }
}
