package com.apple.laf;

import apple.laf.JRSUIConstants;
import apple.laf.JRSUIState;
import apple.laf.JRSUIStateFactory;
import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
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
import javax.swing.AbstractButton;
import javax.swing.ActionMap;
import javax.swing.ButtonModel;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.SpinnerUI;
import javax.swing.plaf.UIResource;
import javax.swing.text.InternationalFormatter;

public class AquaSpinnerUI extends SpinnerUI {
   private static final AquaUtils.RecyclableSingleton<? extends PropertyChangeListener> propertyChangeListener = new AquaUtils.RecyclableSingletonFromDefaultConstructor(AquaSpinnerUI.PropertyChangeHandler.class);
   private static final AquaUtils.RecyclableSingleton<AquaSpinnerUI.ArrowButtonHandler> nextButtonHandler = new AquaUtils.RecyclableSingleton<AquaSpinnerUI.ArrowButtonHandler>() {
      protected AquaSpinnerUI.ArrowButtonHandler getInstance() {
         return new AquaSpinnerUI.ArrowButtonHandler("increment", true);
      }
   };
   private static final AquaUtils.RecyclableSingleton<AquaSpinnerUI.ArrowButtonHandler> previousButtonHandler = new AquaUtils.RecyclableSingleton<AquaSpinnerUI.ArrowButtonHandler>() {
      protected AquaSpinnerUI.ArrowButtonHandler getInstance() {
         return new AquaSpinnerUI.ArrowButtonHandler("decrement", false);
      }
   };
   JSpinner spinner;
   AquaSpinnerUI.SpinPainter spinPainter;
   boolean wasOpaque;

   static PropertyChangeListener getPropertyChangeListener() {
      return (PropertyChangeListener)propertyChangeListener.get();
   }

   static AquaSpinnerUI.ArrowButtonHandler getNextButtonHandler() {
      return (AquaSpinnerUI.ArrowButtonHandler)nextButtonHandler.get();
   }

   static AquaSpinnerUI.ArrowButtonHandler getPreviousButtonHandler() {
      return (AquaSpinnerUI.ArrowButtonHandler)previousButtonHandler.get();
   }

   public static ComponentUI createUI(JComponent var0) {
      return new AquaSpinnerUI();
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
      AquaSpinnerUI.TransparentButton var2 = this.createNextButton();
      AquaSpinnerUI.TransparentButton var3 = this.createPreviousButton();
      this.spinPainter = new AquaSpinnerUI.SpinPainter(var2, var3);
      this.maybeAdd(var2, "Next");
      this.maybeAdd(var3, "Previous");
      this.maybeAdd(this.createEditor(), "Editor");
      this.maybeAdd(this.spinPainter, "Painter");
      this.updateEnabledState();
      this.installKeyboardActions();
      this.wasOpaque = this.spinner.isOpaque();
      this.spinner.setOpaque(false);
   }

   public void uninstallUI(JComponent var1) {
      this.uninstallDefaults();
      this.uninstallListeners();
      this.spinner.setOpaque(this.wasOpaque);
      this.spinner = null;
      var1.removeAll();
   }

   protected void installListeners() {
      this.spinner.addPropertyChangeListener(getPropertyChangeListener());
   }

   protected void uninstallListeners() {
      this.spinner.removePropertyChangeListener(getPropertyChangeListener());
   }

   protected void installDefaults() {
      this.spinner.setLayout(this.createLayout());
      LookAndFeel.installBorder(this.spinner, "Spinner.border");
      LookAndFeel.installColorsAndFont(this.spinner, "Spinner.background", "Spinner.foreground", "Spinner.font");
   }

   protected void uninstallDefaults() {
      this.spinner.setLayout((LayoutManager)null);
   }

   protected LayoutManager createLayout() {
      return new AquaSpinnerUI.SpinnerLayout();
   }

   protected PropertyChangeListener createPropertyChangeListener() {
      return new AquaSpinnerUI.PropertyChangeHandler();
   }

   protected AquaSpinnerUI.TransparentButton createPreviousButton() {
      AquaSpinnerUI.TransparentButton var1 = new AquaSpinnerUI.TransparentButton();
      var1.addActionListener(getPreviousButtonHandler());
      var1.addMouseListener(getPreviousButtonHandler());
      var1.setInheritsPopupMenu(true);
      return var1;
   }

   protected AquaSpinnerUI.TransparentButton createNextButton() {
      AquaSpinnerUI.TransparentButton var1 = new AquaSpinnerUI.TransparentButton();
      var1.addActionListener(getNextButtonHandler());
      var1.addMouseListener(getNextButtonHandler());
      var1.setInheritsPopupMenu(true);
      return var1;
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

   protected JComponent createEditor() {
      JComponent var1 = this.spinner.getEditor();
      this.fixupEditor(var1);
      return var1;
   }

   protected void replaceEditor(JComponent var1, JComponent var2) {
      this.spinner.remove(var1);
      this.fixupEditor(var2);
      this.spinner.add(var2, "Editor");
   }

   protected void fixupEditor(JComponent var1) {
      if (var1 instanceof JSpinner.DefaultEditor) {
         var1.setOpaque(false);
         var1.setInheritsPopupMenu(true);
         if (var1.getFont() instanceof UIResource) {
            var1.setFont(this.spinner.getFont());
         }

         JFormattedTextField var2 = ((JSpinner.DefaultEditor)var1).getTextField();
         if (var2.getFont() instanceof UIResource) {
            var2.setFont(this.spinner.getFont());
         }

         InputMap var3 = this.getInputMap(1);
         InputMap var4 = var2.getInputMap();
         KeyStroke[] var5 = var3.keys();
         KeyStroke[] var6 = var5;
         int var7 = var5.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            KeyStroke var9 = var6[var8];
            var4.put(var9, var3.get(var9));
         }

      }
   }

   void updateEnabledState() {
      this.updateEnabledState(this.spinner, this.spinner.isEnabled());
   }

   private void updateEnabledState(Container var1, boolean var2) {
      for(int var3 = var1.getComponentCount() - 1; var3 >= 0; --var3) {
         Component var4 = var1.getComponent(var3);
         var4.setEnabled(var2);
         if (var4 instanceof Container) {
            this.updateEnabledState((Container)var4, var2);
         }
      }

   }

   private void installKeyboardActions() {
      InputMap var1 = this.getInputMap(1);
      SwingUtilities.replaceUIInputMap(this.spinner, 1, var1);
      SwingUtilities.replaceUIActionMap(this.spinner, this.getActionMap());
   }

   private InputMap getInputMap(int var1) {
      return var1 == 1 ? (InputMap)UIManager.get("Spinner.ancestorInputMap") : null;
   }

   private ActionMap getActionMap() {
      ActionMap var1 = (ActionMap)UIManager.get("Spinner.actionMap");
      if (var1 == null) {
         var1 = this.createActionMap();
         if (var1 != null) {
            UIManager.getLookAndFeelDefaults().put("Spinner.actionMap", var1);
         }
      }

      return var1;
   }

   private ActionMap createActionMap() {
      ActionMapUIResource var1 = new ActionMapUIResource();
      var1.put("increment", getNextButtonHandler());
      var1.put("decrement", getPreviousButtonHandler());
      return var1;
   }

   void updateToolTipTextForChildren(JComponent var1) {
      String var2 = var1.getToolTipText();
      Component[] var3 = var1.getComponents();
      Component[] var4 = var3;
      int var5 = var3.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Component var7 = var4[var6];
         if (var7 instanceof JSpinner.DefaultEditor) {
            JFormattedTextField var8 = ((JSpinner.DefaultEditor)var7).getTextField();
            if (var8 != null) {
               var8.setToolTipText(var2);
            }
         } else if (var7 instanceof JComponent) {
            ((JComponent)var7).setToolTipText(var2);
         }
      }

   }

   static class PropertyChangeHandler implements PropertyChangeListener {
      public void propertyChange(PropertyChangeEvent var1) {
         String var2 = var1.getPropertyName();
         JSpinner var3 = (JSpinner)((JSpinner)var1.getSource());
         SpinnerUI var4 = var3.getUI();
         if (var4 instanceof AquaSpinnerUI) {
            AquaSpinnerUI var5 = (AquaSpinnerUI)var4;
            JComponent var6;
            if ("editor".equals(var2)) {
               var6 = (JComponent)var1.getOldValue();
               JComponent var7 = (JComponent)var1.getNewValue();
               var5.replaceEditor(var6, var7);
               var5.updateEnabledState();
            } else if ("enabled".equals(var2)) {
               var5.updateEnabledState();
            } else if ("ToolTipText".equals(var2)) {
               var5.updateToolTipTextForChildren(var3);
            } else if ("font".equals(var2)) {
               var6 = var3.getEditor();
               if (var6 != null && var6 instanceof JSpinner.DefaultEditor) {
                  JFormattedTextField var8 = ((JSpinner.DefaultEditor)var6).getTextField();
                  if (var8 != null && var8.getFont() instanceof UIResource) {
                     var8.setFont(var3.getFont());
                  }
               }
            }
         }

      }
   }

   static class SpinnerLayout implements LayoutManager {
      private Component nextButton = null;
      private Component previousButton = null;
      private Component editor = null;
      private Component painter = null;

      public void addLayoutComponent(String var1, Component var2) {
         if ("Next".equals(var1)) {
            this.nextButton = var2;
         } else if ("Previous".equals(var1)) {
            this.previousButton = var2;
         } else if ("Editor".equals(var1)) {
            this.editor = var2;
         } else if ("Painter".equals(var1)) {
            this.painter = var2;
         }

      }

      public void removeLayoutComponent(Component var1) {
         if (var1 == this.nextButton) {
            var1 = null;
         } else if (var1 == this.previousButton) {
            this.previousButton = null;
         } else if (var1 == this.editor) {
            this.editor = null;
         } else if (var1 == this.painter) {
            this.painter = null;
         }

      }

      private Dimension preferredSize(Component var1) {
         return var1 == null ? new Dimension(0, 0) : var1.getPreferredSize();
      }

      public Dimension preferredLayoutSize(Container var1) {
         Dimension var2 = this.preferredSize(this.editor);
         Dimension var3 = this.preferredSize(this.painter);
         var2.height = (var2.height + 1) / 2 * 2;
         Dimension var4 = new Dimension(var2.width, Math.max(var3.height, var2.height));
         var4.width += var3.width;
         Insets var5 = var1.getInsets();
         var4.width += var5.left + var5.right;
         var4.height += var5.top + var5.bottom;
         return var4;
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
         Dimension var5 = this.preferredSize(this.painter);
         int var6 = var4 / 2;
         int var7 = var4 - var6;
         int var8 = var5.width;
         int var9 = var3 - var8;
         int var10;
         int var11;
         if (var1.getComponentOrientation().isLeftToRight()) {
            var10 = var2.left;
            var11 = var10 + var9;
         } else {
            var11 = var2.left;
            var10 = var11 + var8;
         }

         int var12 = var2.top + var6;
         int var13 = var12 - var5.height / 2;
         this.setBounds(this.editor, var10, var2.top, var9, var4);
         this.setBounds(this.nextButton, var11, var2.top, var8, var6);
         this.setBounds(this.previousButton, var11, var12, var8, var7);
         this.setBounds(this.painter, var11, var13, var8, var5.height);
      }
   }

   class SpinPainter extends JComponent {
      final AquaPainter<JRSUIState> painter = AquaPainter.create(JRSUIStateFactory.getSpinnerArrows());
      ButtonModel fTopModel;
      ButtonModel fBottomModel;
      boolean fPressed = false;
      boolean fTopPressed = false;
      Dimension kPreferredSize = new Dimension(15, 24);

      public SpinPainter(AbstractButton var2, AbstractButton var3) {
         if (var2 != null) {
            this.fTopModel = var2.getModel();
         }

         if (var3 != null) {
            this.fBottomModel = var3.getModel();
         }

      }

      public void paint(Graphics var1) {
         if (AquaSpinnerUI.this.spinner.isOpaque()) {
            var1.setColor(AquaSpinnerUI.this.spinner.getBackground());
            var1.fillRect(0, 0, this.getWidth(), this.getHeight());
         }

         AquaUtilControlSize.applySizeForControl(AquaSpinnerUI.this.spinner, this.painter);
         if (this.isEnabled()) {
            if (this.fTopModel != null && this.fTopModel.isPressed()) {
               this.painter.state.set(JRSUIConstants.State.PRESSED);
               this.painter.state.set(JRSUIConstants.BooleanValue.NO);
            } else if (this.fBottomModel != null && this.fBottomModel.isPressed()) {
               this.painter.state.set(JRSUIConstants.State.PRESSED);
               this.painter.state.set(JRSUIConstants.BooleanValue.YES);
            } else {
               this.painter.state.set(JRSUIConstants.State.ACTIVE);
            }
         } else {
            this.painter.state.set(JRSUIConstants.State.DISABLED);
         }

         Rectangle var2 = this.getBounds();
         this.painter.paint(var1, AquaSpinnerUI.this.spinner, 0, 0, var2.width, var2.height);
      }

      public Dimension getPreferredSize() {
         JRSUIConstants.Size var1 = AquaUtilControlSize.getUserSizeFrom(this);
         return var1 == JRSUIConstants.Size.MINI ? new Dimension(this.kPreferredSize.width, this.kPreferredSize.height - 8) : this.kPreferredSize;
      }
   }

   private static class ArrowButtonHandler extends AbstractAction implements MouseListener {
      final Timer autoRepeatTimer;
      final boolean isNext;
      JSpinner spinner = null;

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
         if (!(var1.getSource() instanceof Timer)) {
            this.spinner = this.eventToSpinner(var1);
         }

         if (this.spinner != null) {
            try {
               int var2 = this.getCalendarField(this.spinner);
               this.spinner.commitEdit();
               if (var2 != -1) {
                  ((SpinnerDateModel)this.spinner.getModel()).setCalendarField(var2);
               }

               Object var3 = this.isNext ? this.spinner.getNextValue() : this.spinner.getPreviousValue();
               if (var3 != null) {
                  this.spinner.setValue(var3);
                  this.select(this.spinner);
               }
            } catch (IllegalArgumentException var4) {
               UIManager.getLookAndFeel().provideErrorFeedback(this.spinner);
            } catch (ParseException var5) {
               UIManager.getLookAndFeel().provideErrorFeedback(this.spinner);
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
         if (!(var2 instanceof JSpinner.DateEditor)) {
            return -1;
         } else {
            JSpinner.DateEditor var3 = (JSpinner.DateEditor)var2;
            JFormattedTextField var4 = var3.getTextField();
            int var5 = var4.getSelectionStart();
            JFormattedTextField.AbstractFormatter var6 = var4.getFormatter();
            if (!(var6 instanceof InternationalFormatter)) {
               return -1;
            } else {
               Format.Field[] var7 = ((InternationalFormatter)var6).getFields(var5);
               Format.Field[] var8 = var7;
               int var9 = var7.length;

               for(int var10 = 0; var10 < var9; ++var10) {
                  Format.Field var11 = var8[var10];
                  if (var11 instanceof DateFormat.Field) {
                     int var12;
                     if (var11 == DateFormat.Field.HOUR1) {
                        var12 = 10;
                     } else {
                        var12 = ((DateFormat.Field)var11).getCalendarField();
                     }

                     if (var12 != -1) {
                        return var12;
                     }
                  }
               }

               return -1;
            }
         }
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
         this.spinner = null;
      }

      public void mouseClicked(MouseEvent var1) {
      }

      public void mouseEntered(MouseEvent var1) {
      }

      public void mouseExited(MouseEvent var1) {
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
   }

   class TransparentButton extends JButton implements SwingConstants {
      boolean interceptRepaints = false;

      public TransparentButton() {
         this.setFocusable(false);
         this.interceptRepaints = true;
      }

      public void paint(Graphics var1) {
      }

      public void repaint() {
         if (this.interceptRepaints) {
            if (AquaSpinnerUI.this.spinPainter == null) {
               return;
            }

            AquaSpinnerUI.this.spinPainter.repaint();
         }

         super.repaint();
      }
   }
}
