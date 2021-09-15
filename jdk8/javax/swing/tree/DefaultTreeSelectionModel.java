package javax.swing.tree;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.DefaultListSelectionModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.SwingPropertyChangeSupport;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

public class DefaultTreeSelectionModel implements Cloneable, Serializable, TreeSelectionModel {
   public static final String SELECTION_MODE_PROPERTY = "selectionMode";
   protected SwingPropertyChangeSupport changeSupport;
   protected TreePath[] selection;
   protected EventListenerList listenerList = new EventListenerList();
   protected transient RowMapper rowMapper;
   protected DefaultListSelectionModel listSelectionModel = new DefaultListSelectionModel();
   protected int selectionMode = 4;
   protected TreePath leadPath;
   protected int leadIndex;
   protected int leadRow;
   private Hashtable<TreePath, Boolean> uniquePaths;
   private Hashtable<TreePath, Boolean> lastPaths;
   private TreePath[] tempPaths;

   public DefaultTreeSelectionModel() {
      this.leadIndex = this.leadRow = -1;
      this.uniquePaths = new Hashtable();
      this.lastPaths = new Hashtable();
      this.tempPaths = new TreePath[1];
   }

   public void setRowMapper(RowMapper var1) {
      this.rowMapper = var1;
      this.resetRowSelection();
   }

   public RowMapper getRowMapper() {
      return this.rowMapper;
   }

   public void setSelectionMode(int var1) {
      int var2 = this.selectionMode;
      this.selectionMode = var1;
      if (this.selectionMode != 1 && this.selectionMode != 2 && this.selectionMode != 4) {
         this.selectionMode = 4;
      }

      if (var2 != this.selectionMode && this.changeSupport != null) {
         this.changeSupport.firePropertyChange("selectionMode", var2, this.selectionMode);
      }

   }

   public int getSelectionMode() {
      return this.selectionMode;
   }

   public void setSelectionPath(TreePath var1) {
      if (var1 == null) {
         this.setSelectionPaths((TreePath[])null);
      } else {
         TreePath[] var2 = new TreePath[]{var1};
         this.setSelectionPaths(var2);
      }

   }

   public void setSelectionPaths(TreePath[] var1) {
      TreePath[] var6 = var1;
      int var2;
      if (var1 == null) {
         var2 = 0;
      } else {
         var2 = var1.length;
      }

      int var4;
      if (this.selection == null) {
         var4 = 0;
      } else {
         var4 = this.selection.length;
      }

      if (var2 + var4 != 0) {
         if (this.selectionMode == 1) {
            if (var2 > 1) {
               var6 = new TreePath[]{var1[0]};
               var2 = 1;
            }
         } else if (this.selectionMode == 2 && var2 > 0 && !this.arePathsContiguous(var1)) {
            var6 = new TreePath[]{var1[0]};
            var2 = 1;
         }

         TreePath var7 = this.leadPath;
         Vector var8 = new Vector(var2 + var4);
         ArrayList var9 = new ArrayList(var2);
         this.lastPaths.clear();
         this.leadPath = null;

         for(int var3 = 0; var3 < var2; ++var3) {
            TreePath var10 = var6[var3];
            if (var10 != null && this.lastPaths.get(var10) == null) {
               this.lastPaths.put(var10, Boolean.TRUE);
               if (this.uniquePaths.get(var10) == null) {
                  var8.addElement(new PathPlaceHolder(var10, true));
               }

               this.leadPath = var10;
               var9.add(var10);
            }
         }

         TreePath[] var12 = (TreePath[])var9.toArray(new TreePath[var9.size()]);

         for(int var5 = 0; var5 < var4; ++var5) {
            if (this.selection[var5] != null && this.lastPaths.get(this.selection[var5]) == null) {
               var8.addElement(new PathPlaceHolder(this.selection[var5], false));
            }
         }

         this.selection = var12;
         Hashtable var11 = this.uniquePaths;
         this.uniquePaths = this.lastPaths;
         this.lastPaths = var11;
         this.lastPaths.clear();
         this.insureUniqueness();
         this.updateLeadIndex();
         this.resetRowSelection();
         if (var8.size() > 0) {
            this.notifyPathChange(var8, var7);
         }
      }

   }

   public void addSelectionPath(TreePath var1) {
      if (var1 != null) {
         TreePath[] var2 = new TreePath[]{var1};
         this.addSelectionPaths(var2);
      }

   }

   public void addSelectionPaths(TreePath[] var1) {
      int var2 = var1 == null ? 0 : var1.length;
      if (var2 > 0) {
         if (this.selectionMode == 1) {
            this.setSelectionPaths(var1);
         } else if (this.selectionMode == 2 && !this.canPathsBeAdded(var1)) {
            if (this.arePathsContiguous(var1)) {
               this.setSelectionPaths(var1);
            } else {
               TreePath[] var10 = new TreePath[]{var1[0]};
               this.setSelectionPaths(var10);
            }
         } else {
            TreePath var6 = this.leadPath;
            Vector var7 = null;
            int var5;
            if (this.selection == null) {
               var5 = 0;
            } else {
               var5 = this.selection.length;
            }

            this.lastPaths.clear();
            int var3 = 0;

            int var4;
            for(var4 = 0; var3 < var2; ++var3) {
               if (var1[var3] != null) {
                  if (this.uniquePaths.get(var1[var3]) == null) {
                     ++var4;
                     if (var7 == null) {
                        var7 = new Vector();
                     }

                     var7.addElement(new PathPlaceHolder(var1[var3], true));
                     this.uniquePaths.put(var1[var3], Boolean.TRUE);
                     this.lastPaths.put(var1[var3], Boolean.TRUE);
                  }

                  this.leadPath = var1[var3];
               }
            }

            if (this.leadPath == null) {
               this.leadPath = var6;
            }

            if (var4 > 0) {
               TreePath[] var8 = new TreePath[var5 + var4];
               if (var5 > 0) {
                  System.arraycopy(this.selection, 0, var8, 0, var5);
               }

               if (var4 != var1.length) {
                  Enumeration var9 = this.lastPaths.keys();

                  for(var3 = var5; var9.hasMoreElements(); var8[var3++] = (TreePath)var9.nextElement()) {
                  }
               } else {
                  System.arraycopy(var1, 0, var8, var5, var4);
               }

               this.selection = var8;
               this.insureUniqueness();
               this.updateLeadIndex();
               this.resetRowSelection();
               this.notifyPathChange(var7, var6);
            } else {
               this.leadPath = var6;
            }

            this.lastPaths.clear();
         }
      }

   }

   public void removeSelectionPath(TreePath var1) {
      if (var1 != null) {
         TreePath[] var2 = new TreePath[]{var1};
         this.removeSelectionPaths(var2);
      }

   }

   public void removeSelectionPaths(TreePath[] var1) {
      if (var1 != null && this.selection != null && var1.length > 0) {
         if (!this.canPathsBeRemoved(var1)) {
            this.clearSelection();
         } else {
            Vector var2 = null;

            int var3;
            for(var3 = var1.length - 1; var3 >= 0; --var3) {
               if (var1[var3] != null && this.uniquePaths.get(var1[var3]) != null) {
                  if (var2 == null) {
                     var2 = new Vector(var1.length);
                  }

                  this.uniquePaths.remove(var1[var3]);
                  var2.addElement(new PathPlaceHolder(var1[var3], false));
               }
            }

            if (var2 != null) {
               var3 = var2.size();
               TreePath var4 = this.leadPath;
               if (var3 == this.selection.length) {
                  this.selection = null;
               } else {
                  Enumeration var5 = this.uniquePaths.keys();
                  int var6 = 0;

                  for(this.selection = new TreePath[this.selection.length - var3]; var5.hasMoreElements(); this.selection[var6++] = (TreePath)var5.nextElement()) {
                  }
               }

               if (this.leadPath != null && this.uniquePaths.get(this.leadPath) == null) {
                  if (this.selection != null) {
                     this.leadPath = this.selection[this.selection.length - 1];
                  } else {
                     this.leadPath = null;
                  }
               } else if (this.selection != null) {
                  this.leadPath = this.selection[this.selection.length - 1];
               } else {
                  this.leadPath = null;
               }

               this.updateLeadIndex();
               this.resetRowSelection();
               this.notifyPathChange(var2, var4);
            }
         }
      }

   }

   public TreePath getSelectionPath() {
      return this.selection != null && this.selection.length > 0 ? this.selection[0] : null;
   }

   public TreePath[] getSelectionPaths() {
      if (this.selection != null) {
         int var1 = this.selection.length;
         TreePath[] var2 = new TreePath[var1];
         System.arraycopy(this.selection, 0, var2, 0, var1);
         return var2;
      } else {
         return new TreePath[0];
      }
   }

   public int getSelectionCount() {
      return this.selection == null ? 0 : this.selection.length;
   }

   public boolean isPathSelected(TreePath var1) {
      return var1 != null ? this.uniquePaths.get(var1) != null : false;
   }

   public boolean isSelectionEmpty() {
      return this.selection == null || this.selection.length == 0;
   }

   public void clearSelection() {
      if (this.selection != null && this.selection.length > 0) {
         int var1 = this.selection.length;
         boolean[] var2 = new boolean[var1];

         for(int var3 = 0; var3 < var1; ++var3) {
            var2[var3] = false;
         }

         TreeSelectionEvent var4 = new TreeSelectionEvent(this, this.selection, var2, this.leadPath, (TreePath)null);
         this.leadPath = null;
         this.leadIndex = this.leadRow = -1;
         this.uniquePaths.clear();
         this.selection = null;
         this.resetRowSelection();
         this.fireValueChanged(var4);
      }

   }

   public void addTreeSelectionListener(TreeSelectionListener var1) {
      this.listenerList.add(TreeSelectionListener.class, var1);
   }

   public void removeTreeSelectionListener(TreeSelectionListener var1) {
      this.listenerList.remove(TreeSelectionListener.class, var1);
   }

   public TreeSelectionListener[] getTreeSelectionListeners() {
      return (TreeSelectionListener[])this.listenerList.getListeners(TreeSelectionListener.class);
   }

   protected void fireValueChanged(TreeSelectionEvent var1) {
      Object[] var2 = this.listenerList.getListenerList();

      for(int var3 = var2.length - 2; var3 >= 0; var3 -= 2) {
         if (var2[var3] == TreeSelectionListener.class) {
            ((TreeSelectionListener)var2[var3 + 1]).valueChanged(var1);
         }
      }

   }

   public <T extends EventListener> T[] getListeners(Class<T> var1) {
      return this.listenerList.getListeners(var1);
   }

   public int[] getSelectionRows() {
      if (this.rowMapper != null && this.selection != null && this.selection.length > 0) {
         int[] var1 = this.rowMapper.getRowsForPaths(this.selection);
         if (var1 != null) {
            int var2 = 0;

            for(int var3 = var1.length - 1; var3 >= 0; --var3) {
               if (var1[var3] == -1) {
                  ++var2;
               }
            }

            if (var2 > 0) {
               if (var2 == var1.length) {
                  var1 = null;
               } else {
                  int[] var6 = new int[var1.length - var2];
                  int var4 = var1.length - 1;

                  for(int var5 = 0; var4 >= 0; --var4) {
                     if (var1[var4] != -1) {
                        var6[var5++] = var1[var4];
                     }
                  }

                  var1 = var6;
               }
            }
         }

         return var1;
      } else {
         return new int[0];
      }
   }

   public int getMinSelectionRow() {
      return this.listSelectionModel.getMinSelectionIndex();
   }

   public int getMaxSelectionRow() {
      return this.listSelectionModel.getMaxSelectionIndex();
   }

   public boolean isRowSelected(int var1) {
      return this.listSelectionModel.isSelectedIndex(var1);
   }

   public void resetRowSelection() {
      this.listSelectionModel.clearSelection();
      if (this.selection != null && this.rowMapper != null) {
         boolean var2 = false;
         int[] var3 = this.rowMapper.getRowsForPaths(this.selection);
         int var4 = 0;

         for(int var5 = this.selection.length; var4 < var5; ++var4) {
            int var1 = var3[var4];
            if (var1 != -1) {
               this.listSelectionModel.addSelectionInterval(var1, var1);
            }
         }

         if (this.leadIndex != -1 && var3 != null) {
            this.leadRow = var3[this.leadIndex];
         } else if (this.leadPath != null) {
            this.tempPaths[0] = this.leadPath;
            var3 = this.rowMapper.getRowsForPaths(this.tempPaths);
            this.leadRow = var3 != null ? var3[0] : -1;
         } else {
            this.leadRow = -1;
         }

         this.insureRowContinuity();
      } else {
         this.leadRow = -1;
      }

   }

   public int getLeadSelectionRow() {
      return this.leadRow;
   }

   public TreePath getLeadSelectionPath() {
      return this.leadPath;
   }

   public synchronized void addPropertyChangeListener(PropertyChangeListener var1) {
      if (this.changeSupport == null) {
         this.changeSupport = new SwingPropertyChangeSupport(this);
      }

      this.changeSupport.addPropertyChangeListener(var1);
   }

   public synchronized void removePropertyChangeListener(PropertyChangeListener var1) {
      if (this.changeSupport != null) {
         this.changeSupport.removePropertyChangeListener(var1);
      }
   }

   public PropertyChangeListener[] getPropertyChangeListeners() {
      return this.changeSupport == null ? new PropertyChangeListener[0] : this.changeSupport.getPropertyChangeListeners();
   }

   protected void insureRowContinuity() {
      if (this.selectionMode == 2 && this.selection != null && this.rowMapper != null) {
         DefaultListSelectionModel var1 = this.listSelectionModel;
         int var2 = var1.getMinSelectionIndex();
         if (var2 != -1) {
            int var3 = var2;

            for(int var4 = var1.getMaxSelectionIndex(); var3 <= var4; ++var3) {
               if (!var1.isSelectedIndex(var3)) {
                  if (var3 != var2) {
                     TreePath[] var5 = new TreePath[var3 - var2];
                     int[] var6 = this.rowMapper.getRowsForPaths(this.selection);

                     for(int var7 = 0; var7 < var6.length; ++var7) {
                        if (var6[var7] < var3) {
                           var5[var6[var7] - var2] = this.selection[var7];
                        }
                     }

                     this.setSelectionPaths(var5);
                     break;
                  }

                  this.clearSelection();
               }
            }
         }
      } else if (this.selectionMode == 1 && this.selection != null && this.selection.length > 1) {
         this.setSelectionPath(this.selection[0]);
      }

   }

   protected boolean arePathsContiguous(TreePath[] var1) {
      if (this.rowMapper != null && var1.length >= 2) {
         BitSet var2 = new BitSet(32);
         int var6 = var1.length;
         int var7 = 0;
         TreePath[] var8 = new TreePath[]{var1[0]};
         int var5 = this.rowMapper.getRowsForPaths(var8)[0];

         int var4;
         for(var4 = 0; var4 < var6; ++var4) {
            if (var1[var4] != null) {
               var8[0] = var1[var4];
               int[] var9 = this.rowMapper.getRowsForPaths(var8);
               if (var9 == null) {
                  return false;
               }

               int var3 = var9[0];
               if (var3 == -1 || var3 < var5 - var6 || var3 > var5 + var6) {
                  return false;
               }

               if (var3 < var5) {
                  var5 = var3;
               }

               if (!var2.get(var3)) {
                  var2.set(var3);
                  ++var7;
               }
            }
         }

         int var10 = var7 + var5;

         for(var4 = var5; var4 < var10; ++var4) {
            if (!var2.get(var4)) {
               return false;
            }
         }

         return true;
      } else {
         return true;
      }
   }

   protected boolean canPathsBeAdded(TreePath[] var1) {
      if (var1 != null && var1.length != 0 && this.rowMapper != null && this.selection != null && this.selectionMode != 4) {
         BitSet var2 = new BitSet();
         DefaultListSelectionModel var3 = this.listSelectionModel;
         int var6 = var3.getMinSelectionIndex();
         int var7 = var3.getMaxSelectionIndex();
         TreePath[] var8 = new TreePath[1];
         int var5;
         if (var6 != -1) {
            for(var5 = var6; var5 <= var7; ++var5) {
               if (var3.isSelectedIndex(var5)) {
                  var2.set(var5);
               }
            }
         } else {
            var8[0] = var1[0];
            var6 = var7 = this.rowMapper.getRowsForPaths(var8)[0];
         }

         for(var5 = var1.length - 1; var5 >= 0; --var5) {
            if (var1[var5] != null) {
               var8[0] = var1[var5];
               int[] var9 = this.rowMapper.getRowsForPaths(var8);
               if (var9 == null) {
                  return false;
               }

               int var4 = var9[0];
               var6 = Math.min(var4, var6);
               var7 = Math.max(var4, var7);
               if (var4 == -1) {
                  return false;
               }

               var2.set(var4);
            }
         }

         for(var5 = var6; var5 <= var7; ++var5) {
            if (!var2.get(var5)) {
               return false;
            }
         }

         return true;
      } else {
         return true;
      }
   }

   protected boolean canPathsBeRemoved(TreePath[] var1) {
      if (this.rowMapper != null && this.selection != null && this.selectionMode != 4) {
         BitSet var2 = new BitSet();
         int var4 = var1.length;
         int var6 = -1;
         int var7 = 0;
         TreePath[] var8 = new TreePath[1];
         this.lastPaths.clear();

         int var3;
         for(var3 = 0; var3 < var4; ++var3) {
            if (var1[var3] != null) {
               this.lastPaths.put(var1[var3], Boolean.TRUE);
            }
         }

         for(var3 = this.selection.length - 1; var3 >= 0; --var3) {
            if (this.lastPaths.get(this.selection[var3]) == null) {
               var8[0] = this.selection[var3];
               int[] var9 = this.rowMapper.getRowsForPaths(var8);
               if (var9 != null && var9[0] != -1 && !var2.get(var9[0])) {
                  ++var7;
                  if (var6 == -1) {
                     var6 = var9[0];
                  } else {
                     var6 = Math.min(var6, var9[0]);
                  }

                  var2.set(var9[0]);
               }
            }
         }

         this.lastPaths.clear();
         if (var7 > 1) {
            for(var3 = var6 + var7 - 1; var3 >= var6; --var3) {
               if (!var2.get(var3)) {
                  return false;
               }
            }
         }

         return true;
      } else {
         return true;
      }
   }

   /** @deprecated */
   @Deprecated
   protected void notifyPathChange(Vector<?> var1, TreePath var2) {
      int var3 = var1.size();
      boolean[] var4 = new boolean[var3];
      TreePath[] var5 = new TreePath[var3];

      for(int var7 = 0; var7 < var3; ++var7) {
         PathPlaceHolder var6 = (PathPlaceHolder)var1.elementAt(var7);
         var4[var7] = var6.isNew;
         var5[var7] = var6.path;
      }

      TreeSelectionEvent var8 = new TreeSelectionEvent(this, var5, var4, var2, this.leadPath);
      this.fireValueChanged(var8);
   }

   protected void updateLeadIndex() {
      if (this.leadPath != null) {
         if (this.selection == null) {
            this.leadPath = null;
            this.leadIndex = this.leadRow = -1;
         } else {
            this.leadRow = this.leadIndex = -1;

            for(int var1 = this.selection.length - 1; var1 >= 0; --var1) {
               if (this.selection[var1] == this.leadPath) {
                  this.leadIndex = var1;
                  break;
               }
            }
         }
      } else {
         this.leadIndex = -1;
      }

   }

   protected void insureUniqueness() {
   }

   public String toString() {
      int var1 = this.getSelectionCount();
      StringBuffer var2 = new StringBuffer();
      int[] var3;
      if (this.rowMapper != null) {
         var3 = this.rowMapper.getRowsForPaths(this.selection);
      } else {
         var3 = null;
      }

      var2.append(this.getClass().getName() + " " + this.hashCode() + " [ ");

      for(int var4 = 0; var4 < var1; ++var4) {
         if (var3 != null) {
            var2.append(this.selection[var4].toString() + "@" + Integer.toString(var3[var4]) + " ");
         } else {
            var2.append(this.selection[var4].toString() + " ");
         }
      }

      var2.append("]");
      return var2.toString();
   }

   public Object clone() throws CloneNotSupportedException {
      DefaultTreeSelectionModel var1 = (DefaultTreeSelectionModel)super.clone();
      var1.changeSupport = null;
      if (this.selection != null) {
         int var2 = this.selection.length;
         var1.selection = new TreePath[var2];
         System.arraycopy(this.selection, 0, var1.selection, 0, var2);
      }

      var1.listenerList = new EventListenerList();
      var1.listSelectionModel = (DefaultListSelectionModel)this.listSelectionModel.clone();
      var1.uniquePaths = new Hashtable();
      var1.lastPaths = new Hashtable();
      var1.tempPaths = new TreePath[1];
      return var1;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      Object[] var2;
      if (this.rowMapper != null && this.rowMapper instanceof Serializable) {
         var2 = new Object[]{"rowMapper", this.rowMapper};
      } else {
         var2 = new Object[0];
      }

      var1.writeObject(var2);
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      Object[] var2 = (Object[])((Object[])var1.readObject());
      if (var2.length > 0 && var2[0].equals("rowMapper")) {
         this.rowMapper = (RowMapper)var2[1];
      }

   }
}
