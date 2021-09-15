package javax.swing.plaf.basic;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.ActionMap;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.MenuDragMouseEvent;
import javax.swing.event.MenuDragMouseListener;
import javax.swing.event.MenuKeyListener;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentInputMapUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.MenuItemUI;
import javax.swing.plaf.UIResource;
import javax.swing.text.View;
import sun.swing.MenuItemCheckIconFactory;
import sun.swing.MenuItemLayoutHelper;
import sun.swing.SwingUtilities2;
import sun.swing.UIAction;

public class BasicMenuItemUI extends MenuItemUI {
   protected JMenuItem menuItem = null;
   protected Color selectionBackground;
   protected Color selectionForeground;
   protected Color disabledForeground;
   protected Color acceleratorForeground;
   protected Color acceleratorSelectionForeground;
   protected String acceleratorDelimiter;
   protected int defaultTextIconGap;
   protected Font acceleratorFont;
   protected MouseInputListener mouseInputListener;
   protected MenuDragMouseListener menuDragMouseListener;
   protected MenuKeyListener menuKeyListener;
   protected PropertyChangeListener propertyChangeListener;
   BasicMenuItemUI.Handler handler;
   protected Icon arrowIcon = null;
   protected Icon checkIcon = null;
   protected boolean oldBorderPainted;
   private static final boolean TRACE = false;
   private static final boolean VERBOSE = false;
   private static final boolean DEBUG = false;

   static void loadActionMap(LazyActionMap var0) {
      var0.put(new BasicMenuItemUI.Actions("doClick"));
      BasicLookAndFeel.installAudioActionMap(var0);
   }

   public static ComponentUI createUI(JComponent var0) {
      return new BasicMenuItemUI();
   }

   public void installUI(JComponent var1) {
      this.menuItem = (JMenuItem)var1;
      this.installDefaults();
      this.installComponents(this.menuItem);
      this.installListeners();
      this.installKeyboardActions();
   }

   protected void installDefaults() {
      String var1 = this.getPropertyPrefix();
      this.acceleratorFont = UIManager.getFont("MenuItem.acceleratorFont");
      if (this.acceleratorFont == null) {
         this.acceleratorFont = UIManager.getFont("MenuItem.font");
      }

      Object var2 = UIManager.get(this.getPropertyPrefix() + ".opaque");
      if (var2 != null) {
         LookAndFeel.installProperty(this.menuItem, "opaque", var2);
      } else {
         LookAndFeel.installProperty(this.menuItem, "opaque", Boolean.TRUE);
      }

      if (this.menuItem.getMargin() == null || this.menuItem.getMargin() instanceof UIResource) {
         this.menuItem.setMargin(UIManager.getInsets(var1 + ".margin"));
      }

      LookAndFeel.installProperty(this.menuItem, "iconTextGap", 4);
      this.defaultTextIconGap = this.menuItem.getIconTextGap();
      LookAndFeel.installBorder(this.menuItem, var1 + ".border");
      this.oldBorderPainted = this.menuItem.isBorderPainted();
      LookAndFeel.installProperty(this.menuItem, "borderPainted", UIManager.getBoolean(var1 + ".borderPainted"));
      LookAndFeel.installColorsAndFont(this.menuItem, var1 + ".background", var1 + ".foreground", var1 + ".font");
      if (this.selectionBackground == null || this.selectionBackground instanceof UIResource) {
         this.selectionBackground = UIManager.getColor(var1 + ".selectionBackground");
      }

      if (this.selectionForeground == null || this.selectionForeground instanceof UIResource) {
         this.selectionForeground = UIManager.getColor(var1 + ".selectionForeground");
      }

      if (this.disabledForeground == null || this.disabledForeground instanceof UIResource) {
         this.disabledForeground = UIManager.getColor(var1 + ".disabledForeground");
      }

      if (this.acceleratorForeground == null || this.acceleratorForeground instanceof UIResource) {
         this.acceleratorForeground = UIManager.getColor(var1 + ".acceleratorForeground");
      }

      if (this.acceleratorSelectionForeground == null || this.acceleratorSelectionForeground instanceof UIResource) {
         this.acceleratorSelectionForeground = UIManager.getColor(var1 + ".acceleratorSelectionForeground");
      }

      this.acceleratorDelimiter = UIManager.getString("MenuItem.acceleratorDelimiter");
      if (this.acceleratorDelimiter == null) {
         this.acceleratorDelimiter = "+";
      }

      if (this.arrowIcon == null || this.arrowIcon instanceof UIResource) {
         this.arrowIcon = UIManager.getIcon(var1 + ".arrowIcon");
      }

      this.updateCheckIcon();
   }

   private void updateCheckIcon() {
      String var1 = this.getPropertyPrefix();
      if (this.checkIcon == null || this.checkIcon instanceof UIResource) {
         this.checkIcon = UIManager.getIcon(var1 + ".checkIcon");
         boolean var2 = MenuItemLayoutHelper.isColumnLayout(BasicGraphicsUtils.isLeftToRight(this.menuItem), this.menuItem);
         if (var2) {
            MenuItemCheckIconFactory var3 = (MenuItemCheckIconFactory)UIManager.get(var1 + ".checkIconFactory");
            if (var3 != null && MenuItemLayoutHelper.useCheckAndArrow(this.menuItem) && var3.isCompatible(this.checkIcon, var1)) {
               this.checkIcon = var3.getIcon(this.menuItem);
            }
         }
      }

   }

   protected void installComponents(JMenuItem var1) {
      BasicHTML.updateRenderer(var1, var1.getText());
   }

   protected String getPropertyPrefix() {
      return "MenuItem";
   }

   protected void installListeners() {
      if ((this.mouseInputListener = this.createMouseInputListener(this.menuItem)) != null) {
         this.menuItem.addMouseListener(this.mouseInputListener);
         this.menuItem.addMouseMotionListener(this.mouseInputListener);
      }

      if ((this.menuDragMouseListener = this.createMenuDragMouseListener(this.menuItem)) != null) {
         this.menuItem.addMenuDragMouseListener(this.menuDragMouseListener);
      }

      if ((this.menuKeyListener = this.createMenuKeyListener(this.menuItem)) != null) {
         this.menuItem.addMenuKeyListener(this.menuKeyListener);
      }

      if ((this.propertyChangeListener = this.createPropertyChangeListener(this.menuItem)) != null) {
         this.menuItem.addPropertyChangeListener(this.propertyChangeListener);
      }

   }

   protected void installKeyboardActions() {
      this.installLazyActionMap();
      this.updateAcceleratorBinding();
   }

   void installLazyActionMap() {
      LazyActionMap.installLazyActionMap(this.menuItem, BasicMenuItemUI.class, this.getPropertyPrefix() + ".actionMap");
   }

   public void uninstallUI(JComponent var1) {
      this.menuItem = (JMenuItem)var1;
      this.uninstallDefaults();
      this.uninstallComponents(this.menuItem);
      this.uninstallListeners();
      this.uninstallKeyboardActions();
      MenuItemLayoutHelper.clearUsedParentClientProperties(this.menuItem);
      this.menuItem = null;
   }

   protected void uninstallDefaults() {
      LookAndFeel.uninstallBorder(this.menuItem);
      LookAndFeel.installProperty(this.menuItem, "borderPainted", this.oldBorderPainted);
      if (this.menuItem.getMargin() instanceof UIResource) {
         this.menuItem.setMargin((Insets)null);
      }

      if (this.arrowIcon instanceof UIResource) {
         this.arrowIcon = null;
      }

      if (this.checkIcon instanceof UIResource) {
         this.checkIcon = null;
      }

   }

   protected void uninstallComponents(JMenuItem var1) {
      BasicHTML.updateRenderer(var1, "");
   }

   protected void uninstallListeners() {
      if (this.mouseInputListener != null) {
         this.menuItem.removeMouseListener(this.mouseInputListener);
         this.menuItem.removeMouseMotionListener(this.mouseInputListener);
      }

      if (this.menuDragMouseListener != null) {
         this.menuItem.removeMenuDragMouseListener(this.menuDragMouseListener);
      }

      if (this.menuKeyListener != null) {
         this.menuItem.removeMenuKeyListener(this.menuKeyListener);
      }

      if (this.propertyChangeListener != null) {
         this.menuItem.removePropertyChangeListener(this.propertyChangeListener);
      }

      this.mouseInputListener = null;
      this.menuDragMouseListener = null;
      this.menuKeyListener = null;
      this.propertyChangeListener = null;
      this.handler = null;
   }

   protected void uninstallKeyboardActions() {
      SwingUtilities.replaceUIActionMap(this.menuItem, (ActionMap)null);
      SwingUtilities.replaceUIInputMap(this.menuItem, 2, (InputMap)null);
   }

   protected MouseInputListener createMouseInputListener(JComponent var1) {
      return this.getHandler();
   }

   protected MenuDragMouseListener createMenuDragMouseListener(JComponent var1) {
      return this.getHandler();
   }

   protected MenuKeyListener createMenuKeyListener(JComponent var1) {
      return null;
   }

   protected PropertyChangeListener createPropertyChangeListener(JComponent var1) {
      return this.getHandler();
   }

   BasicMenuItemUI.Handler getHandler() {
      if (this.handler == null) {
         this.handler = new BasicMenuItemUI.Handler();
      }

      return this.handler;
   }

   InputMap createInputMap(int var1) {
      return var1 == 2 ? new ComponentInputMapUIResource(this.menuItem) : null;
   }

   void updateAcceleratorBinding() {
      KeyStroke var1 = this.menuItem.getAccelerator();
      InputMap var2 = SwingUtilities.getUIInputMap(this.menuItem, 2);
      if (var2 != null) {
         var2.clear();
      }

      if (var1 != null) {
         if (var2 == null) {
            var2 = this.createInputMap(2);
            SwingUtilities.replaceUIInputMap(this.menuItem, 2, var2);
         }

         var2.put(var1, "doClick");
      }

   }

   public Dimension getMinimumSize(JComponent var1) {
      Dimension var2 = null;
      View var3 = (View)var1.getClientProperty("html");
      if (var3 != null) {
         var2 = this.getPreferredSize(var1);
         var2.width = (int)((float)var2.width - (var3.getPreferredSpan(0) - var3.getMinimumSpan(0)));
      }

      return var2;
   }

   public Dimension getPreferredSize(JComponent var1) {
      return this.getPreferredMenuItemSize(var1, this.checkIcon, this.arrowIcon, this.defaultTextIconGap);
   }

   public Dimension getMaximumSize(JComponent var1) {
      Dimension var2 = null;
      View var3 = (View)var1.getClientProperty("html");
      if (var3 != null) {
         var2 = this.getPreferredSize(var1);
         var2.width = (int)((float)var2.width + (var3.getMaximumSpan(0) - var3.getPreferredSpan(0)));
      }

      return var2;
   }

   protected Dimension getPreferredMenuItemSize(JComponent var1, Icon var2, Icon var3, int var4) {
      JMenuItem var5 = (JMenuItem)var1;
      MenuItemLayoutHelper var6 = new MenuItemLayoutHelper(var5, var2, var3, MenuItemLayoutHelper.createMaxRect(), var4, this.acceleratorDelimiter, BasicGraphicsUtils.isLeftToRight(var5), var5.getFont(), this.acceleratorFont, MenuItemLayoutHelper.useCheckAndArrow(this.menuItem), this.getPropertyPrefix());
      Dimension var7 = new Dimension();
      var7.width = var6.getLeadingGap();
      MenuItemLayoutHelper.addMaxWidth(var6.getCheckSize(), var6.getAfterCheckIconGap(), var7);
      if (!var6.isTopLevelMenu() && var6.getMinTextOffset() > 0 && var7.width < var6.getMinTextOffset()) {
         var7.width = var6.getMinTextOffset();
      }

      MenuItemLayoutHelper.addMaxWidth(var6.getLabelSize(), var6.getGap(), var7);
      MenuItemLayoutHelper.addMaxWidth(var6.getAccSize(), var6.getGap(), var7);
      MenuItemLayoutHelper.addMaxWidth(var6.getArrowSize(), var6.getGap(), var7);
      var7.height = MenuItemLayoutHelper.max(var6.getCheckSize().getHeight(), var6.getLabelSize().getHeight(), var6.getAccSize().getHeight(), var6.getArrowSize().getHeight());
      Insets var8 = var6.getMenuItem().getInsets();
      if (var8 != null) {
         var7.width += var8.left + var8.right;
         var7.height += var8.top + var8.bottom;
      }

      if (var7.width % 2 == 0) {
         ++var7.width;
      }

      if (var7.height % 2 == 0 && Boolean.TRUE != UIManager.get(this.getPropertyPrefix() + ".evenHeight")) {
         ++var7.height;
      }

      return var7;
   }

   public void update(Graphics var1, JComponent var2) {
      this.paint(var1, var2);
   }

   public void paint(Graphics var1, JComponent var2) {
      this.paintMenuItem(var1, var2, this.checkIcon, this.arrowIcon, this.selectionBackground, this.selectionForeground, this.defaultTextIconGap);
   }

   protected void paintMenuItem(Graphics var1, JComponent var2, Icon var3, Icon var4, Color var5, Color var6, int var7) {
      Font var8 = var1.getFont();
      Color var9 = var1.getColor();
      JMenuItem var10 = (JMenuItem)var2;
      var1.setFont(var10.getFont());
      Rectangle var11 = new Rectangle(0, 0, var10.getWidth(), var10.getHeight());
      this.applyInsets(var11, var10.getInsets());
      MenuItemLayoutHelper var12 = new MenuItemLayoutHelper(var10, var3, var4, var11, var7, this.acceleratorDelimiter, BasicGraphicsUtils.isLeftToRight(var10), var10.getFont(), this.acceleratorFont, MenuItemLayoutHelper.useCheckAndArrow(this.menuItem), this.getPropertyPrefix());
      MenuItemLayoutHelper.LayoutResult var13 = var12.layoutMenuItem();
      this.paintBackground(var1, var10, var5);
      this.paintCheckIcon(var1, var12, var13, var9, var6);
      this.paintIcon(var1, var12, var13, var9);
      this.paintText(var1, var12, var13);
      this.paintAccText(var1, var12, var13);
      this.paintArrowIcon(var1, var12, var13, var6);
      var1.setColor(var9);
      var1.setFont(var8);
   }

   private void paintIcon(Graphics var1, MenuItemLayoutHelper var2, MenuItemLayoutHelper.LayoutResult var3, Color var4) {
      if (var2.getIcon() != null) {
         ButtonModel var6 = var2.getMenuItem().getModel();
         Icon var5;
         if (!var6.isEnabled()) {
            var5 = var2.getMenuItem().getDisabledIcon();
         } else if (var6.isPressed() && var6.isArmed()) {
            var5 = var2.getMenuItem().getPressedIcon();
            if (var5 == null) {
               var5 = var2.getMenuItem().getIcon();
            }
         } else {
            var5 = var2.getMenuItem().getIcon();
         }

         if (var5 != null) {
            var5.paintIcon(var2.getMenuItem(), var1, var3.getIconRect().x, var3.getIconRect().y);
            var1.setColor(var4);
         }
      }

   }

   private void paintCheckIcon(Graphics var1, MenuItemLayoutHelper var2, MenuItemLayoutHelper.LayoutResult var3, Color var4, Color var5) {
      if (var2.getCheckIcon() != null) {
         ButtonModel var6 = var2.getMenuItem().getModel();
         if (!var6.isArmed() && (!(var2.getMenuItem() instanceof JMenu) || !var6.isSelected())) {
            var1.setColor(var4);
         } else {
            var1.setColor(var5);
         }

         if (var2.useCheckAndArrow()) {
            var2.getCheckIcon().paintIcon(var2.getMenuItem(), var1, var3.getCheckRect().x, var3.getCheckRect().y);
         }

         var1.setColor(var4);
      }

   }

   private void paintAccText(Graphics var1, MenuItemLayoutHelper var2, MenuItemLayoutHelper.LayoutResult var3) {
      if (!var2.getAccText().equals("")) {
         ButtonModel var4 = var2.getMenuItem().getModel();
         var1.setFont(var2.getAccFontMetrics().getFont());
         if (!var4.isEnabled()) {
            if (this.disabledForeground != null) {
               var1.setColor(this.disabledForeground);
               SwingUtilities2.drawString(var2.getMenuItem(), var1, (String)var2.getAccText(), var3.getAccRect().x, var3.getAccRect().y + var2.getAccFontMetrics().getAscent());
            } else {
               var1.setColor(var2.getMenuItem().getBackground().brighter());
               SwingUtilities2.drawString(var2.getMenuItem(), var1, (String)var2.getAccText(), var3.getAccRect().x, var3.getAccRect().y + var2.getAccFontMetrics().getAscent());
               var1.setColor(var2.getMenuItem().getBackground().darker());
               SwingUtilities2.drawString(var2.getMenuItem(), var1, (String)var2.getAccText(), var3.getAccRect().x - 1, var3.getAccRect().y + var2.getFontMetrics().getAscent() - 1);
            }
         } else {
            if (!var4.isArmed() && (!(var2.getMenuItem() instanceof JMenu) || !var4.isSelected())) {
               var1.setColor(this.acceleratorForeground);
            } else {
               var1.setColor(this.acceleratorSelectionForeground);
            }

            SwingUtilities2.drawString(var2.getMenuItem(), var1, (String)var2.getAccText(), var3.getAccRect().x, var3.getAccRect().y + var2.getAccFontMetrics().getAscent());
         }
      }

   }

   private void paintText(Graphics var1, MenuItemLayoutHelper var2, MenuItemLayoutHelper.LayoutResult var3) {
      if (!var2.getText().equals("")) {
         if (var2.getHtmlView() != null) {
            var2.getHtmlView().paint(var1, var3.getTextRect());
         } else {
            this.paintText(var1, var2.getMenuItem(), var3.getTextRect(), var2.getText());
         }
      }

   }

   private void paintArrowIcon(Graphics var1, MenuItemLayoutHelper var2, MenuItemLayoutHelper.LayoutResult var3, Color var4) {
      if (var2.getArrowIcon() != null) {
         ButtonModel var5 = var2.getMenuItem().getModel();
         if (var5.isArmed() || var2.getMenuItem() instanceof JMenu && var5.isSelected()) {
            var1.setColor(var4);
         }

         if (var2.useCheckAndArrow()) {
            var2.getArrowIcon().paintIcon(var2.getMenuItem(), var1, var3.getArrowRect().x, var3.getArrowRect().y);
         }
      }

   }

   private void applyInsets(Rectangle var1, Insets var2) {
      if (var2 != null) {
         var1.x += var2.left;
         var1.y += var2.top;
         var1.width -= var2.right + var1.x;
         var1.height -= var2.bottom + var1.y;
      }

   }

   protected void paintBackground(Graphics var1, JMenuItem var2, Color var3) {
      ButtonModel var4 = var2.getModel();
      Color var5 = var1.getColor();
      int var6 = var2.getWidth();
      int var7 = var2.getHeight();
      if (var2.isOpaque()) {
         if (!var4.isArmed() && (!(var2 instanceof JMenu) || !var4.isSelected())) {
            var1.setColor(var2.getBackground());
            var1.fillRect(0, 0, var6, var7);
         } else {
            var1.setColor(var3);
            var1.fillRect(0, 0, var6, var7);
         }

         var1.setColor(var5);
      } else if (var4.isArmed() || var2 instanceof JMenu && var4.isSelected()) {
         var1.setColor(var3);
         var1.fillRect(0, 0, var6, var7);
         var1.setColor(var5);
      }

   }

   protected void paintText(Graphics var1, JMenuItem var2, Rectangle var3, String var4) {
      ButtonModel var5 = var2.getModel();
      FontMetrics var6 = SwingUtilities2.getFontMetrics(var2, (Graphics)var1);
      int var7 = var2.getDisplayedMnemonicIndex();
      if (!var5.isEnabled()) {
         if (UIManager.get("MenuItem.disabledForeground") instanceof Color) {
            var1.setColor(UIManager.getColor("MenuItem.disabledForeground"));
            SwingUtilities2.drawStringUnderlineCharAt(var2, var1, var4, var7, var3.x, var3.y + var6.getAscent());
         } else {
            var1.setColor(var2.getBackground().brighter());
            SwingUtilities2.drawStringUnderlineCharAt(var2, var1, var4, var7, var3.x, var3.y + var6.getAscent());
            var1.setColor(var2.getBackground().darker());
            SwingUtilities2.drawStringUnderlineCharAt(var2, var1, var4, var7, var3.x - 1, var3.y + var6.getAscent() - 1);
         }
      } else {
         if (var5.isArmed() || var2 instanceof JMenu && var5.isSelected()) {
            var1.setColor(this.selectionForeground);
         }

         SwingUtilities2.drawStringUnderlineCharAt(var2, var1, var4, var7, var3.x, var3.y + var6.getAscent());
      }

   }

   public MenuElement[] getPath() {
      MenuSelectionManager var1 = MenuSelectionManager.defaultManager();
      MenuElement[] var2 = var1.getSelectedPath();
      int var4 = var2.length;
      if (var4 == 0) {
         return new MenuElement[0];
      } else {
         Container var5 = this.menuItem.getParent();
         MenuElement[] var3;
         if (var2[var4 - 1].getComponent() == var5) {
            var3 = new MenuElement[var4 + 1];
            System.arraycopy(var2, 0, var3, 0, var4);
            var3[var4] = this.menuItem;
         } else {
            int var6;
            for(var6 = var2.length - 1; var6 >= 0 && var2[var6].getComponent() != var5; --var6) {
            }

            var3 = new MenuElement[var6 + 2];
            System.arraycopy(var2, 0, var3, 0, var6 + 1);
            var3[var6 + 1] = this.menuItem;
         }

         return var3;
      }
   }

   void printMenuElementArray(MenuElement[] var1, boolean var2) {
      System.out.println("Path is(");
      int var3 = 0;

      for(int var4 = var1.length; var3 < var4; ++var3) {
         for(int var5 = 0; var5 <= var3; ++var5) {
            System.out.print("  ");
         }

         MenuElement var6 = var1[var3];
         if (var6 instanceof JMenuItem) {
            System.out.println(((JMenuItem)var6).getText() + ", ");
         } else if (var6 == null) {
            System.out.println("NULL , ");
         } else {
            System.out.println("" + var6 + ", ");
         }
      }

      System.out.println(")");
      if (var2) {
         Thread.dumpStack();
      }

   }

   protected void doClick(MenuSelectionManager var1) {
      if (!this.isInternalFrameSystemMenu()) {
         BasicLookAndFeel.playSound(this.menuItem, this.getPropertyPrefix() + ".commandSound");
      }

      if (var1 == null) {
         var1 = MenuSelectionManager.defaultManager();
      }

      var1.clearSelectedPath();
      this.menuItem.doClick(0);
   }

   private boolean isInternalFrameSystemMenu() {
      String var1 = this.menuItem.getActionCommand();
      return var1 == "Close" || var1 == "Minimize" || var1 == "Restore" || var1 == "Maximize";
   }

   class Handler implements MenuDragMouseListener, MouseInputListener, PropertyChangeListener {
      public void mouseClicked(MouseEvent var1) {
      }

      public void mousePressed(MouseEvent var1) {
      }

      public void mouseReleased(MouseEvent var1) {
         if (BasicMenuItemUI.this.menuItem.isEnabled()) {
            MenuSelectionManager var2 = MenuSelectionManager.defaultManager();
            Point var3 = var1.getPoint();
            if (var3.x >= 0 && var3.x < BasicMenuItemUI.this.menuItem.getWidth() && var3.y >= 0 && var3.y < BasicMenuItemUI.this.menuItem.getHeight()) {
               BasicMenuItemUI.this.doClick(var2);
            } else {
               var2.processMouseEvent(var1);
            }

         }
      }

      public void mouseEntered(MouseEvent var1) {
         MenuSelectionManager var2 = MenuSelectionManager.defaultManager();
         int var3 = var1.getModifiers();
         if ((var3 & 28) != 0) {
            MenuSelectionManager.defaultManager().processMouseEvent(var1);
         } else {
            var2.setSelectedPath(BasicMenuItemUI.this.getPath());
         }

      }

      public void mouseExited(MouseEvent var1) {
         MenuSelectionManager var2 = MenuSelectionManager.defaultManager();
         int var3 = var1.getModifiers();
         if ((var3 & 28) != 0) {
            MenuSelectionManager.defaultManager().processMouseEvent(var1);
         } else {
            MenuElement[] var4 = var2.getSelectedPath();
            if (var4.length > 1 && var4[var4.length - 1] == BasicMenuItemUI.this.menuItem) {
               MenuElement[] var5 = new MenuElement[var4.length - 1];
               int var6 = 0;

               for(int var7 = var4.length - 1; var6 < var7; ++var6) {
                  var5[var6] = var4[var6];
               }

               var2.setSelectedPath(var5);
            }
         }

      }

      public void mouseDragged(MouseEvent var1) {
         MenuSelectionManager.defaultManager().processMouseEvent(var1);
      }

      public void mouseMoved(MouseEvent var1) {
      }

      public void menuDragMouseEntered(MenuDragMouseEvent var1) {
         MenuSelectionManager var2 = var1.getMenuSelectionManager();
         MenuElement[] var3 = var1.getPath();
         var2.setSelectedPath(var3);
      }

      public void menuDragMouseDragged(MenuDragMouseEvent var1) {
         MenuSelectionManager var2 = var1.getMenuSelectionManager();
         MenuElement[] var3 = var1.getPath();
         var2.setSelectedPath(var3);
      }

      public void menuDragMouseExited(MenuDragMouseEvent var1) {
      }

      public void menuDragMouseReleased(MenuDragMouseEvent var1) {
         if (BasicMenuItemUI.this.menuItem.isEnabled()) {
            MenuSelectionManager var2 = var1.getMenuSelectionManager();
            MenuElement[] var3 = var1.getPath();
            Point var4 = var1.getPoint();
            if (var4.x >= 0 && var4.x < BasicMenuItemUI.this.menuItem.getWidth() && var4.y >= 0 && var4.y < BasicMenuItemUI.this.menuItem.getHeight()) {
               BasicMenuItemUI.this.doClick(var2);
            } else {
               var2.clearSelectedPath();
            }

         }
      }

      public void propertyChange(PropertyChangeEvent var1) {
         String var2 = var1.getPropertyName();
         if (var2 != "labelFor" && var2 != "displayedMnemonic" && var2 != "accelerator") {
            if (var2 != "text" && "font" != var2 && "foreground" != var2) {
               if (var2 == "iconTextGap") {
                  BasicMenuItemUI.this.defaultTextIconGap = ((Number)var1.getNewValue()).intValue();
               } else if (var2 == "horizontalTextPosition") {
                  BasicMenuItemUI.this.updateCheckIcon();
               }
            } else {
               JMenuItem var3 = (JMenuItem)var1.getSource();
               String var4 = var3.getText();
               BasicHTML.updateRenderer(var3, var4);
            }
         } else {
            BasicMenuItemUI.this.updateAcceleratorBinding();
         }

      }
   }

   private static class Actions extends UIAction {
      private static final String CLICK = "doClick";

      Actions(String var1) {
         super(var1);
      }

      public void actionPerformed(ActionEvent var1) {
         JMenuItem var2 = (JMenuItem)var1.getSource();
         MenuSelectionManager.defaultManager().clearSelectedPath();
         var2.doClick();
      }
   }

   protected class MouseInputHandler implements MouseInputListener {
      public void mouseClicked(MouseEvent var1) {
         BasicMenuItemUI.this.getHandler().mouseClicked(var1);
      }

      public void mousePressed(MouseEvent var1) {
         BasicMenuItemUI.this.getHandler().mousePressed(var1);
      }

      public void mouseReleased(MouseEvent var1) {
         BasicMenuItemUI.this.getHandler().mouseReleased(var1);
      }

      public void mouseEntered(MouseEvent var1) {
         BasicMenuItemUI.this.getHandler().mouseEntered(var1);
      }

      public void mouseExited(MouseEvent var1) {
         BasicMenuItemUI.this.getHandler().mouseExited(var1);
      }

      public void mouseDragged(MouseEvent var1) {
         BasicMenuItemUI.this.getHandler().mouseDragged(var1);
      }

      public void mouseMoved(MouseEvent var1) {
         BasicMenuItemUI.this.getHandler().mouseMoved(var1);
      }
   }
}
