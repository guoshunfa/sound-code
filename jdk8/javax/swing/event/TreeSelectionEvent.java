package javax.swing.event;

import java.util.EventObject;
import javax.swing.tree.TreePath;

public class TreeSelectionEvent extends EventObject {
   protected TreePath[] paths;
   protected boolean[] areNew;
   protected TreePath oldLeadSelectionPath;
   protected TreePath newLeadSelectionPath;

   public TreeSelectionEvent(Object var1, TreePath[] var2, boolean[] var3, TreePath var4, TreePath var5) {
      super(var1);
      this.paths = var2;
      this.areNew = var3;
      this.oldLeadSelectionPath = var4;
      this.newLeadSelectionPath = var5;
   }

   public TreeSelectionEvent(Object var1, TreePath var2, boolean var3, TreePath var4, TreePath var5) {
      super(var1);
      this.paths = new TreePath[1];
      this.paths[0] = var2;
      this.areNew = new boolean[1];
      this.areNew[0] = var3;
      this.oldLeadSelectionPath = var4;
      this.newLeadSelectionPath = var5;
   }

   public TreePath[] getPaths() {
      int var1 = this.paths.length;
      TreePath[] var2 = new TreePath[var1];
      System.arraycopy(this.paths, 0, var2, 0, var1);
      return var2;
   }

   public TreePath getPath() {
      return this.paths[0];
   }

   public boolean isAddedPath() {
      return this.areNew[0];
   }

   public boolean isAddedPath(TreePath var1) {
      for(int var2 = this.paths.length - 1; var2 >= 0; --var2) {
         if (this.paths[var2].equals(var1)) {
            return this.areNew[var2];
         }
      }

      throw new IllegalArgumentException("path is not a path identified by the TreeSelectionEvent");
   }

   public boolean isAddedPath(int var1) {
      if (this.paths != null && var1 >= 0 && var1 < this.paths.length) {
         return this.areNew[var1];
      } else {
         throw new IllegalArgumentException("index is beyond range of added paths identified by TreeSelectionEvent");
      }
   }

   public TreePath getOldLeadSelectionPath() {
      return this.oldLeadSelectionPath;
   }

   public TreePath getNewLeadSelectionPath() {
      return this.newLeadSelectionPath;
   }

   public Object cloneWithSource(Object var1) {
      return new TreeSelectionEvent(var1, this.paths, this.areNew, this.oldLeadSelectionPath, this.newLeadSelectionPath);
   }
}
