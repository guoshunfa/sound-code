package javax.swing.tree;

import java.awt.Component;
import javax.swing.CellEditor;
import javax.swing.JTree;

public interface TreeCellEditor extends CellEditor {
   Component getTreeCellEditorComponent(JTree var1, Object var2, boolean var3, boolean var4, boolean var5, int var6);
}
