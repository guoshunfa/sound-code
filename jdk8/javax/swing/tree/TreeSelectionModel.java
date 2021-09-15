package javax.swing.tree;

import java.beans.PropertyChangeListener;
import javax.swing.event.TreeSelectionListener;

public interface TreeSelectionModel {
   int SINGLE_TREE_SELECTION = 1;
   int CONTIGUOUS_TREE_SELECTION = 2;
   int DISCONTIGUOUS_TREE_SELECTION = 4;

   void setSelectionMode(int var1);

   int getSelectionMode();

   void setSelectionPath(TreePath var1);

   void setSelectionPaths(TreePath[] var1);

   void addSelectionPath(TreePath var1);

   void addSelectionPaths(TreePath[] var1);

   void removeSelectionPath(TreePath var1);

   void removeSelectionPaths(TreePath[] var1);

   TreePath getSelectionPath();

   TreePath[] getSelectionPaths();

   int getSelectionCount();

   boolean isPathSelected(TreePath var1);

   boolean isSelectionEmpty();

   void clearSelection();

   void setRowMapper(RowMapper var1);

   RowMapper getRowMapper();

   int[] getSelectionRows();

   int getMinSelectionRow();

   int getMaxSelectionRow();

   boolean isRowSelected(int var1);

   void resetRowSelection();

   int getLeadSelectionRow();

   TreePath getLeadSelectionPath();

   void addPropertyChangeListener(PropertyChangeListener var1);

   void removePropertyChangeListener(PropertyChangeListener var1);

   void addTreeSelectionListener(TreeSelectionListener var1);

   void removeTreeSelectionListener(TreeSelectionListener var1);
}
