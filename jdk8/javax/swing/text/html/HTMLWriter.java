package javax.swing.text.html;

import java.io.IOException;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Stack;
import java.util.Vector;
import javax.swing.text.AbstractWriter;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.ElementIterator;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.Segment;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;

public class HTMLWriter extends AbstractWriter {
   private Stack<Element> blockElementStack;
   private boolean inContent;
   private boolean inPre;
   private int preEndOffset;
   private boolean inTextArea;
   private boolean newlineOutputed;
   private boolean completeDoc;
   private Vector<HTML.Tag> tags;
   private Vector<Object> tagValues;
   private Segment segment;
   private Vector<HTML.Tag> tagsToRemove;
   private boolean wroteHead;
   private boolean replaceEntities;
   private char[] tempChars;
   private boolean indentNext;
   private boolean writeCSS;
   private MutableAttributeSet convAttr;
   private MutableAttributeSet oConvAttr;
   private boolean indented;

   public HTMLWriter(Writer var1, HTMLDocument var2) {
      this(var1, var2, 0, var2.getLength());
   }

   public HTMLWriter(Writer var1, HTMLDocument var2, int var3, int var4) {
      super(var1, (Document)var2, var3, var4);
      this.blockElementStack = new Stack();
      this.inContent = false;
      this.inPre = false;
      this.inTextArea = false;
      this.newlineOutputed = false;
      this.tags = new Vector(10);
      this.tagValues = new Vector(10);
      this.tagsToRemove = new Vector(10);
      this.indentNext = false;
      this.writeCSS = false;
      this.convAttr = new SimpleAttributeSet();
      this.oConvAttr = new SimpleAttributeSet();
      this.indented = false;
      this.completeDoc = var3 == 0 && var4 == var2.getLength();
      this.setLineLength(80);
   }

   public void write() throws IOException, BadLocationException {
      ElementIterator var1 = this.getElementIterator();
      Element var2 = null;
      this.wroteHead = false;
      this.setCurrentLineLength(0);
      this.replaceEntities = false;
      this.setCanWrapLines(false);
      if (this.segment == null) {
         this.segment = new Segment();
      }

      this.inPre = false;
      boolean var4 = false;

      while(true) {
         Element var3;
         while(true) {
            if ((var3 = var1.next()) == null) {
               this.closeOutUnwantedEmbeddedTags((AttributeSet)null);
               if (var4) {
                  this.blockElementStack.pop();
                  this.endTag(var2);
               }

               while(!this.blockElementStack.empty()) {
                  var2 = (Element)this.blockElementStack.pop();
                  if (!this.synthesizedElement(var2)) {
                     AttributeSet var7 = var2.getAttributes();
                     if (!this.matchNameAttribute(var7, HTML.Tag.PRE) && !this.isFormElementWithContent(var7)) {
                        this.decrIndent();
                     }

                     this.endTag(var2);
                  }
               }

               if (this.completeDoc) {
                  this.writeAdditionalComments();
               }

               this.segment.array = null;
               return;
            }

            if (this.inRange(var3)) {
               break;
            }

            if (this.completeDoc && var3.getAttributes().getAttribute(StyleConstants.NameAttribute) == HTML.Tag.BODY) {
               var4 = true;
               break;
            }
         }

         if (var2 != null) {
            if (this.indentNeedsIncrementing(var2, var3)) {
               this.incrIndent();
            } else {
               Element var5;
               if (var2.getParentElement() != var3.getParentElement()) {
                  for(var5 = (Element)this.blockElementStack.peek(); var5 != var3.getParentElement(); var5 = (Element)this.blockElementStack.peek()) {
                     this.blockElementStack.pop();
                     if (!this.synthesizedElement(var5)) {
                        AttributeSet var6 = var5.getAttributes();
                        if (!this.matchNameAttribute(var6, HTML.Tag.PRE) && !this.isFormElementWithContent(var6)) {
                           this.decrIndent();
                        }

                        this.endTag(var5);
                     }
                  }
               } else if (var2.getParentElement() == var3.getParentElement()) {
                  var5 = (Element)this.blockElementStack.peek();
                  if (var5 == var2) {
                     this.blockElementStack.pop();
                     this.endTag(var5);
                  }
               }
            }
         }

         if (var3.isLeaf() && !this.isFormElementWithContent(var3.getAttributes())) {
            this.emptyTag(var3);
         } else {
            this.blockElementStack.push(var3);
            this.startTag(var3);
         }

         var2 = var3;
      }
   }

   protected void writeAttributes(AttributeSet var1) throws IOException {
      this.convAttr.removeAttributes((AttributeSet)this.convAttr);
      convertToHTML32(var1, this.convAttr);
      Enumeration var2 = this.convAttr.getAttributeNames();

      while(var2.hasMoreElements()) {
         Object var3 = var2.nextElement();
         if (!(var3 instanceof HTML.Tag) && !(var3 instanceof StyleConstants) && var3 != HTML.Attribute.ENDTAG) {
            this.write(" " + var3 + "=\"" + this.convAttr.getAttribute(var3) + "\"");
         }
      }

   }

   protected void emptyTag(Element var1) throws BadLocationException, IOException {
      if (!this.inContent && !this.inPre) {
         this.indentSmart();
      }

      AttributeSet var2 = var1.getAttributes();
      this.closeOutUnwantedEmbeddedTags(var2);
      this.writeEmbeddedTags(var2);
      if (this.matchNameAttribute(var2, HTML.Tag.CONTENT)) {
         this.inContent = true;
         this.text(var1);
      } else if (this.matchNameAttribute(var2, HTML.Tag.COMMENT)) {
         this.comment(var1);
      } else {
         boolean var3 = this.isBlockTag(var1.getAttributes());
         if (this.inContent && var3) {
            this.writeLineSeparator();
            this.indentSmart();
         }

         Object var4 = var2 != null ? var2.getAttribute(StyleConstants.NameAttribute) : null;
         Object var5 = var2 != null ? var2.getAttribute(HTML.Attribute.ENDTAG) : null;
         boolean var6 = false;
         if (var4 != null && var5 != null && var5 instanceof String && var5.equals("true")) {
            var6 = true;
         }

         if (this.completeDoc && this.matchNameAttribute(var2, HTML.Tag.HEAD)) {
            if (var6) {
               this.writeStyles(((HTMLDocument)this.getDocument()).getStyleSheet());
            }

            this.wroteHead = true;
         }

         this.write('<');
         if (var6) {
            this.write('/');
         }

         this.write(var1.getName());
         this.writeAttributes(var2);
         this.write('>');
         if (this.matchNameAttribute(var2, HTML.Tag.TITLE) && !var6) {
            Document var7 = var1.getDocument();
            String var8 = (String)var7.getProperty("title");
            this.write(var8);
         } else if (!this.inContent || var3) {
            this.writeLineSeparator();
            if (var3 && this.inContent) {
               this.indentSmart();
            }
         }
      }

   }

   protected boolean isBlockTag(AttributeSet var1) {
      Object var2 = var1.getAttribute(StyleConstants.NameAttribute);
      if (var2 instanceof HTML.Tag) {
         HTML.Tag var3 = (HTML.Tag)var2;
         return var3.isBlock();
      } else {
         return false;
      }
   }

   protected void startTag(Element var1) throws IOException, BadLocationException {
      if (!this.synthesizedElement(var1)) {
         AttributeSet var2 = var1.getAttributes();
         Object var3 = var2.getAttribute(StyleConstants.NameAttribute);
         HTML.Tag var4;
         if (var3 instanceof HTML.Tag) {
            var4 = (HTML.Tag)var3;
         } else {
            var4 = null;
         }

         if (var4 == HTML.Tag.PRE) {
            this.inPre = true;
            this.preEndOffset = var1.getEndOffset();
         }

         this.closeOutUnwantedEmbeddedTags(var2);
         if (this.inContent) {
            this.writeLineSeparator();
            this.inContent = false;
            this.newlineOutputed = false;
         }

         if (this.completeDoc && var4 == HTML.Tag.BODY && !this.wroteHead) {
            this.wroteHead = true;
            this.indentSmart();
            this.write("<head>");
            this.writeLineSeparator();
            this.incrIndent();
            this.writeStyles(((HTMLDocument)this.getDocument()).getStyleSheet());
            this.decrIndent();
            this.writeLineSeparator();
            this.indentSmart();
            this.write("</head>");
            this.writeLineSeparator();
         }

         this.indentSmart();
         this.write('<');
         this.write(var1.getName());
         this.writeAttributes(var2);
         this.write('>');
         if (var4 != HTML.Tag.PRE) {
            this.writeLineSeparator();
         }

         if (var4 == HTML.Tag.TEXTAREA) {
            this.textAreaContent(var1.getAttributes());
         } else if (var4 == HTML.Tag.SELECT) {
            this.selectContent(var1.getAttributes());
         } else if (this.completeDoc && var4 == HTML.Tag.BODY) {
            this.writeMaps(((HTMLDocument)this.getDocument()).getMaps());
         } else if (var4 == HTML.Tag.HEAD) {
            HTMLDocument var5 = (HTMLDocument)this.getDocument();
            this.wroteHead = true;
            this.incrIndent();
            this.writeStyles(var5.getStyleSheet());
            if (var5.hasBaseTag()) {
               this.indentSmart();
               this.write("<base href=\"" + var5.getBase() + "\">");
               this.writeLineSeparator();
            }

            this.decrIndent();
         }

      }
   }

   protected void textAreaContent(AttributeSet var1) throws BadLocationException, IOException {
      Document var2 = (Document)var1.getAttribute(StyleConstants.ModelAttribute);
      if (var2 != null && var2.getLength() > 0) {
         if (this.segment == null) {
            this.segment = new Segment();
         }

         var2.getText(0, var2.getLength(), this.segment);
         if (this.segment.count > 0) {
            this.inTextArea = true;
            this.incrIndent();
            this.indentSmart();
            this.setCanWrapLines(true);
            this.replaceEntities = true;
            this.write(this.segment.array, this.segment.offset, this.segment.count);
            this.replaceEntities = false;
            this.setCanWrapLines(false);
            this.writeLineSeparator();
            this.inTextArea = false;
            this.decrIndent();
         }
      }

   }

   protected void text(Element var1) throws BadLocationException, IOException {
      int var2 = Math.max(this.getStartOffset(), var1.getStartOffset());
      int var3 = Math.min(this.getEndOffset(), var1.getEndOffset());
      if (var2 < var3) {
         if (this.segment == null) {
            this.segment = new Segment();
         }

         this.getDocument().getText(var2, var3 - var2, this.segment);
         this.newlineOutputed = false;
         if (this.segment.count > 0) {
            if (this.segment.array[this.segment.offset + this.segment.count - 1] == '\n') {
               this.newlineOutputed = true;
            }

            if (this.inPre && var3 == this.preEndOffset) {
               if (this.segment.count <= 1) {
                  return;
               }

               --this.segment.count;
            }

            this.replaceEntities = true;
            this.setCanWrapLines(!this.inPre);
            this.write(this.segment.array, this.segment.offset, this.segment.count);
            this.setCanWrapLines(false);
            this.replaceEntities = false;
         }
      }

   }

   protected void selectContent(AttributeSet var1) throws IOException {
      Object var2 = var1.getAttribute(StyleConstants.ModelAttribute);
      this.incrIndent();
      int var4;
      int var5;
      Option var6;
      if (var2 instanceof OptionListModel) {
         OptionListModel var3 = (OptionListModel)var2;
         var4 = var3.getSize();

         for(var5 = 0; var5 < var4; ++var5) {
            var6 = (Option)var3.getElementAt(var5);
            this.writeOption(var6);
         }
      } else if (var2 instanceof OptionComboBoxModel) {
         OptionComboBoxModel var7 = (OptionComboBoxModel)var2;
         var4 = var7.getSize();

         for(var5 = 0; var5 < var4; ++var5) {
            var6 = (Option)var7.getElementAt(var5);
            this.writeOption(var6);
         }
      }

      this.decrIndent();
   }

   protected void writeOption(Option var1) throws IOException {
      this.indentSmart();
      this.write('<');
      this.write("option");
      Object var2 = var1.getAttributes().getAttribute(HTML.Attribute.VALUE);
      if (var2 != null) {
         this.write(" value=" + var2);
      }

      if (var1.isSelected()) {
         this.write(" selected");
      }

      this.write('>');
      if (var1.getLabel() != null) {
         this.write(var1.getLabel());
      }

      this.writeLineSeparator();
   }

   protected void endTag(Element var1) throws IOException {
      if (!this.synthesizedElement(var1)) {
         this.closeOutUnwantedEmbeddedTags(var1.getAttributes());
         if (this.inContent) {
            if (!this.newlineOutputed && !this.inPre) {
               this.writeLineSeparator();
            }

            this.newlineOutputed = false;
            this.inContent = false;
         }

         if (!this.inPre) {
            this.indentSmart();
         }

         if (this.matchNameAttribute(var1.getAttributes(), HTML.Tag.PRE)) {
            this.inPre = false;
         }

         this.write('<');
         this.write('/');
         this.write(var1.getName());
         this.write('>');
         this.writeLineSeparator();
      }
   }

   protected void comment(Element var1) throws BadLocationException, IOException {
      AttributeSet var2 = var1.getAttributes();
      if (this.matchNameAttribute(var2, HTML.Tag.COMMENT)) {
         Object var3 = var2.getAttribute(HTML.Attribute.COMMENT);
         if (var3 instanceof String) {
            this.writeComment((String)var3);
         } else {
            this.writeComment((String)null);
         }
      }

   }

   void writeComment(String var1) throws IOException {
      this.write("<!--");
      if (var1 != null) {
         this.write(var1);
      }

      this.write("-->");
      this.writeLineSeparator();
      this.indentSmart();
   }

   void writeAdditionalComments() throws IOException {
      Object var1 = this.getDocument().getProperty("AdditionalComments");
      if (var1 instanceof Vector) {
         Vector var2 = (Vector)var1;
         int var3 = 0;

         for(int var4 = var2.size(); var3 < var4; ++var3) {
            this.writeComment(var2.elementAt(var3).toString());
         }
      }

   }

   protected boolean synthesizedElement(Element var1) {
      return this.matchNameAttribute(var1.getAttributes(), HTML.Tag.IMPLIED);
   }

   protected boolean matchNameAttribute(AttributeSet var1, HTML.Tag var2) {
      Object var3 = var1.getAttribute(StyleConstants.NameAttribute);
      if (var3 instanceof HTML.Tag) {
         HTML.Tag var4 = (HTML.Tag)var3;
         if (var4 == var2) {
            return true;
         }
      }

      return false;
   }

   protected void writeEmbeddedTags(AttributeSet var1) throws IOException {
      var1 = this.convertToHTML(var1, this.oConvAttr);
      Enumeration var2 = var1.getAttributeNames();

      while(var2.hasMoreElements()) {
         Object var3 = var2.nextElement();
         if (var3 instanceof HTML.Tag) {
            HTML.Tag var4 = (HTML.Tag)var3;
            if (var4 != HTML.Tag.FORM && !this.tags.contains(var4)) {
               this.write('<');
               this.write(var4.toString());
               Object var5 = var1.getAttribute(var4);
               if (var5 != null && var5 instanceof AttributeSet) {
                  this.writeAttributes((AttributeSet)var5);
               }

               this.write('>');
               this.tags.addElement(var4);
               this.tagValues.addElement(var5);
            }
         }
      }

   }

   private boolean noMatchForTagInAttributes(AttributeSet var1, HTML.Tag var2, Object var3) {
      if (var1 != null && var1.isDefined(var2)) {
         Object var4 = var1.getAttribute(var2);
         if (var3 == null) {
            if (var4 == null) {
               return false;
            }
         } else if (var4 != null && var3.equals(var4)) {
            return false;
         }
      }

      return true;
   }

   protected void closeOutUnwantedEmbeddedTags(AttributeSet var1) throws IOException {
      this.tagsToRemove.removeAllElements();
      var1 = this.convertToHTML(var1, (MutableAttributeSet)null);
      int var4 = -1;
      int var5 = this.tags.size();

      HTML.Tag var2;
      for(int var6 = var5 - 1; var6 >= 0; --var6) {
         var2 = (HTML.Tag)this.tags.elementAt(var6);
         Object var3 = this.tagValues.elementAt(var6);
         if (var1 == null || this.noMatchForTagInAttributes(var1, var2, var3)) {
            var4 = var6;
            this.tagsToRemove.addElement(var2);
         }
      }

      if (var4 != -1) {
         boolean var9 = var5 - var4 == this.tagsToRemove.size();

         int var7;
         for(var7 = var5 - 1; var7 >= var4; --var7) {
            var2 = (HTML.Tag)this.tags.elementAt(var7);
            if (var9 || this.tagsToRemove.contains(var2)) {
               this.tags.removeElementAt(var7);
               this.tagValues.removeElementAt(var7);
            }

            this.write('<');
            this.write('/');
            this.write(var2.toString());
            this.write('>');
         }

         var5 = this.tags.size();

         for(var7 = var4; var7 < var5; ++var7) {
            var2 = (HTML.Tag)this.tags.elementAt(var7);
            this.write('<');
            this.write(var2.toString());
            Object var8 = this.tagValues.elementAt(var7);
            if (var8 != null && var8 instanceof AttributeSet) {
               this.writeAttributes((AttributeSet)var8);
            }

            this.write('>');
         }
      }

   }

   private boolean isFormElementWithContent(AttributeSet var1) {
      return this.matchNameAttribute(var1, HTML.Tag.TEXTAREA) || this.matchNameAttribute(var1, HTML.Tag.SELECT);
   }

   private boolean indentNeedsIncrementing(Element var1, Element var2) {
      if (var2.getParentElement() == var1 && !this.inPre) {
         if (this.indentNext) {
            this.indentNext = false;
            return true;
         }

         if (this.synthesizedElement(var2)) {
            this.indentNext = true;
         } else if (!this.synthesizedElement(var1)) {
            return true;
         }
      }

      return false;
   }

   void writeMaps(Enumeration var1) throws IOException {
      if (var1 != null) {
         while(var1.hasMoreElements()) {
            Map var2 = (Map)var1.nextElement();
            String var3 = var2.getName();
            this.incrIndent();
            this.indentSmart();
            this.write("<map");
            if (var3 != null) {
               this.write(" name=\"");
               this.write(var3);
               this.write("\">");
            } else {
               this.write('>');
            }

            this.writeLineSeparator();
            this.incrIndent();
            AttributeSet[] var4 = var2.getAreas();
            if (var4 != null) {
               int var5 = 0;

               for(int var6 = var4.length; var5 < var6; ++var5) {
                  this.indentSmart();
                  this.write("<area");
                  this.writeAttributes(var4[var5]);
                  this.write("></area>");
                  this.writeLineSeparator();
               }
            }

            this.decrIndent();
            this.indentSmart();
            this.write("</map>");
            this.writeLineSeparator();
            this.decrIndent();
         }
      }

   }

   void writeStyles(StyleSheet var1) throws IOException {
      if (var1 != null) {
         Enumeration var2 = var1.getStyleNames();
         if (var2 != null) {
            boolean var3 = false;

            while(var2.hasMoreElements()) {
               String var4 = (String)var2.nextElement();
               if (!"default".equals(var4) && this.writeStyle(var4, var1.getStyle(var4), var3)) {
                  var3 = true;
               }
            }

            if (var3) {
               this.writeStyleEndTag();
            }
         }
      }

   }

   boolean writeStyle(String var1, Style var2, boolean var3) throws IOException {
      boolean var4 = false;
      Enumeration var5 = var2.getAttributeNames();
      if (var5 != null) {
         while(var5.hasMoreElements()) {
            Object var6 = var5.nextElement();
            if (var6 instanceof CSS.Attribute) {
               String var7 = var2.getAttribute(var6).toString();
               if (var7 != null) {
                  if (!var3) {
                     this.writeStyleStartTag();
                     var3 = true;
                  }

                  if (!var4) {
                     var4 = true;
                     this.indentSmart();
                     this.write(var1);
                     this.write(" {");
                  } else {
                     this.write(";");
                  }

                  this.write(' ');
                  this.write(var6.toString());
                  this.write(": ");
                  this.write(var7);
               }
            }
         }
      }

      if (var4) {
         this.write(" }");
         this.writeLineSeparator();
      }

      return var4;
   }

   void writeStyleStartTag() throws IOException {
      this.indentSmart();
      this.write("<style type=\"text/css\">");
      this.incrIndent();
      this.writeLineSeparator();
      this.indentSmart();
      this.write("<!--");
      this.incrIndent();
      this.writeLineSeparator();
   }

   void writeStyleEndTag() throws IOException {
      this.decrIndent();
      this.indentSmart();
      this.write("-->");
      this.writeLineSeparator();
      this.decrIndent();
      this.indentSmart();
      this.write("</style>");
      this.writeLineSeparator();
      this.indentSmart();
   }

   AttributeSet convertToHTML(AttributeSet var1, MutableAttributeSet var2) {
      if (var2 == null) {
         var2 = this.convAttr;
      }

      var2.removeAttributes((AttributeSet)var2);
      if (this.writeCSS) {
         convertToHTML40(var1, var2);
      } else {
         convertToHTML32(var1, var2);
      }

      return var2;
   }

   private static void convertToHTML32(AttributeSet var0, MutableAttributeSet var1) {
      if (var0 != null) {
         Enumeration var2 = var0.getAttributeNames();
         String var3 = "";

         while(true) {
            while(true) {
               while(var2.hasMoreElements()) {
                  Object var4 = var2.nextElement();
                  if (var4 instanceof CSS.Attribute) {
                     if (var4 != CSS.Attribute.FONT_FAMILY && var4 != CSS.Attribute.FONT_SIZE && var4 != CSS.Attribute.COLOR) {
                        if (var4 == CSS.Attribute.FONT_WEIGHT) {
                           CSS.FontWeight var6 = (CSS.FontWeight)var0.getAttribute(CSS.Attribute.FONT_WEIGHT);
                           if (var6 != null && var6.getValue() > 400) {
                              addAttribute(var1, HTML.Tag.B, SimpleAttributeSet.EMPTY);
                           }
                        } else {
                           String var7;
                           if (var4 == CSS.Attribute.FONT_STYLE) {
                              var7 = var0.getAttribute(var4).toString();
                              if (var7.indexOf("italic") >= 0) {
                                 addAttribute(var1, HTML.Tag.I, SimpleAttributeSet.EMPTY);
                              }
                           } else if (var4 == CSS.Attribute.TEXT_DECORATION) {
                              var7 = var0.getAttribute(var4).toString();
                              if (var7.indexOf("underline") >= 0) {
                                 addAttribute(var1, HTML.Tag.U, SimpleAttributeSet.EMPTY);
                              }

                              if (var7.indexOf("line-through") >= 0) {
                                 addAttribute(var1, HTML.Tag.STRIKE, SimpleAttributeSet.EMPTY);
                              }
                           } else if (var4 == CSS.Attribute.VERTICAL_ALIGN) {
                              var7 = var0.getAttribute(var4).toString();
                              if (var7.indexOf("sup") >= 0) {
                                 addAttribute(var1, HTML.Tag.SUP, SimpleAttributeSet.EMPTY);
                              }

                              if (var7.indexOf("sub") >= 0) {
                                 addAttribute(var1, HTML.Tag.SUB, SimpleAttributeSet.EMPTY);
                              }
                           } else if (var4 == CSS.Attribute.TEXT_ALIGN) {
                              addAttribute(var1, HTML.Attribute.ALIGN, var0.getAttribute(var4).toString());
                           } else {
                              if (var3.length() > 0) {
                                 var3 = var3 + "; ";
                              }

                              var3 = var3 + var4 + ": " + var0.getAttribute(var4);
                           }
                        }
                     } else {
                        createFontAttribute((CSS.Attribute)var4, var0, var1);
                     }
                  } else {
                     Object var5 = var0.getAttribute(var4);
                     if (var5 instanceof AttributeSet) {
                        var5 = ((AttributeSet)var5).copyAttributes();
                     }

                     addAttribute(var1, var4, var5);
                  }
               }

               if (var3.length() > 0) {
                  var1.addAttribute(HTML.Attribute.STYLE, var3);
               }

               return;
            }
         }
      }
   }

   private static void addAttribute(MutableAttributeSet var0, Object var1, Object var2) {
      Object var3 = var0.getAttribute(var1);
      if (var3 != null && var3 != SimpleAttributeSet.EMPTY) {
         if (var3 instanceof MutableAttributeSet && var2 instanceof AttributeSet) {
            ((MutableAttributeSet)var3).addAttributes((AttributeSet)var2);
         }
      } else {
         var0.addAttribute(var1, var2);
      }

   }

   private static void createFontAttribute(CSS.Attribute var0, AttributeSet var1, MutableAttributeSet var2) {
      Object var3 = (MutableAttributeSet)var2.getAttribute(HTML.Tag.FONT);
      if (var3 == null) {
         var3 = new SimpleAttributeSet();
         var2.addAttribute(HTML.Tag.FONT, var3);
      }

      String var4 = var1.getAttribute(var0).toString();
      if (var0 == CSS.Attribute.FONT_FAMILY) {
         ((MutableAttributeSet)var3).addAttribute(HTML.Attribute.FACE, var4);
      } else if (var0 == CSS.Attribute.FONT_SIZE) {
         ((MutableAttributeSet)var3).addAttribute(HTML.Attribute.SIZE, var4);
      } else if (var0 == CSS.Attribute.COLOR) {
         ((MutableAttributeSet)var3).addAttribute(HTML.Attribute.COLOR, var4);
      }

   }

   private static void convertToHTML40(AttributeSet var0, MutableAttributeSet var1) {
      Enumeration var2 = var0.getAttributeNames();
      String var3 = "";

      while(var2.hasMoreElements()) {
         Object var4 = var2.nextElement();
         if (var4 instanceof CSS.Attribute) {
            var3 = var3 + " " + var4 + "=" + var0.getAttribute(var4) + ";";
         } else {
            var1.addAttribute(var4, var0.getAttribute(var4));
         }
      }

      if (var3.length() > 0) {
         var1.addAttribute(HTML.Attribute.STYLE, var3);
      }

   }

   protected void writeLineSeparator() throws IOException {
      boolean var1 = this.replaceEntities;
      this.replaceEntities = false;
      super.writeLineSeparator();
      this.replaceEntities = var1;
      this.indented = false;
   }

   protected void output(char[] var1, int var2, int var3) throws IOException {
      if (!this.replaceEntities) {
         super.output(var1, var2, var3);
      } else {
         int var4 = var2;
         var3 += var2;

         for(int var5 = var2; var5 < var3; ++var5) {
            switch(var1[var5]) {
            case '\t':
            case '\n':
            case '\r':
               break;
            case '"':
               if (var5 > var4) {
                  super.output(var1, var4, var5 - var4);
               }

               var4 = var5 + 1;
               this.output("&quot;");
               break;
            case '&':
               if (var5 > var4) {
                  super.output(var1, var4, var5 - var4);
               }

               var4 = var5 + 1;
               this.output("&amp;");
               break;
            case '<':
               if (var5 > var4) {
                  super.output(var1, var4, var5 - var4);
               }

               var4 = var5 + 1;
               this.output("&lt;");
               break;
            case '>':
               if (var5 > var4) {
                  super.output(var1, var4, var5 - var4);
               }

               var4 = var5 + 1;
               this.output("&gt;");
               break;
            default:
               if (var1[var5] < ' ' || var1[var5] > 127) {
                  if (var5 > var4) {
                     super.output(var1, var4, var5 - var4);
                  }

                  var4 = var5 + 1;
                  this.output("&#");
                  this.output(String.valueOf((int)var1[var5]));
                  this.output(";");
               }
            }
         }

         if (var4 < var3) {
            super.output(var1, var4, var3 - var4);
         }

      }
   }

   private void output(String var1) throws IOException {
      int var2 = var1.length();
      if (this.tempChars == null || this.tempChars.length < var2) {
         this.tempChars = new char[var2];
      }

      var1.getChars(0, var2, this.tempChars, 0);
      super.output(this.tempChars, 0, var2);
   }

   private void indentSmart() throws IOException {
      if (!this.indented) {
         this.indent();
         this.indented = true;
      }

   }
}
