package javax.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;

public class ToolTipManager extends MouseAdapter implements MouseMotionListener {
   Timer enterTimer = new Timer(750, new ToolTipManager.insideTimerAction());
   Timer exitTimer;
   Timer insideTimer;
   String toolTipText;
   Point preferredLocation;
   JComponent insideComponent;
   MouseEvent mouseEvent;
   boolean showImmediately;
   private static final Object TOOL_TIP_MANAGER_KEY = new Object();
   transient Popup tipWindow;
   private Window window;
   JToolTip tip;
   private Rectangle popupRect = null;
   private Rectangle popupFrameRect = null;
   boolean enabled = true;
   private boolean tipShowing = false;
   private FocusListener focusChangeListener = null;
   private MouseMotionListener moveBeforeEnterListener = null;
   private KeyListener accessibilityKeyListener = null;
   private KeyStroke postTip;
   private KeyStroke hideTip;
   protected boolean lightWeightPopupEnabled = true;
   protected boolean heavyWeightPopupEnabled = false;

   ToolTipManager() {
      this.enterTimer.setRepeats(false);
      this.exitTimer = new Timer(500, new ToolTipManager.outsideTimerAction());
      this.exitTimer.setRepeats(false);
      this.insideTimer = new Timer(4000, new ToolTipManager.stillInsideTimerAction());
      this.insideTimer.setRepeats(false);
      this.moveBeforeEnterListener = new ToolTipManager.MoveBeforeEnterListener();
      this.accessibilityKeyListener = new ToolTipManager.AccessibilityKeyListener();
      this.postTip = KeyStroke.getKeyStroke(112, 2);
      this.hideTip = KeyStroke.getKeyStroke(27, 0);
   }

   public void setEnabled(boolean var1) {
      this.enabled = var1;
      if (!var1) {
         this.hideTipWindow();
      }

   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void setLightWeightPopupEnabled(boolean var1) {
      this.lightWeightPopupEnabled = var1;
   }

   public boolean isLightWeightPopupEnabled() {
      return this.lightWeightPopupEnabled;
   }

   public void setInitialDelay(int var1) {
      this.enterTimer.setInitialDelay(var1);
   }

   public int getInitialDelay() {
      return this.enterTimer.getInitialDelay();
   }

   public void setDismissDelay(int var1) {
      this.insideTimer.setInitialDelay(var1);
   }

   public int getDismissDelay() {
      return this.insideTimer.getInitialDelay();
   }

   public void setReshowDelay(int var1) {
      this.exitTimer.setInitialDelay(var1);
   }

   public int getReshowDelay() {
      return this.exitTimer.getInitialDelay();
   }

   private GraphicsConfiguration getDrawingGC(Point var1) {
      GraphicsEnvironment var2 = GraphicsEnvironment.getLocalGraphicsEnvironment();
      GraphicsDevice[] var3 = var2.getScreenDevices();
      GraphicsDevice[] var4 = var3;
      int var5 = var3.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         GraphicsDevice var7 = var4[var6];
         GraphicsConfiguration[] var8 = var7.getConfigurations();
         GraphicsConfiguration[] var9 = var8;
         int var10 = var8.length;

         for(int var11 = 0; var11 < var10; ++var11) {
            GraphicsConfiguration var12 = var9[var11];
            Rectangle var13 = var12.getBounds();
            if (var13.contains(var1)) {
               return var12;
            }
         }
      }

      return null;
   }

   void showTipWindow() {
      if (this.insideComponent != null && this.insideComponent.isShowing()) {
         String var1 = UIManager.getString("ToolTipManager.enableToolTipMode");
         if ("activeApplication".equals(var1)) {
            KeyboardFocusManager var2 = KeyboardFocusManager.getCurrentKeyboardFocusManager();
            if (var2.getFocusedWindow() == null) {
               return;
            }
         }

         if (this.enabled) {
            Point var3 = this.insideComponent.getLocationOnScreen();
            Point var5;
            if (this.preferredLocation != null) {
               var5 = new Point(var3.x + this.preferredLocation.x, var3.y + this.preferredLocation.y);
            } else {
               var5 = this.mouseEvent.getLocationOnScreen();
            }

            GraphicsConfiguration var6 = this.getDrawingGC(var5);
            if (var6 == null) {
               var5 = this.mouseEvent.getLocationOnScreen();
               var6 = this.getDrawingGC(var5);
               if (var6 == null) {
                  var6 = this.insideComponent.getGraphicsConfiguration();
               }
            }

            Rectangle var7 = var6.getBounds();
            Insets var8 = Toolkit.getDefaultToolkit().getScreenInsets(var6);
            var7.x += var8.left;
            var7.y += var8.top;
            var7.width -= var8.left + var8.right;
            var7.height -= var8.top + var8.bottom;
            boolean var9 = SwingUtilities.isLeftToRight(this.insideComponent);
            this.hideTipWindow();
            this.tip = this.insideComponent.createToolTip();
            this.tip.setTipText(this.toolTipText);
            Dimension var13 = this.tip.getPreferredSize();
            Point var4;
            if (this.preferredLocation != null) {
               var4 = var5;
               if (!var9) {
                  var5.x -= var13.width;
               }
            } else {
               var4 = new Point(var3.x + this.mouseEvent.getX(), var3.y + this.mouseEvent.getY() + 20);
               if (!var9 && var4.x - var13.width >= 0) {
                  var4.x -= var13.width;
               }
            }

            if (this.popupRect == null) {
               this.popupRect = new Rectangle();
            }

            this.popupRect.setBounds(var4.x, var4.y, var13.width, var13.height);
            if (var4.x < var7.x) {
               var4.x = var7.x;
            } else if (var4.x - var7.x + var13.width > var7.width) {
               var4.x = var7.x + Math.max(0, var7.width - var13.width);
            }

            if (var4.y < var7.y) {
               var4.y = var7.y;
            } else if (var4.y - var7.y + var13.height > var7.height) {
               var4.y = var7.y + Math.max(0, var7.height - var13.height);
            }

            PopupFactory var10 = PopupFactory.getSharedInstance();
            if (this.lightWeightPopupEnabled) {
               int var11 = this.getPopupFitHeight(this.popupRect, this.insideComponent);
               int var12 = this.getPopupFitWidth(this.popupRect, this.insideComponent);
               if (var12 <= 0 && var11 <= 0) {
                  var10.setPopupType(0);
               } else {
                  var10.setPopupType(1);
               }
            } else {
               var10.setPopupType(1);
            }

            this.tipWindow = var10.getPopup(this.insideComponent, this.tip, var4.x, var4.y);
            var10.setPopupType(0);
            this.tipWindow.show();
            Window var14 = SwingUtilities.windowForComponent(this.insideComponent);
            this.window = SwingUtilities.windowForComponent(this.tip);
            if (this.window != null && this.window != var14) {
               this.window.addMouseListener(this);
            } else {
               this.window = null;
            }

            this.insideTimer.start();
            this.tipShowing = true;
         }

      }
   }

   void hideTipWindow() {
      if (this.tipWindow != null) {
         if (this.window != null) {
            this.window.removeMouseListener(this);
            this.window = null;
         }

         this.tipWindow.hide();
         this.tipWindow = null;
         this.tipShowing = false;
         this.tip = null;
         this.insideTimer.stop();
      }

   }

   public static ToolTipManager sharedInstance() {
      Object var0 = SwingUtilities.appContextGet(TOOL_TIP_MANAGER_KEY);
      if (var0 instanceof ToolTipManager) {
         return (ToolTipManager)var0;
      } else {
         ToolTipManager var1 = new ToolTipManager();
         SwingUtilities.appContextPut(TOOL_TIP_MANAGER_KEY, var1);
         return var1;
      }
   }

   public void registerComponent(JComponent var1) {
      var1.removeMouseListener(this);
      var1.addMouseListener(this);
      var1.removeMouseMotionListener(this.moveBeforeEnterListener);
      var1.addMouseMotionListener(this.moveBeforeEnterListener);
      var1.removeKeyListener(this.accessibilityKeyListener);
      var1.addKeyListener(this.accessibilityKeyListener);
   }

   public void unregisterComponent(JComponent var1) {
      var1.removeMouseListener(this);
      var1.removeMouseMotionListener(this.moveBeforeEnterListener);
      var1.removeKeyListener(this.accessibilityKeyListener);
   }

   public void mouseEntered(MouseEvent var1) {
      this.initiateToolTip(var1);
   }

   private void initiateToolTip(MouseEvent var1) {
      if (var1.getSource() != this.window) {
         JComponent var2 = (JComponent)var1.getSource();
         var2.removeMouseMotionListener(this.moveBeforeEnterListener);
         this.exitTimer.stop();
         Point var3 = var1.getPoint();
         if (var3.x >= 0 && var3.x < var2.getWidth() && var3.y >= 0 && var3.y < var2.getHeight()) {
            if (this.insideComponent != null) {
               this.enterTimer.stop();
            }

            var2.removeMouseMotionListener(this);
            var2.addMouseMotionListener(this);
            boolean var4 = this.insideComponent == var2;
            this.insideComponent = var2;
            if (this.tipWindow != null) {
               this.mouseEvent = var1;
               if (this.showImmediately) {
                  String var5 = var2.getToolTipText(var1);
                  Point var6 = var2.getToolTipLocation(var1);
                  boolean var7 = this.preferredLocation != null ? this.preferredLocation.equals(var6) : var6 == null;
                  if (!var4 || !this.toolTipText.equals(var5) || !var7) {
                     this.toolTipText = var5;
                     this.preferredLocation = var6;
                     this.showTipWindow();
                  }
               } else {
                  this.enterTimer.start();
               }
            }

         }
      }
   }

   public void mouseExited(MouseEvent var1) {
      boolean var2 = true;
      if (this.insideComponent == null) {
      }

      Point var4;
      if (this.window != null && var1.getSource() == this.window && this.insideComponent != null) {
         Container var7 = this.insideComponent.getTopLevelAncestor();
         if (var7 != null) {
            var4 = var1.getPoint();
            SwingUtilities.convertPointToScreen(var4, this.window);
            var4.x -= var7.getX();
            var4.y -= var7.getY();
            var4 = SwingUtilities.convertPoint((Component)null, var4, this.insideComponent);
            if (var4.x >= 0 && var4.x < this.insideComponent.getWidth() && var4.y >= 0 && var4.y < this.insideComponent.getHeight()) {
               var2 = false;
            } else {
               var2 = true;
            }
         }
      } else if (var1.getSource() == this.insideComponent && this.tipWindow != null) {
         Window var3 = SwingUtilities.getWindowAncestor(this.insideComponent);
         if (var3 != null) {
            var4 = SwingUtilities.convertPoint(this.insideComponent, var1.getPoint(), var3);
            Rectangle var5 = this.insideComponent.getTopLevelAncestor().getBounds();
            var4.x += var5.x;
            var4.y += var5.y;
            Point var6 = new Point(0, 0);
            SwingUtilities.convertPointToScreen(var6, this.tip);
            var5.x = var6.x;
            var5.y = var6.y;
            var5.width = this.tip.getWidth();
            var5.height = this.tip.getHeight();
            if (var4.x >= var5.x && var4.x < var5.x + var5.width && var4.y >= var5.y && var4.y < var5.y + var5.height) {
               var2 = false;
            } else {
               var2 = true;
            }
         }
      }

      if (var2) {
         this.enterTimer.stop();
         if (this.insideComponent != null) {
            this.insideComponent.removeMouseMotionListener(this);
         }

         this.insideComponent = null;
         this.toolTipText = null;
         this.mouseEvent = null;
         this.hideTipWindow();
         this.exitTimer.restart();
      }

   }

   public void mousePressed(MouseEvent var1) {
      this.hideTipWindow();
      this.enterTimer.stop();
      this.showImmediately = false;
      this.insideComponent = null;
      this.mouseEvent = null;
   }

   public void mouseDragged(MouseEvent var1) {
   }

   public void mouseMoved(MouseEvent var1) {
      if (this.tipShowing) {
         this.checkForTipChange(var1);
      } else if (this.showImmediately) {
         JComponent var2 = (JComponent)var1.getSource();
         this.toolTipText = var2.getToolTipText(var1);
         if (this.toolTipText != null) {
            this.preferredLocation = var2.getToolTipLocation(var1);
            this.mouseEvent = var1;
            this.insideComponent = var2;
            this.exitTimer.stop();
            this.showTipWindow();
         }
      } else {
         this.insideComponent = (JComponent)var1.getSource();
         this.mouseEvent = var1;
         this.toolTipText = null;
         this.enterTimer.restart();
      }

   }

   private void checkForTipChange(MouseEvent var1) {
      JComponent var2 = (JComponent)var1.getSource();
      String var3 = var2.getToolTipText(var1);
      Point var4 = var2.getToolTipLocation(var1);
      if (var3 == null && var4 == null) {
         this.toolTipText = null;
         this.preferredLocation = null;
         this.mouseEvent = null;
         this.insideComponent = null;
         this.hideTipWindow();
         this.enterTimer.stop();
         this.exitTimer.restart();
      } else {
         this.mouseEvent = var1;
         if ((var3 != null && var3.equals(this.toolTipText) || var3 == null) && (var4 != null && var4.equals(this.preferredLocation) || var4 == null)) {
            if (this.tipWindow != null) {
               this.insideTimer.restart();
            } else {
               this.enterTimer.restart();
            }
         } else {
            this.toolTipText = var3;
            this.preferredLocation = var4;
            if (this.showImmediately) {
               this.hideTipWindow();
               this.showTipWindow();
               this.exitTimer.stop();
            } else {
               this.enterTimer.restart();
            }
         }
      }

   }

   static Frame frameForComponent(Component var0) {
      while(!(var0 instanceof Frame)) {
         var0 = ((Component)var0).getParent();
      }

      return (Frame)var0;
   }

   private FocusListener createFocusChangeListener() {
      return new FocusAdapter() {
         public void focusLost(FocusEvent var1) {
            ToolTipManager.this.hideTipWindow();
            ToolTipManager.this.insideComponent = null;
            JComponent var2 = (JComponent)var1.getSource();
            var2.removeFocusListener(ToolTipManager.this.focusChangeListener);
         }
      };
   }

   private int getPopupFitWidth(Rectangle var1, Component var2) {
      if (var2 != null) {
         for(Container var3 = var2.getParent(); var3 != null; var3 = var3.getParent()) {
            if (var3 instanceof JFrame || var3 instanceof JDialog || var3 instanceof JWindow) {
               return this.getWidthAdjust(var3.getBounds(), var1);
            }

            if (var3 instanceof JApplet || var3 instanceof JInternalFrame) {
               if (this.popupFrameRect == null) {
                  this.popupFrameRect = new Rectangle();
               }

               Point var4 = var3.getLocationOnScreen();
               this.popupFrameRect.setBounds(var4.x, var4.y, var3.getBounds().width, var3.getBounds().height);
               return this.getWidthAdjust(this.popupFrameRect, var1);
            }
         }
      }

      return 0;
   }

   private int getPopupFitHeight(Rectangle var1, Component var2) {
      if (var2 != null) {
         for(Container var3 = var2.getParent(); var3 != null; var3 = var3.getParent()) {
            if (var3 instanceof JFrame || var3 instanceof JDialog || var3 instanceof JWindow) {
               return this.getHeightAdjust(var3.getBounds(), var1);
            }

            if (var3 instanceof JApplet || var3 instanceof JInternalFrame) {
               if (this.popupFrameRect == null) {
                  this.popupFrameRect = new Rectangle();
               }

               Point var4 = var3.getLocationOnScreen();
               this.popupFrameRect.setBounds(var4.x, var4.y, var3.getBounds().width, var3.getBounds().height);
               return this.getHeightAdjust(this.popupFrameRect, var1);
            }
         }
      }

      return 0;
   }

   private int getHeightAdjust(Rectangle var1, Rectangle var2) {
      return var2.y >= var1.y && var2.y + var2.height <= var1.y + var1.height ? 0 : var2.y + var2.height - (var1.y + var1.height) + 5;
   }

   private int getWidthAdjust(Rectangle var1, Rectangle var2) {
      return var2.x >= var1.x && var2.x + var2.width <= var1.x + var1.width ? 0 : var2.x + var2.width - (var1.x + var1.width) + 5;
   }

   private void show(JComponent var1) {
      if (this.tipWindow != null) {
         this.hideTipWindow();
         this.insideComponent = null;
      } else {
         this.hideTipWindow();
         this.enterTimer.stop();
         this.exitTimer.stop();
         this.insideTimer.stop();
         this.insideComponent = var1;
         if (this.insideComponent != null) {
            this.toolTipText = this.insideComponent.getToolTipText();
            this.preferredLocation = new Point(10, this.insideComponent.getHeight() + 10);
            this.showTipWindow();
            if (this.focusChangeListener == null) {
               this.focusChangeListener = this.createFocusChangeListener();
            }

            this.insideComponent.addFocusListener(this.focusChangeListener);
         }
      }

   }

   private void hide(JComponent var1) {
      this.hideTipWindow();
      var1.removeFocusListener(this.focusChangeListener);
      this.preferredLocation = null;
      this.insideComponent = null;
   }

   private class AccessibilityKeyListener extends KeyAdapter {
      private AccessibilityKeyListener() {
      }

      public void keyPressed(KeyEvent var1) {
         if (!var1.isConsumed()) {
            JComponent var2 = (JComponent)var1.getComponent();
            KeyStroke var3 = KeyStroke.getKeyStrokeForEvent(var1);
            if (ToolTipManager.this.hideTip.equals(var3)) {
               if (ToolTipManager.this.tipWindow != null) {
                  ToolTipManager.this.hide(var2);
                  var1.consume();
               }
            } else if (ToolTipManager.this.postTip.equals(var3)) {
               ToolTipManager.this.show(var2);
               var1.consume();
            }
         }

      }

      // $FF: synthetic method
      AccessibilityKeyListener(Object var2) {
         this();
      }
   }

   private class MoveBeforeEnterListener extends MouseMotionAdapter {
      private MoveBeforeEnterListener() {
      }

      public void mouseMoved(MouseEvent var1) {
         ToolTipManager.this.initiateToolTip(var1);
      }

      // $FF: synthetic method
      MoveBeforeEnterListener(Object var2) {
         this();
      }
   }

   protected class stillInsideTimerAction implements ActionListener {
      public void actionPerformed(ActionEvent var1) {
         ToolTipManager.this.hideTipWindow();
         ToolTipManager.this.enterTimer.stop();
         ToolTipManager.this.showImmediately = false;
         ToolTipManager.this.insideComponent = null;
         ToolTipManager.this.mouseEvent = null;
      }
   }

   protected class outsideTimerAction implements ActionListener {
      public void actionPerformed(ActionEvent var1) {
         ToolTipManager.this.showImmediately = false;
      }
   }

   protected class insideTimerAction implements ActionListener {
      public void actionPerformed(ActionEvent var1) {
         if (ToolTipManager.this.insideComponent != null && ToolTipManager.this.insideComponent.isShowing()) {
            if (ToolTipManager.this.toolTipText == null && ToolTipManager.this.mouseEvent != null) {
               ToolTipManager.this.toolTipText = ToolTipManager.this.insideComponent.getToolTipText(ToolTipManager.this.mouseEvent);
               ToolTipManager.this.preferredLocation = ToolTipManager.this.insideComponent.getToolTipLocation(ToolTipManager.this.mouseEvent);
            }

            if (ToolTipManager.this.toolTipText != null) {
               ToolTipManager.this.showImmediately = true;
               ToolTipManager.this.showTipWindow();
            } else {
               ToolTipManager.this.insideComponent = null;
               ToolTipManager.this.toolTipText = null;
               ToolTipManager.this.preferredLocation = null;
               ToolTipManager.this.mouseEvent = null;
               ToolTipManager.this.hideTipWindow();
            }
         }

      }
   }
}
