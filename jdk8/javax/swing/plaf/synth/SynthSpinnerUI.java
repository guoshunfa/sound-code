package javax.swing.plaf.synth;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.SpinnerUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicSpinnerUI;

public class SynthSpinnerUI extends BasicSpinnerUI implements PropertyChangeListener, SynthUI {
   private SynthStyle style;
   private SynthSpinnerUI.EditorFocusHandler editorFocusHandler = new SynthSpinnerUI.EditorFocusHandler();

   public static ComponentUI createUI(JComponent var0) {
      return new SynthSpinnerUI();
   }

   protected void installListeners() {
      super.installListeners();
      this.spinner.addPropertyChangeListener(this);
      JComponent var1 = this.spinner.getEditor();
      if (var1 instanceof JSpinner.DefaultEditor) {
         JFormattedTextField var2 = ((JSpinner.DefaultEditor)var1).getTextField();
         if (var2 != null) {
            var2.addFocusListener(this.editorFocusHandler);
         }
      }

   }

   protected void uninstallListeners() {
      super.uninstallListeners();
      this.spinner.removePropertyChangeListener(this);
      JComponent var1 = this.spinner.getEditor();
      if (var1 instanceof JSpinner.DefaultEditor) {
         JFormattedTextField var2 = ((JSpinner.DefaultEditor)var1).getTextField();
         if (var2 != null) {
            var2.removeFocusListener(this.editorFocusHandler);
         }
      }

   }

   protected void installDefaults() {
      LayoutManager var1 = this.spinner.getLayout();
      if (var1 == null || var1 instanceof UIResource) {
         this.spinner.setLayout(this.createLayout());
      }

      this.updateStyle(this.spinner);
   }

   private void updateStyle(JSpinner var1) {
      SynthContext var2 = this.getContext(var1, 1);
      SynthStyle var3 = this.style;
      this.style = SynthLookAndFeel.updateStyle(var2, this);
      if (this.style != var3 && var3 != null) {
         this.installKeyboardActions();
      }

      var2.dispose();
   }

   protected void uninstallDefaults() {
      if (this.spinner.getLayout() instanceof UIResource) {
         this.spinner.setLayout((LayoutManager)null);
      }

      SynthContext var1 = this.getContext(this.spinner, 1);
      this.style.uninstallDefaults(var1);
      var1.dispose();
      this.style = null;
   }

   protected LayoutManager createLayout() {
      return new SynthSpinnerUI.SpinnerLayout();
   }

   protected Component createPreviousButton() {
      SynthArrowButton var1 = new SynthArrowButton(5);
      var1.setName("Spinner.previousButton");
      this.installPreviousButtonListeners(var1);
      return var1;
   }

   protected Component createNextButton() {
      SynthArrowButton var1 = new SynthArrowButton(1);
      var1.setName("Spinner.nextButton");
      this.installNextButtonListeners(var1);
      return var1;
   }

   protected JComponent createEditor() {
      JComponent var1 = this.spinner.getEditor();
      var1.setName("Spinner.editor");
      this.updateEditorAlignment(var1);
      return var1;
   }

   protected void replaceEditor(JComponent var1, JComponent var2) {
      this.spinner.remove(var1);
      this.spinner.add(var2, "Editor");
      JFormattedTextField var3;
      if (var1 instanceof JSpinner.DefaultEditor) {
         var3 = ((JSpinner.DefaultEditor)var1).getTextField();
         if (var3 != null) {
            var3.removeFocusListener(this.editorFocusHandler);
         }
      }

      if (var2 instanceof JSpinner.DefaultEditor) {
         var3 = ((JSpinner.DefaultEditor)var2).getTextField();
         if (var3 != null) {
            var3.addFocusListener(this.editorFocusHandler);
         }
      }

   }

   private void updateEditorAlignment(JComponent var1) {
      if (var1 instanceof JSpinner.DefaultEditor) {
         SynthContext var2 = this.getContext(this.spinner);
         Integer var3 = (Integer)var2.getStyle().get(var2, "Spinner.editorAlignment");
         JFormattedTextField var4 = ((JSpinner.DefaultEditor)var1).getTextField();
         if (var3 != null) {
            var4.setHorizontalAlignment(var3);
         }

         var4.putClientProperty("JComponent.sizeVariant", this.spinner.getClientProperty("JComponent.sizeVariant"));
      }

   }

   public SynthContext getContext(JComponent var1) {
      return this.getContext(var1, SynthLookAndFeel.getComponentState(var1));
   }

   private SynthContext getContext(JComponent var1, int var2) {
      return SynthContext.getContext(var1, this.style, var2);
   }

   public void update(Graphics var1, JComponent var2) {
      SynthContext var3 = this.getContext(var2);
      SynthLookAndFeel.update(var3, var1);
      var3.getPainter().paintSpinnerBackground(var3, var1, 0, 0, var2.getWidth(), var2.getHeight());
      this.paint(var3, var1);
      var3.dispose();
   }

   public void paint(Graphics var1, JComponent var2) {
      SynthContext var3 = this.getContext(var2);
      this.paint(var3, var1);
      var3.dispose();
   }

   protected void paint(SynthContext var1, Graphics var2) {
   }

   public void paintBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      var1.getPainter().paintSpinnerBorder(var1, var2, var3, var4, var5, var6);
   }

   public void propertyChange(PropertyChangeEvent var1) {
      JSpinner var2 = (JSpinner)((JSpinner)var1.getSource());
      SpinnerUI var3 = var2.getUI();
      if (var3 instanceof SynthSpinnerUI) {
         SynthSpinnerUI var4 = (SynthSpinnerUI)var3;
         if (SynthLookAndFeel.shouldUpdateStyle(var1)) {
            var4.updateStyle(var2);
         }
      }

   }

   private class EditorFocusHandler implements FocusListener {
      private EditorFocusHandler() {
      }

      public void focusGained(FocusEvent var1) {
         SynthSpinnerUI.this.spinner.repaint();
      }

      public void focusLost(FocusEvent var1) {
         SynthSpinnerUI.this.spinner.repaint();
      }

      // $FF: synthetic method
      EditorFocusHandler(Object var2) {
         this();
      }
   }

   private static class SpinnerLayout implements LayoutManager, UIResource {
      private Component nextButton;
      private Component previousButton;
      private Component editor;

      private SpinnerLayout() {
         this.nextButton = null;
         this.previousButton = null;
         this.editor = null;
      }

      public void addLayoutComponent(String var1, Component var2) {
         if ("Next".equals(var1)) {
            this.nextButton = var2;
         } else if ("Previous".equals(var1)) {
            this.previousButton = var2;
         } else if ("Editor".equals(var1)) {
            this.editor = var2;
         }

      }

      public void removeLayoutComponent(Component var1) {
         if (var1 == this.nextButton) {
            this.nextButton = null;
         } else if (var1 == this.previousButton) {
            this.previousButton = null;
         } else if (var1 == this.editor) {
            this.editor = null;
         }

      }

      private Dimension preferredSize(Component var1) {
         return var1 == null ? new Dimension(0, 0) : var1.getPreferredSize();
      }

      public Dimension preferredLayoutSize(Container var1) {
         Dimension var2 = this.preferredSize(this.nextButton);
         Dimension var3 = this.preferredSize(this.previousButton);
         Dimension var4 = this.preferredSize(this.editor);
         var4.height = (var4.height + 1) / 2 * 2;
         Dimension var5 = new Dimension(var4.width, var4.height);
         var5.width += Math.max(var2.width, var3.width);
         Insets var6 = var1.getInsets();
         var5.width += var6.left + var6.right;
         var5.height += var6.top + var6.bottom;
         return var5;
      }

      public Dimension minimumLayoutSize(Container var1) {
         return this.preferredLayoutSize(var1);
      }

      private void setBounds(Component var1, int var2, int var3, int var4, int var5) {
         if (var1 != null) {
            var1.setBounds(var2, var3, var4, var5);
         }

      }

      public void layoutContainer(Container var1) {
         Insets var2 = var1.getInsets();
         int var3 = var1.getWidth() - (var2.left + var2.right);
         int var4 = var1.getHeight() - (var2.top + var2.bottom);
         Dimension var5 = this.preferredSize(this.nextButton);
         Dimension var6 = this.preferredSize(this.previousButton);
         int var7 = var4 / 2;
         int var8 = var4 - var7;
         int var9 = Math.max(var5.width, var6.width);
         int var10 = var3 - var9;
         int var11;
         int var12;
         if (var1.getComponentOrientation().isLeftToRight()) {
            var11 = var2.left;
            var12 = var11 + var10;
         } else {
            var12 = var2.left;
            var11 = var12 + var9;
         }

         int var13 = var2.top + var7;
         this.setBounds(this.editor, var11, var2.top, var10, var4);
         this.setBounds(this.nextButton, var12, var2.top, var9, var7);
         this.setBounds(this.previousButton, var12, var13, var9, var8);
      }

      // $FF: synthetic method
      SpinnerLayout(Object var1) {
         this();
      }
   }
}
