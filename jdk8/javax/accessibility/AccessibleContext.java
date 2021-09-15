package javax.accessibility;

import java.awt.IllegalComponentStateException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Locale;
import sun.awt.AWTAccessor;
import sun.awt.AppContext;

public abstract class AccessibleContext {
   private volatile AppContext targetAppContext;
   public static final String ACCESSIBLE_NAME_PROPERTY = "AccessibleName";
   public static final String ACCESSIBLE_DESCRIPTION_PROPERTY = "AccessibleDescription";
   public static final String ACCESSIBLE_STATE_PROPERTY = "AccessibleState";
   public static final String ACCESSIBLE_VALUE_PROPERTY = "AccessibleValue";
   public static final String ACCESSIBLE_SELECTION_PROPERTY = "AccessibleSelection";
   public static final String ACCESSIBLE_CARET_PROPERTY = "AccessibleCaret";
   public static final String ACCESSIBLE_VISIBLE_DATA_PROPERTY = "AccessibleVisibleData";
   public static final String ACCESSIBLE_CHILD_PROPERTY = "AccessibleChild";
   public static final String ACCESSIBLE_ACTIVE_DESCENDANT_PROPERTY = "AccessibleActiveDescendant";
   public static final String ACCESSIBLE_TABLE_CAPTION_CHANGED = "accessibleTableCaptionChanged";
   public static final String ACCESSIBLE_TABLE_SUMMARY_CHANGED = "accessibleTableSummaryChanged";
   public static final String ACCESSIBLE_TABLE_MODEL_CHANGED = "accessibleTableModelChanged";
   public static final String ACCESSIBLE_TABLE_ROW_HEADER_CHANGED = "accessibleTableRowHeaderChanged";
   public static final String ACCESSIBLE_TABLE_ROW_DESCRIPTION_CHANGED = "accessibleTableRowDescriptionChanged";
   public static final String ACCESSIBLE_TABLE_COLUMN_HEADER_CHANGED = "accessibleTableColumnHeaderChanged";
   public static final String ACCESSIBLE_TABLE_COLUMN_DESCRIPTION_CHANGED = "accessibleTableColumnDescriptionChanged";
   public static final String ACCESSIBLE_ACTION_PROPERTY = "accessibleActionProperty";
   public static final String ACCESSIBLE_HYPERTEXT_OFFSET = "AccessibleHypertextOffset";
   public static final String ACCESSIBLE_TEXT_PROPERTY = "AccessibleText";
   public static final String ACCESSIBLE_INVALIDATE_CHILDREN = "accessibleInvalidateChildren";
   public static final String ACCESSIBLE_TEXT_ATTRIBUTES_CHANGED = "accessibleTextAttributesChanged";
   public static final String ACCESSIBLE_COMPONENT_BOUNDS_CHANGED = "accessibleComponentBoundsChanged";
   protected Accessible accessibleParent = null;
   protected String accessibleName = null;
   protected String accessibleDescription = null;
   private PropertyChangeSupport accessibleChangeSupport = null;
   private AccessibleRelationSet relationSet = new AccessibleRelationSet();
   private Object nativeAXResource;

   public String getAccessibleName() {
      return this.accessibleName;
   }

   public void setAccessibleName(String var1) {
      String var2 = this.accessibleName;
      this.accessibleName = var1;
      this.firePropertyChange("AccessibleName", var2, this.accessibleName);
   }

   public String getAccessibleDescription() {
      return this.accessibleDescription;
   }

   public void setAccessibleDescription(String var1) {
      String var2 = this.accessibleDescription;
      this.accessibleDescription = var1;
      this.firePropertyChange("AccessibleDescription", var2, this.accessibleDescription);
   }

   public abstract AccessibleRole getAccessibleRole();

   public abstract AccessibleStateSet getAccessibleStateSet();

   public Accessible getAccessibleParent() {
      return this.accessibleParent;
   }

   public void setAccessibleParent(Accessible var1) {
      this.accessibleParent = var1;
   }

   public abstract int getAccessibleIndexInParent();

   public abstract int getAccessibleChildrenCount();

   public abstract Accessible getAccessibleChild(int var1);

   public abstract Locale getLocale() throws IllegalComponentStateException;

   public void addPropertyChangeListener(PropertyChangeListener var1) {
      if (this.accessibleChangeSupport == null) {
         this.accessibleChangeSupport = new PropertyChangeSupport(this);
      }

      this.accessibleChangeSupport.addPropertyChangeListener(var1);
   }

   public void removePropertyChangeListener(PropertyChangeListener var1) {
      if (this.accessibleChangeSupport != null) {
         this.accessibleChangeSupport.removePropertyChangeListener(var1);
      }

   }

   public AccessibleAction getAccessibleAction() {
      return null;
   }

   public AccessibleComponent getAccessibleComponent() {
      return null;
   }

   public AccessibleSelection getAccessibleSelection() {
      return null;
   }

   public AccessibleText getAccessibleText() {
      return null;
   }

   public AccessibleEditableText getAccessibleEditableText() {
      return null;
   }

   public AccessibleValue getAccessibleValue() {
      return null;
   }

   public AccessibleIcon[] getAccessibleIcon() {
      return null;
   }

   public AccessibleRelationSet getAccessibleRelationSet() {
      return this.relationSet;
   }

   public AccessibleTable getAccessibleTable() {
      return null;
   }

   public void firePropertyChange(String var1, Object var2, Object var3) {
      if (this.accessibleChangeSupport != null) {
         if (var3 instanceof PropertyChangeEvent) {
            PropertyChangeEvent var4 = (PropertyChangeEvent)var3;
            this.accessibleChangeSupport.firePropertyChange(var4);
         } else {
            this.accessibleChangeSupport.firePropertyChange(var1, var2, var3);
         }
      }

   }

   static {
      AWTAccessor.setAccessibleContextAccessor(new AWTAccessor.AccessibleContextAccessor() {
         public void setAppContext(AccessibleContext var1, AppContext var2) {
            var1.targetAppContext = var2;
         }

         public AppContext getAppContext(AccessibleContext var1) {
            return var1.targetAppContext;
         }
      });
   }
}
