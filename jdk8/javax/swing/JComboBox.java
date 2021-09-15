package javax.swing;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.IllegalComponentStateException;
import java.awt.ItemSelectable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.Transient;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Locale;
import java.util.Vector;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleAction;
import javax.accessibility.AccessibleComponent;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleEditableText;
import javax.accessibility.AccessibleIcon;
import javax.accessibility.AccessibleRelationSet;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleSelection;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleTable;
import javax.accessibility.AccessibleText;
import javax.accessibility.AccessibleValue;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.ComboBoxUI;
import javax.swing.plaf.basic.ComboPopup;

public class JComboBox<E> extends JComponent implements ItemSelectable, ListDataListener, ActionListener, Accessible {
   private static final String uiClassID = "ComboBoxUI";
   protected ComboBoxModel<E> dataModel;
   protected ListCellRenderer<? super E> renderer;
   protected ComboBoxEditor editor;
   protected int maximumRowCount = 8;
   protected boolean isEditable = false;
   protected JComboBox.KeySelectionManager keySelectionManager = null;
   protected String actionCommand = "comboBoxChanged";
   protected boolean lightWeightPopupEnabled = JPopupMenu.getDefaultLightWeightPopupEnabled();
   protected Object selectedItemReminder = null;
   private E prototypeDisplayValue;
   private boolean firingActionEvent = false;
   private boolean selectingItem = false;
   private Action action;
   private PropertyChangeListener actionPropertyChangeListener;

   public JComboBox(ComboBoxModel<E> var1) {
      this.setModel(var1);
      this.init();
   }

   public JComboBox(E[] var1) {
      this.setModel(new DefaultComboBoxModel(var1));
      this.init();
   }

   public JComboBox(Vector<E> var1) {
      this.setModel(new DefaultComboBoxModel(var1));
      this.init();
   }

   public JComboBox() {
      this.setModel(new DefaultComboBoxModel());
      this.init();
   }

   private void init() {
      this.installAncestorListener();
      this.setUIProperty("opaque", true);
      this.updateUI();
   }

   protected void installAncestorListener() {
      this.addAncestorListener(new AncestorListener() {
         public void ancestorAdded(AncestorEvent var1) {
            JComboBox.this.hidePopup();
         }

         public void ancestorRemoved(AncestorEvent var1) {
            JComboBox.this.hidePopup();
         }

         public void ancestorMoved(AncestorEvent var1) {
            if (var1.getSource() != JComboBox.this) {
               JComboBox.this.hidePopup();
            }

         }
      });
   }

   public void setUI(ComboBoxUI var1) {
      super.setUI(var1);
   }

   public void updateUI() {
      this.setUI((ComboBoxUI)UIManager.getUI(this));
      ListCellRenderer var1 = this.getRenderer();
      if (var1 instanceof Component) {
         SwingUtilities.updateComponentTreeUI((Component)var1);
      }

   }

   public String getUIClassID() {
      return "ComboBoxUI";
   }

   public ComboBoxUI getUI() {
      return (ComboBoxUI)this.ui;
   }

   public void setModel(ComboBoxModel<E> var1) {
      ComboBoxModel var2 = this.dataModel;
      if (var2 != null) {
         var2.removeListDataListener(this);
      }

      this.dataModel = var1;
      this.dataModel.addListDataListener(this);
      this.selectedItemReminder = this.dataModel.getSelectedItem();
      this.firePropertyChange("model", var2, this.dataModel);
   }

   public ComboBoxModel<E> getModel() {
      return this.dataModel;
   }

   public void setLightWeightPopupEnabled(boolean var1) {
      boolean var2 = this.lightWeightPopupEnabled;
      this.lightWeightPopupEnabled = var1;
      this.firePropertyChange("lightWeightPopupEnabled", var2, this.lightWeightPopupEnabled);
   }

   public boolean isLightWeightPopupEnabled() {
      return this.lightWeightPopupEnabled;
   }

   public void setEditable(boolean var1) {
      boolean var2 = this.isEditable;
      this.isEditable = var1;
      this.firePropertyChange("editable", var2, this.isEditable);
   }

   public boolean isEditable() {
      return this.isEditable;
   }

   public void setMaximumRowCount(int var1) {
      int var2 = this.maximumRowCount;
      this.maximumRowCount = var1;
      this.firePropertyChange("maximumRowCount", var2, this.maximumRowCount);
   }

   public int getMaximumRowCount() {
      return this.maximumRowCount;
   }

   public void setRenderer(ListCellRenderer<? super E> var1) {
      ListCellRenderer var2 = this.renderer;
      this.renderer = var1;
      this.firePropertyChange("renderer", var2, this.renderer);
      this.invalidate();
   }

   public ListCellRenderer<? super E> getRenderer() {
      return this.renderer;
   }

   public void setEditor(ComboBoxEditor var1) {
      ComboBoxEditor var2 = this.editor;
      if (this.editor != null) {
         this.editor.removeActionListener(this);
      }

      this.editor = var1;
      if (this.editor != null) {
         this.editor.addActionListener(this);
      }

      this.firePropertyChange("editor", var2, this.editor);
   }

   public ComboBoxEditor getEditor() {
      return this.editor;
   }

   public void setSelectedItem(Object var1) {
      Object var2 = this.selectedItemReminder;
      Object var3 = var1;
      if (var2 == null || !var2.equals(var1)) {
         if (var1 != null && !this.isEditable()) {
            boolean var4 = false;

            for(int var5 = 0; var5 < this.dataModel.getSize(); ++var5) {
               Object var6 = this.dataModel.getElementAt(var5);
               if (var1.equals(var6)) {
                  var4 = true;
                  var3 = var6;
                  break;
               }
            }

            if (!var4) {
               return;
            }
         }

         this.selectingItem = true;
         this.dataModel.setSelectedItem(var3);
         this.selectingItem = false;
         if (this.selectedItemReminder != this.dataModel.getSelectedItem()) {
            this.selectedItemChanged();
         }
      }

      this.fireActionEvent();
   }

   public Object getSelectedItem() {
      return this.dataModel.getSelectedItem();
   }

   public void setSelectedIndex(int var1) {
      int var2 = this.dataModel.getSize();
      if (var1 == -1) {
         this.setSelectedItem((Object)null);
      } else {
         if (var1 < -1 || var1 >= var2) {
            throw new IllegalArgumentException("setSelectedIndex: " + var1 + " out of bounds");
         }

         this.setSelectedItem(this.dataModel.getElementAt(var1));
      }

   }

   @Transient
   public int getSelectedIndex() {
      Object var1 = this.dataModel.getSelectedItem();
      int var2 = 0;

      for(int var3 = this.dataModel.getSize(); var2 < var3; ++var2) {
         Object var4 = this.dataModel.getElementAt(var2);
         if (var4 != null && var4.equals(var1)) {
            return var2;
         }
      }

      return -1;
   }

   public E getPrototypeDisplayValue() {
      return this.prototypeDisplayValue;
   }

   public void setPrototypeDisplayValue(E var1) {
      Object var2 = this.prototypeDisplayValue;
      this.prototypeDisplayValue = var1;
      this.firePropertyChange("prototypeDisplayValue", var2, var1);
   }

   public void addItem(E var1) {
      this.checkMutableComboBoxModel();
      ((MutableComboBoxModel)this.dataModel).addElement(var1);
   }

   public void insertItemAt(E var1, int var2) {
      this.checkMutableComboBoxModel();
      ((MutableComboBoxModel)this.dataModel).insertElementAt(var1, var2);
   }

   public void removeItem(Object var1) {
      this.checkMutableComboBoxModel();
      ((MutableComboBoxModel)this.dataModel).removeElement(var1);
   }

   public void removeItemAt(int var1) {
      this.checkMutableComboBoxModel();
      ((MutableComboBoxModel)this.dataModel).removeElementAt(var1);
   }

   public void removeAllItems() {
      this.checkMutableComboBoxModel();
      MutableComboBoxModel var1 = (MutableComboBoxModel)this.dataModel;
      int var2 = var1.getSize();
      if (var1 instanceof DefaultComboBoxModel) {
         ((DefaultComboBoxModel)var1).removeAllElements();
      } else {
         for(int var3 = 0; var3 < var2; ++var3) {
            Object var4 = var1.getElementAt(0);
            var1.removeElement(var4);
         }
      }

      this.selectedItemReminder = null;
      if (this.isEditable()) {
         this.editor.setItem((Object)null);
      }

   }

   void checkMutableComboBoxModel() {
      if (!(this.dataModel instanceof MutableComboBoxModel)) {
         throw new RuntimeException("Cannot use this method with a non-Mutable data model.");
      }
   }

   public void showPopup() {
      this.setPopupVisible(true);
   }

   public void hidePopup() {
      this.setPopupVisible(false);
   }

   public void setPopupVisible(boolean var1) {
      this.getUI().setPopupVisible(this, var1);
   }

   public boolean isPopupVisible() {
      return this.getUI().isPopupVisible(this);
   }

   public void addItemListener(ItemListener var1) {
      this.listenerList.add(ItemListener.class, var1);
   }

   public void removeItemListener(ItemListener var1) {
      this.listenerList.remove(ItemListener.class, var1);
   }

   public ItemListener[] getItemListeners() {
      return (ItemListener[])this.listenerList.getListeners(ItemListener.class);
   }

   public void addActionListener(ActionListener var1) {
      this.listenerList.add(ActionListener.class, var1);
   }

   public void removeActionListener(ActionListener var1) {
      if (var1 != null && this.getAction() == var1) {
         this.setAction((Action)null);
      } else {
         this.listenerList.remove(ActionListener.class, var1);
      }

   }

   public ActionListener[] getActionListeners() {
      return (ActionListener[])this.listenerList.getListeners(ActionListener.class);
   }

   public void addPopupMenuListener(PopupMenuListener var1) {
      this.listenerList.add(PopupMenuListener.class, var1);
   }

   public void removePopupMenuListener(PopupMenuListener var1) {
      this.listenerList.remove(PopupMenuListener.class, var1);
   }

   public PopupMenuListener[] getPopupMenuListeners() {
      return (PopupMenuListener[])this.listenerList.getListeners(PopupMenuListener.class);
   }

   public void firePopupMenuWillBecomeVisible() {
      Object[] var1 = this.listenerList.getListenerList();
      PopupMenuEvent var2 = null;

      for(int var3 = var1.length - 2; var3 >= 0; var3 -= 2) {
         if (var1[var3] == PopupMenuListener.class) {
            if (var2 == null) {
               var2 = new PopupMenuEvent(this);
            }

            ((PopupMenuListener)var1[var3 + 1]).popupMenuWillBecomeVisible(var2);
         }
      }

   }

   public void firePopupMenuWillBecomeInvisible() {
      Object[] var1 = this.listenerList.getListenerList();
      PopupMenuEvent var2 = null;

      for(int var3 = var1.length - 2; var3 >= 0; var3 -= 2) {
         if (var1[var3] == PopupMenuListener.class) {
            if (var2 == null) {
               var2 = new PopupMenuEvent(this);
            }

            ((PopupMenuListener)var1[var3 + 1]).popupMenuWillBecomeInvisible(var2);
         }
      }

   }

   public void firePopupMenuCanceled() {
      Object[] var1 = this.listenerList.getListenerList();
      PopupMenuEvent var2 = null;

      for(int var3 = var1.length - 2; var3 >= 0; var3 -= 2) {
         if (var1[var3] == PopupMenuListener.class) {
            if (var2 == null) {
               var2 = new PopupMenuEvent(this);
            }

            ((PopupMenuListener)var1[var3 + 1]).popupMenuCanceled(var2);
         }
      }

   }

   public void setActionCommand(String var1) {
      this.actionCommand = var1;
   }

   public String getActionCommand() {
      return this.actionCommand;
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

   protected PropertyChangeListener createActionPropertyChangeListener(Action var1) {
      return new JComboBox.ComboBoxActionPropertyChangeListener(this, var1);
   }

   protected void actionPropertyChanged(Action var1, String var2) {
      if (var2 == "ActionCommandKey") {
         this.setActionCommandFromAction(var1);
      } else if (var2 == "enabled") {
         AbstractAction.setEnabledFromAction(this, var1);
      } else if ("ShortDescription" == var2) {
         AbstractAction.setToolTipTextFromAction(this, var1);
      }

   }

   private void setActionCommandFromAction(Action var1) {
      this.setActionCommand(var1 != null ? (String)var1.getValue("ActionCommandKey") : null);
   }

   protected void fireItemStateChanged(ItemEvent var1) {
      Object[] var2 = this.listenerList.getListenerList();

      for(int var3 = var2.length - 2; var3 >= 0; var3 -= 2) {
         if (var2[var3] == ItemListener.class) {
            ((ItemListener)var2[var3 + 1]).itemStateChanged(var1);
         }
      }

   }

   protected void fireActionEvent() {
      if (!this.firingActionEvent) {
         this.firingActionEvent = true;
         ActionEvent var1 = null;
         Object[] var2 = this.listenerList.getListenerList();
         long var3 = EventQueue.getMostRecentEventTime();
         int var5 = 0;
         AWTEvent var6 = EventQueue.getCurrentEvent();
         if (var6 instanceof InputEvent) {
            var5 = ((InputEvent)var6).getModifiers();
         } else if (var6 instanceof ActionEvent) {
            var5 = ((ActionEvent)var6).getModifiers();
         }

         for(int var7 = var2.length - 2; var7 >= 0; var7 -= 2) {
            if (var2[var7] == ActionListener.class) {
               if (var1 == null) {
                  var1 = new ActionEvent(this, 1001, this.getActionCommand(), var3, var5);
               }

               ((ActionListener)var2[var7 + 1]).actionPerformed(var1);
            }
         }

         this.firingActionEvent = false;
      }

   }

   protected void selectedItemChanged() {
      if (this.selectedItemReminder != null) {
         this.fireItemStateChanged(new ItemEvent(this, 701, this.selectedItemReminder, 2));
      }

      this.selectedItemReminder = this.dataModel.getSelectedItem();
      if (this.selectedItemReminder != null) {
         this.fireItemStateChanged(new ItemEvent(this, 701, this.selectedItemReminder, 1));
      }

   }

   public Object[] getSelectedObjects() {
      Object var1 = this.getSelectedItem();
      if (var1 == null) {
         return new Object[0];
      } else {
         Object[] var2 = new Object[]{var1};
         return var2;
      }
   }

   public void actionPerformed(ActionEvent var1) {
      ComboBoxEditor var2 = this.getEditor();
      if (var2 != null && var1 != null && (var2 == var1.getSource() || var2.getEditorComponent() == var1.getSource())) {
         this.setPopupVisible(false);
         this.getModel().setSelectedItem(var2.getItem());
         String var3 = this.getActionCommand();
         this.setActionCommand("comboBoxEdited");
         this.fireActionEvent();
         this.setActionCommand(var3);
      }

   }

   public void contentsChanged(ListDataEvent var1) {
      Object var2 = this.selectedItemReminder;
      Object var3 = this.dataModel.getSelectedItem();
      if (var2 == null || !var2.equals(var3)) {
         this.selectedItemChanged();
         if (!this.selectingItem) {
            this.fireActionEvent();
         }
      }

   }

   public void intervalAdded(ListDataEvent var1) {
      if (this.selectedItemReminder != this.dataModel.getSelectedItem()) {
         this.selectedItemChanged();
      }

   }

   public void intervalRemoved(ListDataEvent var1) {
      this.contentsChanged(var1);
   }

   public boolean selectWithKeyChar(char var1) {
      if (this.keySelectionManager == null) {
         this.keySelectionManager = this.createDefaultKeySelectionManager();
      }

      int var2 = this.keySelectionManager.selectionForKey(var1, this.getModel());
      if (var2 != -1) {
         this.setSelectedIndex(var2);
         return true;
      } else {
         return false;
      }
   }

   public void setEnabled(boolean var1) {
      super.setEnabled(var1);
      this.firePropertyChange("enabled", !this.isEnabled(), this.isEnabled());
   }

   public void configureEditor(ComboBoxEditor var1, Object var2) {
      var1.setItem(var2);
   }

   public void processKeyEvent(KeyEvent var1) {
      if (var1.getKeyCode() == 9) {
         this.hidePopup();
      }

      super.processKeyEvent(var1);
   }

   protected boolean processKeyBinding(KeyStroke var1, KeyEvent var2, int var3, boolean var4) {
      if (super.processKeyBinding(var1, var2, var3, var4)) {
         return true;
      } else if (this.isEditable() && var3 == 0 && this.getEditor() != null && Boolean.TRUE.equals(this.getClientProperty("JComboBox.isTableCellEditor"))) {
         Component var5 = this.getEditor().getEditorComponent();
         if (var5 instanceof JComponent) {
            JComponent var6 = (JComponent)var5;
            return var6.processKeyBinding(var1, var2, 0, var4);
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public void setKeySelectionManager(JComboBox.KeySelectionManager var1) {
      this.keySelectionManager = var1;
   }

   public JComboBox.KeySelectionManager getKeySelectionManager() {
      return this.keySelectionManager;
   }

   public int getItemCount() {
      return this.dataModel.getSize();
   }

   public E getItemAt(int var1) {
      return this.dataModel.getElementAt(var1);
   }

   protected JComboBox.KeySelectionManager createDefaultKeySelectionManager() {
      return new JComboBox.DefaultKeySelectionManager();
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      if (this.getUIClassID().equals("ComboBoxUI")) {
         byte var2 = JComponent.getWriteObjCounter(this);
         --var2;
         JComponent.setWriteObjCounter(this, var2);
         if (var2 == 0 && this.ui != null) {
            this.ui.installUI(this);
         }
      }

   }

   protected String paramString() {
      String var1 = this.selectedItemReminder != null ? this.selectedItemReminder.toString() : "";
      String var2 = this.isEditable ? "true" : "false";
      String var3 = this.lightWeightPopupEnabled ? "true" : "false";
      return super.paramString() + ",isEditable=" + var2 + ",lightWeightPopupEnabled=" + var3 + ",maximumRowCount=" + this.maximumRowCount + ",selectedItemReminder=" + var1;
   }

   public AccessibleContext getAccessibleContext() {
      if (this.accessibleContext == null) {
         this.accessibleContext = new JComboBox.AccessibleJComboBox();
      }

      return this.accessibleContext;
   }

   protected class AccessibleJComboBox extends JComponent.AccessibleJComponent implements AccessibleAction, AccessibleSelection {
      private JList popupList;
      private Accessible previousSelectedAccessible = null;
      private JComboBox<E>.AccessibleJComboBox.EditorAccessibleContext editorAccessibleContext = null;

      public AccessibleJComboBox() {
         super();
         JComboBox.this.addPropertyChangeListener(new JComboBox.AccessibleJComboBox.AccessibleJComboBoxPropertyChangeListener());
         this.setEditorNameAndDescription();
         Accessible var2 = JComboBox.this.getUI().getAccessibleChild(JComboBox.this, 0);
         if (var2 instanceof ComboPopup) {
            this.popupList = ((ComboPopup)var2).getList();
            this.popupList.addListSelectionListener(new JComboBox.AccessibleJComboBox.AccessibleJComboBoxListSelectionListener());
         }

         JComboBox.this.addPopupMenuListener(new JComboBox.AccessibleJComboBox.AccessibleJComboBoxPopupMenuListener());
      }

      private void setEditorNameAndDescription() {
         ComboBoxEditor var1 = JComboBox.this.getEditor();
         if (var1 != null) {
            Component var2 = var1.getEditorComponent();
            if (var2 instanceof Accessible) {
               AccessibleContext var3 = var2.getAccessibleContext();
               if (var3 != null) {
                  var3.setAccessibleName(this.getAccessibleName());
                  var3.setAccessibleDescription(this.getAccessibleDescription());
               }
            }
         }

      }

      public int getAccessibleChildrenCount() {
         return JComboBox.this.ui != null ? JComboBox.this.ui.getAccessibleChildrenCount(JComboBox.this) : super.getAccessibleChildrenCount();
      }

      public Accessible getAccessibleChild(int var1) {
         return JComboBox.this.ui != null ? JComboBox.this.ui.getAccessibleChild(JComboBox.this, var1) : super.getAccessibleChild(var1);
      }

      public AccessibleRole getAccessibleRole() {
         return AccessibleRole.COMBO_BOX;
      }

      public AccessibleStateSet getAccessibleStateSet() {
         AccessibleStateSet var1 = super.getAccessibleStateSet();
         if (var1 == null) {
            var1 = new AccessibleStateSet();
         }

         if (JComboBox.this.isPopupVisible()) {
            var1.add(AccessibleState.EXPANDED);
         } else {
            var1.add(AccessibleState.COLLAPSED);
         }

         return var1;
      }

      public AccessibleAction getAccessibleAction() {
         return this;
      }

      public String getAccessibleActionDescription(int var1) {
         return var1 == 0 ? UIManager.getString("ComboBox.togglePopupText") : null;
      }

      public int getAccessibleActionCount() {
         return 1;
      }

      public boolean doAccessibleAction(int var1) {
         if (var1 == 0) {
            JComboBox.this.setPopupVisible(!JComboBox.this.isPopupVisible());
            return true;
         } else {
            return false;
         }
      }

      public AccessibleSelection getAccessibleSelection() {
         return this;
      }

      public int getAccessibleSelectionCount() {
         Object var1 = JComboBox.this.getSelectedItem();
         return var1 != null ? 1 : 0;
      }

      public Accessible getAccessibleSelection(int var1) {
         Accessible var2 = JComboBox.this.getUI().getAccessibleChild(JComboBox.this, 0);
         if (var2 != null && var2 instanceof ComboPopup) {
            JList var3 = ((ComboPopup)var2).getList();
            AccessibleContext var4 = var3.getAccessibleContext();
            if (var4 != null) {
               AccessibleSelection var5 = var4.getAccessibleSelection();
               if (var5 != null) {
                  return var5.getAccessibleSelection(var1);
               }
            }
         }

         return null;
      }

      public boolean isAccessibleChildSelected(int var1) {
         return JComboBox.this.getSelectedIndex() == var1;
      }

      public void addAccessibleSelection(int var1) {
         this.clearAccessibleSelection();
         JComboBox.this.setSelectedIndex(var1);
      }

      public void removeAccessibleSelection(int var1) {
         if (JComboBox.this.getSelectedIndex() == var1) {
            this.clearAccessibleSelection();
         }

      }

      public void clearAccessibleSelection() {
         JComboBox.this.setSelectedIndex(-1);
      }

      public void selectAllAccessibleSelection() {
      }

      private class EditorAccessibleContext extends AccessibleContext {
         private AccessibleContext ac;

         private EditorAccessibleContext() {
         }

         EditorAccessibleContext(Accessible var2) {
            this.ac = var2.getAccessibleContext();
         }

         public String getAccessibleName() {
            return this.ac.getAccessibleName();
         }

         public void setAccessibleName(String var1) {
            this.ac.setAccessibleName(var1);
         }

         public String getAccessibleDescription() {
            return this.ac.getAccessibleDescription();
         }

         public void setAccessibleDescription(String var1) {
            this.ac.setAccessibleDescription(var1);
         }

         public AccessibleRole getAccessibleRole() {
            return this.ac.getAccessibleRole();
         }

         public AccessibleStateSet getAccessibleStateSet() {
            return this.ac.getAccessibleStateSet();
         }

         public Accessible getAccessibleParent() {
            return this.ac.getAccessibleParent();
         }

         public void setAccessibleParent(Accessible var1) {
            this.ac.setAccessibleParent(var1);
         }

         public int getAccessibleIndexInParent() {
            return JComboBox.this.getSelectedIndex();
         }

         public int getAccessibleChildrenCount() {
            return this.ac.getAccessibleChildrenCount();
         }

         public Accessible getAccessibleChild(int var1) {
            return this.ac.getAccessibleChild(var1);
         }

         public Locale getLocale() throws IllegalComponentStateException {
            return this.ac.getLocale();
         }

         public void addPropertyChangeListener(PropertyChangeListener var1) {
            this.ac.addPropertyChangeListener(var1);
         }

         public void removePropertyChangeListener(PropertyChangeListener var1) {
            this.ac.removePropertyChangeListener(var1);
         }

         public AccessibleAction getAccessibleAction() {
            return this.ac.getAccessibleAction();
         }

         public AccessibleComponent getAccessibleComponent() {
            return this.ac.getAccessibleComponent();
         }

         public AccessibleSelection getAccessibleSelection() {
            return this.ac.getAccessibleSelection();
         }

         public AccessibleText getAccessibleText() {
            return this.ac.getAccessibleText();
         }

         public AccessibleEditableText getAccessibleEditableText() {
            return this.ac.getAccessibleEditableText();
         }

         public AccessibleValue getAccessibleValue() {
            return this.ac.getAccessibleValue();
         }

         public AccessibleIcon[] getAccessibleIcon() {
            return this.ac.getAccessibleIcon();
         }

         public AccessibleRelationSet getAccessibleRelationSet() {
            return this.ac.getAccessibleRelationSet();
         }

         public AccessibleTable getAccessibleTable() {
            return this.ac.getAccessibleTable();
         }

         public void firePropertyChange(String var1, Object var2, Object var3) {
            this.ac.firePropertyChange(var1, var2, var3);
         }
      }

      private class AccessibleEditor implements Accessible {
         public AccessibleContext getAccessibleContext() {
            if (AccessibleJComboBox.this.editorAccessibleContext == null) {
               Component var1 = JComboBox.this.getEditor().getEditorComponent();
               if (var1 instanceof Accessible) {
                  AccessibleJComboBox.this.editorAccessibleContext = AccessibleJComboBox.this.new EditorAccessibleContext((Accessible)var1);
               }
            }

            return AccessibleJComboBox.this.editorAccessibleContext;
         }
      }

      private class AccessibleJComboBoxListSelectionListener implements ListSelectionListener {
         private AccessibleJComboBoxListSelectionListener() {
         }

         public void valueChanged(ListSelectionEvent var1) {
            if (AccessibleJComboBox.this.popupList != null) {
               int var2 = AccessibleJComboBox.this.popupList.getSelectedIndex();
               if (var2 >= 0) {
                  Accessible var3 = AccessibleJComboBox.this.popupList.getAccessibleContext().getAccessibleChild(var2);
                  if (var3 != null) {
                     PropertyChangeEvent var4;
                     if (AccessibleJComboBox.this.previousSelectedAccessible != null) {
                        var4 = new PropertyChangeEvent(AccessibleJComboBox.this.previousSelectedAccessible, "AccessibleState", AccessibleState.FOCUSED, (Object)null);
                        AccessibleJComboBox.this.firePropertyChange("AccessibleState", (Object)null, var4);
                     }

                     var4 = new PropertyChangeEvent(var3, "AccessibleState", (Object)null, AccessibleState.FOCUSED);
                     AccessibleJComboBox.this.firePropertyChange("AccessibleState", (Object)null, var4);
                     AccessibleJComboBox.this.firePropertyChange("AccessibleActiveDescendant", AccessibleJComboBox.this.previousSelectedAccessible, var3);
                     AccessibleJComboBox.this.previousSelectedAccessible = var3;
                  }
               }
            }
         }

         // $FF: synthetic method
         AccessibleJComboBoxListSelectionListener(Object var2) {
            this();
         }
      }

      private class AccessibleJComboBoxPopupMenuListener implements PopupMenuListener {
         private AccessibleJComboBoxPopupMenuListener() {
         }

         public void popupMenuWillBecomeVisible(PopupMenuEvent var1) {
            if (AccessibleJComboBox.this.popupList != null) {
               int var2 = AccessibleJComboBox.this.popupList.getSelectedIndex();
               if (var2 >= 0) {
                  AccessibleJComboBox.this.previousSelectedAccessible = AccessibleJComboBox.this.popupList.getAccessibleContext().getAccessibleChild(var2);
               }
            }
         }

         public void popupMenuWillBecomeInvisible(PopupMenuEvent var1) {
         }

         public void popupMenuCanceled(PopupMenuEvent var1) {
         }

         // $FF: synthetic method
         AccessibleJComboBoxPopupMenuListener(Object var2) {
            this();
         }
      }

      private class AccessibleJComboBoxPropertyChangeListener implements PropertyChangeListener {
         private AccessibleJComboBoxPropertyChangeListener() {
         }

         public void propertyChange(PropertyChangeEvent var1) {
            if (var1.getPropertyName() == "editor") {
               AccessibleJComboBox.this.setEditorNameAndDescription();
            }

         }

         // $FF: synthetic method
         AccessibleJComboBoxPropertyChangeListener(Object var2) {
            this();
         }
      }
   }

   class DefaultKeySelectionManager implements JComboBox.KeySelectionManager, Serializable {
      public int selectionForKey(char var1, ComboBoxModel var2) {
         int var5 = -1;
         Object var6 = var2.getSelectedItem();
         int var3;
         int var4;
         if (var6 != null) {
            var3 = 0;

            for(var4 = var2.getSize(); var3 < var4; ++var3) {
               if (var6 == var2.getElementAt(var3)) {
                  var5 = var3;
                  break;
               }
            }
         }

         String var8 = ("" + var1).toLowerCase();
         var1 = var8.charAt(0);
         ++var5;
         var3 = var5;

         String var7;
         Object var9;
         for(var4 = var2.getSize(); var3 < var4; ++var3) {
            var9 = var2.getElementAt(var3);
            if (var9 != null && var9.toString() != null) {
               var7 = var9.toString().toLowerCase();
               if (var7.length() > 0 && var7.charAt(0) == var1) {
                  return var3;
               }
            }
         }

         for(var3 = 0; var3 < var5; ++var3) {
            var9 = var2.getElementAt(var3);
            if (var9 != null && var9.toString() != null) {
               var7 = var9.toString().toLowerCase();
               if (var7.length() > 0 && var7.charAt(0) == var1) {
                  return var3;
               }
            }
         }

         return -1;
      }
   }

   public interface KeySelectionManager {
      int selectionForKey(char var1, ComboBoxModel var2);
   }

   private static class ComboBoxActionPropertyChangeListener extends ActionPropertyChangeListener<JComboBox<?>> {
      ComboBoxActionPropertyChangeListener(JComboBox<?> var1, Action var2) {
         super(var1, var2);
      }

      protected void actionPropertyChanged(JComboBox<?> var1, Action var2, PropertyChangeEvent var3) {
         if (AbstractAction.shouldReconfigure(var3)) {
            var1.configurePropertiesFromAction(var2);
         } else {
            var1.actionPropertyChanged(var2, var3.getPropertyName());
         }

      }
   }
}
