package javax.swing.plaf.synth;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import sun.swing.SwingUtilities2;

class SynthInternalFrameTitlePane extends BasicInternalFrameTitlePane implements SynthUI, PropertyChangeListener {
   protected JPopupMenu systemPopupMenu;
   protected JButton menuButton;
   private SynthStyle style;
   private int titleSpacing;
   private int buttonSpacing;
   private int titleAlignment;

   public SynthInternalFrameTitlePane(JInternalFrame var1) {
      super(var1);
   }

   public String getUIClassID() {
      return "InternalFrameTitlePaneUI";
   }

   public SynthContext getContext(JComponent var1) {
      return this.getContext(var1, this.getComponentState(var1));
   }

   public SynthContext getContext(JComponent var1, int var2) {
      return SynthContext.getContext(var1, this.style, var2);
   }

   private Region getRegion(JComponent var1) {
      return SynthLookAndFeel.getRegion(var1);
   }

   private int getComponentState(JComponent var1) {
      return this.frame != null && this.frame.isSelected() ? 512 : SynthLookAndFeel.getComponentState(var1);
   }

   protected void addSubComponents() {
      this.menuButton.setName("InternalFrameTitlePane.menuButton");
      this.iconButton.setName("InternalFrameTitlePane.iconifyButton");
      this.maxButton.setName("InternalFrameTitlePane.maximizeButton");
      this.closeButton.setName("InternalFrameTitlePane.closeButton");
      this.add(this.menuButton);
      this.add(this.iconButton);
      this.add(this.maxButton);
      this.add(this.closeButton);
   }

   protected void installListeners() {
      super.installListeners();
      this.frame.addPropertyChangeListener(this);
      this.addPropertyChangeListener(this);
   }

   protected void uninstallListeners() {
      this.frame.removePropertyChangeListener(this);
      this.removePropertyChangeListener(this);
      super.uninstallListeners();
   }

   private void updateStyle(JComponent var1) {
      SynthContext var2 = this.getContext(this, 1);
      SynthStyle var3 = this.style;
      this.style = SynthLookAndFeel.updateStyle(var2, this);
      if (this.style != var3) {
         this.maxIcon = this.style.getIcon(var2, "InternalFrameTitlePane.maximizeIcon");
         this.minIcon = this.style.getIcon(var2, "InternalFrameTitlePane.minimizeIcon");
         this.iconIcon = this.style.getIcon(var2, "InternalFrameTitlePane.iconifyIcon");
         this.closeIcon = this.style.getIcon(var2, "InternalFrameTitlePane.closeIcon");
         this.titleSpacing = this.style.getInt(var2, "InternalFrameTitlePane.titleSpacing", 2);
         this.buttonSpacing = this.style.getInt(var2, "InternalFrameTitlePane.buttonSpacing", 2);
         String var4 = (String)this.style.get(var2, "InternalFrameTitlePane.titleAlignment");
         this.titleAlignment = 10;
         if (var4 != null) {
            var4 = var4.toUpperCase();
            if (var4.equals("TRAILING")) {
               this.titleAlignment = 11;
            } else if (var4.equals("CENTER")) {
               this.titleAlignment = 0;
            }
         }
      }

      var2.dispose();
   }

   protected void installDefaults() {
      super.installDefaults();
      this.updateStyle(this);
   }

   protected void uninstallDefaults() {
      SynthContext var1 = this.getContext(this, 1);
      this.style.uninstallDefaults(var1);
      var1.dispose();
      this.style = null;
      JInternalFrame.JDesktopIcon var2 = this.frame.getDesktopIcon();
      if (var2 != null && var2.getComponentPopupMenu() == this.systemPopupMenu) {
         var2.setComponentPopupMenu((JPopupMenu)null);
      }

      super.uninstallDefaults();
   }

   protected void assembleSystemMenu() {
      this.systemPopupMenu = new SynthInternalFrameTitlePane.JPopupMenuUIResource();
      this.addSystemMenuItems(this.systemPopupMenu);
      this.enableActions();
      this.menuButton = this.createNoFocusButton();
      this.updateMenuIcon();
      this.menuButton.addMouseListener(new MouseAdapter() {
         public void mousePressed(MouseEvent var1) {
            try {
               SynthInternalFrameTitlePane.this.frame.setSelected(true);
            } catch (PropertyVetoException var3) {
            }

            SynthInternalFrameTitlePane.this.showSystemMenu();
         }
      });
      JPopupMenu var1 = this.frame.getComponentPopupMenu();
      if (var1 == null || var1 instanceof UIResource) {
         this.frame.setComponentPopupMenu(this.systemPopupMenu);
      }

      if (this.frame.getDesktopIcon() != null) {
         var1 = this.frame.getDesktopIcon().getComponentPopupMenu();
         if (var1 == null || var1 instanceof UIResource) {
            this.frame.getDesktopIcon().setComponentPopupMenu(this.systemPopupMenu);
         }
      }

      this.setInheritsPopupMenu(true);
   }

   protected void addSystemMenuItems(JPopupMenu var1) {
      JMenuItem var2 = var1.add(this.restoreAction);
      var2.setMnemonic(getButtonMnemonic("restore"));
      var2 = var1.add(this.moveAction);
      var2.setMnemonic(getButtonMnemonic("move"));
      var2 = var1.add(this.sizeAction);
      var2.setMnemonic(getButtonMnemonic("size"));
      var2 = var1.add(this.iconifyAction);
      var2.setMnemonic(getButtonMnemonic("minimize"));
      var2 = var1.add(this.maximizeAction);
      var2.setMnemonic(getButtonMnemonic("maximize"));
      var1.add((Component)(new JSeparator()));
      var2 = var1.add(this.closeAction);
      var2.setMnemonic(getButtonMnemonic("close"));
   }

   private static int getButtonMnemonic(String var0) {
      try {
         return Integer.parseInt(UIManager.getString("InternalFrameTitlePane." + var0 + "Button.mnemonic"));
      } catch (NumberFormatException var2) {
         return -1;
      }
   }

   protected void showSystemMenu() {
      Insets var1 = this.frame.getInsets();
      if (!this.frame.isIcon()) {
         this.systemPopupMenu.show(this.frame, this.menuButton.getX(), this.getY() + this.getHeight());
      } else {
         this.systemPopupMenu.show(this.menuButton, this.getX() - var1.left - var1.right, this.getY() - this.systemPopupMenu.getPreferredSize().height - var1.bottom - var1.top);
      }

   }

   public void paintComponent(Graphics var1) {
      SynthContext var2 = this.getContext(this);
      SynthLookAndFeel.update(var2, var1);
      var2.getPainter().paintInternalFrameTitlePaneBackground(var2, var1, 0, 0, this.getWidth(), this.getHeight());
      this.paint(var2, var1);
      var2.dispose();
   }

   protected void paint(SynthContext var1, Graphics var2) {
      String var3 = this.frame.getTitle();
      if (var3 != null) {
         SynthStyle var4 = var1.getStyle();
         var2.setColor(var4.getColor(var1, ColorType.TEXT_FOREGROUND));
         var2.setFont(var4.getFont(var1));
         FontMetrics var5 = SwingUtilities2.getFontMetrics(this.frame, (Graphics)var2);
         int var6 = (this.getHeight() + var5.getAscent() - var5.getLeading() - var5.getDescent()) / 2;
         JButton var7 = null;
         if (this.frame.isIconifiable()) {
            var7 = this.iconButton;
         } else if (this.frame.isMaximizable()) {
            var7 = this.maxButton;
         } else if (this.frame.isClosable()) {
            var7 = this.closeButton;
         }

         boolean var10 = SynthLookAndFeel.isLeftToRight(this.frame);
         int var11 = this.titleAlignment;
         int var8;
         int var9;
         if (var10) {
            if (var7 != null) {
               var8 = var7.getX() - this.titleSpacing;
            } else {
               var8 = this.frame.getWidth() - this.frame.getInsets().right - this.titleSpacing;
            }

            var9 = this.menuButton.getX() + this.menuButton.getWidth() + this.titleSpacing;
         } else {
            if (var7 != null) {
               var9 = var7.getX() + var7.getWidth() + this.titleSpacing;
            } else {
               var9 = this.frame.getInsets().left + this.titleSpacing;
            }

            var8 = this.menuButton.getX() - this.titleSpacing;
            if (var11 == 10) {
               var11 = 11;
            } else if (var11 == 11) {
               var11 = 10;
            }
         }

         String var12 = this.getTitle(var3, var5, var8 - var9);
         if (var12 == var3) {
            if (var11 == 11) {
               var9 = var8 - var4.getGraphicsUtils(var1).computeStringWidth(var1, var2.getFont(), var5, var3);
            } else if (var11 == 0) {
               int var13 = var4.getGraphicsUtils(var1).computeStringWidth(var1, var2.getFont(), var5, var3);
               var9 = Math.max(var9, (this.getWidth() - var13) / 2);
               var9 = Math.min(var8 - var13, var9);
            }
         }

         var4.getGraphicsUtils(var1).paintText(var1, var2, var12, var9, var6 - var5.getAscent(), -1);
      }

   }

   public void paintBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      var1.getPainter().paintInternalFrameTitlePaneBorder(var1, var2, var3, var4, var5, var6);
   }

   protected LayoutManager createLayout() {
      SynthContext var1 = this.getContext(this);
      LayoutManager var2 = (LayoutManager)this.style.get(var1, "InternalFrameTitlePane.titlePaneLayout");
      var1.dispose();
      return (LayoutManager)(var2 != null ? var2 : new SynthInternalFrameTitlePane.SynthTitlePaneLayout());
   }

   public void propertyChange(PropertyChangeEvent var1) {
      if (var1.getSource() == this) {
         if (SynthLookAndFeel.shouldUpdateStyle(var1)) {
            this.updateStyle(this);
         }
      } else if (var1.getPropertyName() == "frameIcon") {
         this.updateMenuIcon();
      }

   }

   private void updateMenuIcon() {
      Object var1 = this.frame.getFrameIcon();
      SynthContext var2 = this.getContext(this);
      if (var1 != null) {
         Dimension var3 = (Dimension)var2.getStyle().get(var2, "InternalFrameTitlePane.maxFrameIconSize");
         int var4 = 16;
         int var5 = 16;
         if (var3 != null) {
            var4 = var3.width;
            var5 = var3.height;
         }

         if ((((Icon)var1).getIconWidth() > var4 || ((Icon)var1).getIconHeight() > var5) && var1 instanceof ImageIcon) {
            var1 = new ImageIcon(((ImageIcon)var1).getImage().getScaledInstance(var4, var5, 4));
         }
      }

      var2.dispose();
      this.menuButton.setIcon((Icon)var1);
   }

   private JButton createNoFocusButton() {
      JButton var1 = new JButton();
      var1.setFocusable(false);
      var1.setMargin(new Insets(0, 0, 0, 0));
      return var1;
   }

   class SynthTitlePaneLayout implements LayoutManager {
      public void addLayoutComponent(String var1, Component var2) {
      }

      public void removeLayoutComponent(Component var1) {
      }

      public Dimension preferredLayoutSize(Container var1) {
         return this.minimumLayoutSize(var1);
      }

      public Dimension minimumLayoutSize(Container var1) {
         SynthContext var2 = SynthInternalFrameTitlePane.this.getContext(SynthInternalFrameTitlePane.this);
         int var3 = 0;
         int var4 = 0;
         int var5 = 0;
         Dimension var6;
         if (SynthInternalFrameTitlePane.this.frame.isClosable()) {
            var6 = SynthInternalFrameTitlePane.this.closeButton.getPreferredSize();
            var3 += var6.width;
            var4 = Math.max(var6.height, var4);
            ++var5;
         }

         if (SynthInternalFrameTitlePane.this.frame.isMaximizable()) {
            var6 = SynthInternalFrameTitlePane.this.maxButton.getPreferredSize();
            var3 += var6.width;
            var4 = Math.max(var6.height, var4);
            ++var5;
         }

         if (SynthInternalFrameTitlePane.this.frame.isIconifiable()) {
            var6 = SynthInternalFrameTitlePane.this.iconButton.getPreferredSize();
            var3 += var6.width;
            var4 = Math.max(var6.height, var4);
            ++var5;
         }

         var6 = SynthInternalFrameTitlePane.this.menuButton.getPreferredSize();
         var3 += var6.width;
         var4 = Math.max(var6.height, var4);
         var3 += Math.max(0, (var5 - 1) * SynthInternalFrameTitlePane.this.buttonSpacing);
         FontMetrics var7 = SynthInternalFrameTitlePane.this.getFontMetrics(SynthInternalFrameTitlePane.this.getFont());
         SynthGraphicsUtils var8 = var2.getStyle().getGraphicsUtils(var2);
         String var9 = SynthInternalFrameTitlePane.this.frame.getTitle();
         int var10 = var9 != null ? var8.computeStringWidth(var2, var7.getFont(), var7, var9) : 0;
         int var11 = var9 != null ? var9.length() : 0;
         if (var11 > 3) {
            int var12 = var8.computeStringWidth(var2, var7.getFont(), var7, var9.substring(0, 3) + "...");
            var3 += var10 < var12 ? var10 : var12;
         } else {
            var3 += var10;
         }

         var4 = Math.max(var7.getHeight() + 2, var4);
         var3 += SynthInternalFrameTitlePane.this.titleSpacing + SynthInternalFrameTitlePane.this.titleSpacing;
         Insets var13 = SynthInternalFrameTitlePane.this.getInsets();
         var4 += var13.top + var13.bottom;
         var3 += var13.left + var13.right;
         var2.dispose();
         return new Dimension(var3, var4);
      }

      private int center(Component var1, Insets var2, int var3, boolean var4) {
         Dimension var5 = var1.getPreferredSize();
         if (var4) {
            var3 -= var5.width;
         }

         var1.setBounds(var3, var2.top + (SynthInternalFrameTitlePane.this.getHeight() - var2.top - var2.bottom - var5.height) / 2, var5.width, var5.height);
         if (var5.width > 0) {
            return var4 ? var3 - SynthInternalFrameTitlePane.this.buttonSpacing : var3 + var5.width + SynthInternalFrameTitlePane.this.buttonSpacing;
         } else {
            return var3;
         }
      }

      public void layoutContainer(Container var1) {
         Insets var2 = var1.getInsets();
         int var4;
         if (SynthLookAndFeel.isLeftToRight(SynthInternalFrameTitlePane.this.frame)) {
            this.center(SynthInternalFrameTitlePane.this.menuButton, var2, var2.left, false);
            var4 = SynthInternalFrameTitlePane.this.getWidth() - var2.right;
            if (SynthInternalFrameTitlePane.this.frame.isClosable()) {
               var4 = this.center(SynthInternalFrameTitlePane.this.closeButton, var2, var4, true);
            }

            if (SynthInternalFrameTitlePane.this.frame.isMaximizable()) {
               var4 = this.center(SynthInternalFrameTitlePane.this.maxButton, var2, var4, true);
            }

            if (SynthInternalFrameTitlePane.this.frame.isIconifiable()) {
               this.center(SynthInternalFrameTitlePane.this.iconButton, var2, var4, true);
            }
         } else {
            this.center(SynthInternalFrameTitlePane.this.menuButton, var2, SynthInternalFrameTitlePane.this.getWidth() - var2.right, true);
            var4 = var2.left;
            if (SynthInternalFrameTitlePane.this.frame.isClosable()) {
               var4 = this.center(SynthInternalFrameTitlePane.this.closeButton, var2, var4, false);
            }

            if (SynthInternalFrameTitlePane.this.frame.isMaximizable()) {
               var4 = this.center(SynthInternalFrameTitlePane.this.maxButton, var2, var4, false);
            }

            if (SynthInternalFrameTitlePane.this.frame.isIconifiable()) {
               this.center(SynthInternalFrameTitlePane.this.iconButton, var2, var4, false);
            }
         }

      }
   }

   private static class JPopupMenuUIResource extends JPopupMenu implements UIResource {
      private JPopupMenuUIResource() {
      }

      // $FF: synthetic method
      JPopupMenuUIResource(Object var1) {
         this();
      }
   }
}
