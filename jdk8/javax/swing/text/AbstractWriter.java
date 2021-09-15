package javax.swing.text;

import java.io.IOException;
import java.io.Writer;
import java.util.Enumeration;

public abstract class AbstractWriter {
   private ElementIterator it;
   private Writer out;
   private int indentLevel;
   private int indentSpace;
   private Document doc;
   private int maxLineLength;
   private int currLength;
   private int startOffset;
   private int endOffset;
   private int offsetIndent;
   private String lineSeparator;
   private boolean canWrapLines;
   private boolean isLineEmpty;
   private char[] indentChars;
   private char[] tempChars;
   private char[] newlineChars;
   private Segment segment;
   protected static final char NEWLINE = '\n';

   protected AbstractWriter(Writer var1, Document var2) {
      this(var1, (Document)var2, 0, var2.getLength());
   }

   protected AbstractWriter(Writer var1, Document var2, int var3, int var4) {
      this.indentLevel = 0;
      this.indentSpace = 2;
      this.doc = null;
      this.maxLineLength = 100;
      this.currLength = 0;
      this.startOffset = 0;
      this.endOffset = 0;
      this.offsetIndent = 0;
      this.doc = var2;
      this.it = new ElementIterator(var2.getDefaultRootElement());
      this.out = var1;
      this.startOffset = var3;
      this.endOffset = var3 + var4;
      Object var5 = var2.getProperty("__EndOfLine__");
      if (var5 instanceof String) {
         this.setLineSeparator((String)var5);
      } else {
         String var6 = null;

         try {
            var6 = System.getProperty("line.separator");
         } catch (SecurityException var8) {
         }

         if (var6 == null) {
            var6 = "\n";
         }

         this.setLineSeparator(var6);
      }

      this.canWrapLines = true;
   }

   protected AbstractWriter(Writer var1, Element var2) {
      this(var1, (Element)var2, 0, var2.getEndOffset());
   }

   protected AbstractWriter(Writer var1, Element var2, int var3, int var4) {
      this.indentLevel = 0;
      this.indentSpace = 2;
      this.doc = null;
      this.maxLineLength = 100;
      this.currLength = 0;
      this.startOffset = 0;
      this.endOffset = 0;
      this.offsetIndent = 0;
      this.doc = var2.getDocument();
      this.it = new ElementIterator(var2);
      this.out = var1;
      this.startOffset = var3;
      this.endOffset = var3 + var4;
      this.canWrapLines = true;
   }

   public int getStartOffset() {
      return this.startOffset;
   }

   public int getEndOffset() {
      return this.endOffset;
   }

   protected ElementIterator getElementIterator() {
      return this.it;
   }

   protected Writer getWriter() {
      return this.out;
   }

   protected Document getDocument() {
      return this.doc;
   }

   protected boolean inRange(Element var1) {
      int var2 = this.getStartOffset();
      int var3 = this.getEndOffset();
      return var1.getStartOffset() >= var2 && var1.getStartOffset() < var3 || var2 >= var1.getStartOffset() && var2 < var1.getEndOffset();
   }

   protected abstract void write() throws IOException, BadLocationException;

   protected String getText(Element var1) throws BadLocationException {
      return this.doc.getText(var1.getStartOffset(), var1.getEndOffset() - var1.getStartOffset());
   }

   protected void text(Element var1) throws BadLocationException, IOException {
      int var2 = Math.max(this.getStartOffset(), var1.getStartOffset());
      int var3 = Math.min(this.getEndOffset(), var1.getEndOffset());
      if (var2 < var3) {
         if (this.segment == null) {
            this.segment = new Segment();
         }

         this.getDocument().getText(var2, var3 - var2, this.segment);
         if (this.segment.count > 0) {
            this.write(this.segment.array, this.segment.offset, this.segment.count);
         }
      }

   }

   protected void setLineLength(int var1) {
      this.maxLineLength = var1;
   }

   protected int getLineLength() {
      return this.maxLineLength;
   }

   protected void setCurrentLineLength(int var1) {
      this.currLength = var1;
      this.isLineEmpty = this.currLength == 0;
   }

   protected int getCurrentLineLength() {
      return this.currLength;
   }

   protected boolean isLineEmpty() {
      return this.isLineEmpty;
   }

   protected void setCanWrapLines(boolean var1) {
      this.canWrapLines = var1;
   }

   protected boolean getCanWrapLines() {
      return this.canWrapLines;
   }

   protected void setIndentSpace(int var1) {
      this.indentSpace = var1;
   }

   protected int getIndentSpace() {
      return this.indentSpace;
   }

   public void setLineSeparator(String var1) {
      this.lineSeparator = var1;
   }

   public String getLineSeparator() {
      return this.lineSeparator;
   }

   protected void incrIndent() {
      if (this.offsetIndent > 0) {
         ++this.offsetIndent;
      } else if (++this.indentLevel * this.getIndentSpace() >= this.getLineLength()) {
         ++this.offsetIndent;
         --this.indentLevel;
      }

   }

   protected void decrIndent() {
      if (this.offsetIndent > 0) {
         --this.offsetIndent;
      } else {
         --this.indentLevel;
      }

   }

   protected int getIndentLevel() {
      return this.indentLevel;
   }

   protected void indent() throws IOException {
      int var1 = this.getIndentLevel() * this.getIndentSpace();
      int var2;
      if (this.indentChars == null || var1 > this.indentChars.length) {
         this.indentChars = new char[var1];

         for(var2 = 0; var2 < var1; ++var2) {
            this.indentChars[var2] = ' ';
         }
      }

      var2 = this.getCurrentLineLength();
      boolean var3 = this.isLineEmpty();
      this.output(this.indentChars, 0, var1);
      if (var3 && var2 == 0) {
         this.isLineEmpty = true;
      }

   }

   protected void write(char var1) throws IOException {
      if (this.tempChars == null) {
         this.tempChars = new char[128];
      }

      this.tempChars[0] = var1;
      this.write(this.tempChars, 0, 1);
   }

   protected void write(String var1) throws IOException {
      if (var1 != null) {
         int var2 = var1.length();
         if (this.tempChars == null || this.tempChars.length < var2) {
            this.tempChars = new char[var2];
         }

         var1.getChars(0, var2, this.tempChars, 0);
         this.write(this.tempChars, 0, var2);
      }
   }

   protected void writeLineSeparator() throws IOException {
      String var1 = this.getLineSeparator();
      int var2 = var1.length();
      if (this.newlineChars == null || this.newlineChars.length < var2) {
         this.newlineChars = new char[var2];
      }

      var1.getChars(0, var2, this.newlineChars, 0);
      this.output(this.newlineChars, 0, var2);
      this.setCurrentLineLength(0);
   }

   protected void write(char[] var1, int var2, int var3) throws IOException {
      int var4;
      int var5;
      int var6;
      if (!this.getCanWrapLines()) {
         var4 = var2;
         var5 = var2 + var3;

         for(var6 = this.indexOf(var1, '\n', var2, var5); var6 != -1; var6 = this.indexOf(var1, '\n', var4, var5)) {
            if (var6 > var4) {
               this.output(var1, var4, var6 - var4);
            }

            this.writeLineSeparator();
            var4 = var6 + 1;
         }

         if (var4 < var5) {
            this.output(var1, var4, var5 - var4);
         }
      } else {
         var4 = var2;
         var5 = var2 + var3;
         var6 = this.getCurrentLineLength();
         int var7 = this.getLineLength();

         while(true) {
            boolean var10;
            do {
               boolean var9;
               do {
                  if (var4 >= var5) {
                     return;
                  }

                  int var8 = this.indexOf(var1, '\n', var4, var5);
                  var9 = false;
                  var10 = false;
                  var6 = this.getCurrentLineLength();
                  if (var8 != -1 && var6 + (var8 - var4) < var7) {
                     if (var8 > var4) {
                        this.output(var1, var4, var8 - var4);
                     }

                     var4 = var8 + 1;
                     var10 = true;
                  } else if (var8 == -1 && var6 + (var5 - var4) < var7) {
                     if (var5 > var4) {
                        this.output(var1, var4, var5 - var4);
                     }

                     var4 = var5;
                  } else {
                     int var11 = -1;
                     int var12 = Math.min(var5 - var4, var7 - var6 - 1);

                     int var13;
                     for(var13 = 0; var13 < var12; ++var13) {
                        if (Character.isWhitespace(var1[var13 + var4])) {
                           var11 = var13;
                        }
                     }

                     if (var11 != -1) {
                        var11 += var4 + 1;
                        this.output(var1, var4, var11 - var4);
                        var4 = var11;
                        var9 = true;
                     } else {
                        var13 = Math.max(0, var12);

                        for(var12 = var5 - var4; var13 < var12; ++var13) {
                           if (Character.isWhitespace(var1[var13 + var4])) {
                              var11 = var13;
                              break;
                           }
                        }

                        if (var11 == -1) {
                           this.output(var1, var4, var5 - var4);
                           var11 = var5;
                        } else {
                           var11 += var4;
                           if (var1[var11] == '\n') {
                              this.output(var1, var4, var11++ - var4);
                              var10 = true;
                           } else {
                              ++var11;
                              this.output(var1, var4, var11 - var4);
                              var9 = true;
                           }
                        }

                        var4 = var11;
                     }
                  }
               } while(!var10 && !var9 && var4 >= var5);

               this.writeLineSeparator();
            } while(var4 >= var5 && var10);

            this.indent();
         }
      }

   }

   protected void writeAttributes(AttributeSet var1) throws IOException {
      Enumeration var2 = var1.getAttributeNames();

      while(var2.hasMoreElements()) {
         Object var3 = var2.nextElement();
         this.write(" " + var3 + "=" + var1.getAttribute(var3));
      }

   }

   protected void output(char[] var1, int var2, int var3) throws IOException {
      this.getWriter().write(var1, var2, var3);
      this.setCurrentLineLength(this.getCurrentLineLength() + var3);
   }

   private int indexOf(char[] var1, char var2, int var3, int var4) {
      while(var3 < var4) {
         if (var1[var3] == var2) {
            return var3;
         }

         ++var3;
      }

      return -1;
   }
}
