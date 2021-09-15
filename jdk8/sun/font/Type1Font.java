package sun.font;

import java.awt.FontFormatException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.HashSet;
import sun.java2d.Disposer;
import sun.java2d.DisposerRecord;

public class Type1Font extends FileFont {
   WeakReference bufferRef;
   private String psName;
   private static HashMap styleAbbreviationsMapping = new HashMap();
   private static HashSet styleNameTokes = new HashSet();
   private static final int PSEOFTOKEN = 0;
   private static final int PSNAMETOKEN = 1;
   private static final int PSSTRINGTOKEN = 2;

   public Type1Font(String var1, Object var2) throws FontFormatException {
      this(var1, var2, false);
   }

   public Type1Font(String var1, Object var2, boolean var3) throws FontFormatException {
      super(var1, var2);
      this.bufferRef = new WeakReference((Object)null);
      this.psName = null;
      this.fontRank = 4;
      this.checkedNatives = true;

      try {
         this.verify();
      } catch (Throwable var6) {
         if (var3) {
            Type1Font.T1DisposerRecord var5 = new Type1Font.T1DisposerRecord(var1);
            Disposer.addObjectRecord(this.bufferRef, var5);
            this.bufferRef = null;
         }

         if (var6 instanceof FontFormatException) {
            throw (FontFormatException)var6;
         } else {
            throw new FontFormatException("Unexpected runtime exception.");
         }
      }
   }

   private synchronized ByteBuffer getBuffer() throws FontFormatException {
      MappedByteBuffer var1 = (MappedByteBuffer)this.bufferRef.get();
      if (var1 == null) {
         try {
            RandomAccessFile var2 = (RandomAccessFile)AccessController.doPrivileged(new PrivilegedAction() {
               public Object run() {
                  try {
                     return new RandomAccessFile(Type1Font.this.platName, "r");
                  } catch (FileNotFoundException var2) {
                     return null;
                  }
               }
            });
            FileChannel var3 = var2.getChannel();
            this.fileSize = (int)var3.size();
            var1 = var3.map(FileChannel.MapMode.READ_ONLY, 0L, (long)this.fileSize);
            var1.position(0);
            this.bufferRef = new WeakReference(var1);
            var3.close();
         } catch (NullPointerException var4) {
            throw new FontFormatException(var4.toString());
         } catch (ClosedChannelException var5) {
            Thread.interrupted();
            return this.getBuffer();
         } catch (IOException var6) {
            throw new FontFormatException(var6.toString());
         }
      }

      return var1;
   }

   protected void close() {
   }

   void readFile(ByteBuffer var1) {
      RandomAccessFile var2 = null;

      try {
         var2 = (RandomAccessFile)AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
               try {
                  return new RandomAccessFile(Type1Font.this.platName, "r");
               } catch (FileNotFoundException var2) {
                  return null;
               }
            }
         });
         FileChannel var3 = var2.getChannel();

         while(var1.remaining() > 0 && var3.read(var1) != -1) {
         }
      } catch (NullPointerException var20) {
      } catch (ClosedChannelException var21) {
         try {
            if (var2 != null) {
               var2.close();
               var2 = null;
            }
         } catch (IOException var19) {
         }

         Thread.interrupted();
         this.readFile(var1);
      } catch (IOException var22) {
      } finally {
         if (var2 != null) {
            try {
               var2.close();
            } catch (IOException var18) {
            }
         }

      }

   }

   public synchronized ByteBuffer readBlock(int var1, int var2) {
      ByteBuffer var3 = null;

      try {
         var3 = this.getBuffer();
         if (var1 > this.fileSize) {
            var1 = this.fileSize;
         }

         var3.position(var1);
         return var3.slice();
      } catch (FontFormatException var5) {
         return null;
      }
   }

   private void verify() throws FontFormatException {
      ByteBuffer var1 = this.getBuffer();
      if (var1.capacity() < 6) {
         throw new FontFormatException("short file");
      } else {
         int var2 = var1.get(0) & 255;
         if ((var1.get(0) & 255) == 128) {
            this.verifyPFB(var1);
            var1.position(6);
         } else {
            this.verifyPFA(var1);
            var1.position(0);
         }

         this.initNames(var1);
         if (this.familyName != null && this.fullName != null) {
            this.setStyle();
         } else {
            throw new FontFormatException("Font name not found");
         }
      }
   }

   public int getFileSize() {
      if (this.fileSize == 0) {
         try {
            this.getBuffer();
         } catch (FontFormatException var2) {
         }
      }

      return this.fileSize;
   }

   private void verifyPFA(ByteBuffer var1) throws FontFormatException {
      if (var1.getShort() != 9505) {
         throw new FontFormatException("bad pfa font");
      }
   }

   private void verifyPFB(ByteBuffer var1) throws FontFormatException {
      int var2 = 0;

      while(true) {
         try {
            int var3 = var1.getShort(var2) & '\uffff';
            if (var3 != 32769 && var3 != 32770) {
               if (var3 == 32771) {
                  return;
               }

               throw new FontFormatException("bad pfb file");
            }

            var1.order(ByteOrder.LITTLE_ENDIAN);
            int var4 = var1.getInt(var2 + 2);
            var1.order(ByteOrder.BIG_ENDIAN);
            if (var4 <= 0) {
               throw new FontFormatException("bad segment length");
            }

            var2 += var4 + 6;
         } catch (BufferUnderflowException var5) {
            throw new FontFormatException(var5.toString());
         } catch (Exception var6) {
            throw new FontFormatException(var6.toString());
         }
      }
   }

   private void initNames(ByteBuffer var1) throws FontFormatException {
      boolean var2 = false;
      String var3 = null;

      try {
         while((this.fullName == null || this.familyName == null || this.psName == null || var3 == null) && !var2) {
            int var4 = this.nextTokenType(var1);
            if (var4 == 1) {
               int var5 = var1.position();
               if (var1.get(var5) == 70) {
                  String var6 = this.getSimpleToken(var1);
                  if ("FullName".equals(var6)) {
                     if (this.nextTokenType(var1) == 2) {
                        this.fullName = this.getString(var1);
                     }
                  } else if ("FamilyName".equals(var6)) {
                     if (this.nextTokenType(var1) == 2) {
                        this.familyName = this.getString(var1);
                     }
                  } else if ("FontName".equals(var6)) {
                     if (this.nextTokenType(var1) == 1) {
                        this.psName = this.getSimpleToken(var1);
                     }
                  } else if ("FontType".equals(var6)) {
                     String var7 = this.getSimpleToken(var1);
                     if ("def".equals(this.getSimpleToken(var1))) {
                        var3 = var7;
                     }
                  }
               } else {
                  while(var1.get() > 32) {
                  }
               }
            } else if (var4 == 0) {
               var2 = true;
            }
         }
      } catch (Exception var8) {
         throw new FontFormatException(var8.toString());
      }

      if (!"1".equals(var3)) {
         throw new FontFormatException("Unsupported font type");
      } else {
         if (this.psName == null) {
            var1.position(0);
            if (var1.getShort() != 9505) {
               var1.position(8);
            }

            String var9 = this.getSimpleToken(var1);
            if (!var9.startsWith("FontType1-") && !var9.startsWith("PS-AdobeFont-")) {
               throw new FontFormatException("Unsupported font format [" + var9 + "]");
            }

            this.psName = this.getSimpleToken(var1);
         }

         if (var2) {
            if (this.fullName != null) {
               this.familyName = this.fullName2FamilyName(this.fullName);
            } else if (this.familyName != null) {
               this.fullName = this.familyName;
            } else {
               this.fullName = this.psName2FullName(this.psName);
               this.familyName = this.psName2FamilyName(this.psName);
            }
         }

      }
   }

   private String fullName2FamilyName(String var1) {
      int var5;
      for(int var6 = var1.length(); var6 > 0; var6 = var5) {
         for(var5 = var6 - 1; var5 > 0 && var1.charAt(var5) != ' '; --var5) {
         }

         if (!this.isStyleToken(var1.substring(var5 + 1, var6))) {
            return var1.substring(0, var6);
         }
      }

      return var1;
   }

   private String expandAbbreviation(String var1) {
      return styleAbbreviationsMapping.containsKey(var1) ? (String)styleAbbreviationsMapping.get(var1) : var1;
   }

   private boolean isStyleToken(String var1) {
      return styleNameTokes.contains(var1);
   }

   private String psName2FullName(String var1) {
      int var3 = var1.indexOf("-");
      String var2;
      if (var3 >= 0) {
         var2 = this.expandName(var1.substring(0, var3), false);
         var2 = var2 + " " + this.expandName(var1.substring(var3 + 1), true);
      } else {
         var2 = this.expandName(var1, false);
      }

      return var2;
   }

   private String psName2FamilyName(String var1) {
      String var2 = var1;
      if (var1.indexOf("-") > 0) {
         var2 = var1.substring(0, var1.indexOf("-"));
      }

      return this.expandName(var2, false);
   }

   private int nextCapitalLetter(String var1, int var2) {
      while(var2 >= 0 && var2 < var1.length()) {
         if (var1.charAt(var2) >= 'A' && var1.charAt(var2) <= 'Z') {
            return var2;
         }

         ++var2;
      }

      return -1;
   }

   private String expandName(String var1, boolean var2) {
      StringBuffer var3 = new StringBuffer(var1.length() + 10);

      int var5;
      for(int var4 = 0; var4 < var1.length(); var4 = var5) {
         var5 = this.nextCapitalLetter(var1, var4 + 1);
         if (var5 < 0) {
            var5 = var1.length();
         }

         if (var4 != 0) {
            var3.append(" ");
         }

         if (var2) {
            var3.append(this.expandAbbreviation(var1.substring(var4, var5)));
         } else {
            var3.append(var1.substring(var4, var5));
         }
      }

      return var3.toString();
   }

   private byte skip(ByteBuffer var1) {
      byte var2 = var1.get();

      while(var2 == 37) {
         while(true) {
            var2 = var1.get();
            if (var2 == 13 || var2 == 10) {
               break;
            }
         }
      }

      while(var2 <= 32) {
         var2 = var1.get();
      }

      return var2;
   }

   private int nextTokenType(ByteBuffer var1) {
      try {
         byte var2 = this.skip(var1);

         while(var2 != 47) {
            if (var2 == 40) {
               return 2;
            }

            if (var2 != 13 && var2 != 10) {
               var2 = var1.get();
            } else {
               var2 = this.skip(var1);
            }
         }

         return 1;
      } catch (BufferUnderflowException var3) {
         return 0;
      }
   }

   private String getSimpleToken(ByteBuffer var1) {
      while(var1.get() <= 32) {
      }

      int var2 = var1.position() - 1;

      while(var1.get() > 32) {
      }

      int var3 = var1.position();
      byte[] var4 = new byte[var3 - var2 - 1];
      var1.position(var2);
      var1.get(var4);

      try {
         return new String(var4, "US-ASCII");
      } catch (UnsupportedEncodingException var6) {
         return new String(var4);
      }
   }

   private String getString(ByteBuffer var1) {
      int var2 = var1.position();

      while(var1.get() != 41) {
      }

      int var3 = var1.position();
      byte[] var4 = new byte[var3 - var2 - 1];
      var1.position(var2);
      var1.get(var4);

      try {
         return new String(var4, "US-ASCII");
      } catch (UnsupportedEncodingException var6) {
         return new String(var4);
      }
   }

   public String getPostscriptName() {
      return this.psName;
   }

   protected synchronized FontScaler getScaler() {
      if (this.scaler == null) {
         this.scaler = FontScaler.getScaler(this, 0, false, this.fileSize);
      }

      return this.scaler;
   }

   CharToGlyphMapper getMapper() {
      if (this.mapper == null) {
         this.mapper = new Type1GlyphMapper(this);
      }

      return this.mapper;
   }

   public int getNumGlyphs() {
      try {
         return this.getScaler().getNumGlyphs();
      } catch (FontScalerException var2) {
         this.scaler = FontScaler.getNullScaler();
         return this.getNumGlyphs();
      }
   }

   public int getMissingGlyphCode() {
      try {
         return this.getScaler().getMissingGlyphCode();
      } catch (FontScalerException var2) {
         this.scaler = FontScaler.getNullScaler();
         return this.getMissingGlyphCode();
      }
   }

   public int getGlyphCode(char var1) {
      try {
         return this.getScaler().getGlyphCode(var1);
      } catch (FontScalerException var3) {
         this.scaler = FontScaler.getNullScaler();
         return this.getGlyphCode(var1);
      }
   }

   public String toString() {
      return "** Type1 Font: Family=" + this.familyName + " Name=" + this.fullName + " style=" + this.style + " fileName=" + this.getPublicFileName();
   }

   static {
      String[] var0 = new String[]{"Black", "Bold", "Book", "Demi", "Heavy", "Light", "Meduium", "Nord", "Poster", "Regular", "Super", "Thin", "Compressed", "Condensed", "Compact", "Extended", "Narrow", "Inclined", "Italic", "Kursiv", "Oblique", "Upright", "Sloped", "Semi", "Ultra", "Extra", "Alternate", "Alternate", "Deutsche Fraktur", "Expert", "Inline", "Ornaments", "Outline", "Roman", "Rounded", "Script", "Shaded", "Swash", "Titling", "Typewriter"};
      String[] var1 = new String[]{"Blk", "Bd", "Bk", "Dm", "Hv", "Lt", "Md", "Nd", "Po", "Rg", "Su", "Th", "Cm", "Cn", "Ct", "Ex", "Nr", "Ic", "It", "Ks", "Obl", "Up", "Sl", "Sm", "Ult", "X", "A", "Alt", "Dfr", "Exp", "In", "Or", "Ou", "Rm", "Rd", "Scr", "Sh", "Sw", "Ti", "Typ"};
      String[] var2 = new String[]{"Black", "Bold", "Book", "Demi", "Heavy", "Light", "Medium", "Nord", "Poster", "Regular", "Super", "Thin", "Compressed", "Condensed", "Compact", "Extended", "Narrow", "Inclined", "Italic", "Kursiv", "Oblique", "Upright", "Sloped", "Slanted", "Semi", "Ultra", "Extra"};

      int var3;
      for(var3 = 0; var3 < var0.length; ++var3) {
         styleAbbreviationsMapping.put(var1[var3], var0[var3]);
      }

      for(var3 = 0; var3 < var2.length; ++var3) {
         styleNameTokes.add(var2[var3]);
      }

   }

   private static class T1DisposerRecord implements DisposerRecord {
      String fileName = null;

      T1DisposerRecord(String var1) {
         this.fileName = var1;
      }

      public synchronized void dispose() {
         AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
               if (T1DisposerRecord.this.fileName != null) {
                  (new File(T1DisposerRecord.this.fileName)).delete();
               }

               return null;
            }
         });
      }
   }
}
