package javax.swing.plaf.basic;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.AttributedCharacterIterator;
import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.ButtonModel;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.LookAndFeel;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerModel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.SpinnerUI;
import javax.swing.plaf.UIResource;
import javax.swing.text.InternationalFormatter;
import sun.swing.DefaultLookup;

public class BasicSpinnerUI extends SpinnerUI {
   protected JSpinner spinner;
   private BasicSpinnerUI.Handler handler;
   private static final BasicSpinnerUI.ArrowButtonHandler nextButtonHandler = new BasicSpinnerUI.ArrowButtonHandler("increment", true);
   private static final BasicSpinnerUI.ArrowButtonHandler previousButtonHandler = new BasicSpinnerUI.ArrowButtonHandler("decrement", false);
   private PropertyChangeListener propertyChangeListener;
   private static final Dimension zeroSize = new Dimension(0, 0);

   public static ComponentUI createUI(JComponent var0) {
      return new BasicSpinnerUI();
   }

   private void maybeAdd(Component var1, String var2) {
      if (var1 != null) {
         this.spinner.add(var1, var2);
      }

   }

   public void installUI(JComponent var1) {
      this.spinner = (JSpinner)var1;
      this.installDefaults();
      this.installListeners();
      this.maybeAdd(this.createNextButton(), "Next");
      this.maybeAdd(this.createPreviousButton(), "Previous");
      this.maybeAdd(this.createEditor(), "Editor");
      this.updateEnabledState();
      this.installKeyboardActions();
   }

   public void uninstallUI(JComponent var1) {
      this.uninstallDefaults();
      this.uninstallListeners();
      this.spinner = null;
      var1.removeAll();
   }

   protected void installListeners() {
      this.propertyChangeListener = this.createPropertyChangeListener();
      this.spinner.addPropertyChangeListener(this.propertyChangeListener);
      if (DefaultLookup.getBoolean(this.spinner, this, "Spinner.disableOnBoundaryValues", false)) {
         this.spinner.addChangeListener(this.getHandler());
      }

      JComponent var1 = this.spinner.getEditor();
      if (var1 != null && var1 instanceof JSpinner.DefaultEditor) {
         JFormattedTextField var2 = ((JSpinner.DefaultEditor)var1).getTextField();
         if (var2 != null) {
            var2.addFocusListener(nextButtonHandler);
            var2.addFocusListener(previousButtonHandler);
         }
      }

   }

   protected void uninstallListeners() {
      this.spinner.removePropertyChangeListener(this.propertyChangeListener);
      this.spinner.removeChangeListener(this.handler);
      JComponent var1 = this.spinner.getEditor();
      this.removeEditorBorderListener(var1);
      if (var1 instanceof JSpinner.DefaultEditor) {
         JFormattedTextField var2 = ((JSpinner.DefaultEditor)var1).getTextField();
         if (var2 != null) {
            var2.removeFocusListener(nextButtonHandler);
            var2.removeFocusListener(previousButtonHandler);
         }
      }

      this.propertyChangeListener = null;
      this.handler = null;
   }

   protected void installDefaults() {
      this.spinner.setLayout(this.createLayout());
      LookAndFeel.installBorder(this.spinner, "Spinner.border");
      LookAndFeel.installColorsAndFont(this.spinner, "Spinner.background", "Spinner.foreground", "Spinner.font");
      LookAndFeel.installProperty(this.spinner, "opaque", Boolean.TRUE);
   }

   protected void uninstallDefaults() {
      this.spinner.setLayout((LayoutManager)null);
   }

   private BasicSpinnerUI.Handler getHandler() {
      if (this.handler == null) {
         this.handler = new BasicSpinnerUI.Handler();
      }

      return this.handler;
   }

   protected void installNextButtonListeners(Component var1) {
      this.installButtonListeners(var1, nextButtonHandler);
   }

   protected void installPreviousButtonListeners(Component var1) {
      this.installButtonListeners(var1, previousButtonHandler);
   }

   private void installButtonListeners(Component var1, BasicSpinnerUI.ArrowButtonHandler var2) {
      if (var1 instanceof JButton) {
         ((JButton)var1).addActionListener(var2);
      }

      var1.addMouseListener(var2);
   }

   protected LayoutManager createLayout() {
      return this.getHandler();
   }

   protected PropertyChangeListener createPropertyChangeListener() {
      return this.getHandler();
   }

   protected Component createPreviousButton() {
      Component var1 = this.createArrowButton(5);
      var1.setName("Spinner.previousButton");
      this.installPreviousButtonListeners(var1);
      return var1;
   }

   protected Component createNextButton() {
      Component var1 = this.createArrowButton(1);
      var1.setName("Spinner.nextButton");
      this.installNextButtonListeners(var1);
      return var1;
   }

   private Component createArrowButton(int var1) {
      BasicArrowButton var2 = new BasicArrowButton(var1);
      Border var3 = UIManager.getBorder("Spinner.arrowButtonBorder");
      if (var3 instanceof UIResource) {
         var2.setBorder(new CompoundBorder(var3, (Border)null));
      } else {
         var2.setBorder(var3);
      }

      var2.setInheritsPopupMenu(true);
      return var2;
   }

   protected JComponent createEditor() {
      JComponent var1 = this.spinner.getEditor();
      this.maybeRemoveEditorBorder(var1);
      this.installEditorBorderListener(var1);
      var1.setInheritsPopupMenu(true);
      this.updateEditorAlignment(var1);
      return var1;
   }

   protected void replaceEditor(JComponent var1, JComponent var2) {
      this.spinner.remove(var1);
      this.maybeRemoveEditorBorder(var2);
      this.installEditorBorderListener(var2);
      var2.setInheritsPopupMenu(true);
      this.spinner.add(var2, "Editor");
   }

   private void updateEditorAlignment(JComponent var1) {
      if (var1 instanceof JSpinner.DefaultEditor) {
         int var2 = UIManager.getInt("Spinner.editorAlignment");
         JFormattedTextField var3 = ((JSpinner.DefaultEditor)var1).getTextField();
         var3.setHorizontalAlignment(var2);
      }

   }

   private void maybeRemoveEditorBorder(JComponent var1) {
      if (!UIManager.getBoolean("Spinner.editorBorderPainted")) {
         if (var1 instanceof JPanel && var1.getBorder() == null && var1.getComponentCount() > 0) {
            var1 = (JComponent)var1.getComponent(0);
         }

         if (var1 != null && var1.getBorder() instanceof UIResource) {
            var1.setBorder((Border)null);
         }
      }

   }

   private void installEditorBorderListener(JComponent var1) {
      if (!UIManager.getBoolean("Spinner.editorBorderPainted")) {
         if (var1 instanceof JPanel && var1.getBorder() == null && var1.getComponentCount() > 0) {
            var1 = (JComponent)var1.getComponent(0);
         }

         if (var1 != null && (var1.getBorder() == null || var1.getBorder() instanceof UIResource)) {
            var1.addPropertyChangeListener(this.getHandler());
         }
      }

   }

   private void removeEditorBorderListener(JComponent var1) {
      if (!UIManager.getBoolean("Spinner.editorBorderPainted")) {
         if (var1 instanceof JPanel && var1.getComponentCount() > 0) {
            var1 = (JComponent)var1.getComponent(0);
         }

         if (var1 != null) {
            var1.removePropertyChangeListener(this.getHandler());
         }
      }

   }

   private void updateEnabledState() {
      this.updateEnabledState(this.spinner, this.spinner.isEnabled());
   }

   private void updateEnabledState(Container var1, boolean var2) {
      for(int var3 = var1.getComponentCount() - 1; var3 >= 0; --var3) {
         Component var4 = var1.getComponent(var3);
         if (DefaultLookup.getBoolean(this.spinner, this, "Spinner.disableOnBoundaryValues", false)) {
            SpinnerModel var5 = this.spinner.getModel();
            if (var4.getName() == "Spinner.nextButton" && var5.getNextValue() == null) {
               var4.setEnabled(false);
            } else if (var4.getName() == "Spinner.previousButton" && var5.getPreviousValue() == null) {
               var4.setEnabled(false);
            } else {
               var4.setEnabled(var2);
            }
         } else {
            var4.setEnabled(var2);
         }

         if (var4 instanceof Container) {
            this.updateEnabledState((Container)var4, var2);
         }
      }

   }

   protected void installKeyboardActions() {
      InputMap var1 = this.getInputMap(1);
      SwingUtilities.replaceUIInputMap(this.spinner, 1, var1);
      LazyActionMap.installLazyActionMap(this.spinner, BasicSpinnerUI.class, "Spinner.actionMap");
   }

   private InputMap getInputMap(int var1) {
      return var1 == 1 ? (InputMap)DefaultLookup.get(this.spinner, this, "Spinner.ancestorInputMap") : null;
   }

   static void loadActionMap(LazyActionMap var0) {
      var0.put("increment", nextButtonHandler);
      var0.put("decrement", previousButtonHandler);
   }

   public int getBaseline(JComponent var1, int var2, int var3) {
      super.getBaseline(var1, var2, var3);
      JComponent var4 = this.spinner.getEditor();
      Insets var5 = this.spinner.getInsets();
      var2 = var2 - var5.left - var5.right;
      var3 = var3 - var5.top - var5.bottom;
      if (var2 >= 0 && var3 >= 0) {
         int var6 = var4.getBaseline(var2, var3);
         if (var6 >= 0) {
            return var5.top + var6;
         }
      }

      return -1;
   }

   public Component.BaselineResizeBehavior getBaselineResizeBehavior(JComponent var1) {
      super.getBaselineResizeBehavior(var1);
      return this.spinner.getEditor().getBaselineResizeBehavior();
   }

   private static class Handler implements LayoutManager, PropertyChangeListener, ChangeListener {
      private Component nextButton;
      private Component previousButton;
      private Component editor;

      private Handler() {
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
         return var1 == null ? BasicSpinnerUI.zeroSize : var1.getPreferredSize();
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
         int var2 = var1.getWidth();
         int var3 = var1.getHeight();
         Insets var4 = var1.getInsets();
         if (this.nextButton == null && this.previousButton == null) {
            this.setBounds(this.editor, var4.left, var4.top, var2 - var4.left - var4.right, var3 - var4.top - var4.bottom);
         } else {
            Dimension var5 = this.preferredSize(this.nextButton);
            Dimension var6 = this.preferredSize(this.previousButton);
            int var7 = Math.max(var5.width, var6.width);
            int var8 = var3 - (var4.top + var4.bottom);
            Insets var9 = UIManager.getInsets("Spinner.arrowButtonInsets");
            if (var9 == null) {
               var9 = var4;
            }

            int var10;
            int var11;
            int var12;
            if (var1.getComponentOrientation().isLeftToRight()) {
               var10 = var4.left;
               var11 = var2 - var4.left - var7 - var9.right;
               var12 = var2 - var7 - var9.right;
            } else {
               var12 = var9.left;
               var10 = var12 + var7;
               var11 = var2 - var9.left - var7 - var4.right;
            }

            int var13 = var9.top;
            int var14 = var3 / 2 + var3 % 2 - var13;
            int var15 = var9.top + var14;
            int var16 = var3 - var15 - var9.bottom;
            this.setBounds(this.editor, var10, var4.top, var11, var8);
            this.setBounds(this.nextButton, var12, var13, var7, var14);
            this.setBounds(this.previousButton, var12, var15, var7, var16);
         }
      }

      public void propertyChange(PropertyChangeEvent var1) {
         String var2 = var1.getPropertyName();
         if (var1.getSource() instanceof JSpinner) {
            JSpinner var3 = (JSpinner)((JSpinner)var1.getSource());
            SpinnerUI var4 = var3.getUI();
            if (var4 instanceof BasicSpinnerUI) {
               BasicSpinnerUI var5 = (BasicSpinnerUI)var4;
               JComponent var6;
               if ("editor".equals(var2)) {
                  var6 = (JComponent)var1.getOldValue();
                  JComponent var7 = (JComponent)var1.getNewValue();
                  var5.replaceEditor(var6, var7);
                  var5.updateEnabledState();
                  JFormattedTextField var8;
                  if (var6 instanceof JSpinner.DefaultEditor) {
                     var8 = ((JSpinner.DefaultEditor)var6).getTextField();
                     if (var8 != null) {
                        var8.removeFocusListener(BasicSpinnerUI.nextButtonHandler);
                        var8.removeFocusListener(BasicSpinnerUI.previousButtonHandler);
                     }
                  }

                  if (var7 instanceof JSpinner.DefaultEditor) {
                     var8 = ((JSpinner.DefaultEditor)var7).getTextField();
                     if (var8 != null) {
                        if (var8.getFont() instanceof UIResource) {
                           var8.setFont(var3.getFont());
                        }

                        var8.addFocusListener(BasicSpinnerUI.nextButtonHandler);
                        var8.addFocusListener(BasicSpinnerUI.previousButtonHandler);
                     }
                  }
               } else if (!"enabled".equals(var2) && !"model".equals(var2)) {
                  if ("font".equals(var2)) {
                     var6 = var3.getEditor();
                     if (var6 != null && var6 instanceof JSpinner.DefaultEditor) {
                        JFormattedTextField var13 = ((JSpinner.DefaultEditor)var6).getTextField();
                        if (var13 != null && var13.getFont() instanceof UIResource) {
                           var13.setFont(var3.getFont());
                        }
                     }
                  } else if ("ToolTipText".equals(var2)) {
                     this.updateToolTipTextForChildren(var3);
                  }
               } else {
                  var5.updateEnabledState();
               }
            }
         } else if (var1.getSource() instanceof JComponent) {
            JComponent var9 = (JComponent)var1.getSource();
            if (var9.getParent() instanceof JPanel && var9.getParent().getParent() instanceof JSpinner && "border".equals(var2)) {
               JSpinner var10 = (JSpinner)var9.getParent().getParent();
               SpinnerUI var11 = var10.getUI();
               if (var11 instanceof BasicSpinnerUI) {
                  BasicSpinnerUI var12 = (BasicSpinnerUI)var11;
                  var12.maybeRemoveEditorBorder(var9);
               }
            }
         }

      }

      private void updateToolTipTextForChildren(JComponent var1) {
         String var2 = var1.getToolTipText();
         Component[] var3 = var1.getComponents();

         for(int var4 = 0; var4 < var3.length; ++var4) {
            if (var3[var4] instanceof JSpinner.DefaultEditor) {
               JFormattedTextField var5 = ((JSpinner.DefaultEditor)var3[var4]).getTextField();
               if (var5 != null) {
                  var5.setToolTipText(var2);
               }
            } else if (var3[var4] instanceof JComponent) {
               ((JComponent)var3[var4]).setToolTipText(var1.getToolTipText());
            }
         }

      }

      public void stateChanged(ChangeEvent var1) {
         if (var1.getSource() instanceof JSpinner) {
            JSpinner var2 = (JSpinner)var1.getSource();
            SpinnerUI var3 = var2.getUI();
            if (DefaultLookup.getBoolean(var2, var3, "Spinner.disableOnBoundaryValues", false) && var3 instanceof BasicSpinnerUI) {
               BasicSpinnerUI var4 = (BasicSpinnerUI)var3;
               var4.updateEnabledState();
            }
         }

      }

      // $FF: synthetic method
      Handler(Object var1) {
         this();
      }
   }

   private static class ArrowButtonHandler extends AbstractAction implements FocusListener, MouseListener, UIResource {
      final Timer autoRepeatTimer;
      final boolean isNext;
      JSpinner spinner = null;
      JButton arrowButton = null;

      ArrowButtonHandler(String var1, boolean var2) {
         super(var1);
         this.isNext = var2;
         this.autoRepeatTimer = new Timer(60, this);
         this.autoRepeatTimer.setInitialDelay(300);
      }

      private JSpinner eventToSpinner(AWTEvent var1) {
         Object var2;
         for(var2 = var1.getSource(); var2 instanceof Component && !(var2 instanceof JSpinner); var2 = ((Component)var2).getParent()) {
         }

         return var2 instanceof JSpinner ? (JSpinner)var2 : null;
      }

      public void actionPerformed(ActionEvent var1) {
         JSpinner var2 = this.spinner;
         if (!(var1.getSource() instanceof Timer)) {
            var2 = this.eventToSpinner(var1);
            if (var1.getSource() instanceof JButton) {
               this.arrowButton = (JButton)var1.getSource();
            }
         } else if (this.arrowButton != null && !this.arrowButton.getModel().isPressed() && this.autoRepeatTimer.isRunning()) {
            this.autoRepeatTimer.stop();
            var2 = null;
            this.arrowButton = null;
         }

         if (var2 != null) {
            try {
               int var3 = this.getCalendarField(var2);
               var2.commitEdit();
               if (var3 != -1) {
                  ((SpinnerDateModel)var2.getModel()).setCalendarField(var3);
               }

               Object var4 = this.isNext ? var2.getNextValue() : var2.getPreviousValue();
               if (var4 != null) {
                  var2.setValue(var4);
                  this.select(var2);
               }
            } catch (IllegalArgumentException var5) {
               UIManager.getLookAndFeel().provideErrorFeedback(var2);
            } catch (ParseException var6) {
               UIManager.getLookAndFeel().provideErrorFeedback(var2);
            }
         }

      }

      private void select(JSpinner var1) {
         JComponent var2 = var1.getEditor();
         if (var2 instanceof JSpinner.DateEditor) {
            JSpinner.DateEditor var3 = (JSpinner.DateEditor)var2;
            JFormattedTextField var4 = var3.getTextField();
            SimpleDateFormat var5 = var3.getFormat();
            Object var6;
            if (var5 != null && (var6 = var1.getValue()) != null) {
               SpinnerDateModel var7 = var3.getModel();
               DateFormat.Field var8 = DateFormat.Field.ofCalendarField(var7.getCalendarField());
               if (var8 != null) {
                  try {
                     AttributedCharacterIterator var9 = var5.formatToCharacterIterator(var6);
                     if (!this.select(var4, var9, var8) && var8 == DateFormat.Field.HOUR0) {
                        this.select(var4, var9, DateFormat.Field.HOUR1);
                     }
                  } catch (IllegalArgumentException var10) {
                  }
               }
            }
         }

      }

      private boolean select(JFormattedTextField var1, AttributedCharacterIterator var2, DateFormat.Field var3) {
         int var4 = var1.getDocument().getLength();
         var2.first();

         do {
            Map var5 = var2.getAttributes();
            if (var5 != null && var5.containsKey(var3)) {
               int var6 = var2.getRunStart((AttributedCharacterIterator.Attribute)var3);
               int var7 = var2.getRunLimit((AttributedCharacterIterator.Attribute)var3);
               if (var6 != -1 && var7 != -1 && var6 <= var4 && var7 <= var4) {
                  var1.select(var6, var7);
               }

               return true;
            }
         } while(var2.next() != '\uffff');

         return false;
      }

      private int getCalendarField(JSpinner var1) {
         JComponent var2 = var1.getEditor();
         if (var2 instanceof JSpinner.DateEditor) {
            JSpinner.DateEditor var3 = (JSpinner.DateEditor)var2;
            JFormattedTextField var4 = var3.getTextField();
            int var5 = var4.getSelectionStart();
            JFormattedTextField.AbstractFormatter var6 = var4.getFormatter();
            if (var6 instanceof InternationalFormatter) {
               Format.Field[] var7 = ((InternationalFormatter)var6).getFields(var5);

               for(int var8 = 0; var8 < var7.length; ++var8) {
                  if (var7[var8] instanceof DateFormat.Field) {
                     int var9;
                     if (var7[var8] == DateFormat.Field.HOUR1) {
                        var9 = 10;
                     } else {
                        var9 = ((DateFormat.Field)var7[var8]).getCalendarField();
                     }

                     if (var9 != -1) {
                        return var9;
                     }
                  }
               }
            }
         }

         return -1;
      }

      public void mousePressed(MouseEvent var1) {
         if (SwingUtilities.isLeftMouseButton(var1) && var1.getComponent().isEnabled()) {
            this.spinner = this.eventToSpinner(var1);
            this.autoRepeatTimer.start();
            this.focusSpinnerIfNecessary();
         }

      }

      public void mouseReleased(MouseEvent var1) {
         this.autoRepeatTimer.stop();
         this.arrowButton = null;
         this.spinner = null;
      }

      public void mouseClicked(MouseEvent var1) {
      }

      public void mouseEntered(MouseEvent var1) {
         if (this.spinner != null && !this.autoRepeatTimer.isRunning() && this.spinner == this.eventToSpinner(var1)) {
            this.autoRepeatTimer.start();
         }

      }

      public void mouseExited(MouseEvent var1) {
         if (this.autoRepeatTimer.isRunning()) {
            this.autoRepeatTimer.stop();
         }

      }

      private void focusSpinnerIfNecessary() {
         Component var1 = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
         if (this.spinner.isRequestFocusEnabled() && (var1 == null || !SwingUtilities.isDescendingFrom(var1, this.spinner))) {
            Object var2 = this.spinner;
            if (!((Container)var2).isFocusCycleRoot()) {
               var2 = ((Container)var2).getFocusCycleRootAncestor();
            }

            if (var2 != null) {
               FocusTraversalPolicy var3 = ((Container)var2).getFocusTraversalPolicy();
               Component var4 = var3.getComponentAfter((Container)var2, this.spinner);
               if (var4 != null && SwingUtilities.isDescendingFrom(var4, this.spinner)) {
                  var4.requestFocus();
               }
            }
         }

      }

      public void focusGained(FocusEvent var1) {
      }

      public void focusLost(FocusEvent var1) {
         if (this.spinner == this.eventToSpinner(var1)) {
            if (this.autoRepeatTimer.isRunning()) {
               this.autoRepeatTimer.stop();
            }

            this.spinner = null;
            if (this.arrowButton != null) {
               ButtonModel var2 = this.arrowButton.getModel();
               var2.setPressed(false);
               var2.setArmed(false);
               this.arrowButton = null;
            }
         }

      }
   }
}
