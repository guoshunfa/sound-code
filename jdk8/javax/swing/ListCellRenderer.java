package javax.swing;

import java.awt.Component;

public interface ListCellRenderer<E> {
   Component getListCellRendererComponent(JList<? extends E> var1, E var2, int var3, boolean var4, boolean var5);
}
