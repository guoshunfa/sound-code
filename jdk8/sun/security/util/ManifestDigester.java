package sun.security.util;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ManifestDigester {
   public static final String MF_MAIN_ATTRS = "Manifest-Main-Attributes";
   private byte[] rawBytes;
   private HashMap<String, ManifestDigester.Entry> entries;

   private boolean findSection(int var1, ManifestDigester.Position var2) {
      int var3 = var1;
      int var4 = this.rawBytes.length;
      int var5 = var1;
      boolean var7 = true;

      for(var2.endOfFirstLine = -1; var3 < var4; ++var3) {
         byte var8 = this.rawBytes[var3];
         switch(var8) {
         case 13:
            if (var2.endOfFirstLine == -1) {
               var2.endOfFirstLine = var3 - 1;
            }

            if (var3 < var4 && this.rawBytes[var3 + 1] == 10) {
               ++var3;
            }
         case 10:
            if (var2.endOfFirstLine == -1) {
               var2.endOfFirstLine = var3 - 1;
            }

            if (var7 || var3 == var4 - 1) {
               if (var3 == var4 - 1) {
                  var2.endOfSection = var3;
               } else {
                  var2.endOfSection = var5;
               }

               var2.startOfNext = var3 + 1;
               return true;
            }

            var5 = var3;
            var7 = true;
            break;
         default:
            var7 = false;
         }
      }

      return false;
   }

   public ManifestDigester(byte[] var1) {
      this.rawBytes = var1;
      this.entries = new HashMap();
      new ByteArrayOutputStream();
      ManifestDigester.Position var3 = new ManifestDigester.Position();
      if (this.findSection(0, var3)) {
         this.entries.put("Manifest-Main-Attributes", (new ManifestDigester.Entry()).addSection(new ManifestDigester.Section(0, var3.endOfSection + 1, var3.startOfNext, this.rawBytes)));

         for(int var4 = var3.startOfNext; this.findSection(var4, var3); var4 = var3.startOfNext) {
            int var5 = var3.endOfFirstLine - var4 + 1;
            int var6 = var3.endOfSection - var4 + 1;
            int var7 = var3.startOfNext - var4;
            if (var5 > 6 && this.isNameAttr(var1, var4)) {
               StringBuilder var8 = new StringBuilder(var6);

               try {
                  var8.append(new String(var1, var4 + 6, var5 - 6, "UTF8"));
                  int var9 = var4 + var5;
                  if (var9 - var4 < var6) {
                     if (var1[var9] == 13) {
                        var9 += 2;
                     } else {
                        ++var9;
                     }
                  }

                  int var10;
                  int var11;
                  for(; var9 - var4 < var6 && var1[var9++] == 32; var8.append(new String(var1, var10, var11, "UTF8"))) {
                     var10 = var9;

                     while(var9 - var4 < var6 && var1[var9++] != 10) {
                     }

                     if (var1[var9 - 1] != 10) {
                        return;
                     }

                     if (var1[var9 - 2] == 13) {
                        var11 = var9 - var10 - 2;
                     } else {
                        var11 = var9 - var10 - 1;
                     }
                  }

                  ManifestDigester.Entry var13 = (ManifestDigester.Entry)this.entries.get(var8.toString());
                  if (var13 == null) {
                     this.entries.put(var8.toString(), (new ManifestDigester.Entry()).addSection(new ManifestDigester.Section(var4, var6, var7, this.rawBytes)));
                  } else {
                     var13.addSection(new ManifestDigester.Section(var4, var6, var7, this.rawBytes));
                  }
               } catch (UnsupportedEncodingException var12) {
                  throw new IllegalStateException("UTF8 not available on platform");
               }
            }
         }

      }
   }

   private boolean isNameAttr(byte[] var1, int var2) {
      return (var1[var2] == 78 || var1[var2] == 110) && (var1[var2 + 1] == 97 || var1[var2 + 1] == 65) && (var1[var2 + 2] == 109 || var1[var2 + 2] == 77) && (var1[var2 + 3] == 101 || var1[var2 + 3] == 69) && var1[var2 + 4] == 58 && var1[var2 + 5] == 32;
   }

   public ManifestDigester.Entry get(String var1, boolean var2) {
      ManifestDigester.Entry var3 = (ManifestDigester.Entry)this.entries.get(var1);
      if (var3 != null) {
         var3.oldStyle = var2;
      }

      return var3;
   }

   public byte[] manifestDigest(MessageDigest var1) {
      var1.reset();
      var1.update(this.rawBytes, 0, this.rawBytes.length);
      return var1.digest();
   }

   private static class Section {
      int offset;
      int length;
      int lengthWithBlankLine;
      byte[] rawBytes;

      public Section(int var1, int var2, int var3, byte[] var4) {
         this.offset = var1;
         this.length = var2;
         this.lengthWithBlankLine = var3;
         this.rawBytes = var4;
      }

      private static void doOldStyle(MessageDigest var0, byte[] var1, int var2, int var3) {
         int var4 = var2;
         int var5 = var2;
         int var6 = var2 + var3;

         for(byte var7 = -1; var4 < var6; ++var4) {
            if (var1[var4] == 13 && var7 == 32) {
               var0.update(var1, var5, var4 - var5 - 1);
               var5 = var4;
            }

            var7 = var1[var4];
         }

         var0.update(var1, var5, var4 - var5);
      }
   }

   public static class Entry {
      private List<ManifestDigester.Section> sections = new ArrayList();
      boolean oldStyle;

      private ManifestDigester.Entry addSection(ManifestDigester.Section var1) {
         this.sections.add(var1);
         return this;
      }

      public byte[] digest(MessageDigest var1) {
         var1.reset();
         Iterator var2 = this.sections.iterator();

         while(var2.hasNext()) {
            ManifestDigester.Section var3 = (ManifestDigester.Section)var2.next();
            if (this.oldStyle) {
               ManifestDigester.Section.doOldStyle(var1, var3.rawBytes, var3.offset, var3.lengthWithBlankLine);
            } else {
               var1.update(var3.rawBytes, var3.offset, var3.lengthWithBlankLine);
            }
         }

         return var1.digest();
      }

      public byte[] digestWorkaround(MessageDigest var1) {
         var1.reset();
         Iterator var2 = this.sections.iterator();

         while(var2.hasNext()) {
            ManifestDigester.Section var3 = (ManifestDigester.Section)var2.next();
            var1.update(var3.rawBytes, var3.offset, var3.length);
         }

         return var1.digest();
      }
   }

   static class Position {
      int endOfFirstLine;
      int endOfSection;
      int startOfNext;
   }
}
