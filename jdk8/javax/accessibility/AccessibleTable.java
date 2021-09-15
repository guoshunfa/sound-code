package javax.accessibility;

public interface AccessibleTable {
   Accessible getAccessibleCaption();

   void setAccessibleCaption(Accessible var1);

   Accessible getAccessibleSummary();

   void setAccessibleSummary(Accessible var1);

   int getAccessibleRowCount();

   int getAccessibleColumnCount();

   Accessible getAccessibleAt(int var1, int var2);

   int getAccessibleRowExtentAt(int var1, int var2);

   int getAccessibleColumnExtentAt(int var1, int var2);

   AccessibleTable getAccessibleRowHeader();

   void setAccessibleRowHeader(AccessibleTable var1);

   AccessibleTable getAccessibleColumnHeader();

   void setAccessibleColumnHeader(AccessibleTable var1);

   Accessible getAccessibleRowDescription(int var1);

   void setAccessibleRowDescription(int var1, Accessible var2);

   Accessible getAccessibleColumnDescription(int var1);

   void setAccessibleColumnDescription(int var1, Accessible var2);

   boolean isAccessibleSelected(int var1, int var2);

   boolean isAccessibleRowSelected(int var1);

   boolean isAccessibleColumnSelected(int var1);

   int[] getSelectedAccessibleRows();

   int[] getSelectedAccessibleColumns();
}
