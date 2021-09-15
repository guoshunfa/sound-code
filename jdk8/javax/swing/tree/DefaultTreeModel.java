package javax.swing.tree;

import java.beans.ConstructorProperties;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.EventListener;
import java.util.Vector;
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

public class DefaultTreeModel implements Serializable, TreeModel {
   protected TreeNode root;
   protected EventListenerList listenerList;
   protected boolean asksAllowsChildren;

   @ConstructorProperties({"root"})
   public DefaultTreeModel(TreeNode var1) {
      this(var1, false);
   }

   public DefaultTreeModel(TreeNode var1, boolean var2) {
      this.listenerList = new EventListenerList();
      this.root = var1;
      this.asksAllowsChildren = var2;
   }

   public void setAsksAllowsChildren(boolean var1) {
      this.asksAllowsChildren = var1;
   }

   public boolean asksAllowsChildren() {
      return this.asksAllowsChildren;
   }

   public void setRoot(TreeNode var1) {
      TreeNode var2 = this.root;
      this.root = var1;
      if (var1 == null && var2 != null) {
         this.fireTreeStructureChanged(this, (TreePath)null);
      } else {
         this.nodeStructureChanged(var1);
      }

   }

   public Object getRoot() {
      return this.root;
   }

   public int getIndexOfChild(Object var1, Object var2) {
      return var1 != null && var2 != null ? ((TreeNode)var1).getIndex((TreeNode)var2) : -1;
   }

   public Object getChild(Object var1, int var2) {
      return ((TreeNode)var1).getChildAt(var2);
   }

   public int getChildCount(Object var1) {
      return ((TreeNode)var1).getChildCount();
   }

   public boolean isLeaf(Object var1) {
      if (this.asksAllowsChildren) {
         return !((TreeNode)var1).getAllowsChildren();
      } else {
         return ((TreeNode)var1).isLeaf();
      }
   }

   public void reload() {
      this.reload(this.root);
   }

   public void valueForPathChanged(TreePath var1, Object var2) {
      MutableTreeNode var3 = (MutableTreeNode)var1.getLastPathComponent();
      var3.setUserObject(var2);
      this.nodeChanged(var3);
   }

   public void insertNodeInto(MutableTreeNode var1, MutableTreeNode var2, int var3) {
      var2.insert(var1, var3);
      int[] var4 = new int[]{var3};
      this.nodesWereInserted(var2, var4);
   }

   public void removeNodeFromParent(MutableTreeNode var1) {
      MutableTreeNode var2 = (MutableTreeNode)var1.getParent();
      if (var2 == null) {
         throw new IllegalArgumentException("node does not have a parent.");
      } else {
         int[] var3 = new int[1];
         Object[] var4 = new Object[1];
         var3[0] = var2.getIndex(var1);
         var2.remove(var3[0]);
         var4[0] = var1;
         this.nodesWereRemoved(var2, var3, var4);
      }
   }

   public void nodeChanged(TreeNode var1) {
      if (this.listenerList != null && var1 != null) {
         TreeNode var2 = var1.getParent();
         if (var2 != null) {
            int var3 = var2.getIndex(var1);
            if (var3 != -1) {
               int[] var4 = new int[]{var3};
               this.nodesChanged(var2, var4);
            }
         } else if (var1 == this.getRoot()) {
            this.nodesChanged(var1, (int[])null);
         }
      }

   }

   public void reload(TreeNode var1) {
      if (var1 != null) {
         this.fireTreeStructureChanged(this, this.getPathToRoot(var1), (int[])null, (Object[])null);
      }

   }

   public void nodesWereInserted(TreeNode var1, int[] var2) {
      if (this.listenerList != null && var1 != null && var2 != null && var2.length > 0) {
         int var3 = var2.length;
         Object[] var4 = new Object[var3];

         for(int var5 = 0; var5 < var3; ++var5) {
            var4[var5] = var1.getChildAt(var2[var5]);
         }

         this.fireTreeNodesInserted(this, this.getPathToRoot(var1), var2, var4);
      }

   }

   public void nodesWereRemoved(TreeNode var1, int[] var2, Object[] var3) {
      if (var1 != null && var2 != null) {
         this.fireTreeNodesRemoved(this, this.getPathToRoot(var1), var2, var3);
      }

   }

   public void nodesChanged(TreeNode var1, int[] var2) {
      if (var1 != null) {
         if (var2 != null) {
            int var3 = var2.length;
            if (var3 > 0) {
               Object[] var4 = new Object[var3];

               for(int var5 = 0; var5 < var3; ++var5) {
                  var4[var5] = var1.getChildAt(var2[var5]);
               }

               this.fireTreeNodesChanged(this, this.getPathToRoot(var1), var2, var4);
            }
         } else if (var1 == this.getRoot()) {
            this.fireTreeNodesChanged(this, this.getPathToRoot(var1), (int[])null, (Object[])null);
         }
      }

   }

   public void nodeStructureChanged(TreeNode var1) {
      if (var1 != null) {
         this.fireTreeStructureChanged(this, this.getPathToRoot(var1), (int[])null, (Object[])null);
      }

   }

   public TreeNode[] getPathToRoot(TreeNode var1) {
      return this.getPathToRoot(var1, 0);
   }

   protected TreeNode[] getPathToRoot(TreeNode var1, int var2) {
      TreeNode[] var3;
      if (var1 == null) {
         if (var2 == 0) {
            return null;
         }

         var3 = new TreeNode[var2];
      } else {
         ++var2;
         if (var1 == this.root) {
            var3 = new TreeNode[var2];
         } else {
            var3 = this.getPathToRoot(var1.getParent(), var2);
         }

         var3[var3.length - var2] = var1;
      }

      return var3;
   }

   public void addTreeModelListener(TreeModelListener var1) {
      this.listenerList.add(TreeModelListener.class, var1);
   }

   public void removeTreeModelListener(TreeModelListener var1) {
      this.listenerList.remove(TreeModelListener.class, var1);
   }

   public TreeModelListener[] getTreeModelListeners() {
      return (TreeModelListener[])this.listenerList.getListeners(TreeModelListener.class);
   }

   protected void fireTreeNodesChanged(Object var1, Object[] var2, int[] var3, Object[] var4) {
      Object[] var5 = this.listenerList.getListenerList();
      TreeModelEvent var6 = null;

      for(int var7 = var5.length - 2; var7 >= 0; var7 -= 2) {
         if (var5[var7] == TreeModelListener.class) {
            if (var6 == null) {
               var6 = new TreeModelEvent(var1, var2, var3, var4);
            }

            ((TreeModelListener)var5[var7 + 1]).treeNodesChanged(var6);
         }
      }

   }

   protected void fireTreeNodesInserted(Object var1, Object[] var2, int[] var3, Object[] var4) {
      Object[] var5 = this.listenerList.getListenerList();
      TreeModelEvent var6 = null;

      for(int var7 = var5.length - 2; var7 >= 0; var7 -= 2) {
         if (var5[var7] == TreeModelListener.class) {
            if (var6 == null) {
               var6 = new TreeModelEvent(var1, var2, var3, var4);
            }

            ((TreeModelListener)var5[var7 + 1]).treeNodesInserted(var6);
         }
      }

   }

   protected void fireTreeNodesRemoved(Object var1, Object[] var2, int[] var3, Object[] var4) {
      Object[] var5 = this.listenerList.getListenerList();
      TreeModelEvent var6 = null;

      for(int var7 = var5.length - 2; var7 >= 0; var7 -= 2) {
         if (var5[var7] == TreeModelListener.class) {
            if (var6 == null) {
               var6 = new TreeModelEvent(var1, var2, var3, var4);
            }

            ((TreeModelListener)var5[var7 + 1]).treeNodesRemoved(var6);
         }
      }

   }

   protected void fireTreeStructureChanged(Object var1, Object[] var2, int[] var3, Object[] var4) {
      Object[] var5 = this.listenerList.getListenerList();
      TreeModelEvent var6 = null;

      for(int var7 = var5.length - 2; var7 >= 0; var7 -= 2) {
         if (var5[var7] == TreeModelListener.class) {
            if (var6 == null) {
               var6 = new TreeModelEvent(var1, var2, var3, var4);
            }

            ((TreeModelListener)var5[var7 + 1]).treeStructureChanged(var6);
         }
      }

   }

   private void fireTreeStructureChanged(Object var1, TreePath var2) {
      Object[] var3 = this.listenerList.getListenerList();
      TreeModelEvent var4 = null;

      for(int var5 = var3.length - 2; var5 >= 0; var5 -= 2) {
         if (var3[var5] == TreeModelListener.class) {
            if (var4 == null) {
               var4 = new TreeModelEvent(var1, var2);
            }

            ((TreeModelListener)var3[var5 + 1]).treeStructureChanged(var4);
         }
      }

   }

   public <T extends EventListener> T[] getListeners(Class<T> var1) {
      return this.listenerList.getListeners(var1);
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      Vector var2 = new Vector();
      var1.defaultWriteObject();
      if (this.root != null && this.root instanceof Serializable) {
         var2.addElement("root");
         var2.addElement(this.root);
      }

      var1.writeObject(var2);
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      Vector var2 = (Vector)var1.readObject();
      byte var3 = 0;
      int var4 = var2.size();
      if (var3 < var4 && var2.elementAt(var3).equals("root")) {
         int var5 = var3 + 1;
         this.root = (TreeNode)var2.elementAt(var5);
         ++var5;
      }

   }
}
