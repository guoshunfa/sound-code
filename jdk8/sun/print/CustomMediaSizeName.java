package sun.print;

import java.util.ArrayList;
import javax.print.attribute.EnumSyntax;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;

class CustomMediaSizeName extends MediaSizeName {
   private static ArrayList customStringTable = new ArrayList();
   private static ArrayList customEnumTable = new ArrayList();
   private String choiceName;
   private MediaSizeName mediaName;
   private static final long serialVersionUID = 7412807582228043717L;

   private CustomMediaSizeName(int var1) {
      super(var1);
   }

   private static synchronized int nextValue(String var0) {
      customStringTable.add(var0);
      return customStringTable.size() - 1;
   }

   public CustomMediaSizeName(String var1) {
      super(nextValue(var1));
      customEnumTable.add(this);
      this.choiceName = null;
      this.mediaName = null;
   }

   public CustomMediaSizeName(String var1, String var2, float var3, float var4) {
      super(nextValue(var1));
      this.choiceName = var2;
      customEnumTable.add(this);
      this.mediaName = null;

      try {
         this.mediaName = MediaSize.findMedia(var3, var4, 25400);
      } catch (IllegalArgumentException var10) {
      }

      if (this.mediaName != null) {
         MediaSize var5 = MediaSize.getMediaSizeForName(this.mediaName);
         if (var5 == null) {
            this.mediaName = null;
         } else {
            float var6 = var5.getX(25400);
            float var7 = var5.getY(25400);
            float var8 = Math.abs(var6 - var3);
            float var9 = Math.abs(var7 - var4);
            if ((double)var8 > 0.1D || (double)var9 > 0.1D) {
               this.mediaName = null;
            }
         }
      }

   }

   public String getChoiceName() {
      return this.choiceName;
   }

   public MediaSizeName getStandardMedia() {
      return this.mediaName;
   }

   public static MediaSizeName findMedia(Media[] var0, float var1, float var2, int var3) {
      if (var1 > 0.0F && var2 > 0.0F && var3 >= 1) {
         if (var0 != null && var0.length != 0) {
            int var4 = 0;
            MediaSizeName[] var5 = new MediaSizeName[var0.length];

            int var6;
            for(var6 = 0; var6 < var0.length; ++var6) {
               if (var0[var6] instanceof MediaSizeName) {
                  var5[var4++] = (MediaSizeName)var0[var6];
               }
            }

            if (var4 == 0) {
               return null;
            } else {
               var6 = 0;
               double var7 = (double)(var1 * var1 + var2 * var2);

               for(int var14 = 0; var14 < var4; ++var14) {
                  MediaSize var15 = MediaSize.getMediaSizeForName(var5[var14]);
                  if (var15 != null) {
                     float[] var11 = var15.getSize(var3);
                     if (var1 == var11[0] && var2 == var11[1]) {
                        var6 = var14;
                        break;
                     }

                     float var12 = var1 - var11[0];
                     float var13 = var2 - var11[1];
                     double var9 = (double)(var12 * var12 + var13 * var13);
                     if (var9 < var7) {
                        var7 = var9;
                        var6 = var14;
                     }
                  }
               }

               return var5[var6];
            }
         } else {
            throw new IllegalArgumentException("args must have valid array of media");
         }
      } else {
         throw new IllegalArgumentException("args must be +ve values");
      }
   }

   public Media[] getSuperEnumTable() {
      return (Media[])((Media[])super.getEnumValueTable());
   }

   protected String[] getStringTable() {
      String[] var1 = new String[customStringTable.size()];
      return (String[])((String[])customStringTable.toArray(var1));
   }

   protected EnumSyntax[] getEnumValueTable() {
      MediaSizeName[] var1 = new MediaSizeName[customEnumTable.size()];
      return (MediaSizeName[])((MediaSizeName[])customEnumTable.toArray(var1));
   }
}
