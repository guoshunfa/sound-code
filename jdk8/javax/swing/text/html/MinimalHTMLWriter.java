package javax.swing.text.html;

import java.awt.Color;
import java.io.IOException;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AbstractWriter;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.ElementIterator;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

public class MinimalHTMLWriter extends AbstractWriter {
   private static final int BOLD = 1;
   private static final int ITALIC = 2;
   private static final int UNDERLINE = 4;
   private static final CSS css = new CSS();
   private int fontMask = 0;
   int startOffset = 0;
   int endOffset = 0;
   private AttributeSet fontAttributes;
   private Hashtable<String, String> styleNameMapping;

   public MinimalHTMLWriter(Writer var1, StyledDocument var2) {
      super(var1, (Document)var2);
   }

   public MinimalHTMLWriter(Writer var1, StyledDocument var2, int var3, int var4) {
      super(var1, (Document)var2, var3, var4);
   }

   public void write() throws IOException, BadLocationException {
      this.styleNameMapping = new Hashtable();
      this.writeStartTag("<html>");
      this.writeHeader();
      this.writeBody();
      this.writeEndTag("</html>");
   }

   protected void writeAttributes(AttributeSet var1) throws IOException {
      Enumeration var2 = var1.getAttributeNames();

      while(true) {
         Object var3;
         do {
            if (!var2.hasMoreElements()) {
               return;
            }

            var3 = var2.nextElement();
         } while(!(var3 instanceof StyleConstants.ParagraphConstants) && !(var3 instanceof StyleConstants.CharacterConstants) && !(var3 instanceof StyleConstants.FontConstants) && !(var3 instanceof StyleConstants.ColorConstants));

         this.indent();
         this.write(var3.toString());
         this.write(':');
         this.write(css.styleConstantsValueToCSSValue((StyleConstants)var3, var1.getAttribute(var3)).toString());
         this.write(';');
         this.write('\n');
      }
   }

   protected void text(Element var1) throws IOException, BadLocationException {
      String var2 = this.getText(var1);
      if (var2.length() > 0 && var2.charAt(var2.length() - 1) == '\n') {
         var2 = var2.substring(0, var2.length() - 1);
      }

      if (var2.length() > 0) {
         this.write(var2);
      }

   }

   protected void writeStartTag(String var1) throws IOException {
      this.indent();
      this.write(var1);
      this.write('\n');
      this.incrIndent();
   }

   protected void writeEndTag(String var1) throws IOException {
      this.decrIndent();
      this.indent();
      this.write(var1);
      this.write('\n');
   }

   protected void writeHeader() throws IOException {
      this.writeStartTag("<head>");
      this.writeStartTag("<style>");
      this.writeStartTag("<!--");
      this.writeStyles();
      this.writeEndTag("-->");
      this.writeEndTag("</style>");
      this.writeEndTag("</head>");
   }

   protected void writeStyles() throws IOException {
      DefaultStyledDocument var1 = (DefaultStyledDocument)this.getDocument();
      Enumeration var2 = var1.getStyleNames();

      while(true) {
         Style var3;
         do {
            if (!var2.hasMoreElements()) {
               return;
            }

            var3 = var1.getStyle((String)var2.nextElement());
         } while(var3.getAttributeCount() == 1 && var3.isDefined(StyleConstants.NameAttribute));

         this.indent();
         this.write("p." + this.addStyleName(var3.getName()));
         this.write(" {\n");
         this.incrIndent();
         this.writeAttributes(var3);
         this.decrIndent();
         this.indent();
         this.write("}\n");
      }
   }

   protected void writeBody() throws IOException, BadLocationException {
      ElementIterator var1 = this.getElementIterator();
      var1.current();
      this.writeStartTag("<body>");
      boolean var3 = false;

      Element var2;
      while((var2 = var1.next()) != null) {
         if (this.inRange(var2)) {
            if (var2 instanceof AbstractDocument.BranchElement) {
               if (var3) {
                  this.writeEndParagraph();
                  var3 = false;
                  this.fontMask = 0;
               }

               this.writeStartParagraph(var2);
            } else if (this.isText(var2)) {
               this.writeContent(var2, !var3);
               var3 = true;
            } else {
               this.writeLeaf(var2);
               var3 = true;
            }
         }
      }

      if (var3) {
         this.writeEndParagraph();
      }

      this.writeEndTag("</body>");
   }

   protected void writeEndParagraph() throws IOException {
      this.writeEndMask(this.fontMask);
      if (this.inFontTag()) {
         this.endSpanTag();
      } else {
         this.write('\n');
      }

      this.writeEndTag("</p>");
   }

   protected void writeStartParagraph(Element var1) throws IOException {
      AttributeSet var2 = var1.getAttributes();
      Object var3 = var2.getAttribute(StyleConstants.ResolveAttribute);
      if (var3 instanceof StyleContext.NamedStyle) {
         this.writeStartTag("<p class=" + this.mapStyleName(((StyleContext.NamedStyle)var3).getName()) + ">");
      } else {
         this.writeStartTag("<p>");
      }

   }

   protected void writeLeaf(Element var1) throws IOException {
      this.indent();
      if (var1.getName() == "icon") {
         this.writeImage(var1);
      } else if (var1.getName() == "component") {
         this.writeComponent(var1);
      }

   }

   protected void writeImage(Element var1) throws IOException {
   }

   protected void writeComponent(Element var1) throws IOException {
   }

   protected boolean isText(Element var1) {
      return var1.getName() == "content";
   }

   protected void writeContent(Element var1, boolean var2) throws IOException, BadLocationException {
      AttributeSet var3 = var1.getAttributes();
      this.writeNonHTMLAttributes(var3);
      if (var2) {
         this.indent();
      }

      this.writeHTMLTags(var3);
      this.text(var1);
   }

   protected void writeHTMLTags(AttributeSet var1) throws IOException {
      int var2 = this.fontMask;
      this.setFontMask(var1);
      int var3 = 0;
      int var4 = 0;
      if ((var2 & 1) != 0) {
         if ((this.fontMask & 1) == 0) {
            var3 |= 1;
         }
      } else if ((this.fontMask & 1) != 0) {
         var4 |= 1;
      }

      if ((var2 & 2) != 0) {
         if ((this.fontMask & 2) == 0) {
            var3 |= 2;
         }
      } else if ((this.fontMask & 2) != 0) {
         var4 |= 2;
      }

      if ((var2 & 4) != 0) {
         if ((this.fontMask & 4) == 0) {
            var3 |= 4;
         }
      } else if ((this.fontMask & 4) != 0) {
         var4 |= 4;
      }

      this.writeEndMask(var3);
      this.writeStartMask(var4);
   }

   private void setFontMask(AttributeSet var1) {
      if (StyleConstants.isBold(var1)) {
         this.fontMask |= 1;
      }

      if (StyleConstants.isItalic(var1)) {
         this.fontMask |= 2;
      }

      if (StyleConstants.isUnderline(var1)) {
         this.fontMask |= 4;
      }

   }

   private void writeStartMask(int var1) throws IOException {
      if (var1 != 0) {
         if ((var1 & 4) != 0) {
            this.write("<u>");
         }

         if ((var1 & 2) != 0) {
            this.write("<i>");
         }

         if ((var1 & 1) != 0) {
            this.write("<b>");
         }
      }

   }

   private void writeEndMask(int var1) throws IOException {
      if (var1 != 0) {
         if ((var1 & 1) != 0) {
            this.write("</b>");
         }

         if ((var1 & 2) != 0) {
            this.write("</i>");
         }

         if ((var1 & 4) != 0) {
            this.write("</u>");
         }
      }

   }

   protected void writeNonHTMLAttributes(AttributeSet var1) throws IOException {
      String var2 = "";
      String var3 = "; ";
      if (!this.inFontTag() || !this.fontAttributes.isEqual(var1)) {
         boolean var4 = true;
         Color var5 = (Color)var1.getAttribute(StyleConstants.Foreground);
         if (var5 != null) {
            var2 = var2 + "color: " + css.styleConstantsValueToCSSValue((StyleConstants)StyleConstants.Foreground, var5);
            var4 = false;
         }

         Integer var6 = (Integer)var1.getAttribute(StyleConstants.FontSize);
         if (var6 != null) {
            if (!var4) {
               var2 = var2 + var3;
            }

            var2 = var2 + "font-size: " + var6 + "pt";
            var4 = false;
         }

         String var7 = (String)var1.getAttribute(StyleConstants.FontFamily);
         if (var7 != null) {
            if (!var4) {
               var2 = var2 + var3;
            }

            var2 = var2 + "font-family: " + var7;
            var4 = false;
         }

         if (var2.length() > 0) {
            if (this.fontMask != 0) {
               this.writeEndMask(this.fontMask);
               this.fontMask = 0;
            }

            this.startSpanTag(var2);
            this.fontAttributes = var1;
         } else if (this.fontAttributes != null) {
            this.writeEndMask(this.fontMask);
            this.fontMask = 0;
            this.endSpanTag();
         }

      }
   }

   protected boolean inFontTag() {
      return this.fontAttributes != null;
   }

   protected void endFontTag() throws IOException {
      this.write('\n');
      this.writeEndTag("</font>");
      this.fontAttributes = null;
   }

   protected void startFontTag(String var1) throws IOException {
      boolean var2 = false;
      if (this.inFontTag()) {
         this.endFontTag();
         var2 = true;
      }

      this.writeStartTag("<font style=\"" + var1 + "\">");
      if (var2) {
         this.indent();
      }

   }

   private void startSpanTag(String var1) throws IOException {
      boolean var2 = false;
      if (this.inFontTag()) {
         this.endSpanTag();
         var2 = true;
      }

      this.writeStartTag("<span style=\"" + var1 + "\">");
      if (var2) {
         this.indent();
      }

   }

   private void endSpanTag() throws IOException {
      this.write('\n');
      this.writeEndTag("</span>");
      this.fontAttributes = null;
   }

   private String addStyleName(String var1) {
      if (this.styleNameMapping == null) {
         return var1;
      } else {
         StringBuilder var2 = null;

         for(int var3 = var1.length() - 1; var3 >= 0; --var3) {
            if (!this.isValidCharacter(var1.charAt(var3))) {
               if (var2 == null) {
                  var2 = new StringBuilder(var1);
               }

               var2.setCharAt(var3, 'a');
            }
         }

         String var4;
         for(var4 = var2 != null ? var2.toString() : var1; this.styleNameMapping.get(var4) != null; var4 = var4 + 'x') {
         }

         this.styleNameMapping.put(var1, var4);
         return var4;
      }
   }

   private String mapStyleName(String var1) {
      if (this.styleNameMapping == null) {
         return var1;
      } else {
         String var2 = (String)this.styleNameMapping.get(var1);
         return var2 == null ? var1 : var2;
      }
   }

   private boolean isValidCharacter(char var1) {
      return var1 >= 'a' && var1 <= 'z' || var1 >= 'A' && var1 <= 'Z';
   }
}
