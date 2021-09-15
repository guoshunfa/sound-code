package javax.swing.text.html;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.io.Writer;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleAction;
import javax.accessibility.AccessibleContext;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JViewport;
import javax.swing.SizeRequirements;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.plaf.TextUI;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.BoxView;
import javax.swing.text.ComponentView;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.Element;
import javax.swing.text.ElementIterator;
import javax.swing.text.Highlighter;
import javax.swing.text.IconView;
import javax.swing.text.JTextComponent;
import javax.swing.text.LabelView;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.Position;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.TextAction;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import sun.awt.AppContext;

public class HTMLEditorKit extends StyledEditorKit implements Accessible {
   private JEditorPane theEditor;
   public static final String DEFAULT_CSS = "default.css";
   private AccessibleContext accessibleContext;
   private static final Cursor MoveCursor = Cursor.getPredefinedCursor(12);
   private static final Cursor DefaultCursor = Cursor.getPredefinedCursor(0);
   private static final ViewFactory defaultFactory = new HTMLEditorKit.HTMLFactory();
   MutableAttributeSet input;
   private static final Object DEFAULT_STYLES_KEY = new Object();
   private HTMLEditorKit.LinkController linkHandler = new HTMLEditorKit.LinkController();
   private static HTMLEditorKit.Parser defaultParser = null;
   private Cursor defaultCursor;
   private Cursor linkCursor;
   private boolean isAutoFormSubmission;
   public static final String BOLD_ACTION = "html-bold-action";
   public static final String ITALIC_ACTION = "html-italic-action";
   public static final String PARA_INDENT_LEFT = "html-para-indent-left";
   public static final String PARA_INDENT_RIGHT = "html-para-indent-right";
   public static final String FONT_CHANGE_BIGGER = "html-font-bigger";
   public static final String FONT_CHANGE_SMALLER = "html-font-smaller";
   public static final String COLOR_ACTION = "html-color-action";
   public static final String LOGICAL_STYLE_ACTION = "html-logical-style-action";
   public static final String IMG_ALIGN_TOP = "html-image-align-top";
   public static final String IMG_ALIGN_MIDDLE = "html-image-align-middle";
   public static final String IMG_ALIGN_BOTTOM = "html-image-align-bottom";
   public static final String IMG_BORDER = "html-image-border";
   private static final String INSERT_TABLE_HTML = "<table border=1><tr><td></td></tr></table>";
   private static final String INSERT_UL_HTML = "<ul><li></li></ul>";
   private static final String INSERT_OL_HTML = "<ol><li></li></ol>";
   private static final String INSERT_HR_HTML = "<hr>";
   private static final String INSERT_PRE_HTML = "<pre></pre>";
   private static final HTMLEditorKit.NavigateLinkAction nextLinkAction = new HTMLEditorKit.NavigateLinkAction("next-link-action");
   private static final HTMLEditorKit.NavigateLinkAction previousLinkAction = new HTMLEditorKit.NavigateLinkAction("previous-link-action");
   private static final HTMLEditorKit.ActivateLinkAction activateLinkAction = new HTMLEditorKit.ActivateLinkAction("activate-link-action");
   private static final Action[] defaultActions;
   private boolean foundLink;
   private int prevHypertextOffset;
   private Object linkNavigationTag;

   public HTMLEditorKit() {
      this.defaultCursor = DefaultCursor;
      this.linkCursor = MoveCursor;
      this.isAutoFormSubmission = true;
      this.foundLink = false;
      this.prevHypertextOffset = -1;
   }

   public String getContentType() {
      return "text/html";
   }

   public ViewFactory getViewFactory() {
      return defaultFactory;
   }

   public Document createDefaultDocument() {
      StyleSheet var1 = this.getStyleSheet();
      StyleSheet var2 = new StyleSheet();
      var2.addStyleSheet(var1);
      HTMLDocument var3 = new HTMLDocument(var2);
      var3.setParser(this.getParser());
      var3.setAsynchronousLoadPriority(4);
      var3.setTokenThreshold(100);
      return var3;
   }

   private HTMLEditorKit.Parser ensureParser(HTMLDocument var1) throws IOException {
      HTMLEditorKit.Parser var2 = var1.getParser();
      if (var2 == null) {
         var2 = this.getParser();
      }

      if (var2 == null) {
         throw new IOException("Can't load parser");
      } else {
         return var2;
      }
   }

   public void read(Reader var1, Document var2, int var3) throws IOException, BadLocationException {
      if (var2 instanceof HTMLDocument) {
         HTMLDocument var4 = (HTMLDocument)var2;
         if (var3 > var2.getLength()) {
            throw new BadLocationException("Invalid location", var3);
         }

         HTMLEditorKit.Parser var5 = this.ensureParser(var4);
         HTMLEditorKit.ParserCallback var6 = var4.getReader(var3);
         Boolean var7 = (Boolean)var2.getProperty("IgnoreCharsetDirective");
         var5.parse(var1, var6, var7 == null ? false : var7);
         var6.flush();
      } else {
         super.read(var1, var2, var3);
      }

   }

   public void insertHTML(HTMLDocument var1, int var2, String var3, int var4, int var5, HTML.Tag var6) throws BadLocationException, IOException {
      if (var2 > var1.getLength()) {
         throw new BadLocationException("Invalid location", var2);
      } else {
         HTMLEditorKit.Parser var7 = this.ensureParser(var1);
         HTMLEditorKit.ParserCallback var8 = var1.getReader(var2, var4, var5, var6);
         Boolean var9 = (Boolean)var1.getProperty("IgnoreCharsetDirective");
         var7.parse(new StringReader(var3), var8, var9 == null ? false : var9);
         var8.flush();
      }
   }

   public void write(Writer var1, Document var2, int var3, int var4) throws IOException, BadLocationException {
      if (var2 instanceof HTMLDocument) {
         HTMLWriter var5 = new HTMLWriter(var1, (HTMLDocument)var2, var3, var4);
         var5.write();
      } else if (var2 instanceof StyledDocument) {
         MinimalHTMLWriter var6 = new MinimalHTMLWriter(var1, (StyledDocument)var2, var3, var4);
         var6.write();
      } else {
         super.write(var1, var2, var3, var4);
      }

   }

   public void install(JEditorPane var1) {
      var1.addMouseListener(this.linkHandler);
      var1.addMouseMotionListener(this.linkHandler);
      var1.addCaretListener(nextLinkAction);
      super.install(var1);
      this.theEditor = var1;
   }

   public void deinstall(JEditorPane var1) {
      var1.removeMouseListener(this.linkHandler);
      var1.removeMouseMotionListener(this.linkHandler);
      var1.removeCaretListener(nextLinkAction);
      super.deinstall(var1);
      this.theEditor = null;
   }

   public void setStyleSheet(StyleSheet var1) {
      if (var1 == null) {
         AppContext.getAppContext().remove(DEFAULT_STYLES_KEY);
      } else {
         AppContext.getAppContext().put(DEFAULT_STYLES_KEY, var1);
      }

   }

   public StyleSheet getStyleSheet() {
      AppContext var1 = AppContext.getAppContext();
      StyleSheet var2 = (StyleSheet)var1.get(DEFAULT_STYLES_KEY);
      if (var2 == null) {
         var2 = new StyleSheet();
         var1.put(DEFAULT_STYLES_KEY, var2);

         try {
            InputStream var3 = getResourceAsStream("default.css");
            BufferedReader var4 = new BufferedReader(new InputStreamReader(var3, "ISO-8859-1"));
            var2.loadRules(var4, (URL)null);
            var4.close();
         } catch (Throwable var5) {
         }
      }

      return var2;
   }

   static InputStream getResourceAsStream(final String var0) {
      return (InputStream)AccessController.doPrivileged(new PrivilegedAction<InputStream>() {
         public InputStream run() {
            return HTMLEditorKit.class.getResourceAsStream(var0);
         }
      });
   }

   public Action[] getActions() {
      return TextAction.augmentList(super.getActions(), defaultActions);
   }

   protected void createInputAttributes(Element var1, MutableAttributeSet var2) {
      var2.removeAttributes((AttributeSet)var2);
      var2.addAttributes(var1.getAttributes());
      var2.removeAttribute(StyleConstants.ComposedTextAttribute);
      Object var3 = var2.getAttribute(StyleConstants.NameAttribute);
      if (var3 instanceof HTML.Tag) {
         HTML.Tag var4 = (HTML.Tag)var3;
         if (var4 == HTML.Tag.IMG) {
            var2.removeAttribute(HTML.Attribute.SRC);
            var2.removeAttribute(HTML.Attribute.HEIGHT);
            var2.removeAttribute(HTML.Attribute.WIDTH);
            var2.addAttribute(StyleConstants.NameAttribute, HTML.Tag.CONTENT);
         } else if (var4 != HTML.Tag.HR && var4 != HTML.Tag.BR) {
            if (var4 == HTML.Tag.COMMENT) {
               var2.addAttribute(StyleConstants.NameAttribute, HTML.Tag.CONTENT);
               var2.removeAttribute(HTML.Attribute.COMMENT);
            } else if (var4 == HTML.Tag.INPUT) {
               var2.addAttribute(StyleConstants.NameAttribute, HTML.Tag.CONTENT);
               var2.removeAttribute(HTML.Tag.INPUT);
            } else if (var4 instanceof HTML.UnknownTag) {
               var2.addAttribute(StyleConstants.NameAttribute, HTML.Tag.CONTENT);
               var2.removeAttribute(HTML.Attribute.ENDTAG);
            }
         } else {
            var2.addAttribute(StyleConstants.NameAttribute, HTML.Tag.CONTENT);
         }
      }

   }

   public MutableAttributeSet getInputAttributes() {
      if (this.input == null) {
         this.input = this.getStyleSheet().addStyle((String)null, (Style)null);
      }

      return this.input;
   }

   public void setDefaultCursor(Cursor var1) {
      this.defaultCursor = var1;
   }

   public Cursor getDefaultCursor() {
      return this.defaultCursor;
   }

   public void setLinkCursor(Cursor var1) {
      this.linkCursor = var1;
   }

   public Cursor getLinkCursor() {
      return this.linkCursor;
   }

   public boolean isAutoFormSubmission() {
      return this.isAutoFormSubmission;
   }

   public void setAutoFormSubmission(boolean var1) {
      this.isAutoFormSubmission = var1;
   }

   public Object clone() {
      HTMLEditorKit var1 = (HTMLEditorKit)super.clone();
      if (var1 != null) {
         var1.input = null;
         var1.linkHandler = new HTMLEditorKit.LinkController();
      }

      return var1;
   }

   protected HTMLEditorKit.Parser getParser() {
      if (defaultParser == null) {
         try {
            Class var1 = Class.forName("javax.swing.text.html.parser.ParserDelegator");
            defaultParser = (HTMLEditorKit.Parser)var1.newInstance();
         } catch (Throwable var2) {
         }
      }

      return defaultParser;
   }

   public AccessibleContext getAccessibleContext() {
      if (this.theEditor == null) {
         return null;
      } else {
         if (this.accessibleContext == null) {
            AccessibleHTML var1 = new AccessibleHTML(this.theEditor);
            this.accessibleContext = var1.getAccessibleContext();
         }

         return this.accessibleContext;
      }
   }

   private static Object getAttrValue(AttributeSet var0, HTML.Attribute var1) {
      Enumeration var2 = var0.getAttributeNames();

      while(var2.hasMoreElements()) {
         Object var3 = var2.nextElement();
         Object var4 = var0.getAttribute(var3);
         if (var4 instanceof AttributeSet) {
            Object var5 = getAttrValue((AttributeSet)var4, var1);
            if (var5 != null) {
               return var5;
            }
         } else if (var3 == var1) {
            return var4;
         }
      }

      return null;
   }

   private static int getBodyElementStart(JTextComponent var0) {
      Element var1 = var0.getDocument().getRootElements()[0];

      for(int var2 = 0; var2 < var1.getElementCount(); ++var2) {
         Element var3 = var1.getElement(var2);
         if ("body".equals(var3.getName())) {
            return var3.getStartOffset();
         }
      }

      return 0;
   }

   static {
      defaultActions = new Action[]{new HTMLEditorKit.InsertHTMLTextAction("InsertTable", "<table border=1><tr><td></td></tr></table>", HTML.Tag.BODY, HTML.Tag.TABLE), new HTMLEditorKit.InsertHTMLTextAction("InsertTableRow", "<table border=1><tr><td></td></tr></table>", HTML.Tag.TABLE, HTML.Tag.TR, HTML.Tag.BODY, HTML.Tag.TABLE), new HTMLEditorKit.InsertHTMLTextAction("InsertTableDataCell", "<table border=1><tr><td></td></tr></table>", HTML.Tag.TR, HTML.Tag.TD, HTML.Tag.BODY, HTML.Tag.TABLE), new HTMLEditorKit.InsertHTMLTextAction("InsertUnorderedList", "<ul><li></li></ul>", HTML.Tag.BODY, HTML.Tag.UL), new HTMLEditorKit.InsertHTMLTextAction("InsertUnorderedListItem", "<ul><li></li></ul>", HTML.Tag.UL, HTML.Tag.LI, HTML.Tag.BODY, HTML.Tag.UL), new HTMLEditorKit.InsertHTMLTextAction("InsertOrderedList", "<ol><li></li></ol>", HTML.Tag.BODY, HTML.Tag.OL), new HTMLEditorKit.InsertHTMLTextAction("InsertOrderedListItem", "<ol><li></li></ol>", HTML.Tag.OL, HTML.Tag.LI, HTML.Tag.BODY, HTML.Tag.OL), new HTMLEditorKit.InsertHRAction(), new HTMLEditorKit.InsertHTMLTextAction("InsertPre", "<pre></pre>", HTML.Tag.BODY, HTML.Tag.PRE), nextLinkAction, previousLinkAction, activateLinkAction, new HTMLEditorKit.BeginAction("caret-begin", false), new HTMLEditorKit.BeginAction("selection-begin", true)};
   }

   static class BeginAction extends TextAction {
      private boolean select;

      BeginAction(String var1, boolean var2) {
         super(var1);
         this.select = var2;
      }

      public void actionPerformed(ActionEvent var1) {
         JTextComponent var2 = this.getTextComponent(var1);
         int var3 = HTMLEditorKit.getBodyElementStart(var2);
         if (var2 != null) {
            if (this.select) {
               var2.moveCaretPosition(var3);
            } else {
               var2.setCaretPosition(var3);
            }
         }

      }
   }

   static class ActivateLinkAction extends TextAction {
      public ActivateLinkAction(String var1) {
         super(var1);
      }

      private void activateLink(String var1, HTMLDocument var2, JEditorPane var3, int var4) {
         try {
            URL var5 = (URL)var2.getProperty("stream");
            URL var6 = new URL(var5, var1);
            HyperlinkEvent var7 = new HyperlinkEvent(var3, HyperlinkEvent.EventType.ACTIVATED, var6, var6.toExternalForm(), var2.getCharacterElement(var4));
            var3.fireHyperlinkUpdate(var7);
         } catch (MalformedURLException var8) {
         }

      }

      private void doObjectAction(JEditorPane var1, Element var2) {
         View var3 = this.getView(var1, var2);
         if (var3 != null && var3 instanceof ObjectView) {
            Component var4 = ((ObjectView)var3).getComponent();
            if (var4 != null && var4 instanceof Accessible) {
               AccessibleContext var5 = var4.getAccessibleContext();
               if (var5 != null) {
                  AccessibleAction var6 = var5.getAccessibleAction();
                  if (var6 != null) {
                     var6.doAccessibleAction(0);
                  }
               }
            }
         }

      }

      private View getRootView(JEditorPane var1) {
         return var1.getUI().getRootView(var1);
      }

      private View getView(JEditorPane var1, Element var2) {
         Object var3 = this.lock(var1);

         View var6;
         try {
            View var4 = this.getRootView(var1);
            int var5 = var2.getStartOffset();
            if (var4 != null) {
               var6 = this.getView(var4, var2, var5);
               return var6;
            }

            var6 = null;
         } finally {
            this.unlock(var3);
         }

         return var6;
      }

      private View getView(View var1, Element var2, int var3) {
         if (var1.getElement() == var2) {
            return var1;
         } else {
            int var4 = var1.getViewIndex(var3, Position.Bias.Forward);
            return var4 != -1 && var4 < var1.getViewCount() ? this.getView(var1.getView(var4), var2, var3) : null;
         }
      }

      private Object lock(JEditorPane var1) {
         Document var2 = var1.getDocument();
         if (var2 instanceof AbstractDocument) {
            ((AbstractDocument)var2).readLock();
            return var2;
         } else {
            return null;
         }
      }

      private void unlock(Object var1) {
         if (var1 != null) {
            ((AbstractDocument)var1).readUnlock();
         }

      }

      public void actionPerformed(ActionEvent var1) {
         JTextComponent var2 = this.getTextComponent(var1);
         if (!var2.isEditable() && var2 instanceof JEditorPane) {
            JEditorPane var3 = (JEditorPane)var2;
            Document var4 = var3.getDocument();
            if (var4 != null && var4 instanceof HTMLDocument) {
               HTMLDocument var5 = (HTMLDocument)var4;
               ElementIterator var6 = new ElementIterator(var5);
               int var7 = var3.getCaretPosition();
               Object var8 = null;
               Object var9 = null;

               Element var10;
               while((var10 = var6.next()) != null) {
                  String var11 = var10.getName();
                  AttributeSet var12 = var10.getAttributes();
                  Object var13 = HTMLEditorKit.getAttrValue(var12, HTML.Attribute.HREF);
                  if (var13 != null) {
                     if (var7 >= var10.getStartOffset() && var7 <= var10.getEndOffset()) {
                        this.activateLink((String)var13, var5, var3, var7);
                        return;
                     }
                  } else if (var11.equals(HTML.Tag.OBJECT.toString())) {
                     Object var14 = HTMLEditorKit.getAttrValue(var12, HTML.Attribute.CLASSID);
                     if (var14 != null && var7 >= var10.getStartOffset() && var7 <= var10.getEndOffset()) {
                        this.doObjectAction(var3, var10);
                        return;
                     }
                  }
               }

            }
         }
      }
   }

   static class NavigateLinkAction extends TextAction implements CaretListener {
      private static final HTMLEditorKit.NavigateLinkAction.FocusHighlightPainter focusPainter = new HTMLEditorKit.NavigateLinkAction.FocusHighlightPainter((Color)null);
      private final boolean focusBack;

      public NavigateLinkAction(String var1) {
         super(var1);
         this.focusBack = "previous-link-action".equals(var1);
      }

      public void caretUpdate(CaretEvent var1) {
         Object var2 = var1.getSource();
         if (var2 instanceof JTextComponent) {
            JTextComponent var3 = (JTextComponent)var2;
            HTMLEditorKit var4 = this.getHTMLEditorKit(var3);
            if (var4 != null && var4.foundLink) {
               var4.foundLink = false;
               var3.getAccessibleContext().firePropertyChange("AccessibleHypertextOffset", var4.prevHypertextOffset, var1.getDot());
            }
         }

      }

      public void actionPerformed(ActionEvent var1) {
         JTextComponent var2 = this.getTextComponent(var1);
         if (var2 != null && !var2.isEditable()) {
            Document var3 = var2.getDocument();
            HTMLEditorKit var4 = this.getHTMLEditorKit(var2);
            if (var3 != null && var4 != null) {
               ElementIterator var5 = new ElementIterator(var3);
               int var6 = var2.getCaretPosition();
               int var7 = -1;
               int var8 = -1;

               while(true) {
                  Element var9;
                  String var10;
                  Object var12;
                  do {
                     if ((var9 = var5.next()) == null) {
                        if (this.focusBack && var7 >= 0) {
                           var4.foundLink = true;
                           var2.setCaretPosition(var7);
                           this.moveCaretPosition(var2, var4, var7, var8);
                           var4.prevHypertextOffset = var7;
                        }

                        return;
                     }

                     var10 = var9.getName();
                     AttributeSet var11 = var9.getAttributes();
                     var12 = HTMLEditorKit.getAttrValue(var11, HTML.Attribute.HREF);
                  } while(!var10.equals(HTML.Tag.OBJECT.toString()) && var12 == null);

                  int var13 = var9.getStartOffset();
                  if (this.focusBack) {
                     if (var13 >= var6 && var7 >= 0) {
                        var4.foundLink = true;
                        var2.setCaretPosition(var7);
                        this.moveCaretPosition(var2, var4, var7, var8);
                        var4.prevHypertextOffset = var7;
                        return;
                     }
                  } else if (var13 > var6) {
                     var4.foundLink = true;
                     var2.setCaretPosition(var13);
                     this.moveCaretPosition(var2, var4, var13, var9.getEndOffset());
                     var4.prevHypertextOffset = var13;
                     return;
                  }

                  var7 = var9.getStartOffset();
                  var8 = var9.getEndOffset();
               }
            }
         }
      }

      private void moveCaretPosition(JTextComponent var1, HTMLEditorKit var2, int var3, int var4) {
         Highlighter var5 = var1.getHighlighter();
         if (var5 != null) {
            int var6 = Math.min(var4, var3);
            int var7 = Math.max(var4, var3);

            try {
               if (var2.linkNavigationTag != null) {
                  var5.changeHighlight(var2.linkNavigationTag, var6, var7);
               } else {
                  var2.linkNavigationTag = var5.addHighlight(var6, var7, focusPainter);
               }
            } catch (BadLocationException var9) {
            }
         }

      }

      private HTMLEditorKit getHTMLEditorKit(JTextComponent var1) {
         if (var1 instanceof JEditorPane) {
            EditorKit var2 = ((JEditorPane)var1).getEditorKit();
            if (var2 instanceof HTMLEditorKit) {
               return (HTMLEditorKit)var2;
            }
         }

         return null;
      }

      static class FocusHighlightPainter extends DefaultHighlighter.DefaultHighlightPainter {
         FocusHighlightPainter(Color var1) {
            super(var1);
         }

         public Shape paintLayer(Graphics var1, int var2, int var3, Shape var4, JTextComponent var5, View var6) {
            Color var7 = this.getColor();
            if (var7 == null) {
               var1.setColor(var5.getSelectionColor());
            } else {
               var1.setColor(var7);
            }

            if (var2 == var6.getStartOffset() && var3 == var6.getEndOffset()) {
               Rectangle var11;
               if (var4 instanceof Rectangle) {
                  var11 = (Rectangle)var4;
               } else {
                  var11 = var4.getBounds();
               }

               var1.drawRect(var11.x, var11.y, var11.width - 1, var11.height);
               return var11;
            } else {
               try {
                  Shape var8 = var6.modelToView(var2, Position.Bias.Forward, var3, Position.Bias.Backward, var4);
                  Rectangle var9 = var8 instanceof Rectangle ? (Rectangle)var8 : var8.getBounds();
                  var1.drawRect(var9.x, var9.y, var9.width - 1, var9.height);
                  return var9;
               } catch (BadLocationException var10) {
                  return null;
               }
            }
         }
      }
   }

   static class InsertHRAction extends HTMLEditorKit.InsertHTMLTextAction {
      InsertHRAction() {
         super("InsertHR", "<hr>", (HTML.Tag)null, HTML.Tag.IMPLIED, (HTML.Tag)null, (HTML.Tag)null, false);
      }

      public void actionPerformed(ActionEvent var1) {
         JEditorPane var2 = this.getEditor(var1);
         if (var2 != null) {
            HTMLDocument var3 = this.getHTMLDocument(var2);
            int var4 = var2.getSelectionStart();
            Element var5 = var3.getParagraphElement(var4);
            if (var5.getParentElement() != null) {
               this.parentTag = (HTML.Tag)var5.getParentElement().getAttributes().getAttribute(StyleConstants.NameAttribute);
               super.actionPerformed(var1);
            }
         }

      }
   }

   public static class InsertHTMLTextAction extends HTMLEditorKit.HTMLTextAction {
      protected String html;
      protected HTML.Tag parentTag;
      protected HTML.Tag addTag;
      protected HTML.Tag alternateParentTag;
      protected HTML.Tag alternateAddTag;
      boolean adjustSelection;

      public InsertHTMLTextAction(String var1, String var2, HTML.Tag var3, HTML.Tag var4) {
         this(var1, var2, var3, var4, (HTML.Tag)null, (HTML.Tag)null);
      }

      public InsertHTMLTextAction(String var1, String var2, HTML.Tag var3, HTML.Tag var4, HTML.Tag var5, HTML.Tag var6) {
         this(var1, var2, var3, var4, var5, var6, true);
      }

      InsertHTMLTextAction(String var1, String var2, HTML.Tag var3, HTML.Tag var4, HTML.Tag var5, HTML.Tag var6, boolean var7) {
         super(var1);
         this.html = var2;
         this.parentTag = var3;
         this.addTag = var4;
         this.alternateParentTag = var5;
         this.alternateAddTag = var6;
         this.adjustSelection = var7;
      }

      protected void insertHTML(JEditorPane var1, HTMLDocument var2, int var3, String var4, int var5, int var6, HTML.Tag var7) {
         try {
            this.getHTMLEditorKit(var1).insertHTML(var2, var3, var4, var5, var6, var7);
         } catch (IOException var9) {
            throw new RuntimeException("Unable to insert: " + var9);
         } catch (BadLocationException var10) {
            throw new RuntimeException("Unable to insert: " + var10);
         }
      }

      protected void insertAtBoundary(JEditorPane var1, HTMLDocument var2, int var3, Element var4, String var5, HTML.Tag var6, HTML.Tag var7) {
         this.insertAtBoundry(var1, var2, var3, var4, var5, var6, var7);
      }

      /** @deprecated */
      @Deprecated
      protected void insertAtBoundry(JEditorPane var1, HTMLDocument var2, int var3, Element var4, String var5, HTML.Tag var6, HTML.Tag var7) {
         boolean var10 = var3 == 0;
         Element var8;
         Element var9;
         if (var3 <= 0 && var4 != null) {
            var9 = var4;
         } else {
            for(var8 = var2.getDefaultRootElement(); var8 != null && var8.getStartOffset() != var3 && !var8.isLeaf(); var8 = var8.getElement(var8.getElementIndex(var3))) {
            }

            var9 = var8 != null ? var8.getParentElement() : null;
         }

         if (var9 != null) {
            int var11 = 0;
            int var12 = 0;
            if (var10 && var4 != null) {
               for(var8 = var9; var8 != null && !var8.isLeaf(); ++var11) {
                  var8 = var8.getElement(var8.getElementIndex(var3));
               }
            } else {
               var8 = var9;
               --var3;

               while(var8 != null && !var8.isLeaf()) {
                  var8 = var8.getElement(var8.getElementIndex(var3));
                  ++var11;
               }

               var8 = var9;
               ++var3;

               while(var8 != null && var8 != var4) {
                  var8 = var8.getElement(var8.getElementIndex(var3));
                  ++var12;
               }
            }

            var11 = Math.max(0, var11 - 1);
            this.insertHTML(var1, var2, var3, var5, var11, var12, var7);
         }

      }

      boolean insertIntoTag(JEditorPane var1, HTMLDocument var2, int var3, HTML.Tag var4, HTML.Tag var5) {
         Element var6 = this.findElementMatchingTag(var2, var3, var4);
         if (var6 != null && var6.getStartOffset() == var3) {
            this.insertAtBoundary(var1, var2, var3, var6, this.html, var4, var5);
            return true;
         } else {
            if (var3 > 0) {
               int var7 = this.elementCountToTag(var2, var3 - 1, var4);
               if (var7 != -1) {
                  this.insertHTML(var1, var2, var3, this.html, var7, 0, var5);
                  return true;
               }
            }

            return false;
         }
      }

      void adjustSelection(JEditorPane var1, HTMLDocument var2, int var3, int var4) {
         int var5 = var2.getLength();
         if (var5 != var4 && var3 < var5) {
            if (var3 > 0) {
               String var6;
               try {
                  var6 = var2.getText(var3 - 1, 1);
               } catch (BadLocationException var8) {
                  var6 = null;
               }

               if (var6 != null && var6.length() > 0 && var6.charAt(0) == '\n') {
                  var1.select(var3, var3);
               } else {
                  var1.select(var3 + 1, var3 + 1);
               }
            } else {
               var1.select(1, 1);
            }
         }

      }

      public void actionPerformed(ActionEvent var1) {
         JEditorPane var2 = this.getEditor(var1);
         if (var2 != null) {
            HTMLDocument var3 = this.getHTMLDocument(var2);
            int var4 = var2.getSelectionStart();
            int var5 = var3.getLength();
            boolean var6;
            if (!this.insertIntoTag(var2, var3, var4, this.parentTag, this.addTag) && this.alternateParentTag != null) {
               var6 = this.insertIntoTag(var2, var3, var4, this.alternateParentTag, this.alternateAddTag);
            } else {
               var6 = true;
            }

            if (this.adjustSelection && var6) {
               this.adjustSelection(var2, var3, var4, var5);
            }
         }

      }
   }

   public abstract static class HTMLTextAction extends StyledEditorKit.StyledTextAction {
      public HTMLTextAction(String var1) {
         super(var1);
      }

      protected HTMLDocument getHTMLDocument(JEditorPane var1) {
         Document var2 = var1.getDocument();
         if (var2 instanceof HTMLDocument) {
            return (HTMLDocument)var2;
         } else {
            throw new IllegalArgumentException("document must be HTMLDocument");
         }
      }

      protected HTMLEditorKit getHTMLEditorKit(JEditorPane var1) {
         EditorKit var2 = var1.getEditorKit();
         if (var2 instanceof HTMLEditorKit) {
            return (HTMLEditorKit)var2;
         } else {
            throw new IllegalArgumentException("EditorKit must be HTMLEditorKit");
         }
      }

      protected Element[] getElementsAt(HTMLDocument var1, int var2) {
         return this.getElementsAt(var1.getDefaultRootElement(), var2, 0);
      }

      private Element[] getElementsAt(Element var1, int var2, int var3) {
         Element[] var4;
         if (var1.isLeaf()) {
            var4 = new Element[var3 + 1];
            var4[var3] = var1;
            return var4;
         } else {
            var4 = this.getElementsAt(var1.getElement(var1.getElementIndex(var2)), var2, var3 + 1);
            var4[var3] = var1;
            return var4;
         }
      }

      protected int elementCountToTag(HTMLDocument var1, int var2, HTML.Tag var3) {
         int var4 = -1;

         Element var5;
         for(var5 = var1.getCharacterElement(var2); var5 != null && var5.getAttributes().getAttribute(StyleConstants.NameAttribute) != var3; ++var4) {
            var5 = var5.getParentElement();
         }

         return var5 == null ? -1 : var4;
      }

      protected Element findElementMatchingTag(HTMLDocument var1, int var2, HTML.Tag var3) {
         Element var4 = var1.getDefaultRootElement();

         Element var5;
         for(var5 = null; var4 != null; var4 = var4.getElement(var4.getElementIndex(var2))) {
            if (var4.getAttributes().getAttribute(StyleConstants.NameAttribute) == var3) {
               var5 = var4;
            }
         }

         return var5;
      }
   }

   public static class HTMLFactory implements ViewFactory {
      public View create(Element var1) {
         AttributeSet var2 = var1.getAttributes();
         Object var3 = var2.getAttribute("$ename");
         Object var4 = var3 != null ? null : var2.getAttribute(StyleConstants.NameAttribute);
         if (var4 instanceof HTML.Tag) {
            HTML.Tag var5 = (HTML.Tag)var4;
            if (var5 == HTML.Tag.CONTENT) {
               return new InlineView(var1);
            }

            if (var5 == HTML.Tag.IMPLIED) {
               String var6 = (String)var1.getAttributes().getAttribute(CSS.Attribute.WHITE_SPACE);
               if (var6 != null && var6.equals("pre")) {
                  return new LineView(var1);
               }

               return new ParagraphView(var1);
            }

            if (var5 == HTML.Tag.P || var5 == HTML.Tag.H1 || var5 == HTML.Tag.H2 || var5 == HTML.Tag.H3 || var5 == HTML.Tag.H4 || var5 == HTML.Tag.H5 || var5 == HTML.Tag.H6 || var5 == HTML.Tag.DT) {
               return new ParagraphView(var1);
            }

            if (var5 == HTML.Tag.MENU || var5 == HTML.Tag.DIR || var5 == HTML.Tag.UL || var5 == HTML.Tag.OL) {
               return new ListView(var1);
            }

            if (var5 == HTML.Tag.BODY) {
               return new HTMLEditorKit.HTMLFactory.BodyBlockView(var1);
            }

            if (var5 == HTML.Tag.HTML) {
               return new BlockView(var1, 1);
            }

            if (var5 == HTML.Tag.LI || var5 == HTML.Tag.CENTER || var5 == HTML.Tag.DL || var5 == HTML.Tag.DD || var5 == HTML.Tag.DIV || var5 == HTML.Tag.BLOCKQUOTE || var5 == HTML.Tag.PRE || var5 == HTML.Tag.FORM) {
               return new BlockView(var1, 1);
            }

            if (var5 == HTML.Tag.NOFRAMES) {
               return new NoFramesView(var1, 1);
            }

            if (var5 == HTML.Tag.IMG) {
               return new ImageView(var1);
            }

            if (var5 == HTML.Tag.ISINDEX) {
               return new IsindexView(var1);
            }

            if (var5 == HTML.Tag.HR) {
               return new HRuleView(var1);
            }

            if (var5 == HTML.Tag.BR) {
               return new BRView(var1);
            }

            if (var5 == HTML.Tag.TABLE) {
               return new TableView(var1);
            }

            if (var5 == HTML.Tag.INPUT || var5 == HTML.Tag.SELECT || var5 == HTML.Tag.TEXTAREA) {
               return new FormView(var1);
            }

            if (var5 == HTML.Tag.OBJECT) {
               return new ObjectView(var1);
            }

            if (var5 == HTML.Tag.FRAMESET) {
               if (var1.getAttributes().isDefined(HTML.Attribute.ROWS)) {
                  return new FrameSetView(var1, 1);
               }

               if (var1.getAttributes().isDefined(HTML.Attribute.COLS)) {
                  return new FrameSetView(var1, 0);
               }

               throw new RuntimeException("Can't build a" + var5 + ", " + var1 + ":no ROWS or COLS defined.");
            }

            if (var5 == HTML.Tag.FRAME) {
               return new FrameView(var1);
            }

            if (var5 instanceof HTML.UnknownTag) {
               return new HiddenTagView(var1);
            }

            if (var5 == HTML.Tag.COMMENT) {
               return new CommentView(var1);
            }

            if (var5 == HTML.Tag.HEAD) {
               return new BlockView(var1, 0) {
                  public float getPreferredSpan(int var1) {
                     return 0.0F;
                  }

                  public float getMinimumSpan(int var1) {
                     return 0.0F;
                  }

                  public float getMaximumSpan(int var1) {
                     return 0.0F;
                  }

                  protected void loadChildren(ViewFactory var1) {
                  }

                  public Shape modelToView(int var1, Shape var2, Position.Bias var3) throws BadLocationException {
                     return var2;
                  }

                  public int getNextVisualPositionFrom(int var1, Position.Bias var2, Shape var3, int var4, Position.Bias[] var5) {
                     return this.getElement().getEndOffset();
                  }
               };
            }

            if (var5 == HTML.Tag.TITLE || var5 == HTML.Tag.META || var5 == HTML.Tag.LINK || var5 == HTML.Tag.STYLE || var5 == HTML.Tag.SCRIPT || var5 == HTML.Tag.AREA || var5 == HTML.Tag.MAP || var5 == HTML.Tag.PARAM || var5 == HTML.Tag.APPLET) {
               return new HiddenTagView(var1);
            }
         }

         String var7 = var3 != null ? (String)var3 : var1.getName();
         if (var7 != null) {
            if (var7.equals("content")) {
               return new LabelView(var1);
            }

            if (var7.equals("paragraph")) {
               return new ParagraphView(var1);
            }

            if (var7.equals("section")) {
               return new BoxView(var1, 1);
            }

            if (var7.equals("component")) {
               return new ComponentView(var1);
            }

            if (var7.equals("icon")) {
               return new IconView(var1);
            }
         }

         return new LabelView(var1);
      }

      static class BodyBlockView extends BlockView implements ComponentListener {
         private Reference<JViewport> cachedViewPort = null;
         private boolean isListening = false;
         private int viewVisibleWidth = Integer.MAX_VALUE;
         private int componentVisibleWidth = Integer.MAX_VALUE;

         public BodyBlockView(Element var1) {
            super(var1, 1);
         }

         protected SizeRequirements calculateMajorAxisRequirements(int var1, SizeRequirements var2) {
            var2 = super.calculateMajorAxisRequirements(var1, var2);
            var2.maximum = Integer.MAX_VALUE;
            return var2;
         }

         protected void layoutMinorAxis(int var1, int var2, int[] var3, int[] var4) {
            Container var5 = this.getContainer();
            Container var6;
            JViewport var7;
            if (var5 != null && var5 instanceof JEditorPane && (var6 = var5.getParent()) != null && var6 instanceof JViewport) {
               var7 = (JViewport)var6;
               if (this.cachedViewPort != null) {
                  JViewport var8 = (JViewport)this.cachedViewPort.get();
                  if (var8 != null) {
                     if (var8 != var7) {
                        var8.removeComponentListener(this);
                     }
                  } else {
                     this.cachedViewPort = null;
                  }
               }

               if (this.cachedViewPort == null) {
                  var7.addComponentListener(this);
                  this.cachedViewPort = new WeakReference(var7);
               }

               this.componentVisibleWidth = var7.getExtentSize().width;
               if (this.componentVisibleWidth > 0) {
                  Insets var9 = var5.getInsets();
                  this.viewVisibleWidth = this.componentVisibleWidth - var9.left - this.getLeftInset();
                  var1 = Math.min(var1, this.viewVisibleWidth);
               }
            } else if (this.cachedViewPort != null) {
               var7 = (JViewport)this.cachedViewPort.get();
               if (var7 != null) {
                  var7.removeComponentListener(this);
               }

               this.cachedViewPort = null;
            }

            super.layoutMinorAxis(var1, var2, var3, var4);
         }

         public void setParent(View var1) {
            if (var1 == null && this.cachedViewPort != null) {
               Object var2;
               if ((var2 = this.cachedViewPort.get()) != null) {
                  ((JComponent)var2).removeComponentListener(this);
               }

               this.cachedViewPort = null;
            }

            super.setParent(var1);
         }

         public void componentResized(ComponentEvent var1) {
            if (var1.getSource() instanceof JViewport) {
               JViewport var2 = (JViewport)var1.getSource();
               if (this.componentVisibleWidth != var2.getExtentSize().width) {
                  Document var3 = this.getDocument();
                  if (var3 instanceof AbstractDocument) {
                     AbstractDocument var4 = (AbstractDocument)this.getDocument();
                     var4.readLock();

                     try {
                        this.layoutChanged(0);
                        this.preferenceChanged((View)null, true, true);
                     } finally {
                        var4.readUnlock();
                     }
                  }
               }

            }
         }

         public void componentHidden(ComponentEvent var1) {
         }

         public void componentMoved(ComponentEvent var1) {
         }

         public void componentShown(ComponentEvent var1) {
         }
      }
   }

   public static class ParserCallback {
      public static final Object IMPLIED = "_implied_";

      public void flush() throws BadLocationException {
      }

      public void handleText(char[] var1, int var2) {
      }

      public void handleComment(char[] var1, int var2) {
      }

      public void handleStartTag(HTML.Tag var1, MutableAttributeSet var2, int var3) {
      }

      public void handleEndTag(HTML.Tag var1, int var2) {
      }

      public void handleSimpleTag(HTML.Tag var1, MutableAttributeSet var2, int var3) {
      }

      public void handleError(String var1, int var2) {
      }

      public void handleEndOfLineString(String var1) {
      }
   }

   public abstract static class Parser {
      public abstract void parse(Reader var1, HTMLEditorKit.ParserCallback var2, boolean var3) throws IOException;
   }

   public static class LinkController extends MouseAdapter implements MouseMotionListener, Serializable {
      private Element curElem = null;
      private boolean curElemImage = false;
      private String href = null;
      private transient Position.Bias[] bias = new Position.Bias[1];
      private int curOffset;

      public void mouseClicked(MouseEvent var1) {
         JEditorPane var2 = (JEditorPane)var1.getSource();
         if (!var2.isEditable() && var2.isEnabled() && SwingUtilities.isLeftMouseButton(var1)) {
            Point var3 = new Point(var1.getX(), var1.getY());
            int var4 = var2.viewToModel(var3);
            if (var4 >= 0) {
               this.activateLink(var4, var2, var1);
            }
         }

      }

      public void mouseDragged(MouseEvent var1) {
      }

      public void mouseMoved(MouseEvent var1) {
         JEditorPane var2 = (JEditorPane)var1.getSource();
         if (var2.isEnabled()) {
            HTMLEditorKit var3 = (HTMLEditorKit)var2.getEditorKit();
            boolean var4 = true;
            Cursor var5 = var3.getDefaultCursor();
            if (!var2.isEditable()) {
               Point var6 = new Point(var1.getX(), var1.getY());
               int var7 = var2.getUI().viewToModel(var2, var6, this.bias);
               if (this.bias[0] == Position.Bias.Backward && var7 > 0) {
                  --var7;
               }

               if (var7 >= 0 && var2.getDocument() instanceof HTMLDocument) {
                  HTMLDocument var8 = (HTMLDocument)var2.getDocument();
                  Element var9 = var8.getCharacterElement(var7);
                  if (!this.doesElementContainLocation(var2, var9, var7, var1.getX(), var1.getY())) {
                     var9 = null;
                  }

                  if (this.curElem == var9 && !this.curElemImage) {
                     var4 = false;
                  } else {
                     Element var10 = this.curElem;
                     this.curElem = var9;
                     String var11 = null;
                     this.curElemImage = false;
                     if (var9 != null) {
                        AttributeSet var12 = var9.getAttributes();
                        AttributeSet var13 = (AttributeSet)var12.getAttribute(HTML.Tag.A);
                        if (var13 == null) {
                           this.curElemImage = var12.getAttribute(StyleConstants.NameAttribute) == HTML.Tag.IMG;
                           if (this.curElemImage) {
                              var11 = this.getMapHREF(var2, var8, var9, var12, var7, var1.getX(), var1.getY());
                           }
                        } else {
                           var11 = (String)var13.getAttribute(HTML.Attribute.HREF);
                        }
                     }

                     if (var11 != this.href) {
                        this.fireEvents(var2, var8, var11, var10, var1);
                        this.href = var11;
                        if (var11 != null) {
                           var5 = var3.getLinkCursor();
                        }
                     } else {
                        var4 = false;
                     }
                  }

                  this.curOffset = var7;
               }
            }

            if (var4 && var2.getCursor() != var5) {
               var2.setCursor(var5);
            }

         }
      }

      private String getMapHREF(JEditorPane var1, HTMLDocument var2, Element var3, AttributeSet var4, int var5, int var6, int var7) {
         Object var8 = var4.getAttribute(HTML.Attribute.USEMAP);
         if (var8 != null && var8 instanceof String) {
            Map var9 = var2.getMap((String)var8);
            if (var9 != null && var5 < var2.getLength()) {
               TextUI var11 = var1.getUI();

               Rectangle var10;
               try {
                  Rectangle var12 = var11.modelToView(var1, var5, Position.Bias.Forward);
                  Rectangle var13 = var11.modelToView(var1, var5 + 1, Position.Bias.Backward);
                  var10 = var12.getBounds();
                  var10.add(var13 instanceof Rectangle ? (Rectangle)var13 : var13.getBounds());
               } catch (BadLocationException var14) {
                  var10 = null;
               }

               if (var10 != null) {
                  AttributeSet var15 = var9.getArea(var6 - var10.x, var7 - var10.y, var10.width, var10.height);
                  if (var15 != null) {
                     return (String)var15.getAttribute(HTML.Attribute.HREF);
                  }
               }
            }
         }

         return null;
      }

      private boolean doesElementContainLocation(JEditorPane var1, Element var2, int var3, int var4, int var5) {
         if (var2 != null && var3 > 0 && var2.getStartOffset() == var3) {
            try {
               TextUI var6 = var1.getUI();
               Rectangle var7 = var6.modelToView(var1, var3, Position.Bias.Forward);
               if (var7 == null) {
                  return false;
               }

               Rectangle var8 = var7 instanceof Rectangle ? (Rectangle)var7 : var7.getBounds();
               Rectangle var9 = var6.modelToView(var1, var2.getEndOffset(), Position.Bias.Backward);
               if (var9 != null) {
                  Rectangle var10 = var9 instanceof Rectangle ? (Rectangle)var9 : var9.getBounds();
                  var8.add(var10);
               }

               return var8.contains(var4, var5);
            } catch (BadLocationException var11) {
            }
         }

         return true;
      }

      protected void activateLink(int var1, JEditorPane var2) {
         this.activateLink(var1, var2, (MouseEvent)null);
      }

      void activateLink(int var1, JEditorPane var2, MouseEvent var3) {
         Document var4 = var2.getDocument();
         if (var4 instanceof HTMLDocument) {
            HTMLDocument var5 = (HTMLDocument)var4;
            Element var6 = var5.getCharacterElement(var1);
            AttributeSet var7 = var6.getAttributes();
            AttributeSet var8 = (AttributeSet)var7.getAttribute(HTML.Tag.A);
            HyperlinkEvent var9 = null;
            int var11 = -1;
            int var12 = -1;
            if (var3 != null) {
               var11 = var3.getX();
               var12 = var3.getY();
            }

            if (var8 == null) {
               this.href = this.getMapHREF(var2, var5, var6, var7, var1, var11, var12);
            } else {
               this.href = (String)var8.getAttribute(HTML.Attribute.HREF);
            }

            if (this.href != null) {
               var9 = this.createHyperlinkEvent(var2, var5, this.href, var8, var6, var3);
            }

            if (var9 != null) {
               var2.fireHyperlinkUpdate(var9);
            }
         }

      }

      HyperlinkEvent createHyperlinkEvent(JEditorPane var1, HTMLDocument var2, String var3, AttributeSet var4, Element var5, MouseEvent var6) {
         URL var7;
         String var9;
         try {
            URL var8 = var2.getBase();
            var7 = new URL(var8, var3);
            if (var3 != null && "file".equals(var7.getProtocol()) && var3.startsWith("#")) {
               var9 = var8.getFile();
               String var10 = var7.getFile();
               if (var9 != null && var10 != null && !var10.startsWith(var9)) {
                  var7 = new URL(var8, var9 + var3);
               }
            }
         } catch (MalformedURLException var11) {
            var7 = null;
         }

         Object var12;
         if (!var2.isFrameDocument()) {
            var12 = new HyperlinkEvent(var1, HyperlinkEvent.EventType.ACTIVATED, var7, var3, var5, var6);
         } else {
            var9 = var4 != null ? (String)var4.getAttribute(HTML.Attribute.TARGET) : null;
            if (var9 == null || var9.equals("")) {
               var9 = var2.getBaseTarget();
            }

            if (var9 == null || var9.equals("")) {
               var9 = "_self";
            }

            var12 = new HTMLFrameHyperlinkEvent(var1, HyperlinkEvent.EventType.ACTIVATED, var7, var3, var5, var6, var9);
         }

         return (HyperlinkEvent)var12;
      }

      void fireEvents(JEditorPane var1, HTMLDocument var2, String var3, Element var4, MouseEvent var5) {
         URL var6;
         HyperlinkEvent var7;
         if (this.href != null) {
            try {
               var6 = new URL(var2.getBase(), this.href);
            } catch (MalformedURLException var9) {
               var6 = null;
            }

            var7 = new HyperlinkEvent(var1, HyperlinkEvent.EventType.EXITED, var6, this.href, var4, var5);
            var1.fireHyperlinkUpdate(var7);
         }

         if (var3 != null) {
            try {
               var6 = new URL(var2.getBase(), var3);
            } catch (MalformedURLException var8) {
               var6 = null;
            }

            var7 = new HyperlinkEvent(var1, HyperlinkEvent.EventType.ENTERED, var6, var3, this.curElem, var5);
            var1.fireHyperlinkUpdate(var7);
         }

      }
   }
}
