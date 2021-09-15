package javax.swing.text;

import com.sun.beans.util.Cache;
import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.IllegalComponentStateException;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.TextAttribute;
import java.awt.font.TextHitInfo;
import java.awt.im.InputContext;
import java.awt.im.InputMethodRequests;
import java.awt.print.Printable;
import java.awt.print.PrinterAbortException;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.beans.Transient;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.BreakIterator;
import java.text.CharacterIterator;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleAction;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleEditableText;
import javax.accessibility.AccessibleExtendedText;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleText;
import javax.accessibility.AccessibleTextSequence;
import javax.print.PrintService;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.DropMode;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.TextUI;
import javax.swing.plaf.UIResource;
import sun.awt.AppContext;
import sun.swing.PrintingStatus;
import sun.swing.SwingAccessor;
import sun.swing.SwingUtilities2;
import sun.swing.text.TextComponentPrintable;

public abstract class JTextComponent extends JComponent implements Scrollable, Accessible {
   public static final String FOCUS_ACCELERATOR_KEY = "focusAcceleratorKey";
   private Document model;
   private transient Caret caret;
   private NavigationFilter navigationFilter;
   private transient Highlighter highlighter;
   private transient Keymap keymap;
   private transient JTextComponent.MutableCaretEvent caretEvent;
   private Color caretColor;
   private Color selectionColor;
   private Color selectedTextColor;
   private Color disabledTextColor;
   private boolean editable;
   private Insets margin;
   private char focusAccelerator;
   private boolean dragEnabled;
   private DropMode dropMode;
   private transient JTextComponent.DropLocation dropLocation;
   private static JTextComponent.DefaultTransferHandler defaultTransferHandler;
   private static Cache<Class<?>, Boolean> METHOD_OVERRIDDEN;
   private static final Object KEYMAP_TABLE;
   private transient InputMethodRequests inputMethodRequestsHandler;
   private SimpleAttributeSet composedTextAttribute;
   private String composedTextContent;
   private Position composedTextStart;
   private Position composedTextEnd;
   private Position latestCommittedTextStart;
   private Position latestCommittedTextEnd;
   private JTextComponent.ComposedTextCaret composedTextCaret;
   private transient Caret originalCaret;
   private boolean checkedInputOverride;
   private boolean needToSendKeyTypedEvent;
   private static final Object FOCUSED_COMPONENT;
   public static final String DEFAULT_KEYMAP = "default";

   public JTextComponent() {
      this.dropMode = DropMode.USE_SELECTION;
      this.enableEvents(2056L);
      this.caretEvent = new JTextComponent.MutableCaretEvent(this);
      this.addMouseListener(this.caretEvent);
      this.addFocusListener(this.caretEvent);
      this.setEditable(true);
      this.setDragEnabled(false);
      this.setLayout((LayoutManager)null);
      this.updateUI();
   }

   public TextUI getUI() {
      return (TextUI)this.ui;
   }

   public void setUI(TextUI var1) {
      super.setUI(var1);
   }

   public void updateUI() {
      this.setUI((TextUI)UIManager.getUI(this));
      this.invalidate();
   }

   public void addCaretListener(CaretListener var1) {
      this.listenerList.add(CaretListener.class, var1);
   }

   public void removeCaretListener(CaretListener var1) {
      this.listenerList.remove(CaretListener.class, var1);
   }

   public CaretListener[] getCaretListeners() {
      return (CaretListener[])this.listenerList.getListeners(CaretListener.class);
   }

   protected void fireCaretUpdate(CaretEvent var1) {
      Object[] var2 = this.listenerList.getListenerList();

      for(int var3 = var2.length - 2; var3 >= 0; var3 -= 2) {
         if (var2[var3] == CaretListener.class) {
            ((CaretListener)var2[var3 + 1]).caretUpdate(var1);
         }
      }

   }

   public void setDocument(Document var1) {
      Document var2 = this.model;

      try {
         if (var2 instanceof AbstractDocument) {
            ((AbstractDocument)var2).readLock();
         }

         if (this.accessibleContext != null) {
            this.model.removeDocumentListener((JTextComponent.AccessibleJTextComponent)this.accessibleContext);
         }

         if (this.inputMethodRequestsHandler != null) {
            this.model.removeDocumentListener((DocumentListener)this.inputMethodRequestsHandler);
         }

         this.model = var1;
         Boolean var3 = this.getComponentOrientation().isLeftToRight() ? TextAttribute.RUN_DIRECTION_LTR : TextAttribute.RUN_DIRECTION_RTL;
         if (var3 != var1.getProperty(TextAttribute.RUN_DIRECTION)) {
            var1.putProperty(TextAttribute.RUN_DIRECTION, var3);
         }

         this.firePropertyChange("document", var2, var1);
      } finally {
         if (var2 instanceof AbstractDocument) {
            ((AbstractDocument)var2).readUnlock();
         }

      }

      this.revalidate();
      this.repaint();
      if (this.accessibleContext != null) {
         this.model.addDocumentListener((JTextComponent.AccessibleJTextComponent)this.accessibleContext);
      }

      if (this.inputMethodRequestsHandler != null) {
         this.model.addDocumentListener((DocumentListener)this.inputMethodRequestsHandler);
      }

   }

   public Document getDocument() {
      return this.model;
   }

   public void setComponentOrientation(ComponentOrientation var1) {
      Document var2 = this.getDocument();
      if (var2 != null) {
         Boolean var3 = var1.isLeftToRight() ? TextAttribute.RUN_DIRECTION_LTR : TextAttribute.RUN_DIRECTION_RTL;
         var2.putProperty(TextAttribute.RUN_DIRECTION, var3);
      }

      super.setComponentOrientation(var1);
   }

   public Action[] getActions() {
      return this.getUI().getEditorKit(this).getActions();
   }

   public void setMargin(Insets var1) {
      Insets var2 = this.margin;
      this.margin = var1;
      this.firePropertyChange("margin", var2, var1);
      this.invalidate();
   }

   public Insets getMargin() {
      return this.margin;
   }

   public void setNavigationFilter(NavigationFilter var1) {
      this.navigationFilter = var1;
   }

   public NavigationFilter getNavigationFilter() {
      return this.navigationFilter;
   }

   @Transient
   public Caret getCaret() {
      return this.caret;
   }

   public void setCaret(Caret var1) {
      if (this.caret != null) {
         this.caret.removeChangeListener(this.caretEvent);
         this.caret.deinstall(this);
      }

      Caret var2 = this.caret;
      this.caret = var1;
      if (this.caret != null) {
         this.caret.install(this);
         this.caret.addChangeListener(this.caretEvent);
      }

      this.firePropertyChange("caret", var2, this.caret);
   }

   public Highlighter getHighlighter() {
      return this.highlighter;
   }

   public void setHighlighter(Highlighter var1) {
      if (this.highlighter != null) {
         this.highlighter.deinstall(this);
      }

      Highlighter var2 = this.highlighter;
      this.highlighter = var1;
      if (this.highlighter != null) {
         this.highlighter.install(this);
      }

      this.firePropertyChange("highlighter", var2, var1);
   }

   public void setKeymap(Keymap var1) {
      Keymap var2 = this.keymap;
      this.keymap = var1;
      this.firePropertyChange("keymap", var2, this.keymap);
      this.updateInputMap(var2, var1);
   }

   public void setDragEnabled(boolean var1) {
      if (var1 && GraphicsEnvironment.isHeadless()) {
         throw new HeadlessException();
      } else {
         this.dragEnabled = var1;
      }
   }

   public boolean getDragEnabled() {
      return this.dragEnabled;
   }

   public final void setDropMode(DropMode var1) {
      if (var1 != null) {
         switch(var1) {
         case USE_SELECTION:
         case INSERT:
            this.dropMode = var1;
            return;
         }
      }

      throw new IllegalArgumentException(var1 + ": Unsupported drop mode for text");
   }

   public final DropMode getDropMode() {
      return this.dropMode;
   }

   JTextComponent.DropLocation dropLocationForPoint(Point var1) {
      Position.Bias[] var2 = new Position.Bias[1];
      int var3 = this.getUI().viewToModel(this, var1, var2);
      if (var2[0] == null) {
         var2[0] = Position.Bias.Forward;
      }

      return new JTextComponent.DropLocation(var1, var3, var2[0]);
   }

   Object setDropLocation(TransferHandler.DropLocation var1, Object var2, boolean var3) {
      Object var4 = null;
      JTextComponent.DropLocation var5 = (JTextComponent.DropLocation)var1;
      boolean var8;
      if (this.dropMode == DropMode.USE_SELECTION) {
         if (var5 == null) {
            if (var2 != null) {
               Object[] var6 = (Object[])((Object[])var2);
               if (!var3) {
                  if (this.caret instanceof DefaultCaret) {
                     ((DefaultCaret)this.caret).setDot((Integer)var6[0], (Position.Bias)var6[3]);
                     ((DefaultCaret)this.caret).moveDot((Integer)var6[1], (Position.Bias)var6[4]);
                  } else {
                     this.caret.setDot((Integer)var6[0]);
                     this.caret.moveDot((Integer)var6[1]);
                  }
               }

               this.caret.setVisible((Boolean)var6[2]);
            }
         } else {
            if (this.dropLocation == null) {
               if (this.caret instanceof DefaultCaret) {
                  DefaultCaret var7 = (DefaultCaret)this.caret;
                  var8 = var7.isActive();
                  var4 = new Object[]{var7.getMark(), var7.getDot(), var8, var7.getMarkBias(), var7.getDotBias()};
               } else {
                  var8 = this.caret.isVisible();
                  var4 = new Object[]{this.caret.getMark(), this.caret.getDot(), var8};
               }

               this.caret.setVisible(true);
            } else {
               var4 = var2;
            }

            if (this.caret instanceof DefaultCaret) {
               ((DefaultCaret)this.caret).setDot(var5.getIndex(), var5.getBias());
            } else {
               this.caret.setDot(var5.getIndex());
            }
         }
      } else if (var5 == null) {
         if (var2 != null) {
            this.caret.setVisible((Boolean)var2);
         }
      } else if (this.dropLocation == null) {
         var8 = this.caret instanceof DefaultCaret ? ((DefaultCaret)this.caret).isActive() : this.caret.isVisible();
         var4 = var8;
         this.caret.setVisible(false);
      } else {
         var4 = var2;
      }

      JTextComponent.DropLocation var9 = this.dropLocation;
      this.dropLocation = var5;
      this.firePropertyChange("dropLocation", var9, this.dropLocation);
      return var4;
   }

   public final JTextComponent.DropLocation getDropLocation() {
      return this.dropLocation;
   }

   void updateInputMap(Keymap var1, Keymap var2) {
      InputMap var3 = this.getInputMap(0);

      InputMap var4;
      for(var4 = var3; var3 != null && !(var3 instanceof JTextComponent.KeymapWrapper); var3 = var3.getParent()) {
         var4 = var3;
      }

      JTextComponent.KeymapWrapper var5;
      if (var3 != null) {
         if (var2 == null) {
            if (var4 != var3) {
               var4.setParent(var3.getParent());
            } else {
               var4.setParent((InputMap)null);
            }
         } else {
            var5 = new JTextComponent.KeymapWrapper(var2);
            var4.setParent(var5);
            if (var4 != var3) {
               var5.setParent(var3.getParent());
            }
         }
      } else if (var2 != null) {
         var3 = this.getInputMap(0);
         if (var3 != null) {
            var5 = new JTextComponent.KeymapWrapper(var2);
            var5.setParent(var3.getParent());
            var3.setParent(var5);
         }
      }

      ActionMap var8 = this.getActionMap();

      ActionMap var6;
      for(var6 = var8; var8 != null && !(var8 instanceof JTextComponent.KeymapActionMap); var8 = var8.getParent()) {
         var6 = var8;
      }

      JTextComponent.KeymapActionMap var7;
      if (var8 != null) {
         if (var2 == null) {
            if (var6 != var8) {
               var6.setParent(var8.getParent());
            } else {
               var6.setParent((ActionMap)null);
            }
         } else {
            var7 = new JTextComponent.KeymapActionMap(var2);
            var6.setParent(var7);
            if (var6 != var8) {
               var7.setParent(var8.getParent());
            }
         }
      } else if (var2 != null) {
         var8 = this.getActionMap();
         if (var8 != null) {
            var7 = new JTextComponent.KeymapActionMap(var2);
            var7.setParent(var8.getParent());
            var8.setParent(var7);
         }
      }

   }

   public Keymap getKeymap() {
      return this.keymap;
   }

   public static Keymap addKeymap(String var0, Keymap var1) {
      JTextComponent.DefaultKeymap var2 = new JTextComponent.DefaultKeymap(var0, var1);
      if (var0 != null) {
         getKeymapTable().put(var0, var2);
      }

      return var2;
   }

   public static Keymap removeKeymap(String var0) {
      return (Keymap)getKeymapTable().remove(var0);
   }

   public static Keymap getKeymap(String var0) {
      return (Keymap)getKeymapTable().get(var0);
   }

   private static HashMap<String, Keymap> getKeymapTable() {
      synchronized(KEYMAP_TABLE) {
         AppContext var1 = AppContext.getAppContext();
         HashMap var2 = (HashMap)var1.get(KEYMAP_TABLE);
         if (var2 == null) {
            var2 = new HashMap(17);
            var1.put(KEYMAP_TABLE, var2);
            Keymap var3 = addKeymap("default", (Keymap)null);
            var3.setDefaultAction(new DefaultEditorKit.DefaultKeyTypedAction());
         }

         return var2;
      }
   }

   public static void loadKeymap(Keymap var0, JTextComponent.KeyBinding[] var1, Action[] var2) {
      Hashtable var3 = new Hashtable();
      Action[] var4 = var2;
      int var5 = var2.length;

      int var6;
      for(var6 = 0; var6 < var5; ++var6) {
         Action var7 = var4[var6];
         String var8 = (String)var7.getValue("Name");
         var3.put(var8 != null ? var8 : "", var7);
      }

      JTextComponent.KeyBinding[] var9 = var1;
      var5 = var1.length;

      for(var6 = 0; var6 < var5; ++var6) {
         JTextComponent.KeyBinding var10 = var9[var6];
         Action var11 = (Action)var3.get(var10.actionName);
         if (var11 != null) {
            var0.addActionForKeyStroke(var10.key, var11);
         }
      }

   }

   public Color getCaretColor() {
      return this.caretColor;
   }

   public void setCaretColor(Color var1) {
      Color var2 = this.caretColor;
      this.caretColor = var1;
      this.firePropertyChange("caretColor", var2, this.caretColor);
   }

   public Color getSelectionColor() {
      return this.selectionColor;
   }

   public void setSelectionColor(Color var1) {
      Color var2 = this.selectionColor;
      this.selectionColor = var1;
      this.firePropertyChange("selectionColor", var2, this.selectionColor);
   }

   public Color getSelectedTextColor() {
      return this.selectedTextColor;
   }

   public void setSelectedTextColor(Color var1) {
      Color var2 = this.selectedTextColor;
      this.selectedTextColor = var1;
      this.firePropertyChange("selectedTextColor", var2, this.selectedTextColor);
   }

   public Color getDisabledTextColor() {
      return this.disabledTextColor;
   }

   public void setDisabledTextColor(Color var1) {
      Color var2 = this.disabledTextColor;
      this.disabledTextColor = var1;
      this.firePropertyChange("disabledTextColor", var2, this.disabledTextColor);
   }

   public void replaceSelection(String var1) {
      Document var2 = this.getDocument();
      if (var2 != null) {
         try {
            boolean var3 = this.saveComposedText(this.caret.getDot());
            int var4 = Math.min(this.caret.getDot(), this.caret.getMark());
            int var5 = Math.max(this.caret.getDot(), this.caret.getMark());
            if (var2 instanceof AbstractDocument) {
               ((AbstractDocument)var2).replace(var4, var5 - var4, var1, (AttributeSet)null);
            } else {
               if (var4 != var5) {
                  var2.remove(var4, var5 - var4);
               }

               if (var1 != null && var1.length() > 0) {
                  var2.insertString(var4, var1, (AttributeSet)null);
               }
            }

            if (var3) {
               this.restoreComposedText();
            }
         } catch (BadLocationException var6) {
            UIManager.getLookAndFeel().provideErrorFeedback(this);
         }
      }

   }

   public String getText(int var1, int var2) throws BadLocationException {
      return this.getDocument().getText(var1, var2);
   }

   public Rectangle modelToView(int var1) throws BadLocationException {
      return this.getUI().modelToView(this, var1);
   }

   public int viewToModel(Point var1) {
      return this.getUI().viewToModel(this, var1);
   }

   public void cut() {
      if (this.isEditable() && this.isEnabled()) {
         this.invokeAction("cut", TransferHandler.getCutAction());
      }

   }

   public void copy() {
      this.invokeAction("copy", TransferHandler.getCopyAction());
   }

   public void paste() {
      if (this.isEditable() && this.isEnabled()) {
         this.invokeAction("paste", TransferHandler.getPasteAction());
      }

   }

   private void invokeAction(String var1, Action var2) {
      ActionMap var3 = this.getActionMap();
      Action var4 = null;
      if (var3 != null) {
         var4 = var3.get(var1);
      }

      if (var4 == null) {
         this.installDefaultTransferHandlerIfNecessary();
         var4 = var2;
      }

      var4.actionPerformed(new ActionEvent(this, 1001, (String)var4.getValue("Name"), EventQueue.getMostRecentEventTime(), this.getCurrentEventModifiers()));
   }

   private void installDefaultTransferHandlerIfNecessary() {
      if (this.getTransferHandler() == null) {
         if (defaultTransferHandler == null) {
            defaultTransferHandler = new JTextComponent.DefaultTransferHandler();
         }

         this.setTransferHandler(defaultTransferHandler);
      }

   }

   public void moveCaretPosition(int var1) {
      Document var2 = this.getDocument();
      if (var2 != null) {
         if (var1 > var2.getLength() || var1 < 0) {
            throw new IllegalArgumentException("bad position: " + var1);
         }

         this.caret.moveDot(var1);
      }

   }

   public void setFocusAccelerator(char var1) {
      var1 = Character.toUpperCase(var1);
      char var2 = this.focusAccelerator;
      this.focusAccelerator = var1;
      this.firePropertyChange("focusAcceleratorKey", var2, this.focusAccelerator);
      this.firePropertyChange("focusAccelerator", var2, this.focusAccelerator);
   }

   public char getFocusAccelerator() {
      return this.focusAccelerator;
   }

   public void read(Reader var1, Object var2) throws IOException {
      EditorKit var3 = this.getUI().getEditorKit(this);
      Document var4 = var3.createDefaultDocument();
      if (var2 != null) {
         var4.putProperty("stream", var2);
      }

      try {
         var3.read((Reader)var1, var4, 0);
         this.setDocument(var4);
      } catch (BadLocationException var6) {
         throw new IOException(var6.getMessage());
      }
   }

   public void write(Writer var1) throws IOException {
      Document var2 = this.getDocument();

      try {
         this.getUI().getEditorKit(this).write((Writer)var1, var2, 0, var2.getLength());
      } catch (BadLocationException var4) {
         throw new IOException(var4.getMessage());
      }
   }

   public void removeNotify() {
      super.removeNotify();
      if (getFocusedComponent() == this) {
         AppContext.getAppContext().remove(FOCUSED_COMPONENT);
      }

   }

   public void setCaretPosition(int var1) {
      Document var2 = this.getDocument();
      if (var2 != null) {
         if (var1 > var2.getLength() || var1 < 0) {
            throw new IllegalArgumentException("bad position: " + var1);
         }

         this.caret.setDot(var1);
      }

   }

   @Transient
   public int getCaretPosition() {
      return this.caret.getDot();
   }

   public void setText(String var1) {
      try {
         Document var2 = this.getDocument();
         if (var2 instanceof AbstractDocument) {
            ((AbstractDocument)var2).replace(0, var2.getLength(), var1, (AttributeSet)null);
         } else {
            var2.remove(0, var2.getLength());
            var2.insertString(0, var1, (AttributeSet)null);
         }
      } catch (BadLocationException var3) {
         UIManager.getLookAndFeel().provideErrorFeedback(this);
      }

   }

   public String getText() {
      Document var1 = this.getDocument();

      String var2;
      try {
         var2 = var1.getText(0, var1.getLength());
      } catch (BadLocationException var4) {
         var2 = null;
      }

      return var2;
   }

   public String getSelectedText() {
      String var1 = null;
      int var2 = Math.min(this.caret.getDot(), this.caret.getMark());
      int var3 = Math.max(this.caret.getDot(), this.caret.getMark());
      if (var2 != var3) {
         try {
            Document var4 = this.getDocument();
            var1 = var4.getText(var2, var3 - var2);
         } catch (BadLocationException var5) {
            throw new IllegalArgumentException(var5.getMessage());
         }
      }

      return var1;
   }

   public boolean isEditable() {
      return this.editable;
   }

   public void setEditable(boolean var1) {
      if (var1 != this.editable) {
         boolean var2 = this.editable;
         this.editable = var1;
         this.enableInputMethods(this.editable);
         this.firePropertyChange("editable", var2, this.editable);
         this.repaint();
      }

   }

   @Transient
   public int getSelectionStart() {
      int var1 = Math.min(this.caret.getDot(), this.caret.getMark());
      return var1;
   }

   public void setSelectionStart(int var1) {
      this.select(var1, this.getSelectionEnd());
   }

   @Transient
   public int getSelectionEnd() {
      int var1 = Math.max(this.caret.getDot(), this.caret.getMark());
      return var1;
   }

   public void setSelectionEnd(int var1) {
      this.select(this.getSelectionStart(), var1);
   }

   public void select(int var1, int var2) {
      int var3 = this.getDocument().getLength();
      if (var1 < 0) {
         var1 = 0;
      }

      if (var1 > var3) {
         var1 = var3;
      }

      if (var2 > var3) {
         var2 = var3;
      }

      if (var2 < var1) {
         var2 = var1;
      }

      this.setCaretPosition(var1);
      this.moveCaretPosition(var2);
   }

   public void selectAll() {
      Document var1 = this.getDocument();
      if (var1 != null) {
         this.setCaretPosition(0);
         this.moveCaretPosition(var1.getLength());
      }

   }

   public String getToolTipText(MouseEvent var1) {
      String var2 = super.getToolTipText(var1);
      if (var2 == null) {
         TextUI var3 = this.getUI();
         if (var3 != null) {
            var2 = var3.getToolTipText(this, new Point(var1.getX(), var1.getY()));
         }
      }

      return var2;
   }

   public Dimension getPreferredScrollableViewportSize() {
      return this.getPreferredSize();
   }

   public int getScrollableUnitIncrement(Rectangle var1, int var2, int var3) {
      switch(var2) {
      case 0:
         return var1.width / 10;
      case 1:
         return var1.height / 10;
      default:
         throw new IllegalArgumentException("Invalid orientation: " + var2);
      }
   }

   public int getScrollableBlockIncrement(Rectangle var1, int var2, int var3) {
      switch(var2) {
      case 0:
         return var1.width;
      case 1:
         return var1.height;
      default:
         throw new IllegalArgumentException("Invalid orientation: " + var2);
      }
   }

   public boolean getScrollableTracksViewportWidth() {
      Container var1 = SwingUtilities.getUnwrappedParent(this);
      if (var1 instanceof JViewport) {
         return var1.getWidth() > this.getPreferredSize().width;
      } else {
         return false;
      }
   }

   public boolean getScrollableTracksViewportHeight() {
      Container var1 = SwingUtilities.getUnwrappedParent(this);
      if (var1 instanceof JViewport) {
         return var1.getHeight() > this.getPreferredSize().height;
      } else {
         return false;
      }
   }

   public boolean print() throws PrinterException {
      return this.print((MessageFormat)null, (MessageFormat)null, true, (PrintService)null, (PrintRequestAttributeSet)null, true);
   }

   public boolean print(MessageFormat var1, MessageFormat var2) throws PrinterException {
      return this.print(var1, var2, true, (PrintService)null, (PrintRequestAttributeSet)null, true);
   }

   public boolean print(MessageFormat var1, MessageFormat var2, boolean var3, PrintService var4, PrintRequestAttributeSet var5, boolean var6) throws PrinterException {
      final PrinterJob var7 = PrinterJob.getPrinterJob();
      boolean var10 = GraphicsEnvironment.isHeadless();
      final boolean var11 = SwingUtilities.isEventDispatchThread();
      Printable var12 = this.getPrintable(var1, var2);
      Printable var8;
      final PrintingStatus var9;
      if (var6 && !var10) {
         var9 = PrintingStatus.createPrintingStatus(this, var7);
         var8 = var9.createNotificationPrintable(var12);
      } else {
         var9 = null;
         var8 = var12;
      }

      if (var4 != null) {
         var7.setPrintService(var4);
      }

      var7.setPrintable(var8);
      final Object var13 = var5 == null ? new HashPrintRequestAttributeSet() : var5;
      if (var3 && !var10 && !var7.printDialog((PrintRequestAttributeSet)var13)) {
         return false;
      } else {
         Callable var14 = new Callable<Object>() {
            public Object call() throws Exception {
               try {
                  var7.print((PrintRequestAttributeSet)var13);
               } finally {
                  if (var9 != null) {
                     var9.dispose();
                  }

               }

               return null;
            }
         };
         final FutureTask var15 = new FutureTask(var14);
         Runnable var16 = new Runnable() {
            public void run() {
               boolean var1 = false;
               Throwable var3;
               if (var11) {
                  if (JTextComponent.this.isEnabled()) {
                     var1 = true;
                     JTextComponent.this.setEnabled(false);
                  }
               } else {
                  try {
                     var1 = (Boolean)SwingUtilities2.submit(new Callable<Boolean>() {
                        public Boolean call() throws Exception {
                           boolean var1 = JTextComponent.this.isEnabled();
                           if (var1) {
                              JTextComponent.this.setEnabled(false);
                           }

                           return var1;
                        }
                     }).get();
                  } catch (InterruptedException var4) {
                     throw new RuntimeException(var4);
                  } catch (ExecutionException var5) {
                     var3 = var5.getCause();
                     if (var3 instanceof Error) {
                        throw (Error)var3;
                     }

                     if (var3 instanceof RuntimeException) {
                        throw (RuntimeException)var3;
                     }

                     throw new AssertionError(var3);
                  }
               }

               JTextComponent.this.getDocument().render(var15);
               if (var1) {
                  if (var11) {
                     JTextComponent.this.setEnabled(true);
                  } else {
                     try {
                        SwingUtilities2.submit(new Runnable() {
                           public void run() {
                              JTextComponent.this.setEnabled(true);
                           }
                        }, (Object)null).get();
                     } catch (InterruptedException var6) {
                        throw new RuntimeException(var6);
                     } catch (ExecutionException var7) {
                        var3 = var7.getCause();
                        if (var3 instanceof Error) {
                           throw (Error)var3;
                        }

                        if (var3 instanceof RuntimeException) {
                           throw (RuntimeException)var3;
                        }

                        throw new AssertionError(var3);
                     }
                  }
               }

            }
         };
         if (var6 && !var10) {
            if (var11) {
               (new Thread(var16)).start();
               var9.showModal(true);
            } else {
               var9.showModal(false);
               var16.run();
            }
         } else {
            var16.run();
         }

         try {
            var15.get();
            return true;
         } catch (InterruptedException var19) {
            throw new RuntimeException(var19);
         } catch (ExecutionException var20) {
            Throwable var18 = var20.getCause();
            if (var18 instanceof PrinterAbortException) {
               if (var9 != null && var9.isAborted()) {
                  return false;
               } else {
                  throw (PrinterAbortException)var18;
               }
            } else if (var18 instanceof PrinterException) {
               throw (PrinterException)var18;
            } else if (var18 instanceof RuntimeException) {
               throw (RuntimeException)var18;
            } else if (var18 instanceof Error) {
               throw (Error)var18;
            } else {
               throw new AssertionError(var18);
            }
         }
      }
   }

   public Printable getPrintable(MessageFormat var1, MessageFormat var2) {
      return TextComponentPrintable.getPrintable(this, var1, var2);
   }

   public AccessibleContext getAccessibleContext() {
      if (this.accessibleContext == null) {
         this.accessibleContext = new JTextComponent.AccessibleJTextComponent();
      }

      return this.accessibleContext;
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.caretEvent = new JTextComponent.MutableCaretEvent(this);
      this.addMouseListener(this.caretEvent);
      this.addFocusListener(this.caretEvent);
   }

   protected String paramString() {
      String var1 = this.editable ? "true" : "false";
      String var2 = this.caretColor != null ? this.caretColor.toString() : "";
      String var3 = this.selectionColor != null ? this.selectionColor.toString() : "";
      String var4 = this.selectedTextColor != null ? this.selectedTextColor.toString() : "";
      String var5 = this.disabledTextColor != null ? this.disabledTextColor.toString() : "";
      String var6 = this.margin != null ? this.margin.toString() : "";
      return super.paramString() + ",caretColor=" + var2 + ",disabledTextColor=" + var5 + ",editable=" + var1 + ",margin=" + var6 + ",selectedTextColor=" + var4 + ",selectionColor=" + var3;
   }

   static final JTextComponent getFocusedComponent() {
      return (JTextComponent)AppContext.getAppContext().get(FOCUSED_COMPONENT);
   }

   private int getCurrentEventModifiers() {
      int var1 = 0;
      AWTEvent var2 = EventQueue.getCurrentEvent();
      if (var2 instanceof InputEvent) {
         var1 = ((InputEvent)var2).getModifiers();
      } else if (var2 instanceof ActionEvent) {
         var1 = ((ActionEvent)var2).getModifiers();
      }

      return var1;
   }

   protected void processInputMethodEvent(InputMethodEvent var1) {
      super.processInputMethodEvent(var1);
      if (!var1.isConsumed()) {
         if (!this.isEditable()) {
            return;
         }

         switch(var1.getID()) {
         case 1100:
            this.replaceInputMethodText(var1);
         case 1101:
            this.setInputMethodCaretPosition(var1);
         default:
            var1.consume();
         }
      }

   }

   public InputMethodRequests getInputMethodRequests() {
      if (this.inputMethodRequestsHandler == null) {
         this.inputMethodRequestsHandler = new JTextComponent.InputMethodRequestsHandler();
         Document var1 = this.getDocument();
         if (var1 != null) {
            var1.addDocumentListener((DocumentListener)this.inputMethodRequestsHandler);
         }
      }

      return this.inputMethodRequestsHandler;
   }

   public void addInputMethodListener(InputMethodListener var1) {
      super.addInputMethodListener(var1);
      if (var1 != null) {
         this.needToSendKeyTypedEvent = false;
         this.checkedInputOverride = true;
      }

   }

   private void replaceInputMethodText(InputMethodEvent var1) {
      int var2 = var1.getCommittedCharacterCount();
      AttributedCharacterIterator var3 = var1.getText();
      Document var5 = this.getDocument();
      if (this.composedTextExists()) {
         try {
            var5.remove(this.composedTextStart.getOffset(), this.composedTextEnd.getOffset() - this.composedTextStart.getOffset());
         } catch (BadLocationException var12) {
         }

         this.composedTextStart = this.composedTextEnd = null;
         this.composedTextAttribute = null;
         this.composedTextContent = null;
      }

      if (var3 != null) {
         var3.first();
         int var6 = 0;
         int var7 = 0;
         if (var2 > 0) {
            var6 = this.caret.getDot();
            if (this.shouldSynthensizeKeyEvents()) {
               for(char var8 = var3.current(); var2 > 0; --var2) {
                  KeyEvent var9 = new KeyEvent(this, 400, EventQueue.getMostRecentEventTime(), 0, 0, var8);
                  this.processKeyEvent(var9);
                  var8 = var3.next();
               }
            } else {
               StringBuilder var13 = new StringBuilder();

               for(char var14 = var3.current(); var2 > 0; --var2) {
                  var13.append(var14);
                  var14 = var3.next();
               }

               this.mapCommittedTextToAction(var13.toString());
            }

            var7 = this.caret.getDot();
         }

         int var4 = var3.getIndex();
         if (var4 < var3.getEndIndex()) {
            this.createComposedTextAttribute(var4, var3);

            try {
               this.replaceSelection((String)null);
               var5.insertString(this.caret.getDot(), this.composedTextContent, this.composedTextAttribute);
               this.composedTextStart = var5.createPosition(this.caret.getDot() - this.composedTextContent.length());
               this.composedTextEnd = var5.createPosition(this.caret.getDot());
            } catch (BadLocationException var11) {
               this.composedTextStart = this.composedTextEnd = null;
               this.composedTextAttribute = null;
               this.composedTextContent = null;
            }
         }

         if (var6 != var7) {
            try {
               this.latestCommittedTextStart = var5.createPosition(var6);
               this.latestCommittedTextEnd = var5.createPosition(var7);
            } catch (BadLocationException var10) {
               this.latestCommittedTextStart = this.latestCommittedTextEnd = null;
            }
         } else {
            this.latestCommittedTextStart = this.latestCommittedTextEnd = null;
         }
      }

   }

   private void createComposedTextAttribute(int var1, AttributedCharacterIterator var2) {
      Document var3 = this.getDocument();
      StringBuilder var4 = new StringBuilder();

      for(char var5 = var2.setIndex(var1); var5 != '\uffff'; var5 = var2.next()) {
         var4.append(var5);
      }

      this.composedTextContent = var4.toString();
      this.composedTextAttribute = new SimpleAttributeSet();
      this.composedTextAttribute.addAttribute(StyleConstants.ComposedTextAttribute, new AttributedString(var2, var1, var2.getEndIndex()));
   }

   protected boolean saveComposedText(int var1) {
      if (this.composedTextExists()) {
         int var2 = this.composedTextStart.getOffset();
         int var3 = this.composedTextEnd.getOffset() - this.composedTextStart.getOffset();
         if (var1 >= var2 && var1 <= var2 + var3) {
            try {
               this.getDocument().remove(var2, var3);
               return true;
            } catch (BadLocationException var5) {
            }
         }
      }

      return false;
   }

   protected void restoreComposedText() {
      Document var1 = this.getDocument();

      try {
         var1.insertString(this.caret.getDot(), this.composedTextContent, this.composedTextAttribute);
         this.composedTextStart = var1.createPosition(this.caret.getDot() - this.composedTextContent.length());
         this.composedTextEnd = var1.createPosition(this.caret.getDot());
      } catch (BadLocationException var3) {
      }

   }

   private void mapCommittedTextToAction(String var1) {
      Keymap var2 = this.getKeymap();
      if (var2 != null) {
         Action var3 = null;
         if (var1.length() == 1) {
            KeyStroke var4 = KeyStroke.getKeyStroke(var1.charAt(0));
            var3 = var2.getAction(var4);
         }

         if (var3 == null) {
            var3 = var2.getDefaultAction();
         }

         if (var3 != null) {
            ActionEvent var5 = new ActionEvent(this, 1001, var1, EventQueue.getMostRecentEventTime(), this.getCurrentEventModifiers());
            var3.actionPerformed(var5);
         }
      }

   }

   private void setInputMethodCaretPosition(InputMethodEvent var1) {
      int var2;
      if (this.composedTextExists()) {
         var2 = this.composedTextStart.getOffset();
         if (!(this.caret instanceof JTextComponent.ComposedTextCaret)) {
            if (this.composedTextCaret == null) {
               this.composedTextCaret = new JTextComponent.ComposedTextCaret();
            }

            this.originalCaret = this.caret;
            this.exchangeCaret(this.originalCaret, this.composedTextCaret);
         }

         TextHitInfo var3 = var1.getCaret();
         if (var3 != null) {
            int var4 = var3.getInsertionIndex();
            var2 += var4;
            if (var4 == 0) {
               try {
                  Rectangle var5 = this.modelToView(var2);
                  Rectangle var6 = this.modelToView(this.composedTextEnd.getOffset());
                  Rectangle var7 = this.getBounds();
                  var5.x += Math.min(var6.x - var5.x, var7.width);
                  this.scrollRectToVisible(var5);
               } catch (BadLocationException var8) {
               }
            }
         }

         this.caret.setDot(var2);
      } else if (this.caret instanceof JTextComponent.ComposedTextCaret) {
         var2 = this.caret.getDot();
         this.exchangeCaret(this.caret, this.originalCaret);
         this.caret.setDot(var2);
      }

   }

   private void exchangeCaret(Caret var1, Caret var2) {
      int var3 = var1.getBlinkRate();
      this.setCaret(var2);
      this.caret.setBlinkRate(var3);
      this.caret.setVisible(this.hasFocus());
   }

   private boolean shouldSynthensizeKeyEvents() {
      if (!this.checkedInputOverride) {
         this.needToSendKeyTypedEvent = !(Boolean)METHOD_OVERRIDDEN.get(this.getClass());
         this.checkedInputOverride = true;
      }

      return this.needToSendKeyTypedEvent;
   }

   boolean composedTextExists() {
      return this.composedTextStart != null;
   }

   static {
      SwingAccessor.setJTextComponentAccessor(new SwingAccessor.JTextComponentAccessor() {
         public TransferHandler.DropLocation dropLocationForPoint(JTextComponent var1, Point var2) {
            return var1.dropLocationForPoint(var2);
         }

         public Object setDropLocation(JTextComponent var1, TransferHandler.DropLocation var2, Object var3, boolean var4) {
            return var1.setDropLocation(var2, var3, var4);
         }
      });
      METHOD_OVERRIDDEN = new Cache<Class<?>, Boolean>(Cache.Kind.WEAK, Cache.Kind.STRONG) {
         public Boolean create(final Class<?> var1) {
            if (JTextComponent.class == var1) {
               return Boolean.FALSE;
            } else {
               return (Boolean)this.get(var1.getSuperclass()) ? Boolean.TRUE : (Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
                  public Boolean run() {
                     try {
                        var1.getDeclaredMethod("processInputMethodEvent", InputMethodEvent.class);
                        return Boolean.TRUE;
                     } catch (NoSuchMethodException var2) {
                        return Boolean.FALSE;
                     }
                  }
               });
            }
         }
      };
      KEYMAP_TABLE = new StringBuilder("JTextComponent_KeymapTable");
      FOCUSED_COMPONENT = new StringBuilder("JTextComponent_FocusedComponent");
   }

   private class DoSetCaretPosition implements Runnable {
      JTextComponent host;
      Position newPos;

      DoSetCaretPosition(JTextComponent var2, Position var3) {
         this.host = var2;
         this.newPos = var3;
      }

      public void run() {
         this.host.setCaretPosition(this.newPos.getOffset());
      }
   }

   class ComposedTextCaret extends DefaultCaret implements Serializable {
      Color bg;

      public void install(JTextComponent var1) {
         super.install(var1);
         Document var2 = var1.getDocument();
         if (var2 instanceof StyledDocument) {
            StyledDocument var3 = (StyledDocument)var2;
            Element var4 = var3.getCharacterElement(var1.composedTextStart.getOffset());
            AttributeSet var5 = var4.getAttributes();
            this.bg = var3.getBackground(var5);
         }

         if (this.bg == null) {
            this.bg = var1.getBackground();
         }

      }

      public void paint(Graphics var1) {
         if (this.isVisible()) {
            try {
               Rectangle var2 = this.component.modelToView(this.getDot());
               var1.setXORMode(this.bg);
               var1.drawLine(var2.x, var2.y, var2.x, var2.y + var2.height - 1);
               var1.setPaintMode();
            } catch (BadLocationException var3) {
            }
         }

      }

      protected void positionCaret(MouseEvent var1) {
         JTextComponent var2 = this.component;
         Point var3 = new Point(var1.getX(), var1.getY());
         int var4 = var2.viewToModel(var3);
         int var5 = var2.composedTextStart.getOffset();
         if (var4 >= var5 && var4 <= JTextComponent.this.composedTextEnd.getOffset()) {
            super.positionCaret(var1);
         } else {
            try {
               Position var6 = var2.getDocument().createPosition(var4);
               var2.getInputContext().endComposition();
               EventQueue.invokeLater(JTextComponent.this.new DoSetCaretPosition(var2, var6));
            } catch (BadLocationException var7) {
               System.err.println((Object)var7);
            }
         }

      }
   }

   class InputMethodRequestsHandler implements InputMethodRequests, DocumentListener {
      public AttributedCharacterIterator cancelLatestCommittedText(AttributedCharacterIterator.Attribute[] var1) {
         Document var2 = JTextComponent.this.getDocument();
         if (var2 != null && JTextComponent.this.latestCommittedTextStart != null && !JTextComponent.this.latestCommittedTextStart.equals(JTextComponent.this.latestCommittedTextEnd)) {
            try {
               int var3 = JTextComponent.this.latestCommittedTextStart.getOffset();
               int var4 = JTextComponent.this.latestCommittedTextEnd.getOffset();
               String var5 = var2.getText(var3, var4 - var3);
               var2.remove(var3, var4 - var3);
               return (new AttributedString(var5)).getIterator();
            } catch (BadLocationException var6) {
            }
         }

         return null;
      }

      public AttributedCharacterIterator getCommittedText(int var1, int var2, AttributedCharacterIterator.Attribute[] var3) {
         int var4 = 0;
         int var5 = 0;
         if (JTextComponent.this.composedTextExists()) {
            var4 = JTextComponent.this.composedTextStart.getOffset();
            var5 = JTextComponent.this.composedTextEnd.getOffset();
         }

         String var6;
         try {
            if (var1 < var4) {
               if (var2 <= var4) {
                  var6 = JTextComponent.this.getText(var1, var2 - var1);
               } else {
                  int var7 = var4 - var1;
                  var6 = JTextComponent.this.getText(var1, var7) + JTextComponent.this.getText(var5, var2 - var1 - var7);
               }
            } else {
               var6 = JTextComponent.this.getText(var1 + (var5 - var4), var2 - var1);
            }
         } catch (BadLocationException var8) {
            throw new IllegalArgumentException("Invalid range");
         }

         return (new AttributedString(var6)).getIterator();
      }

      public int getCommittedTextLength() {
         Document var1 = JTextComponent.this.getDocument();
         int var2 = 0;
         if (var1 != null) {
            var2 = var1.getLength();
            if (JTextComponent.this.composedTextContent != null) {
               if (JTextComponent.this.composedTextEnd != null && JTextComponent.this.composedTextStart != null) {
                  var2 -= JTextComponent.this.composedTextEnd.getOffset() - JTextComponent.this.composedTextStart.getOffset();
               } else {
                  var2 -= JTextComponent.this.composedTextContent.length();
               }
            }
         }

         return var2;
      }

      public int getInsertPositionOffset() {
         int var1 = 0;
         int var2 = 0;
         if (JTextComponent.this.composedTextExists()) {
            var1 = JTextComponent.this.composedTextStart.getOffset();
            var2 = JTextComponent.this.composedTextEnd.getOffset();
         }

         int var3 = JTextComponent.this.getCaretPosition();
         if (var3 < var1) {
            return var3;
         } else {
            return var3 < var2 ? var1 : var3 - (var2 - var1);
         }
      }

      public TextHitInfo getLocationOffset(int var1, int var2) {
         if (JTextComponent.this.composedTextAttribute == null) {
            return null;
         } else {
            Point var3 = JTextComponent.this.getLocationOnScreen();
            var3.x = var1 - var3.x;
            var3.y = var2 - var3.y;
            int var4 = JTextComponent.this.viewToModel(var3);
            return var4 >= JTextComponent.this.composedTextStart.getOffset() && var4 <= JTextComponent.this.composedTextEnd.getOffset() ? TextHitInfo.leading(var4 - JTextComponent.this.composedTextStart.getOffset()) : null;
         }
      }

      public Rectangle getTextLocation(TextHitInfo var1) {
         Rectangle var2;
         try {
            var2 = JTextComponent.this.modelToView(JTextComponent.this.getCaretPosition());
            if (var2 != null) {
               Point var3 = JTextComponent.this.getLocationOnScreen();
               var2.translate(var3.x, var3.y);
            }
         } catch (BadLocationException var4) {
            var2 = null;
         }

         if (var2 == null) {
            var2 = new Rectangle();
         }

         return var2;
      }

      public AttributedCharacterIterator getSelectedText(AttributedCharacterIterator.Attribute[] var1) {
         String var2 = JTextComponent.this.getSelectedText();
         return var2 != null ? (new AttributedString(var2)).getIterator() : null;
      }

      public void changedUpdate(DocumentEvent var1) {
         JTextComponent.this.latestCommittedTextStart = JTextComponent.this.latestCommittedTextEnd = null;
      }

      public void insertUpdate(DocumentEvent var1) {
         JTextComponent.this.latestCommittedTextStart = JTextComponent.this.latestCommittedTextEnd = null;
      }

      public void removeUpdate(DocumentEvent var1) {
         JTextComponent.this.latestCommittedTextStart = JTextComponent.this.latestCommittedTextEnd = null;
      }
   }

   static class MutableCaretEvent extends CaretEvent implements ChangeListener, FocusListener, MouseListener {
      private boolean dragActive;
      private int dot;
      private int mark;

      MutableCaretEvent(JTextComponent var1) {
         super(var1);
      }

      final void fire() {
         JTextComponent var1 = (JTextComponent)this.getSource();
         if (var1 != null) {
            Caret var2 = var1.getCaret();
            this.dot = var2.getDot();
            this.mark = var2.getMark();
            var1.fireCaretUpdate(this);
         }

      }

      public final String toString() {
         return "dot=" + this.dot + ",mark=" + this.mark;
      }

      public final int getDot() {
         return this.dot;
      }

      public final int getMark() {
         return this.mark;
      }

      public final void stateChanged(ChangeEvent var1) {
         if (!this.dragActive) {
            this.fire();
         }

      }

      public void focusGained(FocusEvent var1) {
         AppContext.getAppContext().put(JTextComponent.FOCUSED_COMPONENT, var1.getSource());
      }

      public void focusLost(FocusEvent var1) {
      }

      public final void mousePressed(MouseEvent var1) {
         this.dragActive = true;
      }

      public final void mouseReleased(MouseEvent var1) {
         this.dragActive = false;
         this.fire();
      }

      public final void mouseClicked(MouseEvent var1) {
      }

      public final void mouseEntered(MouseEvent var1) {
      }

      public final void mouseExited(MouseEvent var1) {
      }
   }

   static class KeymapActionMap extends ActionMap {
      private Keymap keymap;

      KeymapActionMap(Keymap var1) {
         this.keymap = var1;
      }

      public Object[] keys() {
         Object[] var1 = super.keys();
         Action[] var2 = this.keymap.getBoundActions();
         int var3 = var1 == null ? 0 : var1.length;
         int var4 = var2 == null ? 0 : var2.length;
         boolean var5 = this.keymap.getDefaultAction() != null;
         if (var5) {
            ++var4;
         }

         Object[] var6;
         if (var3 == 0) {
            if (var5) {
               var6 = new Object[var4];
               if (var4 > 1) {
                  System.arraycopy(var2, 0, var6, 0, var4 - 1);
               }

               var6[var4 - 1] = JTextComponent.KeymapWrapper.DefaultActionKey;
               return var6;
            } else {
               return var2;
            }
         } else if (var4 == 0) {
            return var1;
         } else {
            var6 = new Object[var3 + var4];
            System.arraycopy(var1, 0, var6, 0, var3);
            if (var5) {
               if (var4 > 1) {
                  System.arraycopy(var2, 0, var6, var3, var4 - 1);
               }

               var6[var3 + var4 - 1] = JTextComponent.KeymapWrapper.DefaultActionKey;
            } else {
               System.arraycopy(var2, 0, var6, var3, var4);
            }

            return var6;
         }
      }

      public int size() {
         Action[] var1 = this.keymap.getBoundActions();
         int var2 = var1 == null ? 0 : var1.length;
         if (this.keymap.getDefaultAction() != null) {
            ++var2;
         }

         return super.size() + var2;
      }

      public Action get(Object var1) {
         Action var2 = super.get(var1);
         if (var2 == null) {
            if (var1 == JTextComponent.KeymapWrapper.DefaultActionKey) {
               var2 = this.keymap.getDefaultAction();
            } else if (var1 instanceof Action) {
               var2 = (Action)var1;
            }
         }

         return var2;
      }
   }

   static class KeymapWrapper extends InputMap {
      static final Object DefaultActionKey = new Object();
      private Keymap keymap;

      KeymapWrapper(Keymap var1) {
         this.keymap = var1;
      }

      public KeyStroke[] keys() {
         KeyStroke[] var1 = super.keys();
         KeyStroke[] var2 = this.keymap.getBoundKeyStrokes();
         int var3 = var1 == null ? 0 : var1.length;
         int var4 = var2 == null ? 0 : var2.length;
         if (var3 == 0) {
            return var2;
         } else if (var4 == 0) {
            return var1;
         } else {
            KeyStroke[] var5 = new KeyStroke[var3 + var4];
            System.arraycopy(var1, 0, var5, 0, var3);
            System.arraycopy(var2, 0, var5, var3, var4);
            return var5;
         }
      }

      public int size() {
         KeyStroke[] var1 = this.keymap.getBoundKeyStrokes();
         int var2 = var1 == null ? 0 : var1.length;
         return super.size() + var2;
      }

      public Object get(KeyStroke var1) {
         Object var2 = this.keymap.getAction(var1);
         if (var2 == null) {
            var2 = super.get(var1);
            if (var2 == null && var1.getKeyChar() != '\uffff' && this.keymap.getDefaultAction() != null) {
               var2 = DefaultActionKey;
            }
         }

         return var2;
      }
   }

   static class DefaultKeymap implements Keymap {
      String nm;
      Keymap parent;
      Hashtable<KeyStroke, Action> bindings;
      Action defaultAction;

      DefaultKeymap(String var1, Keymap var2) {
         this.nm = var1;
         this.parent = var2;
         this.bindings = new Hashtable();
      }

      public Action getDefaultAction() {
         if (this.defaultAction != null) {
            return this.defaultAction;
         } else {
            return this.parent != null ? this.parent.getDefaultAction() : null;
         }
      }

      public void setDefaultAction(Action var1) {
         this.defaultAction = var1;
      }

      public String getName() {
         return this.nm;
      }

      public Action getAction(KeyStroke var1) {
         Action var2 = (Action)this.bindings.get(var1);
         if (var2 == null && this.parent != null) {
            var2 = this.parent.getAction(var1);
         }

         return var2;
      }

      public KeyStroke[] getBoundKeyStrokes() {
         KeyStroke[] var1 = new KeyStroke[this.bindings.size()];
         int var2 = 0;

         for(Enumeration var3 = this.bindings.keys(); var3.hasMoreElements(); var1[var2++] = (KeyStroke)var3.nextElement()) {
         }

         return var1;
      }

      public Action[] getBoundActions() {
         Action[] var1 = new Action[this.bindings.size()];
         int var2 = 0;

         for(Enumeration var3 = this.bindings.elements(); var3.hasMoreElements(); var1[var2++] = (Action)var3.nextElement()) {
         }

         return var1;
      }

      public KeyStroke[] getKeyStrokesForAction(Action var1) {
         if (var1 == null) {
            return null;
         } else {
            KeyStroke[] var2 = null;
            Vector var3 = null;
            Enumeration var4 = this.bindings.keys();

            while(var4.hasMoreElements()) {
               KeyStroke var5 = (KeyStroke)var4.nextElement();
               if (this.bindings.get(var5) == var1) {
                  if (var3 == null) {
                     var3 = new Vector();
                  }

                  var3.addElement(var5);
               }
            }

            if (this.parent != null) {
               KeyStroke[] var7 = this.parent.getKeyStrokesForAction(var1);
               if (var7 != null) {
                  int var8 = 0;

                  int var6;
                  for(var6 = var7.length - 1; var6 >= 0; --var6) {
                     if (this.isLocallyDefined(var7[var6])) {
                        var7[var6] = null;
                        ++var8;
                     }
                  }

                  if (var8 > 0 && var8 < var7.length) {
                     if (var3 == null) {
                        var3 = new Vector();
                     }

                     for(var6 = var7.length - 1; var6 >= 0; --var6) {
                        if (var7[var6] != null) {
                           var3.addElement(var7[var6]);
                        }
                     }
                  } else if (var8 == 0) {
                     if (var3 == null) {
                        var2 = var7;
                     } else {
                        var2 = new KeyStroke[var3.size() + var7.length];
                        var3.copyInto(var2);
                        System.arraycopy(var7, 0, var2, var3.size(), var7.length);
                        var3 = null;
                     }
                  }
               }
            }

            if (var3 != null) {
               var2 = new KeyStroke[var3.size()];
               var3.copyInto(var2);
            }

            return var2;
         }
      }

      public boolean isLocallyDefined(KeyStroke var1) {
         return this.bindings.containsKey(var1);
      }

      public void addActionForKeyStroke(KeyStroke var1, Action var2) {
         this.bindings.put(var1, var2);
      }

      public void removeKeyStrokeBinding(KeyStroke var1) {
         this.bindings.remove(var1);
      }

      public void removeBindings() {
         this.bindings.clear();
      }

      public Keymap getResolveParent() {
         return this.parent;
      }

      public void setResolveParent(Keymap var1) {
         this.parent = var1;
      }

      public String toString() {
         return "Keymap[" + this.nm + "]" + this.bindings;
      }
   }

   static class DefaultTransferHandler extends TransferHandler implements UIResource {
      public void exportToClipboard(JComponent var1, Clipboard var2, int var3) throws IllegalStateException {
         if (var1 instanceof JTextComponent) {
            JTextComponent var4 = (JTextComponent)var1;
            int var5 = var4.getSelectionStart();
            int var6 = var4.getSelectionEnd();
            if (var5 != var6) {
               try {
                  Document var7 = var4.getDocument();
                  String var8 = var7.getText(var5, var6 - var5);
                  StringSelection var9 = new StringSelection(var8);
                  var2.setContents(var9, (ClipboardOwner)null);
                  if (var3 == 2) {
                     var7.remove(var5, var6 - var5);
                  }
               } catch (BadLocationException var10) {
               }
            }
         }

      }

      public boolean importData(JComponent var1, Transferable var2) {
         if (var1 instanceof JTextComponent) {
            DataFlavor var3 = this.getFlavor(var2.getTransferDataFlavors());
            if (var3 != null) {
               InputContext var4 = var1.getInputContext();
               if (var4 != null) {
                  var4.endComposition();
               }

               try {
                  String var5 = (String)var2.getTransferData(var3);
                  ((JTextComponent)var1).replaceSelection(var5);
                  return true;
               } catch (UnsupportedFlavorException var6) {
               } catch (IOException var7) {
               }
            }
         }

         return false;
      }

      public boolean canImport(JComponent var1, DataFlavor[] var2) {
         JTextComponent var3 = (JTextComponent)var1;
         if (var3.isEditable() && var3.isEnabled()) {
            return this.getFlavor(var2) != null;
         } else {
            return false;
         }
      }

      public int getSourceActions(JComponent var1) {
         return 0;
      }

      private DataFlavor getFlavor(DataFlavor[] var1) {
         if (var1 != null) {
            DataFlavor[] var2 = var1;
            int var3 = var1.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               DataFlavor var5 = var2[var4];
               if (var5.equals(DataFlavor.stringFlavor)) {
                  return var5;
               }
            }
         }

         return null;
      }
   }

   public static final class DropLocation extends TransferHandler.DropLocation {
      private final int index;
      private final Position.Bias bias;

      private DropLocation(Point var1, int var2, Position.Bias var3) {
         super(var1);
         this.index = var2;
         this.bias = var3;
      }

      public int getIndex() {
         return this.index;
      }

      public Position.Bias getBias() {
         return this.bias;
      }

      public String toString() {
         return this.getClass().getName() + "[dropPoint=" + this.getDropPoint() + ",index=" + this.index + ",bias=" + this.bias + "]";
      }

      // $FF: synthetic method
      DropLocation(Point var1, int var2, Position.Bias var3, Object var4) {
         this(var1, var2, var3);
      }
   }

   public class AccessibleJTextComponent extends JComponent.AccessibleJComponent implements AccessibleText, CaretListener, DocumentListener, AccessibleAction, AccessibleEditableText, AccessibleExtendedText {
      int caretPos;
      Point oldLocationOnScreen;

      public AccessibleJTextComponent() {
         super();
         Document var2 = JTextComponent.this.getDocument();
         if (var2 != null) {
            var2.addDocumentListener(this);
         }

         JTextComponent.this.addCaretListener(this);
         this.caretPos = this.getCaretPosition();

         try {
            this.oldLocationOnScreen = this.getLocationOnScreen();
         } catch (IllegalComponentStateException var4) {
         }

         JTextComponent.this.addComponentListener(new ComponentAdapter() {
            public void componentMoved(ComponentEvent var1) {
               try {
                  Point var2 = AccessibleJTextComponent.this.getLocationOnScreen();
                  AccessibleJTextComponent.this.firePropertyChange("AccessibleVisibleData", AccessibleJTextComponent.this.oldLocationOnScreen, var2);
                  AccessibleJTextComponent.this.oldLocationOnScreen = var2;
               } catch (IllegalComponentStateException var3) {
               }

            }
         });
      }

      public void caretUpdate(CaretEvent var1) {
         int var2 = var1.getDot();
         int var3 = var1.getMark();
         if (this.caretPos != var2) {
            this.firePropertyChange("AccessibleCaret", new Integer(this.caretPos), new Integer(var2));
            this.caretPos = var2;

            try {
               this.oldLocationOnScreen = this.getLocationOnScreen();
            } catch (IllegalComponentStateException var5) {
            }
         }

         if (var3 != var2) {
            this.firePropertyChange("AccessibleSelection", (Object)null, this.getSelectedText());
         }

      }

      public void insertUpdate(DocumentEvent var1) {
         final Integer var2 = new Integer(var1.getOffset());
         if (SwingUtilities.isEventDispatchThread()) {
            this.firePropertyChange("AccessibleText", (Object)null, var2);
         } else {
            Runnable var3 = new Runnable() {
               public void run() {
                  AccessibleJTextComponent.this.firePropertyChange("AccessibleText", (Object)null, var2);
               }
            };
            SwingUtilities.invokeLater(var3);
         }

      }

      public void removeUpdate(DocumentEvent var1) {
         final Integer var2 = new Integer(var1.getOffset());
         if (SwingUtilities.isEventDispatchThread()) {
            this.firePropertyChange("AccessibleText", (Object)null, var2);
         } else {
            Runnable var3 = new Runnable() {
               public void run() {
                  AccessibleJTextComponent.this.firePropertyChange("AccessibleText", (Object)null, var2);
               }
            };
            SwingUtilities.invokeLater(var3);
         }

      }

      public void changedUpdate(DocumentEvent var1) {
         final Integer var2 = new Integer(var1.getOffset());
         if (SwingUtilities.isEventDispatchThread()) {
            this.firePropertyChange("AccessibleText", (Object)null, var2);
         } else {
            Runnable var3 = new Runnable() {
               public void run() {
                  AccessibleJTextComponent.this.firePropertyChange("AccessibleText", (Object)null, var2);
               }
            };
            SwingUtilities.invokeLater(var3);
         }

      }

      public AccessibleStateSet getAccessibleStateSet() {
         AccessibleStateSet var1 = super.getAccessibleStateSet();
         if (JTextComponent.this.isEditable()) {
            var1.add(AccessibleState.EDITABLE);
         }

         return var1;
      }

      public AccessibleRole getAccessibleRole() {
         return AccessibleRole.TEXT;
      }

      public AccessibleText getAccessibleText() {
         return this;
      }

      public int getIndexAtPoint(Point var1) {
         return var1 == null ? -1 : JTextComponent.this.viewToModel(var1);
      }

      Rectangle getRootEditorRect() {
         Rectangle var1 = JTextComponent.this.getBounds();
         if (var1.width > 0 && var1.height > 0) {
            var1.x = var1.y = 0;
            Insets var2 = JTextComponent.this.getInsets();
            var1.x += var2.left;
            var1.y += var2.top;
            var1.width -= var2.left + var2.right;
            var1.height -= var2.top + var2.bottom;
            return var1;
         } else {
            return null;
         }
      }

      public Rectangle getCharacterBounds(int var1) {
         if (var1 >= 0 && var1 <= JTextComponent.this.model.getLength() - 1) {
            TextUI var2 = JTextComponent.this.getUI();
            if (var2 == null) {
               return null;
            } else {
               Rectangle var3 = null;
               Rectangle var4 = this.getRootEditorRect();
               if (var4 == null) {
                  return null;
               } else {
                  if (JTextComponent.this.model instanceof AbstractDocument) {
                     ((AbstractDocument)JTextComponent.this.model).readLock();
                  }

                  try {
                     View var5 = var2.getRootView(JTextComponent.this);
                     if (var5 != null) {
                        var5.setSize((float)var4.width, (float)var4.height);
                        Shape var6 = var5.modelToView(var1, Position.Bias.Forward, var1 + 1, Position.Bias.Backward, var4);
                        var3 = var6 instanceof Rectangle ? (Rectangle)var6 : var6.getBounds();
                     }
                  } catch (BadLocationException var10) {
                  } finally {
                     if (JTextComponent.this.model instanceof AbstractDocument) {
                        ((AbstractDocument)JTextComponent.this.model).readUnlock();
                     }

                  }

                  return var3;
               }
            }
         } else {
            return null;
         }
      }

      public int getCharCount() {
         return JTextComponent.this.model.getLength();
      }

      public int getCaretPosition() {
         return JTextComponent.this.getCaretPosition();
      }

      public AttributeSet getCharacterAttribute(int var1) {
         Element var2 = null;
         if (JTextComponent.this.model instanceof AbstractDocument) {
            ((AbstractDocument)JTextComponent.this.model).readLock();
         }

         int var3;
         try {
            for(var2 = JTextComponent.this.model.getDefaultRootElement(); !var2.isLeaf(); var2 = var2.getElement(var3)) {
               var3 = var2.getElementIndex(var1);
            }
         } finally {
            if (JTextComponent.this.model instanceof AbstractDocument) {
               ((AbstractDocument)JTextComponent.this.model).readUnlock();
            }

         }

         return var2.getAttributes();
      }

      public int getSelectionStart() {
         return JTextComponent.this.getSelectionStart();
      }

      public int getSelectionEnd() {
         return JTextComponent.this.getSelectionEnd();
      }

      public String getSelectedText() {
         return JTextComponent.this.getSelectedText();
      }

      public String getAtIndex(int var1, int var2) {
         return this.getAtIndex(var1, var2, 0);
      }

      public String getAfterIndex(int var1, int var2) {
         return this.getAtIndex(var1, var2, 1);
      }

      public String getBeforeIndex(int var1, int var2) {
         return this.getAtIndex(var1, var2, -1);
      }

      private String getAtIndex(int var1, int var2, int var3) {
         if (JTextComponent.this.model instanceof AbstractDocument) {
            ((AbstractDocument)JTextComponent.this.model).readLock();
         }

         String var12;
         try {
            JTextComponent.AccessibleJTextComponent.IndexedSegment var4;
            label198: {
               if (var2 >= 0 && var2 < JTextComponent.this.model.getLength()) {
                  switch(var1) {
                  case 1:
                     if (var2 + var3 >= JTextComponent.this.model.getLength() || var2 + var3 < 0) {
                        return null;
                     }

                     String var11 = JTextComponent.this.model.getText(var2 + var3, 1);
                     return var11;
                  case 2:
                  case 3:
                     var4 = this.getSegmentAt(var1, var2);
                     if (var4 != null) {
                        if (var3 == 0) {
                           break label198;
                        }

                        int var5;
                        if (var3 < 0) {
                           var5 = var4.modelOffset - 1;
                        } else {
                           var5 = var4.modelOffset + var3 * var4.count;
                        }

                        if (var5 >= 0 && var5 <= JTextComponent.this.model.getLength()) {
                           var4 = this.getSegmentAt(var1, var5);
                           break label198;
                        }

                        var4 = null;
                        break label198;
                     }

                     return null;
                  default:
                     return null;
                  }
               }

               var4 = null;
               return var4;
            }

            if (var4 == null) {
               return null;
            }

            var12 = new String(var4.array, var4.offset, var4.count);
         } catch (BadLocationException var9) {
            return null;
         } finally {
            if (JTextComponent.this.model instanceof AbstractDocument) {
               ((AbstractDocument)JTextComponent.this.model).readUnlock();
            }

         }

         return var12;
      }

      private Element getParagraphElement(int var1) {
         if (JTextComponent.this.model instanceof PlainDocument) {
            PlainDocument var5 = (PlainDocument)JTextComponent.this.model;
            return var5.getParagraphElement(var1);
         } else if (JTextComponent.this.model instanceof StyledDocument) {
            StyledDocument var4 = (StyledDocument)JTextComponent.this.model;
            return var4.getParagraphElement(var1);
         } else {
            Element var2;
            int var3;
            for(var2 = JTextComponent.this.model.getDefaultRootElement(); !var2.isLeaf(); var2 = var2.getElement(var3)) {
               var3 = var2.getElementIndex(var1);
            }

            return var2 == null ? null : var2.getParentElement();
         }
      }

      private JTextComponent.AccessibleJTextComponent.IndexedSegment getParagraphElementText(int var1) throws BadLocationException {
         Element var2 = this.getParagraphElement(var1);
         if (var2 != null) {
            JTextComponent.AccessibleJTextComponent.IndexedSegment var3 = new JTextComponent.AccessibleJTextComponent.IndexedSegment();

            try {
               int var4 = var2.getEndOffset() - var2.getStartOffset();
               JTextComponent.this.model.getText(var2.getStartOffset(), var4, var3);
            } catch (BadLocationException var5) {
               return null;
            }

            var3.modelOffset = var2.getStartOffset();
            return var3;
         } else {
            return null;
         }
      }

      private JTextComponent.AccessibleJTextComponent.IndexedSegment getSegmentAt(int var1, int var2) throws BadLocationException {
         JTextComponent.AccessibleJTextComponent.IndexedSegment var3 = this.getParagraphElementText(var2);
         if (var3 == null) {
            return null;
         } else {
            BreakIterator var4;
            switch(var1) {
            case 2:
               var4 = BreakIterator.getWordInstance(this.getLocale());
               break;
            case 3:
               var4 = BreakIterator.getSentenceInstance(this.getLocale());
               break;
            default:
               return null;
            }

            var3.first();
            var4.setText((CharacterIterator)var3);
            int var5 = var4.following(var2 - var3.modelOffset + var3.offset);
            if (var5 == -1) {
               return null;
            } else if (var5 > var3.offset + var3.count) {
               return null;
            } else {
               int var6 = var4.previous();
               if (var6 != -1 && var6 < var3.offset + var3.count) {
                  var3.modelOffset = var3.modelOffset + var6 - var3.offset;
                  var3.offset = var6;
                  var3.count = var5 - var6;
                  return var3;
               } else {
                  return null;
               }
            }
         }
      }

      public AccessibleEditableText getAccessibleEditableText() {
         return this;
      }

      public void setTextContents(String var1) {
         JTextComponent.this.setText(var1);
      }

      public void insertTextAtIndex(int var1, String var2) {
         Document var3 = JTextComponent.this.getDocument();
         if (var3 != null) {
            try {
               if (var2 != null && var2.length() > 0) {
                  boolean var4 = JTextComponent.this.saveComposedText(var1);
                  var3.insertString(var1, var2, (AttributeSet)null);
                  if (var4) {
                     JTextComponent.this.restoreComposedText();
                  }
               }
            } catch (BadLocationException var5) {
               UIManager.getLookAndFeel().provideErrorFeedback(JTextComponent.this);
            }
         }

      }

      public String getTextRange(int var1, int var2) {
         String var3 = null;
         int var4 = Math.min(var1, var2);
         int var5 = Math.max(var1, var2);
         if (var4 != var5) {
            try {
               Document var6 = JTextComponent.this.getDocument();
               var3 = var6.getText(var4, var5 - var4);
            } catch (BadLocationException var7) {
               throw new IllegalArgumentException(var7.getMessage());
            }
         }

         return var3;
      }

      public void delete(int var1, int var2) {
         if (JTextComponent.this.isEditable() && this.isEnabled()) {
            try {
               int var3 = Math.min(var1, var2);
               int var4 = Math.max(var1, var2);
               if (var3 != var4) {
                  Document var5 = JTextComponent.this.getDocument();
                  var5.remove(var3, var4 - var3);
               }
            } catch (BadLocationException var6) {
            }
         } else {
            UIManager.getLookAndFeel().provideErrorFeedback(JTextComponent.this);
         }

      }

      public void cut(int var1, int var2) {
         this.selectText(var1, var2);
         JTextComponent.this.cut();
      }

      public void paste(int var1) {
         JTextComponent.this.setCaretPosition(var1);
         JTextComponent.this.paste();
      }

      public void replaceText(int var1, int var2, String var3) {
         this.selectText(var1, var2);
         JTextComponent.this.replaceSelection(var3);
      }

      public void selectText(int var1, int var2) {
         JTextComponent.this.select(var1, var2);
      }

      public void setAttributes(int var1, int var2, AttributeSet var3) {
         Document var4 = JTextComponent.this.getDocument();
         if (var4 != null && var4 instanceof StyledDocument) {
            StyledDocument var5 = (StyledDocument)var4;
            int var7 = var2 - var1;
            var5.setCharacterAttributes(var1, var7, var3, true);
         }

      }

      private AccessibleTextSequence getSequenceAtIndex(int var1, int var2, int var3) {
         if (var2 >= 0 && var2 < JTextComponent.this.model.getLength()) {
            if (var3 >= -1 && var3 <= 1) {
               int var7;
               int var8;
               switch(var1) {
               case 1:
                  if (JTextComponent.this.model instanceof AbstractDocument) {
                     ((AbstractDocument)JTextComponent.this.model).readLock();
                  }

                  AccessibleTextSequence var4 = null;

                  try {
                     if (var2 + var3 < JTextComponent.this.model.getLength() && var2 + var3 >= 0) {
                        var4 = new AccessibleTextSequence(var2 + var3, var2 + var3 + 1, JTextComponent.this.model.getText(var2 + var3, 1));
                     }
                  } catch (BadLocationException var45) {
                  } finally {
                     if (JTextComponent.this.model instanceof AbstractDocument) {
                        ((AbstractDocument)JTextComponent.this.model).readUnlock();
                     }

                  }

                  return var4;
               case 2:
               case 3:
                  if (JTextComponent.this.model instanceof AbstractDocument) {
                     ((AbstractDocument)JTextComponent.this.model).readLock();
                  }

                  AccessibleTextSequence var5 = null;

                  try {
                     JTextComponent.AccessibleJTextComponent.IndexedSegment var53 = this.getSegmentAt(var1, var2);
                     if (var53 != null) {
                        if (var3 != 0) {
                           if (var3 < 0) {
                              var7 = var53.modelOffset - 1;
                           } else {
                              var7 = var53.modelOffset + var53.count;
                           }

                           if (var7 >= 0 && var7 <= JTextComponent.this.model.getLength()) {
                              var53 = this.getSegmentAt(var1, var7);
                           } else {
                              var53 = null;
                           }
                        }

                        if (var53 != null && var53.offset + var53.count <= JTextComponent.this.model.getLength()) {
                           var5 = new AccessibleTextSequence(var53.offset, var53.offset + var53.count, new String(var53.array, var53.offset, var53.count));
                        }
                     }
                  } catch (BadLocationException var51) {
                  } finally {
                     if (JTextComponent.this.model instanceof AbstractDocument) {
                        ((AbstractDocument)JTextComponent.this.model).readUnlock();
                     }

                  }

                  return var5;
               case 4:
                  AccessibleTextSequence var6 = null;
                  if (JTextComponent.this.model instanceof AbstractDocument) {
                     ((AbstractDocument)JTextComponent.this.model).readLock();
                  }

                  try {
                     var7 = Utilities.getRowStart(JTextComponent.this, var2);
                     var8 = Utilities.getRowEnd(JTextComponent.this, var2);
                     if (var7 >= 0 && var8 >= var7) {
                        if (var3 == 0) {
                           var6 = new AccessibleTextSequence(var7, var8, JTextComponent.this.model.getText(var7, var8 - var7 + 1));
                        } else if (var3 == -1 && var7 > 0) {
                           var8 = Utilities.getRowEnd(JTextComponent.this, var7 - 1);
                           var7 = Utilities.getRowStart(JTextComponent.this, var7 - 1);
                           if (var7 >= 0 && var8 >= var7) {
                              var6 = new AccessibleTextSequence(var7, var8, JTextComponent.this.model.getText(var7, var8 - var7 + 1));
                           }
                        } else if (var3 == 1 && var8 < JTextComponent.this.model.getLength()) {
                           var7 = Utilities.getRowStart(JTextComponent.this, var8 + 1);
                           var8 = Utilities.getRowEnd(JTextComponent.this, var8 + 1);
                           if (var7 >= 0 && var8 >= var7) {
                              var6 = new AccessibleTextSequence(var7, var8, JTextComponent.this.model.getText(var7, var8 - var7 + 1));
                           }
                        }
                     }
                  } catch (BadLocationException var49) {
                  } finally {
                     if (JTextComponent.this.model instanceof AbstractDocument) {
                        ((AbstractDocument)JTextComponent.this.model).readUnlock();
                     }

                  }

                  return var6;
               case 5:
                  String var9 = null;
                  if (JTextComponent.this.model instanceof AbstractDocument) {
                     ((AbstractDocument)JTextComponent.this.model).readLock();
                  }

                  Object var11;
                  try {
                     var8 = Integer.MIN_VALUE;
                     var7 = Integer.MIN_VALUE;
                     int var10 = var2;
                     switch(var3) {
                     case -1:
                        var8 = this.getRunEdge(var2, var3);
                        var10 = var8 - 1;
                     case 0:
                        break;
                     case 1:
                        var7 = this.getRunEdge(var2, var3);
                        var10 = var7;
                        break;
                     default:
                        throw new AssertionError(var3);
                     }

                     var7 = var7 != Integer.MIN_VALUE ? var7 : this.getRunEdge(var10, -1);
                     var8 = var8 != Integer.MIN_VALUE ? var8 : this.getRunEdge(var10, 1);
                     var9 = JTextComponent.this.model.getText(var7, var8 - var7);
                     return new AccessibleTextSequence(var7, var8, var9);
                  } catch (BadLocationException var47) {
                     var11 = null;
                  } finally {
                     if (JTextComponent.this.model instanceof AbstractDocument) {
                        ((AbstractDocument)JTextComponent.this.model).readUnlock();
                     }

                  }

                  return (AccessibleTextSequence)var11;
               default:
                  return null;
               }
            } else {
               return null;
            }
         } else {
            return null;
         }
      }

      private int getRunEdge(int var1, int var2) throws BadLocationException {
         if (var1 >= 0 && var1 < JTextComponent.this.model.getLength()) {
            int var4 = -1;

            Element var3;
            for(var3 = JTextComponent.this.model.getDefaultRootElement(); !var3.isLeaf(); var3 = var3.getElement(var4)) {
               var4 = var3.getElementIndex(var1);
            }

            if (var4 == -1) {
               throw new AssertionError(var1);
            } else {
               AttributeSet var5 = var3.getAttributes();
               Element var6 = var3.getParentElement();
               switch(var2) {
               case -1:
               case 1:
                  int var8 = var4;

                  for(int var9 = var6.getElementCount(); var8 + var2 > 0 && var8 + var2 < var9 && var6.getElement(var8 + var2).getAttributes().isEqual(var5); var8 += var2) {
                  }

                  Element var7 = var6.getElement(var8);
                  switch(var2) {
                  case -1:
                     return var7.getStartOffset();
                  case 1:
                     return var7.getEndOffset();
                  default:
                     return Integer.MIN_VALUE;
                  }
               default:
                  throw new AssertionError(var2);
               }
            }
         } else {
            throw new BadLocationException("Location out of bounds", var1);
         }
      }

      public AccessibleTextSequence getTextSequenceAt(int var1, int var2) {
         return this.getSequenceAtIndex(var1, var2, 0);
      }

      public AccessibleTextSequence getTextSequenceAfter(int var1, int var2) {
         return this.getSequenceAtIndex(var1, var2, 1);
      }

      public AccessibleTextSequence getTextSequenceBefore(int var1, int var2) {
         return this.getSequenceAtIndex(var1, var2, -1);
      }

      public Rectangle getTextBounds(int var1, int var2) {
         if (var1 >= 0 && var1 <= JTextComponent.this.model.getLength() - 1 && var2 >= 0 && var2 <= JTextComponent.this.model.getLength() - 1 && var1 <= var2) {
            TextUI var3 = JTextComponent.this.getUI();
            if (var3 == null) {
               return null;
            } else {
               Rectangle var4 = null;
               Rectangle var5 = this.getRootEditorRect();
               if (var5 == null) {
                  return null;
               } else {
                  if (JTextComponent.this.model instanceof AbstractDocument) {
                     ((AbstractDocument)JTextComponent.this.model).readLock();
                  }

                  try {
                     View var6 = var3.getRootView(JTextComponent.this);
                     if (var6 != null) {
                        Shape var7 = var6.modelToView(var1, Position.Bias.Forward, var2, Position.Bias.Backward, var5);
                        var4 = var7 instanceof Rectangle ? (Rectangle)var7 : var7.getBounds();
                     }
                  } catch (BadLocationException var11) {
                  } finally {
                     if (JTextComponent.this.model instanceof AbstractDocument) {
                        ((AbstractDocument)JTextComponent.this.model).readUnlock();
                     }

                  }

                  return var4;
               }
            }
         } else {
            return null;
         }
      }

      public AccessibleAction getAccessibleAction() {
         return this;
      }

      public int getAccessibleActionCount() {
         Action[] var1 = JTextComponent.this.getActions();
         return var1.length;
      }

      public String getAccessibleActionDescription(int var1) {
         Action[] var2 = JTextComponent.this.getActions();
         return var1 >= 0 && var1 < var2.length ? (String)var2[var1].getValue("Name") : null;
      }

      public boolean doAccessibleAction(int var1) {
         Action[] var2 = JTextComponent.this.getActions();
         if (var1 >= 0 && var1 < var2.length) {
            ActionEvent var3 = new ActionEvent(JTextComponent.this, 1001, (String)null, EventQueue.getMostRecentEventTime(), JTextComponent.this.getCurrentEventModifiers());
            var2[var1].actionPerformed(var3);
            return true;
         } else {
            return false;
         }
      }

      private class IndexedSegment extends Segment {
         public int modelOffset;

         private IndexedSegment() {
         }

         // $FF: synthetic method
         IndexedSegment(Object var2) {
            this();
         }
      }
   }

   public static class KeyBinding {
      public KeyStroke key;
      public String actionName;

      public KeyBinding(KeyStroke var1, String var2) {
         this.key = var1;
         this.actionName = var2;
      }
   }
}
