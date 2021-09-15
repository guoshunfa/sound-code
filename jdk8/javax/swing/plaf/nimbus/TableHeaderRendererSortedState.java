package javax.swing.plaf.nimbus;

import javax.swing.JComponent;

class TableHeaderRendererSortedState extends State {
   TableHeaderRendererSortedState() {
      super("Sorted");
   }

   protected boolean isInState(JComponent var1) {
      String var2 = (String)var1.getClientProperty("Table.sortOrder");
      return var2 != null && ("ASCENDING".equals(var2) || "DESCENDING".equals(var2));
   }
}
