package javax.swing;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.InputMethodEvent;
import java.awt.im.InputContext;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.AttributedCharacterIterator;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.UIResource;
import javax.swing.text.AbstractDocument;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.InternationalFormatter;
import javax.swing.text.JTextComponent;
import javax.swing.text.NavigationFilter;
import javax.swing.text.NumberFormatter;
import javax.swing.text.TextAction;

public class JFormattedTextField extends JTextField {
   private static final String uiClassID = "FormattedTextFieldUI";
   private static final Action[] defaultActions = new Action[]{new JFormattedTextField.CommitAction(), new JFormattedTextField.CancelAction()};
   public static final int COMMIT = 0;
   public static final int COMMIT_OR_REVERT = 1;
   public static final int REVERT = 2;
   public static final int PERSIST = 3;
   private JFormattedTextField.AbstractFormatterFactory factory;
   private JFormattedTextField.AbstractFormatter format;
   private Object value;
   private boolean editValid;
   private int focusLostBehavior;
   private boolean edited;
   private DocumentListener documentListener;
   private Object mask;
   private ActionMap textFormatterActionMap;
   private boolean composedTextExists;
   private JFormattedTextField.FocusLostHandler focusLostHandler;

   public JFormattedTextField() {
      this.composedTextExists = false;
      this.enableEvents(4L);
      this.setFocusLostBehavior(1);
   }

   public JFormattedTextField(Object var1) {
      this();
      this.setValue(var1);
   }

   public JFormattedTextField(Format var1) {
      this();
      this.setFormatterFactory(this.getDefaultFormatterFactory(var1));
   }

   public JFormattedTextField(JFormattedTextField.AbstractFormatter var1) {
      this((JFormattedTextField.AbstractFormatterFactory)(new DefaultFormatterFactory(var1)));
   }

   public JFormattedTextField(JFormattedTextField.AbstractFormatterFactory var1) {
      this();
      this.setFormatterFactory(var1);
   }

   public JFormattedTextField(JFormattedTextField.AbstractFormatterFactory var1, Object var2) {
      this(var2);
      this.setFormatterFactory(var1);
   }

   public void setFocusLostBehavior(int var1) {
      if (var1 != 0 && var1 != 1 && var1 != 3 && var1 != 2) {
         throw new IllegalArgumentException("setFocusLostBehavior must be one of: JFormattedTextField.COMMIT, JFormattedTextField.COMMIT_OR_REVERT, JFormattedTextField.PERSIST or JFormattedTextField.REVERT");
      } else {
         this.focusLostBehavior = var1;
      }
   }

   public int getFocusLostBehavior() {
      return this.focusLostBehavior;
   }

   public void setFormatterFactory(JFormattedTextField.AbstractFormatterFactory var1) {
      JFormattedTextField.AbstractFormatterFactory var2 = this.factory;
      this.factory = var1;
      this.firePropertyChange("formatterFactory", var2, var1);
      this.setValue(this.getValue(), true, false);
   }

   public JFormattedTextField.AbstractFormatterFactory getFormatterFactory() {
      return this.factory;
   }

   protected void setFormatter(JFormattedTextField.AbstractFormatter var1) {
      JFormattedTextField.AbstractFormatter var2 = this.format;
      if (var2 != null) {
         var2.uninstall();
      }

      this.setEditValid(true);
      this.format = var1;
      if (var1 != null) {
         var1.install(this);
      }

      this.setEdited(false);
      this.firePropertyChange("textFormatter", var2, var1);
   }

   public JFormattedTextField.AbstractFormatter getFormatter() {
      return this.format;
   }

   public void setValue(Object var1) {
      if (var1 != null && this.getFormatterFactory() == null) {
         this.setFormatterFactory(this.getDefaultFormatterFactory(var1));
      }

      this.setValue(var1, true, true);
   }

   public Object getValue() {
      return this.value;
   }

   public void commitEdit() throws ParseException {
      JFormattedTextField.AbstractFormatter var1 = this.getFormatter();
      if (var1 != null) {
         this.setValue(var1.stringToValue(this.getText()), false, true);
      }

   }

   private void setEditValid(boolean var1) {
      if (var1 != this.editValid) {
         this.editValid = var1;
         this.firePropertyChange("editValid", !var1, var1);
      }

   }

   public boolean isEditValid() {
      return this.editValid;
   }

   protected void invalidEdit() {
      UIManager.getLookAndFeel().provideErrorFeedback(this);
   }

   protected void processInputMethodEvent(InputMethodEvent var1) {
      AttributedCharacterIterator var2 = var1.getText();
      int var3 = var1.getCommittedCharacterCount();
      if (var2 != null) {
         int var4 = var2.getBeginIndex();
         int var5 = var2.getEndIndex();
         this.composedTextExists = var5 - var4 > var3;
      } else {
         this.composedTextExists = false;
      }

      super.processInputMethodEvent(var1);
   }

   protected void processFocusEvent(FocusEvent var1) {
      super.processFocusEvent(var1);
      if (!var1.isTemporary()) {
         if (this.isEdited() && var1.getID() == 1005) {
            InputContext var2 = this.getInputContext();
            if (this.focusLostHandler == null) {
               this.focusLostHandler = new JFormattedTextField.FocusLostHandler();
            }

            if (var2 != null && this.composedTextExists) {
               var2.endComposition();
               EventQueue.invokeLater(this.focusLostHandler);
            } else {
               this.focusLostHandler.run();
            }
         } else if (!this.isEdited()) {
            this.setValue(this.getValue(), true, true);
         }

      }
   }

   public Action[] getActions() {
      return TextAction.augmentList(super.getActions(), defaultActions);
   }

   public String getUIClassID() {
      return "FormattedTextFieldUI";
   }

   public void setDocument(Document var1) {
      if (this.documentListener != null && this.getDocument() != null) {
         this.getDocument().removeDocumentListener(this.documentListener);
      }

      super.setDocument(var1);
      if (this.documentListener == null) {
         this.documentListener = new JFormattedTextField.DocumentHandler();
      }

      var1.addDocumentListener(this.documentListener);
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      if (this.getUIClassID().equals("FormattedTextFieldUI")) {
         byte var2 = JComponent.getWriteObjCounter(this);
         --var2;
         JComponent.setWriteObjCounter(this, var2);
         if (var2 == 0 && this.ui != null) {
            this.ui.installUI(this);
         }
      }

   }

   private void setFormatterActions(Action[] var1) {
      if (var1 == null) {
         if (this.textFormatterActionMap != null) {
            this.textFormatterActionMap.clear();
         }
      } else {
         if (this.textFormatterActionMap == null) {
            ActionMap var2 = this.getActionMap();

            ActionMap var3;
            for(this.textFormatterActionMap = new ActionMap(); var2 != null; var2 = var3) {
               var3 = var2.getParent();
               if (var3 instanceof UIResource || var3 == null) {
                  var2.setParent(this.textFormatterActionMap);
                  this.textFormatterActionMap.setParent(var3);
                  break;
               }
            }
         }

         for(int var4 = var1.length - 1; var4 >= 0; --var4) {
            Object var5 = var1[var4].getValue("Name");
            if (var5 != null) {
               this.textFormatterActionMap.put(var5, var1[var4]);
            }
         }
      }

   }

   private void setValue(Object var1, boolean var2, boolean var3) {
      Object var4 = this.value;
      this.value = var1;
      if (var2) {
         JFormattedTextField.AbstractFormatterFactory var5 = this.getFormatterFactory();
         JFormattedTextField.AbstractFormatter var6;
         if (var5 != null) {
            var6 = var5.getFormatter(this);
         } else {
            var6 = null;
         }

         this.setFormatter(var6);
      } else {
         this.setEditValid(true);
      }

      this.setEdited(false);
      if (var3) {
         this.firePropertyChange("value", var4, var1);
      }

   }

   private void setEdited(boolean var1) {
      this.edited = var1;
   }

   private boolean isEdited() {
      return this.edited;
   }

   private JFormattedTextField.AbstractFormatterFactory getDefaultFormatterFactory(Object var1) {
      if (var1 instanceof DateFormat) {
         return new DefaultFormatterFactory(new DateFormatter((DateFormat)var1));
      } else if (var1 instanceof NumberFormat) {
         return new DefaultFormatterFactory(new NumberFormatter((NumberFormat)var1));
      } else if (var1 instanceof Format) {
         return new DefaultFormatterFactory(new InternationalFormatter((Format)var1));
      } else if (var1 instanceof Date) {
         return new DefaultFormatterFactory(new DateFormatter());
      } else if (var1 instanceof Number) {
         NumberFormatter var2 = new NumberFormatter();
         ((NumberFormatter)var2).setValueClass(var1.getClass());
         NumberFormatter var3 = new NumberFormatter(new DecimalFormat("#.#"));
         ((NumberFormatter)var3).setValueClass(var1.getClass());
         return new DefaultFormatterFactory(var2, var2, var3);
      } else {
         return new DefaultFormatterFactory(new DefaultFormatter());
      }
   }

   private class DocumentHandler implements DocumentListener, Serializable {
      private DocumentHandler() {
      }

      public void insertUpdate(DocumentEvent var1) {
         JFormattedTextField.this.setEdited(true);
      }

      public void removeUpdate(DocumentEvent var1) {
         JFormattedTextField.this.setEdited(true);
      }

      public void changedUpdate(DocumentEvent var1) {
      }

      // $FF: synthetic method
      DocumentHandler(Object var2) {
         this();
      }
   }

   private static class CancelAction extends TextAction {
      public CancelAction() {
         super("reset-field-edit");
      }

      public void actionPerformed(ActionEvent var1) {
         JTextComponent var2 = this.getFocusedComponent();
         if (var2 instanceof JFormattedTextField) {
            JFormattedTextField var3 = (JFormattedTextField)var2;
            var3.setValue(var3.getValue());
         }

      }

      public boolean isEnabled() {
         JTextComponent var1 = this.getFocusedComponent();
         if (var1 instanceof JFormattedTextField) {
            JFormattedTextField var2 = (JFormattedTextField)var1;
            return var2.isEdited();
         } else {
            return super.isEnabled();
         }
      }
   }

   static class CommitAction extends JTextField.NotifyAction {
      public void actionPerformed(ActionEvent var1) {
         JTextComponent var2 = this.getFocusedComponent();
         if (var2 instanceof JFormattedTextField) {
            try {
               ((JFormattedTextField)var2).commitEdit();
            } catch (ParseException var4) {
               ((JFormattedTextField)var2).invalidEdit();
               return;
            }
         }

         super.actionPerformed(var1);
      }

      public boolean isEnabled() {
         JTextComponent var1 = this.getFocusedComponent();
         if (var1 instanceof JFormattedTextField) {
            JFormattedTextField var2 = (JFormattedTextField)var1;
            return var2.isEdited();
         } else {
            return super.isEnabled();
         }
      }
   }

   public abstract static class AbstractFormatter implements Serializable {
      private JFormattedTextField ftf;

      public void install(JFormattedTextField var1) {
         if (this.ftf != null) {
            this.uninstall();
         }

         this.ftf = var1;
         if (var1 != null) {
            try {
               var1.setText(this.valueToString(var1.getValue()));
            } catch (ParseException var3) {
               var1.setText("");
               this.setEditValid(false);
            }

            this.installDocumentFilter(this.getDocumentFilter());
            var1.setNavigationFilter(this.getNavigationFilter());
            var1.setFormatterActions(this.getActions());
         }

      }

      public void uninstall() {
         if (this.ftf != null) {
            this.installDocumentFilter((DocumentFilter)null);
            this.ftf.setNavigationFilter((NavigationFilter)null);
            this.ftf.setFormatterActions((Action[])null);
         }

      }

      public abstract Object stringToValue(String var1) throws ParseException;

      public abstract String valueToString(Object var1) throws ParseException;

      protected JFormattedTextField getFormattedTextField() {
         return this.ftf;
      }

      protected void invalidEdit() {
         JFormattedTextField var1 = this.getFormattedTextField();
         if (var1 != null) {
            var1.invalidEdit();
         }

      }

      protected void setEditValid(boolean var1) {
         JFormattedTextField var2 = this.getFormattedTextField();
         if (var2 != null) {
            var2.setEditValid(var1);
         }

      }

      protected Action[] getActions() {
         return null;
      }

      protected DocumentFilter getDocumentFilter() {
         return null;
      }

      protected NavigationFilter getNavigationFilter() {
         return null;
      }

      protected Object clone() throws CloneNotSupportedException {
         JFormattedTextField.AbstractFormatter var1 = (JFormattedTextField.AbstractFormatter)super.clone();
         var1.ftf = null;
         return var1;
      }

      private void installDocumentFilter(DocumentFilter var1) {
         JFormattedTextField var2 = this.getFormattedTextField();
         if (var2 != null) {
            Document var3 = var2.getDocument();
            if (var3 instanceof AbstractDocument) {
               ((AbstractDocument)var3).setDocumentFilter(var1);
            }

            var3.putProperty(DocumentFilter.class, (Object)null);
         }

      }
   }

   public abstract static class AbstractFormatterFactory {
      public abstract JFormattedTextField.AbstractFormatter getFormatter(JFormattedTextField var1);
   }

   private class FocusLostHandler implements Runnable, Serializable {
      private FocusLostHandler() {
      }

      public void run() {
         int var1 = JFormattedTextField.this.getFocusLostBehavior();
         if (var1 != 0 && var1 != 1) {
            if (var1 == 2) {
               JFormattedTextField.this.setValue(JFormattedTextField.this.getValue(), true, true);
            }
         } else {
            try {
               JFormattedTextField.this.commitEdit();
               JFormattedTextField.this.setValue(JFormattedTextField.this.getValue(), true, true);
            } catch (ParseException var3) {
               JFormattedTextField var10001 = JFormattedTextField.this;
               if (var1 == 1) {
                  JFormattedTextField.this.setValue(JFormattedTextField.this.getValue(), true, true);
               }
            }
         }

      }

      // $FF: synthetic method
      FocusLostHandler(Object var2) {
         this();
      }
   }
}
