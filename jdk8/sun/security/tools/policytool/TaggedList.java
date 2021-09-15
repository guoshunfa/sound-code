package sun.security.tools.policytool;

import java.util.LinkedList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListModel;

class TaggedList extends JList {
   private static final long serialVersionUID = -5676238110427785853L;
   private List<Object> data = new LinkedList();

   public TaggedList(int var1, boolean var2) {
      super((ListModel)(new DefaultListModel()));
      this.setVisibleRowCount(var1);
      this.setSelectionMode(var2 ? 2 : 0);
   }

   public Object getObject(int var1) {
      return this.data.get(var1);
   }

   public void addTaggedItem(String var1, Object var2) {
      ((DefaultListModel)this.getModel()).addElement(var1);
      this.data.add(var2);
   }

   public void replaceTaggedItem(String var1, Object var2, int var3) {
      ((DefaultListModel)this.getModel()).set(var3, var1);
      this.data.set(var3, var2);
   }

   public void removeTaggedItem(int var1) {
      ((DefaultListModel)this.getModel()).remove(var1);
      this.data.remove(var1);
   }
}
