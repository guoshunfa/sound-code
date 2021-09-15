package com.sun.java.swing.plaf.windows;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.ButtonModel;
import javax.swing.ComboBoxEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import sun.swing.DefaultLookup;
import sun.swing.StringUIClientPropertyKey;

public class WindowsComboBoxUI extends BasicComboBoxUI {
   private static final MouseListener rolloverListener = new MouseAdapter() {
      private void handleRollover(MouseEvent var1, boolean var2) {
         JComboBox var3 = this.getComboBox(var1);
         WindowsComboBoxUI var4 = this.getWindowsComboBoxUI(var1);
         if (var3 != null && var4 != null) {
            if (!var3.isEditable()) {
               ButtonModel var5 = null;
               if (var4.arrowButton != null) {
                  var5 = var4.arrowButton.getModel();
               }

               if (var5 != null) {
                  var5.setRollover(var2);
               }
            }

            var4.isRollover = var2;
            var3.repaint();
         }
      }

      public void mouseEntered(MouseEvent var1) {
         this.handleRollover(var1, true);
      }

      public void mouseExited(MouseEvent var1) {
         this.handleRollover(var1, false);
      }

      private JComboBox getComboBox(MouseEvent var1) {
         Object var2 = var1.getSource();
         JComboBox var3 = null;
         if (var2 instanceof JComboBox) {
            var3 = (JComboBox)var2;
         } else if (var2 instanceof WindowsComboBoxUI.XPComboBoxButton) {
            var3 = ((WindowsComboBoxUI.XPComboBoxButton)var2).getWindowsComboBoxUI().comboBox;
         }

         return var3;
      }

      private WindowsComboBoxUI getWindowsComboBoxUI(MouseEvent var1) {
         JComboBox var2 = this.getComboBox(var1);
         WindowsComboBoxUI var3 = null;
         if (var2 != null && var2.getUI() instanceof WindowsComboBoxUI) {
            var3 = (WindowsComboBoxUI)var2.getUI();
         }

         return var3;
      }
   };
   private boolean isRollover = false;
   private static final PropertyChangeListener componentOrientationListener = new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent var1) {
         String var2 = var1.getPropertyName();
         Object var3 = null;
         if ("componentOrientation" == var2 && (var3 = var1.getSource()) instanceof JComboBox && ((JComboBox)var3).getUI() instanceof WindowsComboBoxUI) {
            JComboBox var4 = (JComboBox)var3;
            WindowsComboBoxUI var5 = (WindowsComboBoxUI)var4.getUI();
            if (var5.arrowButton instanceof WindowsComboBoxUI.XPComboBoxButton) {
               ((WindowsComboBoxUI.XPComboBoxButton)var5.arrowButton).setPart(var4.getComponentOrientation() == ComponentOrientation.RIGHT_TO_LEFT ? TMSchema.Part.CP_DROPDOWNBUTTONLEFT : TMSchema.Part.CP_DROPDOWNBUTTONRIGHT);
            }
         }

      }
   };

   public static ComponentUI createUI(JComponent var0) {
      return new WindowsComboBoxUI();
   }

   public void installUI(JComponent var1) {
      super.installUI(var1);
      this.isRollover = false;
      this.comboBox.setRequestFocusEnabled(true);
      if (XPStyle.getXP() != null && this.arrowButton != null) {
         this.comboBox.addMouseListener(rolloverListener);
         this.arrowButton.addMouseListener(rolloverListener);
      }

   }

   public void uninstallUI(JComponent var1) {
      this.comboBox.removeMouseListener(rolloverListener);
      if (this.arrowButton != null) {
         this.arrowButton.removeMouseListener(rolloverListener);
      }

      super.uninstallUI(var1);
   }

   protected void installListeners() {
      super.installListeners();
      XPStyle var1 = XPStyle.getXP();
      if (var1 != null && var1.isSkinDefined(this.comboBox, TMSchema.Part.CP_DROPDOWNBUTTONRIGHT)) {
         this.comboBox.addPropertyChangeListener("componentOrientation", componentOrientationListener);
      }

   }

   protected void uninstallListeners() {
      super.uninstallListeners();
      this.comboBox.removePropertyChangeListener("componentOrientation", componentOrientationListener);
   }

   protected void configureEditor() {
      super.configureEditor();
      if (XPStyle.getXP() != null) {
         this.editor.addMouseListener(rolloverListener);
      }

   }

   protected void unconfigureEditor() {
      super.unconfigureEditor();
      this.editor.removeMouseListener(rolloverListener);
   }

   public void paint(Graphics var1, JComponent var2) {
      if (XPStyle.getXP() != null) {
         this.paintXPComboBoxBackground(var1, var2);
      }

      super.paint(var1, var2);
   }

   TMSchema.State getXPComboBoxState(JComponent var1) {
      TMSchema.State var2 = TMSchema.State.NORMAL;
      if (!var1.isEnabled()) {
         var2 = TMSchema.State.DISABLED;
      } else if (this.isPopupVisible(this.comboBox)) {
         var2 = TMSchema.State.PRESSED;
      } else if (this.isRollover) {
         var2 = TMSchema.State.HOT;
      }

      return var2;
   }

   private void paintXPComboBoxBackground(Graphics var1, JComponent var2) {
      XPStyle var3 = XPStyle.getXP();
      if (var3 != null) {
         TMSchema.State var4 = this.getXPComboBoxState(var2);
         XPStyle.Skin var5 = null;
         if (!this.comboBox.isEditable() && var3.isSkinDefined(var2, TMSchema.Part.CP_READONLY)) {
            var5 = var3.getSkin(var2, TMSchema.Part.CP_READONLY);
         }

         if (var5 == null) {
            var5 = var3.getSkin(var2, TMSchema.Part.CP_COMBOBOX);
         }

         var5.paintSkin(var1, 0, 0, var2.getWidth(), var2.getHeight(), var4);
      }
   }

   public void paintCurrentValue(Graphics var1, Rectangle var2, boolean var3) {
      XPStyle var4 = XPStyle.getXP();
      if (var4 != null) {
         var2.x += 2;
         var2.y += 2;
         var2.width -= 4;
         var2.height -= 4;
      } else {
         ++var2.x;
         ++var2.y;
         var2.width -= 2;
         var2.height -= 2;
      }

      if (!this.comboBox.isEditable() && var4 != null && var4.isSkinDefined(this.comboBox, TMSchema.Part.CP_READONLY)) {
         ListCellRenderer var5 = this.comboBox.getRenderer();
         Component var6;
         if (var3 && !this.isPopupVisible(this.comboBox)) {
            var6 = var5.getListCellRendererComponent(this.listBox, this.comboBox.getSelectedItem(), -1, true, false);
         } else {
            var6 = var5.getListCellRendererComponent(this.listBox, this.comboBox.getSelectedItem(), -1, false, false);
         }

         var6.setFont(this.comboBox.getFont());
         if (this.comboBox.isEnabled()) {
            var6.setForeground(this.comboBox.getForeground());
            var6.setBackground(this.comboBox.getBackground());
         } else {
            var6.setForeground(DefaultLookup.getColor(this.comboBox, this, "ComboBox.disabledForeground", (Color)null));
            var6.setBackground(DefaultLookup.getColor(this.comboBox, this, "ComboBox.disabledBackground", (Color)null));
         }

         boolean var7 = false;
         if (var6 instanceof JPanel) {
            var7 = true;
         }

         this.currentValuePane.paintComponent(var1, var6, this.comboBox, var2.x, var2.y, var2.width, var2.height, var7);
      } else {
         super.paintCurrentValue(var1, var2, var3);
      }

   }

   public void paintCurrentValueBackground(Graphics var1, Rectangle var2, boolean var3) {
      if (XPStyle.getXP() == null) {
         super.paintCurrentValueBackground(var1, var2, var3);
      }

   }

   public Dimension getMinimumSize(JComponent var1) {
      Dimension var2 = super.getMinimumSize(var1);
      if (XPStyle.getXP() != null) {
         var2.width += 5;
      } else {
         var2.width += 4;
      }

      var2.height += 2;
      return var2;
   }

   protected LayoutManager createLayoutManager() {
      return new BasicComboBoxUI.ComboBoxLayoutManager() {
         public void layoutContainer(Container var1) {
            super.layoutContainer(var1);
            if (XPStyle.getXP() != null && WindowsComboBoxUI.this.arrowButton != null) {
               Dimension var2 = var1.getSize();
               Insets var3 = WindowsComboBoxUI.this.getInsets();
               int var4 = WindowsComboBoxUI.this.arrowButton.getPreferredSize().width;
               WindowsComboBoxUI.this.arrowButton.setBounds(WindowsGraphicsUtils.isLeftToRight((JComboBox)var1) ? var2.width - var3.right - var4 : var3.left, var3.top, var4, var2.height - var3.top - var3.bottom);
            }

         }
      };
   }

   protected void installKeyboardActions() {
      super.installKeyboardActions();
   }

   protected ComboPopup createPopup() {
      return super.createPopup();
   }

   protected ComboBoxEditor createEditor() {
      return new WindowsComboBoxUI.WindowsComboBoxEditor();
   }

   protected ListCellRenderer createRenderer() {
      XPStyle var1 = XPStyle.getXP();
      return (ListCellRenderer)(var1 != null && var1.isSkinDefined(this.comboBox, TMSchema.Part.CP_READONLY) ? new WindowsComboBoxUI.WindowsComboBoxRenderer() : super.createRenderer());
   }

   protected JButton createArrowButton() {
      XPStyle var1 = XPStyle.getXP();
      return (JButton)(var1 != null ? new WindowsComboBoxUI.XPComboBoxButton(var1) : super.createArrowButton());
   }

   private static class WindowsComboBoxRenderer extends BasicComboBoxRenderer.UIResource {
      private static final Object BORDER_KEY = new StringUIClientPropertyKey("BORDER_KEY");
      private static final Border NULL_BORDER = new EmptyBorder(0, 0, 0, 0);

      private WindowsComboBoxRenderer() {
      }

      public Component getListCellRendererComponent(JList var1, Object var2, int var3, boolean var4, boolean var5) {
         Component var6 = super.getListCellRendererComponent(var1, var2, var3, var4, var5);
         if (var6 instanceof JComponent) {
            JComponent var7 = (JComponent)var6;
            if (var3 == -1 && var4) {
               Border var10 = var7.getBorder();
               WindowsBorders.DashedBorder var9 = new WindowsBorders.DashedBorder(var1.getForeground());
               var7.setBorder(var9);
               if (var7.getClientProperty(BORDER_KEY) == null) {
                  var7.putClientProperty(BORDER_KEY, var10 == null ? NULL_BORDER : var10);
               }
            } else if (var7.getBorder() instanceof WindowsBorders.DashedBorder) {
               Object var8 = var7.getClientProperty(BORDER_KEY);
               if (var8 instanceof Border) {
                  var7.setBorder(var8 == NULL_BORDER ? null : (Border)var8);
               }

               var7.putClientProperty(BORDER_KEY, (Object)null);
            }

            if (var3 == -1) {
               var7.setOpaque(false);
               var7.setForeground(var1.getForeground());
            } else {
               var7.setOpaque(true);
            }
         }

         return var6;
      }

      // $FF: synthetic method
      WindowsComboBoxRenderer(Object var1) {
         this();
      }
   }

   public static class WindowsComboBoxEditor extends BasicComboBoxEditor.UIResource {
      protected JTextField createEditorComponent() {
         JTextField var1 = super.createEditorComponent();
         Border var2 = (Border)UIManager.get("ComboBox.editorBorder");
         if (var2 != null) {
            var1.setBorder(var2);
         }

         var1.setOpaque(false);
         return var1;
      }

      public void setItem(Object var1) {
         super.setItem(var1);
         Component var2 = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
         if (var2 == this.editor || var2 == this.editor.getParent()) {
            this.editor.selectAll();
         }

      }
   }

   /** @deprecated */
   @Deprecated
   protected class WindowsComboPopup extends BasicComboPopup {
      public WindowsComboPopup(JComboBox var2) {
         super(var2);
      }

      protected KeyListener createKeyListener() {
         return new WindowsComboBoxUI.WindowsComboPopup.InvocationKeyHandler();
      }

      protected class InvocationKeyHandler extends BasicComboPopup.InvocationKeyHandler {
         protected InvocationKeyHandler() {
            WindowsComboPopup.this.getClass();
            super();
         }
      }
   }

   private class XPComboBoxButton extends XPStyle.GlyphButton {
      public XPComboBoxButton(XPStyle var2) {
         super((Component)null, !var2.isSkinDefined(WindowsComboBoxUI.this.comboBox, TMSchema.Part.CP_DROPDOWNBUTTONRIGHT) ? TMSchema.Part.CP_DROPDOWNBUTTON : (WindowsComboBoxUI.this.comboBox.getComponentOrientation() == ComponentOrientation.RIGHT_TO_LEFT ? TMSchema.Part.CP_DROPDOWNBUTTONLEFT : TMSchema.Part.CP_DROPDOWNBUTTONRIGHT));
         this.setRequestFocusEnabled(false);
      }

      protected TMSchema.State getState() {
         TMSchema.State var1 = super.getState();
         XPStyle var2 = XPStyle.getXP();
         if (var1 != TMSchema.State.DISABLED && WindowsComboBoxUI.this.comboBox != null && !WindowsComboBoxUI.this.comboBox.isEditable() && var2 != null && var2.isSkinDefined(WindowsComboBoxUI.this.comboBox, TMSchema.Part.CP_DROPDOWNBUTTONRIGHT)) {
            var1 = TMSchema.State.NORMAL;
         }

         return var1;
      }

      public Dimension getPreferredSize() {
         return new Dimension(17, 21);
      }

      void setPart(TMSchema.Part var1) {
         this.setPart(WindowsComboBoxUI.this.comboBox, var1);
      }

      WindowsComboBoxUI getWindowsComboBoxUI() {
         return WindowsComboBoxUI.this;
      }
   }
}
