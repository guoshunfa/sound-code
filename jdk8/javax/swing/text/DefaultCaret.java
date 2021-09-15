package javax.swing.text;

import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.EventListener;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JPasswordField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.EventListenerList;
import javax.swing.plaf.TextUI;
import sun.swing.SwingUtilities2;

public class DefaultCaret extends Rectangle implements Caret, FocusListener, MouseListener, MouseMotionListener {
   public static final int UPDATE_WHEN_ON_EDT = 0;
   public static final int NEVER_UPDATE = 1;
   public static final int ALWAYS_UPDATE = 2;
   protected EventListenerList listenerList = new EventListenerList();
   protected transient ChangeEvent changeEvent = null;
   JTextComponent component;
   int updatePolicy = 0;
   boolean visible;
   boolean active;
   int dot;
   int mark;
   Object selectionTag;
   boolean selectionVisible;
   Timer flasher;
   Point magicCaretPosition;
   transient Position.Bias dotBias;
   transient Position.Bias markBias;
   boolean dotLTR;
   boolean markLTR;
   transient DefaultCaret.Handler handler = new DefaultCaret.Handler();
   private transient int[] flagXPoints = new int[3];
   private transient int[] flagYPoints = new int[3];
   private transient NavigationFilter.FilterBypass filterBypass;
   private static transient Action selectWord = null;
   private static transient Action selectLine = null;
   private boolean ownsSelection;
   private boolean forceCaretPositionChange;
   private transient boolean shouldHandleRelease;
   private transient MouseEvent selectedWordEvent = null;
   private int caretWidth = -1;
   private float aspectRatio = -1.0F;

   public void setUpdatePolicy(int var1) {
      this.updatePolicy = var1;
   }

   public int getUpdatePolicy() {
      return this.updatePolicy;
   }

   protected final JTextComponent getComponent() {
      return this.component;
   }

   protected final synchronized void repaint() {
      if (this.component != null) {
         this.component.repaint(this.x, this.y, this.width, this.height);
      }

   }

   protected synchronized void damage(Rectangle var1) {
      if (var1 != null) {
         int var2 = this.getCaretWidth(var1.height);
         this.x = var1.x - 4 - (var2 >> 1);
         this.y = var1.y;
         this.width = 9 + var2;
         this.height = var1.height;
         this.repaint();
      }

   }

   protected void adjustVisibility(Rectangle var1) {
      if (this.component != null) {
         if (SwingUtilities.isEventDispatchThread()) {
            this.component.scrollRectToVisible(var1);
         } else {
            SwingUtilities.invokeLater(new DefaultCaret.SafeScroller(var1));
         }

      }
   }

   protected Highlighter.HighlightPainter getSelectionPainter() {
      return DefaultHighlighter.DefaultPainter;
   }

   protected void positionCaret(MouseEvent var1) {
      Point var2 = new Point(var1.getX(), var1.getY());
      Position.Bias[] var3 = new Position.Bias[1];
      int var4 = this.component.getUI().viewToModel(this.component, var2, var3);
      if (var3[0] == null) {
         var3[0] = Position.Bias.Forward;
      }

      if (var4 >= 0) {
         this.setDot(var4, var3[0]);
      }

   }

   protected void moveCaret(MouseEvent var1) {
      Point var2 = new Point(var1.getX(), var1.getY());
      Position.Bias[] var3 = new Position.Bias[1];
      int var4 = this.component.getUI().viewToModel(this.component, var2, var3);
      if (var3[0] == null) {
         var3[0] = Position.Bias.Forward;
      }

      if (var4 >= 0) {
         this.moveDot(var4, var3[0]);
      }

   }

   public void focusGained(FocusEvent var1) {
      if (this.component.isEnabled()) {
         if (this.component.isEditable()) {
            this.setVisible(true);
         }

         this.setSelectionVisible(true);
      }

   }

   public void focusLost(FocusEvent var1) {
      this.setVisible(false);
      this.setSelectionVisible(this.ownsSelection || var1.isTemporary());
   }

   private void selectWord(MouseEvent var1) {
      if (this.selectedWordEvent == null || this.selectedWordEvent.getX() != var1.getX() || this.selectedWordEvent.getY() != var1.getY()) {
         Action var2 = null;
         ActionMap var3 = this.getComponent().getActionMap();
         if (var3 != null) {
            var2 = var3.get("select-word");
         }

         if (var2 == null) {
            if (selectWord == null) {
               selectWord = new DefaultEditorKit.SelectWordAction();
            }

            var2 = selectWord;
         }

         var2.actionPerformed(new ActionEvent(this.getComponent(), 1001, (String)null, var1.getWhen(), var1.getModifiers()));
         this.selectedWordEvent = var1;
      }
   }

   public void mouseClicked(MouseEvent var1) {
      if (this.getComponent() != null) {
         int var2 = SwingUtilities2.getAdjustedClickCount(this.getComponent(), var1);
         if (!var1.isConsumed()) {
            if (SwingUtilities.isLeftMouseButton(var1)) {
               if (var2 == 1) {
                  this.selectedWordEvent = null;
               } else if (var2 == 2 && SwingUtilities2.canEventAccessSystemClipboard(var1)) {
                  this.selectWord(var1);
                  this.selectedWordEvent = null;
               } else if (var2 == 3 && SwingUtilities2.canEventAccessSystemClipboard(var1)) {
                  Action var3 = null;
                  ActionMap var4 = this.getComponent().getActionMap();
                  if (var4 != null) {
                     var3 = var4.get("select-line");
                  }

                  if (var3 == null) {
                     if (selectLine == null) {
                        selectLine = new DefaultEditorKit.SelectLineAction();
                     }

                     var3 = selectLine;
                  }

                  var3.actionPerformed(new ActionEvent(this.getComponent(), 1001, (String)null, var1.getWhen(), var1.getModifiers()));
               }
            } else if (SwingUtilities.isMiddleMouseButton(var1) && var2 == 1 && this.component.isEditable() && this.component.isEnabled() && SwingUtilities2.canEventAccessSystemClipboard(var1)) {
               JTextComponent var12 = (JTextComponent)var1.getSource();
               if (var12 != null) {
                  try {
                     Toolkit var11 = var12.getToolkit();
                     Clipboard var5 = var11.getSystemSelection();
                     if (var5 != null) {
                        this.adjustCaret(var1);
                        TransferHandler var6 = var12.getTransferHandler();
                        if (var6 != null) {
                           Transferable var7 = null;

                           try {
                              var7 = var5.getContents((Object)null);
                           } catch (IllegalStateException var9) {
                              UIManager.getLookAndFeel().provideErrorFeedback(var12);
                           }

                           if (var7 != null) {
                              var6.importData(var12, var7);
                           }
                        }

                        this.adjustFocus(true);
                     }
                  } catch (HeadlessException var10) {
                  }
               }
            }
         }

      }
   }

   public void mousePressed(MouseEvent var1) {
      int var2 = SwingUtilities2.getAdjustedClickCount(this.getComponent(), var1);
      if (SwingUtilities.isLeftMouseButton(var1)) {
         if (var1.isConsumed()) {
            this.shouldHandleRelease = true;
         } else {
            this.shouldHandleRelease = false;
            this.adjustCaretAndFocus(var1);
            if (var2 == 2 && SwingUtilities2.canEventAccessSystemClipboard(var1)) {
               this.selectWord(var1);
            }
         }
      }

   }

   void adjustCaretAndFocus(MouseEvent var1) {
      this.adjustCaret(var1);
      this.adjustFocus(false);
   }

   private void adjustCaret(MouseEvent var1) {
      if ((var1.getModifiers() & 1) != 0 && this.getDot() != -1) {
         this.moveCaret(var1);
      } else if (!var1.isPopupTrigger()) {
         this.positionCaret(var1);
      }

   }

   private void adjustFocus(boolean var1) {
      if (this.component != null && this.component.isEnabled() && this.component.isRequestFocusEnabled()) {
         if (var1) {
            this.component.requestFocusInWindow();
         } else {
            this.component.requestFocus();
         }
      }

   }

   public void mouseReleased(MouseEvent var1) {
      if (!var1.isConsumed() && this.shouldHandleRelease && SwingUtilities.isLeftMouseButton(var1)) {
         this.adjustCaretAndFocus(var1);
      }

   }

   public void mouseEntered(MouseEvent var1) {
   }

   public void mouseExited(MouseEvent var1) {
   }

   public void mouseDragged(MouseEvent var1) {
      if (!var1.isConsumed() && SwingUtilities.isLeftMouseButton(var1)) {
         this.moveCaret(var1);
      }

   }

   public void mouseMoved(MouseEvent var1) {
   }

   public void paint(Graphics var1) {
      if (this.isVisible()) {
         try {
            TextUI var2 = this.component.getUI();
            Rectangle var3 = var2.modelToView(this.component, this.dot, this.dotBias);
            if (var3 == null || var3.width == 0 && var3.height == 0) {
               return;
            }

            if (this.width > 0 && this.height > 0 && !this._contains(var3.x, var3.y, var3.width, var3.height)) {
               Rectangle var4 = var1.getClipBounds();
               if (var4 != null && !var4.contains((Rectangle)this)) {
                  this.repaint();
               }

               this.damage(var3);
            }

            var1.setColor(this.component.getCaretColor());
            int var8 = this.getCaretWidth(var3.height);
            var3.x -= var8 >> 1;
            var1.fillRect(var3.x, var3.y, var8, var3.height);
            Document var5 = this.component.getDocument();
            if (var5 instanceof AbstractDocument) {
               Element var6 = ((AbstractDocument)var5).getBidiRootElement();
               if (var6 != null && var6.getElementCount() > 1) {
                  this.flagXPoints[0] = var3.x + (this.dotLTR ? var8 : 0);
                  this.flagYPoints[0] = var3.y;
                  this.flagXPoints[1] = this.flagXPoints[0];
                  this.flagYPoints[1] = this.flagYPoints[0] + 4;
                  this.flagXPoints[2] = this.flagXPoints[0] + (this.dotLTR ? 4 : -4);
                  this.flagYPoints[2] = this.flagYPoints[0];
                  var1.fillPolygon(this.flagXPoints, this.flagYPoints, 3);
               }
            }
         } catch (BadLocationException var7) {
         }
      }

   }

   public void install(JTextComponent var1) {
      this.component = var1;
      Document var2 = var1.getDocument();
      this.dot = this.mark = 0;
      this.dotLTR = this.markLTR = true;
      this.dotBias = this.markBias = Position.Bias.Forward;
      if (var2 != null) {
         var2.addDocumentListener(this.handler);
      }

      var1.addPropertyChangeListener(this.handler);
      var1.addFocusListener(this);
      var1.addMouseListener(this);
      var1.addMouseMotionListener(this);
      if (this.component.hasFocus()) {
         this.focusGained((FocusEvent)null);
      }

      Number var3 = (Number)var1.getClientProperty("caretAspectRatio");
      if (var3 != null) {
         this.aspectRatio = var3.floatValue();
      } else {
         this.aspectRatio = -1.0F;
      }

      Integer var4 = (Integer)var1.getClientProperty("caretWidth");
      if (var4 != null) {
         this.caretWidth = var4;
      } else {
         this.caretWidth = -1;
      }

   }

   public void deinstall(JTextComponent var1) {
      var1.removeMouseListener(this);
      var1.removeMouseMotionListener(this);
      var1.removeFocusListener(this);
      var1.removePropertyChangeListener(this.handler);
      Document var2 = var1.getDocument();
      if (var2 != null) {
         var2.removeDocumentListener(this.handler);
      }

      synchronized(this) {
         this.component = null;
      }

      if (this.flasher != null) {
         this.flasher.stop();
      }

   }

   public void addChangeListener(ChangeListener var1) {
      this.listenerList.add(ChangeListener.class, var1);
   }

   public void removeChangeListener(ChangeListener var1) {
      this.listenerList.remove(ChangeListener.class, var1);
   }

   public ChangeListener[] getChangeListeners() {
      return (ChangeListener[])this.listenerList.getListeners(ChangeListener.class);
   }

   protected void fireStateChanged() {
      Object[] var1 = this.listenerList.getListenerList();

      for(int var2 = var1.length - 2; var2 >= 0; var2 -= 2) {
         if (var1[var2] == ChangeListener.class) {
            if (this.changeEvent == null) {
               this.changeEvent = new ChangeEvent(this);
            }

            ((ChangeListener)var1[var2 + 1]).stateChanged(this.changeEvent);
         }
      }

   }

   public <T extends EventListener> T[] getListeners(Class<T> var1) {
      return this.listenerList.getListeners(var1);
   }

   public void setSelectionVisible(boolean var1) {
      if (var1 != this.selectionVisible) {
         this.selectionVisible = var1;
         Highlighter var2;
         if (this.selectionVisible) {
            var2 = this.component.getHighlighter();
            if (this.dot != this.mark && var2 != null && this.selectionTag == null) {
               int var3 = Math.min(this.dot, this.mark);
               int var4 = Math.max(this.dot, this.mark);
               Highlighter.HighlightPainter var5 = this.getSelectionPainter();

               try {
                  this.selectionTag = var2.addHighlight(var3, var4, var5);
               } catch (BadLocationException var7) {
                  this.selectionTag = null;
               }
            }
         } else if (this.selectionTag != null) {
            var2 = this.component.getHighlighter();
            var2.removeHighlight(this.selectionTag);
            this.selectionTag = null;
         }
      }

   }

   public boolean isSelectionVisible() {
      return this.selectionVisible;
   }

   public boolean isActive() {
      return this.active;
   }

   public boolean isVisible() {
      return this.visible;
   }

   public void setVisible(boolean var1) {
      this.active = var1;
      if (this.component != null) {
         TextUI var2 = this.component.getUI();
         if (this.visible != var1) {
            this.visible = var1;

            try {
               Rectangle var3 = var2.modelToView(this.component, this.dot, this.dotBias);
               this.damage(var3);
            } catch (BadLocationException var4) {
            }
         }
      }

      if (this.flasher != null) {
         if (this.visible) {
            this.flasher.start();
         } else {
            this.flasher.stop();
         }
      }

   }

   public void setBlinkRate(int var1) {
      if (var1 != 0) {
         if (this.flasher == null) {
            this.flasher = new Timer(var1, this.handler);
         }

         this.flasher.setDelay(var1);
      } else if (this.flasher != null) {
         this.flasher.stop();
         this.flasher.removeActionListener(this.handler);
         this.flasher = null;
      }

   }

   public int getBlinkRate() {
      return this.flasher == null ? 0 : this.flasher.getDelay();
   }

   public int getDot() {
      return this.dot;
   }

   public int getMark() {
      return this.mark;
   }

   public void setDot(int var1) {
      this.setDot(var1, Position.Bias.Forward);
   }

   public void moveDot(int var1) {
      this.moveDot(var1, Position.Bias.Forward);
   }

   public void moveDot(int var1, Position.Bias var2) {
      if (var2 == null) {
         throw new IllegalArgumentException("null bias");
      } else if (!this.component.isEnabled()) {
         this.setDot(var1, var2);
      } else {
         if (var1 != this.dot) {
            NavigationFilter var3 = this.component.getNavigationFilter();
            if (var3 != null) {
               var3.moveDot(this.getFilterBypass(), var1, var2);
            } else {
               this.handleMoveDot(var1, var2);
            }
         }

      }
   }

   void handleMoveDot(int var1, Position.Bias var2) {
      this.changeCaretPosition(var1, var2);
      if (this.selectionVisible) {
         Highlighter var3 = this.component.getHighlighter();
         if (var3 != null) {
            int var4 = Math.min(var1, this.mark);
            int var5 = Math.max(var1, this.mark);
            if (var4 == var5) {
               if (this.selectionTag != null) {
                  var3.removeHighlight(this.selectionTag);
                  this.selectionTag = null;
               }
            } else {
               try {
                  if (this.selectionTag != null) {
                     var3.changeHighlight(this.selectionTag, var4, var5);
                  } else {
                     Highlighter.HighlightPainter var6 = this.getSelectionPainter();
                     this.selectionTag = var3.addHighlight(var4, var5, var6);
                  }
               } catch (BadLocationException var7) {
                  throw new StateInvariantError("Bad caret position");
               }
            }
         }
      }

   }

   public void setDot(int var1, Position.Bias var2) {
      if (var2 == null) {
         throw new IllegalArgumentException("null bias");
      } else {
         NavigationFilter var3 = this.component.getNavigationFilter();
         if (var3 != null) {
            var3.setDot(this.getFilterBypass(), var1, var2);
         } else {
            this.handleSetDot(var1, var2);
         }

      }
   }

   void handleSetDot(int var1, Position.Bias var2) {
      Document var3 = this.component.getDocument();
      if (var3 != null) {
         var1 = Math.min(var1, var3.getLength());
      }

      var1 = Math.max(var1, 0);
      if (var1 == 0) {
         var2 = Position.Bias.Forward;
      }

      this.mark = var1;
      if (this.dot != var1 || this.dotBias != var2 || this.selectionTag != null || this.forceCaretPositionChange) {
         this.changeCaretPosition(var1, var2);
      }

      this.markBias = this.dotBias;
      this.markLTR = this.dotLTR;
      Highlighter var4 = this.component.getHighlighter();
      if (var4 != null && this.selectionTag != null) {
         var4.removeHighlight(this.selectionTag);
         this.selectionTag = null;
      }

   }

   public Position.Bias getDotBias() {
      return this.dotBias;
   }

   public Position.Bias getMarkBias() {
      return this.markBias;
   }

   boolean isDotLeftToRight() {
      return this.dotLTR;
   }

   boolean isMarkLeftToRight() {
      return this.markLTR;
   }

   boolean isPositionLTR(int var1, Position.Bias var2) {
      Document var3 = this.component.getDocument();
      if (var2 == Position.Bias.Backward) {
         --var1;
         if (var1 < 0) {
            var1 = 0;
         }
      }

      return AbstractDocument.isLeftToRight(var3, var1, var1);
   }

   Position.Bias guessBiasForOffset(int var1, Position.Bias var2, boolean var3) {
      if (var3 != this.isPositionLTR(var1, var2)) {
         var2 = Position.Bias.Backward;
      } else if (var2 != Position.Bias.Backward && var3 != this.isPositionLTR(var1, Position.Bias.Backward)) {
         var2 = Position.Bias.Backward;
      }

      if (var2 == Position.Bias.Backward && var1 > 0) {
         try {
            Segment var4 = new Segment();
            this.component.getDocument().getText(var1 - 1, 1, var4);
            if (var4.count > 0 && var4.array[var4.offset] == '\n') {
               var2 = Position.Bias.Forward;
            }
         } catch (BadLocationException var5) {
         }
      }

      return var2;
   }

   void changeCaretPosition(int var1, Position.Bias var2) {
      this.repaint();
      if (this.flasher != null && this.flasher.isRunning()) {
         this.visible = true;
         this.flasher.restart();
      }

      this.dot = var1;
      this.dotBias = var2;
      this.dotLTR = this.isPositionLTR(var1, var2);
      this.fireStateChanged();
      this.updateSystemSelection();
      this.setMagicCaretPosition((Point)null);
      Runnable var3 = new Runnable() {
         public void run() {
            DefaultCaret.this.repaintNewCaret();
         }
      };
      SwingUtilities.invokeLater(var3);
   }

   void repaintNewCaret() {
      if (this.component != null) {
         TextUI var1 = this.component.getUI();
         Document var2 = this.component.getDocument();
         if (var1 != null && var2 != null) {
            Rectangle var3;
            try {
               var3 = var1.modelToView(this.component, this.dot, this.dotBias);
            } catch (BadLocationException var5) {
               var3 = null;
            }

            if (var3 != null) {
               this.adjustVisibility(var3);
               if (this.getMagicCaretPosition() == null) {
                  this.setMagicCaretPosition(new Point(var3.x, var3.y));
               }
            }

            this.damage(var3);
         }
      }

   }

   private void updateSystemSelection() {
      if (SwingUtilities2.canCurrentEventAccessSystemClipboard()) {
         if (this.dot != this.mark && this.component != null && this.component.hasFocus()) {
            Clipboard var1 = this.getSystemSelection();
            if (var1 != null) {
               String var2;
               if (this.component instanceof JPasswordField && this.component.getClientProperty("JPasswordField.cutCopyAllowed") != Boolean.TRUE) {
                  StringBuilder var3 = null;
                  char var4 = ((JPasswordField)this.component).getEchoChar();
                  int var5 = Math.min(this.getDot(), this.getMark());
                  int var6 = Math.max(this.getDot(), this.getMark());

                  for(int var7 = var5; var7 < var6; ++var7) {
                     if (var3 == null) {
                        var3 = new StringBuilder();
                     }

                     var3.append(var4);
                  }

                  var2 = var3 != null ? var3.toString() : null;
               } else {
                  var2 = this.component.getSelectedText();
               }

               try {
                  var1.setContents(new StringSelection(var2), this.getClipboardOwner());
                  this.ownsSelection = true;
               } catch (IllegalStateException var8) {
               }
            }
         }

      }
   }

   private Clipboard getSystemSelection() {
      try {
         return this.component.getToolkit().getSystemSelection();
      } catch (HeadlessException var2) {
      } catch (SecurityException var3) {
      }

      return null;
   }

   private ClipboardOwner getClipboardOwner() {
      return this.handler;
   }

   private void ensureValidPosition() {
      int var1 = this.component.getDocument().getLength();
      if (this.dot > var1 || this.mark > var1) {
         this.handleSetDot(var1, Position.Bias.Forward);
      }

   }

   public void setMagicCaretPosition(Point var1) {
      this.magicCaretPosition = var1;
   }

   public Point getMagicCaretPosition() {
      return this.magicCaretPosition;
   }

   public boolean equals(Object var1) {
      return this == var1;
   }

   public String toString() {
      String var1 = "Dot=(" + this.dot + ", " + this.dotBias + ")";
      var1 = var1 + " Mark=(" + this.mark + ", " + this.markBias + ")";
      return var1;
   }

   private NavigationFilter.FilterBypass getFilterBypass() {
      if (this.filterBypass == null) {
         this.filterBypass = new DefaultCaret.DefaultFilterBypass();
      }

      return this.filterBypass;
   }

   private boolean _contains(int var1, int var2, int var3, int var4) {
      int var5 = this.width;
      int var6 = this.height;
      if ((var5 | var6 | var3 | var4) < 0) {
         return false;
      } else {
         int var7 = this.x;
         int var8 = this.y;
         if (var1 >= var7 && var2 >= var8) {
            if (var3 > 0) {
               var5 += var7;
               var3 += var1;
               if (var3 <= var1) {
                  if (var5 >= var7 || var3 > var5) {
                     return false;
                  }
               } else if (var5 >= var7 && var3 > var5) {
                  return false;
               }
            } else if (var7 + var5 < var1) {
               return false;
            }

            if (var4 > 0) {
               var6 += var8;
               var4 += var2;
               if (var4 <= var2) {
                  if (var6 >= var8 || var4 > var6) {
                     return false;
                  }
               } else if (var6 >= var8 && var4 > var6) {
                  return false;
               }
            } else if (var8 + var6 < var2) {
               return false;
            }

            return true;
         } else {
            return false;
         }
      }
   }

   int getCaretWidth(int var1) {
      if (this.aspectRatio > -1.0F) {
         return (int)(this.aspectRatio * (float)var1) + 1;
      } else if (this.caretWidth > -1) {
         return this.caretWidth;
      } else {
         Object var2 = UIManager.get("Caret.width");
         return var2 instanceof Integer ? (Integer)var2 : 1;
      }
   }

   private void readObject(ObjectInputStream var1) throws ClassNotFoundException, IOException {
      var1.defaultReadObject();
      this.handler = new DefaultCaret.Handler();
      if (!var1.readBoolean()) {
         this.dotBias = Position.Bias.Forward;
      } else {
         this.dotBias = Position.Bias.Backward;
      }

      if (!var1.readBoolean()) {
         this.markBias = Position.Bias.Forward;
      } else {
         this.markBias = Position.Bias.Backward;
      }

   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeBoolean(this.dotBias == Position.Bias.Backward);
      var1.writeBoolean(this.markBias == Position.Bias.Backward);
   }

   private class DefaultFilterBypass extends NavigationFilter.FilterBypass {
      private DefaultFilterBypass() {
      }

      public Caret getCaret() {
         return DefaultCaret.this;
      }

      public void setDot(int var1, Position.Bias var2) {
         DefaultCaret.this.handleSetDot(var1, var2);
      }

      public void moveDot(int var1, Position.Bias var2) {
         DefaultCaret.this.handleMoveDot(var1, var2);
      }

      // $FF: synthetic method
      DefaultFilterBypass(Object var2) {
         this();
      }
   }

   class Handler implements PropertyChangeListener, DocumentListener, ActionListener, ClipboardOwner {
      public void actionPerformed(ActionEvent var1) {
         if ((DefaultCaret.this.width == 0 || DefaultCaret.this.height == 0) && DefaultCaret.this.component != null) {
            TextUI var2 = DefaultCaret.this.component.getUI();

            try {
               Rectangle var3 = var2.modelToView(DefaultCaret.this.component, DefaultCaret.this.dot, DefaultCaret.this.dotBias);
               if (var3 != null && var3.width != 0 && var3.height != 0) {
                  DefaultCaret.this.damage(var3);
               }
            } catch (BadLocationException var4) {
            }
         }

         DefaultCaret.this.visible = !DefaultCaret.this.visible;
         DefaultCaret.this.repaint();
      }

      public void insertUpdate(DocumentEvent var1) {
         if (DefaultCaret.this.getUpdatePolicy() == 1 || DefaultCaret.this.getUpdatePolicy() == 0 && !SwingUtilities.isEventDispatchThread()) {
            if ((var1.getOffset() <= DefaultCaret.this.dot || var1.getOffset() <= DefaultCaret.this.mark) && DefaultCaret.this.selectionTag != null) {
               try {
                  DefaultCaret.this.component.getHighlighter().changeHighlight(DefaultCaret.this.selectionTag, Math.min(DefaultCaret.this.dot, DefaultCaret.this.mark), Math.max(DefaultCaret.this.dot, DefaultCaret.this.mark));
               } catch (BadLocationException var11) {
                  var11.printStackTrace();
               }
            }

         } else {
            int var2 = var1.getOffset();
            int var3 = var1.getLength();
            int var4 = DefaultCaret.this.dot;
            short var5 = 0;
            if (var1 instanceof AbstractDocument.UndoRedoDocumentEvent) {
               DefaultCaret.this.setDot(var2 + var3);
            } else {
               if (var4 >= var2) {
                  var4 += var3;
                  var5 = (short)(var5 | 1);
               }

               int var6 = DefaultCaret.this.mark;
               if (var6 >= var2) {
                  var6 += var3;
                  var5 = (short)(var5 | 2);
               }

               if (var5 != 0) {
                  Position.Bias var7 = DefaultCaret.this.dotBias;
                  if (DefaultCaret.this.dot == var2) {
                     Document var8 = DefaultCaret.this.component.getDocument();

                     boolean var9;
                     try {
                        Segment var10 = new Segment();
                        var8.getText(var4 - 1, 1, var10);
                        var9 = var10.count > 0 && var10.array[var10.offset] == '\n';
                     } catch (BadLocationException var12) {
                        var9 = false;
                     }

                     if (var9) {
                        var7 = Position.Bias.Forward;
                     } else {
                        var7 = Position.Bias.Backward;
                     }
                  }

                  if (var6 == var4) {
                     DefaultCaret.this.setDot(var4, var7);
                     DefaultCaret.this.ensureValidPosition();
                  } else {
                     DefaultCaret.this.setDot(var6, DefaultCaret.this.markBias);
                     if (DefaultCaret.this.getDot() == var6) {
                        DefaultCaret.this.moveDot(var4, var7);
                     }

                     DefaultCaret.this.ensureValidPosition();
                  }
               }

            }
         }
      }

      public void removeUpdate(DocumentEvent var1) {
         int var2;
         if (DefaultCaret.this.getUpdatePolicy() != 1 && (DefaultCaret.this.getUpdatePolicy() != 0 || SwingUtilities.isEventDispatchThread())) {
            var2 = var1.getOffset();
            int var3 = var2 + var1.getLength();
            int var4 = DefaultCaret.this.dot;
            boolean var5 = false;
            int var6 = DefaultCaret.this.mark;
            boolean var7 = false;
            if (var1 instanceof AbstractDocument.UndoRedoDocumentEvent) {
               DefaultCaret.this.setDot(var2);
            } else {
               if (var4 >= var3) {
                  var4 -= var3 - var2;
                  if (var4 == var3) {
                     var5 = true;
                  }
               } else if (var4 >= var2) {
                  var4 = var2;
                  var5 = true;
               }

               if (var6 >= var3) {
                  var6 -= var3 - var2;
                  if (var6 == var3) {
                     var7 = true;
                  }
               } else if (var6 >= var2) {
                  var6 = var2;
                  var7 = true;
               }

               if (var6 == var4) {
                  DefaultCaret.this.forceCaretPositionChange = true;

                  try {
                     DefaultCaret.this.setDot(var4, DefaultCaret.this.guessBiasForOffset(var4, DefaultCaret.this.dotBias, DefaultCaret.this.dotLTR));
                  } finally {
                     DefaultCaret.this.forceCaretPositionChange = false;
                  }

                  DefaultCaret.this.ensureValidPosition();
               } else {
                  Position.Bias var8 = DefaultCaret.this.dotBias;
                  Position.Bias var9 = DefaultCaret.this.markBias;
                  if (var5) {
                     var8 = DefaultCaret.this.guessBiasForOffset(var4, var8, DefaultCaret.this.dotLTR);
                  }

                  if (var7) {
                     var9 = DefaultCaret.this.guessBiasForOffset(DefaultCaret.this.mark, var9, DefaultCaret.this.markLTR);
                  }

                  DefaultCaret.this.setDot(var6, var9);
                  if (DefaultCaret.this.getDot() == var6) {
                     DefaultCaret.this.moveDot(var4, var8);
                  }

                  DefaultCaret.this.ensureValidPosition();
               }

            }
         } else {
            var2 = DefaultCaret.this.component.getDocument().getLength();
            DefaultCaret.this.dot = Math.min(DefaultCaret.this.dot, var2);
            DefaultCaret.this.mark = Math.min(DefaultCaret.this.mark, var2);
            if ((var1.getOffset() < DefaultCaret.this.dot || var1.getOffset() < DefaultCaret.this.mark) && DefaultCaret.this.selectionTag != null) {
               try {
                  DefaultCaret.this.component.getHighlighter().changeHighlight(DefaultCaret.this.selectionTag, Math.min(DefaultCaret.this.dot, DefaultCaret.this.mark), Math.max(DefaultCaret.this.dot, DefaultCaret.this.mark));
               } catch (BadLocationException var13) {
                  var13.printStackTrace();
               }
            }

         }
      }

      public void changedUpdate(DocumentEvent var1) {
         if (DefaultCaret.this.getUpdatePolicy() != 1 && (DefaultCaret.this.getUpdatePolicy() != 0 || SwingUtilities.isEventDispatchThread())) {
            if (var1 instanceof AbstractDocument.UndoRedoDocumentEvent) {
               DefaultCaret.this.setDot(var1.getOffset() + var1.getLength());
            }

         }
      }

      public void propertyChange(PropertyChangeEvent var1) {
         Object var2 = var1.getOldValue();
         Object var3 = var1.getNewValue();
         if (!(var2 instanceof Document) && !(var3 instanceof Document)) {
            if ("enabled".equals(var1.getPropertyName())) {
               Boolean var4 = (Boolean)var1.getNewValue();
               if (DefaultCaret.this.component.isFocusOwner()) {
                  if (var4 == Boolean.TRUE) {
                     if (DefaultCaret.this.component.isEditable()) {
                        DefaultCaret.this.setVisible(true);
                     }

                     DefaultCaret.this.setSelectionVisible(true);
                  } else {
                     DefaultCaret.this.setVisible(false);
                     DefaultCaret.this.setSelectionVisible(false);
                  }
               }
            } else if ("caretWidth".equals(var1.getPropertyName())) {
               Integer var5 = (Integer)var1.getNewValue();
               if (var5 != null) {
                  DefaultCaret.this.caretWidth = var5;
               } else {
                  DefaultCaret.this.caretWidth = -1;
               }

               DefaultCaret.this.repaint();
            } else if ("caretAspectRatio".equals(var1.getPropertyName())) {
               Number var6 = (Number)var1.getNewValue();
               if (var6 != null) {
                  DefaultCaret.this.aspectRatio = var6.floatValue();
               } else {
                  DefaultCaret.this.aspectRatio = -1.0F;
               }

               DefaultCaret.this.repaint();
            }
         } else {
            DefaultCaret.this.setDot(0);
            if (var2 != null) {
               ((Document)var2).removeDocumentListener(this);
            }

            if (var3 != null) {
               ((Document)var3).addDocumentListener(this);
            }
         }

      }

      public void lostOwnership(Clipboard var1, Transferable var2) {
         if (DefaultCaret.this.ownsSelection) {
            DefaultCaret.this.ownsSelection = false;
            if (DefaultCaret.this.component != null && !DefaultCaret.this.component.hasFocus()) {
               DefaultCaret.this.setSelectionVisible(false);
            }
         }

      }
   }

   class SafeScroller implements Runnable {
      Rectangle r;

      SafeScroller(Rectangle var2) {
         this.r = var2;
      }

      public void run() {
         if (DefaultCaret.this.component != null) {
            DefaultCaret.this.component.scrollRectToVisible(this.r);
         }

      }
   }
}
