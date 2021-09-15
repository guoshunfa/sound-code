package javax.swing.text.rtf;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.TabStop;

class RTFReader extends RTFParser {
   StyledDocument target;
   Dictionary<Object, Object> parserState;
   RTFReader.Destination rtfDestination;
   MutableAttributeSet documentAttributes;
   Dictionary<Integer, String> fontTable;
   Color[] colorTable;
   Style[] characterStyles;
   Style[] paragraphStyles;
   Style[] sectionStyles;
   int rtfversion;
   boolean ignoreGroupIfUnknownKeyword;
   int skippingCharacters;
   private static Dictionary<String, RTFAttribute> straightforwardAttributes = RTFAttributes.attributesByKeyword();
   private MockAttributeSet mockery;
   static Dictionary<String, String> textKeywords = null;
   static final String TabAlignmentKey = "tab_alignment";
   static final String TabLeaderKey = "tab_leader";
   static Dictionary<String, char[]> characterSets;
   static boolean useNeXTForAnsi;

   public RTFReader(StyledDocument var1) {
      this.target = var1;
      this.parserState = new Hashtable();
      this.fontTable = new Hashtable();
      this.rtfversion = -1;
      this.mockery = new MockAttributeSet();
      this.documentAttributes = new SimpleAttributeSet();
   }

   public void handleBinaryBlob(byte[] var1) {
      if (this.skippingCharacters > 0) {
         --this.skippingCharacters;
      }
   }

   public void handleText(String var1) {
      if (this.skippingCharacters > 0) {
         if (this.skippingCharacters >= var1.length()) {
            this.skippingCharacters -= var1.length();
            return;
         }

         var1 = var1.substring(this.skippingCharacters);
         this.skippingCharacters = 0;
      }

      if (this.rtfDestination != null) {
         this.rtfDestination.handleText(var1);
      } else {
         this.warning("Text with no destination. oops.");
      }
   }

   Color defaultColor() {
      return Color.black;
   }

   public void begingroup() {
      if (this.skippingCharacters > 0) {
         this.skippingCharacters = 0;
      }

      Object var1 = this.parserState.get("_savedState");
      if (var1 != null) {
         this.parserState.remove("_savedState");
      }

      Dictionary var2 = (Dictionary)((Hashtable)this.parserState).clone();
      if (var1 != null) {
         var2.put("_savedState", var1);
      }

      this.parserState.put("_savedState", var2);
      if (this.rtfDestination != null) {
         this.rtfDestination.begingroup();
      }

   }

   public void endgroup() {
      if (this.skippingCharacters > 0) {
         this.skippingCharacters = 0;
      }

      Dictionary var1 = (Dictionary)this.parserState.get("_savedState");
      RTFReader.Destination var2 = (RTFReader.Destination)var1.get("dst");
      if (var2 != this.rtfDestination) {
         this.rtfDestination.close();
         this.rtfDestination = var2;
      }

      Dictionary var3 = this.parserState;
      this.parserState = var1;
      if (this.rtfDestination != null) {
         this.rtfDestination.endgroup(var3);
      }

   }

   protected void setRTFDestination(RTFReader.Destination var1) {
      Dictionary var2 = (Dictionary)this.parserState.get("_savedState");
      if (var2 != null && this.rtfDestination != var2.get("dst")) {
         this.warning("Warning, RTF destination overridden, invalid RTF.");
         this.rtfDestination.close();
      }

      this.rtfDestination = var1;
      this.parserState.put("dst", this.rtfDestination);
   }

   public void close() throws IOException {
      Enumeration var1 = this.documentAttributes.getAttributeNames();

      while(var1.hasMoreElements()) {
         Object var2 = var1.nextElement();
         this.target.putProperty(var2, this.documentAttributes.getAttribute(var2));
      }

      this.warning("RTF filter done.");
      super.close();
   }

   public boolean handleKeyword(String var1) {
      boolean var3 = this.ignoreGroupIfUnknownKeyword;
      if (this.skippingCharacters > 0) {
         --this.skippingCharacters;
         return true;
      } else {
         this.ignoreGroupIfUnknownKeyword = false;
         String var2;
         if ((var2 = (String)textKeywords.get(var1)) != null) {
            this.handleText(var2);
            return true;
         } else if (var1.equals("fonttbl")) {
            this.setRTFDestination(new RTFReader.FonttblDestination());
            return true;
         } else if (var1.equals("colortbl")) {
            this.setRTFDestination(new RTFReader.ColortblDestination());
            return true;
         } else if (var1.equals("stylesheet")) {
            this.setRTFDestination(new RTFReader.StylesheetDestination());
            return true;
         } else if (var1.equals("info")) {
            this.setRTFDestination(new RTFReader.InfoDestination());
            return false;
         } else if (var1.equals("mac")) {
            this.setCharacterSet("mac");
            return true;
         } else if (var1.equals("ansi")) {
            if (useNeXTForAnsi) {
               this.setCharacterSet("NeXT");
            } else {
               this.setCharacterSet("ansi");
            }

            return true;
         } else if (var1.equals("next")) {
            this.setCharacterSet("NeXT");
            return true;
         } else if (var1.equals("pc")) {
            this.setCharacterSet("cpg437");
            return true;
         } else if (var1.equals("pca")) {
            this.setCharacterSet("cpg850");
            return true;
         } else if (var1.equals("*")) {
            this.ignoreGroupIfUnknownKeyword = true;
            return true;
         } else if (this.rtfDestination != null && this.rtfDestination.handleKeyword(var1)) {
            return true;
         } else {
            if (var1.equals("aftncn") || var1.equals("aftnsep") || var1.equals("aftnsepc") || var1.equals("annotation") || var1.equals("atnauthor") || var1.equals("atnicn") || var1.equals("atnid") || var1.equals("atnref") || var1.equals("atntime") || var1.equals("atrfend") || var1.equals("atrfstart") || var1.equals("bkmkend") || var1.equals("bkmkstart") || var1.equals("datafield") || var1.equals("do") || var1.equals("dptxbxtext") || var1.equals("falt") || var1.equals("field") || var1.equals("file") || var1.equals("filetbl") || var1.equals("fname") || var1.equals("fontemb") || var1.equals("fontfile") || var1.equals("footer") || var1.equals("footerf") || var1.equals("footerl") || var1.equals("footerr") || var1.equals("footnote") || var1.equals("ftncn") || var1.equals("ftnsep") || var1.equals("ftnsepc") || var1.equals("header") || var1.equals("headerf") || var1.equals("headerl") || var1.equals("headerr") || var1.equals("keycode") || var1.equals("nextfile") || var1.equals("object") || var1.equals("pict") || var1.equals("pn") || var1.equals("pnseclvl") || var1.equals("pntxtb") || var1.equals("pntxta") || var1.equals("revtbl") || var1.equals("rxe") || var1.equals("tc") || var1.equals("template") || var1.equals("txe") || var1.equals("xe")) {
               var3 = true;
            }

            if (var3) {
               this.setRTFDestination(new RTFReader.DiscardingDestination());
            }

            return false;
         }
      }
   }

   public boolean handleKeyword(String var1, int var2) {
      boolean var3 = this.ignoreGroupIfUnknownKeyword;
      if (this.skippingCharacters > 0) {
         --this.skippingCharacters;
         return true;
      } else {
         this.ignoreGroupIfUnknownKeyword = false;
         if (var1.equals("uc")) {
            this.parserState.put("UnicodeSkip", var2);
            return true;
         } else if (var1.equals("u")) {
            if (var2 < 0) {
               var2 += 65536;
            }

            this.handleText((char)var2);
            Number var4 = (Number)((Number)this.parserState.get("UnicodeSkip"));
            if (var4 != null) {
               this.skippingCharacters = var4.intValue();
            } else {
               this.skippingCharacters = 1;
            }

            return true;
         } else if (var1.equals("rtf")) {
            this.rtfversion = var2;
            this.setRTFDestination(new RTFReader.DocumentDestination());
            return true;
         } else {
            if (var1.startsWith("NeXT") || var1.equals("private")) {
               var3 = true;
            }

            if (this.rtfDestination != null && this.rtfDestination.handleKeyword(var1, var2)) {
               return true;
            } else {
               if (var3) {
                  this.setRTFDestination(new RTFReader.DiscardingDestination());
               }

               return false;
            }
         }
      }
   }

   private void setTargetAttribute(String var1, Object var2) {
   }

   public void setCharacterSet(String var1) {
      Object var2;
      try {
         var2 = getCharacterSet(var1);
      } catch (Exception var5) {
         this.warning("Exception loading RTF character set \"" + var1 + "\": " + var5);
         var2 = null;
      }

      if (var2 != null) {
         this.translationTable = (char[])((char[])var2);
      } else {
         this.warning("Unknown RTF character set \"" + var1 + "\"");
         if (!var1.equals("ansi")) {
            try {
               this.translationTable = (char[])((char[])getCharacterSet("ansi"));
            } catch (IOException var4) {
               throw new InternalError("RTFReader: Unable to find character set resources (" + var4 + ")", var4);
            }
         }
      }

      this.setTargetAttribute("rtfCharacterSet", var1);
   }

   public static void defineCharacterSet(String var0, char[] var1) {
      if (var1.length < 256) {
         throw new IllegalArgumentException("Translation table must have 256 entries.");
      } else {
         characterSets.put(var0, var1);
      }
   }

   public static Object getCharacterSet(final String var0) throws IOException {
      char[] var1 = (char[])characterSets.get(var0);
      if (var1 == null) {
         InputStream var2 = (InputStream)AccessController.doPrivileged(new PrivilegedAction<InputStream>() {
            public InputStream run() {
               return RTFReader.class.getResourceAsStream("charsets/" + var0 + ".txt");
            }
         });
         var1 = readCharset(var2);
         defineCharacterSet(var0, var1);
      }

      return var1;
   }

   static char[] readCharset(InputStream var0) throws IOException {
      char[] var1 = new char[256];
      StreamTokenizer var3 = new StreamTokenizer(new BufferedReader(new InputStreamReader(var0, "ISO-8859-1")));
      var3.eolIsSignificant(false);
      var3.commentChar(35);
      var3.slashSlashComments(true);
      var3.slashStarComments(true);

      for(int var2 = 0; var2 < 256; ++var2) {
         int var4;
         try {
            var4 = var3.nextToken();
         } catch (Exception var6) {
            throw new IOException("Unable to read from character set file (" + var6 + ")");
         }

         if (var4 != -2) {
            throw new IOException("Unexpected token in character set file");
         }

         var1[var2] = (char)((int)var3.nval);
      }

      return var1;
   }

   static char[] readCharset(URL var0) throws IOException {
      return readCharset(var0.openStream());
   }

   static {
      textKeywords = new Hashtable();
      textKeywords.put("\\", "\\");
      textKeywords.put("{", "{");
      textKeywords.put("}", "}");
      textKeywords.put(" ", " ");
      textKeywords.put("~", " ");
      textKeywords.put("_", "‑");
      textKeywords.put("bullet", "•");
      textKeywords.put("emdash", "—");
      textKeywords.put("emspace", " ");
      textKeywords.put("endash", "–");
      textKeywords.put("enspace", " ");
      textKeywords.put("ldblquote", "“");
      textKeywords.put("lquote", "‘");
      textKeywords.put("ltrmark", "\u200e");
      textKeywords.put("rdblquote", "”");
      textKeywords.put("rquote", "’");
      textKeywords.put("rtlmark", "\u200f");
      textKeywords.put("tab", "\t");
      textKeywords.put("zwj", "\u200d");
      textKeywords.put("zwnj", "\u200c");
      textKeywords.put("-", "‧");
      useNeXTForAnsi = false;
      characterSets = new Hashtable();
   }

   class DocumentDestination extends RTFReader.TextHandlingDestination implements RTFReader.Destination {
      DocumentDestination() {
         super();
      }

      public void deliverText(String var1, AttributeSet var2) {
         try {
            RTFReader.this.target.insertString(RTFReader.this.target.getLength(), var1, this.currentTextAttributes());
         } catch (BadLocationException var4) {
            throw new InternalError(var4.getMessage(), var4);
         }
      }

      public void finishParagraph(AttributeSet var1, AttributeSet var2) {
         int var3 = RTFReader.this.target.getLength();

         try {
            RTFReader.this.target.insertString(var3, "\n", var2);
            RTFReader.this.target.setParagraphAttributes(var3, 1, var1, true);
         } catch (BadLocationException var5) {
            throw new InternalError(var5.getMessage(), var5);
         }
      }

      public void endSection() {
      }
   }

   abstract class TextHandlingDestination extends RTFReader.AttributeTrackingDestination implements RTFReader.Destination {
      boolean inParagraph = false;

      public TextHandlingDestination() {
         super();
      }

      public void handleText(String var1) {
         if (!this.inParagraph) {
            this.beginParagraph();
         }

         this.deliverText(var1, this.currentTextAttributes());
      }

      abstract void deliverText(String var1, AttributeSet var2);

      public void close() {
         if (this.inParagraph) {
            this.endParagraph();
         }

         super.close();
      }

      public boolean handleKeyword(String var1) {
         if (var1.equals("\r") || var1.equals("\n")) {
            var1 = "par";
         }

         if (var1.equals("par")) {
            this.endParagraph();
            return true;
         } else if (var1.equals("sect")) {
            this.endSection();
            return true;
         } else {
            return super.handleKeyword(var1);
         }
      }

      protected void beginParagraph() {
         this.inParagraph = true;
      }

      protected void endParagraph() {
         MutableAttributeSet var1 = this.currentParagraphAttributes();
         MutableAttributeSet var2 = this.currentTextAttributes();
         this.finishParagraph(var1, var2);
         this.inParagraph = false;
      }

      abstract void finishParagraph(AttributeSet var1, AttributeSet var2);

      abstract void endSection();
   }

   abstract class AttributeTrackingDestination implements RTFReader.Destination {
      MutableAttributeSet characterAttributes = this.rootCharacterAttributes();
      MutableAttributeSet paragraphAttributes;
      MutableAttributeSet sectionAttributes;

      public AttributeTrackingDestination() {
         RTFReader.this.parserState.put("chr", this.characterAttributes);
         this.paragraphAttributes = this.rootParagraphAttributes();
         RTFReader.this.parserState.put("pgf", this.paragraphAttributes);
         this.sectionAttributes = this.rootSectionAttributes();
         RTFReader.this.parserState.put("sec", this.sectionAttributes);
      }

      public abstract void handleText(String var1);

      public void handleBinaryBlob(byte[] var1) {
         RTFReader.this.warning("Unexpected binary data in RTF file.");
      }

      public void begingroup() {
         MutableAttributeSet var1 = this.currentTextAttributes();
         MutableAttributeSet var2 = this.currentParagraphAttributes();
         AttributeSet var3 = this.currentSectionAttributes();
         this.characterAttributes = new SimpleAttributeSet();
         this.characterAttributes.addAttributes(var1);
         RTFReader.this.parserState.put("chr", this.characterAttributes);
         this.paragraphAttributes = new SimpleAttributeSet();
         this.paragraphAttributes.addAttributes(var2);
         RTFReader.this.parserState.put("pgf", this.paragraphAttributes);
         this.sectionAttributes = new SimpleAttributeSet();
         this.sectionAttributes.addAttributes(var3);
         RTFReader.this.parserState.put("sec", this.sectionAttributes);
      }

      public void endgroup(Dictionary var1) {
         this.characterAttributes = (MutableAttributeSet)RTFReader.this.parserState.get("chr");
         this.paragraphAttributes = (MutableAttributeSet)RTFReader.this.parserState.get("pgf");
         this.sectionAttributes = (MutableAttributeSet)RTFReader.this.parserState.get("sec");
      }

      public void close() {
      }

      public boolean handleKeyword(String var1) {
         if (var1.equals("ulnone")) {
            return this.handleKeyword("ul", 0);
         } else {
            RTFAttribute var2 = (RTFAttribute)RTFReader.straightforwardAttributes.get(var1);
            if (var2 != null) {
               boolean var3;
               switch(var2.domain()) {
               case 0:
                  var3 = var2.set(this.characterAttributes);
                  break;
               case 1:
                  var3 = var2.set(this.paragraphAttributes);
                  break;
               case 2:
                  var3 = var2.set(this.sectionAttributes);
                  break;
               case 3:
                  var3 = var2.set(RTFReader.this.documentAttributes);
                  break;
               case 4:
                  RTFReader.this.mockery.backing = RTFReader.this.parserState;
                  var3 = var2.set(RTFReader.this.mockery);
                  RTFReader.this.mockery.backing = null;
                  break;
               default:
                  var3 = false;
               }

               if (var3) {
                  return true;
               }
            }

            if (var1.equals("plain")) {
               this.resetCharacterAttributes();
               return true;
            } else if (var1.equals("pard")) {
               this.resetParagraphAttributes();
               return true;
            } else if (var1.equals("sectd")) {
               this.resetSectionAttributes();
               return true;
            } else {
               return false;
            }
         }
      }

      public boolean handleKeyword(String var1, int var2) {
         boolean var3 = var2 != 0;
         if (var1.equals("fc")) {
            var1 = "cf";
         }

         if (var1.equals("f")) {
            RTFReader.this.parserState.put(var1, var2);
            return true;
         } else if (var1.equals("cf")) {
            RTFReader.this.parserState.put(var1, var2);
            return true;
         } else {
            RTFAttribute var4 = (RTFAttribute)RTFReader.straightforwardAttributes.get(var1);
            if (var4 != null) {
               boolean var5;
               switch(var4.domain()) {
               case 0:
                  var5 = var4.set(this.characterAttributes, var2);
                  break;
               case 1:
                  var5 = var4.set(this.paragraphAttributes, var2);
                  break;
               case 2:
                  var5 = var4.set(this.sectionAttributes, var2);
                  break;
               case 3:
                  var5 = var4.set(RTFReader.this.documentAttributes, var2);
                  break;
               case 4:
                  RTFReader.this.mockery.backing = RTFReader.this.parserState;
                  var5 = var4.set(RTFReader.this.mockery, var2);
                  RTFReader.this.mockery.backing = null;
                  break;
               default:
                  var5 = false;
               }

               if (var5) {
                  return true;
               }
            }

            if (var1.equals("fs")) {
               StyleConstants.setFontSize(this.characterAttributes, var2 / 2);
               return true;
            } else if (var1.equals("sl")) {
               if (var2 == 1000) {
                  this.characterAttributes.removeAttribute(StyleConstants.LineSpacing);
               } else {
                  StyleConstants.setLineSpacing(this.characterAttributes, (float)var2 / 20.0F);
               }

               return true;
            } else if (!var1.equals("tx") && !var1.equals("tb")) {
               if (var1.equals("s") && RTFReader.this.paragraphStyles != null) {
                  RTFReader.this.parserState.put("paragraphStyle", RTFReader.this.paragraphStyles[var2]);
                  return true;
               } else if (var1.equals("cs") && RTFReader.this.characterStyles != null) {
                  RTFReader.this.parserState.put("characterStyle", RTFReader.this.characterStyles[var2]);
                  return true;
               } else if (var1.equals("ds") && RTFReader.this.sectionStyles != null) {
                  RTFReader.this.parserState.put("sectionStyle", RTFReader.this.sectionStyles[var2]);
                  return true;
               } else {
                  return false;
               }
            } else {
               float var11 = (float)var2 / 20.0F;
               int var12 = 0;
               Number var7 = (Number)((Number)RTFReader.this.parserState.get("tab_alignment"));
               if (var7 != null) {
                  var12 = var7.intValue();
               }

               int var6 = 0;
               var7 = (Number)((Number)RTFReader.this.parserState.get("tab_leader"));
               if (var7 != null) {
                  var6 = var7.intValue();
               }

               if (var1.equals("tb")) {
                  var12 = 5;
               }

               RTFReader.this.parserState.remove("tab_alignment");
               RTFReader.this.parserState.remove("tab_leader");
               TabStop var8 = new TabStop(var11, var12, var6);
               Object var9 = (Dictionary)RTFReader.this.parserState.get("_tabs");
               Integer var10;
               if (var9 == null) {
                  var9 = new Hashtable();
                  RTFReader.this.parserState.put("_tabs", var9);
                  var10 = 1;
               } else {
                  var10 = (Integer)((Dictionary)var9).get("stop count");
                  var10 = 1 + var10;
               }

               ((Dictionary)var9).put(var10, var8);
               ((Dictionary)var9).put("stop count", var10);
               RTFReader.this.parserState.remove("_tabs_immutable");
               return true;
            }
         }
      }

      protected MutableAttributeSet rootCharacterAttributes() {
         SimpleAttributeSet var1 = new SimpleAttributeSet();
         StyleConstants.setItalic(var1, false);
         StyleConstants.setBold(var1, false);
         StyleConstants.setUnderline(var1, false);
         StyleConstants.setForeground(var1, RTFReader.this.defaultColor());
         return var1;
      }

      protected MutableAttributeSet rootParagraphAttributes() {
         SimpleAttributeSet var1 = new SimpleAttributeSet();
         StyleConstants.setLeftIndent(var1, 0.0F);
         StyleConstants.setRightIndent(var1, 0.0F);
         StyleConstants.setFirstLineIndent(var1, 0.0F);
         var1.setResolveParent(RTFReader.this.target.getStyle("default"));
         return var1;
      }

      protected MutableAttributeSet rootSectionAttributes() {
         SimpleAttributeSet var1 = new SimpleAttributeSet();
         return var1;
      }

      MutableAttributeSet currentTextAttributes() {
         SimpleAttributeSet var1 = new SimpleAttributeSet(this.characterAttributes);
         Integer var2 = (Integer)RTFReader.this.parserState.get("f");
         String var4;
         if (var2 != null) {
            var4 = (String)RTFReader.this.fontTable.get(var2);
         } else {
            var4 = null;
         }

         if (var4 != null) {
            StyleConstants.setFontFamily(var1, var4);
         } else {
            var1.removeAttribute(StyleConstants.FontFamily);
         }

         Integer var3;
         Color var5;
         if (RTFReader.this.colorTable != null) {
            var3 = (Integer)RTFReader.this.parserState.get("cf");
            if (var3 != null) {
               var5 = RTFReader.this.colorTable[var3];
               StyleConstants.setForeground(var1, var5);
            } else {
               var1.removeAttribute(StyleConstants.Foreground);
            }
         }

         if (RTFReader.this.colorTable != null) {
            var3 = (Integer)RTFReader.this.parserState.get("cb");
            if (var3 != null) {
               var5 = RTFReader.this.colorTable[var3];
               var1.addAttribute(StyleConstants.Background, var5);
            } else {
               var1.removeAttribute(StyleConstants.Background);
            }
         }

         Style var6 = (Style)RTFReader.this.parserState.get("characterStyle");
         if (var6 != null) {
            var1.setResolveParent(var6);
         }

         return var1;
      }

      MutableAttributeSet currentParagraphAttributes() {
         SimpleAttributeSet var1 = new SimpleAttributeSet(this.paragraphAttributes);
         TabStop[] var3 = (TabStop[])((TabStop[])RTFReader.this.parserState.get("_tabs_immutable"));
         if (var3 == null) {
            Dictionary var4 = (Dictionary)RTFReader.this.parserState.get("_tabs");
            if (var4 != null) {
               int var5 = (Integer)var4.get("stop count");
               var3 = new TabStop[var5];

               for(int var6 = 1; var6 <= var5; ++var6) {
                  var3[var6 - 1] = (TabStop)var4.get(var6);
               }

               RTFReader.this.parserState.put("_tabs_immutable", var3);
            }
         }

         if (var3 != null) {
            var1.addAttribute("tabs", var3);
         }

         Style var7 = (Style)RTFReader.this.parserState.get("paragraphStyle");
         if (var7 != null) {
            var1.setResolveParent(var7);
         }

         return var1;
      }

      public AttributeSet currentSectionAttributes() {
         SimpleAttributeSet var1 = new SimpleAttributeSet(this.sectionAttributes);
         Style var2 = (Style)RTFReader.this.parserState.get("sectionStyle");
         if (var2 != null) {
            var1.setResolveParent(var2);
         }

         return var1;
      }

      protected void resetCharacterAttributes() {
         this.handleKeyword("f", 0);
         this.handleKeyword("cf", 0);
         this.handleKeyword("fs", 24);
         Enumeration var1 = RTFReader.straightforwardAttributes.elements();

         while(var1.hasMoreElements()) {
            RTFAttribute var2 = (RTFAttribute)var1.nextElement();
            if (var2.domain() == 0) {
               var2.setDefault(this.characterAttributes);
            }
         }

         this.handleKeyword("sl", 1000);
         RTFReader.this.parserState.remove("characterStyle");
      }

      protected void resetParagraphAttributes() {
         RTFReader.this.parserState.remove("_tabs");
         RTFReader.this.parserState.remove("_tabs_immutable");
         RTFReader.this.parserState.remove("paragraphStyle");
         StyleConstants.setAlignment(this.paragraphAttributes, 0);
         Enumeration var1 = RTFReader.straightforwardAttributes.elements();

         while(var1.hasMoreElements()) {
            RTFAttribute var2 = (RTFAttribute)var1.nextElement();
            if (var2.domain() == 1) {
               var2.setDefault(this.characterAttributes);
            }
         }

      }

      protected void resetSectionAttributes() {
         Enumeration var1 = RTFReader.straightforwardAttributes.elements();

         while(var1.hasMoreElements()) {
            RTFAttribute var2 = (RTFAttribute)var1.nextElement();
            if (var2.domain() == 2) {
               var2.setDefault(this.characterAttributes);
            }
         }

         RTFReader.this.parserState.remove("sectionStyle");
      }
   }

   class InfoDestination extends RTFReader.DiscardingDestination implements RTFReader.Destination {
      InfoDestination() {
         super();
      }
   }

   class StylesheetDestination extends RTFReader.DiscardingDestination implements RTFReader.Destination {
      Dictionary<Integer, RTFReader.StylesheetDestination.StyleDefiningDestination> definedStyles = new Hashtable();

      public StylesheetDestination() {
         super();
      }

      public void begingroup() {
         RTFReader.this.setRTFDestination(new RTFReader.StylesheetDestination.StyleDefiningDestination());
      }

      public void close() {
         Vector var1 = new Vector();
         Vector var2 = new Vector();
         Vector var3 = new Vector();

         RTFReader.StylesheetDestination.StyleDefiningDestination var5;
         Style var6;
         Vector var8;
         for(Enumeration var4 = this.definedStyles.elements(); var4.hasMoreElements(); var8.setElementAt(var6, var5.number)) {
            var5 = (RTFReader.StylesheetDestination.StyleDefiningDestination)var4.nextElement();
            var6 = var5.realize();
            RTFReader.this.warning("Style " + var5.number + " (" + var5.styleName + "): " + var6);
            String var7 = (String)var6.getAttribute("style:type");
            if (var7.equals("section")) {
               var8 = var3;
            } else if (var7.equals("character")) {
               var8 = var1;
            } else {
               var8 = var2;
            }

            if (var8.size() <= var5.number) {
               var8.setSize(var5.number + 1);
            }
         }

         Style[] var9;
         if (!var1.isEmpty()) {
            var9 = new Style[var1.size()];
            var1.copyInto(var9);
            RTFReader.this.characterStyles = var9;
         }

         if (!var2.isEmpty()) {
            var9 = new Style[var2.size()];
            var2.copyInto(var9);
            RTFReader.this.paragraphStyles = var9;
         }

         if (!var3.isEmpty()) {
            var9 = new Style[var3.size()];
            var3.copyInto(var9);
            RTFReader.this.sectionStyles = var9;
         }

      }

      class StyleDefiningDestination extends RTFReader.AttributeTrackingDestination implements RTFReader.Destination {
         final int STYLENUMBER_NONE = 222;
         boolean additive = false;
         boolean characterStyle = false;
         boolean sectionStyle = false;
         public String styleName = null;
         public int number = 0;
         int basedOn = 222;
         int nextStyle = 222;
         boolean hidden = false;
         Style realizedStyle;

         public StyleDefiningDestination() {
            super();
         }

         public void handleText(String var1) {
            if (this.styleName != null) {
               this.styleName = this.styleName + var1;
            } else {
               this.styleName = var1;
            }

         }

         public void close() {
            int var1 = this.styleName == null ? 0 : this.styleName.indexOf(59);
            if (var1 > 0) {
               this.styleName = this.styleName.substring(0, var1);
            }

            StylesheetDestination.this.definedStyles.put(this.number, this);
            super.close();
         }

         public boolean handleKeyword(String var1) {
            if (var1.equals("additive")) {
               this.additive = true;
               return true;
            } else if (var1.equals("shidden")) {
               this.hidden = true;
               return true;
            } else {
               return super.handleKeyword(var1);
            }
         }

         public boolean handleKeyword(String var1, int var2) {
            if (var1.equals("s")) {
               this.characterStyle = false;
               this.sectionStyle = false;
               this.number = var2;
            } else if (var1.equals("cs")) {
               this.characterStyle = true;
               this.sectionStyle = false;
               this.number = var2;
            } else if (var1.equals("ds")) {
               this.characterStyle = false;
               this.sectionStyle = true;
               this.number = var2;
            } else if (var1.equals("sbasedon")) {
               this.basedOn = var2;
            } else {
               if (!var1.equals("snext")) {
                  return super.handleKeyword(var1, var2);
               }

               this.nextStyle = var2;
            }

            return true;
         }

         public Style realize() {
            Style var1 = null;
            Style var2 = null;
            if (this.realizedStyle != null) {
               return this.realizedStyle;
            } else {
               RTFReader.StylesheetDestination.StyleDefiningDestination var3;
               if (this.basedOn != 222) {
                  var3 = (RTFReader.StylesheetDestination.StyleDefiningDestination)StylesheetDestination.this.definedStyles.get(this.basedOn);
                  if (var3 != null && var3 != this) {
                     var1 = var3.realize();
                  }
               }

               this.realizedStyle = RTFReader.this.target.addStyle(this.styleName, var1);
               if (this.characterStyle) {
                  this.realizedStyle.addAttributes(this.currentTextAttributes());
                  this.realizedStyle.addAttribute("style:type", "character");
               } else if (this.sectionStyle) {
                  this.realizedStyle.addAttributes(this.currentSectionAttributes());
                  this.realizedStyle.addAttribute("style:type", "section");
               } else {
                  this.realizedStyle.addAttributes(this.currentParagraphAttributes());
                  this.realizedStyle.addAttribute("style:type", "paragraph");
               }

               if (this.nextStyle != 222) {
                  var3 = (RTFReader.StylesheetDestination.StyleDefiningDestination)StylesheetDestination.this.definedStyles.get(this.nextStyle);
                  if (var3 != null) {
                     var2 = var3.realize();
                  }
               }

               if (var2 != null) {
                  this.realizedStyle.addAttribute("style:nextStyle", var2);
               }

               this.realizedStyle.addAttribute("style:additive", this.additive);
               this.realizedStyle.addAttribute("style:hidden", this.hidden);
               return this.realizedStyle;
            }
         }
      }
   }

   class ColortblDestination implements RTFReader.Destination {
      int red = 0;
      int green = 0;
      int blue = 0;
      Vector<Color> proTemTable = new Vector();

      public ColortblDestination() {
      }

      public void handleText(String var1) {
         for(int var2 = 0; var2 < var1.length(); ++var2) {
            if (var1.charAt(var2) == ';') {
               Color var3 = new Color(this.red, this.green, this.blue);
               this.proTemTable.addElement(var3);
            }
         }

      }

      public void close() {
         int var1 = this.proTemTable.size();
         RTFReader.this.warning("Done reading color table, " + var1 + " entries.");
         RTFReader.this.colorTable = new Color[var1];
         this.proTemTable.copyInto(RTFReader.this.colorTable);
      }

      public boolean handleKeyword(String var1, int var2) {
         if (var1.equals("red")) {
            this.red = var2;
         } else if (var1.equals("green")) {
            this.green = var2;
         } else {
            if (!var1.equals("blue")) {
               return false;
            }

            this.blue = var2;
         }

         return true;
      }

      public boolean handleKeyword(String var1) {
         return false;
      }

      public void begingroup() {
      }

      public void endgroup(Dictionary var1) {
      }

      public void handleBinaryBlob(byte[] var1) {
      }
   }

   class FonttblDestination implements RTFReader.Destination {
      int nextFontNumber;
      Integer fontNumberKey = null;
      String nextFontFamily;

      public void handleBinaryBlob(byte[] var1) {
      }

      public void handleText(String var1) {
         int var2 = var1.indexOf(59);
         String var3;
         if (var2 > -1) {
            var3 = var1.substring(0, var2);
         } else {
            var3 = var1;
         }

         if (this.nextFontNumber == -1 && this.fontNumberKey != null) {
            var3 = (String)RTFReader.this.fontTable.get(this.fontNumberKey) + var3;
         } else {
            this.fontNumberKey = this.nextFontNumber;
         }

         RTFReader.this.fontTable.put(this.fontNumberKey, var3);
         this.nextFontNumber = -1;
         this.nextFontFamily = null;
      }

      public boolean handleKeyword(String var1) {
         if (var1.charAt(0) == 'f') {
            this.nextFontFamily = var1.substring(1);
            return true;
         } else {
            return false;
         }
      }

      public boolean handleKeyword(String var1, int var2) {
         if (var1.equals("f")) {
            this.nextFontNumber = var2;
            return true;
         } else {
            return false;
         }
      }

      public void begingroup() {
      }

      public void endgroup(Dictionary var1) {
      }

      public void close() {
         Enumeration var1 = RTFReader.this.fontTable.keys();
         RTFReader.this.warning("Done reading font table.");

         while(var1.hasMoreElements()) {
            Integer var2 = (Integer)var1.nextElement();
            RTFReader.this.warning("Number " + var2 + ": " + (String)RTFReader.this.fontTable.get(var2));
         }

      }
   }

   class DiscardingDestination implements RTFReader.Destination {
      public void handleBinaryBlob(byte[] var1) {
      }

      public void handleText(String var1) {
      }

      public boolean handleKeyword(String var1) {
         return true;
      }

      public boolean handleKeyword(String var1, int var2) {
         return true;
      }

      public void begingroup() {
      }

      public void endgroup(Dictionary var1) {
      }

      public void close() {
      }
   }

   interface Destination {
      void handleBinaryBlob(byte[] var1);

      void handleText(String var1);

      boolean handleKeyword(String var1);

      boolean handleKeyword(String var1, int var2);

      void begingroup();

      void endgroup(Dictionary var1);

      void close();
   }
}
