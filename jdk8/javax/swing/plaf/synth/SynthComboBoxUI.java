package javax.swing.plaf.synth;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.ComboBoxEditor;
import javax.swing.DefaultButtonModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.ComboPopup;

public class SynthComboBoxUI extends BasicComboBoxUI implements PropertyChangeListener, SynthUI {
   private SynthStyle style;
   private boolean useListColors;
   Insets popupInsets;
   private boolean buttonWhenNotEditable;
   private boolean pressedWhenPopupVisible;
   private SynthComboBoxUI.ButtonHandler buttonHandler;
   private SynthComboBoxUI.EditorFocusHandler editorFocusHandler;
   private boolean forceOpaque = false;

   public static ComponentUI createUI(JComponent var0) {
      return new SynthComboBoxUI();
   }

   public void installUI(JComponent var1) {
      this.buttonHandler = new SynthComboBoxUI.ButtonHandler();
      super.installUI(var1);
   }

   protected void installDefaults() {
      this.updateStyle(this.comboBox);
   }

   private void updateStyle(JComboBox var1) {
      SynthStyle var2 = this.style;
      SynthContext var3 = this.getContext(var1, 1);
      this.style = SynthLookAndFeel.updateStyle(var3, this);
      if (this.style != var2) {
         this.padding = (Insets)this.style.get(var3, "ComboBox.padding");
         this.popupInsets = (Insets)this.style.get(var3, "ComboBox.popupInsets");
         this.useListColors = this.style.getBoolean(var3, "ComboBox.rendererUseListColors", true);
         this.buttonWhenNotEditable = this.style.getBoolean(var3, "ComboBox.buttonWhenNotEditable", false);
         this.pressedWhenPopupVisible = this.style.getBoolean(var3, "ComboBox.pressedWhenPopupVisible", false);
         this.squareButton = this.style.getBoolean(var3, "ComboBox.squareButton", true);
         if (var2 != null) {
            this.uninstallKeyboardActions();
            this.installKeyboardActions();
         }

         this.forceOpaque = this.style.getBoolean(var3, "ComboBox.forceOpaque", false);
      }

      var3.dispose();
      if (this.listBox != null) {
         SynthLookAndFeel.updateStyles(this.listBox);
      }

   }

   protected void installListeners() {
      this.comboBox.addPropertyChangeListener(this);
      this.comboBox.addMouseListener(this.buttonHandler);
      this.editorFocusHandler = new SynthComboBoxUI.EditorFocusHandler(this.comboBox);
      super.installListeners();
   }

   public void uninstallUI(JComponent var1) {
      if (this.popup instanceof SynthComboPopup) {
         ((SynthComboPopup)this.popup).removePopupMenuListener(this.buttonHandler);
      }

      super.uninstallUI(var1);
      this.buttonHandler = null;
   }

   protected void uninstallDefaults() {
      SynthContext var1 = this.getContext(this.comboBox, 1);
      this.style.uninstallDefaults(var1);
      var1.dispose();
      this.style = null;
   }

   protected void uninstallListeners() {
      this.editorFocusHandler.unregister();
      this.comboBox.removePropertyChangeListener(this);
      this.comboBox.removeMouseListener(this.buttonHandler);
      this.buttonHandler.pressed = false;
      this.buttonHandler.over = false;
      super.uninstallListeners();
   }

   public SynthContext getContext(JComponent var1) {
      return this.getContext(var1, this.getComponentState(var1));
   }

   private SynthContext getContext(JComponent var1, int var2) {
      return SynthContext.getContext(var1, this.style, var2);
   }

   private int getComponentState(JComponent var1) {
      if (!(var1 instanceof JComboBox)) {
         return SynthLookAndFeel.getComponentState(var1);
      } else {
         JComboBox var2 = (JComboBox)var1;
         int var3;
         if (this.shouldActLikeButton()) {
            var3 = 1;
            if (!var1.isEnabled()) {
               var3 = 8;
            }

            if (this.buttonHandler.isPressed()) {
               var3 |= 4;
            }

            if (this.buttonHandler.isRollover()) {
               var3 |= 2;
            }

            if (var2.isFocusOwner()) {
               var3 |= 256;
            }

            return var3;
         } else {
            var3 = SynthLookAndFeel.getComponentState(var1);
            if (var2.isEditable() && var2.getEditor().getEditorComponent().isFocusOwner()) {
               var3 |= 256;
            }

            return var3;
         }
      }
   }

   protected ComboPopup createPopup() {
      SynthComboPopup var1 = new SynthComboPopup(this.comboBox);
      var1.addPopupMenuListener(this.buttonHandler);
      return var1;
   }

   protected ListCellRenderer createRenderer() {
      return new SynthComboBoxUI.SynthComboBoxRenderer();
   }

   protected ComboBoxEditor createEditor() {
      return new SynthComboBoxUI.SynthComboBoxEditor();
   }

   public void propertyChange(PropertyChangeEvent var1) {
      if (SynthLookAndFeel.shouldUpdateStyle(var1)) {
         this.updateStyle(this.comboBox);
      }

   }

   protected JButton createArrowButton() {
      SynthArrowButton var1 = new SynthArrowButton(5);
      var1.setName("ComboBox.arrowButton");
      var1.setModel(this.buttonHandler);
      return var1;
   }

   public void update(Graphics var1, JComponent var2) {
      SynthContext var3 = this.getContext(var2);
      SynthLookAndFeel.update(var3, var1);
      var3.getPainter().paintComboBoxBackground(var3, var1, 0, 0, var2.getWidth(), var2.getHeight());
      this.paint(var3, var1);
      var3.dispose();
   }

   public void paint(Graphics var1, JComponent var2) {
      SynthContext var3 = this.getContext(var2);
      this.paint(var3, var1);
      var3.dispose();
   }

   protected void paint(SynthContext var1, Graphics var2) {
      this.hasFocus = this.comboBox.hasFocus();
      if (!this.comboBox.isEditable()) {
         Rectangle var3 = this.rectangleForCurrentValue();
         this.paintCurrentValue(var2, var3, this.hasFocus);
      }

   }

   public void paintBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      var1.getPainter().paintComboBoxBorder(var1, var2, var3, var4, var5, var6);
   }

   public void paintCurrentValue(Graphics var1, Rectangle var2, boolean var3) {
      ListCellRenderer var4 = this.comboBox.getRenderer();
      Component var5 = var4.getListCellRendererComponent(this.listBox, this.comboBox.getSelectedItem(), -1, false, false);
      boolean var6 = false;
      if (var5 instanceof JPanel) {
         var6 = true;
      }

      if (var5 instanceof UIResource) {
         var5.setName("ComboBox.renderer");
      }

      boolean var7 = this.forceOpaque && var5 instanceof JComponent;
      if (var7) {
         ((JComponent)var5).setOpaque(false);
      }

      int var8 = var2.x;
      int var9 = var2.y;
      int var10 = var2.width;
      int var11 = var2.height;
      if (this.padding != null) {
         var8 = var2.x + this.padding.left;
         var9 = var2.y + this.padding.top;
         var10 = var2.width - (this.padding.left + this.padding.right);
         var11 = var2.height - (this.padding.top + this.padding.bottom);
      }

      this.currentValuePane.paintComponent(var1, var5, this.comboBox, var8, var9, var10, var11, var6);
      if (var7) {
         ((JComponent)var5).setOpaque(true);
      }

   }

   private boolean shouldActLikeButton() {
      return this.buttonWhenNotEditable && !this.comboBox.isEditable();
   }

   protected Dimension getDefaultSize() {
      SynthComboBoxUI.SynthComboBoxRenderer var1 = new SynthComboBoxUI.SynthComboBoxRenderer();
      Dimension var2 = this.getSizeForComponent(var1.getListCellRendererComponent(this.listBox, " ", -1, false, false));
      return new Dimension(var2.width, var2.height);
   }

   private static class EditorFocusHandler implements FocusListener, PropertyChangeListener {
      private JComboBox comboBox;
      private ComboBoxEditor editor;
      private Component editorComponent;

      private EditorFocusHandler(JComboBox var1) {
         this.editor = null;
         this.editorComponent = null;
         this.comboBox = var1;
         this.editor = var1.getEditor();
         if (this.editor != null) {
            this.editorComponent = this.editor.getEditorComponent();
            if (this.editorComponent != null) {
               this.editorComponent.addFocusListener(this);
            }
         }

         var1.addPropertyChangeListener("editor", this);
      }

      public void unregister() {
         this.comboBox.removePropertyChangeListener(this);
         if (this.editorComponent != null) {
            this.editorComponent.removeFocusListener(this);
         }

      }

      public void focusGained(FocusEvent var1) {
         this.comboBox.repaint();
      }

      public void focusLost(FocusEvent var1) {
         this.comboBox.repaint();
      }

      public void propertyChange(PropertyChangeEvent var1) {
         ComboBoxEditor var2 = this.comboBox.getEditor();
         if (this.editor != var2) {
            if (this.editorComponent != null) {
               this.editorComponent.removeFocusListener(this);
            }

            this.editor = var2;
            if (this.editor != null) {
               this.editorComponent = this.editor.getEditorComponent();
               if (this.editorComponent != null) {
                  this.editorComponent.addFocusListener(this);
               }
            }
         }

      }

      // $FF: synthetic method
      EditorFocusHandler(JComboBox var1, Object var2) {
         this(var1);
      }
   }

   private final class ButtonHandler extends DefaultButtonModel implements MouseListener, PopupMenuListener {
      private boolean over;
      private boolean pressed;

      private ButtonHandler() {
      }

      private void updatePressed(boolean var1) {
         this.pressed = var1 && this.isEnabled();
         if (SynthComboBoxUI.this.shouldActLikeButton()) {
            SynthComboBoxUI.this.comboBox.repaint();
         }

      }

      private void updateOver(boolean var1) {
         boolean var2 = this.isRollover();
         this.over = var1 && this.isEnabled();
         boolean var3 = this.isRollover();
         if (SynthComboBoxUI.this.shouldActLikeButton() && var2 != var3) {
            SynthComboBoxUI.this.comboBox.repaint();
         }

      }

      public boolean isPressed() {
         boolean var1 = SynthComboBoxUI.this.shouldActLikeButton() ? this.pressed : super.isPressed();
         return var1 || SynthComboBoxUI.this.pressedWhenPopupVisible && SynthComboBoxUI.this.comboBox.isPopupVisible();
      }

      public boolean isArmed() {
         boolean var1 = SynthComboBoxUI.this.shouldActLikeButton() || SynthComboBoxUI.this.pressedWhenPopupVisible && SynthComboBoxUI.this.comboBox.isPopupVisible();
         return var1 ? this.isPressed() : super.isArmed();
      }

      public boolean isRollover() {
         return SynthComboBoxUI.this.shouldActLikeButton() ? this.over : super.isRollover();
      }

      public void setPressed(boolean var1) {
         super.setPressed(var1);
         this.updatePressed(var1);
      }

      public void setRollover(boolean var1) {
         super.setRollover(var1);
         this.updateOver(var1);
      }

      public void mouseEntered(MouseEvent var1) {
         this.updateOver(true);
      }

      public void mouseExited(MouseEvent var1) {
         this.updateOver(false);
      }

      public void mousePressed(MouseEvent var1) {
         this.updatePressed(true);
      }

      public void mouseReleased(MouseEvent var1) {
         this.updatePressed(false);
      }

      public void mouseClicked(MouseEvent var1) {
      }

      public void popupMenuCanceled(PopupMenuEvent var1) {
         if (SynthComboBoxUI.this.shouldActLikeButton() || SynthComboBoxUI.this.pressedWhenPopupVisible) {
            SynthComboBoxUI.this.comboBox.repaint();
         }

      }

      public void popupMenuWillBecomeVisible(PopupMenuEvent var1) {
      }

      public void popupMenuWillBecomeInvisible(PopupMenuEvent var1) {
      }

      // $FF: synthetic method
      ButtonHandler(Object var2) {
         this();
      }
   }

   private static class SynthComboBoxEditor extends BasicComboBoxEditor.UIResource {
      private SynthComboBoxEditor() {
      }

      public JTextField createEditorComponent() {
         JTextField var1 = new JTextField("", 9);
         var1.setName("ComboBox.textField");
         return var1;
      }

      // $FF: synthetic method
      SynthComboBoxEditor(Object var1) {
         this();
      }
   }

   private class SynthComboBoxRenderer extends JLabel implements ListCellRenderer<Object>, UIResource {
      public SynthComboBoxRenderer() {
         this.setText(" ");
      }

      public String getName() {
         String var1 = super.getName();
         return var1 == null ? "ComboBox.renderer" : var1;
      }

      public Component getListCellRendererComponent(JList<?> var1, Object var2, int var3, boolean var4, boolean var5) {
         this.setName("ComboBox.listRenderer");
         SynthLookAndFeel.resetSelectedUI();
         if (var4) {
            this.setBackground(var1.getSelectionBackground());
            this.setForeground(var1.getSelectionForeground());
            if (!SynthComboBoxUI.this.useListColors) {
               SynthLookAndFeel.setSelectedUI((SynthLabelUI)SynthLookAndFeel.getUIOfType(this.getUI(), SynthLabelUI.class), var4, var5, var1.isEnabled(), false);
            }
         } else {
            this.setBackground(var1.getBackground());
            this.setForeground(var1.getForeground());
         }

         this.setFont(var1.getFont());
         if (var2 instanceof Icon) {
            this.setIcon((Icon)var2);
            this.setText("");
         } else {
            String var6 = var2 == null ? " " : var2.toString();
            if ("".equals(var6)) {
               var6 = " ";
            }

            this.setText(var6);
         }

         if (SynthComboBoxUI.this.comboBox != null) {
            this.setEnabled(SynthComboBoxUI.this.comboBox.isEnabled());
            this.setComponentOrientation(SynthComboBoxUI.this.comboBox.getComponentOrientation());
         }

         return this;
      }

      public void paint(Graphics var1) {
         super.paint(var1);
         SynthLookAndFeel.resetSelectedUI();
      }
   }
}
