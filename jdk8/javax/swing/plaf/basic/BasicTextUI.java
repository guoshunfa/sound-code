package javax.swing.plaf.basic;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.im.InputContext;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringBufferInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.DropMode;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.ComponentInputMapUIResource;
import javax.swing.plaf.InputMapUIResource;
import javax.swing.plaf.TextUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.synth.SynthUI;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.DefaultCaret;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.Keymap;
import javax.swing.text.Position;
import javax.swing.text.TextAction;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import sun.awt.AppContext;
import sun.swing.DefaultLookup;

public abstract class BasicTextUI extends TextUI implements ViewFactory {
   private static BasicTextUI.BasicCursor textCursor = new BasicTextUI.BasicCursor(2);
   private static final EditorKit defaultKit = new DefaultEditorKit();
   transient JTextComponent editor;
   transient boolean painted = false;
   transient BasicTextUI.RootView rootView = new BasicTextUI.RootView();
   transient BasicTextUI.UpdateHandler updateHandler = new BasicTextUI.UpdateHandler();
   private static final TransferHandler defaultTransferHandler = new BasicTextUI.TextTransferHandler();
   private final BasicTextUI.DragListener dragListener = getDragListener();
   private static final Position.Bias[] discardBias = new Position.Bias[1];
   private DefaultCaret dropCaret;

   protected Caret createCaret() {
      return new BasicTextUI.BasicCaret();
   }

   protected Highlighter createHighlighter() {
      return new BasicTextUI.BasicHighlighter();
   }

   protected String getKeymapName() {
      String var1 = this.getClass().getName();
      int var2 = var1.lastIndexOf(46);
      if (var2 >= 0) {
         var1 = var1.substring(var2 + 1, var1.length());
      }

      return var1;
   }

   protected Keymap createKeymap() {
      String var1 = this.getKeymapName();
      Keymap var2 = JTextComponent.getKeymap(var1);
      if (var2 == null) {
         Keymap var3 = JTextComponent.getKeymap("default");
         var2 = JTextComponent.addKeymap(var1, var3);
         String var4 = this.getPropertyPrefix();
         Object var5 = DefaultLookup.get(this.editor, this, var4 + ".keyBindings");
         if (var5 != null && var5 instanceof JTextComponent.KeyBinding[]) {
            JTextComponent.KeyBinding[] var6 = (JTextComponent.KeyBinding[])((JTextComponent.KeyBinding[])var5);
            JTextComponent.loadKeymap(var2, var6, this.getComponent().getActions());
         }
      }

      return var2;
   }

   protected void propertyChange(PropertyChangeEvent var1) {
      if (var1.getPropertyName().equals("editable") || var1.getPropertyName().equals("enabled")) {
         this.updateBackground((JTextComponent)var1.getSource());
      }

   }

   private void updateBackground(JTextComponent var1) {
      if (!(this instanceof SynthUI) && !(var1 instanceof JTextArea)) {
         Color var2 = var1.getBackground();
         if (var2 instanceof UIResource) {
            String var3 = this.getPropertyPrefix();
            Color var4 = DefaultLookup.getColor(var1, this, var3 + ".disabledBackground", (Color)null);
            Color var5 = DefaultLookup.getColor(var1, this, var3 + ".inactiveBackground", (Color)null);
            Color var6 = DefaultLookup.getColor(var1, this, var3 + ".background", (Color)null);
            if ((var1 instanceof JTextArea || var1 instanceof JEditorPane) && var2 != var4 && var2 != var5 && var2 != var6) {
               return;
            }

            Color var7 = null;
            if (!var1.isEnabled()) {
               var7 = var4;
            }

            if (var7 == null && !var1.isEditable()) {
               var7 = var5;
            }

            if (var7 == null) {
               var7 = var6;
            }

            if (var7 != null && var7 != var2) {
               var1.setBackground(var7);
            }
         }

      }
   }

   protected abstract String getPropertyPrefix();

   protected void installDefaults() {
      String var1 = this.getPropertyPrefix();
      Font var2 = this.editor.getFont();
      if (var2 == null || var2 instanceof UIResource) {
         this.editor.setFont(UIManager.getFont(var1 + ".font"));
      }

      Color var3 = this.editor.getBackground();
      if (var3 == null || var3 instanceof UIResource) {
         this.editor.setBackground(UIManager.getColor(var1 + ".background"));
      }

      Color var4 = this.editor.getForeground();
      if (var4 == null || var4 instanceof UIResource) {
         this.editor.setForeground(UIManager.getColor(var1 + ".foreground"));
      }

      Color var5 = this.editor.getCaretColor();
      if (var5 == null || var5 instanceof UIResource) {
         this.editor.setCaretColor(UIManager.getColor(var1 + ".caretForeground"));
      }

      Color var6 = this.editor.getSelectionColor();
      if (var6 == null || var6 instanceof UIResource) {
         this.editor.setSelectionColor(UIManager.getColor(var1 + ".selectionBackground"));
      }

      Color var7 = this.editor.getSelectedTextColor();
      if (var7 == null || var7 instanceof UIResource) {
         this.editor.setSelectedTextColor(UIManager.getColor(var1 + ".selectionForeground"));
      }

      Color var8 = this.editor.getDisabledTextColor();
      if (var8 == null || var8 instanceof UIResource) {
         this.editor.setDisabledTextColor(UIManager.getColor(var1 + ".inactiveForeground"));
      }

      Border var9 = this.editor.getBorder();
      if (var9 == null || var9 instanceof UIResource) {
         this.editor.setBorder(UIManager.getBorder(var1 + ".border"));
      }

      Insets var10 = this.editor.getMargin();
      if (var10 == null || var10 instanceof UIResource) {
         this.editor.setMargin(UIManager.getInsets(var1 + ".margin"));
      }

      this.updateCursor();
   }

   private void installDefaults2() {
      this.editor.addMouseListener(this.dragListener);
      this.editor.addMouseMotionListener(this.dragListener);
      String var1 = this.getPropertyPrefix();
      Caret var2 = this.editor.getCaret();
      if (var2 == null || var2 instanceof UIResource) {
         var2 = this.createCaret();
         this.editor.setCaret(var2);
         int var3 = DefaultLookup.getInt(this.getComponent(), this, var1 + ".caretBlinkRate", 500);
         var2.setBlinkRate(var3);
      }

      Highlighter var5 = this.editor.getHighlighter();
      if (var5 == null || var5 instanceof UIResource) {
         this.editor.setHighlighter(this.createHighlighter());
      }

      TransferHandler var4 = this.editor.getTransferHandler();
      if (var4 == null || var4 instanceof UIResource) {
         this.editor.setTransferHandler(this.getTransferHandler());
      }

   }

   protected void uninstallDefaults() {
      this.editor.removeMouseListener(this.dragListener);
      this.editor.removeMouseMotionListener(this.dragListener);
      if (this.editor.getCaretColor() instanceof UIResource) {
         this.editor.setCaretColor((Color)null);
      }

      if (this.editor.getSelectionColor() instanceof UIResource) {
         this.editor.setSelectionColor((Color)null);
      }

      if (this.editor.getDisabledTextColor() instanceof UIResource) {
         this.editor.setDisabledTextColor((Color)null);
      }

      if (this.editor.getSelectedTextColor() instanceof UIResource) {
         this.editor.setSelectedTextColor((Color)null);
      }

      if (this.editor.getBorder() instanceof UIResource) {
         this.editor.setBorder((Border)null);
      }

      if (this.editor.getMargin() instanceof UIResource) {
         this.editor.setMargin((Insets)null);
      }

      if (this.editor.getCaret() instanceof UIResource) {
         this.editor.setCaret((Caret)null);
      }

      if (this.editor.getHighlighter() instanceof UIResource) {
         this.editor.setHighlighter((Highlighter)null);
      }

      if (this.editor.getTransferHandler() instanceof UIResource) {
         this.editor.setTransferHandler((TransferHandler)null);
      }

      if (this.editor.getCursor() instanceof UIResource) {
         this.editor.setCursor((Cursor)null);
      }

   }

   protected void installListeners() {
   }

   protected void uninstallListeners() {
   }

   protected void installKeyboardActions() {
      this.editor.setKeymap(this.createKeymap());
      InputMap var1 = this.getInputMap();
      if (var1 != null) {
         SwingUtilities.replaceUIInputMap(this.editor, 0, var1);
      }

      ActionMap var2 = this.getActionMap();
      if (var2 != null) {
         SwingUtilities.replaceUIActionMap(this.editor, var2);
      }

      this.updateFocusAcceleratorBinding(false);
   }

   InputMap getInputMap() {
      InputMapUIResource var1 = new InputMapUIResource();
      InputMap var2 = (InputMap)DefaultLookup.get(this.editor, this, this.getPropertyPrefix() + ".focusInputMap");
      if (var2 != null) {
         var1.setParent(var2);
      }

      return var1;
   }

   void updateFocusAcceleratorBinding(boolean var1) {
      char var2 = this.editor.getFocusAccelerator();
      if (var1 || var2 != 0) {
         Object var3 = SwingUtilities.getUIInputMap(this.editor, 2);
         if (var3 == null && var2 != 0) {
            var3 = new ComponentInputMapUIResource(this.editor);
            SwingUtilities.replaceUIInputMap(this.editor, 2, (InputMap)var3);
            ActionMap var4 = this.getActionMap();
            SwingUtilities.replaceUIActionMap(this.editor, var4);
         }

         if (var3 != null) {
            ((InputMap)var3).clear();
            if (var2 != 0) {
               ((InputMap)var3).put(KeyStroke.getKeyStroke(var2, BasicLookAndFeel.getFocusAcceleratorKeyMask()), "requestFocus");
            }
         }
      }

   }

   void updateFocusTraversalKeys() {
      EditorKit var1 = this.getEditorKit(this.editor);
      if (var1 != null && var1 instanceof DefaultEditorKit) {
         Set var2 = this.editor.getFocusTraversalKeys(0);
         Set var3 = this.editor.getFocusTraversalKeys(1);
         HashSet var4 = new HashSet(var2);
         HashSet var5 = new HashSet(var3);
         if (this.editor.isEditable()) {
            var4.remove(KeyStroke.getKeyStroke(9, 0));
            var5.remove(KeyStroke.getKeyStroke(9, 1));
         } else {
            var4.add(KeyStroke.getKeyStroke(9, 0));
            var5.add(KeyStroke.getKeyStroke(9, 1));
         }

         LookAndFeel.installProperty(this.editor, "focusTraversalKeysForward", var4);
         LookAndFeel.installProperty(this.editor, "focusTraversalKeysBackward", var5);
      }

   }

   private void updateCursor() {
      if (!this.editor.isCursorSet() || this.editor.getCursor() instanceof UIResource) {
         BasicTextUI.BasicCursor var1 = this.editor.isEditable() ? textCursor : null;
         this.editor.setCursor(var1);
      }

   }

   TransferHandler getTransferHandler() {
      return defaultTransferHandler;
   }

   ActionMap getActionMap() {
      String var1 = this.getPropertyPrefix() + ".actionMap";
      ActionMap var2 = (ActionMap)UIManager.get(var1);
      if (var2 == null) {
         var2 = this.createActionMap();
         if (var2 != null) {
            UIManager.getLookAndFeelDefaults().put(var1, var2);
         }
      }

      ActionMapUIResource var3 = new ActionMapUIResource();
      var3.put("requestFocus", new BasicTextUI.FocusAction());
      if (this.getEditorKit(this.editor) instanceof DefaultEditorKit && var2 != null) {
         Action var4 = var2.get("insert-break");
         if (var4 != null && var4 instanceof DefaultEditorKit.InsertBreakAction) {
            BasicTextUI.TextActionWrapper var5 = new BasicTextUI.TextActionWrapper((TextAction)var4);
            var3.put(var5.getValue("Name"), var5);
         }
      }

      if (var2 != null) {
         var3.setParent(var2);
      }

      return var3;
   }

   ActionMap createActionMap() {
      ActionMapUIResource var1 = new ActionMapUIResource();
      Action[] var2 = this.editor.getActions();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Action var5 = var2[var4];
         var1.put(var5.getValue("Name"), var5);
      }

      var1.put(TransferHandler.getCutAction().getValue("Name"), TransferHandler.getCutAction());
      var1.put(TransferHandler.getCopyAction().getValue("Name"), TransferHandler.getCopyAction());
      var1.put(TransferHandler.getPasteAction().getValue("Name"), TransferHandler.getPasteAction());
      return var1;
   }

   protected void uninstallKeyboardActions() {
      this.editor.setKeymap((Keymap)null);
      SwingUtilities.replaceUIInputMap(this.editor, 2, (InputMap)null);
      SwingUtilities.replaceUIActionMap(this.editor, (ActionMap)null);
   }

   protected void paintBackground(Graphics var1) {
      var1.setColor(this.editor.getBackground());
      var1.fillRect(0, 0, this.editor.getWidth(), this.editor.getHeight());
   }

   protected final JTextComponent getComponent() {
      return this.editor;
   }

   protected void modelChanged() {
      ViewFactory var1 = this.rootView.getViewFactory();
      Document var2 = this.editor.getDocument();
      Element var3 = var2.getDefaultRootElement();
      this.setView(var1.create(var3));
   }

   protected final void setView(View var1) {
      this.rootView.setView(var1);
      this.painted = false;
      this.editor.revalidate();
      this.editor.repaint();
   }

   protected void paintSafely(Graphics var1) {
      this.painted = true;
      Highlighter var2 = this.editor.getHighlighter();
      Caret var3 = this.editor.getCaret();
      if (this.editor.isOpaque()) {
         this.paintBackground(var1);
      }

      if (var2 != null) {
         var2.paint(var1);
      }

      Rectangle var4 = this.getVisibleEditorRect();
      if (var4 != null) {
         this.rootView.paint(var1, var4);
      }

      if (var3 != null) {
         var3.paint(var1);
      }

      if (this.dropCaret != null) {
         this.dropCaret.paint(var1);
      }

   }

   public void installUI(JComponent var1) {
      if (var1 instanceof JTextComponent) {
         this.editor = (JTextComponent)var1;
         LookAndFeel.installProperty(this.editor, "opaque", Boolean.TRUE);
         LookAndFeel.installProperty(this.editor, "autoscrolls", Boolean.TRUE);
         this.installDefaults();
         this.installDefaults2();
         this.editor.addPropertyChangeListener(this.updateHandler);
         Document var2 = this.editor.getDocument();
         if (var2 == null) {
            this.editor.setDocument(this.getEditorKit(this.editor).createDefaultDocument());
         } else {
            var2.addDocumentListener(this.updateHandler);
            this.modelChanged();
         }

         this.installListeners();
         this.installKeyboardActions();
         LayoutManager var3 = this.editor.getLayout();
         if (var3 == null || var3 instanceof UIResource) {
            this.editor.setLayout(this.updateHandler);
         }

         this.updateBackground(this.editor);
      } else {
         throw new Error("TextUI needs JTextComponent");
      }
   }

   public void uninstallUI(JComponent var1) {
      this.editor.removePropertyChangeListener(this.updateHandler);
      this.editor.getDocument().removeDocumentListener(this.updateHandler);
      this.painted = false;
      this.uninstallDefaults();
      this.rootView.setView((View)null);
      var1.removeAll();
      LayoutManager var2 = var1.getLayout();
      if (var2 instanceof UIResource) {
         var1.setLayout((LayoutManager)null);
      }

      this.uninstallKeyboardActions();
      this.uninstallListeners();
      this.editor = null;
   }

   public void update(Graphics var1, JComponent var2) {
      this.paint(var1, var2);
   }

   public final void paint(Graphics var1, JComponent var2) {
      if (this.rootView.getViewCount() > 0 && this.rootView.getView(0) != null) {
         Document var3 = this.editor.getDocument();
         if (var3 instanceof AbstractDocument) {
            ((AbstractDocument)var3).readLock();
         }

         try {
            this.paintSafely(var1);
         } finally {
            if (var3 instanceof AbstractDocument) {
               ((AbstractDocument)var3).readUnlock();
            }

         }
      }

   }

   public Dimension getPreferredSize(JComponent var1) {
      Document var2 = this.editor.getDocument();
      Insets var3 = var1.getInsets();
      Dimension var4 = var1.getSize();
      if (var2 instanceof AbstractDocument) {
         ((AbstractDocument)var2).readLock();
      }

      try {
         if (var4.width > var3.left + var3.right && var4.height > var3.top + var3.bottom) {
            this.rootView.setSize((float)(var4.width - var3.left - var3.right), (float)(var4.height - var3.top - var3.bottom));
         } else if (var4.width == 0 && var4.height == 0) {
            this.rootView.setSize(2.14748365E9F, 2.14748365E9F);
         }

         var4.width = (int)Math.min((long)this.rootView.getPreferredSpan(0) + (long)var3.left + (long)var3.right, 2147483647L);
         var4.height = (int)Math.min((long)this.rootView.getPreferredSpan(1) + (long)var3.top + (long)var3.bottom, 2147483647L);
      } finally {
         if (var2 instanceof AbstractDocument) {
            ((AbstractDocument)var2).readUnlock();
         }

      }

      return var4;
   }

   public Dimension getMinimumSize(JComponent var1) {
      Document var2 = this.editor.getDocument();
      Insets var3 = var1.getInsets();
      Dimension var4 = new Dimension();
      if (var2 instanceof AbstractDocument) {
         ((AbstractDocument)var2).readLock();
      }

      try {
         var4.width = (int)this.rootView.getMinimumSpan(0) + var3.left + var3.right;
         var4.height = (int)this.rootView.getMinimumSpan(1) + var3.top + var3.bottom;
      } finally {
         if (var2 instanceof AbstractDocument) {
            ((AbstractDocument)var2).readUnlock();
         }

      }

      return var4;
   }

   public Dimension getMaximumSize(JComponent var1) {
      Document var2 = this.editor.getDocument();
      Insets var3 = var1.getInsets();
      Dimension var4 = new Dimension();
      if (var2 instanceof AbstractDocument) {
         ((AbstractDocument)var2).readLock();
      }

      try {
         var4.width = (int)Math.min((long)this.rootView.getMaximumSpan(0) + (long)var3.left + (long)var3.right, 2147483647L);
         var4.height = (int)Math.min((long)this.rootView.getMaximumSpan(1) + (long)var3.top + (long)var3.bottom, 2147483647L);
      } finally {
         if (var2 instanceof AbstractDocument) {
            ((AbstractDocument)var2).readUnlock();
         }

      }

      return var4;
   }

   protected Rectangle getVisibleEditorRect() {
      Rectangle var1 = this.editor.getBounds();
      if (var1.width > 0 && var1.height > 0) {
         var1.x = var1.y = 0;
         Insets var2 = this.editor.getInsets();
         var1.x += var2.left;
         var1.y += var2.top;
         var1.width -= var2.left + var2.right;
         var1.height -= var2.top + var2.bottom;
         return var1;
      } else {
         return null;
      }
   }

   public Rectangle modelToView(JTextComponent var1, int var2) throws BadLocationException {
      return this.modelToView(var1, var2, Position.Bias.Forward);
   }

   public Rectangle modelToView(JTextComponent var1, int var2, Position.Bias var3) throws BadLocationException {
      Document var4 = this.editor.getDocument();
      if (var4 instanceof AbstractDocument) {
         ((AbstractDocument)var4).readLock();
      }

      Rectangle var7;
      try {
         Rectangle var5 = this.getVisibleEditorRect();
         if (var5 == null) {
            return null;
         }

         this.rootView.setSize((float)var5.width, (float)var5.height);
         Shape var6 = this.rootView.modelToView(var2, var5, var3);
         if (var6 == null) {
            return null;
         }

         var7 = var6.getBounds();
      } finally {
         if (var4 instanceof AbstractDocument) {
            ((AbstractDocument)var4).readUnlock();
         }

      }

      return var7;
   }

   public int viewToModel(JTextComponent var1, Point var2) {
      return this.viewToModel(var1, var2, discardBias);
   }

   public int viewToModel(JTextComponent var1, Point var2, Position.Bias[] var3) {
      int var4 = -1;
      Document var5 = this.editor.getDocument();
      if (var5 instanceof AbstractDocument) {
         ((AbstractDocument)var5).readLock();
      }

      try {
         Rectangle var6 = this.getVisibleEditorRect();
         if (var6 != null) {
            this.rootView.setSize((float)var6.width, (float)var6.height);
            var4 = this.rootView.viewToModel((float)var2.x, (float)var2.y, var6, var3);
         }
      } finally {
         if (var5 instanceof AbstractDocument) {
            ((AbstractDocument)var5).readUnlock();
         }

      }

      return var4;
   }

   public int getNextVisualPositionFrom(JTextComponent var1, int var2, Position.Bias var3, int var4, Position.Bias[] var5) throws BadLocationException {
      Document var6 = this.editor.getDocument();
      if (var6 instanceof AbstractDocument) {
         ((AbstractDocument)var6).readLock();
      }

      int var8;
      try {
         if (!this.painted) {
            return -1;
         }

         Rectangle var7 = this.getVisibleEditorRect();
         if (var7 != null) {
            this.rootView.setSize((float)var7.width, (float)var7.height);
         }

         var8 = this.rootView.getNextVisualPositionFrom(var2, var3, var7, var4, var5);
      } finally {
         if (var6 instanceof AbstractDocument) {
            ((AbstractDocument)var6).readUnlock();
         }

      }

      return var8;
   }

   public void damageRange(JTextComponent var1, int var2, int var3) {
      this.damageRange(var1, var2, var3, Position.Bias.Forward, Position.Bias.Backward);
   }

   public void damageRange(JTextComponent var1, int var2, int var3, Position.Bias var4, Position.Bias var5) {
      if (this.painted) {
         Rectangle var6 = this.getVisibleEditorRect();
         if (var6 != null) {
            Document var7 = var1.getDocument();
            if (var7 instanceof AbstractDocument) {
               ((AbstractDocument)var7).readLock();
            }

            try {
               this.rootView.setSize((float)var6.width, (float)var6.height);
               Shape var8 = this.rootView.modelToView(var2, var4, var3, var5, var6);
               Rectangle var9 = var8 instanceof Rectangle ? (Rectangle)var8 : var8.getBounds();
               this.editor.repaint(var9.x, var9.y, var9.width, var9.height);
            } catch (BadLocationException var13) {
            } finally {
               if (var7 instanceof AbstractDocument) {
                  ((AbstractDocument)var7).readUnlock();
               }

            }
         }
      }

   }

   public EditorKit getEditorKit(JTextComponent var1) {
      return defaultKit;
   }

   public View getRootView(JTextComponent var1) {
      return this.rootView;
   }

   public String getToolTipText(JTextComponent var1, Point var2) {
      if (!this.painted) {
         return null;
      } else {
         Document var3 = this.editor.getDocument();
         String var4 = null;
         Rectangle var5 = this.getVisibleEditorRect();
         if (var5 != null) {
            if (var3 instanceof AbstractDocument) {
               ((AbstractDocument)var3).readLock();
            }

            try {
               var4 = this.rootView.getToolTipText((float)var2.x, (float)var2.y, var5);
            } finally {
               if (var3 instanceof AbstractDocument) {
                  ((AbstractDocument)var3).readUnlock();
               }

            }
         }

         return var4;
      }
   }

   public View create(Element var1) {
      return null;
   }

   public View create(Element var1, int var2, int var3) {
      return null;
   }

   private static BasicTextUI.DragListener getDragListener() {
      Class var0 = BasicTextUI.DragListener.class;
      synchronized(BasicTextUI.DragListener.class) {
         BasicTextUI.DragListener var1 = (BasicTextUI.DragListener)AppContext.getAppContext().get(BasicTextUI.DragListener.class);
         if (var1 == null) {
            var1 = new BasicTextUI.DragListener();
            AppContext.getAppContext().put(BasicTextUI.DragListener.class, var1);
         }

         return var1;
      }
   }

   static class TextTransferHandler extends TransferHandler implements UIResource {
      private JTextComponent exportComp;
      private boolean shouldRemove;
      private int p0;
      private int p1;
      private boolean modeBetween = false;
      private boolean isDrop = false;
      private int dropAction = 2;
      private Position.Bias dropBias;

      protected DataFlavor getImportFlavor(DataFlavor[] var1, JTextComponent var2) {
         DataFlavor var3 = null;
         DataFlavor var4 = null;
         DataFlavor var5 = null;
         int var6;
         String var7;
         if (var2 instanceof JEditorPane) {
            for(var6 = 0; var6 < var1.length; ++var6) {
               var7 = var1[var6].getMimeType();
               if (var7.startsWith(((JEditorPane)var2).getEditorKit().getContentType())) {
                  return var1[var6];
               }

               if (var3 == null && var7.startsWith("text/plain")) {
                  var3 = var1[var6];
               } else if (var4 == null && var7.startsWith("application/x-java-jvm-local-objectref") && var1[var6].getRepresentationClass() == String.class) {
                  var4 = var1[var6];
               } else if (var5 == null && var1[var6].equals(DataFlavor.stringFlavor)) {
                  var5 = var1[var6];
               }
            }

            if (var3 != null) {
               return var3;
            } else if (var4 != null) {
               return var4;
            } else if (var5 != null) {
               return var5;
            } else {
               return null;
            }
         } else {
            for(var6 = 0; var6 < var1.length; ++var6) {
               var7 = var1[var6].getMimeType();
               if (var7.startsWith("text/plain")) {
                  return var1[var6];
               }

               if (var4 == null && var7.startsWith("application/x-java-jvm-local-objectref") && var1[var6].getRepresentationClass() == String.class) {
                  var4 = var1[var6];
               } else if (var5 == null && var1[var6].equals(DataFlavor.stringFlavor)) {
                  var5 = var1[var6];
               }
            }

            if (var4 != null) {
               return var4;
            } else if (var5 != null) {
               return var5;
            } else {
               return null;
            }
         }
      }

      protected void handleReaderImport(Reader var1, JTextComponent var2, boolean var3) throws BadLocationException, IOException {
         int var5;
         if (var3) {
            int var4 = var2.getSelectionStart();
            var5 = var2.getSelectionEnd();
            int var6 = var5 - var4;
            EditorKit var7 = var2.getUI().getEditorKit(var2);
            Document var8 = var2.getDocument();
            if (var6 > 0) {
               var8.remove(var4, var6);
            }

            var7.read(var1, var8, var4);
         } else {
            char[] var10 = new char[1024];
            boolean var11 = false;
            StringBuffer var13 = null;

            while((var5 = var1.read(var10, 0, var10.length)) != -1) {
               if (var13 == null) {
                  var13 = new StringBuffer(var5);
               }

               int var12 = 0;

               for(int var9 = 0; var9 < var5; ++var9) {
                  switch(var10[var9]) {
                  case '\n':
                     if (var11) {
                        if (var9 > var12 + 1) {
                           var13.append(var10, var12, var9 - var12 - 1);
                        }

                        var11 = false;
                        var12 = var9;
                     }
                     break;
                  case '\r':
                     if (var11) {
                        if (var9 == 0) {
                           var13.append('\n');
                        } else {
                           var10[var9 - 1] = '\n';
                        }
                     } else {
                        var11 = true;
                     }
                     break;
                  default:
                     if (var11) {
                        if (var9 == 0) {
                           var13.append('\n');
                        } else {
                           var10[var9 - 1] = '\n';
                        }

                        var11 = false;
                     }
                  }
               }

               if (var12 < var5) {
                  if (var11) {
                     if (var12 < var5 - 1) {
                        var13.append(var10, var12, var5 - var12 - 1);
                     }
                  } else {
                     var13.append(var10, var12, var5 - var12);
                  }
               }
            }

            if (var11) {
               var13.append('\n');
            }

            var2.replaceSelection(var13 != null ? var13.toString() : "");
         }

      }

      public int getSourceActions(JComponent var1) {
         if (var1 instanceof JPasswordField && var1.getClientProperty("JPasswordField.cutCopyAllowed") != Boolean.TRUE) {
            return 0;
         } else {
            return ((JTextComponent)var1).isEditable() ? 3 : 1;
         }
      }

      protected Transferable createTransferable(JComponent var1) {
         this.exportComp = (JTextComponent)var1;
         this.shouldRemove = true;
         this.p0 = this.exportComp.getSelectionStart();
         this.p1 = this.exportComp.getSelectionEnd();
         return this.p0 != this.p1 ? new BasicTextUI.TextTransferHandler.TextTransferable(this.exportComp, this.p0, this.p1) : null;
      }

      protected void exportDone(JComponent var1, Transferable var2, int var3) {
         if (this.shouldRemove && var3 == 2) {
            BasicTextUI.TextTransferHandler.TextTransferable var4 = (BasicTextUI.TextTransferHandler.TextTransferable)var2;
            var4.removeText();
         }

         this.exportComp = null;
      }

      public boolean importData(TransferHandler.TransferSupport var1) {
         this.isDrop = var1.isDrop();
         if (this.isDrop) {
            this.modeBetween = ((JTextComponent)var1.getComponent()).getDropMode() == DropMode.INSERT;
            this.dropBias = ((JTextComponent.DropLocation)var1.getDropLocation()).getBias();
            this.dropAction = var1.getDropAction();
         }

         boolean var2;
         try {
            var2 = super.importData(var1);
         } finally {
            this.isDrop = false;
            this.modeBetween = false;
            this.dropBias = null;
            this.dropAction = 2;
         }

         return var2;
      }

      public boolean importData(JComponent var1, Transferable var2) {
         JTextComponent var3 = (JTextComponent)var1;
         int var4 = this.modeBetween ? var3.getDropLocation().getIndex() : var3.getCaretPosition();
         if (this.dropAction == 2 && var3 == this.exportComp && var4 >= this.p0 && var4 <= this.p1) {
            this.shouldRemove = false;
            return true;
         } else {
            boolean var5 = false;
            DataFlavor var6 = this.getImportFlavor(var2.getTransferDataFlavors(), var3);
            if (var6 != null) {
               try {
                  boolean var7 = false;
                  if (var1 instanceof JEditorPane) {
                     JEditorPane var8 = (JEditorPane)var1;
                     if (!var8.getContentType().startsWith("text/plain") && var6.getMimeType().startsWith(var8.getContentType())) {
                        var7 = true;
                     }
                  }

                  InputContext var16 = var3.getInputContext();
                  if (var16 != null) {
                     var16.endComposition();
                  }

                  Reader var9 = var6.getReaderForText(var2);
                  Caret var10;
                  if (this.modeBetween) {
                     var10 = var3.getCaret();
                     if (var10 instanceof DefaultCaret) {
                        ((DefaultCaret)var10).setDot(var4, this.dropBias);
                     } else {
                        var3.setCaretPosition(var4);
                     }
                  }

                  this.handleReaderImport(var9, var3, var7);
                  if (this.isDrop) {
                     var3.requestFocus();
                     var10 = var3.getCaret();
                     if (var10 instanceof DefaultCaret) {
                        int var11 = var10.getDot();
                        Position.Bias var12 = ((DefaultCaret)var10).getDotBias();
                        ((DefaultCaret)var10).setDot(var4, this.dropBias);
                        ((DefaultCaret)var10).moveDot(var11, var12);
                     } else {
                        var3.select(var4, var3.getCaretPosition());
                     }
                  }

                  var5 = true;
               } catch (UnsupportedFlavorException var13) {
               } catch (BadLocationException var14) {
               } catch (IOException var15) {
               }
            }

            return var5;
         }
      }

      public boolean canImport(JComponent var1, DataFlavor[] var2) {
         JTextComponent var3 = (JTextComponent)var1;
         if (var3.isEditable() && var3.isEnabled()) {
            return this.getImportFlavor(var2, var3) != null;
         } else {
            return false;
         }
      }

      static class TextTransferable extends BasicTransferable {
         Position p0;
         Position p1;
         String mimeType;
         String richText;
         JTextComponent c;

         TextTransferable(JTextComponent var1, int var2, int var3) {
            super((String)null, (String)null);
            this.c = var1;
            Document var4 = var1.getDocument();

            try {
               this.p0 = var4.createPosition(var2);
               this.p1 = var4.createPosition(var3);
               this.plainData = var1.getSelectedText();
               if (var1 instanceof JEditorPane) {
                  JEditorPane var5 = (JEditorPane)var1;
                  this.mimeType = var5.getContentType();
                  if (this.mimeType.startsWith("text/plain")) {
                     return;
                  }

                  StringWriter var6 = new StringWriter(this.p1.getOffset() - this.p0.getOffset());
                  var5.getEditorKit().write((Writer)var6, var4, this.p0.getOffset(), this.p1.getOffset() - this.p0.getOffset());
                  if (this.mimeType.startsWith("text/html")) {
                     this.htmlData = var6.toString();
                  } else {
                     this.richText = var6.toString();
                  }
               }
            } catch (BadLocationException var7) {
            } catch (IOException var8) {
            }

         }

         void removeText() {
            if (this.p0 != null && this.p1 != null && this.p0.getOffset() != this.p1.getOffset()) {
               try {
                  Document var1 = this.c.getDocument();
                  var1.remove(this.p0.getOffset(), this.p1.getOffset() - this.p0.getOffset());
               } catch (BadLocationException var2) {
               }
            }

         }

         protected DataFlavor[] getRicherFlavors() {
            if (this.richText == null) {
               return null;
            } else {
               try {
                  DataFlavor[] var1 = new DataFlavor[]{new DataFlavor(this.mimeType + ";class=java.lang.String"), new DataFlavor(this.mimeType + ";class=java.io.Reader"), new DataFlavor(this.mimeType + ";class=java.io.InputStream;charset=unicode")};
                  return var1;
               } catch (ClassNotFoundException var2) {
                  return null;
               }
            }
         }

         protected Object getRicherData(DataFlavor var1) throws UnsupportedFlavorException {
            if (this.richText == null) {
               return null;
            } else if (String.class.equals(var1.getRepresentationClass())) {
               return this.richText;
            } else if (Reader.class.equals(var1.getRepresentationClass())) {
               return new StringReader(this.richText);
            } else if (InputStream.class.equals(var1.getRepresentationClass())) {
               return new StringBufferInputStream(this.richText);
            } else {
               throw new UnsupportedFlavorException(var1);
            }
         }
      }
   }

   static class DragListener extends MouseInputAdapter implements DragRecognitionSupport.BeforeDrag {
      private boolean dragStarted;

      public void dragStarting(MouseEvent var1) {
         this.dragStarted = true;
      }

      public void mousePressed(MouseEvent var1) {
         JTextComponent var2 = (JTextComponent)var1.getSource();
         if (var2.getDragEnabled()) {
            this.dragStarted = false;
            if (this.isDragPossible(var1) && DragRecognitionSupport.mousePressed(var1)) {
               var1.consume();
            }
         }

      }

      public void mouseReleased(MouseEvent var1) {
         JTextComponent var2 = (JTextComponent)var1.getSource();
         if (var2.getDragEnabled()) {
            if (this.dragStarted) {
               var1.consume();
            }

            DragRecognitionSupport.mouseReleased(var1);
         }

      }

      public void mouseDragged(MouseEvent var1) {
         JTextComponent var2 = (JTextComponent)var1.getSource();
         if (var2.getDragEnabled() && (this.dragStarted || DragRecognitionSupport.mouseDragged(var1, this))) {
            var1.consume();
         }

      }

      protected boolean isDragPossible(MouseEvent var1) {
         JTextComponent var2 = (JTextComponent)var1.getSource();
         if (var2.isEnabled()) {
            Caret var3 = var2.getCaret();
            int var4 = var3.getDot();
            int var5 = var3.getMark();
            if (var4 != var5) {
               Point var6 = new Point(var1.getX(), var1.getY());
               int var7 = var2.viewToModel(var6);
               int var8 = Math.min(var4, var5);
               int var9 = Math.max(var4, var5);
               if (var7 >= var8 && var7 < var9) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   class FocusAction extends AbstractAction {
      public void actionPerformed(ActionEvent var1) {
         BasicTextUI.this.editor.requestFocus();
      }

      public boolean isEnabled() {
         return BasicTextUI.this.editor.isEditable();
      }
   }

   class TextActionWrapper extends TextAction {
      TextAction action = null;

      public TextActionWrapper(TextAction var2) {
         super((String)var2.getValue("Name"));
         this.action = var2;
      }

      public void actionPerformed(ActionEvent var1) {
         this.action.actionPerformed(var1);
      }

      public boolean isEnabled() {
         return BasicTextUI.this.editor != null && !BasicTextUI.this.editor.isEditable() ? false : this.action.isEnabled();
      }
   }

   class UpdateHandler implements PropertyChangeListener, DocumentListener, LayoutManager2, UIResource {
      private Hashtable<Component, Object> constraints;
      private boolean i18nView = false;

      public final void propertyChange(PropertyChangeEvent var1) {
         Object var2 = var1.getOldValue();
         Object var3 = var1.getNewValue();
         String var4 = var1.getPropertyName();
         if (var2 instanceof Document || var3 instanceof Document) {
            if (var2 != null) {
               ((Document)var2).removeDocumentListener(this);
               this.i18nView = false;
            }

            if (var3 != null) {
               ((Document)var3).addDocumentListener(this);
               if ("document" == var4) {
                  BasicTextUI.this.setView((View)null);
                  BasicTextUI.this.propertyChange(var1);
                  BasicTextUI.this.modelChanged();
                  return;
               }
            }

            BasicTextUI.this.modelChanged();
         }

         if ("focusAccelerator" == var4) {
            BasicTextUI.this.updateFocusAcceleratorBinding(true);
         } else if ("componentOrientation" == var4) {
            BasicTextUI.this.modelChanged();
         } else if ("font" == var4) {
            BasicTextUI.this.modelChanged();
         } else if ("dropLocation" == var4) {
            this.dropIndexChanged();
         } else if ("editable" == var4) {
            BasicTextUI.this.updateCursor();
            BasicTextUI.this.modelChanged();
         }

         BasicTextUI.this.propertyChange(var1);
      }

      private void dropIndexChanged() {
         if (BasicTextUI.this.editor.getDropMode() != DropMode.USE_SELECTION) {
            JTextComponent.DropLocation var1 = BasicTextUI.this.editor.getDropLocation();
            if (var1 == null) {
               if (BasicTextUI.this.dropCaret != null) {
                  BasicTextUI.this.dropCaret.deinstall(BasicTextUI.this.editor);
                  BasicTextUI.this.editor.repaint(BasicTextUI.this.dropCaret);
                  BasicTextUI.this.dropCaret = null;
               }
            } else {
               if (BasicTextUI.this.dropCaret == null) {
                  BasicTextUI.this.dropCaret = new BasicTextUI.BasicCaret();
                  BasicTextUI.this.dropCaret.install(BasicTextUI.this.editor);
                  BasicTextUI.this.dropCaret.setVisible(true);
               }

               BasicTextUI.this.dropCaret.setDot(var1.getIndex(), var1.getBias());
            }

         }
      }

      public final void insertUpdate(DocumentEvent var1) {
         Document var2 = var1.getDocument();
         Object var3 = var2.getProperty("i18n");
         if (var3 instanceof Boolean) {
            Boolean var4 = (Boolean)var3;
            if (var4 != this.i18nView) {
               this.i18nView = var4;
               BasicTextUI.this.modelChanged();
               return;
            }
         }

         Rectangle var5 = BasicTextUI.this.painted ? BasicTextUI.this.getVisibleEditorRect() : null;
         BasicTextUI.this.rootView.insertUpdate(var1, var5, BasicTextUI.this.rootView.getViewFactory());
      }

      public final void removeUpdate(DocumentEvent var1) {
         Rectangle var2 = BasicTextUI.this.painted ? BasicTextUI.this.getVisibleEditorRect() : null;
         BasicTextUI.this.rootView.removeUpdate(var1, var2, BasicTextUI.this.rootView.getViewFactory());
      }

      public final void changedUpdate(DocumentEvent var1) {
         Rectangle var2 = BasicTextUI.this.painted ? BasicTextUI.this.getVisibleEditorRect() : null;
         BasicTextUI.this.rootView.changedUpdate(var1, var2, BasicTextUI.this.rootView.getViewFactory());
      }

      public void addLayoutComponent(String var1, Component var2) {
      }

      public void removeLayoutComponent(Component var1) {
         if (this.constraints != null) {
            this.constraints.remove(var1);
         }

      }

      public Dimension preferredLayoutSize(Container var1) {
         return null;
      }

      public Dimension minimumLayoutSize(Container var1) {
         return null;
      }

      public void layoutContainer(Container var1) {
         if (this.constraints != null && !this.constraints.isEmpty()) {
            Rectangle var2 = BasicTextUI.this.getVisibleEditorRect();
            if (var2 != null) {
               Document var3 = BasicTextUI.this.editor.getDocument();
               if (var3 instanceof AbstractDocument) {
                  ((AbstractDocument)var3).readLock();
               }

               try {
                  BasicTextUI.this.rootView.setSize((float)var2.width, (float)var2.height);
                  Enumeration var4 = this.constraints.keys();

                  while(var4.hasMoreElements()) {
                     Component var5 = (Component)var4.nextElement();
                     View var6 = (View)this.constraints.get(var5);
                     Shape var7 = this.calculateViewPosition(var2, var6);
                     if (var7 != null) {
                        Rectangle var8 = var7 instanceof Rectangle ? (Rectangle)var7 : var7.getBounds();
                        var5.setBounds(var8);
                     }
                  }
               } finally {
                  if (var3 instanceof AbstractDocument) {
                     ((AbstractDocument)var3).readUnlock();
                  }

               }
            }
         }

      }

      Shape calculateViewPosition(Shape var1, View var2) {
         int var3 = var2.getStartOffset();
         View var4 = null;

         for(Object var5 = BasicTextUI.this.rootView; var5 != null && var5 != var2; var5 = var4) {
            int var6 = ((View)var5).getViewIndex(var3, Position.Bias.Forward);
            var1 = ((View)var5).getChildAllocation(var6, var1);
            var4 = ((View)var5).getView(var6);
         }

         return var4 != null ? var1 : null;
      }

      public void addLayoutComponent(Component var1, Object var2) {
         if (var2 instanceof View) {
            if (this.constraints == null) {
               this.constraints = new Hashtable(7);
            }

            this.constraints.put(var1, var2);
         }

      }

      public Dimension maximumLayoutSize(Container var1) {
         return null;
      }

      public float getLayoutAlignmentX(Container var1) {
         return 0.5F;
      }

      public float getLayoutAlignmentY(Container var1) {
         return 0.5F;
      }

      public void invalidateLayout(Container var1) {
      }
   }

   class RootView extends View {
      private View view;

      RootView() {
         super((Element)null);
      }

      void setView(View var1) {
         View var2 = this.view;
         this.view = null;
         if (var2 != null) {
            var2.setParent((View)null);
         }

         if (var1 != null) {
            var1.setParent(this);
         }

         this.view = var1;
      }

      public AttributeSet getAttributes() {
         return null;
      }

      public float getPreferredSpan(int var1) {
         return this.view != null ? this.view.getPreferredSpan(var1) : 10.0F;
      }

      public float getMinimumSpan(int var1) {
         return this.view != null ? this.view.getMinimumSpan(var1) : 10.0F;
      }

      public float getMaximumSpan(int var1) {
         return 2.14748365E9F;
      }

      public void preferenceChanged(View var1, boolean var2, boolean var3) {
         BasicTextUI.this.editor.revalidate();
      }

      public float getAlignment(int var1) {
         return this.view != null ? this.view.getAlignment(var1) : 0.0F;
      }

      public void paint(Graphics var1, Shape var2) {
         if (this.view != null) {
            Rectangle var3 = var2 instanceof Rectangle ? (Rectangle)var2 : var2.getBounds();
            this.setSize((float)var3.width, (float)var3.height);
            this.view.paint(var1, var2);
         }

      }

      public void setParent(View var1) {
         throw new Error("Can't set parent on root view");
      }

      public int getViewCount() {
         return 1;
      }

      public View getView(int var1) {
         return this.view;
      }

      public int getViewIndex(int var1, Position.Bias var2) {
         return 0;
      }

      public Shape getChildAllocation(int var1, Shape var2) {
         return var2;
      }

      public Shape modelToView(int var1, Shape var2, Position.Bias var3) throws BadLocationException {
         return this.view != null ? this.view.modelToView(var1, var2, var3) : null;
      }

      public Shape modelToView(int var1, Position.Bias var2, int var3, Position.Bias var4, Shape var5) throws BadLocationException {
         return this.view != null ? this.view.modelToView(var1, var2, var3, var4, var5) : null;
      }

      public int viewToModel(float var1, float var2, Shape var3, Position.Bias[] var4) {
         if (this.view != null) {
            int var5 = this.view.viewToModel(var1, var2, var3, var4);
            return var5;
         } else {
            return -1;
         }
      }

      public int getNextVisualPositionFrom(int var1, Position.Bias var2, Shape var3, int var4, Position.Bias[] var5) throws BadLocationException {
         if (var1 < -1) {
            throw new BadLocationException("invalid position", var1);
         } else {
            if (this.view != null) {
               int var6 = this.view.getNextVisualPositionFrom(var1, var2, var3, var4, var5);
               if (var6 != -1) {
                  var1 = var6;
               } else {
                  var5[0] = var2;
               }
            }

            return var1;
         }
      }

      public void insertUpdate(DocumentEvent var1, Shape var2, ViewFactory var3) {
         if (this.view != null) {
            this.view.insertUpdate(var1, var2, var3);
         }

      }

      public void removeUpdate(DocumentEvent var1, Shape var2, ViewFactory var3) {
         if (this.view != null) {
            this.view.removeUpdate(var1, var2, var3);
         }

      }

      public void changedUpdate(DocumentEvent var1, Shape var2, ViewFactory var3) {
         if (this.view != null) {
            this.view.changedUpdate(var1, var2, var3);
         }

      }

      public Document getDocument() {
         return BasicTextUI.this.editor.getDocument();
      }

      public int getStartOffset() {
         return this.view != null ? this.view.getStartOffset() : this.getElement().getStartOffset();
      }

      public int getEndOffset() {
         return this.view != null ? this.view.getEndOffset() : this.getElement().getEndOffset();
      }

      public Element getElement() {
         return this.view != null ? this.view.getElement() : BasicTextUI.this.editor.getDocument().getDefaultRootElement();
      }

      public View breakView(int var1, float var2, Shape var3) {
         throw new Error("Can't break root view");
      }

      public int getResizeWeight(int var1) {
         return this.view != null ? this.view.getResizeWeight(var1) : 0;
      }

      public void setSize(float var1, float var2) {
         if (this.view != null) {
            this.view.setSize(var1, var2);
         }

      }

      public Container getContainer() {
         return BasicTextUI.this.editor;
      }

      public ViewFactory getViewFactory() {
         EditorKit var1 = BasicTextUI.this.getEditorKit(BasicTextUI.this.editor);
         ViewFactory var2 = var1.getViewFactory();
         return var2 != null ? var2 : BasicTextUI.this;
      }
   }

   static class BasicCursor extends Cursor implements UIResource {
      BasicCursor(int var1) {
         super(var1);
      }

      BasicCursor(String var1) {
         super(var1);
      }
   }

   public static class BasicHighlighter extends DefaultHighlighter implements UIResource {
   }

   public static class BasicCaret extends DefaultCaret implements UIResource {
   }
}
