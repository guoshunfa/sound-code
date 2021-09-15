package com.apple.laf;

import apple.laf.JRSUIConstants;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.beans.PropertyChangeEvent;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JRadioButton;
import javax.swing.JRootPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.RootPaneUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicButtonListener;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.text.View;
import sun.swing.SwingUtilities2;

public class AquaButtonUI extends BasicButtonUI implements AquaUtilControlSize.Sizeable {
   private static final String BUTTON_TYPE = "JButton.buttonType";
   private static final String SEGMENTED_BUTTON_POSITION = "JButton.segmentPosition";
   protected static final AquaUtils.RecyclableSingleton<AquaButtonUI> buttonUI = new AquaUtils.RecyclableSingletonFromDefaultConstructor(AquaButtonUI.class);
   private boolean defaults_initialized = false;
   private Color defaultDisabledTextColor = null;
   static final AquaUtils.RecyclableSingleton<AquaButtonUI.AquaHierarchyButtonListener> fHierListener = new AquaUtils.RecyclableSingletonFromDefaultConstructor(AquaButtonUI.AquaHierarchyButtonListener.class);

   public static ComponentUI createUI(JComponent var0) {
      return (ComponentUI)buttonUI.get();
   }

   protected void installDefaults(AbstractButton var1) {
      String var2 = this.getPropertyPrefix();
      if (!this.defaults_initialized) {
         this.defaultDisabledTextColor = UIManager.getColor(var2 + "disabledText");
         this.defaults_initialized = true;
      }

      this.setButtonMarginIfNeeded(var1, UIManager.getInsets(var2 + "margin"));
      LookAndFeel.installColorsAndFont(var1, var2 + "background", var2 + "foreground", var2 + "font");
      LookAndFeel.installProperty(var1, "opaque", UIManager.getBoolean(var2 + "opaque"));
      Object var3 = var1.getClientProperty("JButton.buttonType");
      boolean var4 = false;
      if (var3 != null) {
         var4 = this.setButtonType(var1, var3);
      }

      if (!var4) {
         this.setThemeBorder(var1);
      }

      Object var5 = var1.getClientProperty("JButton.segmentPosition");
      if (var5 != null) {
         Border var6 = var1.getBorder();
         if (!(var6 instanceof AquaBorder)) {
            return;
         }

         var1.setBorder(AquaButtonExtendedTypes.getBorderForPosition(var1, var1.getClientProperty("JButton.buttonType"), var5));
      }

   }

   public void applySizeFor(JComponent var1, JRSUIConstants.Size var2) {
   }

   protected void setThemeBorder(AbstractButton var1) {
      ButtonUI var2 = var1.getUI();
      if (var2 instanceof AquaButtonUI) {
         AquaButtonUI var3 = (AquaButtonUI)var2;
         Border var4 = var1.getBorder();
         if (!var3.isBorderFromProperty(var1) && (var4 == null || var4 instanceof UIResource || var4 instanceof AquaButtonBorder)) {
            boolean var5 = true;
            Object var7;
            if (isOnToolbar(var1)) {
               if (var1 instanceof JToggleButton) {
                  var7 = AquaButtonBorder.getToolBarButtonBorder();
               } else {
                  var7 = AquaButtonBorder.getBevelButtonBorder();
               }
            } else if (var1.getIcon() == null && var1.getComponentCount() <= 0) {
               var7 = UIManager.getBorder(this.getPropertyPrefix() + "border");
               var5 = false;
            } else {
               var7 = AquaButtonBorder.getToggleButtonBorder();
            }

            var1.setBorder((Border)var7);
            Font var6 = var1.getFont();
            if (var5 && (var6 == null || var6 instanceof UIResource)) {
               var1.setFont(UIManager.getFont("IconButton.font"));
            }
         }

      }
   }

   protected static boolean isOnToolbar(AbstractButton var0) {
      for(Container var1 = var0.getParent(); var1 != null; var1 = var1.getParent()) {
         if (var1 instanceof JToolBar) {
            return true;
         }
      }

      return false;
   }

   protected static void updateBorder(AbstractButton var0) {
      Object var1 = var0.getClientProperty("JButton.buttonType");
      if (var1 == null) {
         ButtonUI var2 = var0.getUI();
         if (var2 instanceof AquaButtonUI) {
            if (var0.getBorder() != null) {
               ((AquaButtonUI)var2).setThemeBorder(var0);
            }

         }
      }
   }

   protected void setButtonMarginIfNeeded(AbstractButton var1, Insets var2) {
      Insets var3 = var1.getMargin();
      if (var3 == null || var3 instanceof UIResource) {
         var1.setMargin(var2);
      }

   }

   public boolean isBorderFromProperty(AbstractButton var1) {
      return var1.getClientProperty("JButton.buttonType") != null;
   }

   protected boolean setButtonType(AbstractButton var1, Object var2) {
      if (!(var2 instanceof String)) {
         var1.putClientProperty("JButton.buttonType", (Object)null);
         return false;
      } else {
         String var3 = (String)var2;
         boolean var4 = true;
         AquaButtonExtendedTypes.TypeSpecifier var5 = AquaButtonExtendedTypes.getSpecifierByName(var3);
         if (var5 != null) {
            var1.setBorder(var5.getBorder());
            var4 = var5.setIconFont;
         }

         Font var6 = var1.getFont();
         if (var6 == null || var6 instanceof UIResource) {
            var1.setFont(UIManager.getFont(var4 ? "IconButton.font" : "Button.font"));
         }

         return true;
      }
   }

   protected void installListeners(AbstractButton var1) {
      AquaButtonUI.AquaButtonListener var2 = this.createButtonListener(var1);
      if (var2 != null) {
         var1.putClientProperty(this, var2);
         var1.addMouseListener(var2);
         var1.addMouseMotionListener(var2);
         var1.addFocusListener(var2);
         var1.addPropertyChangeListener(var2);
         var1.addChangeListener(var2);
         var1.addAncestorListener(var2);
      }

      this.installHierListener(var1);
      AquaUtilControlSize.addSizePropertyListener(var1);
   }

   protected void installKeyboardActions(AbstractButton var1) {
      BasicButtonListener var2 = (BasicButtonListener)var1.getClientProperty(this);
      if (var2 != null) {
         var2.installKeyboardActions(var1);
      }

   }

   public void uninstallUI(JComponent var1) {
      this.uninstallKeyboardActions((AbstractButton)var1);
      this.uninstallListeners((AbstractButton)var1);
      this.uninstallDefaults((AbstractButton)var1);
   }

   protected void uninstallKeyboardActions(AbstractButton var1) {
      BasicButtonListener var2 = (BasicButtonListener)var1.getClientProperty(this);
      if (var2 != null) {
         var2.uninstallKeyboardActions(var1);
      }

   }

   protected void uninstallListeners(AbstractButton var1) {
      AquaButtonUI.AquaButtonListener var2 = (AquaButtonUI.AquaButtonListener)var1.getClientProperty(this);
      var1.putClientProperty(this, (Object)null);
      if (var2 != null) {
         var1.removeMouseListener(var2);
         var1.removeMouseListener(var2);
         var1.removeMouseMotionListener(var2);
         var1.removeFocusListener(var2);
         var1.removeChangeListener(var2);
         var1.removePropertyChangeListener(var2);
         var1.removeAncestorListener(var2);
      }

      this.uninstallHierListener(var1);
      AquaUtilControlSize.addSizePropertyListener(var1);
   }

   protected void uninstallDefaults(AbstractButton var1) {
      LookAndFeel.uninstallBorder(var1);
      this.defaults_initialized = false;
   }

   protected AquaButtonUI.AquaButtonListener createButtonListener(AbstractButton var1) {
      return new AquaButtonUI.AquaButtonListener(var1);
   }

   public void paint(Graphics var1, JComponent var2) {
      AbstractButton var3 = (AbstractButton)var2;
      ButtonModel var4 = var3.getModel();
      Insets var5 = var2.getInsets();
      Rectangle var6 = new Rectangle(var3.getWidth(), var3.getHeight());
      Rectangle var7 = new Rectangle();
      Rectangle var8 = new Rectangle();
      if (var3.isOpaque()) {
         var1.setColor(var2.getBackground());
         var1.fillRect(var6.x, var6.y, var6.width, var6.height);
      }

      AquaButtonBorder var9 = null;
      if (((AbstractButton)var2).isBorderPainted()) {
         Border var10 = var2.getBorder();
         if (var10 instanceof AquaButtonBorder) {
            var9 = (AquaButtonBorder)var10;
            var9.paintButton(var2, var1, var6.x, var6.y, var6.width, var6.height);
         }
      } else {
         if (var3.isOpaque()) {
            var6.x = var5.left - 2;
            var6.y = var5.top - 2;
            var6.width = var3.getWidth() - (var5.right + var6.x) + 4;
            var6.height = var3.getHeight() - (var5.bottom + var6.y) + 4;
            if (var3.isContentAreaFilled() || var4.isSelected()) {
               if (var4.isSelected()) {
                  var1.setColor(var2.getBackground().darker());
               } else {
                  var1.setColor(var2.getBackground());
               }

               var1.fillRect(var6.x, var6.y, var6.width, var6.height);
            }
         }

         if (var3.isFocusPainted() && var3.hasFocus()) {
            this.paintFocus(var1, var3, var6, var8, var7);
         }
      }

      String var12 = this.layoutAndGetText(var1, var3, var9, var5, var6, var7, var8);
      if (var3.getIcon() != null) {
         this.paintIcon(var1, var3, var7);
      }

      if (var8.width == 0) {
         var8.width = 50;
      }

      if (var12 != null && !var12.equals("")) {
         View var11 = (View)var2.getClientProperty("html");
         if (var11 != null) {
            var11.paint(var1, var8);
         } else {
            this.paintText(var1, var3, var8, var12);
         }
      }

   }

   protected String layoutAndGetText(Graphics var1, AbstractButton var2, AquaButtonBorder var3, Insets var4, Rectangle var5, Rectangle var6, Rectangle var7) {
      var5.x = var4.left;
      var5.y = var4.top;
      var5.width = var2.getWidth() - (var4.right + var5.x);
      var5.height = var2.getHeight() - (var4.bottom + var5.y);
      var7.x = var7.y = var7.width = var7.height = 0;
      var6.x = var6.y = var6.width = var6.height = 0;
      var1.setFont(var2.getFont());
      FontMetrics var8 = var1.getFontMetrics();
      String var9 = var2.getText();
      String var10 = SwingUtilities.layoutCompoundLabel(var2, var8, var9, var2.getIcon(), var2.getVerticalAlignment(), var2.getHorizontalAlignment(), var2.getVerticalTextPosition(), var2.getHorizontalTextPosition(), var5, var6, var7, var9 == null ? 0 : var2.getIconTextGap());
      if (var10 != var9 && var3 != null) {
         Insets var11 = var3.getContentInsets(var2, var2.getWidth(), var2.getHeight());
         return var11 != null ? this.layoutAndGetText(var1, var2, (AquaButtonBorder)null, var11, var5, var6, var7) : var10;
      } else {
         return var10;
      }
   }

   protected void paintIcon(Graphics var1, AbstractButton var2, Rectangle var3) {
      ButtonModel var4 = var2.getModel();
      Object var5 = var2.getIcon();
      Object var6 = null;
      if (var5 != null) {
         if (!var4.isEnabled()) {
            if (var4.isSelected()) {
               var6 = var2.getDisabledSelectedIcon();
            } else {
               var6 = var2.getDisabledIcon();
            }
         } else if (var4.isPressed() && var4.isArmed()) {
            var6 = var2.getPressedIcon();
            if (var6 == null && var5 instanceof ImageIcon) {
               var6 = new ImageIcon(AquaUtils.generateSelectedDarkImage(((ImageIcon)var5).getImage()));
            }
         } else if (var2.isRolloverEnabled() && var4.isRollover()) {
            if (var4.isSelected()) {
               var6 = var2.getRolloverSelectedIcon();
            } else {
               var6 = var2.getRolloverIcon();
            }
         } else if (var4.isSelected()) {
            var6 = var2.getSelectedIcon();
         }

         if (var4.isEnabled() && var2.isFocusOwner() && var2.getBorder() instanceof AquaButtonBorder.Toolbar) {
            if (var6 == null) {
               var6 = var5;
            }

            if (var6 instanceof ImageIcon) {
               Icon var7 = AquaFocus.createFocusedIcon((Icon)var6, var2, 3);
               var7.paintIcon(var2, var1, var3.x - 3, var3.y - 3);
               return;
            }
         }

         if (var6 != null) {
            var5 = var6;
         }

         ((Icon)var5).paintIcon(var2, var1, var3.x, var3.y);
      }
   }

   protected void paintText(Graphics var1, JComponent var2, Rectangle var3, String var4) {
      Graphics2D var10000;
      if (var1 instanceof Graphics2D) {
         var10000 = (Graphics2D)var1;
      } else {
         var10000 = null;
      }

      AbstractButton var6 = (AbstractButton)var2;
      ButtonModel var7 = var6.getModel();
      FontMetrics var8 = var1.getFontMetrics();
      int var9 = AquaMnemonicHandler.isMnemonicHidden() ? -1 : var6.getDisplayedMnemonicIndex();
      if (var7.isEnabled()) {
         var1.setColor(var6.getForeground());
      } else {
         var1.setColor(this.defaultDisabledTextColor);
      }

      SwingUtilities2.drawStringUnderlineCharAt(var2, var1, var4, var9, var3.x, var3.y + var8.getAscent());
   }

   protected void paintText(Graphics var1, AbstractButton var2, Rectangle var3, String var4) {
      this.paintText(var1, (JComponent)var2, var3, var4);
   }

   protected void paintButtonPressed(Graphics var1, AbstractButton var2) {
      this.paint(var1, var2);
   }

   public Dimension getMinimumSize(JComponent var1) {
      Dimension var2 = this.getPreferredSize(var1);
      View var3 = (View)var1.getClientProperty("html");
      if (var3 != null) {
         var2.width = (int)((float)var2.width - (var3.getPreferredSpan(0) - var3.getMinimumSpan(0)));
      }

      return var2;
   }

   public Dimension getPreferredSize(JComponent var1) {
      AbstractButton var2 = (AbstractButton)var1;
      Dimension var3 = BasicGraphicsUtils.getPreferredButtonSize(var2, var2.getIconTextGap());
      if (var3 == null) {
         return null;
      } else {
         Border var4 = var2.getBorder();
         if (var4 instanceof AquaButtonBorder) {
            ((AquaButtonBorder)var4).alterPreferredSize(var3);
         }

         return var3;
      }
   }

   public Dimension getMaximumSize(JComponent var1) {
      Dimension var2 = this.getPreferredSize(var1);
      View var3 = (View)var1.getClientProperty("html");
      if (var3 != null) {
         var2.width = (int)((float)var2.width + (var3.getMaximumSpan(0) - var3.getPreferredSpan(0)));
      }

      return var2;
   }

   static AquaButtonUI.AquaHierarchyButtonListener getAquaHierarchyButtonListener() {
      return (AquaButtonUI.AquaHierarchyButtonListener)fHierListener.get();
   }

   private boolean shouldInstallHierListener(AbstractButton var1) {
      return var1 instanceof JButton || var1 instanceof JToggleButton && !(var1 instanceof AquaComboBoxButton) && !(var1 instanceof JCheckBox) && !(var1 instanceof JRadioButton);
   }

   protected void installHierListener(AbstractButton var1) {
      if (this.shouldInstallHierListener(var1)) {
         var1.addHierarchyListener(getAquaHierarchyButtonListener());
      }

   }

   protected void uninstallHierListener(AbstractButton var1) {
      if (this.shouldInstallHierListener(var1)) {
         var1.removeHierarchyListener(getAquaHierarchyButtonListener());
      }

   }

   class AquaButtonListener extends BasicButtonListener implements AncestorListener {
      protected final AbstractButton b;

      public AquaButtonListener(AbstractButton var2) {
         super(var2);
         this.b = var2;
      }

      public void focusGained(FocusEvent var1) {
         ((Component)var1.getSource()).repaint();
      }

      public void focusLost(FocusEvent var1) {
         ((Component)var1.getSource()).repaint();
      }

      public void propertyChange(PropertyChangeEvent var1) {
         super.propertyChange(var1);
         String var2 = var1.getPropertyName();
         if ("Frame.active".equals(var2)) {
            this.b.repaint();
         } else if (!"icon".equals(var2) && !"text".equals(var2)) {
            if ("JButton.buttonType".equals(var2)) {
               String var6 = (String)var1.getNewValue();
               Border var7 = AquaButtonExtendedTypes.getBorderForPosition(this.b, var6, this.b.getClientProperty("JButton.segmentPosition"));
               if (var7 != null) {
                  this.b.setBorder(var7);
               }

            } else {
               Border var3;
               if ("JButton.segmentPosition".equals(var2)) {
                  var3 = this.b.getBorder();
                  if (!(var3 instanceof AquaBorder)) {
                     return;
                  }

                  this.b.setBorder(AquaButtonExtendedTypes.getBorderForPosition(this.b, this.b.getClientProperty("JButton.buttonType"), var1.getNewValue()));
               }

               if ("componentOrientation".equals(var2)) {
                  var3 = this.b.getBorder();
                  if (!(var3 instanceof AquaBorder)) {
                     return;
                  }

                  Object var4 = this.b.getClientProperty("JButton.buttonType");
                  Object var5 = this.b.getClientProperty("JButton.segmentPosition");
                  if (var4 != null && var5 != null) {
                     this.b.setBorder(AquaButtonExtendedTypes.getBorderForPosition(this.b, var4, var5));
                  }
               }

            }
         } else {
            AquaButtonUI.this.setThemeBorder(this.b);
         }
      }

      public void ancestorMoved(AncestorEvent var1) {
      }

      public void ancestorAdded(AncestorEvent var1) {
         this.updateDefaultButton();
      }

      public void ancestorRemoved(AncestorEvent var1) {
         this.updateDefaultButton();
      }

      protected void updateDefaultButton() {
         if (this.b instanceof JButton) {
            if (((JButton)this.b).isDefaultButton()) {
               JRootPane var1 = this.b.getRootPane();
               if (var1 != null) {
                  RootPaneUI var2 = var1.getUI();
                  if (var2 instanceof AquaRootPaneUI) {
                     ((AquaRootPaneUI)var2).updateDefaultButton(var1);
                  }
               }
            }
         }
      }
   }

   static class AquaHierarchyButtonListener implements HierarchyListener {
      public void hierarchyChanged(HierarchyEvent var1) {
         if ((var1.getChangeFlags() & 1L) != 0L) {
            Object var2 = var1.getSource();
            if (var2 instanceof AbstractButton) {
               AbstractButton var3 = (AbstractButton)var2;
               ButtonUI var4 = var3.getUI();
               if (var4 instanceof AquaButtonUI) {
                  if (var3.getBorder() instanceof UIResource) {
                     ((AquaButtonUI)var4).setThemeBorder(var3);
                  }
               }
            }
         }
      }
   }
}
