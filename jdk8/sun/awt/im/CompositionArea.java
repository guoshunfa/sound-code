package sun.awt.im;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.TextHitInfo;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.im.InputMethodRequests;
import java.text.AttributedCharacterIterator;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

public final class CompositionArea extends JPanel implements InputMethodListener {
   private CompositionAreaHandler handler;
   private TextLayout composedTextLayout;
   private TextHitInfo caret = null;
   private JFrame compositionWindow;
   private static final int TEXT_ORIGIN_X = 5;
   private static final int TEXT_ORIGIN_Y = 15;
   private static final int PASSIVE_WIDTH = 480;
   private static final int WIDTH_MARGIN = 10;
   private static final int HEIGHT_MARGIN = 3;
   private static final long serialVersionUID = -1057247068746557444L;

   CompositionArea() {
      String var1 = Toolkit.getProperty("AWT.CompositionWindowTitle", "Input Window");
      this.compositionWindow = (JFrame)InputMethodContext.createInputMethodWindow(var1, (InputContext)null, true);
      this.setOpaque(true);
      this.setBorder(LineBorder.createGrayLineBorder());
      this.setForeground(Color.black);
      this.setBackground(Color.white);
      this.enableInputMethods(true);
      this.enableEvents(8L);
      this.compositionWindow.getContentPane().add(this);
      this.compositionWindow.addWindowListener(new CompositionArea.FrameWindowAdapter());
      this.addInputMethodListener(this);
      this.compositionWindow.enableInputMethods(false);
      this.compositionWindow.pack();
      Dimension var2 = this.compositionWindow.getSize();
      Dimension var3 = this.getToolkit().getScreenSize();
      this.compositionWindow.setLocation(var3.width - var2.width - 20, var3.height - var2.height - 100);
      this.compositionWindow.setVisible(false);
   }

   synchronized void setHandlerInfo(CompositionAreaHandler var1, InputContext var2) {
      this.handler = var1;
      ((InputMethodWindow)this.compositionWindow).setInputContext(var2);
   }

   public InputMethodRequests getInputMethodRequests() {
      return this.handler;
   }

   private Rectangle getCaretRectangle(TextHitInfo var1) {
      int var2 = 0;
      TextLayout var3 = this.composedTextLayout;
      if (var3 != null) {
         var2 = Math.round(var3.getCaretInfo(var1)[0]);
      }

      Graphics var4 = this.getGraphics();
      FontMetrics var5 = null;

      try {
         var5 = var4.getFontMetrics();
      } finally {
         var4.dispose();
      }

      return new Rectangle(5 + var2, 15 - var5.getAscent(), 0, var5.getAscent() + var5.getDescent());
   }

   public void paint(Graphics var1) {
      super.paint(var1);
      var1.setColor(this.getForeground());
      TextLayout var2 = this.composedTextLayout;
      if (var2 != null) {
         var2.draw((Graphics2D)var1, 5.0F, 15.0F);
      }

      if (this.caret != null) {
         Rectangle var3 = this.getCaretRectangle(this.caret);
         var1.setXORMode(this.getBackground());
         var1.fillRect(var3.x, var3.y, 1, var3.height);
         var1.setPaintMode();
      }

   }

   void setCompositionAreaVisible(boolean var1) {
      this.compositionWindow.setVisible(var1);
   }

   boolean isCompositionAreaVisible() {
      return this.compositionWindow.isVisible();
   }

   public void inputMethodTextChanged(InputMethodEvent var1) {
      this.handler.inputMethodTextChanged(var1);
   }

   public void caretPositionChanged(InputMethodEvent var1) {
      this.handler.caretPositionChanged(var1);
   }

   void setText(AttributedCharacterIterator var1, TextHitInfo var2) {
      this.composedTextLayout = null;
      if (var1 == null) {
         this.compositionWindow.setVisible(false);
         this.caret = null;
      } else {
         if (!this.compositionWindow.isVisible()) {
            this.compositionWindow.setVisible(true);
         }

         Graphics var3 = this.getGraphics();
         if (var3 == null) {
            return;
         }

         try {
            this.updateWindowLocation();
            FontRenderContext var4 = ((Graphics2D)var3).getFontRenderContext();
            this.composedTextLayout = new TextLayout(var1, var4);
            Rectangle2D var5 = this.composedTextLayout.getBounds();
            this.caret = var2;
            FontMetrics var6 = var3.getFontMetrics();
            Rectangle2D var7 = var6.getMaxCharBounds(var3);
            int var8 = (int)var7.getHeight() + 3;
            int var9 = var8 + this.compositionWindow.getInsets().top + this.compositionWindow.getInsets().bottom;
            InputMethodRequests var10 = this.handler.getClientInputMethodRequests();
            int var11 = var10 == null ? 480 : (int)var5.getWidth() + 10;
            int var12 = var11 + this.compositionWindow.getInsets().left + this.compositionWindow.getInsets().right;
            this.setPreferredSize(new Dimension(var11, var8));
            this.compositionWindow.setSize(new Dimension(var12, var9));
            this.paint(var3);
         } finally {
            var3.dispose();
         }
      }

   }

   void setCaret(TextHitInfo var1) {
      this.caret = var1;
      if (this.compositionWindow.isVisible()) {
         Graphics var2 = this.getGraphics();

         try {
            this.paint(var2);
         } finally {
            var2.dispose();
         }
      }

   }

   void updateWindowLocation() {
      InputMethodRequests var1 = this.handler.getClientInputMethodRequests();
      if (var1 != null) {
         Point var2 = new Point();
         Rectangle var3 = var1.getTextLocation((TextHitInfo)null);
         Dimension var4 = Toolkit.getDefaultToolkit().getScreenSize();
         Dimension var5 = this.compositionWindow.getSize();
         if (var3.x + var5.width > var4.width) {
            var2.x = var4.width - var5.width;
         } else {
            var2.x = var3.x;
         }

         if (var3.y + var3.height + 2 + var5.height > var4.height) {
            var2.y = var3.y - 2 - var5.height;
         } else {
            var2.y = var3.y + var3.height + 2;
         }

         this.compositionWindow.setLocation(var2);
      }
   }

   Rectangle getTextLocation(TextHitInfo var1) {
      Rectangle var2 = this.getCaretRectangle(var1);
      Point var3 = this.getLocationOnScreen();
      var2.translate(var3.x, var3.y);
      return var2;
   }

   TextHitInfo getLocationOffset(int var1, int var2) {
      TextLayout var3 = this.composedTextLayout;
      if (var3 == null) {
         return null;
      } else {
         Point var4 = this.getLocationOnScreen();
         var1 -= var4.x + 5;
         var2 -= var4.y + 15;
         return var3.getBounds().contains((double)var1, (double)var2) ? var3.hitTestChar((float)var1, (float)var2) : null;
      }
   }

   void setCompositionAreaUndecorated(boolean var1) {
      if (this.compositionWindow.isDisplayable()) {
         this.compositionWindow.removeNotify();
      }

      this.compositionWindow.setUndecorated(var1);
      this.compositionWindow.pack();
   }

   class FrameWindowAdapter extends WindowAdapter {
      public void windowActivated(WindowEvent var1) {
         CompositionArea.this.requestFocus();
      }
   }
}
