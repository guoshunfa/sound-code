package javax.swing.tree;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.EventObject;
import java.util.Vector;
import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.CellEditorListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.FontUIResource;

public class DefaultTreeCellEditor implements ActionListener, TreeCellEditor, TreeSelectionListener {
   protected TreeCellEditor realEditor;
   protected DefaultTreeCellRenderer renderer;
   protected Container editingContainer;
   protected transient Component editingComponent;
   protected boolean canEdit;
   protected transient int offset;
   protected transient JTree tree;
   protected transient TreePath lastPath;
   protected transient Timer timer;
   protected transient int lastRow;
   protected Color borderSelectionColor;
   protected transient Icon editingIcon;
   protected Font font;

   public DefaultTreeCellEditor(JTree var1, DefaultTreeCellRenderer var2) {
      this(var1, var2, (TreeCellEditor)null);
   }

   public DefaultTreeCellEditor(JTree var1, DefaultTreeCellRenderer var2, TreeCellEditor var3) {
      this.renderer = var2;
      this.realEditor = var3;
      if (this.realEditor == null) {
         this.realEditor = this.createTreeCellEditor();
      }

      this.editingContainer = this.createContainer();
      this.setTree(var1);
      this.setBorderSelectionColor(UIManager.getColor("Tree.editorBorderSelectionColor"));
   }

   public void setBorderSelectionColor(Color var1) {
      this.borderSelectionColor = var1;
   }

   public Color getBorderSelectionColor() {
      return this.borderSelectionColor;
   }

   public void setFont(Font var1) {
      this.font = var1;
   }

   public Font getFont() {
      return this.font;
   }

   public Component getTreeCellEditorComponent(JTree var1, Object var2, boolean var3, boolean var4, boolean var5, int var6) {
      this.setTree(var1);
      this.lastRow = var6;
      this.determineOffset(var1, var2, var3, var4, var5, var6);
      if (this.editingComponent != null) {
         this.editingContainer.remove(this.editingComponent);
      }

      this.editingComponent = this.realEditor.getTreeCellEditorComponent(var1, var2, var3, var4, var5, var6);
      TreePath var7 = var1.getPathForRow(var6);
      this.canEdit = this.lastPath != null && var7 != null && this.lastPath.equals(var7);
      Font var8 = this.getFont();
      if (var8 == null) {
         if (this.renderer != null) {
            var8 = this.renderer.getFont();
         }

         if (var8 == null) {
            var8 = var1.getFont();
         }
      }

      this.editingContainer.setFont(var8);
      this.prepareForEditing();
      return this.editingContainer;
   }

   public Object getCellEditorValue() {
      return this.realEditor.getCellEditorValue();
   }

   public boolean isCellEditable(EventObject var1) {
      boolean var2 = false;
      boolean var3 = false;
      if (var1 != null && var1.getSource() instanceof JTree) {
         this.setTree((JTree)var1.getSource());
         if (var1 instanceof MouseEvent) {
            TreePath var4 = this.tree.getPathForLocation(((MouseEvent)var1).getX(), ((MouseEvent)var1).getY());
            var3 = this.lastPath != null && var4 != null && this.lastPath.equals(var4);
            if (var4 != null) {
               this.lastRow = this.tree.getRowForPath(var4);
               Object var5 = var4.getLastPathComponent();
               boolean var6 = this.tree.isRowSelected(this.lastRow);
               boolean var7 = this.tree.isExpanded(var4);
               TreeModel var8 = this.tree.getModel();
               boolean var9 = var8.isLeaf(var5);
               this.determineOffset(this.tree, var5, var6, var7, var9, this.lastRow);
            }
         }
      }

      if (!this.realEditor.isCellEditable(var1)) {
         return false;
      } else {
         if (this.canEditImmediately(var1)) {
            var2 = true;
         } else if (var3 && this.shouldStartEditingTimer(var1)) {
            this.startEditingTimer();
         } else if (this.timer != null && this.timer.isRunning()) {
            this.timer.stop();
         }

         if (var2) {
            this.prepareForEditing();
         }

         return var2;
      }
   }

   public boolean shouldSelectCell(EventObject var1) {
      return this.realEditor.shouldSelectCell(var1);
   }

   public boolean stopCellEditing() {
      if (this.realEditor.stopCellEditing()) {
         this.cleanupAfterEditing();
         return true;
      } else {
         return false;
      }
   }

   public void cancelCellEditing() {
      this.realEditor.cancelCellEditing();
      this.cleanupAfterEditing();
   }

   public void addCellEditorListener(CellEditorListener var1) {
      this.realEditor.addCellEditorListener(var1);
   }

   public void removeCellEditorListener(CellEditorListener var1) {
      this.realEditor.removeCellEditorListener(var1);
   }

   public CellEditorListener[] getCellEditorListeners() {
      return ((DefaultCellEditor)this.realEditor).getCellEditorListeners();
   }

   public void valueChanged(TreeSelectionEvent var1) {
      if (this.tree != null) {
         if (this.tree.getSelectionCount() == 1) {
            this.lastPath = this.tree.getSelectionPath();
         } else {
            this.lastPath = null;
         }
      }

      if (this.timer != null) {
         this.timer.stop();
      }

   }

   public void actionPerformed(ActionEvent var1) {
      if (this.tree != null && this.lastPath != null) {
         this.tree.startEditingAtPath(this.lastPath);
      }

   }

   protected void setTree(JTree var1) {
      if (this.tree != var1) {
         if (this.tree != null) {
            this.tree.removeTreeSelectionListener(this);
         }

         this.tree = var1;
         if (this.tree != null) {
            this.tree.addTreeSelectionListener(this);
         }

         if (this.timer != null) {
            this.timer.stop();
         }
      }

   }

   protected boolean shouldStartEditingTimer(EventObject var1) {
      if (var1 instanceof MouseEvent && SwingUtilities.isLeftMouseButton((MouseEvent)var1)) {
         MouseEvent var2 = (MouseEvent)var1;
         return var2.getClickCount() == 1 && this.inHitRegion(var2.getX(), var2.getY());
      } else {
         return false;
      }
   }

   protected void startEditingTimer() {
      if (this.timer == null) {
         this.timer = new Timer(1200, this);
         this.timer.setRepeats(false);
      }

      this.timer.start();
   }

   protected boolean canEditImmediately(EventObject var1) {
      if (var1 instanceof MouseEvent && SwingUtilities.isLeftMouseButton((MouseEvent)var1)) {
         MouseEvent var2 = (MouseEvent)var1;
         return var2.getClickCount() > 2 && this.inHitRegion(var2.getX(), var2.getY());
      } else {
         return var1 == null;
      }
   }

   protected boolean inHitRegion(int var1, int var2) {
      if (this.lastRow != -1 && this.tree != null) {
         Rectangle var3 = this.tree.getRowBounds(this.lastRow);
         ComponentOrientation var4 = this.tree.getComponentOrientation();
         if (var4.isLeftToRight()) {
            if (var3 != null && var1 <= var3.x + this.offset && this.offset < var3.width - 5) {
               return false;
            }
         } else if (var3 != null && (var1 >= var3.x + var3.width - this.offset + 5 || var1 <= var3.x + 5) && this.offset < var3.width - 5) {
            return false;
         }
      }

      return true;
   }

   protected void determineOffset(JTree var1, Object var2, boolean var3, boolean var4, boolean var5, int var6) {
      if (this.renderer != null) {
         if (var5) {
            this.editingIcon = this.renderer.getLeafIcon();
         } else if (var4) {
            this.editingIcon = this.renderer.getOpenIcon();
         } else {
            this.editingIcon = this.renderer.getClosedIcon();
         }

         if (this.editingIcon != null) {
            this.offset = this.renderer.getIconTextGap() + this.editingIcon.getIconWidth();
         } else {
            this.offset = this.renderer.getIconTextGap();
         }
      } else {
         this.editingIcon = null;
         this.offset = 0;
      }

   }

   protected void prepareForEditing() {
      if (this.editingComponent != null) {
         this.editingContainer.add(this.editingComponent);
      }

   }

   protected Container createContainer() {
      return new DefaultTreeCellEditor.EditorContainer();
   }

   protected TreeCellEditor createTreeCellEditor() {
      Border var1 = UIManager.getBorder("Tree.editorBorder");
      DefaultCellEditor var2 = new DefaultCellEditor(new DefaultTreeCellEditor.DefaultTextField(var1)) {
         public boolean shouldSelectCell(EventObject var1) {
            boolean var2 = super.shouldSelectCell(var1);
            return var2;
         }
      };
      var2.setClickCountToStart(1);
      return var2;
   }

   private void cleanupAfterEditing() {
      if (this.editingComponent != null) {
         this.editingContainer.remove(this.editingComponent);
      }

      this.editingComponent = null;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      Vector var2 = new Vector();
      var1.defaultWriteObject();
      if (this.realEditor != null && this.realEditor instanceof Serializable) {
         var2.addElement("realEditor");
         var2.addElement(this.realEditor);
      }

      var1.writeObject(var2);
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      Vector var2 = (Vector)var1.readObject();
      byte var3 = 0;
      int var4 = var2.size();
      if (var3 < var4 && var2.elementAt(var3).equals("realEditor")) {
         int var5 = var3 + 1;
         this.realEditor = (TreeCellEditor)var2.elementAt(var5);
         ++var5;
      }

   }

   public class EditorContainer extends Container {
      public EditorContainer() {
         this.setLayout((LayoutManager)null);
      }

      public void EditorContainer() {
         this.setLayout((LayoutManager)null);
      }

      public void paint(Graphics var1) {
         int var2 = this.getWidth();
         int var3 = this.getHeight();
         if (DefaultTreeCellEditor.this.editingIcon != null) {
            int var4 = this.calculateIconY(DefaultTreeCellEditor.this.editingIcon);
            if (this.getComponentOrientation().isLeftToRight()) {
               DefaultTreeCellEditor.this.editingIcon.paintIcon(this, var1, 0, var4);
            } else {
               DefaultTreeCellEditor.this.editingIcon.paintIcon(this, var1, var2 - DefaultTreeCellEditor.this.editingIcon.getIconWidth(), var4);
            }
         }

         Color var5 = DefaultTreeCellEditor.this.getBorderSelectionColor();
         if (var5 != null) {
            var1.setColor(var5);
            var1.drawRect(0, 0, var2 - 1, var3 - 1);
         }

         super.paint(var1);
      }

      public void doLayout() {
         if (DefaultTreeCellEditor.this.editingComponent != null) {
            int var1 = this.getWidth();
            int var2 = this.getHeight();
            if (this.getComponentOrientation().isLeftToRight()) {
               DefaultTreeCellEditor.this.editingComponent.setBounds(DefaultTreeCellEditor.this.offset, 0, var1 - DefaultTreeCellEditor.this.offset, var2);
            } else {
               DefaultTreeCellEditor.this.editingComponent.setBounds(0, 0, var1 - DefaultTreeCellEditor.this.offset, var2);
            }
         }

      }

      private int calculateIconY(Icon var1) {
         int var2 = var1.getIconHeight();
         int var3 = DefaultTreeCellEditor.this.editingComponent.getFontMetrics(DefaultTreeCellEditor.this.editingComponent.getFont()).getHeight();
         int var4 = var2 / 2 - var3 / 2;
         int var5 = Math.min(0, var4);
         int var6 = Math.max(var2, var4 + var3) - var5;
         return this.getHeight() / 2 - (var5 + var6 / 2);
      }

      public Dimension getPreferredSize() {
         if (DefaultTreeCellEditor.this.editingComponent != null) {
            Dimension var1 = DefaultTreeCellEditor.this.editingComponent.getPreferredSize();
            var1.width += DefaultTreeCellEditor.this.offset + 5;
            Dimension var2 = DefaultTreeCellEditor.this.renderer != null ? DefaultTreeCellEditor.this.renderer.getPreferredSize() : null;
            if (var2 != null) {
               var1.height = Math.max(var1.height, var2.height);
            }

            if (DefaultTreeCellEditor.this.editingIcon != null) {
               var1.height = Math.max(var1.height, DefaultTreeCellEditor.this.editingIcon.getIconHeight());
            }

            var1.width = Math.max(var1.width, 100);
            return var1;
         } else {
            return new Dimension(0, 0);
         }
      }
   }

   public class DefaultTextField extends JTextField {
      protected Border border;

      public DefaultTextField(Border var2) {
         this.setBorder(var2);
      }

      public void setBorder(Border var1) {
         super.setBorder(var1);
         this.border = var1;
      }

      public Border getBorder() {
         return this.border;
      }

      public Font getFont() {
         Font var1 = super.getFont();
         if (var1 instanceof FontUIResource) {
            Container var2 = this.getParent();
            if (var2 != null && var2.getFont() != null) {
               var1 = var2.getFont();
            }
         }

         return var1;
      }

      public Dimension getPreferredSize() {
         Dimension var1 = super.getPreferredSize();
         if (DefaultTreeCellEditor.this.renderer != null && DefaultTreeCellEditor.this.getFont() == null) {
            Dimension var2 = DefaultTreeCellEditor.this.renderer.getPreferredSize();
            var1.height = var2.height;
         }

         return var1;
      }
   }
}
