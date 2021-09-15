package javax.swing.plaf.metal;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.ImageObserver;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Locale;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRootPane;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.UIResource;
import sun.awt.SunToolkit;
import sun.swing.SwingUtilities2;

class MetalTitlePane extends JComponent {
   private static final Border handyEmptyBorder = new EmptyBorder(0, 0, 0, 0);
   private static final int IMAGE_HEIGHT = 16;
   private static final int IMAGE_WIDTH = 16;
   private PropertyChangeListener propertyChangeListener;
   private JMenuBar menuBar;
   private Action closeAction;
   private Action iconifyAction;
   private Action restoreAction;
   private Action maximizeAction;
   private JButton toggleButton;
   private JButton iconifyButton;
   private JButton closeButton;
   private Icon maximizeIcon;
   private Icon minimizeIcon;
   private Image systemIcon;
   private WindowListener windowListener;
   private Window window;
   private JRootPane rootPane;
   private int buttonsWidth;
   private int state;
   private MetalRootPaneUI rootPaneUI;
   private Color inactiveBackground = UIManager.getColor("inactiveCaption");
   private Color inactiveForeground = UIManager.getColor("inactiveCaptionText");
   private Color inactiveShadow = UIManager.getColor("inactiveCaptionBorder");
   private Color activeBumpsHighlight = MetalLookAndFeel.getPrimaryControlHighlight();
   private Color activeBumpsShadow = MetalLookAndFeel.getPrimaryControlDarkShadow();
   private Color activeBackground = null;
   private Color activeForeground = null;
   private Color activeShadow = null;
   private MetalBumps activeBumps;
   private MetalBumps inactiveBumps;

   public MetalTitlePane(JRootPane var1, MetalRootPaneUI var2) {
      this.activeBumps = new MetalBumps(0, 0, this.activeBumpsHighlight, this.activeBumpsShadow, MetalLookAndFeel.getPrimaryControl());
      this.inactiveBumps = new MetalBumps(0, 0, MetalLookAndFeel.getControlHighlight(), MetalLookAndFeel.getControlDarkShadow(), MetalLookAndFeel.getControl());
      this.rootPane = var1;
      this.rootPaneUI = var2;
      this.state = -1;
      this.installSubcomponents();
      this.determineColors();
      this.installDefaults();
      this.setLayout(this.createLayout());
   }

   private void uninstall() {
      this.uninstallListeners();
      this.window = null;
      this.removeAll();
   }

   private void installListeners() {
      if (this.window != null) {
         this.windowListener = this.createWindowListener();
         this.window.addWindowListener(this.windowListener);
         this.propertyChangeListener = this.createWindowPropertyChangeListener();
         this.window.addPropertyChangeListener(this.propertyChangeListener);
      }

   }

   private void uninstallListeners() {
      if (this.window != null) {
         this.window.removeWindowListener(this.windowListener);
         this.window.removePropertyChangeListener(this.propertyChangeListener);
      }

   }

   private WindowListener createWindowListener() {
      return new MetalTitlePane.WindowHandler();
   }

   private PropertyChangeListener createWindowPropertyChangeListener() {
      return new MetalTitlePane.PropertyChangeHandler();
   }

   public JRootPane getRootPane() {
      return this.rootPane;
   }

   private int getWindowDecorationStyle() {
      return this.getRootPane().getWindowDecorationStyle();
   }

   public void addNotify() {
      super.addNotify();
      this.uninstallListeners();
      this.window = SwingUtilities.getWindowAncestor(this);
      if (this.window != null) {
         if (this.window instanceof Frame) {
            this.setState(((Frame)this.window).getExtendedState());
         } else {
            this.setState(0);
         }

         this.setActive(this.window.isActive());
         this.installListeners();
         this.updateSystemIcon();
      }

   }

   public void removeNotify() {
      super.removeNotify();
      this.uninstallListeners();
      this.window = null;
   }

   private void installSubcomponents() {
      int var1 = this.getWindowDecorationStyle();
      if (var1 == 1) {
         this.createActions();
         this.menuBar = this.createMenuBar();
         this.add(this.menuBar);
         this.createButtons();
         this.add(this.iconifyButton);
         this.add(this.toggleButton);
         this.add(this.closeButton);
      } else if (var1 == 2 || var1 == 3 || var1 == 4 || var1 == 5 || var1 == 6 || var1 == 7 || var1 == 8) {
         this.createActions();
         this.createButtons();
         this.add(this.closeButton);
      }

   }

   private void determineColors() {
      switch(this.getWindowDecorationStyle()) {
      case 1:
         this.activeBackground = UIManager.getColor("activeCaption");
         this.activeForeground = UIManager.getColor("activeCaptionText");
         this.activeShadow = UIManager.getColor("activeCaptionBorder");
         break;
      case 2:
      case 3:
      default:
         this.activeBackground = UIManager.getColor("activeCaption");
         this.activeForeground = UIManager.getColor("activeCaptionText");
         this.activeShadow = UIManager.getColor("activeCaptionBorder");
         break;
      case 4:
         this.activeBackground = UIManager.getColor("OptionPane.errorDialog.titlePane.background");
         this.activeForeground = UIManager.getColor("OptionPane.errorDialog.titlePane.foreground");
         this.activeShadow = UIManager.getColor("OptionPane.errorDialog.titlePane.shadow");
         break;
      case 5:
      case 6:
      case 7:
         this.activeBackground = UIManager.getColor("OptionPane.questionDialog.titlePane.background");
         this.activeForeground = UIManager.getColor("OptionPane.questionDialog.titlePane.foreground");
         this.activeShadow = UIManager.getColor("OptionPane.questionDialog.titlePane.shadow");
         break;
      case 8:
         this.activeBackground = UIManager.getColor("OptionPane.warningDialog.titlePane.background");
         this.activeForeground = UIManager.getColor("OptionPane.warningDialog.titlePane.foreground");
         this.activeShadow = UIManager.getColor("OptionPane.warningDialog.titlePane.shadow");
      }

      this.activeBumps.setBumpColors(this.activeBumpsHighlight, this.activeBumpsShadow, this.activeBackground);
   }

   private void installDefaults() {
      this.setFont(UIManager.getFont("InternalFrame.titleFont", this.getLocale()));
   }

   private void uninstallDefaults() {
   }

   protected JMenuBar createMenuBar() {
      this.menuBar = new MetalTitlePane.SystemMenuBar();
      this.menuBar.setFocusable(false);
      this.menuBar.setBorderPainted(true);
      this.menuBar.add(this.createMenu());
      return this.menuBar;
   }

   private void close() {
      Window var1 = this.getWindow();
      if (var1 != null) {
         var1.dispatchEvent(new WindowEvent(var1, 201));
      }

   }

   private void iconify() {
      Frame var1 = this.getFrame();
      if (var1 != null) {
         var1.setExtendedState(this.state | 1);
      }

   }

   private void maximize() {
      Frame var1 = this.getFrame();
      if (var1 != null) {
         var1.setExtendedState(this.state | 6);
      }

   }

   private void restore() {
      Frame var1 = this.getFrame();
      if (var1 != null) {
         if ((this.state & 1) != 0) {
            var1.setExtendedState(this.state & -2);
         } else {
            var1.setExtendedState(this.state & -7);
         }

      }
   }

   private void createActions() {
      this.closeAction = new MetalTitlePane.CloseAction();
      if (this.getWindowDecorationStyle() == 1) {
         this.iconifyAction = new MetalTitlePane.IconifyAction();
         this.restoreAction = new MetalTitlePane.RestoreAction();
         this.maximizeAction = new MetalTitlePane.MaximizeAction();
      }

   }

   private JMenu createMenu() {
      JMenu var1 = new JMenu("");
      if (this.getWindowDecorationStyle() == 1) {
         this.addMenuItems(var1);
      }

      return var1;
   }

   private void addMenuItems(JMenu var1) {
      Locale var2 = this.getRootPane().getLocale();
      JMenuItem var3 = var1.add(this.restoreAction);
      int var4 = MetalUtils.getInt("MetalTitlePane.restoreMnemonic", -1);
      if (var4 != -1) {
         var3.setMnemonic(var4);
      }

      var3 = var1.add(this.iconifyAction);
      var4 = MetalUtils.getInt("MetalTitlePane.iconifyMnemonic", -1);
      if (var4 != -1) {
         var3.setMnemonic(var4);
      }

      if (Toolkit.getDefaultToolkit().isFrameStateSupported(6)) {
         var3 = var1.add(this.maximizeAction);
         var4 = MetalUtils.getInt("MetalTitlePane.maximizeMnemonic", -1);
         if (var4 != -1) {
            var3.setMnemonic(var4);
         }
      }

      var1.add((Component)(new JSeparator()));
      var3 = var1.add(this.closeAction);
      var4 = MetalUtils.getInt("MetalTitlePane.closeMnemonic", -1);
      if (var4 != -1) {
         var3.setMnemonic(var4);
      }

   }

   private JButton createTitleButton() {
      JButton var1 = new JButton();
      var1.setFocusPainted(false);
      var1.setFocusable(false);
      var1.setOpaque(true);
      return var1;
   }

   private void createButtons() {
      this.closeButton = this.createTitleButton();
      this.closeButton.setAction(this.closeAction);
      this.closeButton.setText((String)null);
      this.closeButton.putClientProperty("paintActive", Boolean.TRUE);
      this.closeButton.setBorder(handyEmptyBorder);
      this.closeButton.putClientProperty("AccessibleName", "Close");
      this.closeButton.setIcon(UIManager.getIcon("InternalFrame.closeIcon"));
      if (this.getWindowDecorationStyle() == 1) {
         this.maximizeIcon = UIManager.getIcon("InternalFrame.maximizeIcon");
         this.minimizeIcon = UIManager.getIcon("InternalFrame.minimizeIcon");
         this.iconifyButton = this.createTitleButton();
         this.iconifyButton.setAction(this.iconifyAction);
         this.iconifyButton.setText((String)null);
         this.iconifyButton.putClientProperty("paintActive", Boolean.TRUE);
         this.iconifyButton.setBorder(handyEmptyBorder);
         this.iconifyButton.putClientProperty("AccessibleName", "Iconify");
         this.iconifyButton.setIcon(UIManager.getIcon("InternalFrame.iconifyIcon"));
         this.toggleButton = this.createTitleButton();
         this.toggleButton.setAction(this.restoreAction);
         this.toggleButton.putClientProperty("paintActive", Boolean.TRUE);
         this.toggleButton.setBorder(handyEmptyBorder);
         this.toggleButton.putClientProperty("AccessibleName", "Maximize");
         this.toggleButton.setIcon(this.maximizeIcon);
      }

   }

   private LayoutManager createLayout() {
      return new MetalTitlePane.TitlePaneLayout();
   }

   private void setActive(boolean var1) {
      Boolean var2 = var1 ? Boolean.TRUE : Boolean.FALSE;
      this.closeButton.putClientProperty("paintActive", var2);
      if (this.getWindowDecorationStyle() == 1) {
         this.iconifyButton.putClientProperty("paintActive", var2);
         this.toggleButton.putClientProperty("paintActive", var2);
      }

      this.getRootPane().repaint();
   }

   private void setState(int var1) {
      this.setState(var1, false);
   }

   private void setState(int var1, boolean var2) {
      Window var3 = this.getWindow();
      if (var3 != null && this.getWindowDecorationStyle() == 1) {
         if (this.state == var1 && !var2) {
            return;
         }

         Frame var4 = this.getFrame();
         if (var4 == null) {
            this.maximizeAction.setEnabled(false);
            this.restoreAction.setEnabled(false);
            this.iconifyAction.setEnabled(false);
            this.remove(this.toggleButton);
            this.remove(this.iconifyButton);
            this.revalidate();
            this.repaint();
         } else {
            JRootPane var5 = this.getRootPane();
            if ((var1 & 6) != 0 && (var5.getBorder() == null || var5.getBorder() instanceof UIResource) && var4.isShowing()) {
               var5.setBorder((Border)null);
            } else if ((var1 & 6) == 0) {
               this.rootPaneUI.installBorder(var5);
            }

            if (var4.isResizable()) {
               if ((var1 & 6) != 0) {
                  this.updateToggleButton(this.restoreAction, this.minimizeIcon);
                  this.maximizeAction.setEnabled(false);
                  this.restoreAction.setEnabled(true);
               } else {
                  this.updateToggleButton(this.maximizeAction, this.maximizeIcon);
                  this.maximizeAction.setEnabled(true);
                  this.restoreAction.setEnabled(false);
               }

               if (this.toggleButton.getParent() == null || this.iconifyButton.getParent() == null) {
                  this.add(this.toggleButton);
                  this.add(this.iconifyButton);
                  this.revalidate();
                  this.repaint();
               }

               this.toggleButton.setText((String)null);
            } else {
               this.maximizeAction.setEnabled(false);
               this.restoreAction.setEnabled(false);
               if (this.toggleButton.getParent() != null) {
                  this.remove(this.toggleButton);
                  this.revalidate();
                  this.repaint();
               }
            }
         }

         this.closeAction.setEnabled(true);
         this.state = var1;
      }

   }

   private void updateToggleButton(Action var1, Icon var2) {
      this.toggleButton.setAction(var1);
      this.toggleButton.setIcon(var2);
      this.toggleButton.setText((String)null);
   }

   private Frame getFrame() {
      Window var1 = this.getWindow();
      return var1 instanceof Frame ? (Frame)var1 : null;
   }

   private Window getWindow() {
      return this.window;
   }

   private String getTitle() {
      Window var1 = this.getWindow();
      if (var1 instanceof Frame) {
         return ((Frame)var1).getTitle();
      } else {
         return var1 instanceof Dialog ? ((Dialog)var1).getTitle() : null;
      }
   }

   public void paintComponent(Graphics var1) {
      if (this.getFrame() != null) {
         this.setState(this.getFrame().getExtendedState());
      }

      JRootPane var2 = this.getRootPane();
      Window var3 = this.getWindow();
      boolean var4 = var3 == null ? var2.getComponentOrientation().isLeftToRight() : var3.getComponentOrientation().isLeftToRight();
      boolean var5 = var3 == null ? true : var3.isActive();
      int var6 = this.getWidth();
      int var7 = this.getHeight();
      Color var8;
      Color var9;
      Color var10;
      MetalBumps var11;
      if (var5) {
         var8 = this.activeBackground;
         var9 = this.activeForeground;
         var10 = this.activeShadow;
         var11 = this.activeBumps;
      } else {
         var8 = this.inactiveBackground;
         var9 = this.inactiveForeground;
         var10 = this.inactiveShadow;
         var11 = this.inactiveBumps;
      }

      var1.setColor(var8);
      var1.fillRect(0, 0, var6, var7);
      var1.setColor(var10);
      var1.drawLine(0, var7 - 1, var6, var7 - 1);
      var1.drawLine(0, 0, 0, 0);
      var1.drawLine(var6 - 1, 0, var6 - 1, 0);
      int var12 = var4 ? 5 : var6 - 5;
      if (this.getWindowDecorationStyle() == 1) {
         var12 += var4 ? 21 : -21;
      }

      String var13 = this.getTitle();
      int var15;
      int var17;
      if (var13 != null) {
         FontMetrics var14 = SwingUtilities2.getFontMetrics(var2, (Graphics)var1);
         var1.setColor(var9);
         var15 = (var7 - var14.getHeight()) / 2 + var14.getAscent();
         Rectangle var16 = new Rectangle(0, 0, 0, 0);
         if (this.iconifyButton != null && this.iconifyButton.getParent() != null) {
            var16 = this.iconifyButton.getBounds();
         }

         if (var4) {
            if (var16.x == 0) {
               var16.x = var3.getWidth() - var3.getInsets().right - 2;
            }

            var17 = var16.x - var12 - 4;
            var13 = SwingUtilities2.clipStringIfNecessary(var2, var14, var13, var17);
         } else {
            var17 = var12 - var16.x - var16.width - 4;
            var13 = SwingUtilities2.clipStringIfNecessary(var2, var14, var13, var17);
            var12 -= SwingUtilities2.stringWidth(var2, var14, var13);
         }

         int var18 = SwingUtilities2.stringWidth(var2, var14, var13);
         SwingUtilities2.drawString(var2, var1, (String)var13, var12, var15);
         var12 += var4 ? var18 + 5 : -5;
      }

      int var19;
      if (var4) {
         var15 = var6 - this.buttonsWidth - var12 - 5;
         var19 = var12;
      } else {
         var15 = var12 - this.buttonsWidth - 5;
         var19 = this.buttonsWidth + 5;
      }

      byte var20 = 3;
      var17 = this.getHeight() - 2 * var20;
      var11.setBumpArea(var15, var17);
      var11.paintIcon(this, var1, var19, var20);
   }

   private void updateSystemIcon() {
      Window var1 = this.getWindow();
      if (var1 == null) {
         this.systemIcon = null;
      } else {
         List var2 = var1.getIconImages();

         assert var2 != null;

         if (var2.size() == 0) {
            this.systemIcon = null;
         } else if (var2.size() == 1) {
            this.systemIcon = (Image)var2.get(0);
         } else {
            this.systemIcon = SunToolkit.getScaledIconImage(var2, 16, 16);
         }

      }
   }

   private class WindowHandler extends WindowAdapter {
      private WindowHandler() {
      }

      public void windowActivated(WindowEvent var1) {
         MetalTitlePane.this.setActive(true);
      }

      public void windowDeactivated(WindowEvent var1) {
         MetalTitlePane.this.setActive(false);
      }

      // $FF: synthetic method
      WindowHandler(Object var2) {
         this();
      }
   }

   private class PropertyChangeHandler implements PropertyChangeListener {
      private PropertyChangeHandler() {
      }

      public void propertyChange(PropertyChangeEvent var1) {
         String var2 = var1.getPropertyName();
         if (!"resizable".equals(var2) && !"state".equals(var2)) {
            if ("title".equals(var2)) {
               MetalTitlePane.this.repaint();
            } else if ("componentOrientation" == var2) {
               MetalTitlePane.this.revalidate();
               MetalTitlePane.this.repaint();
            } else if ("iconImage" == var2) {
               MetalTitlePane.this.updateSystemIcon();
               MetalTitlePane.this.revalidate();
               MetalTitlePane.this.repaint();
            }
         } else {
            Frame var3 = MetalTitlePane.this.getFrame();
            if (var3 != null) {
               MetalTitlePane.this.setState(var3.getExtendedState(), true);
            }

            if ("resizable".equals(var2)) {
               MetalTitlePane.this.getRootPane().repaint();
            }
         }

      }

      // $FF: synthetic method
      PropertyChangeHandler(Object var2) {
         this();
      }
   }

   private class TitlePaneLayout implements LayoutManager {
      private TitlePaneLayout() {
      }

      public void addLayoutComponent(String var1, Component var2) {
      }

      public void removeLayoutComponent(Component var1) {
      }

      public Dimension preferredLayoutSize(Container var1) {
         int var2 = this.computeHeight();
         return new Dimension(var2, var2);
      }

      public Dimension minimumLayoutSize(Container var1) {
         return this.preferredLayoutSize(var1);
      }

      private int computeHeight() {
         FontMetrics var1 = MetalTitlePane.this.rootPane.getFontMetrics(MetalTitlePane.this.getFont());
         int var2 = var1.getHeight();
         var2 += 7;
         byte var3 = 0;
         if (MetalTitlePane.this.getWindowDecorationStyle() == 1) {
            var3 = 16;
         }

         int var4 = Math.max(var2, var3);
         return var4;
      }

      public void layoutContainer(Container var1) {
         boolean var2 = MetalTitlePane.this.window == null ? MetalTitlePane.this.getRootPane().getComponentOrientation().isLeftToRight() : MetalTitlePane.this.window.getComponentOrientation().isLeftToRight();
         int var3 = MetalTitlePane.this.getWidth();
         byte var5 = 3;
         int var7;
         int var8;
         if (MetalTitlePane.this.closeButton != null && MetalTitlePane.this.closeButton.getIcon() != null) {
            var7 = MetalTitlePane.this.closeButton.getIcon().getIconHeight();
            var8 = MetalTitlePane.this.closeButton.getIcon().getIconWidth();
         } else {
            var7 = 16;
            var8 = 16;
         }

         int var4 = var2 ? var3 : 0;
         byte var6 = 5;
         var4 = var2 ? var6 : var3 - var8 - var6;
         if (MetalTitlePane.this.menuBar != null) {
            MetalTitlePane.this.menuBar.setBounds(var4, var5, var8, var7);
         }

         var4 = var2 ? var3 : 0;
         var6 = 4;
         var4 += var2 ? -var6 - var8 : var6;
         if (MetalTitlePane.this.closeButton != null) {
            MetalTitlePane.this.closeButton.setBounds(var4, var5, var8, var7);
         }

         if (!var2) {
            var4 += var8;
         }

         if (MetalTitlePane.this.getWindowDecorationStyle() == 1) {
            if (Toolkit.getDefaultToolkit().isFrameStateSupported(6) && MetalTitlePane.this.toggleButton.getParent() != null) {
               var6 = 10;
               var4 += var2 ? -var6 - var8 : var6;
               MetalTitlePane.this.toggleButton.setBounds(var4, var5, var8, var7);
               if (!var2) {
                  var4 += var8;
               }
            }

            if (MetalTitlePane.this.iconifyButton != null && MetalTitlePane.this.iconifyButton.getParent() != null) {
               var6 = 2;
               var4 += var2 ? -var6 - var8 : var6;
               MetalTitlePane.this.iconifyButton.setBounds(var4, var5, var8, var7);
               if (!var2) {
                  var4 += var8;
               }
            }
         }

         MetalTitlePane.this.buttonsWidth = var2 ? var3 - var4 : var4;
      }

      // $FF: synthetic method
      TitlePaneLayout(Object var2) {
         this();
      }
   }

   private class SystemMenuBar extends JMenuBar {
      private SystemMenuBar() {
      }

      public void paint(Graphics var1) {
         if (this.isOpaque()) {
            var1.setColor(this.getBackground());
            var1.fillRect(0, 0, this.getWidth(), this.getHeight());
         }

         if (MetalTitlePane.this.systemIcon != null) {
            var1.drawImage(MetalTitlePane.this.systemIcon, 0, 0, 16, 16, (ImageObserver)null);
         } else {
            Icon var2 = UIManager.getIcon("InternalFrame.icon");
            if (var2 != null) {
               var2.paintIcon(this, var1, 0, 0);
            }
         }

      }

      public Dimension getMinimumSize() {
         return this.getPreferredSize();
      }

      public Dimension getPreferredSize() {
         Dimension var1 = super.getPreferredSize();
         return new Dimension(Math.max(16, var1.width), Math.max(var1.height, 16));
      }

      // $FF: synthetic method
      SystemMenuBar(Object var2) {
         this();
      }
   }

   private class MaximizeAction extends AbstractAction {
      public MaximizeAction() {
         super(UIManager.getString("MetalTitlePane.maximizeTitle", (Locale)MetalTitlePane.this.getLocale()));
      }

      public void actionPerformed(ActionEvent var1) {
         MetalTitlePane.this.maximize();
      }
   }

   private class RestoreAction extends AbstractAction {
      public RestoreAction() {
         super(UIManager.getString("MetalTitlePane.restoreTitle", (Locale)MetalTitlePane.this.getLocale()));
      }

      public void actionPerformed(ActionEvent var1) {
         MetalTitlePane.this.restore();
      }
   }

   private class IconifyAction extends AbstractAction {
      public IconifyAction() {
         super(UIManager.getString("MetalTitlePane.iconifyTitle", (Locale)MetalTitlePane.this.getLocale()));
      }

      public void actionPerformed(ActionEvent var1) {
         MetalTitlePane.this.iconify();
      }
   }

   private class CloseAction extends AbstractAction {
      public CloseAction() {
         super(UIManager.getString("MetalTitlePane.closeTitle", (Locale)MetalTitlePane.this.getLocale()));
      }

      public void actionPerformed(ActionEvent var1) {
         MetalTitlePane.this.close();
      }
   }
}
