package javax.swing;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.beans.ConstructorProperties;
import java.io.Serializable;
import java.util.EventObject;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.TreeCellEditor;

public class DefaultCellEditor extends AbstractCellEditor implements TableCellEditor, TreeCellEditor {
   protected JComponent editorComponent;
   protected DefaultCellEditor.EditorDelegate delegate;
   protected int clickCountToStart = 1;

   @ConstructorProperties({"component"})
   public DefaultCellEditor(final JTextField var1) {
      this.editorComponent = var1;
      this.clickCountToStart = 2;
      this.delegate = new DefaultCellEditor.EditorDelegate() {
         public void setValue(Object var1x) {
            var1.setText(var1x != null ? var1x.toString() : "");
         }

         public Object getCellEditorValue() {
            return var1.getText();
         }
      };
      var1.addActionListener(this.delegate);
   }

   public DefaultCellEditor(final JCheckBox var1) {
      this.editorComponent = var1;
      this.delegate = new DefaultCellEditor.EditorDelegate() {
         public void setValue(Object var1x) {
            boolean var2 = false;
            if (var1x instanceof Boolean) {
               var2 = (Boolean)var1x;
            } else if (var1x instanceof String) {
               var2 = var1x.equals("true");
            }

            var1.setSelected(var2);
         }

         public Object getCellEditorValue() {
            return var1.isSelected();
         }
      };
      var1.addActionListener(this.delegate);
      var1.setRequestFocusEnabled(false);
   }

   public DefaultCellEditor(final JComboBox var1) {
      this.editorComponent = var1;
      var1.putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);
      this.delegate = new DefaultCellEditor.EditorDelegate() {
         public void setValue(Object var1x) {
            var1.setSelectedItem(var1x);
         }

         public Object getCellEditorValue() {
            return var1.getSelectedItem();
         }

         public boolean shouldSelectCell(EventObject var1x) {
            if (var1x instanceof MouseEvent) {
               MouseEvent var2 = (MouseEvent)var1x;
               return var2.getID() != 506;
            } else {
               return true;
            }
         }

         public boolean stopCellEditing() {
            if (var1.isEditable()) {
               var1.actionPerformed(new ActionEvent(DefaultCellEditor.this, 0, ""));
            }

            return super.stopCellEditing();
         }
      };
      var1.addActionListener(this.delegate);
   }

   public Component getComponent() {
      return this.editorComponent;
   }

   public void setClickCountToStart(int var1) {
      this.clickCountToStart = var1;
   }

   public int getClickCountToStart() {
      return this.clickCountToStart;
   }

   public Object getCellEditorValue() {
      return this.delegate.getCellEditorValue();
   }

   public boolean isCellEditable(EventObject var1) {
      return this.delegate.isCellEditable(var1);
   }

   public boolean shouldSelectCell(EventObject var1) {
      return this.delegate.shouldSelectCell(var1);
   }

   public boolean stopCellEditing() {
      return this.delegate.stopCellEditing();
   }

   public void cancelCellEditing() {
      this.delegate.cancelCellEditing();
   }

   public Component getTreeCellEditorComponent(JTree var1, Object var2, boolean var3, boolean var4, boolean var5, int var6) {
      String var7 = var1.convertValueToText(var2, var3, var4, var5, var6, false);
      this.delegate.setValue(var7);
      return this.editorComponent;
   }

   public Component getTableCellEditorComponent(JTable var1, Object var2, boolean var3, int var4, int var5) {
      this.delegate.setValue(var2);
      if (this.editorComponent instanceof JCheckBox) {
         TableCellRenderer var6 = var1.getCellRenderer(var4, var5);
         Component var7 = var6.getTableCellRendererComponent(var1, var2, var3, true, var4, var5);
         if (var7 != null) {
            this.editorComponent.setOpaque(true);
            this.editorComponent.setBackground(var7.getBackground());
            if (var7 instanceof JComponent) {
               this.editorComponent.setBorder(((JComponent)var7).getBorder());
            }
         } else {
            this.editorComponent.setOpaque(false);
         }
      }

      return this.editorComponent;
   }

   protected class EditorDelegate implements ActionListener, ItemListener, Serializable {
      protected Object value;

      public Object getCellEditorValue() {
         return this.value;
      }

      public void setValue(Object var1) {
         this.value = var1;
      }

      public boolean isCellEditable(EventObject var1) {
         if (var1 instanceof MouseEvent) {
            return ((MouseEvent)var1).getClickCount() >= DefaultCellEditor.this.clickCountToStart;
         } else {
            return true;
         }
      }

      public boolean shouldSelectCell(EventObject var1) {
         return true;
      }

      public boolean startCellEditing(EventObject var1) {
         return true;
      }

      public boolean stopCellEditing() {
         DefaultCellEditor.this.fireEditingStopped();
         return true;
      }

      public void cancelCellEditing() {
         DefaultCellEditor.this.fireEditingCanceled();
      }

      public void actionPerformed(ActionEvent var1) {
         DefaultCellEditor.this.stopCellEditing();
      }

      public void itemStateChanged(ItemEvent var1) {
         DefaultCellEditor.this.stopCellEditing();
      }
   }
}
