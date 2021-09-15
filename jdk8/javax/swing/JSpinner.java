package javax.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.spi.DateFormatProvider;
import java.text.spi.NumberFormatProvider;
import java.util.Calendar;
import java.util.Locale;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleAction;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleEditableText;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleText;
import javax.accessibility.AccessibleValue;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.SpinnerUI;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.DocumentFilter;
import javax.swing.text.NumberFormatter;
import sun.util.locale.provider.LocaleProviderAdapter;
import sun.util.locale.provider.LocaleResources;

public class JSpinner extends JComponent implements Accessible {
   private static final String uiClassID = "SpinnerUI";
   private static final Action DISABLED_ACTION = new JSpinner.DisabledAction();
   private SpinnerModel model;
   private JComponent editor;
   private ChangeListener modelListener;
   private transient ChangeEvent changeEvent;
   private boolean editorExplicitlySet;

   public JSpinner(SpinnerModel var1) {
      this.editorExplicitlySet = false;
      if (var1 == null) {
         throw new NullPointerException("model cannot be null");
      } else {
         this.model = var1;
         this.editor = this.createEditor(var1);
         this.setUIProperty("opaque", true);
         this.updateUI();
      }
   }

   public JSpinner() {
      this(new SpinnerNumberModel());
   }

   public SpinnerUI getUI() {
      return (SpinnerUI)this.ui;
   }

   public void setUI(SpinnerUI var1) {
      super.setUI(var1);
   }

   public String getUIClassID() {
      return "SpinnerUI";
   }

   public void updateUI() {
      this.setUI((SpinnerUI)UIManager.getUI(this));
      this.invalidate();
   }

   protected JComponent createEditor(SpinnerModel var1) {
      if (var1 instanceof SpinnerDateModel) {
         return new JSpinner.DateEditor(this);
      } else if (var1 instanceof SpinnerListModel) {
         return new JSpinner.ListEditor(this);
      } else {
         return (JComponent)(var1 instanceof SpinnerNumberModel ? new JSpinner.NumberEditor(this) : new JSpinner.DefaultEditor(this));
      }
   }

   public void setModel(SpinnerModel var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("null model");
      } else {
         if (!var1.equals(this.model)) {
            SpinnerModel var2 = this.model;
            this.model = var1;
            if (this.modelListener != null) {
               var2.removeChangeListener(this.modelListener);
               this.model.addChangeListener(this.modelListener);
            }

            this.firePropertyChange("model", var2, var1);
            if (!this.editorExplicitlySet) {
               this.setEditor(this.createEditor(var1));
               this.editorExplicitlySet = false;
            }

            this.repaint();
            this.revalidate();
         }

      }
   }

   public SpinnerModel getModel() {
      return this.model;
   }

   public Object getValue() {
      return this.getModel().getValue();
   }

   public void setValue(Object var1) {
      this.getModel().setValue(var1);
   }

   public Object getNextValue() {
      return this.getModel().getNextValue();
   }

   public void addChangeListener(ChangeListener var1) {
      if (this.modelListener == null) {
         this.modelListener = new JSpinner.ModelListener();
         this.getModel().addChangeListener(this.modelListener);
      }

      this.listenerList.add(ChangeListener.class, var1);
   }

   public void removeChangeListener(ChangeListener var1) {
      this.listenerList.remove(ChangeListener.class, var1);
   }

   public ChangeListener[] getChangeListeners() {
      return (ChangeListener[])this.listenerList.getListeners(ChangeListener.class);
   }

   protected void fireStateChanged() {
      Object[] var1 = this.listenerList.getListenerList();

      for(int var2 = var1.length - 2; var2 >= 0; var2 -= 2) {
         if (var1[var2] == ChangeListener.class) {
            if (this.changeEvent == null) {
               this.changeEvent = new ChangeEvent(this);
            }

            ((ChangeListener)var1[var2 + 1]).stateChanged(this.changeEvent);
         }
      }

   }

   public Object getPreviousValue() {
      return this.getModel().getPreviousValue();
   }

   public void setEditor(JComponent var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("null editor");
      } else {
         if (!var1.equals(this.editor)) {
            JComponent var2 = this.editor;
            this.editor = var1;
            if (var2 instanceof JSpinner.DefaultEditor) {
               ((JSpinner.DefaultEditor)var2).dismiss(this);
            }

            this.editorExplicitlySet = true;
            this.firePropertyChange("editor", var2, var1);
            this.revalidate();
            this.repaint();
         }

      }
   }

   public JComponent getEditor() {
      return this.editor;
   }

   public void commitEdit() throws ParseException {
      JComponent var1 = this.getEditor();
      if (var1 instanceof JSpinner.DefaultEditor) {
         ((JSpinner.DefaultEditor)var1).commitEdit();
      }

   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      if (this.getUIClassID().equals("SpinnerUI")) {
         byte var2 = JComponent.getWriteObjCounter(this);
         --var2;
         JComponent.setWriteObjCounter(this, var2);
         if (var2 == 0 && this.ui != null) {
            this.ui.installUI(this);
         }
      }

   }

   public AccessibleContext getAccessibleContext() {
      if (this.accessibleContext == null) {
         this.accessibleContext = new JSpinner.AccessibleJSpinner();
      }

      return this.accessibleContext;
   }

   protected class AccessibleJSpinner extends JComponent.AccessibleJComponent implements AccessibleValue, AccessibleAction, AccessibleText, AccessibleEditableText, ChangeListener {
      private Object oldModelValue = null;

      protected AccessibleJSpinner() {
         super();
         this.oldModelValue = JSpinner.this.model.getValue();
         JSpinner.this.addChangeListener(this);
      }

      public void stateChanged(ChangeEvent var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            Object var2 = JSpinner.this.model.getValue();
            this.firePropertyChange("AccessibleValue", this.oldModelValue, var2);
            this.firePropertyChange("AccessibleText", (Object)null, 0);
            this.oldModelValue = var2;
         }
      }

      public AccessibleRole getAccessibleRole() {
         return AccessibleRole.SPIN_BOX;
      }

      public int getAccessibleChildrenCount() {
         return JSpinner.this.editor.getAccessibleContext() != null ? 1 : 0;
      }

      public Accessible getAccessibleChild(int var1) {
         if (var1 != 0) {
            return null;
         } else {
            return JSpinner.this.editor.getAccessibleContext() != null ? (Accessible)JSpinner.this.editor : null;
         }
      }

      public AccessibleAction getAccessibleAction() {
         return this;
      }

      public AccessibleText getAccessibleText() {
         return this;
      }

      private AccessibleContext getEditorAccessibleContext() {
         if (JSpinner.this.editor instanceof JSpinner.DefaultEditor) {
            JFormattedTextField var1 = ((JSpinner.DefaultEditor)JSpinner.this.editor).getTextField();
            if (var1 != null) {
               return var1.getAccessibleContext();
            }
         } else if (JSpinner.this.editor instanceof Accessible) {
            return JSpinner.this.editor.getAccessibleContext();
         }

         return null;
      }

      private AccessibleText getEditorAccessibleText() {
         AccessibleContext var1 = this.getEditorAccessibleContext();
         return var1 != null ? var1.getAccessibleText() : null;
      }

      private AccessibleEditableText getEditorAccessibleEditableText() {
         AccessibleText var1 = this.getEditorAccessibleText();
         return var1 instanceof AccessibleEditableText ? (AccessibleEditableText)var1 : null;
      }

      public AccessibleValue getAccessibleValue() {
         return this;
      }

      public Number getCurrentAccessibleValue() {
         Object var1 = JSpinner.this.model.getValue();
         return var1 instanceof Number ? (Number)var1 : null;
      }

      public boolean setCurrentAccessibleValue(Number var1) {
         try {
            JSpinner.this.model.setValue(var1);
            return true;
         } catch (IllegalArgumentException var3) {
            return false;
         }
      }

      public Number getMinimumAccessibleValue() {
         if (JSpinner.this.model instanceof SpinnerNumberModel) {
            SpinnerNumberModel var1 = (SpinnerNumberModel)JSpinner.this.model;
            Comparable var2 = var1.getMinimum();
            if (var2 instanceof Number) {
               return (Number)var2;
            }
         }

         return null;
      }

      public Number getMaximumAccessibleValue() {
         if (JSpinner.this.model instanceof SpinnerNumberModel) {
            SpinnerNumberModel var1 = (SpinnerNumberModel)JSpinner.this.model;
            Comparable var2 = var1.getMaximum();
            if (var2 instanceof Number) {
               return (Number)var2;
            }
         }

         return null;
      }

      public int getAccessibleActionCount() {
         return 2;
      }

      public String getAccessibleActionDescription(int var1) {
         if (var1 == 0) {
            return AccessibleAction.INCREMENT;
         } else {
            return var1 == 1 ? AccessibleAction.DECREMENT : null;
         }
      }

      public boolean doAccessibleAction(int var1) {
         if (var1 >= 0 && var1 <= 1) {
            Object var2;
            if (var1 == 0) {
               var2 = JSpinner.this.getNextValue();
            } else {
               var2 = JSpinner.this.getPreviousValue();
            }

            try {
               JSpinner.this.model.setValue(var2);
               return true;
            } catch (IllegalArgumentException var4) {
               return false;
            }
         } else {
            return false;
         }
      }

      private boolean sameWindowAncestor(Component var1, Component var2) {
         if (var1 != null && var2 != null) {
            return SwingUtilities.getWindowAncestor(var1) == SwingUtilities.getWindowAncestor(var2);
         } else {
            return false;
         }
      }

      public int getIndexAtPoint(Point var1) {
         AccessibleText var2 = this.getEditorAccessibleText();
         if (var2 != null && this.sameWindowAncestor(JSpinner.this, JSpinner.this.editor)) {
            Point var3 = SwingUtilities.convertPoint(JSpinner.this, var1, JSpinner.this.editor);
            if (var3 != null) {
               return var2.getIndexAtPoint(var3);
            }
         }

         return -1;
      }

      public Rectangle getCharacterBounds(int var1) {
         AccessibleText var2 = this.getEditorAccessibleText();
         if (var2 != null) {
            Rectangle var3 = var2.getCharacterBounds(var1);
            if (var3 != null && this.sameWindowAncestor(JSpinner.this, JSpinner.this.editor)) {
               return SwingUtilities.convertRectangle(JSpinner.this.editor, var3, JSpinner.this);
            }
         }

         return null;
      }

      public int getCharCount() {
         AccessibleText var1 = this.getEditorAccessibleText();
         return var1 != null ? var1.getCharCount() : -1;
      }

      public int getCaretPosition() {
         AccessibleText var1 = this.getEditorAccessibleText();
         return var1 != null ? var1.getCaretPosition() : -1;
      }

      public String getAtIndex(int var1, int var2) {
         AccessibleText var3 = this.getEditorAccessibleText();
         return var3 != null ? var3.getAtIndex(var1, var2) : null;
      }

      public String getAfterIndex(int var1, int var2) {
         AccessibleText var3 = this.getEditorAccessibleText();
         return var3 != null ? var3.getAfterIndex(var1, var2) : null;
      }

      public String getBeforeIndex(int var1, int var2) {
         AccessibleText var3 = this.getEditorAccessibleText();
         return var3 != null ? var3.getBeforeIndex(var1, var2) : null;
      }

      public AttributeSet getCharacterAttribute(int var1) {
         AccessibleText var2 = this.getEditorAccessibleText();
         return var2 != null ? var2.getCharacterAttribute(var1) : null;
      }

      public int getSelectionStart() {
         AccessibleText var1 = this.getEditorAccessibleText();
         return var1 != null ? var1.getSelectionStart() : -1;
      }

      public int getSelectionEnd() {
         AccessibleText var1 = this.getEditorAccessibleText();
         return var1 != null ? var1.getSelectionEnd() : -1;
      }

      public String getSelectedText() {
         AccessibleText var1 = this.getEditorAccessibleText();
         return var1 != null ? var1.getSelectedText() : null;
      }

      public void setTextContents(String var1) {
         AccessibleEditableText var2 = this.getEditorAccessibleEditableText();
         if (var2 != null) {
            var2.setTextContents(var1);
         }

      }

      public void insertTextAtIndex(int var1, String var2) {
         AccessibleEditableText var3 = this.getEditorAccessibleEditableText();
         if (var3 != null) {
            var3.insertTextAtIndex(var1, var2);
         }

      }

      public String getTextRange(int var1, int var2) {
         AccessibleEditableText var3 = this.getEditorAccessibleEditableText();
         return var3 != null ? var3.getTextRange(var1, var2) : null;
      }

      public void delete(int var1, int var2) {
         AccessibleEditableText var3 = this.getEditorAccessibleEditableText();
         if (var3 != null) {
            var3.delete(var1, var2);
         }

      }

      public void cut(int var1, int var2) {
         AccessibleEditableText var3 = this.getEditorAccessibleEditableText();
         if (var3 != null) {
            var3.cut(var1, var2);
         }

      }

      public void paste(int var1) {
         AccessibleEditableText var2 = this.getEditorAccessibleEditableText();
         if (var2 != null) {
            var2.paste(var1);
         }

      }

      public void replaceText(int var1, int var2, String var3) {
         AccessibleEditableText var4 = this.getEditorAccessibleEditableText();
         if (var4 != null) {
            var4.replaceText(var1, var2, var3);
         }

      }

      public void selectText(int var1, int var2) {
         AccessibleEditableText var3 = this.getEditorAccessibleEditableText();
         if (var3 != null) {
            var3.selectText(var1, var2);
         }

      }

      public void setAttributes(int var1, int var2, AttributeSet var3) {
         AccessibleEditableText var4 = this.getEditorAccessibleEditableText();
         if (var4 != null) {
            var4.setAttributes(var1, var2, var3);
         }

      }
   }

   private static class DisabledAction implements Action {
      private DisabledAction() {
      }

      public Object getValue(String var1) {
         return null;
      }

      public void putValue(String var1, Object var2) {
      }

      public void setEnabled(boolean var1) {
      }

      public boolean isEnabled() {
         return false;
      }

      public void addPropertyChangeListener(PropertyChangeListener var1) {
      }

      public void removePropertyChangeListener(PropertyChangeListener var1) {
      }

      public void actionPerformed(ActionEvent var1) {
      }

      // $FF: synthetic method
      DisabledAction(Object var1) {
         this();
      }
   }

   public static class ListEditor extends JSpinner.DefaultEditor {
      public ListEditor(JSpinner var1) {
         super(var1);
         if (!(var1.getModel() instanceof SpinnerListModel)) {
            throw new IllegalArgumentException("model not a SpinnerListModel");
         } else {
            this.getTextField().setEditable(true);
            this.getTextField().setFormatterFactory(new DefaultFormatterFactory(new JSpinner.ListEditor.ListFormatter()));
         }
      }

      public SpinnerListModel getModel() {
         return (SpinnerListModel)((SpinnerListModel)this.getSpinner().getModel());
      }

      private class ListFormatter extends JFormattedTextField.AbstractFormatter {
         private DocumentFilter filter;

         private ListFormatter() {
         }

         public String valueToString(Object var1) throws ParseException {
            return var1 == null ? "" : var1.toString();
         }

         public Object stringToValue(String var1) throws ParseException {
            return var1;
         }

         protected DocumentFilter getDocumentFilter() {
            if (this.filter == null) {
               this.filter = new JSpinner.ListEditor.ListFormatter.Filter();
            }

            return this.filter;
         }

         // $FF: synthetic method
         ListFormatter(Object var2) {
            this();
         }

         private class Filter extends DocumentFilter {
            private Filter() {
            }

            public void replace(DocumentFilter.FilterBypass var1, int var2, int var3, String var4, AttributeSet var5) throws BadLocationException {
               if (var4 != null && var2 + var3 == var1.getDocument().getLength()) {
                  Object var6 = ListEditor.this.getModel().findNextMatch(var1.getDocument().getText(0, var2) + var4);
                  String var7 = var6 != null ? var6.toString() : null;
                  if (var7 != null) {
                     var1.remove(0, var2 + var3);
                     var1.insertString(0, var7, (AttributeSet)null);
                     ListFormatter.this.getFormattedTextField().select(var2 + var4.length(), var7.length());
                     return;
                  }
               }

               super.replace(var1, var2, var3, var4, var5);
            }

            public void insertString(DocumentFilter.FilterBypass var1, int var2, String var3, AttributeSet var4) throws BadLocationException {
               this.replace(var1, var2, 0, var3, var4);
            }

            // $FF: synthetic method
            Filter(Object var2) {
               this();
            }
         }
      }
   }

   public static class NumberEditor extends JSpinner.DefaultEditor {
      private static String getDefaultPattern(Locale var0) {
         LocaleProviderAdapter var1 = LocaleProviderAdapter.getAdapter(NumberFormatProvider.class, var0);
         LocaleResources var2 = var1.getLocaleResources(var0);
         if (var2 == null) {
            var2 = LocaleProviderAdapter.forJRE().getLocaleResources(var0);
         }

         String[] var3 = var2.getNumberPatterns();
         return var3[0];
      }

      public NumberEditor(JSpinner var1) {
         this(var1, getDefaultPattern(var1.getLocale()));
      }

      public NumberEditor(JSpinner var1, String var2) {
         this(var1, new DecimalFormat(var2));
      }

      private NumberEditor(JSpinner var1, DecimalFormat var2) {
         super(var1);
         if (!(var1.getModel() instanceof SpinnerNumberModel)) {
            throw new IllegalArgumentException("model not a SpinnerNumberModel");
         } else {
            SpinnerNumberModel var3 = (SpinnerNumberModel)var1.getModel();
            JSpinner.NumberEditorFormatter var4 = new JSpinner.NumberEditorFormatter(var3, var2);
            DefaultFormatterFactory var5 = new DefaultFormatterFactory(var4);
            JFormattedTextField var6 = this.getTextField();
            var6.setEditable(true);
            var6.setFormatterFactory(var5);
            var6.setHorizontalAlignment(4);

            try {
               String var7 = var4.valueToString(var3.getMinimum());
               String var8 = var4.valueToString(var3.getMaximum());
               var6.setColumns(Math.max(var7.length(), var8.length()));
            } catch (ParseException var9) {
            }

         }
      }

      public DecimalFormat getFormat() {
         return (DecimalFormat)((NumberFormatter)((NumberFormatter)this.getTextField().getFormatter())).getFormat();
      }

      public SpinnerNumberModel getModel() {
         return (SpinnerNumberModel)((SpinnerNumberModel)this.getSpinner().getModel());
      }
   }

   private static class NumberEditorFormatter extends NumberFormatter {
      private final SpinnerNumberModel model;

      NumberEditorFormatter(SpinnerNumberModel var1, NumberFormat var2) {
         super(var2);
         this.model = var1;
         this.setValueClass(var1.getValue().getClass());
      }

      public void setMinimum(Comparable var1) {
         this.model.setMinimum(var1);
      }

      public Comparable getMinimum() {
         return this.model.getMinimum();
      }

      public void setMaximum(Comparable var1) {
         this.model.setMaximum(var1);
      }

      public Comparable getMaximum() {
         return this.model.getMaximum();
      }
   }

   public static class DateEditor extends JSpinner.DefaultEditor {
      private static String getDefaultPattern(Locale var0) {
         LocaleProviderAdapter var1 = LocaleProviderAdapter.getAdapter(DateFormatProvider.class, var0);
         LocaleResources var2 = var1.getLocaleResources(var0);
         if (var2 == null) {
            var2 = LocaleProviderAdapter.forJRE().getLocaleResources(var0);
         }

         return var2.getDateTimePattern(3, 3, (Calendar)null);
      }

      public DateEditor(JSpinner var1) {
         this(var1, getDefaultPattern(var1.getLocale()));
      }

      public DateEditor(JSpinner var1, String var2) {
         this(var1, (DateFormat)(new SimpleDateFormat(var2, var1.getLocale())));
      }

      private DateEditor(JSpinner var1, DateFormat var2) {
         super(var1);
         if (!(var1.getModel() instanceof SpinnerDateModel)) {
            throw new IllegalArgumentException("model not a SpinnerDateModel");
         } else {
            SpinnerDateModel var3 = (SpinnerDateModel)var1.getModel();
            JSpinner.DateEditorFormatter var4 = new JSpinner.DateEditorFormatter(var3, var2);
            DefaultFormatterFactory var5 = new DefaultFormatterFactory(var4);
            JFormattedTextField var6 = this.getTextField();
            var6.setEditable(true);
            var6.setFormatterFactory(var5);

            try {
               String var7 = var4.valueToString(var3.getStart());
               String var8 = var4.valueToString(var3.getEnd());
               var6.setColumns(Math.max(var7.length(), var8.length()));
            } catch (ParseException var9) {
            }

         }
      }

      public SimpleDateFormat getFormat() {
         return (SimpleDateFormat)((DateFormatter)((DateFormatter)this.getTextField().getFormatter())).getFormat();
      }

      public SpinnerDateModel getModel() {
         return (SpinnerDateModel)((SpinnerDateModel)this.getSpinner().getModel());
      }
   }

   private static class DateEditorFormatter extends DateFormatter {
      private final SpinnerDateModel model;

      DateEditorFormatter(SpinnerDateModel var1, DateFormat var2) {
         super(var2);
         this.model = var1;
      }

      public void setMinimum(Comparable var1) {
         this.model.setStart(var1);
      }

      public Comparable getMinimum() {
         return this.model.getStart();
      }

      public void setMaximum(Comparable var1) {
         this.model.setEnd(var1);
      }

      public Comparable getMaximum() {
         return this.model.getEnd();
      }
   }

   public static class DefaultEditor extends JPanel implements ChangeListener, PropertyChangeListener, LayoutManager {
      public DefaultEditor(JSpinner var1) {
         super((LayoutManager)null);
         JFormattedTextField var2 = new JFormattedTextField();
         var2.setName("Spinner.formattedTextField");
         var2.setValue(var1.getValue());
         var2.addPropertyChangeListener(this);
         var2.setEditable(false);
         var2.setInheritsPopupMenu(true);
         String var3 = var1.getToolTipText();
         if (var3 != null) {
            var2.setToolTipText(var3);
         }

         this.add(var2);
         this.setLayout(this);
         var1.addChangeListener(this);
         ActionMap var4 = var2.getActionMap();
         if (var4 != null) {
            var4.put("increment", JSpinner.DISABLED_ACTION);
            var4.put("decrement", JSpinner.DISABLED_ACTION);
         }

      }

      public void dismiss(JSpinner var1) {
         var1.removeChangeListener(this);
      }

      public JSpinner getSpinner() {
         for(Object var1 = this; var1 != null; var1 = ((Component)var1).getParent()) {
            if (var1 instanceof JSpinner) {
               return (JSpinner)var1;
            }
         }

         return null;
      }

      public JFormattedTextField getTextField() {
         return (JFormattedTextField)this.getComponent(0);
      }

      public void stateChanged(ChangeEvent var1) {
         JSpinner var2 = (JSpinner)((JSpinner)var1.getSource());
         this.getTextField().setValue(var2.getValue());
      }

      public void propertyChange(PropertyChangeEvent var1) {
         JSpinner var2 = this.getSpinner();
         if (var2 != null) {
            Object var3 = var1.getSource();
            String var4 = var1.getPropertyName();
            if (var3 instanceof JFormattedTextField && "value".equals(var4)) {
               Object var5 = var2.getValue();

               try {
                  var2.setValue(this.getTextField().getValue());
               } catch (IllegalArgumentException var9) {
                  try {
                     ((JFormattedTextField)var3).setValue(var5);
                  } catch (IllegalArgumentException var8) {
                  }
               }
            }

         }
      }

      public void addLayoutComponent(String var1, Component var2) {
      }

      public void removeLayoutComponent(Component var1) {
      }

      private Dimension insetSize(Container var1) {
         Insets var2 = var1.getInsets();
         int var3 = var2.left + var2.right;
         int var4 = var2.top + var2.bottom;
         return new Dimension(var3, var4);
      }

      public Dimension preferredLayoutSize(Container var1) {
         Dimension var2 = this.insetSize(var1);
         if (var1.getComponentCount() > 0) {
            Dimension var3 = this.getComponent(0).getPreferredSize();
            var2.width += var3.width;
            var2.height += var3.height;
         }

         return var2;
      }

      public Dimension minimumLayoutSize(Container var1) {
         Dimension var2 = this.insetSize(var1);
         if (var1.getComponentCount() > 0) {
            Dimension var3 = this.getComponent(0).getMinimumSize();
            var2.width += var3.width;
            var2.height += var3.height;
         }

         return var2;
      }

      public void layoutContainer(Container var1) {
         if (var1.getComponentCount() > 0) {
            Insets var2 = var1.getInsets();
            int var3 = var1.getWidth() - (var2.left + var2.right);
            int var4 = var1.getHeight() - (var2.top + var2.bottom);
            this.getComponent(0).setBounds(var2.left, var2.top, var3, var4);
         }

      }

      public void commitEdit() throws ParseException {
         JFormattedTextField var1 = this.getTextField();
         var1.commitEdit();
      }

      public int getBaseline(int var1, int var2) {
         super.getBaseline(var1, var2);
         Insets var3 = this.getInsets();
         var1 = var1 - var3.left - var3.right;
         var2 = var2 - var3.top - var3.bottom;
         int var4 = this.getComponent(0).getBaseline(var1, var2);
         return var4 >= 0 ? var4 + var3.top : -1;
      }

      public Component.BaselineResizeBehavior getBaselineResizeBehavior() {
         return this.getComponent(0).getBaselineResizeBehavior();
      }
   }

   private class ModelListener implements ChangeListener, Serializable {
      private ModelListener() {
      }

      public void stateChanged(ChangeEvent var1) {
         JSpinner.this.fireStateChanged();
      }

      // $FF: synthetic method
      ModelListener(Object var2) {
         this();
      }
   }
}
