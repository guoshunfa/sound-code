package javax.swing.plaf.basic;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import javax.accessibility.AccessibleContext;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.UIManager;
import javax.swing.event.InternalFrameEvent;
import javax.swing.plaf.ActionMapUIResource;
import sun.swing.DefaultLookup;
import sun.swing.SwingUtilities2;

public class BasicInternalFrameTitlePane extends JComponent {
   protected JMenuBar menuBar;
   protected JButton iconButton;
   protected JButton maxButton;
   protected JButton closeButton;
   protected JMenu windowMenu;
   protected JInternalFrame frame;
   protected Color selectedTitleColor;
   protected Color selectedTextColor;
   protected Color notSelectedTitleColor;
   protected Color notSelectedTextColor;
   protected Icon maxIcon;
   protected Icon minIcon;
   protected Icon iconIcon;
   protected Icon closeIcon;
   protected PropertyChangeListener propertyChangeListener;
   protected Action closeAction;
   protected Action maximizeAction;
   protected Action iconifyAction;
   protected Action restoreAction;
   protected Action moveAction;
   protected Action sizeAction;
   protected static final String CLOSE_CMD = UIManager.getString("InternalFrameTitlePane.closeButtonText");
   protected static final String ICONIFY_CMD = UIManager.getString("InternalFrameTitlePane.minimizeButtonText");
   protected static final String RESTORE_CMD = UIManager.getString("InternalFrameTitlePane.restoreButtonText");
   protected static final String MAXIMIZE_CMD = UIManager.getString("InternalFrameTitlePane.maximizeButtonText");
   protected static final String MOVE_CMD = UIManager.getString("InternalFrameTitlePane.moveButtonText");
   protected static final String SIZE_CMD = UIManager.getString("InternalFrameTitlePane.sizeButtonText");
   private String closeButtonToolTip;
   private String iconButtonToolTip;
   private String restoreButtonToolTip;
   private String maxButtonToolTip;
   private BasicInternalFrameTitlePane.Handler handler;

   public BasicInternalFrameTitlePane(JInternalFrame var1) {
      this.frame = var1;
      this.installTitlePane();
   }

   protected void installTitlePane() {
      this.installDefaults();
      this.installListeners();
      this.createActions();
      this.enableActions();
      this.createActionMap();
      this.setLayout(this.createLayout());
      this.assembleSystemMenu();
      this.createButtons();
      this.addSubComponents();
      this.updateProperties();
   }

   private void updateProperties() {
      Object var1 = this.frame.getClientProperty(SwingUtilities2.AA_TEXT_PROPERTY_KEY);
      this.putClientProperty(SwingUtilities2.AA_TEXT_PROPERTY_KEY, var1);
   }

   protected void addSubComponents() {
      this.add(this.menuBar);
      this.add(this.iconButton);
      this.add(this.maxButton);
      this.add(this.closeButton);
   }

   protected void createActions() {
      this.maximizeAction = new BasicInternalFrameTitlePane.MaximizeAction();
      this.iconifyAction = new BasicInternalFrameTitlePane.IconifyAction();
      this.closeAction = new BasicInternalFrameTitlePane.CloseAction();
      this.restoreAction = new BasicInternalFrameTitlePane.RestoreAction();
      this.moveAction = new BasicInternalFrameTitlePane.MoveAction();
      this.sizeAction = new BasicInternalFrameTitlePane.SizeAction();
   }

   ActionMap createActionMap() {
      ActionMapUIResource var1 = new ActionMapUIResource();
      var1.put("showSystemMenu", new BasicInternalFrameTitlePane.ShowSystemMenuAction(true));
      var1.put("hideSystemMenu", new BasicInternalFrameTitlePane.ShowSystemMenuAction(false));
      return var1;
   }

   protected void installListeners() {
      if (this.propertyChangeListener == null) {
         this.propertyChangeListener = this.createPropertyChangeListener();
      }

      this.frame.addPropertyChangeListener(this.propertyChangeListener);
   }

   protected void uninstallListeners() {
      this.frame.removePropertyChangeListener(this.propertyChangeListener);
      this.handler = null;
   }

   protected void installDefaults() {
      this.maxIcon = UIManager.getIcon("InternalFrame.maximizeIcon");
      this.minIcon = UIManager.getIcon("InternalFrame.minimizeIcon");
      this.iconIcon = UIManager.getIcon("InternalFrame.iconifyIcon");
      this.closeIcon = UIManager.getIcon("InternalFrame.closeIcon");
      this.selectedTitleColor = UIManager.getColor("InternalFrame.activeTitleBackground");
      this.selectedTextColor = UIManager.getColor("InternalFrame.activeTitleForeground");
      this.notSelectedTitleColor = UIManager.getColor("InternalFrame.inactiveTitleBackground");
      this.notSelectedTextColor = UIManager.getColor("InternalFrame.inactiveTitleForeground");
      this.setFont(UIManager.getFont("InternalFrame.titleFont"));
      this.closeButtonToolTip = UIManager.getString("InternalFrame.closeButtonToolTip");
      this.iconButtonToolTip = UIManager.getString("InternalFrame.iconButtonToolTip");
      this.restoreButtonToolTip = UIManager.getString("InternalFrame.restoreButtonToolTip");
      this.maxButtonToolTip = UIManager.getString("InternalFrame.maxButtonToolTip");
   }

   protected void uninstallDefaults() {
   }

   protected void createButtons() {
      this.iconButton = new BasicInternalFrameTitlePane.NoFocusButton("InternalFrameTitlePane.iconifyButtonAccessibleName", "InternalFrameTitlePane.iconifyButtonOpacity");
      this.iconButton.addActionListener(this.iconifyAction);
      if (this.iconButtonToolTip != null && this.iconButtonToolTip.length() != 0) {
         this.iconButton.setToolTipText(this.iconButtonToolTip);
      }

      this.maxButton = new BasicInternalFrameTitlePane.NoFocusButton("InternalFrameTitlePane.maximizeButtonAccessibleName", "InternalFrameTitlePane.maximizeButtonOpacity");
      this.maxButton.addActionListener(this.maximizeAction);
      this.closeButton = new BasicInternalFrameTitlePane.NoFocusButton("InternalFrameTitlePane.closeButtonAccessibleName", "InternalFrameTitlePane.closeButtonOpacity");
      this.closeButton.addActionListener(this.closeAction);
      if (this.closeButtonToolTip != null && this.closeButtonToolTip.length() != 0) {
         this.closeButton.setToolTipText(this.closeButtonToolTip);
      }

      this.setButtonIcons();
   }

   protected void setButtonIcons() {
      if (this.frame.isIcon()) {
         if (this.minIcon != null) {
            this.iconButton.setIcon(this.minIcon);
         }

         if (this.restoreButtonToolTip != null && this.restoreButtonToolTip.length() != 0) {
            this.iconButton.setToolTipText(this.restoreButtonToolTip);
         }

         if (this.maxIcon != null) {
            this.maxButton.setIcon(this.maxIcon);
         }

         if (this.maxButtonToolTip != null && this.maxButtonToolTip.length() != 0) {
            this.maxButton.setToolTipText(this.maxButtonToolTip);
         }
      } else if (this.frame.isMaximum()) {
         if (this.iconIcon != null) {
            this.iconButton.setIcon(this.iconIcon);
         }

         if (this.iconButtonToolTip != null && this.iconButtonToolTip.length() != 0) {
            this.iconButton.setToolTipText(this.iconButtonToolTip);
         }

         if (this.minIcon != null) {
            this.maxButton.setIcon(this.minIcon);
         }

         if (this.restoreButtonToolTip != null && this.restoreButtonToolTip.length() != 0) {
            this.maxButton.setToolTipText(this.restoreButtonToolTip);
         }
      } else {
         if (this.iconIcon != null) {
            this.iconButton.setIcon(this.iconIcon);
         }

         if (this.iconButtonToolTip != null && this.iconButtonToolTip.length() != 0) {
            this.iconButton.setToolTipText(this.iconButtonToolTip);
         }

         if (this.maxIcon != null) {
            this.maxButton.setIcon(this.maxIcon);
         }

         if (this.maxButtonToolTip != null && this.maxButtonToolTip.length() != 0) {
            this.maxButton.setToolTipText(this.maxButtonToolTip);
         }
      }

      if (this.closeIcon != null) {
         this.closeButton.setIcon(this.closeIcon);
      }

   }

   protected void assembleSystemMenu() {
      this.menuBar = this.createSystemMenuBar();
      this.windowMenu = this.createSystemMenu();
      this.menuBar.add(this.windowMenu);
      this.addSystemMenuItems(this.windowMenu);
      this.enableActions();
   }

   protected void addSystemMenuItems(JMenu var1) {
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

   protected JMenu createSystemMenu() {
      return new JMenu("    ");
   }

   protected JMenuBar createSystemMenuBar() {
      this.menuBar = new BasicInternalFrameTitlePane.SystemMenuBar();
      this.menuBar.setBorderPainted(false);
      return this.menuBar;
   }

   protected void showSystemMenu() {
      this.windowMenu.doClick();
   }

   public void paintComponent(Graphics var1) {
      this.paintTitleBackground(var1);
      if (this.frame.getTitle() != null) {
         boolean var2 = this.frame.isSelected();
         Font var3 = var1.getFont();
         var1.setFont(this.getFont());
         if (var2) {
            var1.setColor(this.selectedTextColor);
         } else {
            var1.setColor(this.notSelectedTextColor);
         }

         FontMetrics var4 = SwingUtilities2.getFontMetrics(this.frame, (Graphics)var1);
         int var5 = (this.getHeight() + var4.getAscent() - var4.getLeading() - var4.getDescent()) / 2;
         Rectangle var7 = new Rectangle(0, 0, 0, 0);
         if (this.frame.isIconifiable()) {
            var7 = this.iconButton.getBounds();
         } else if (this.frame.isMaximizable()) {
            var7 = this.maxButton.getBounds();
         } else if (this.frame.isClosable()) {
            var7 = this.closeButton.getBounds();
         }

         String var9 = this.frame.getTitle();
         int var6;
         if (BasicGraphicsUtils.isLeftToRight(this.frame)) {
            if (var7.x == 0) {
               var7.x = this.frame.getWidth() - this.frame.getInsets().right;
            }

            var6 = this.menuBar.getX() + this.menuBar.getWidth() + 2;
            int var8 = var7.x - var6 - 3;
            var9 = this.getTitle(this.frame.getTitle(), var4, var8);
         } else {
            var6 = this.menuBar.getX() - 2 - SwingUtilities2.stringWidth(this.frame, var4, var9);
         }

         SwingUtilities2.drawString(this.frame, var1, (String)var9, var6, var5);
         var1.setFont(var3);
      }

   }

   protected void paintTitleBackground(Graphics var1) {
      boolean var2 = this.frame.isSelected();
      if (var2) {
         var1.setColor(this.selectedTitleColor);
      } else {
         var1.setColor(this.notSelectedTitleColor);
      }

      var1.fillRect(0, 0, this.getWidth(), this.getHeight());
   }

   protected String getTitle(String var1, FontMetrics var2, int var3) {
      return SwingUtilities2.clipStringIfNecessary(this.frame, var2, var1, var3);
   }

   protected void postClosingEvent(JInternalFrame var1) {
      InternalFrameEvent var2 = new InternalFrameEvent(var1, 25550);

      try {
         Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(var2);
      } catch (SecurityException var4) {
         var1.dispatchEvent(var2);
      }

   }

   protected void enableActions() {
      this.restoreAction.setEnabled(this.frame.isMaximum() || this.frame.isIcon());
      this.maximizeAction.setEnabled(this.frame.isMaximizable() && !this.frame.isMaximum() && !this.frame.isIcon() || this.frame.isMaximizable() && this.frame.isIcon());
      this.iconifyAction.setEnabled(this.frame.isIconifiable() && !this.frame.isIcon());
      this.closeAction.setEnabled(this.frame.isClosable());
      this.sizeAction.setEnabled(false);
      this.moveAction.setEnabled(false);
   }

   private BasicInternalFrameTitlePane.Handler getHandler() {
      if (this.handler == null) {
         this.handler = new BasicInternalFrameTitlePane.Handler();
      }

      return this.handler;
   }

   protected PropertyChangeListener createPropertyChangeListener() {
      return this.getHandler();
   }

   protected LayoutManager createLayout() {
      return this.getHandler();
   }

   private class NoFocusButton extends JButton {
      private String uiKey;

      public NoFocusButton(String var2, String var3) {
         this.setFocusPainted(false);
         this.setMargin(new Insets(0, 0, 0, 0));
         this.uiKey = var2;
         Object var4 = UIManager.get(var3);
         if (var4 instanceof Boolean) {
            this.setOpaque((Boolean)var4);
         }

      }

      public boolean isFocusTraversable() {
         return false;
      }

      public void requestFocus() {
      }

      public AccessibleContext getAccessibleContext() {
         AccessibleContext var1 = super.getAccessibleContext();
         if (this.uiKey != null) {
            var1.setAccessibleName(UIManager.getString(this.uiKey));
            this.uiKey = null;
         }

         return var1;
      }
   }

   public class SystemMenuBar extends JMenuBar {
      public boolean isFocusTraversable() {
         return false;
      }

      public void requestFocus() {
      }

      public void paint(Graphics var1) {
         Icon var2 = BasicInternalFrameTitlePane.this.frame.getFrameIcon();
         if (var2 == null) {
            var2 = (Icon)DefaultLookup.get(BasicInternalFrameTitlePane.this.frame, BasicInternalFrameTitlePane.this.frame.getUI(), "InternalFrame.icon");
         }

         if (var2 != null) {
            if (var2 instanceof ImageIcon && (var2.getIconWidth() > 16 || var2.getIconHeight() > 16)) {
               Image var3 = ((ImageIcon)var2).getImage();
               ((ImageIcon)var2).setImage(var3.getScaledInstance(16, 16, 4));
            }

            var2.paintIcon(this, var1, 0, 0);
         }

      }

      public boolean isOpaque() {
         return true;
      }
   }

   public class SizeAction extends AbstractAction {
      public SizeAction() {
         super(UIManager.getString("InternalFrameTitlePane.sizeButtonText"));
      }

      public void actionPerformed(ActionEvent var1) {
      }
   }

   private class ShowSystemMenuAction extends AbstractAction {
      private boolean show;

      public ShowSystemMenuAction(boolean var2) {
         this.show = var2;
      }

      public void actionPerformed(ActionEvent var1) {
         if (this.show) {
            BasicInternalFrameTitlePane.this.windowMenu.doClick();
         } else {
            BasicInternalFrameTitlePane.this.windowMenu.setVisible(false);
         }

      }
   }

   public class MoveAction extends AbstractAction {
      public MoveAction() {
         super(UIManager.getString("InternalFrameTitlePane.moveButtonText"));
      }

      public void actionPerformed(ActionEvent var1) {
      }
   }

   public class RestoreAction extends AbstractAction {
      public RestoreAction() {
         super(UIManager.getString("InternalFrameTitlePane.restoreButtonText"));
      }

      public void actionPerformed(ActionEvent var1) {
         if (BasicInternalFrameTitlePane.this.frame.isMaximizable() && BasicInternalFrameTitlePane.this.frame.isMaximum() && BasicInternalFrameTitlePane.this.frame.isIcon()) {
            try {
               BasicInternalFrameTitlePane.this.frame.setIcon(false);
            } catch (PropertyVetoException var5) {
            }
         } else if (BasicInternalFrameTitlePane.this.frame.isMaximizable() && BasicInternalFrameTitlePane.this.frame.isMaximum()) {
            try {
               BasicInternalFrameTitlePane.this.frame.setMaximum(false);
            } catch (PropertyVetoException var4) {
            }
         } else if (BasicInternalFrameTitlePane.this.frame.isIconifiable() && BasicInternalFrameTitlePane.this.frame.isIcon()) {
            try {
               BasicInternalFrameTitlePane.this.frame.setIcon(false);
            } catch (PropertyVetoException var3) {
            }
         }

      }
   }

   public class IconifyAction extends AbstractAction {
      public IconifyAction() {
         super(UIManager.getString("InternalFrameTitlePane.minimizeButtonText"));
      }

      public void actionPerformed(ActionEvent var1) {
         if (BasicInternalFrameTitlePane.this.frame.isIconifiable()) {
            if (!BasicInternalFrameTitlePane.this.frame.isIcon()) {
               try {
                  BasicInternalFrameTitlePane.this.frame.setIcon(true);
               } catch (PropertyVetoException var4) {
               }
            } else {
               try {
                  BasicInternalFrameTitlePane.this.frame.setIcon(false);
               } catch (PropertyVetoException var3) {
               }
            }
         }

      }
   }

   public class MaximizeAction extends AbstractAction {
      public MaximizeAction() {
         super(UIManager.getString("InternalFrameTitlePane.maximizeButtonText"));
      }

      public void actionPerformed(ActionEvent var1) {
         if (BasicInternalFrameTitlePane.this.frame.isMaximizable()) {
            if (BasicInternalFrameTitlePane.this.frame.isMaximum() && BasicInternalFrameTitlePane.this.frame.isIcon()) {
               try {
                  BasicInternalFrameTitlePane.this.frame.setIcon(false);
               } catch (PropertyVetoException var5) {
               }
            } else if (!BasicInternalFrameTitlePane.this.frame.isMaximum()) {
               try {
                  BasicInternalFrameTitlePane.this.frame.setMaximum(true);
               } catch (PropertyVetoException var4) {
               }
            } else {
               try {
                  BasicInternalFrameTitlePane.this.frame.setMaximum(false);
               } catch (PropertyVetoException var3) {
               }
            }
         }

      }
   }

   public class CloseAction extends AbstractAction {
      public CloseAction() {
         super(UIManager.getString("InternalFrameTitlePane.closeButtonText"));
      }

      public void actionPerformed(ActionEvent var1) {
         if (BasicInternalFrameTitlePane.this.frame.isClosable()) {
            BasicInternalFrameTitlePane.this.frame.doDefaultCloseAction();
         }

      }
   }

   public class TitlePaneLayout implements LayoutManager {
      public void addLayoutComponent(String var1, Component var2) {
         BasicInternalFrameTitlePane.this.getHandler().addLayoutComponent(var1, var2);
      }

      public void removeLayoutComponent(Component var1) {
         BasicInternalFrameTitlePane.this.getHandler().removeLayoutComponent(var1);
      }

      public Dimension preferredLayoutSize(Container var1) {
         return BasicInternalFrameTitlePane.this.getHandler().preferredLayoutSize(var1);
      }

      public Dimension minimumLayoutSize(Container var1) {
         return BasicInternalFrameTitlePane.this.getHandler().minimumLayoutSize(var1);
      }

      public void layoutContainer(Container var1) {
         BasicInternalFrameTitlePane.this.getHandler().layoutContainer(var1);
      }
   }

   public class PropertyChangeHandler implements PropertyChangeListener {
      public void propertyChange(PropertyChangeEvent var1) {
         BasicInternalFrameTitlePane.this.getHandler().propertyChange(var1);
      }
   }

   private class Handler implements LayoutManager, PropertyChangeListener {
      private Handler() {
      }

      public void propertyChange(PropertyChangeEvent var1) {
         String var2 = var1.getPropertyName();
         if (var2 == "selected") {
            BasicInternalFrameTitlePane.this.repaint();
         } else if (var2 != "icon" && var2 != "maximum") {
            if ("closable" == var2) {
               if (var1.getNewValue() == Boolean.TRUE) {
                  BasicInternalFrameTitlePane.this.add(BasicInternalFrameTitlePane.this.closeButton);
               } else {
                  BasicInternalFrameTitlePane.this.remove(BasicInternalFrameTitlePane.this.closeButton);
               }
            } else if ("maximizable" == var2) {
               if (var1.getNewValue() == Boolean.TRUE) {
                  BasicInternalFrameTitlePane.this.add(BasicInternalFrameTitlePane.this.maxButton);
               } else {
                  BasicInternalFrameTitlePane.this.remove(BasicInternalFrameTitlePane.this.maxButton);
               }
            } else if ("iconable" == var2) {
               if (var1.getNewValue() == Boolean.TRUE) {
                  BasicInternalFrameTitlePane.this.add(BasicInternalFrameTitlePane.this.iconButton);
               } else {
                  BasicInternalFrameTitlePane.this.remove(BasicInternalFrameTitlePane.this.iconButton);
               }
            }

            BasicInternalFrameTitlePane.this.enableActions();
            BasicInternalFrameTitlePane.this.revalidate();
            BasicInternalFrameTitlePane.this.repaint();
         } else {
            BasicInternalFrameTitlePane.this.setButtonIcons();
            BasicInternalFrameTitlePane.this.enableActions();
         }
      }

      public void addLayoutComponent(String var1, Component var2) {
      }

      public void removeLayoutComponent(Component var1) {
      }

      public Dimension preferredLayoutSize(Container var1) {
         return this.minimumLayoutSize(var1);
      }

      public Dimension minimumLayoutSize(Container var1) {
         int var2 = 22;
         if (BasicInternalFrameTitlePane.this.frame.isClosable()) {
            var2 += 19;
         }

         if (BasicInternalFrameTitlePane.this.frame.isMaximizable()) {
            var2 += 19;
         }

         if (BasicInternalFrameTitlePane.this.frame.isIconifiable()) {
            var2 += 19;
         }

         FontMetrics var3 = BasicInternalFrameTitlePane.this.frame.getFontMetrics(BasicInternalFrameTitlePane.this.getFont());
         String var4 = BasicInternalFrameTitlePane.this.frame.getTitle();
         int var5 = var4 != null ? SwingUtilities2.stringWidth(BasicInternalFrameTitlePane.this.frame, var3, var4) : 0;
         int var6 = var4 != null ? var4.length() : 0;
         if (var6 > 3) {
            int var7 = SwingUtilities2.stringWidth(BasicInternalFrameTitlePane.this.frame, var3, var4.substring(0, 3) + "...");
            var2 += var5 < var7 ? var5 : var7;
         } else {
            var2 += var5;
         }

         Icon var13 = BasicInternalFrameTitlePane.this.frame.getFrameIcon();
         int var8 = var3.getHeight();
         var8 += 2;
         int var9 = 0;
         if (var13 != null) {
            var9 = Math.min(var13.getIconHeight(), 16);
         }

         var9 += 2;
         int var10 = Math.max(var8, var9);
         Dimension var11 = new Dimension(var2, var10);
         if (BasicInternalFrameTitlePane.this.getBorder() != null) {
            Insets var12 = BasicInternalFrameTitlePane.this.getBorder().getBorderInsets(var1);
            var11.height += var12.top + var12.bottom;
            var11.width += var12.left + var12.right;
         }

         return var11;
      }

      public void layoutContainer(Container var1) {
         boolean var2 = BasicGraphicsUtils.isLeftToRight(BasicInternalFrameTitlePane.this.frame);
         int var3 = BasicInternalFrameTitlePane.this.getWidth();
         int var4 = BasicInternalFrameTitlePane.this.getHeight();
         int var6 = BasicInternalFrameTitlePane.this.closeButton.getIcon().getIconHeight();
         Icon var7 = BasicInternalFrameTitlePane.this.frame.getFrameIcon();
         int var8 = 0;
         if (var7 != null) {
            var8 = var7.getIconHeight();
         }

         int var5 = var2 ? 2 : var3 - 16 - 2;
         BasicInternalFrameTitlePane.this.menuBar.setBounds(var5, (var4 - var8) / 2, 16, 16);
         var5 = var2 ? var3 - 16 - 2 : 2;
         if (BasicInternalFrameTitlePane.this.frame.isClosable()) {
            BasicInternalFrameTitlePane.this.closeButton.setBounds(var5, (var4 - var6) / 2, 16, 14);
            var5 += var2 ? -18 : 18;
         }

         if (BasicInternalFrameTitlePane.this.frame.isMaximizable()) {
            BasicInternalFrameTitlePane.this.maxButton.setBounds(var5, (var4 - var6) / 2, 16, 14);
            var5 += var2 ? -18 : 18;
         }

         if (BasicInternalFrameTitlePane.this.frame.isIconifiable()) {
            BasicInternalFrameTitlePane.this.iconButton.setBounds(var5, (var4 - var6) / 2, 16, 14);
         }

      }

      // $FF: synthetic method
      Handler(Object var2) {
         this();
      }
   }
}
