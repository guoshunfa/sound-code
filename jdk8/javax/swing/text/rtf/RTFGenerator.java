package javax.swing.text.rtf;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.Segment;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.TabStop;

class RTFGenerator {
   Dictionary<Object, Integer> colorTable = new Hashtable();
   int colorCount;
   Dictionary<String, Integer> fontTable;
   int fontCount;
   Dictionary<AttributeSet, Integer> styleTable;
   int styleCount;
   OutputStream outputStream;
   boolean afterKeyword;
   MutableAttributeSet outputAttributes;
   int unicodeCount;
   private Segment workingSegment;
   int[] outputConversion;
   public static final Color defaultRTFColor;
   public static final float defaultFontSize = 12.0F;
   public static final String defaultFontFamily = "Helvetica";
   private static final Object MagicToken;
   protected static RTFGenerator.CharacterKeywordPair[] textKeywords;
   static final char[] hexdigits;

   public static void writeDocument(Document var0, OutputStream var1) throws IOException {
      RTFGenerator var2 = new RTFGenerator(var1);
      Element var3 = var0.getDefaultRootElement();
      var2.examineElement(var3);
      var2.writeRTFHeader();
      var2.writeDocumentProperties(var0);
      int var4 = var3.getElementCount();

      for(int var5 = 0; var5 < var4; ++var5) {
         var2.writeParagraphElement(var3.getElement(var5));
      }

      var2.writeRTFTrailer();
   }

   public RTFGenerator(OutputStream var1) {
      this.colorTable.put(defaultRTFColor, 0);
      this.colorCount = 1;
      this.fontTable = new Hashtable();
      this.fontCount = 0;
      this.styleTable = new Hashtable();
      this.styleCount = 0;
      this.workingSegment = new Segment();
      this.outputStream = var1;
      this.unicodeCount = 1;
   }

   public void examineElement(Element var1) {
      AttributeSet var2 = var1.getAttributes();
      this.tallyStyles(var2);
      if (var2 != null) {
         Color var4 = StyleConstants.getForeground(var2);
         if (var4 != null && this.colorTable.get(var4) == null) {
            this.colorTable.put(var4, new Integer(this.colorCount));
            ++this.colorCount;
         }

         Object var5 = var2.getAttribute(StyleConstants.Background);
         if (var5 != null && this.colorTable.get(var5) == null) {
            this.colorTable.put(var5, new Integer(this.colorCount));
            ++this.colorCount;
         }

         String var3 = StyleConstants.getFontFamily(var2);
         if (var3 == null) {
            var3 = "Helvetica";
         }

         if (var3 != null && this.fontTable.get(var3) == null) {
            this.fontTable.put(var3, new Integer(this.fontCount));
            ++this.fontCount;
         }
      }

      int var6 = var1.getElementCount();

      for(int var7 = 0; var7 < var6; ++var7) {
         this.examineElement(var1.getElement(var7));
      }

   }

   private void tallyStyles(AttributeSet var1) {
      for(; var1 != null; var1 = var1.getResolveParent()) {
         if (var1 instanceof Style) {
            Integer var2 = (Integer)this.styleTable.get(var1);
            if (var2 == null) {
               ++this.styleCount;
               var2 = new Integer(this.styleCount);
               this.styleTable.put(var1, var2);
            }
         }
      }

   }

   private Style findStyle(AttributeSet var1) {
      for(; var1 != null; var1 = var1.getResolveParent()) {
         if (var1 instanceof Style) {
            Object var2 = this.styleTable.get(var1);
            if (var2 != null) {
               return (Style)var1;
            }
         }
      }

      return null;
   }

   private Integer findStyleNumber(AttributeSet var1, String var2) {
      for(; var1 != null; var1 = var1.getResolveParent()) {
         if (var1 instanceof Style) {
            Integer var3 = (Integer)this.styleTable.get(var1);
            if (var3 != null && (var2 == null || var2.equals(var1.getAttribute("style:type")))) {
               return var3;
            }
         }
      }

      return null;
   }

   private static Object attrDiff(MutableAttributeSet var0, AttributeSet var1, Object var2, Object var3) {
      Object var4 = var0.getAttribute(var2);
      Object var5 = var1.getAttribute(var2);
      if (var5 == var4) {
         return null;
      } else if (var5 == null) {
         var0.removeAttribute(var2);
         return var3 != null && !var3.equals(var4) ? var3 : null;
      } else if (var4 != null && equalArraysOK(var4, var5)) {
         return null;
      } else {
         var0.addAttribute(var2, var5);
         return var5;
      }
   }

   private static boolean equalArraysOK(Object var0, Object var1) {
      if (var0 == var1) {
         return true;
      } else if (var0 != null && var1 != null) {
         if (var0.equals(var1)) {
            return true;
         } else if (var0.getClass().isArray() && var1.getClass().isArray()) {
            Object[] var2 = (Object[])((Object[])var0);
            Object[] var3 = (Object[])((Object[])var1);
            if (var2.length != var3.length) {
               return false;
            } else {
               int var5 = var2.length;

               for(int var4 = 0; var4 < var5; ++var4) {
                  if (!equalArraysOK(var2[var4], var3[var4])) {
                     return false;
                  }
               }

               return true;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public void writeLineBreak() throws IOException {
      this.writeRawString("\n");
      this.afterKeyword = false;
   }

   public void writeRTFHeader() throws IOException {
      this.writeBegingroup();
      this.writeControlWord("rtf", 1);
      this.writeControlWord("ansi");
      this.outputConversion = outputConversionForName("ansi");
      this.writeLineBreak();
      String[] var2 = new String[this.fontCount];

      String var4;
      Integer var5;
      for(Enumeration var3 = this.fontTable.keys(); var3.hasMoreElements(); var2[var5] = var4) {
         var4 = (String)var3.nextElement();
         var5 = (Integer)this.fontTable.get(var4);
      }

      this.writeBegingroup();
      this.writeControlWord("fonttbl");

      int var1;
      for(var1 = 0; var1 < this.fontCount; ++var1) {
         this.writeControlWord("f", var1);
         this.writeControlWord("fnil");
         this.writeText(var2[var1]);
         this.writeText(";");
      }

      this.writeEndgroup();
      this.writeLineBreak();
      if (this.colorCount > 1) {
         Color[] var14 = new Color[this.colorCount];

         Color var7;
         Integer var8;
         for(Enumeration var6 = this.colorTable.keys(); var6.hasMoreElements(); var14[var8] = var7) {
            var7 = (Color)var6.nextElement();
            var8 = (Integer)this.colorTable.get(var7);
         }

         this.writeBegingroup();
         this.writeControlWord("colortbl");

         for(var1 = 0; var1 < this.colorCount; ++var1) {
            var7 = var14[var1];
            if (var7 != null) {
               this.writeControlWord("red", var7.getRed());
               this.writeControlWord("green", var7.getGreen());
               this.writeControlWord("blue", var7.getBlue());
            }

            this.writeRawString(";");
         }

         this.writeEndgroup();
         this.writeLineBreak();
      }

      if (this.styleCount > 1) {
         this.writeBegingroup();
         this.writeControlWord("stylesheet");
         Enumeration var15 = this.styleTable.keys();

         while(var15.hasMoreElements()) {
            Style var16 = (Style)var15.nextElement();
            int var17 = (Integer)this.styleTable.get(var16);
            this.writeBegingroup();
            String var18 = (String)var16.getAttribute("style:type");
            if (var18 == null) {
               var18 = "paragraph";
            }

            if (var18.equals("character")) {
               this.writeControlWord("*");
               this.writeControlWord("cs", var17);
            } else if (var18.equals("section")) {
               this.writeControlWord("*");
               this.writeControlWord("ds", var17);
            } else {
               this.writeControlWord("s", var17);
            }

            AttributeSet var9 = var16.getResolveParent();
            SimpleAttributeSet var10;
            if (var9 == null) {
               var10 = new SimpleAttributeSet();
            } else {
               var10 = new SimpleAttributeSet(var9);
            }

            this.updateSectionAttributes(var10, var16, false);
            this.updateParagraphAttributes(var10, var16, false);
            this.updateCharacterAttributes(var10, var16, false);
            var9 = var16.getResolveParent();
            if (var9 != null && var9 instanceof Style) {
               Integer var11 = (Integer)this.styleTable.get(var9);
               if (var11 != null) {
                  this.writeControlWord("sbasedon", var11);
               }
            }

            Style var19 = (Style)var16.getAttribute("style:nextStyle");
            if (var19 != null) {
               Integer var12 = (Integer)this.styleTable.get(var19);
               if (var12 != null) {
                  this.writeControlWord("snext", var12);
               }
            }

            Boolean var20 = (Boolean)var16.getAttribute("style:hidden");
            if (var20 != null && var20) {
               this.writeControlWord("shidden");
            }

            Boolean var13 = (Boolean)var16.getAttribute("style:additive");
            if (var13 != null && var13) {
               this.writeControlWord("additive");
            }

            this.writeText(var16.getName());
            this.writeText(";");
            this.writeEndgroup();
         }

         this.writeEndgroup();
         this.writeLineBreak();
      }

      this.outputAttributes = new SimpleAttributeSet();
   }

   void writeDocumentProperties(Document var1) throws IOException {
      boolean var3 = false;

      for(int var2 = 0; var2 < RTFAttributes.attributes.length; ++var2) {
         RTFAttribute var4 = RTFAttributes.attributes[var2];
         if (var4.domain() == 3) {
            Object var5 = var1.getProperty(var4.swingName());
            boolean var6 = var4.writeValue(var5, this, false);
            if (var6) {
               var3 = true;
            }
         }
      }

      if (var3) {
         this.writeLineBreak();
      }

   }

   public void writeRTFTrailer() throws IOException {
      this.writeEndgroup();
      this.writeLineBreak();
   }

   protected void checkNumericControlWord(MutableAttributeSet var1, AttributeSet var2, Object var3, String var4, float var5, float var6) throws IOException {
      Object var7;
      if ((var7 = attrDiff(var1, var2, var3, MagicToken)) != null) {
         float var8;
         if (var7 == MagicToken) {
            var8 = var5;
         } else {
            var8 = ((Number)var7).floatValue();
         }

         this.writeControlWord(var4, Math.round(var8 * var6));
      }

   }

   protected void checkControlWord(MutableAttributeSet var1, AttributeSet var2, RTFAttribute var3) throws IOException {
      Object var4;
      if ((var4 = attrDiff(var1, var2, var3.swingName(), MagicToken)) != null) {
         if (var4 == MagicToken) {
            var4 = null;
         }

         var3.writeValue(var4, this, true);
      }

   }

   protected void checkControlWords(MutableAttributeSet var1, AttributeSet var2, RTFAttribute[] var3, int var4) throws IOException {
      int var6 = var3.length;

      for(int var5 = 0; var5 < var6; ++var5) {
         RTFAttribute var7 = var3[var5];
         if (var7.domain() == var4) {
            this.checkControlWord(var1, var2, var7);
         }
      }

   }

   void updateSectionAttributes(MutableAttributeSet var1, AttributeSet var2, boolean var3) throws IOException {
      if (var3) {
         Object var4 = var1.getAttribute("sectionStyle");
         Integer var5 = this.findStyleNumber(var2, "section");
         if (var4 != var5) {
            if (var4 != null) {
               this.resetSectionAttributes(var1);
            }

            if (var5 != null) {
               this.writeControlWord("ds", (Integer)var5);
               var1.addAttribute("sectionStyle", var5);
            } else {
               var1.removeAttribute("sectionStyle");
            }
         }
      }

      this.checkControlWords(var1, var2, RTFAttributes.attributes, 2);
   }

   protected void resetSectionAttributes(MutableAttributeSet var1) throws IOException {
      this.writeControlWord("sectd");
      int var3 = RTFAttributes.attributes.length;

      for(int var2 = 0; var2 < var3; ++var2) {
         RTFAttribute var4 = RTFAttributes.attributes[var2];
         if (var4.domain() == 2) {
            var4.setDefault(var1);
         }
      }

      var1.removeAttribute("sectionStyle");
   }

   void updateParagraphAttributes(MutableAttributeSet var1, AttributeSet var2, boolean var3) throws IOException {
      Object var5;
      Integer var6;
      if (var3) {
         var5 = var1.getAttribute("paragraphStyle");
         var6 = this.findStyleNumber(var2, "paragraph");
         if (var5 != var6 && var5 != null) {
            this.resetParagraphAttributes(var1);
            var5 = null;
         }
      } else {
         var5 = null;
         var6 = null;
      }

      Object var7 = var1.getAttribute("tabs");
      Object var8 = var2.getAttribute("tabs");
      if (var7 != var8 && var7 != null) {
         this.resetParagraphAttributes(var1);
         var7 = null;
         var5 = null;
      }

      if (var5 != var6 && var6 != null) {
         this.writeControlWord("s", (Integer)var6);
         var1.addAttribute("paragraphStyle", var6);
      }

      this.checkControlWords(var1, var2, RTFAttributes.attributes, 1);
      if (var7 != var8 && var8 != null) {
         TabStop[] var9 = (TabStop[])((TabStop[])var8);

         for(int var10 = 0; var10 < var9.length; ++var10) {
            TabStop var11 = var9[var10];
            switch(var11.getAlignment()) {
            case 0:
            case 3:
            case 5:
            default:
               break;
            case 1:
               this.writeControlWord("tqr");
               break;
            case 2:
               this.writeControlWord("tqc");
               break;
            case 4:
               this.writeControlWord("tqdec");
            }

            switch(var11.getLeader()) {
            case 0:
            default:
               break;
            case 1:
               this.writeControlWord("tldot");
               break;
            case 2:
               this.writeControlWord("tlhyph");
               break;
            case 3:
               this.writeControlWord("tlul");
               break;
            case 4:
               this.writeControlWord("tlth");
               break;
            case 5:
               this.writeControlWord("tleq");
            }

            int var12 = Math.round(20.0F * var11.getPosition());
            if (var11.getAlignment() == 5) {
               this.writeControlWord("tb", var12);
            } else {
               this.writeControlWord("tx", var12);
            }
         }

         var1.addAttribute("tabs", var9);
      }

   }

   public void writeParagraphElement(Element var1) throws IOException {
      this.updateParagraphAttributes(this.outputAttributes, var1.getAttributes(), true);
      int var2 = var1.getElementCount();

      for(int var3 = 0; var3 < var2; ++var3) {
         this.writeTextElement(var1.getElement(var3));
      }

      this.writeControlWord("par");
      this.writeLineBreak();
   }

   protected void resetParagraphAttributes(MutableAttributeSet var1) throws IOException {
      this.writeControlWord("pard");
      var1.addAttribute(StyleConstants.Alignment, 0);
      int var3 = RTFAttributes.attributes.length;

      for(int var2 = 0; var2 < var3; ++var2) {
         RTFAttribute var4 = RTFAttributes.attributes[var2];
         if (var4.domain() == 1) {
            var4.setDefault(var1);
         }
      }

      var1.removeAttribute("paragraphStyle");
      var1.removeAttribute("tabs");
   }

   void updateCharacterAttributes(MutableAttributeSet var1, AttributeSet var2, boolean var3) throws IOException {
      if (var3) {
         Object var5 = var1.getAttribute("characterStyle");
         Integer var6 = this.findStyleNumber(var2, "character");
         if (var5 != var6) {
            if (var5 != null) {
               this.resetCharacterAttributes(var1);
            }

            if (var6 != null) {
               this.writeControlWord("cs", (Integer)var6);
               var1.addAttribute("characterStyle", var6);
            } else {
               var1.removeAttribute("characterStyle");
            }
         }
      }

      Object var4;
      if ((var4 = attrDiff(var1, var2, StyleConstants.FontFamily, (Object)null)) != null) {
         Integer var7 = (Integer)this.fontTable.get(var4);
         this.writeControlWord("f", var7);
      }

      this.checkNumericControlWord(var1, var2, StyleConstants.FontSize, "fs", 12.0F, 2.0F);
      this.checkControlWords(var1, var2, RTFAttributes.attributes, 0);
      this.checkNumericControlWord(var1, var2, StyleConstants.LineSpacing, "sl", 0.0F, 20.0F);
      int var8;
      if ((var4 = attrDiff(var1, var2, StyleConstants.Background, MagicToken)) != null) {
         if (var4 == MagicToken) {
            var8 = 0;
         } else {
            var8 = (Integer)this.colorTable.get(var4);
         }

         this.writeControlWord("cb", var8);
      }

      if ((var4 = attrDiff(var1, var2, StyleConstants.Foreground, (Object)null)) != null) {
         if (var4 == MagicToken) {
            var8 = 0;
         } else {
            var8 = (Integer)this.colorTable.get(var4);
         }

         this.writeControlWord("cf", var8);
      }

   }

   protected void resetCharacterAttributes(MutableAttributeSet var1) throws IOException {
      this.writeControlWord("plain");
      int var3 = RTFAttributes.attributes.length;

      for(int var2 = 0; var2 < var3; ++var2) {
         RTFAttribute var4 = RTFAttributes.attributes[var2];
         if (var4.domain() == 0) {
            var4.setDefault(var1);
         }
      }

      StyleConstants.setFontFamily(var1, "Helvetica");
      var1.removeAttribute(StyleConstants.FontSize);
      var1.removeAttribute(StyleConstants.Background);
      var1.removeAttribute(StyleConstants.Foreground);
      var1.removeAttribute(StyleConstants.LineSpacing);
      var1.removeAttribute("characterStyle");
   }

   public void writeTextElement(Element var1) throws IOException {
      this.updateCharacterAttributes(this.outputAttributes, var1.getAttributes(), true);
      if (var1.isLeaf()) {
         try {
            var1.getDocument().getText(var1.getStartOffset(), var1.getEndOffset() - var1.getStartOffset(), this.workingSegment);
         } catch (BadLocationException var4) {
            var4.printStackTrace();
            throw new InternalError(var4.getMessage());
         }

         this.writeText(this.workingSegment);
      } else {
         int var2 = var1.getElementCount();

         for(int var3 = 0; var3 < var2; ++var3) {
            this.writeTextElement(var1.getElement(var3));
         }
      }

   }

   public void writeText(Segment var1) throws IOException {
      int var2 = var1.offset;
      int var3 = var2 + var1.count;

      for(char[] var4 = var1.array; var2 < var3; ++var2) {
         this.writeCharacter(var4[var2]);
      }

   }

   public void writeText(String var1) throws IOException {
      int var2 = 0;

      for(int var3 = var1.length(); var2 < var3; ++var2) {
         this.writeCharacter(var1.charAt(var2));
      }

   }

   public void writeRawString(String var1) throws IOException {
      int var2 = var1.length();

      for(int var3 = 0; var3 < var2; ++var3) {
         this.outputStream.write(var1.charAt(var3));
      }

   }

   public void writeControlWord(String var1) throws IOException {
      this.outputStream.write(92);
      this.writeRawString(var1);
      this.afterKeyword = true;
   }

   public void writeControlWord(String var1, int var2) throws IOException {
      this.outputStream.write(92);
      this.writeRawString(var1);
      this.writeRawString(String.valueOf(var2));
      this.afterKeyword = true;
   }

   public void writeBegingroup() throws IOException {
      this.outputStream.write(123);
      this.afterKeyword = false;
   }

   public void writeEndgroup() throws IOException {
      this.outputStream.write(125);
      this.afterKeyword = false;
   }

   public void writeCharacter(char var1) throws IOException {
      if (var1 == 160) {
         this.outputStream.write(92);
         this.outputStream.write(126);
         this.afterKeyword = false;
      } else if (var1 == '\t') {
         this.writeControlWord("tab");
      } else if (var1 != '\n' && var1 != '\r') {
         int var2 = convertCharacter(this.outputConversion, var1);
         int var3;
         if (var2 == 0) {
            for(var3 = 0; var3 < textKeywords.length; ++var3) {
               if (textKeywords[var3].character == var1) {
                  this.writeControlWord(textKeywords[var3].keyword);
                  return;
               }
            }

            String var4 = this.approximationForUnicode(var1);
            if (var4.length() != this.unicodeCount) {
               this.unicodeCount = var4.length();
               this.writeControlWord("uc", this.unicodeCount);
            }

            this.writeControlWord("u", var1);
            this.writeRawString(" ");
            this.writeRawString(var4);
            this.afterKeyword = false;
         } else if (var2 > 127) {
            this.outputStream.write(92);
            this.outputStream.write(39);
            var3 = (var2 & 240) >>> 4;
            this.outputStream.write(hexdigits[var3]);
            var3 = var2 & 15;
            this.outputStream.write(hexdigits[var3]);
            this.afterKeyword = false;
         } else {
            switch(var2) {
            case 92:
            case 123:
            case 125:
               this.outputStream.write(92);
               this.afterKeyword = false;
            default:
               if (this.afterKeyword) {
                  this.outputStream.write(32);
                  this.afterKeyword = false;
               }

               this.outputStream.write(var2);
            }
         }
      }
   }

   String approximationForUnicode(char var1) {
      return "?";
   }

   static int[] outputConversionFromTranslationTable(char[] var0) {
      int[] var1 = new int[2 * var0.length];

      for(int var2 = 0; var2 < var0.length; var1[var2 * 2 + 1] = var2++) {
         var1[var2 * 2] = var0[var2];
      }

      return var1;
   }

   static int[] outputConversionForName(String var0) throws IOException {
      char[] var1 = (char[])((char[])RTFReader.getCharacterSet(var0));
      return outputConversionFromTranslationTable(var1);
   }

   protected static int convertCharacter(int[] var0, char var1) {
      for(int var2 = 0; var2 < var0.length; var2 += 2) {
         if (var0[var2] == var1) {
            return var0[var2 + 1];
         }
      }

      return 0;
   }

   static {
      defaultRTFColor = Color.black;
      MagicToken = new Object();
      Dictionary var0 = RTFReader.textKeywords;
      Enumeration var1 = var0.keys();
      Vector var2 = new Vector();

      while(var1.hasMoreElements()) {
         RTFGenerator.CharacterKeywordPair var3 = new RTFGenerator.CharacterKeywordPair();
         var3.keyword = (String)var1.nextElement();
         var3.character = ((String)var0.get(var3.keyword)).charAt(0);
         var2.addElement(var3);
      }

      textKeywords = new RTFGenerator.CharacterKeywordPair[var2.size()];
      var2.copyInto(textKeywords);
      hexdigits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
   }

   static class CharacterKeywordPair {
      public char character;
      public String keyword;
   }
}
