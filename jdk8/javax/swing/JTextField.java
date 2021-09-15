package javax.swing;

import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;
import javax.swing.text.TextAction;

public class JTextField extends JTextComponent implements SwingConstants {
   private Action action;
   private PropertyChangeListener actionPropertyChangeListener;
   public static final String notifyAction = "notify-field-accept";
   private BoundedRangeModel visibility;
   private int horizontalAlignment;
   private int columns;
   private int columnWidth;
   private String command;
   private static final Action[] defaultActions = new Action[]{new JTextField.NotifyAction()};
   private static final String uiClassID = "TextFieldUI";

   public JTextField() {
      this((Document)null, (String)null, 0);
   }

   public JTextField(String var1) {
      this((Document)null, var1, 0);
   }

   public JTextField(int var1) {
      this((Document)null, (String)null, var1);
   }

   public JTextField(String var1, int var2) {
      this((Document)null, var1, var2);
   }

   public JTextField(Document var1, String var2, int var3) {
      this.horizontalAlignment = 10;
      if (var3 < 0) {
         throw new IllegalArgumentException("columns less than zero.");
      } else {
         this.visibility = new DefaultBoundedRangeModel();
         this.visibility.addChangeListener(new JTextField.ScrollRepainter());
         this.columns = var3;
         if (var1 == null) {
            var1 = this.createDefaultModel();
         }

         this.setDocument(var1);
         if (var2 != null) {
            this.setText(var2);
         }

      }
   }

   public String getUIClassID() {
      return "TextFieldUI";
   }

   public void setDocument(Document var1) {
      if (var1 != null) {
         var1.putProperty("filterNewlines", Boolean.TRUE);
      }

      super.setDocument(var1);
   }

   public boolean isValidateRoot() {
      return !(SwingUtilities.getUnwrappedParent(this) instanceof JViewport);
   }

   public int getHorizontalAlignment() {
      return this.horizontalAlignment;
   }

   public void setHorizontalAlignment(int var1) {
      if (var1 != this.horizontalAlignment) {
         int var2 = this.horizontalAlignment;
         if (var1 != 2 && var1 != 0 && var1 != 4 && var1 != 10 && var1 != 11) {
            throw new IllegalArgumentException("horizontalAlignment");
         } else {
            this.horizontalAlignment = var1;
            this.firePropertyChange("horizontalAlignment", var2, this.horizontalAlignment);
            this.invalidate();
            this.repaint();
         }
      }
   }

   protected Document createDefaultModel() {
      return new PlainDocument();
   }

   public int getColumns() {
      return this.columns;
   }

   public void setColumns(int var1) {
      int var2 = this.columns;
      if (var1 < 0) {
         throw new IllegalArgumentException("columns less than zero.");
      } else {
         if (var1 != var2) {
            this.columns = var1;
            this.invalidate();
         }

      }
   }

   protected int getColumnWidth() {
      if (this.columnWidth == 0) {
         FontMetrics var1 = this.getFontMetrics(this.getFont());
         this.columnWidth = var1.charWidth('m');
      }

      return this.columnWidth;
   }

   public Dimension getPreferredSize() {
      Dimension var1 = super.getPreferredSize();
      if (this.columns != 0) {
         Insets var2 = this.getInsets();
         var1.width = this.columns * this.getColumnWidth() + var2.left + var2.right;
      }

      return var1;
   }

   public void setFont(Font var1) {
      super.setFont(var1);
      this.columnWidth = 0;
   }

   public synchronized void addActionListener(ActionListener var1) {
      this.listenerList.add(ActionListener.class, var1);
   }

   public synchronized void removeActionListener(ActionListener var1) {
      if (var1 != null && this.getAction() == var1) {
         this.setAction((Action)null);
      } else {
         this.listenerList.remove(ActionListener.class, var1);
      }

   }

   public synchronized ActionListener[] getActionListeners() {
      return (ActionListener[])this.listenerList.getListeners(ActionListener.class);
   }

   protected void fireActionPerformed() {
      Object[] var1 = this.listenerList.getListenerList();
      int var2 = 0;
      AWTEvent var3 = EventQueue.getCurrentEvent();
      if (var3 instanceof InputEvent) {
         var2 = ((InputEvent)var3).getModifiers();
      } else if (var3 instanceof ActionEvent) {
         var2 = ((ActionEvent)var3).getModifiers();
      }

      ActionEvent var4 = new ActionEvent(this, 1001, this.command != null ? this.command : this.getText(), EventQueue.getMostRecentEventTime(), var2);

      for(int var5 = var1.length - 2; var5 >= 0; var5 -= 2) {
         if (var1[var5] == ActionListener.class) {
            ((ActionListener)var1[var5 + 1]).actionPerformed(var4);
         }
      }

   }

   public void setActionCommand(String var1) {
      this.command = var1;
   }

   public void setAction(Action var1) {
      Action var2 = this.getAction();
      if (this.action == null || !this.action.equals(var1)) {
         this.action = var1;
         if (var2 != null) {
            this.removeActionListener(var2);
            var2.removePropertyChangeListener(this.actionPropertyChangeListener);
            this.actionPropertyChangeListener = null;
         }

         this.configurePropertiesFromAction(this.action);
         if (this.action != null) {
            if (!this.isListener(ActionListener.class, this.action)) {
               this.addActionListener(this.action);
            }

            this.actionPropertyChangeListener = this.createActionPropertyChangeListener(this.action);
            this.action.addPropertyChangeListener(this.actionPropertyChangeListener);
         }

         this.firePropertyChange("action", var2, this.action);
      }

   }

   private boolean isListener(Class var1, ActionListener var2) {
      boolean var3 = false;
      Object[] var4 = this.listenerList.getListenerList();

      for(int var5 = var4.length - 2; var5 >= 0; var5 -= 2) {
         if (var4[var5] == var1 && var4[var5 + 1] == var2) {
            var3 = true;
         }
      }

      return var3;
   }

   public Action getAction() {
      return this.action;
   }

   protected void configurePropertiesFromAction(Action var1) {
      AbstractAction.setEnabledFromAction(this, var1);
      AbstractAction.setToolTipTextFromAction(this, var1);
      this.setActionCommandFromAction(var1);
   }

   protected void actionPropertyChanged(Action var1, String var2) {
      if (var2 == "ActionCommandKey") {
         this.setActionCommandFromAction(var1);
      } else if (var2 == "enabled") {
         AbstractAction.setEnabledFromAction(this, var1);
      } else if (var2 == "ShortDescription") {
         AbstractAction.setToolTipTextFromAction(this, var1);
      }

   }

   private void setActionCommandFromAction(Action var1) {
      this.setActionCommand(var1 == null ? null : (String)var1.getValue("ActionCommandKey"));
   }

   protected PropertyChangeListener createActionPropertyChangeListener(Action var1) {
      return new JTextField.TextFieldActionPropertyChangeListener(this, var1);
   }

   public Action[] getActions() {
      return TextAction.augmentList(super.getActions(), defaultActions);
   }

   public void postActionEvent() {
      this.fireActionPerformed();
   }

   public BoundedRangeModel getHorizontalVisibility() {
      return this.visibility;
   }

   public int getScrollOffset() {
      return this.visibility.getValue();
   }

   public void setScrollOffset(int var1) {
      this.visibility.setValue(var1);
   }

   public void scrollRectToVisible(Rectangle var1) {
      Insets var2 = this.getInsets();
      int var3 = var1.x + this.visibility.getValue() - var2.left;
      int var4 = var3 + var1.width;
      if (var3 < this.visibility.getValue()) {
         this.visibility.setValue(var3);
      } else if (var4 > this.visibility.getValue() + this.visibility.getExtent()) {
         this.visibility.setValue(var4 - this.visibility.getExtent());
      }

   }

   boolean hasActionListener() {
      Object[] var1 = this.listenerList.getListenerList();

      for(int var2 = var1.length - 2; var2 >= 0; var2 -= 2) {
         if (var1[var2] == ActionListener.class) {
            return true;
         }
      }

      return false;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      if (this.getUIClassID().equals("TextFieldUI")) {
         byte var2 = JComponent.getWriteObjCounter(this);
         --var2;
         JComponent.setWriteObjCounter(this, var2);
         if (var2 == 0 && this.ui != null) {
            this.ui.installUI(this);
         }
      }

   }

   protected String paramString() {
      String var1;
      if (this.horizontalAlignment == 2) {
         var1 = "LEFT";
      } else if (this.horizontalAlignment == 0) {
         var1 = "CENTER";
      } else if (this.horizontalAlignment == 4) {
         var1 = "RIGHT";
      } else if (this.horizontalAlignment == 10) {
         var1 = "LEADING";
      } else if (this.horizontalAlignment == 11) {
         var1 = "TRAILING";
      } else {
         var1 = "";
      }

      String var2 = this.command != null ? this.command : "";
      return super.paramString() + ",columns=" + this.columns + ",columnWidth=" + this.columnWidth + ",command=" + var2 + ",horizontalAlignment=" + var1;
   }

   public AccessibleContext getAccessibleContext() {
      if (this.accessibleContext == null) {
         this.accessibleContext = new JTextField.AccessibleJTextField();
      }

      return this.accessibleContext;
   }

   protected class AccessibleJTextField extends JTextComponent.AccessibleJTextComponent {
      protected AccessibleJTextField() {
         super();
      }

      public AccessibleStateSet getAccessibleStateSet() {
         AccessibleStateSet var1 = super.getAccessibleStateSet();
         var1.add(AccessibleState.SINGLE_LINE);
         return var1;
      }
   }

   class ScrollRepainter implements ChangeListener, Serializable {
      public void stateChanged(ChangeEvent var1) {
         JTextField.this.repaint();
      }
   }

   static class NotifyAction extends TextAction {
      NotifyAction() {
         super("notify-field-accept");
      }

      public void actionPerformed(ActionEvent var1) {
         JTextComponent var2 = this.getFocusedComponent();
         if (var2 instanceof JTextField) {
            JTextField var3 = (JTextField)var2;
            var3.postActionEvent();
         }

      }

      public boolean isEnabled() {
         JTextComponent var1 = this.getFocusedComponent();
         return var1 instanceof JTextField ? ((JTextField)var1).hasActionListener() : false;
      }
   }

   private static class TextFieldActionPropertyChangeListener extends ActionPropertyChangeListener<JTextField> {
      TextFieldActionPropertyChangeListener(JTextField var1, Action var2) {
         super(var1, var2);
      }

      protected void actionPropertyChanged(JTextField var1, Action var2, PropertyChangeEvent var3) {
         if (AbstractAction.shouldReconfigure(var3)) {
            var1.configurePropertiesFromAction(var2);
         } else {
            var1.actionPropertyChanged(var2, var3.getPropertyName());
         }

      }
   }
}
