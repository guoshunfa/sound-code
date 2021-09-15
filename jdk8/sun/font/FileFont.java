package sun.font;

import java.awt.FontFormatException;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.lang.ref.Reference;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Iterator;
import sun.java2d.Disposer;
import sun.java2d.DisposerRecord;

public abstract class FileFont extends PhysicalFont {
   protected boolean useJavaRasterizer = true;
   protected int fileSize;
   protected FontScaler scaler;
   protected boolean checkedNatives;
   protected boolean useNatives;
   protected NativeFont[] nativeFonts;
   protected char[] glyphToCharMap;

   FileFont(String var1, Object var2) throws FontFormatException {
      super(var1, var2);
   }

   FontStrike createStrike(FontStrikeDesc var1) {
      if (!this.checkedNatives) {
         this.checkUseNatives();
      }

      return new FileFontStrike(this, var1);
   }

   protected boolean checkUseNatives() {
      this.checkedNatives = true;
      return this.useNatives;
   }

   protected abstract void close();

   abstract ByteBuffer readBlock(int var1, int var2);

   public boolean canDoStyle(int var1) {
      return true;
   }

   void setFileToRemove(File var1, CreatedFontTracker var2) {
      Disposer.addObjectRecord(this, new FileFont.CreatedFontFileDisposerRecord(var1, var2));
   }

   static void setFileToRemove(Object var0, File var1, CreatedFontTracker var2) {
      Disposer.addObjectRecord(var0, new FileFont.CreatedFontFileDisposerRecord(var1, var2));
   }

   synchronized void deregisterFontAndClearStrikeCache() {
      SunFontManager var1 = SunFontManager.getInstance();
      var1.deRegisterBadFont(this);
      Iterator var2 = this.strikeCache.values().iterator();

      while(var2.hasNext()) {
         Reference var3 = (Reference)var2.next();
         if (var3 != null) {
            FileFontStrike var4 = (FileFontStrike)var3.get();
            if (var4 != null && var4.pScalerContext != 0L) {
               this.scaler.invalidateScalerContext(var4.pScalerContext);
            }
         }
      }

      if (this.scaler != null) {
         this.scaler.dispose();
      }

      this.scaler = FontScaler.getNullScaler();
   }

   StrikeMetrics getFontMetrics(long var1) {
      try {
         return this.getScaler().getFontMetrics(var1);
      } catch (FontScalerException var4) {
         this.scaler = FontScaler.getNullScaler();
         return this.getFontMetrics(var1);
      }
   }

   float getGlyphAdvance(long var1, int var3) {
      try {
         return this.getScaler().getGlyphAdvance(var1, var3);
      } catch (FontScalerException var5) {
         this.scaler = FontScaler.getNullScaler();
         return this.getGlyphAdvance(var1, var3);
      }
   }

   void getGlyphMetrics(long var1, int var3, Point2D.Float var4) {
      try {
         this.getScaler().getGlyphMetrics(var1, var3, var4);
      } catch (FontScalerException var6) {
         this.scaler = FontScaler.getNullScaler();
         this.getGlyphMetrics(var1, var3, var4);
      }

   }

   long getGlyphImage(long var1, int var3) {
      try {
         return this.getScaler().getGlyphImage(var1, var3);
      } catch (FontScalerException var5) {
         this.scaler = FontScaler.getNullScaler();
         return this.getGlyphImage(var1, var3);
      }
   }

   Rectangle2D.Float getGlyphOutlineBounds(long var1, int var3) {
      try {
         return this.getScaler().getGlyphOutlineBounds(var1, var3);
      } catch (FontScalerException var5) {
         this.scaler = FontScaler.getNullScaler();
         return this.getGlyphOutlineBounds(var1, var3);
      }
   }

   GeneralPath getGlyphOutline(long var1, int var3, float var4, float var5) {
      try {
         return this.getScaler().getGlyphOutline(var1, var3, var4, var5);
      } catch (FontScalerException var7) {
         this.scaler = FontScaler.getNullScaler();
         return this.getGlyphOutline(var1, var3, var4, var5);
      }
   }

   GeneralPath getGlyphVectorOutline(long var1, int[] var3, int var4, float var5, float var6) {
      try {
         return this.getScaler().getGlyphVectorOutline(var1, var3, var4, var5, var6);
      } catch (FontScalerException var8) {
         this.scaler = FontScaler.getNullScaler();
         return this.getGlyphVectorOutline(var1, var3, var4, var5, var6);
      }
   }

   protected abstract FontScaler getScaler();

   protected long getUnitsPerEm() {
      return this.getScaler().getUnitsPerEm();
   }

   protected String getPublicFileName() {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 == null) {
         return this.platName;
      } else {
         boolean var2 = true;

         try {
            var1.checkPropertyAccess("java.io.tmpdir");
         } catch (SecurityException var7) {
            var2 = false;
         }

         if (var2) {
            return this.platName;
         } else {
            final File var3 = new File(this.platName);
            Boolean var4 = Boolean.FALSE;

            try {
               var4 = (Boolean)AccessController.doPrivileged(new PrivilegedExceptionAction<Boolean>() {
                  public Boolean run() {
                     File var1 = new File(System.getProperty("java.io.tmpdir"));

                     try {
                        String var2 = var1.getCanonicalPath();
                        String var3x = var3.getCanonicalPath();
                        return var3x == null || var3x.startsWith(var2);
                     } catch (IOException var4) {
                        return Boolean.TRUE;
                     }
                  }
               });
            } catch (PrivilegedActionException var6) {
               var4 = Boolean.TRUE;
            }

            return var4 ? "temp file" : this.platName;
         }
      }
   }

   private static class CreatedFontFileDisposerRecord implements DisposerRecord {
      File fontFile;
      CreatedFontTracker tracker;

      private CreatedFontFileDisposerRecord(File var1, CreatedFontTracker var2) {
         this.fontFile = null;
         this.fontFile = var1;
         this.tracker = var2;
      }

      public void dispose() {
         AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
               if (CreatedFontFileDisposerRecord.this.fontFile != null) {
                  try {
                     if (CreatedFontFileDisposerRecord.this.tracker != null) {
                        CreatedFontFileDisposerRecord.this.tracker.subBytes((int)CreatedFontFileDisposerRecord.this.fontFile.length());
                     }

                     CreatedFontFileDisposerRecord.this.fontFile.delete();
                     SunFontManager.getInstance().tmpFontFiles.remove(CreatedFontFileDisposerRecord.this.fontFile);
                  } catch (Exception var2) {
                  }
               }

               return null;
            }
         });
      }

      // $FF: synthetic method
      CreatedFontFileDisposerRecord(File var1, CreatedFontTracker var2, Object var3) {
         this(var1, var2);
      }
   }
}
