package javax.swing.text.html;

import java.awt.font.TextAttribute;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;
import javax.swing.ButtonGroup;
import javax.swing.DefaultButtonModel;
import javax.swing.JToggleButton;
import javax.swing.event.DocumentEvent;
import javax.swing.event.UndoableEditEvent;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.ElementIterator;
import javax.swing.text.GapContent;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.PlainDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.undo.UndoableEdit;
import sun.swing.SwingUtilities2;

public class HTMLDocument extends DefaultStyledDocument {
   private boolean frameDocument;
   private boolean preservesUnknownTags;
   private HashMap<String, ButtonGroup> radioButtonGroupsMap;
   static final String TokenThreshold = "token threshold";
   private static final int MaxThreshold = 10000;
   private static final int StepThreshold = 5;
   public static final String AdditionalComments = "AdditionalComments";
   static final String StyleType = "StyleType";
   URL base;
   boolean hasBaseTag;
   private String baseTarget;
   private HTMLEditorKit.Parser parser;
   private static AttributeSet contentAttributeSet = new SimpleAttributeSet();
   static String MAP_PROPERTY = "__MAP__";
   private static char[] NEWLINE;
   private boolean insertInBody;
   private static final String I18NProperty = "i18n";

   public HTMLDocument() {
      this(new GapContent(4096), new StyleSheet());
   }

   public HTMLDocument(StyleSheet var1) {
      this(new GapContent(4096), var1);
   }

   public HTMLDocument(AbstractDocument.Content var1, StyleSheet var2) {
      super(var1, var2);
      this.frameDocument = false;
      this.preservesUnknownTags = true;
      this.hasBaseTag = false;
      this.baseTarget = null;
      this.insertInBody = false;
   }

   public HTMLEditorKit.ParserCallback getReader(int var1) {
      Object var2 = this.getProperty("stream");
      if (var2 instanceof URL) {
         this.setBase((URL)var2);
      }

      HTMLDocument.HTMLReader var3 = new HTMLDocument.HTMLReader(var1);
      return var3;
   }

   public HTMLEditorKit.ParserCallback getReader(int var1, int var2, int var3, HTML.Tag var4) {
      return this.getReader(var1, var2, var3, var4, true);
   }

   HTMLEditorKit.ParserCallback getReader(int var1, int var2, int var3, HTML.Tag var4, boolean var5) {
      Object var6 = this.getProperty("stream");
      if (var6 instanceof URL) {
         this.setBase((URL)var6);
      }

      HTMLDocument.HTMLReader var7 = new HTMLDocument.HTMLReader(var1, var2, var3, var4, var5, false, true);
      return var7;
   }

   public URL getBase() {
      return this.base;
   }

   public void setBase(URL var1) {
      this.base = var1;
      this.getStyleSheet().setBase(var1);
   }

   protected void insert(int var1, DefaultStyledDocument.ElementSpec[] var2) throws BadLocationException {
      super.insert(var1, var2);
   }

   protected void insertUpdate(AbstractDocument.DefaultDocumentEvent var1, AttributeSet var2) {
      if (var2 == null) {
         var2 = contentAttributeSet;
      } else if (var2.isDefined(StyleConstants.ComposedTextAttribute)) {
         ((MutableAttributeSet)var2).addAttributes(contentAttributeSet);
      }

      if (var2.isDefined("CR")) {
         ((MutableAttributeSet)var2).removeAttribute("CR");
      }

      super.insertUpdate(var1, var2);
   }

   protected void create(DefaultStyledDocument.ElementSpec[] var1) {
      super.create(var1);
   }

   public void setParagraphAttributes(int var1, int var2, AttributeSet var3, boolean var4) {
      try {
         this.writeLock();
         int var5 = Math.min(var1 + var2, this.getLength());
         Element var6 = this.getParagraphElement(var1);
         var1 = var6.getStartOffset();
         var6 = this.getParagraphElement(var5);
         var2 = Math.max(0, var6.getEndOffset() - var1);
         AbstractDocument.DefaultDocumentEvent var7 = new AbstractDocument.DefaultDocumentEvent(var1, var2, DocumentEvent.EventType.CHANGE);
         AttributeSet var8 = var3.copyAttributes();
         int var9 = Integer.MAX_VALUE;

         for(int var10 = var1; var10 <= var5; var10 = var9) {
            Element var11 = this.getParagraphElement(var10);
            if (var9 == var11.getEndOffset()) {
               ++var9;
            } else {
               var9 = var11.getEndOffset();
            }

            MutableAttributeSet var12 = (MutableAttributeSet)var11.getAttributes();
            var7.addEdit(new DefaultStyledDocument.AttributeUndoableEdit(var11, var8, var4));
            if (var4) {
               var12.removeAttributes((AttributeSet)var12);
            }

            var12.addAttributes(var3);
         }

         var7.end();
         this.fireChangedUpdate(var7);
         this.fireUndoableEditUpdate(new UndoableEditEvent(this, var7));
      } finally {
         this.writeUnlock();
      }
   }

   public StyleSheet getStyleSheet() {
      return (StyleSheet)this.getAttributeContext();
   }

   public HTMLDocument.Iterator getIterator(HTML.Tag var1) {
      return var1.isBlock() ? null : new HTMLDocument.LeafIterator(var1, this);
   }

   protected Element createLeafElement(Element var1, AttributeSet var2, int var3, int var4) {
      return new HTMLDocument.RunElement(var1, var2, var3, var4);
   }

   protected Element createBranchElement(Element var1, AttributeSet var2) {
      return new HTMLDocument.BlockElement(var1, var2);
   }

   protected AbstractDocument.AbstractElement createDefaultRoot() {
      this.writeLock();
      SimpleAttributeSet var1 = new SimpleAttributeSet();
      var1.addAttribute(StyleConstants.NameAttribute, HTML.Tag.HTML);
      HTMLDocument.BlockElement var2 = new HTMLDocument.BlockElement((Element)null, var1.copyAttributes());
      var1.removeAttributes((AttributeSet)var1);
      var1.addAttribute(StyleConstants.NameAttribute, HTML.Tag.BODY);
      HTMLDocument.BlockElement var3 = new HTMLDocument.BlockElement(var2, var1.copyAttributes());
      var1.removeAttributes((AttributeSet)var1);
      var1.addAttribute(StyleConstants.NameAttribute, HTML.Tag.P);
      this.getStyleSheet().addCSSAttributeFromHTML(var1, CSS.Attribute.MARGIN_TOP, "0");
      HTMLDocument.BlockElement var4 = new HTMLDocument.BlockElement(var3, var1.copyAttributes());
      var1.removeAttributes((AttributeSet)var1);
      var1.addAttribute(StyleConstants.NameAttribute, HTML.Tag.CONTENT);
      HTMLDocument.RunElement var5 = new HTMLDocument.RunElement(var4, var1, 0, 1);
      Element[] var6 = new Element[]{var5};
      var4.replace(0, 0, var6);
      var6[0] = var4;
      var3.replace(0, 0, var6);
      var6[0] = var3;
      var2.replace(0, 0, var6);
      this.writeUnlock();
      return var2;
   }

   public void setTokenThreshold(int var1) {
      this.putProperty("token threshold", new Integer(var1));
   }

   public int getTokenThreshold() {
      Integer var1 = (Integer)this.getProperty("token threshold");
      return var1 != null ? var1 : Integer.MAX_VALUE;
   }

   public void setPreservesUnknownTags(boolean var1) {
      this.preservesUnknownTags = var1;
   }

   public boolean getPreservesUnknownTags() {
      return this.preservesUnknownTags;
   }

   public void processHTMLFrameHyperlinkEvent(HTMLFrameHyperlinkEvent var1) {
      String var2 = var1.getTarget();
      Element var3 = var1.getSourceElement();
      String var4 = var1.getURL().toString();
      if (var2.equals("_self")) {
         this.updateFrame(var3, var4);
      } else if (var2.equals("_parent")) {
         this.updateFrameSet(var3.getParentElement(), var4);
      } else {
         Element var5 = this.findFrame(var2);
         if (var5 != null) {
            this.updateFrame(var5, var4);
         }
      }

   }

   private Element findFrame(String var1) {
      ElementIterator var2 = new ElementIterator(this);

      Element var3;
      while((var3 = var2.next()) != null) {
         AttributeSet var4 = var3.getAttributes();
         if (matchNameAttribute(var4, HTML.Tag.FRAME)) {
            String var5 = (String)var4.getAttribute(HTML.Attribute.NAME);
            if (var5 != null && var5.equals(var1)) {
               break;
            }
         }
      }

      return var3;
   }

   static boolean matchNameAttribute(AttributeSet var0, HTML.Tag var1) {
      Object var2 = var0.getAttribute(StyleConstants.NameAttribute);
      if (var2 instanceof HTML.Tag) {
         HTML.Tag var3 = (HTML.Tag)var2;
         if (var3 == var1) {
            return true;
         }
      }

      return false;
   }

   private void updateFrameSet(Element var1, String var2) {
      try {
         int var3 = var1.getStartOffset();
         int var4 = Math.min(this.getLength(), var1.getEndOffset());
         String var5 = "<frame";
         if (var2 != null) {
            var5 = var5 + " src=\"" + var2 + "\"";
         }

         var5 = var5 + ">";
         this.installParserIfNecessary();
         this.setOuterHTML(var1, var5);
      } catch (BadLocationException var6) {
      } catch (IOException var7) {
      }

   }

   private void updateFrame(Element var1, String var2) {
      try {
         this.writeLock();
         AbstractDocument.DefaultDocumentEvent var3 = new AbstractDocument.DefaultDocumentEvent(var1.getStartOffset(), 1, DocumentEvent.EventType.CHANGE);
         AttributeSet var4 = var1.getAttributes().copyAttributes();
         MutableAttributeSet var5 = (MutableAttributeSet)var1.getAttributes();
         var3.addEdit(new DefaultStyledDocument.AttributeUndoableEdit(var1, var4, false));
         var5.removeAttribute(HTML.Attribute.SRC);
         var5.addAttribute(HTML.Attribute.SRC, var2);
         var3.end();
         this.fireChangedUpdate(var3);
         this.fireUndoableEditUpdate(new UndoableEditEvent(this, var3));
      } finally {
         this.writeUnlock();
      }

   }

   boolean isFrameDocument() {
      return this.frameDocument;
   }

   void setFrameDocumentState(boolean var1) {
      this.frameDocument = var1;
   }

   void addMap(Map var1) {
      String var2 = var1.getName();
      if (var2 != null) {
         Object var3 = this.getProperty(MAP_PROPERTY);
         if (var3 == null) {
            var3 = new Hashtable(11);
            this.putProperty(MAP_PROPERTY, var3);
         }

         if (var3 instanceof Hashtable) {
            ((Hashtable)var3).put("#" + var2, var1);
         }
      }

   }

   void removeMap(Map var1) {
      String var2 = var1.getName();
      if (var2 != null) {
         Object var3 = this.getProperty(MAP_PROPERTY);
         if (var3 instanceof Hashtable) {
            ((Hashtable)var3).remove("#" + var2);
         }
      }

   }

   Map getMap(String var1) {
      if (var1 != null) {
         Object var2 = this.getProperty(MAP_PROPERTY);
         if (var2 != null && var2 instanceof Hashtable) {
            return (Map)((Hashtable)var2).get(var1);
         }
      }

      return null;
   }

   Enumeration getMaps() {
      Object var1 = this.getProperty(MAP_PROPERTY);
      return var1 instanceof Hashtable ? ((Hashtable)var1).elements() : null;
   }

   void setDefaultStyleSheetType(String var1) {
      this.putProperty("StyleType", var1);
   }

   String getDefaultStyleSheetType() {
      String var1 = (String)this.getProperty("StyleType");
      return var1 == null ? "text/css" : var1;
   }

   public void setParser(HTMLEditorKit.Parser var1) {
      this.parser = var1;
      this.putProperty("__PARSER__", (Object)null);
   }

   public HTMLEditorKit.Parser getParser() {
      Object var1 = this.getProperty("__PARSER__");
      return var1 instanceof HTMLEditorKit.Parser ? (HTMLEditorKit.Parser)var1 : this.parser;
   }

   public void setInnerHTML(Element var1, String var2) throws BadLocationException, IOException {
      this.verifyParser();
      if (var1 != null && var1.isLeaf()) {
         throw new IllegalArgumentException("Can not set inner HTML of a leaf");
      } else {
         if (var1 != null && var2 != null) {
            int var3 = var1.getElementCount();
            int var4 = var1.getStartOffset();
            this.insertHTML(var1, var1.getStartOffset(), var2, true);
            if (var1.getElementCount() > var3) {
               this.removeElements(var1, var1.getElementCount() - var3, var3);
            }
         }

      }
   }

   public void setOuterHTML(Element var1, String var2) throws BadLocationException, IOException {
      this.verifyParser();
      if (var1 != null && var1.getParentElement() != null && var2 != null) {
         int var3 = var1.getStartOffset();
         int var4 = var1.getEndOffset();
         int var5 = this.getLength();
         boolean var6 = !var1.isLeaf();
         if (!var6 && (var4 > var5 || this.getText(var4 - 1, 1).charAt(0) == NEWLINE[0])) {
            var6 = true;
         }

         Element var7 = var1.getParentElement();
         int var8 = var7.getElementCount();
         this.insertHTML(var7, var3, var2, var6);
         int var9 = this.getLength();
         if (var8 != var7.getElementCount()) {
            int var10 = var7.getElementIndex(var3 + var9 - var5);
            this.removeElements(var7, var10, 1);
         }
      }

   }

   public void insertAfterStart(Element var1, String var2) throws BadLocationException, IOException {
      this.verifyParser();
      if (var1 != null && var2 != null) {
         if (var1.isLeaf()) {
            throw new IllegalArgumentException("Can not insert HTML after start of a leaf");
         } else {
            this.insertHTML(var1, var1.getStartOffset(), var2, false);
         }
      }
   }

   public void insertBeforeEnd(Element var1, String var2) throws BadLocationException, IOException {
      this.verifyParser();
      if (var1 != null && var1.isLeaf()) {
         throw new IllegalArgumentException("Can not set inner HTML before end of leaf");
      } else {
         if (var1 != null) {
            int var3 = var1.getEndOffset();
            if (var1.getElement(var1.getElementIndex(var3 - 1)).isLeaf() && this.getText(var3 - 1, 1).charAt(0) == NEWLINE[0]) {
               --var3;
            }

            this.insertHTML(var1, var3, var2, false);
         }

      }
   }

   public void insertBeforeStart(Element var1, String var2) throws BadLocationException, IOException {
      this.verifyParser();
      if (var1 != null) {
         Element var3 = var1.getParentElement();
         if (var3 != null) {
            this.insertHTML(var3, var1.getStartOffset(), var2, false);
         }
      }

   }

   public void insertAfterEnd(Element var1, String var2) throws BadLocationException, IOException {
      this.verifyParser();
      if (var1 != null) {
         Element var3 = var1.getParentElement();
         if (var3 != null) {
            if (HTML.Tag.BODY.name.equals(var3.getName())) {
               this.insertInBody = true;
            }

            int var4 = var1.getEndOffset();
            if (var4 > this.getLength() + 1) {
               --var4;
            } else if (var1.isLeaf() && this.getText(var4 - 1, 1).charAt(0) == NEWLINE[0]) {
               --var4;
            }

            this.insertHTML(var3, var4, var2, false);
            if (this.insertInBody) {
               this.insertInBody = false;
            }
         }
      }

   }

   public Element getElement(String var1) {
      return var1 == null ? null : this.getElement(this.getDefaultRootElement(), HTML.Attribute.ID, var1, true);
   }

   public Element getElement(Element var1, Object var2, Object var3) {
      return this.getElement(var1, var2, var3, true);
   }

   private Element getElement(Element var1, Object var2, Object var3, boolean var4) {
      AttributeSet var5 = var1.getAttributes();
      if (var5 != null && var5.isDefined(var2) && var3.equals(var5.getAttribute(var2))) {
         return var1;
      } else {
         if (!var1.isLeaf()) {
            int var6 = 0;

            for(int var7 = var1.getElementCount(); var6 < var7; ++var6) {
               Element var8 = this.getElement(var1.getElement(var6), var2, var3, var4);
               if (var8 != null) {
                  return var8;
               }
            }
         } else if (var4 && var5 != null) {
            Enumeration var9 = var5.getAttributeNames();
            if (var9 != null) {
               while(var9.hasMoreElements()) {
                  Object var10 = var9.nextElement();
                  if (var10 instanceof HTML.Tag && var5.getAttribute(var10) instanceof AttributeSet) {
                     AttributeSet var11 = (AttributeSet)var5.getAttribute(var10);
                     if (var11.isDefined(var2) && var3.equals(var11.getAttribute(var2))) {
                        return var1;
                     }
                  }
               }
            }
         }

         return null;
      }
   }

   private void verifyParser() {
      if (this.getParser() == null) {
         throw new IllegalStateException("No HTMLEditorKit.Parser");
      }
   }

   private void installParserIfNecessary() {
      if (this.getParser() == null) {
         this.setParser((new HTMLEditorKit()).getParser());
      }

   }

   private void insertHTML(Element var1, int var2, String var3, boolean var4) throws BadLocationException, IOException {
      if (var1 != null && var3 != null) {
         HTMLEditorKit.Parser var5 = this.getParser();
         if (var5 != null) {
            int var6 = Math.max(0, var2 - 1);
            Element var7 = this.getCharacterElement(var6);
            Element var8 = var1;
            int var9 = 0;
            int var10 = 0;
            if (var1.getStartOffset() > var6) {
               while(var8 != null && var8.getStartOffset() > var6) {
                  var8 = var8.getParentElement();
                  ++var10;
               }

               if (var8 == null) {
                  throw new BadLocationException("No common parent", var2);
               }
            }

            while(var7 != null && var7 != var8) {
               ++var9;
               var7 = var7.getParentElement();
            }

            if (var7 != null) {
               HTMLDocument.HTMLReader var11 = new HTMLDocument.HTMLReader(var2, var9 - 1, var10, (HTML.Tag)null, false, true, var4);
               var5.parse(new StringReader(var3), var11, true);
               var11.flush();
            }
         }
      }

   }

   private void removeElements(Element var1, int var2, int var3) throws BadLocationException {
      this.writeLock();

      try {
         int var4 = var1.getElement(var2).getStartOffset();
         int var5 = var1.getElement(var2 + var3 - 1).getEndOffset();
         if (var5 > this.getLength()) {
            this.removeElementsAtEnd(var1, var2, var3, var4, var5);
         } else {
            this.removeElements(var1, var2, var3, var4, var5);
         }
      } finally {
         this.writeUnlock();
      }

   }

   private void removeElementsAtEnd(Element var1, int var2, int var3, int var4, int var5) throws BadLocationException {
      boolean var6 = var1.getElement(var2 - 1).isLeaf();
      AbstractDocument.DefaultDocumentEvent var7 = new AbstractDocument.DefaultDocumentEvent(var4 - 1, var5 - var4 + 1, DocumentEvent.EventType.REMOVE);
      Element var8;
      if (var6) {
         var8 = this.getCharacterElement(this.getLength());
         --var2;
         if (var8.getParentElement() != var1) {
            ++var3;
            this.replace(var7, var1, var2, var3, var4, var5, true, true);
         } else {
            this.replace(var7, var1, var2, var3, var4, var5, true, false);
         }
      } else {
         for(var8 = var1.getElement(var2 - 1); !var8.isLeaf(); var8 = var8.getElement(var8.getElementCount() - 1)) {
         }

         var8 = var8.getParentElement();
         this.replace(var7, var1, var2, var3, var4, var5, false, false);
         this.replace(var7, var8, var8.getElementCount() - 1, 1, var4, var5, true, true);
      }

      this.postRemoveUpdate(var7);
      var7.end();
      this.fireRemoveUpdate(var7);
      this.fireUndoableEditUpdate(new UndoableEditEvent(this, var7));
   }

   private void replace(AbstractDocument.DefaultDocumentEvent var1, Element var2, int var3, int var4, int var5, int var6, boolean var7, boolean var8) throws BadLocationException {
      AttributeSet var10 = var2.getElement(var3).getAttributes();
      Element[] var11 = new Element[var4];

      for(int var12 = 0; var12 < var4; ++var12) {
         var11[var12] = var2.getElement(var12 + var3);
      }

      if (var7) {
         UndoableEdit var13 = this.getContent().remove(var5 - 1, var6 - var5);
         if (var13 != null) {
            var1.addEdit(var13);
         }
      }

      Element[] var9;
      if (var8) {
         var9 = new Element[]{this.createLeafElement(var2, var10, var5 - 1, var5)};
      } else {
         var9 = new Element[0];
      }

      var1.addEdit(new AbstractDocument.ElementEdit(var2, var3, var11, var9));
      ((AbstractDocument.BranchElement)var2).replace(var3, var11.length, var9);
   }

   private void removeElements(Element var1, int var2, int var3, int var4, int var5) throws BadLocationException {
      Element[] var6 = new Element[var3];
      Element[] var7 = new Element[0];

      for(int var8 = 0; var8 < var3; ++var8) {
         var6[var8] = var1.getElement(var8 + var2);
      }

      AbstractDocument.DefaultDocumentEvent var10 = new AbstractDocument.DefaultDocumentEvent(var4, var5 - var4, DocumentEvent.EventType.REMOVE);
      ((AbstractDocument.BranchElement)var1).replace(var2, var6.length, var7);
      var10.addEdit(new AbstractDocument.ElementEdit(var1, var2, var6, var7));
      UndoableEdit var9 = this.getContent().remove(var4, var5 - var4);
      if (var9 != null) {
         var10.addEdit(var9);
      }

      this.postRemoveUpdate(var10);
      var10.end();
      this.fireRemoveUpdate(var10);
      if (var9 != null) {
         this.fireUndoableEditUpdate(new UndoableEditEvent(this, var10));
      }

   }

   void obtainLock() {
      this.writeLock();
   }

   void releaseLock() {
      this.writeUnlock();
   }

   protected void fireChangedUpdate(DocumentEvent var1) {
      super.fireChangedUpdate(var1);
   }

   protected void fireUndoableEditUpdate(UndoableEditEvent var1) {
      super.fireUndoableEditUpdate(var1);
   }

   boolean hasBaseTag() {
      return this.hasBaseTag;
   }

   String getBaseTarget() {
      return this.baseTarget;
   }

   static {
      ((MutableAttributeSet)contentAttributeSet).addAttribute(StyleConstants.NameAttribute, HTML.Tag.CONTENT);
      NEWLINE = new char[1];
      NEWLINE[0] = '\n';
   }

   private static class FixedLengthDocument extends PlainDocument {
      private int maxLength;

      public FixedLengthDocument(int var1) {
         this.maxLength = var1;
      }

      public void insertString(int var1, String var2, AttributeSet var3) throws BadLocationException {
         if (var2 != null && var2.length() + this.getLength() <= this.maxLength) {
            super.insertString(var1, var2, var3);
         }

      }
   }

   public class BlockElement extends AbstractDocument.BranchElement {
      public BlockElement(Element var2, AttributeSet var3) {
         super(var2, var3);
      }

      public String getName() {
         Object var1 = this.getAttribute(StyleConstants.NameAttribute);
         return var1 != null ? var1.toString() : super.getName();
      }

      public AttributeSet getResolveParent() {
         return null;
      }
   }

   public class RunElement extends AbstractDocument.LeafElement {
      public RunElement(Element var2, AttributeSet var3, int var4, int var5) {
         super(var2, var3, var4, var5);
      }

      public String getName() {
         Object var1 = this.getAttribute(StyleConstants.NameAttribute);
         return var1 != null ? var1.toString() : super.getName();
      }

      public AttributeSet getResolveParent() {
         return null;
      }
   }

   static class TaggedAttributeSet extends SimpleAttributeSet {
   }

   public class HTMLReader extends HTMLEditorKit.ParserCallback {
      private boolean receivedEndHTML;
      private int flushCount;
      private boolean insertAfterImplied;
      private boolean wantsTrailingNewline;
      int threshold;
      int offset;
      boolean inParagraph;
      boolean impliedP;
      boolean inPre;
      boolean inTextArea;
      TextAreaDocument textAreaDocument;
      boolean inTitle;
      boolean lastWasNewline;
      boolean emptyAnchor;
      boolean midInsert;
      boolean inBody;
      HTML.Tag insertTag;
      boolean insertInsertTag;
      boolean foundInsertTag;
      int insertTagDepthDelta;
      int popDepth;
      int pushDepth;
      Map lastMap;
      boolean inStyle;
      String defaultStyle;
      Vector<Object> styles;
      boolean inHead;
      boolean isStyleCSS;
      boolean emptyDocument;
      AttributeSet styleAttributes;
      Option option;
      protected Vector<DefaultStyledDocument.ElementSpec> parseBuffer;
      protected MutableAttributeSet charAttr;
      Stack<AttributeSet> charAttrStack;
      Hashtable<HTML.Tag, HTMLDocument.HTMLReader.TagAction> tagMap;
      int inBlock;
      private HTML.Tag nextTagAfterPImplied;

      public HTMLReader(int var2) {
         this(var2, 0, 0, (HTML.Tag)null);
      }

      public HTMLReader(int var2, int var3, int var4, HTML.Tag var5) {
         this(var2, var3, var4, var5, true, false, true);
      }

      HTMLReader(int var2, int var3, int var4, HTML.Tag var5, boolean var6, boolean var7, boolean var8) {
         this.inParagraph = false;
         this.impliedP = false;
         this.inPre = false;
         this.inTextArea = false;
         this.textAreaDocument = null;
         this.inTitle = false;
         this.lastWasNewline = true;
         this.inStyle = false;
         this.inHead = false;
         this.parseBuffer = new Vector();
         this.charAttr = new HTMLDocument.TaggedAttributeSet();
         this.charAttrStack = new Stack();
         this.inBlock = 0;
         this.nextTagAfterPImplied = null;
         this.emptyDocument = HTMLDocument.this.getLength() == 0;
         this.isStyleCSS = "text/css".equals(HTMLDocument.this.getDefaultStyleSheetType());
         this.offset = var2;
         this.threshold = HTMLDocument.this.getTokenThreshold();
         this.tagMap = new Hashtable(57);
         new HTMLDocument.HTMLReader.TagAction();
         HTMLDocument.HTMLReader.BlockAction var10 = new HTMLDocument.HTMLReader.BlockAction();
         HTMLDocument.HTMLReader.ParagraphAction var11 = new HTMLDocument.HTMLReader.ParagraphAction();
         HTMLDocument.HTMLReader.CharacterAction var12 = new HTMLDocument.HTMLReader.CharacterAction();
         HTMLDocument.HTMLReader.SpecialAction var13 = new HTMLDocument.HTMLReader.SpecialAction();
         HTMLDocument.HTMLReader.FormAction var14 = new HTMLDocument.HTMLReader.FormAction();
         HTMLDocument.HTMLReader.HiddenAction var15 = new HTMLDocument.HTMLReader.HiddenAction();
         HTMLDocument.HTMLReader.ConvertAction var16 = new HTMLDocument.HTMLReader.ConvertAction();
         this.tagMap.put(HTML.Tag.A, new HTMLDocument.HTMLReader.AnchorAction());
         this.tagMap.put(HTML.Tag.ADDRESS, var12);
         this.tagMap.put(HTML.Tag.APPLET, var15);
         this.tagMap.put(HTML.Tag.AREA, new HTMLDocument.HTMLReader.AreaAction());
         this.tagMap.put(HTML.Tag.B, var16);
         this.tagMap.put(HTML.Tag.BASE, new HTMLDocument.HTMLReader.BaseAction());
         this.tagMap.put(HTML.Tag.BASEFONT, var12);
         this.tagMap.put(HTML.Tag.BIG, var12);
         this.tagMap.put(HTML.Tag.BLOCKQUOTE, var10);
         this.tagMap.put(HTML.Tag.BODY, var10);
         this.tagMap.put(HTML.Tag.BR, var13);
         this.tagMap.put(HTML.Tag.CAPTION, var10);
         this.tagMap.put(HTML.Tag.CENTER, var10);
         this.tagMap.put(HTML.Tag.CITE, var12);
         this.tagMap.put(HTML.Tag.CODE, var12);
         this.tagMap.put(HTML.Tag.DD, var10);
         this.tagMap.put(HTML.Tag.DFN, var12);
         this.tagMap.put(HTML.Tag.DIR, var10);
         this.tagMap.put(HTML.Tag.DIV, var10);
         this.tagMap.put(HTML.Tag.DL, var10);
         this.tagMap.put(HTML.Tag.DT, var11);
         this.tagMap.put(HTML.Tag.EM, var12);
         this.tagMap.put(HTML.Tag.FONT, var16);
         this.tagMap.put(HTML.Tag.FORM, new HTMLDocument.HTMLReader.FormTagAction());
         this.tagMap.put(HTML.Tag.FRAME, var13);
         this.tagMap.put(HTML.Tag.FRAMESET, var10);
         this.tagMap.put(HTML.Tag.H1, var11);
         this.tagMap.put(HTML.Tag.H2, var11);
         this.tagMap.put(HTML.Tag.H3, var11);
         this.tagMap.put(HTML.Tag.H4, var11);
         this.tagMap.put(HTML.Tag.H5, var11);
         this.tagMap.put(HTML.Tag.H6, var11);
         this.tagMap.put(HTML.Tag.HEAD, new HTMLDocument.HTMLReader.HeadAction());
         this.tagMap.put(HTML.Tag.HR, var13);
         this.tagMap.put(HTML.Tag.HTML, var10);
         this.tagMap.put(HTML.Tag.I, var16);
         this.tagMap.put(HTML.Tag.IMG, var13);
         this.tagMap.put(HTML.Tag.INPUT, var14);
         this.tagMap.put(HTML.Tag.ISINDEX, new HTMLDocument.HTMLReader.IsindexAction());
         this.tagMap.put(HTML.Tag.KBD, var12);
         this.tagMap.put(HTML.Tag.LI, var10);
         this.tagMap.put(HTML.Tag.LINK, new HTMLDocument.HTMLReader.LinkAction());
         this.tagMap.put(HTML.Tag.MAP, new HTMLDocument.HTMLReader.MapAction());
         this.tagMap.put(HTML.Tag.MENU, var10);
         this.tagMap.put(HTML.Tag.META, new HTMLDocument.HTMLReader.MetaAction());
         this.tagMap.put(HTML.Tag.NOBR, var12);
         this.tagMap.put(HTML.Tag.NOFRAMES, var10);
         this.tagMap.put(HTML.Tag.OBJECT, var13);
         this.tagMap.put(HTML.Tag.OL, var10);
         this.tagMap.put(HTML.Tag.OPTION, var14);
         this.tagMap.put(HTML.Tag.P, var11);
         this.tagMap.put(HTML.Tag.PARAM, new HTMLDocument.HTMLReader.ObjectAction());
         this.tagMap.put(HTML.Tag.PRE, new HTMLDocument.HTMLReader.PreAction());
         this.tagMap.put(HTML.Tag.SAMP, var12);
         this.tagMap.put(HTML.Tag.SCRIPT, var15);
         this.tagMap.put(HTML.Tag.SELECT, var14);
         this.tagMap.put(HTML.Tag.SMALL, var12);
         this.tagMap.put(HTML.Tag.SPAN, var12);
         this.tagMap.put(HTML.Tag.STRIKE, var16);
         this.tagMap.put(HTML.Tag.S, var12);
         this.tagMap.put(HTML.Tag.STRONG, var12);
         this.tagMap.put(HTML.Tag.STYLE, new HTMLDocument.HTMLReader.StyleAction());
         this.tagMap.put(HTML.Tag.SUB, var16);
         this.tagMap.put(HTML.Tag.SUP, var16);
         this.tagMap.put(HTML.Tag.TABLE, var10);
         this.tagMap.put(HTML.Tag.TD, var10);
         this.tagMap.put(HTML.Tag.TEXTAREA, var14);
         this.tagMap.put(HTML.Tag.TH, var10);
         this.tagMap.put(HTML.Tag.TITLE, new HTMLDocument.HTMLReader.TitleAction());
         this.tagMap.put(HTML.Tag.TR, var10);
         this.tagMap.put(HTML.Tag.TT, var12);
         this.tagMap.put(HTML.Tag.U, var16);
         this.tagMap.put(HTML.Tag.UL, var10);
         this.tagMap.put(HTML.Tag.VAR, var12);
         if (var5 != null) {
            this.insertTag = var5;
            this.popDepth = var3;
            this.pushDepth = var4;
            this.insertInsertTag = var6;
            this.foundInsertTag = false;
         } else {
            this.foundInsertTag = true;
         }

         if (var7) {
            this.popDepth = var3;
            this.pushDepth = var4;
            this.insertAfterImplied = true;
            this.foundInsertTag = false;
            this.midInsert = false;
            this.insertInsertTag = true;
            this.wantsTrailingNewline = var8;
         } else {
            this.midInsert = !this.emptyDocument && var5 == null;
            if (this.midInsert) {
               this.generateEndsSpecsForMidInsert();
            }
         }

         if (!this.emptyDocument && !this.midInsert) {
            int var17 = Math.max(this.offset - 1, 0);
            Element var18 = HTMLDocument.this.getCharacterElement(var17);

            int var19;
            for(var19 = 0; var19 <= this.popDepth; ++var19) {
               var18 = var18.getParentElement();
            }

            for(var19 = 0; var19 < this.pushDepth; ++var19) {
               int var20 = var18.getElementIndex(this.offset);
               var18 = var18.getElement(var20);
            }

            AttributeSet var21 = var18.getAttributes();
            if (var21 != null) {
               HTML.Tag var22 = (HTML.Tag)var21.getAttribute(StyleConstants.NameAttribute);
               if (var22 != null) {
                  this.inParagraph = var22.isParagraph();
               }
            }
         }

      }

      private void generateEndsSpecsForMidInsert() {
         int var1 = this.heightToElementWithName(HTML.Tag.BODY, Math.max(0, this.offset - 1));
         boolean var2 = false;
         if (var1 == -1 && this.offset > 0) {
            var1 = this.heightToElementWithName(HTML.Tag.BODY, this.offset);
            if (var1 != -1) {
               var1 = this.depthTo(this.offset - 1) - 1;
               var2 = true;
            }
         }

         if (var1 == -1) {
            throw new RuntimeException("Must insert new content into body element-");
         } else {
            if (var1 != -1) {
               try {
                  if (!var2 && this.offset > 0 && !HTMLDocument.this.getText(this.offset - 1, 1).equals("\n")) {
                     SimpleAttributeSet var3 = new SimpleAttributeSet();
                     var3.addAttribute(StyleConstants.NameAttribute, HTML.Tag.CONTENT);
                     DefaultStyledDocument.ElementSpec var4 = new DefaultStyledDocument.ElementSpec(var3, (short)3, HTMLDocument.NEWLINE, 0, 1);
                     this.parseBuffer.addElement(var4);
                  }
               } catch (BadLocationException var5) {
               }

               while(var1-- > 0) {
                  this.parseBuffer.addElement(new DefaultStyledDocument.ElementSpec((AttributeSet)null, (short)2));
               }

               if (var2) {
                  DefaultStyledDocument.ElementSpec var6 = new DefaultStyledDocument.ElementSpec((AttributeSet)null, (short)1);
                  var6.setDirection((short)5);
                  this.parseBuffer.addElement(var6);
               }
            }

         }
      }

      private int depthTo(int var1) {
         Element var2 = HTMLDocument.this.getDefaultRootElement();

         int var3;
         for(var3 = 0; !var2.isLeaf(); var2 = var2.getElement(var2.getElementIndex(var1))) {
            ++var3;
         }

         return var3;
      }

      private int heightToElementWithName(Object var1, int var2) {
         Element var3 = HTMLDocument.this.getCharacterElement(var2).getParentElement();

         int var4;
         for(var4 = 0; var3 != null && var3.getAttributes().getAttribute(StyleConstants.NameAttribute) != var1; var3 = var3.getParentElement()) {
            ++var4;
         }

         return var3 == null ? -1 : var4;
      }

      private void adjustEndElement() {
         int var1 = HTMLDocument.this.getLength();
         if (var1 != 0) {
            HTMLDocument.this.obtainLock();

            try {
               Element[] var2 = this.getPathTo(var1 - 1);
               int var3 = var2.length;
               if (var3 > 1 && var2[1].getAttributes().getAttribute(StyleConstants.NameAttribute) == HTML.Tag.BODY && var2[1].getEndOffset() == var1) {
                  String var4 = HTMLDocument.this.getText(var1 - 1, 1);
                  Element[] var6 = new Element[0];
                  Element[] var7 = new Element[1];
                  int var8 = var2[0].getElementIndex(var1);
                  var7[0] = var2[0].getElement(var8);
                  ((AbstractDocument.BranchElement)var2[0]).replace(var8, 1, var6);
                  AbstractDocument.ElementEdit var9 = new AbstractDocument.ElementEdit(var2[0], var8, var7, var6);
                  SimpleAttributeSet var10 = new SimpleAttributeSet();
                  var10.addAttribute(StyleConstants.NameAttribute, HTML.Tag.CONTENT);
                  var10.addAttribute("CR", Boolean.TRUE);
                  var6 = new Element[]{HTMLDocument.this.createLeafElement(var2[var3 - 1], var10, var1, var1 + 1)};
                  var8 = var2[var3 - 1].getElementCount();
                  ((AbstractDocument.BranchElement)var2[var3 - 1]).replace(var8, 0, var6);
                  AbstractDocument.DefaultDocumentEvent var5 = HTMLDocument.this.new DefaultDocumentEvent(var1, 1, DocumentEvent.EventType.CHANGE);
                  var5.addEdit(new AbstractDocument.ElementEdit(var2[var3 - 1], var8, new Element[0], var6));
                  var5.addEdit(var9);
                  var5.end();
                  HTMLDocument.this.fireChangedUpdate(var5);
                  HTMLDocument.this.fireUndoableEditUpdate(new UndoableEditEvent(this, var5));
                  if (var4.equals("\n")) {
                     var5 = HTMLDocument.this.new DefaultDocumentEvent(var1 - 1, 1, DocumentEvent.EventType.REMOVE);
                     HTMLDocument.this.removeUpdate(var5);
                     UndoableEdit var11 = HTMLDocument.this.getContent().remove(var1 - 1, 1);
                     if (var11 != null) {
                        var5.addEdit(var11);
                     }

                     HTMLDocument.this.postRemoveUpdate(var5);
                     var5.end();
                     HTMLDocument.this.fireRemoveUpdate(var5);
                     HTMLDocument.this.fireUndoableEditUpdate(new UndoableEditEvent(this, var5));
                  }
               }
            } catch (BadLocationException var15) {
            } finally {
               HTMLDocument.this.releaseLock();
            }

         }
      }

      private Element[] getPathTo(int var1) {
         Stack var2 = new Stack();

         for(Element var3 = HTMLDocument.this.getDefaultRootElement(); !var3.isLeaf(); var3 = var3.getElement(var3.getElementIndex(var1))) {
            var2.push(var3);
         }

         Element[] var5 = new Element[var2.size()];
         var2.copyInto(var5);
         return var5;
      }

      public void flush() throws BadLocationException {
         if (this.emptyDocument && !this.insertAfterImplied) {
            if (HTMLDocument.this.getLength() > 0 || this.parseBuffer.size() > 0) {
               this.flushBuffer(true);
               this.adjustEndElement();
            }
         } else {
            this.flushBuffer(true);
         }

      }

      public void handleText(char[] var1, int var2) {
         if (!this.receivedEndHTML && (!this.midInsert || this.inBody)) {
            if (HTMLDocument.this.getProperty("i18n").equals(Boolean.FALSE)) {
               Object var3 = HTMLDocument.this.getProperty(TextAttribute.RUN_DIRECTION);
               if (var3 != null && var3.equals(TextAttribute.RUN_DIRECTION_RTL)) {
                  HTMLDocument.this.putProperty("i18n", Boolean.TRUE);
               } else if (SwingUtilities2.isComplexLayout(var1, 0, var1.length)) {
                  HTMLDocument.this.putProperty("i18n", Boolean.TRUE);
               }
            }

            if (this.inTextArea) {
               this.textAreaContent(var1);
            } else if (this.inPre) {
               this.preContent(var1);
            } else if (this.inTitle) {
               HTMLDocument.this.putProperty("title", new String(var1));
            } else if (this.option != null) {
               this.option.setLabel(new String(var1));
            } else if (this.inStyle) {
               if (this.styles != null) {
                  this.styles.addElement(new String(var1));
               }
            } else if (this.inBlock > 0) {
               if (!this.foundInsertTag && this.insertAfterImplied) {
                  this.foundInsertTag(false);
                  this.foundInsertTag = true;
                  this.inParagraph = this.impliedP = !HTMLDocument.this.insertInBody;
               }

               if (var1.length >= 1) {
                  this.addContent(var1, 0, var1.length);
               }
            }

         }
      }

      public void handleStartTag(HTML.Tag var1, MutableAttributeSet var2, int var3) {
         if (!this.receivedEndHTML) {
            if (this.midInsert && !this.inBody) {
               if (var1 == HTML.Tag.BODY) {
                  this.inBody = true;
                  ++this.inBlock;
               }

            } else {
               if (!this.inBody && var1 == HTML.Tag.BODY) {
                  this.inBody = true;
               }

               if (this.isStyleCSS && var2.isDefined(HTML.Attribute.STYLE)) {
                  String var4 = (String)var2.getAttribute(HTML.Attribute.STYLE);
                  var2.removeAttribute(HTML.Attribute.STYLE);
                  this.styleAttributes = HTMLDocument.this.getStyleSheet().getDeclaration(var4);
                  var2.addAttributes(this.styleAttributes);
               } else {
                  this.styleAttributes = null;
               }

               HTMLDocument.HTMLReader.TagAction var5 = (HTMLDocument.HTMLReader.TagAction)this.tagMap.get(var1);
               if (var5 != null) {
                  var5.start(var1, var2);
               }

            }
         }
      }

      public void handleComment(char[] var1, int var2) {
         if (this.receivedEndHTML) {
            this.addExternalComment(new String(var1));
         } else {
            if (this.inStyle) {
               if (this.styles != null) {
                  this.styles.addElement(new String(var1));
               }
            } else if (HTMLDocument.this.getPreservesUnknownTags()) {
               if (this.inBlock == 0 && (this.foundInsertTag || this.insertTag != HTML.Tag.COMMENT)) {
                  this.addExternalComment(new String(var1));
                  return;
               }

               SimpleAttributeSet var3 = new SimpleAttributeSet();
               var3.addAttribute(HTML.Attribute.COMMENT, new String(var1));
               this.addSpecialElement(HTML.Tag.COMMENT, var3);
            }

            HTMLDocument.HTMLReader.TagAction var4 = (HTMLDocument.HTMLReader.TagAction)this.tagMap.get(HTML.Tag.COMMENT);
            if (var4 != null) {
               var4.start(HTML.Tag.COMMENT, new SimpleAttributeSet());
               var4.end(HTML.Tag.COMMENT);
            }

         }
      }

      private void addExternalComment(String var1) {
         Object var2 = HTMLDocument.this.getProperty("AdditionalComments");
         if (var2 == null || var2 instanceof Vector) {
            if (var2 == null) {
               var2 = new Vector();
               HTMLDocument.this.putProperty("AdditionalComments", var2);
            }

            ((Vector)var2).addElement(var1);
         }
      }

      public void handleEndTag(HTML.Tag var1, int var2) {
         if (!this.receivedEndHTML && (!this.midInsert || this.inBody)) {
            if (var1 == HTML.Tag.HTML) {
               this.receivedEndHTML = true;
            }

            if (var1 == HTML.Tag.BODY) {
               this.inBody = false;
               if (this.midInsert) {
                  --this.inBlock;
               }
            }

            HTMLDocument.HTMLReader.TagAction var3 = (HTMLDocument.HTMLReader.TagAction)this.tagMap.get(var1);
            if (var3 != null) {
               var3.end(var1);
            }

         }
      }

      public void handleSimpleTag(HTML.Tag var1, MutableAttributeSet var2, int var3) {
         if (!this.receivedEndHTML && (!this.midInsert || this.inBody)) {
            if (this.isStyleCSS && var2.isDefined(HTML.Attribute.STYLE)) {
               String var4 = (String)var2.getAttribute(HTML.Attribute.STYLE);
               var2.removeAttribute(HTML.Attribute.STYLE);
               this.styleAttributes = HTMLDocument.this.getStyleSheet().getDeclaration(var4);
               var2.addAttributes(this.styleAttributes);
            } else {
               this.styleAttributes = null;
            }

            HTMLDocument.HTMLReader.TagAction var5 = (HTMLDocument.HTMLReader.TagAction)this.tagMap.get(var1);
            if (var5 != null) {
               var5.start(var1, var2);
               var5.end(var1);
            } else if (HTMLDocument.this.getPreservesUnknownTags()) {
               this.addSpecialElement(var1, var2);
            }

         }
      }

      public void handleEndOfLineString(String var1) {
         if (this.emptyDocument && var1 != null) {
            HTMLDocument.this.putProperty("__EndOfLine__", var1);
         }

      }

      protected void registerTag(HTML.Tag var1, HTMLDocument.HTMLReader.TagAction var2) {
         this.tagMap.put(var1, var2);
      }

      protected void pushCharacterStyle() {
         this.charAttrStack.push(this.charAttr.copyAttributes());
      }

      protected void popCharacterStyle() {
         if (!this.charAttrStack.empty()) {
            this.charAttr = (MutableAttributeSet)this.charAttrStack.peek();
            this.charAttrStack.pop();
         }

      }

      protected void textAreaContent(char[] var1) {
         try {
            this.textAreaDocument.insertString(this.textAreaDocument.getLength(), new String(var1), (AttributeSet)null);
         } catch (BadLocationException var3) {
         }

      }

      protected void preContent(char[] var1) {
         int var2 = 0;

         for(int var3 = 0; var3 < var1.length; ++var3) {
            if (var1[var3] == '\n') {
               this.addContent(var1, var2, var3 - var2 + 1);
               this.blockClose(HTML.Tag.IMPLIED);
               SimpleAttributeSet var4 = new SimpleAttributeSet();
               var4.addAttribute(CSS.Attribute.WHITE_SPACE, "pre");
               this.blockOpen(HTML.Tag.IMPLIED, var4);
               var2 = var3 + 1;
            }
         }

         if (var2 < var1.length) {
            this.addContent(var1, var2, var1.length - var2);
         }

      }

      protected void blockOpen(HTML.Tag var1, MutableAttributeSet var2) {
         if (this.impliedP) {
            this.blockClose(HTML.Tag.IMPLIED);
         }

         ++this.inBlock;
         if (this.canInsertTag(var1, var2, true)) {
            if (var2.isDefined(IMPLIED)) {
               var2.removeAttribute(IMPLIED);
            }

            this.lastWasNewline = false;
            var2.addAttribute(StyleConstants.NameAttribute, var1);
            DefaultStyledDocument.ElementSpec var3 = new DefaultStyledDocument.ElementSpec(var2.copyAttributes(), (short)1);
            this.parseBuffer.addElement(var3);
         }
      }

      protected void blockClose(HTML.Tag var1) {
         --this.inBlock;
         if (this.foundInsertTag) {
            if (!this.lastWasNewline) {
               this.pushCharacterStyle();
               this.charAttr.addAttribute("CR", Boolean.TRUE);
               this.addContent(HTMLDocument.NEWLINE, 0, 1, true);
               this.popCharacterStyle();
               this.lastWasNewline = true;
            }

            if (this.impliedP) {
               this.impliedP = false;
               this.inParagraph = false;
               if (var1 != HTML.Tag.IMPLIED) {
                  this.blockClose(HTML.Tag.IMPLIED);
               }
            }

            DefaultStyledDocument.ElementSpec var2 = this.parseBuffer.size() > 0 ? (DefaultStyledDocument.ElementSpec)this.parseBuffer.lastElement() : null;
            if (var2 != null && var2.getType() == 1) {
               char[] var3 = new char[]{' '};
               this.addContent(var3, 0, 1);
            }

            DefaultStyledDocument.ElementSpec var4 = new DefaultStyledDocument.ElementSpec((AttributeSet)null, (short)2);
            this.parseBuffer.addElement(var4);
         }
      }

      protected void addContent(char[] var1, int var2, int var3) {
         this.addContent(var1, var2, var3, true);
      }

      protected void addContent(char[] var1, int var2, int var3, boolean var4) {
         if (this.foundInsertTag) {
            if (var4 && !this.inParagraph && !this.inPre) {
               this.blockOpen(HTML.Tag.IMPLIED, new SimpleAttributeSet());
               this.inParagraph = true;
               this.impliedP = true;
            }

            this.emptyAnchor = false;
            this.charAttr.addAttribute(StyleConstants.NameAttribute, HTML.Tag.CONTENT);
            AttributeSet var5 = this.charAttr.copyAttributes();
            DefaultStyledDocument.ElementSpec var6 = new DefaultStyledDocument.ElementSpec(var5, (short)3, var1, var2, var3);
            this.parseBuffer.addElement(var6);
            if (this.parseBuffer.size() > this.threshold) {
               if (this.threshold <= 10000) {
                  this.threshold *= 5;
               }

               try {
                  this.flushBuffer(false);
               } catch (BadLocationException var8) {
               }
            }

            if (var3 > 0) {
               this.lastWasNewline = var1[var2 + var3 - 1] == '\n';
            }

         }
      }

      protected void addSpecialElement(HTML.Tag var1, MutableAttributeSet var2) {
         if (var1 != HTML.Tag.FRAME && !this.inParagraph && !this.inPre) {
            this.nextTagAfterPImplied = var1;
            this.blockOpen(HTML.Tag.IMPLIED, new SimpleAttributeSet());
            this.nextTagAfterPImplied = null;
            this.inParagraph = true;
            this.impliedP = true;
         }

         if (this.canInsertTag(var1, var2, var1.isBlock())) {
            if (var2.isDefined(IMPLIED)) {
               var2.removeAttribute(IMPLIED);
            }

            this.emptyAnchor = false;
            var2.addAttributes(this.charAttr);
            var2.addAttribute(StyleConstants.NameAttribute, var1);
            char[] var3 = new char[]{' '};
            DefaultStyledDocument.ElementSpec var4 = new DefaultStyledDocument.ElementSpec(var2.copyAttributes(), (short)3, var3, 0, 1);
            this.parseBuffer.addElement(var4);
            if (var1 == HTML.Tag.FRAME) {
               this.lastWasNewline = true;
            }

         }
      }

      void flushBuffer(boolean var1) throws BadLocationException {
         int var2 = HTMLDocument.this.getLength();
         int var3 = this.parseBuffer.size();
         if (var1 && (this.insertTag != null || this.insertAfterImplied) && var3 > 0) {
            this.adjustEndSpecsForPartialInsert();
            var3 = this.parseBuffer.size();
         }

         DefaultStyledDocument.ElementSpec[] var4 = new DefaultStyledDocument.ElementSpec[var3];
         this.parseBuffer.copyInto(var4);
         if (var2 == 0 && this.insertTag == null && !this.insertAfterImplied) {
            HTMLDocument.this.create(var4);
         } else {
            HTMLDocument.this.insert(this.offset, var4);
         }

         this.parseBuffer.removeAllElements();
         this.offset += HTMLDocument.this.getLength() - var2;
         ++this.flushCount;
      }

      private void adjustEndSpecsForPartialInsert() {
         int var1 = this.parseBuffer.size();
         int var2;
         if (this.insertTagDepthDelta < 0) {
            for(var2 = this.insertTagDepthDelta; var2 < 0 && var1 >= 0 && ((DefaultStyledDocument.ElementSpec)this.parseBuffer.elementAt(var1 - 1)).getType() == 2; ++var2) {
               --var1;
               this.parseBuffer.removeElementAt(var1);
            }
         }

         if (this.flushCount == 0 && (!this.insertAfterImplied || !this.wantsTrailingNewline)) {
            var2 = 0;
            if (this.pushDepth > 0 && ((DefaultStyledDocument.ElementSpec)this.parseBuffer.elementAt(0)).getType() == 3) {
               ++var2;
            }

            var2 += this.popDepth + this.pushDepth;
            int var3 = 0;

            int var4;
            for(var4 = var2; var2 < var1 && ((DefaultStyledDocument.ElementSpec)this.parseBuffer.elementAt(var2)).getType() == 3; ++var3) {
               ++var2;
            }

            if (var3 > 1) {
               while(var2 < var1 && ((DefaultStyledDocument.ElementSpec)this.parseBuffer.elementAt(var2)).getType() == 2) {
                  ++var2;
               }

               if (var2 == var1) {
                  char[] var5 = ((DefaultStyledDocument.ElementSpec)this.parseBuffer.elementAt(var4 + var3 - 1)).getArray();
                  if (var5.length == 1 && var5[0] == HTMLDocument.NEWLINE[0]) {
                     var2 = var4 + var3 - 1;

                     while(var1 > var2) {
                        --var1;
                        this.parseBuffer.removeElementAt(var1);
                     }
                  }
               }
            }
         }

         if (this.wantsTrailingNewline) {
            for(var2 = this.parseBuffer.size() - 1; var2 >= 0; --var2) {
               DefaultStyledDocument.ElementSpec var6 = (DefaultStyledDocument.ElementSpec)this.parseBuffer.elementAt(var2);
               if (var6.getType() == 3) {
                  if (var6.getArray()[var6.getLength() - 1] != '\n') {
                     SimpleAttributeSet var7 = new SimpleAttributeSet();
                     var7.addAttribute(StyleConstants.NameAttribute, HTML.Tag.CONTENT);
                     this.parseBuffer.insertElementAt(new DefaultStyledDocument.ElementSpec(var7, (short)3, HTMLDocument.NEWLINE, 0, 1), var2 + 1);
                  }
                  break;
               }
            }
         }

      }

      void addCSSRules(String var1) {
         StyleSheet var2 = HTMLDocument.this.getStyleSheet();
         var2.addRule(var1);
      }

      void linkCSSStyleSheet(String var1) {
         URL var2;
         try {
            var2 = new URL(HTMLDocument.this.base, var1);
         } catch (MalformedURLException var6) {
            try {
               var2 = new URL(var1);
            } catch (MalformedURLException var5) {
               var2 = null;
            }
         }

         if (var2 != null) {
            HTMLDocument.this.getStyleSheet().importStyleSheet(var2);
         }

      }

      private boolean canInsertTag(HTML.Tag var1, AttributeSet var2, boolean var3) {
         if (!this.foundInsertTag) {
            boolean var4 = var1 == HTML.Tag.IMPLIED && !this.inParagraph && !this.inPre;
            if (var4 && this.nextTagAfterPImplied != null) {
               if (this.insertTag != null) {
                  boolean var5 = this.isInsertTag(this.nextTagAfterPImplied);
                  if (!var5 || !this.insertInsertTag) {
                     return false;
                  }
               }
            } else if (this.insertTag != null && !this.isInsertTag(var1) || this.insertAfterImplied && (var2 == null || var2.isDefined(IMPLIED) || var1 == HTML.Tag.IMPLIED)) {
               return false;
            }

            this.foundInsertTag(var3);
            if (!this.insertInsertTag) {
               return false;
            }
         }

         return true;
      }

      private boolean isInsertTag(HTML.Tag var1) {
         return this.insertTag == var1;
      }

      private void foundInsertTag(boolean var1) {
         this.foundInsertTag = true;
         if (!this.insertAfterImplied && (this.popDepth > 0 || this.pushDepth > 0)) {
            try {
               if (this.offset == 0 || !HTMLDocument.this.getText(this.offset - 1, 1).equals("\n")) {
                  SimpleAttributeSet var2 = null;
                  boolean var3 = true;
                  if (this.offset != 0) {
                     Element var4 = HTMLDocument.this.getCharacterElement(this.offset - 1);
                     AttributeSet var5 = var4.getAttributes();
                     if (var5.isDefined(StyleConstants.ComposedTextAttribute)) {
                        var3 = false;
                     } else {
                        Object var6 = var5.getAttribute(StyleConstants.NameAttribute);
                        if (var6 instanceof HTML.Tag) {
                           HTML.Tag var7 = (HTML.Tag)var6;
                           if (var7 == HTML.Tag.IMG || var7 == HTML.Tag.HR || var7 == HTML.Tag.COMMENT || var7 instanceof HTML.UnknownTag) {
                              var3 = false;
                           }
                        }
                     }
                  }

                  if (!var3) {
                     var2 = new SimpleAttributeSet();
                     ((SimpleAttributeSet)var2).addAttribute(StyleConstants.NameAttribute, HTML.Tag.CONTENT);
                  }

                  DefaultStyledDocument.ElementSpec var11 = new DefaultStyledDocument.ElementSpec(var2, (short)3, HTMLDocument.NEWLINE, 0, HTMLDocument.NEWLINE.length);
                  if (var3) {
                     var11.setDirection((short)4);
                  }

                  this.parseBuffer.addElement(var11);
               }
            } catch (BadLocationException var8) {
            }
         }

         int var9;
         for(var9 = 0; var9 < this.popDepth; ++var9) {
            this.parseBuffer.addElement(new DefaultStyledDocument.ElementSpec((AttributeSet)null, (short)2));
         }

         for(var9 = 0; var9 < this.pushDepth; ++var9) {
            DefaultStyledDocument.ElementSpec var10 = new DefaultStyledDocument.ElementSpec((AttributeSet)null, (short)1);
            var10.setDirection((short)5);
            this.parseBuffer.addElement(var10);
         }

         this.insertTagDepthDelta = this.depthTo(Math.max(0, this.offset - 1)) - this.popDepth + this.pushDepth - this.inBlock;
         if (var1) {
            ++this.insertTagDepthDelta;
         } else {
            --this.insertTagDepthDelta;
            this.inParagraph = true;
            this.lastWasNewline = false;
         }

      }

      public class FormAction extends HTMLDocument.HTMLReader.SpecialAction {
         Object selectModel;
         int optionCount;

         public FormAction() {
            super();
         }

         public void start(HTML.Tag var1, MutableAttributeSet var2) {
            if (var1 == HTML.Tag.INPUT) {
               String var3 = (String)var2.getAttribute(HTML.Attribute.TYPE);
               if (var3 == null) {
                  var3 = "text";
                  var2.addAttribute(HTML.Attribute.TYPE, "text");
               }

               this.setModel(var3, var2);
            } else if (var1 == HTML.Tag.TEXTAREA) {
               HTMLReader.this.inTextArea = true;
               HTMLReader.this.textAreaDocument = new TextAreaDocument();
               var2.addAttribute(StyleConstants.ModelAttribute, HTMLReader.this.textAreaDocument);
            } else if (var1 == HTML.Tag.SELECT) {
               int var6 = HTML.getIntegerAttributeValue(var2, HTML.Attribute.SIZE, 1);
               boolean var4 = var2.getAttribute(HTML.Attribute.MULTIPLE) != null;
               if (var6 <= 1 && !var4) {
                  this.selectModel = new OptionComboBoxModel();
               } else {
                  OptionListModel var5 = new OptionListModel();
                  if (var4) {
                     var5.setSelectionMode(2);
                  }

                  this.selectModel = var5;
               }

               var2.addAttribute(StyleConstants.ModelAttribute, this.selectModel);
            }

            if (var1 == HTML.Tag.OPTION) {
               HTMLReader.this.option = new Option(var2);
               if (this.selectModel instanceof OptionListModel) {
                  OptionListModel var7 = (OptionListModel)this.selectModel;
                  var7.addElement(HTMLReader.this.option);
                  if (HTMLReader.this.option.isSelected()) {
                     var7.addSelectionInterval(this.optionCount, this.optionCount);
                     var7.setInitialSelection(this.optionCount);
                  }
               } else if (this.selectModel instanceof OptionComboBoxModel) {
                  OptionComboBoxModel var8 = (OptionComboBoxModel)this.selectModel;
                  var8.addElement(HTMLReader.this.option);
                  if (HTMLReader.this.option.isSelected()) {
                     var8.setSelectedItem(HTMLReader.this.option);
                     var8.setInitialSelection(HTMLReader.this.option);
                  }
               }

               ++this.optionCount;
            } else {
               super.start(var1, var2);
            }

         }

         public void end(HTML.Tag var1) {
            if (var1 == HTML.Tag.OPTION) {
               HTMLReader.this.option = null;
            } else {
               if (var1 == HTML.Tag.SELECT) {
                  this.selectModel = null;
                  this.optionCount = 0;
               } else if (var1 == HTML.Tag.TEXTAREA) {
                  HTMLReader.this.inTextArea = false;
                  HTMLReader.this.textAreaDocument.storeInitialText();
               }

               super.end(var1);
            }

         }

         void setModel(String var1, MutableAttributeSet var2) {
            if (!var1.equals("submit") && !var1.equals("reset") && !var1.equals("image")) {
               if (!var1.equals("text") && !var1.equals("password")) {
                  if (var1.equals("file")) {
                     var2.addAttribute(StyleConstants.ModelAttribute, new PlainDocument());
                  } else if (var1.equals("checkbox") || var1.equals("radio")) {
                     JToggleButton.ToggleButtonModel var8 = new JToggleButton.ToggleButtonModel();
                     if (var1.equals("radio")) {
                        String var9 = (String)var2.getAttribute(HTML.Attribute.NAME);
                        if (HTMLDocument.this.radioButtonGroupsMap == null) {
                           HTMLDocument.this.radioButtonGroupsMap = new HashMap();
                        }

                        ButtonGroup var10 = (ButtonGroup)HTMLDocument.this.radioButtonGroupsMap.get(var9);
                        if (var10 == null) {
                           var10 = new ButtonGroup();
                           HTMLDocument.this.radioButtonGroupsMap.put(var9, var10);
                        }

                        var8.setGroup(var10);
                     }

                     boolean var11 = var2.getAttribute(HTML.Attribute.CHECKED) != null;
                     var8.setSelected(var11);
                     var2.addAttribute(StyleConstants.ModelAttribute, var8);
                  }
               } else {
                  int var3 = HTML.getIntegerAttributeValue(var2, HTML.Attribute.MAXLENGTH, -1);
                  Object var4;
                  if (var3 > 0) {
                     var4 = new HTMLDocument.FixedLengthDocument(var3);
                  } else {
                     var4 = new PlainDocument();
                  }

                  String var5 = (String)var2.getAttribute(HTML.Attribute.VALUE);

                  try {
                     ((Document)var4).insertString(0, var5, (AttributeSet)null);
                  } catch (BadLocationException var7) {
                  }

                  var2.addAttribute(StyleConstants.ModelAttribute, var4);
               }
            } else {
               var2.addAttribute(StyleConstants.ModelAttribute, new DefaultButtonModel());
            }

         }
      }

      class ObjectAction extends HTMLDocument.HTMLReader.SpecialAction {
         ObjectAction() {
            super();
         }

         public void start(HTML.Tag var1, MutableAttributeSet var2) {
            if (var1 == HTML.Tag.PARAM) {
               this.addParameter(var2);
            } else {
               super.start(var1, var2);
            }

         }

         public void end(HTML.Tag var1) {
            if (var1 != HTML.Tag.PARAM) {
               super.end(var1);
            }

         }

         void addParameter(AttributeSet var1) {
            String var2 = (String)var1.getAttribute(HTML.Attribute.NAME);
            String var3 = (String)var1.getAttribute(HTML.Attribute.VALUE);
            if (var2 != null && var3 != null) {
               DefaultStyledDocument.ElementSpec var4 = (DefaultStyledDocument.ElementSpec)HTMLReader.this.parseBuffer.lastElement();
               MutableAttributeSet var5 = (MutableAttributeSet)var4.getAttributes();
               var5.addAttribute(var2, var3);
            }

         }
      }

      class BaseAction extends HTMLDocument.HTMLReader.TagAction {
         BaseAction() {
            super();
         }

         public void start(HTML.Tag var1, MutableAttributeSet var2) {
            String var3 = (String)var2.getAttribute(HTML.Attribute.HREF);
            if (var3 != null) {
               try {
                  URL var4 = new URL(HTMLDocument.this.base, var3);
                  HTMLDocument.this.setBase(var4);
                  HTMLDocument.this.hasBaseTag = true;
               } catch (MalformedURLException var5) {
               }
            }

            HTMLDocument.this.baseTarget = (String)var2.getAttribute(HTML.Attribute.TARGET);
         }
      }

      class TitleAction extends HTMLDocument.HTMLReader.HiddenAction {
         TitleAction() {
            super();
         }

         public void start(HTML.Tag var1, MutableAttributeSet var2) {
            HTMLReader.this.inTitle = true;
            super.start(var1, var2);
         }

         public void end(HTML.Tag var1) {
            HTMLReader.this.inTitle = false;
            super.end(var1);
         }

         boolean isEmpty(HTML.Tag var1) {
            return false;
         }
      }

      class AnchorAction extends HTMLDocument.HTMLReader.CharacterAction {
         AnchorAction() {
            super();
         }

         public void start(HTML.Tag var1, MutableAttributeSet var2) {
            HTMLReader.this.emptyAnchor = true;
            super.start(var1, var2);
         }

         public void end(HTML.Tag var1) {
            if (HTMLReader.this.emptyAnchor) {
               char[] var2 = new char[]{'\n'};
               HTMLReader.this.addContent(var2, 0, 1);
            }

            super.end(var1);
         }
      }

      class ConvertAction extends HTMLDocument.HTMLReader.TagAction {
         ConvertAction() {
            super();
         }

         public void start(HTML.Tag var1, MutableAttributeSet var2) {
            HTMLReader.this.pushCharacterStyle();
            if (!HTMLReader.this.foundInsertTag) {
               boolean var3 = HTMLReader.this.canInsertTag(var1, var2, false);
               if (HTMLReader.this.foundInsertTag && !HTMLReader.this.inParagraph) {
                  HTMLReader.this.inParagraph = HTMLReader.this.impliedP = true;
               }

               if (!var3) {
                  return;
               }
            }

            if (var2.isDefined(HTMLEditorKit.ParserCallback.IMPLIED)) {
               var2.removeAttribute(HTMLEditorKit.ParserCallback.IMPLIED);
            }

            if (HTMLReader.this.styleAttributes != null) {
               HTMLReader.this.charAttr.addAttributes(HTMLReader.this.styleAttributes);
            }

            HTMLReader.this.charAttr.addAttribute(var1, var2.copyAttributes());
            StyleSheet var7 = HTMLDocument.this.getStyleSheet();
            if (var1 == HTML.Tag.B) {
               var7.addCSSAttribute(HTMLReader.this.charAttr, CSS.Attribute.FONT_WEIGHT, "bold");
            } else if (var1 == HTML.Tag.I) {
               var7.addCSSAttribute(HTMLReader.this.charAttr, CSS.Attribute.FONT_STYLE, "italic");
            } else {
               Object var4;
               String var5;
               if (var1 == HTML.Tag.U) {
                  var4 = HTMLReader.this.charAttr.getAttribute(CSS.Attribute.TEXT_DECORATION);
                  var5 = "underline";
                  var5 = var4 != null ? var5 + "," + var4.toString() : var5;
                  var7.addCSSAttribute(HTMLReader.this.charAttr, CSS.Attribute.TEXT_DECORATION, var5);
               } else if (var1 == HTML.Tag.STRIKE) {
                  var4 = HTMLReader.this.charAttr.getAttribute(CSS.Attribute.TEXT_DECORATION);
                  var5 = "line-through";
                  var5 = var4 != null ? var5 + "," + var4.toString() : var5;
                  var7.addCSSAttribute(HTMLReader.this.charAttr, CSS.Attribute.TEXT_DECORATION, var5);
               } else if (var1 == HTML.Tag.SUP) {
                  var4 = HTMLReader.this.charAttr.getAttribute(CSS.Attribute.VERTICAL_ALIGN);
                  var5 = "sup";
                  var5 = var4 != null ? var5 + "," + var4.toString() : var5;
                  var7.addCSSAttribute(HTMLReader.this.charAttr, CSS.Attribute.VERTICAL_ALIGN, var5);
               } else if (var1 == HTML.Tag.SUB) {
                  var4 = HTMLReader.this.charAttr.getAttribute(CSS.Attribute.VERTICAL_ALIGN);
                  var5 = "sub";
                  var5 = var4 != null ? var5 + "," + var4.toString() : var5;
                  var7.addCSSAttribute(HTMLReader.this.charAttr, CSS.Attribute.VERTICAL_ALIGN, var5);
               } else if (var1 == HTML.Tag.FONT) {
                  String var8 = (String)var2.getAttribute(HTML.Attribute.COLOR);
                  if (var8 != null) {
                     var7.addCSSAttribute(HTMLReader.this.charAttr, CSS.Attribute.COLOR, var8);
                  }

                  var5 = (String)var2.getAttribute(HTML.Attribute.FACE);
                  if (var5 != null) {
                     var7.addCSSAttribute(HTMLReader.this.charAttr, CSS.Attribute.FONT_FAMILY, var5);
                  }

                  String var6 = (String)var2.getAttribute(HTML.Attribute.SIZE);
                  if (var6 != null) {
                     var7.addCSSAttributeFromHTML(HTMLReader.this.charAttr, CSS.Attribute.FONT_SIZE, var6);
                  }
               }
            }

         }

         public void end(HTML.Tag var1) {
            HTMLReader.this.popCharacterStyle();
         }
      }

      public class CharacterAction extends HTMLDocument.HTMLReader.TagAction {
         public CharacterAction() {
            super();
         }

         public void start(HTML.Tag var1, MutableAttributeSet var2) {
            HTMLReader.this.pushCharacterStyle();
            if (!HTMLReader.this.foundInsertTag) {
               boolean var3 = HTMLReader.this.canInsertTag(var1, var2, false);
               if (HTMLReader.this.foundInsertTag && !HTMLReader.this.inParagraph) {
                  HTMLReader.this.inParagraph = HTMLReader.this.impliedP = true;
               }

               if (!var3) {
                  return;
               }
            }

            if (var2.isDefined(HTMLEditorKit.ParserCallback.IMPLIED)) {
               var2.removeAttribute(HTMLEditorKit.ParserCallback.IMPLIED);
            }

            HTMLReader.this.charAttr.addAttribute(var1, var2.copyAttributes());
            if (HTMLReader.this.styleAttributes != null) {
               HTMLReader.this.charAttr.addAttributes(HTMLReader.this.styleAttributes);
            }

         }

         public void end(HTML.Tag var1) {
            HTMLReader.this.popCharacterStyle();
         }
      }

      public class PreAction extends HTMLDocument.HTMLReader.BlockAction {
         public PreAction() {
            super();
         }

         public void start(HTML.Tag var1, MutableAttributeSet var2) {
            HTMLReader.this.inPre = true;
            HTMLReader.this.blockOpen(var1, var2);
            var2.addAttribute(CSS.Attribute.WHITE_SPACE, "pre");
            HTMLReader.this.blockOpen(HTML.Tag.IMPLIED, var2);
         }

         public void end(HTML.Tag var1) {
            HTMLReader.this.blockClose(HTML.Tag.IMPLIED);
            HTMLReader.this.inPre = false;
            HTMLReader.this.blockClose(var1);
         }
      }

      class StyleAction extends HTMLDocument.HTMLReader.TagAction {
         StyleAction() {
            super();
         }

         public void start(HTML.Tag var1, MutableAttributeSet var2) {
            if (HTMLReader.this.inHead) {
               if (HTMLReader.this.styles == null) {
                  HTMLReader.this.styles = new Vector(3);
               }

               HTMLReader.this.styles.addElement(var1);
               HTMLReader.this.styles.addElement(var2.getAttribute(HTML.Attribute.TYPE));
               HTMLReader.this.inStyle = true;
            }

         }

         public void end(HTML.Tag var1) {
            HTMLReader.this.inStyle = false;
         }

         boolean isEmpty(HTML.Tag var1) {
            return false;
         }
      }

      class AreaAction extends HTMLDocument.HTMLReader.TagAction {
         AreaAction() {
            super();
         }

         public void start(HTML.Tag var1, MutableAttributeSet var2) {
            if (HTMLReader.this.lastMap != null) {
               HTMLReader.this.lastMap.addArea(var2.copyAttributes());
            }

         }

         public void end(HTML.Tag var1) {
         }
      }

      class MapAction extends HTMLDocument.HTMLReader.TagAction {
         MapAction() {
            super();
         }

         public void start(HTML.Tag var1, MutableAttributeSet var2) {
            HTMLReader.this.lastMap = new Map((String)var2.getAttribute(HTML.Attribute.NAME));
            HTMLDocument.this.addMap(HTMLReader.this.lastMap);
         }

         public void end(HTML.Tag var1) {
         }
      }

      class LinkAction extends HTMLDocument.HTMLReader.HiddenAction {
         LinkAction() {
            super();
         }

         public void start(HTML.Tag var1, MutableAttributeSet var2) {
            String var3 = (String)var2.getAttribute(HTML.Attribute.REL);
            if (var3 != null) {
               var3 = var3.toLowerCase();
               if (var3.equals("stylesheet") || var3.equals("alternate stylesheet")) {
                  if (HTMLReader.this.styles == null) {
                     HTMLReader.this.styles = new Vector(3);
                  }

                  HTMLReader.this.styles.addElement(var1);
                  HTMLReader.this.styles.addElement(var2.copyAttributes());
               }
            }

            super.start(var1, var2);
         }
      }

      class HeadAction extends HTMLDocument.HTMLReader.BlockAction {
         HeadAction() {
            super();
         }

         public void start(HTML.Tag var1, MutableAttributeSet var2) {
            HTMLReader.this.inHead = true;
            if (HTMLReader.this.insertTag == null && !HTMLReader.this.insertAfterImplied || HTMLReader.this.insertTag == HTML.Tag.HEAD || HTMLReader.this.insertAfterImplied && (HTMLReader.this.foundInsertTag || !var2.isDefined(HTMLEditorKit.ParserCallback.IMPLIED))) {
               super.start(var1, var2);
            }

         }

         public void end(HTML.Tag var1) {
            HTMLReader.this.inHead = HTMLReader.this.inStyle = false;
            if (HTMLReader.this.styles != null) {
               boolean var2 = HTMLReader.this.isStyleCSS;
               int var3 = 0;
               int var4 = HTMLReader.this.styles.size();

               label53:
               while(true) {
                  while(true) {
                     if (var3 >= var4) {
                        break label53;
                     }

                     Object var5 = HTMLReader.this.styles.elementAt(var3);
                     if (var5 == HTML.Tag.LINK) {
                        ++var3;
                        this.handleLink((AttributeSet)HTMLReader.this.styles.elementAt(var3));
                        ++var3;
                     } else {
                        ++var3;
                        String var6 = (String)HTMLReader.this.styles.elementAt(var3);
                        boolean var7 = var6 == null ? var2 : var6.equals("text/css");

                        while(true) {
                           ++var3;
                           if (var3 >= var4 || !(HTMLReader.this.styles.elementAt(var3) instanceof String)) {
                              break;
                           }

                           if (var7) {
                              HTMLReader.this.addCSSRules((String)HTMLReader.this.styles.elementAt(var3));
                           }
                        }
                     }
                  }
               }
            }

            if (HTMLReader.this.insertTag == null && !HTMLReader.this.insertAfterImplied || HTMLReader.this.insertTag == HTML.Tag.HEAD || HTMLReader.this.insertAfterImplied && HTMLReader.this.foundInsertTag) {
               super.end(var1);
            }

         }

         boolean isEmpty(HTML.Tag var1) {
            return false;
         }

         private void handleLink(AttributeSet var1) {
            String var2 = (String)var1.getAttribute(HTML.Attribute.TYPE);
            if (var2 == null) {
               var2 = HTMLDocument.this.getDefaultStyleSheetType();
            }

            if (var2.equals("text/css")) {
               String var3 = (String)var1.getAttribute(HTML.Attribute.REL);
               String var4 = (String)var1.getAttribute(HTML.Attribute.TITLE);
               String var5 = (String)var1.getAttribute(HTML.Attribute.MEDIA);
               if (var5 == null) {
                  var5 = "all";
               } else {
                  var5 = var5.toLowerCase();
               }

               if (var3 != null) {
                  var3 = var3.toLowerCase();
                  if ((var5.indexOf("all") != -1 || var5.indexOf("screen") != -1) && (var3.equals("stylesheet") || var3.equals("alternate stylesheet") && var4.equals(HTMLReader.this.defaultStyle))) {
                     HTMLReader.this.linkCSSStyleSheet((String)var1.getAttribute(HTML.Attribute.HREF));
                  }
               }
            }

         }
      }

      class MetaAction extends HTMLDocument.HTMLReader.HiddenAction {
         MetaAction() {
            super();
         }

         public void start(HTML.Tag var1, MutableAttributeSet var2) {
            Object var3 = var2.getAttribute(HTML.Attribute.HTTPEQUIV);
            if (var3 != null) {
               String var5 = ((String)var3).toLowerCase();
               if (var5.equals("content-style-type")) {
                  String var4 = (String)var2.getAttribute(HTML.Attribute.CONTENT);
                  HTMLDocument.this.setDefaultStyleSheetType(var4);
                  HTMLReader.this.isStyleCSS = "text/css".equals(HTMLDocument.this.getDefaultStyleSheetType());
               } else if (var5.equals("default-style")) {
                  HTMLReader.this.defaultStyle = (String)var2.getAttribute(HTML.Attribute.CONTENT);
               }
            }

            super.start(var1, var2);
         }

         boolean isEmpty(HTML.Tag var1) {
            return true;
         }
      }

      public class HiddenAction extends HTMLDocument.HTMLReader.TagAction {
         public HiddenAction() {
            super();
         }

         public void start(HTML.Tag var1, MutableAttributeSet var2) {
            HTMLReader.this.addSpecialElement(var1, var2);
         }

         public void end(HTML.Tag var1) {
            if (!this.isEmpty(var1)) {
               SimpleAttributeSet var2 = new SimpleAttributeSet();
               var2.addAttribute(HTML.Attribute.ENDTAG, "true");
               HTMLReader.this.addSpecialElement(var1, var2);
            }

         }

         boolean isEmpty(HTML.Tag var1) {
            return var1 != HTML.Tag.APPLET && var1 != HTML.Tag.SCRIPT;
         }
      }

      public class IsindexAction extends HTMLDocument.HTMLReader.TagAction {
         public IsindexAction() {
            super();
         }

         public void start(HTML.Tag var1, MutableAttributeSet var2) {
            HTMLReader.this.blockOpen(HTML.Tag.IMPLIED, new SimpleAttributeSet());
            HTMLReader.this.addSpecialElement(var1, var2);
            HTMLReader.this.blockClose(HTML.Tag.IMPLIED);
         }
      }

      public class SpecialAction extends HTMLDocument.HTMLReader.TagAction {
         public SpecialAction() {
            super();
         }

         public void start(HTML.Tag var1, MutableAttributeSet var2) {
            HTMLReader.this.addSpecialElement(var1, var2);
         }
      }

      public class ParagraphAction extends HTMLDocument.HTMLReader.BlockAction {
         public ParagraphAction() {
            super();
         }

         public void start(HTML.Tag var1, MutableAttributeSet var2) {
            super.start(var1, var2);
            HTMLReader.this.inParagraph = true;
         }

         public void end(HTML.Tag var1) {
            super.end(var1);
            HTMLReader.this.inParagraph = false;
         }
      }

      private class FormTagAction extends HTMLDocument.HTMLReader.BlockAction {
         private FormTagAction() {
            super();
         }

         public void start(HTML.Tag var1, MutableAttributeSet var2) {
            super.start(var1, var2);
            HTMLDocument.this.radioButtonGroupsMap = new HashMap();
         }

         public void end(HTML.Tag var1) {
            super.end(var1);
            HTMLDocument.this.radioButtonGroupsMap = null;
         }

         // $FF: synthetic method
         FormTagAction(Object var2) {
            this();
         }
      }

      public class BlockAction extends HTMLDocument.HTMLReader.TagAction {
         public BlockAction() {
            super();
         }

         public void start(HTML.Tag var1, MutableAttributeSet var2) {
            HTMLReader.this.blockOpen(var1, var2);
         }

         public void end(HTML.Tag var1) {
            HTMLReader.this.blockClose(var1);
         }
      }

      public class TagAction {
         public void start(HTML.Tag var1, MutableAttributeSet var2) {
         }

         public void end(HTML.Tag var1) {
         }
      }
   }

   static class LeafIterator extends HTMLDocument.Iterator {
      private int endOffset;
      private HTML.Tag tag;
      private ElementIterator pos;

      LeafIterator(HTML.Tag var1, Document var2) {
         this.tag = var1;
         this.pos = new ElementIterator(var2);
         this.endOffset = 0;
         this.next();
      }

      public AttributeSet getAttributes() {
         Element var1 = this.pos.current();
         if (var1 != null) {
            AttributeSet var2 = (AttributeSet)var1.getAttributes().getAttribute(this.tag);
            if (var2 == null) {
               var2 = var1.getAttributes();
            }

            return var2;
         } else {
            return null;
         }
      }

      public int getStartOffset() {
         Element var1 = this.pos.current();
         return var1 != null ? var1.getStartOffset() : -1;
      }

      public int getEndOffset() {
         return this.endOffset;
      }

      public void next() {
         this.nextLeaf(this.pos);

         for(; this.isValid(); this.nextLeaf(this.pos)) {
            Element var1 = this.pos.current();
            if (var1.getStartOffset() >= this.endOffset) {
               AttributeSet var2 = this.pos.current().getAttributes();
               if (var2.isDefined(this.tag) || var2.getAttribute(StyleConstants.NameAttribute) == this.tag) {
                  this.setEndOffset();
                  break;
               }
            }
         }

      }

      public HTML.Tag getTag() {
         return this.tag;
      }

      public boolean isValid() {
         return this.pos.current() != null;
      }

      void nextLeaf(ElementIterator var1) {
         var1.next();

         while(var1.current() != null) {
            Element var2 = var1.current();
            if (var2.isLeaf()) {
               break;
            }

            var1.next();
         }

      }

      void setEndOffset() {
         AttributeSet var1 = this.getAttributes();
         this.endOffset = this.pos.current().getEndOffset();
         ElementIterator var2 = (ElementIterator)this.pos.clone();
         this.nextLeaf(var2);

         while(var2.current() != null) {
            Element var3 = var2.current();
            AttributeSet var4 = (AttributeSet)var3.getAttributes().getAttribute(this.tag);
            if (var4 == null || !var4.equals(var1)) {
               break;
            }

            this.endOffset = var3.getEndOffset();
            this.nextLeaf(var2);
         }

      }
   }

   public abstract static class Iterator {
      public abstract AttributeSet getAttributes();

      public abstract int getStartOffset();

      public abstract int getEndOffset();

      public abstract void next();

      public abstract boolean isValid();

      public abstract HTML.Tag getTag();
   }
}
