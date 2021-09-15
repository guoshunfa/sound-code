package javax.accessibility;

public interface AccessibleSelection {
   int getAccessibleSelectionCount();

   Accessible getAccessibleSelection(int var1);

   boolean isAccessibleChildSelected(int var1);

   void addAccessibleSelection(int var1);

   void removeAccessibleSelection(int var1);

   void clearAccessibleSelection();

   void selectAllAccessibleSelection();
}
