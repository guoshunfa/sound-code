package javax.swing.undo;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class StateEdit extends AbstractUndoableEdit {
   protected static final String RCSID = "$Id: StateEdit.java,v 1.6 1997/10/01 20:05:51 sandipc Exp $";
   protected StateEditable object;
   protected Hashtable<Object, Object> preState;
   protected Hashtable<Object, Object> postState;
   protected String undoRedoName;

   public StateEdit(StateEditable var1) {
      this.init(var1, (String)null);
   }

   public StateEdit(StateEditable var1, String var2) {
      this.init(var1, var2);
   }

   protected void init(StateEditable var1, String var2) {
      this.object = var1;
      this.preState = new Hashtable(11);
      this.object.storeState(this.preState);
      this.postState = null;
      this.undoRedoName = var2;
   }

   public void end() {
      this.postState = new Hashtable(11);
      this.object.storeState(this.postState);
      this.removeRedundantState();
   }

   public void undo() {
      super.undo();
      this.object.restoreState(this.preState);
   }

   public void redo() {
      super.redo();
      this.object.restoreState(this.postState);
   }

   public String getPresentationName() {
      return this.undoRedoName;
   }

   protected void removeRedundantState() {
      Vector var1 = new Vector();
      Enumeration var2 = this.preState.keys();

      while(var2.hasMoreElements()) {
         Object var3 = var2.nextElement();
         if (this.postState.containsKey(var3) && this.postState.get(var3).equals(this.preState.get(var3))) {
            var1.addElement(var3);
         }
      }

      for(int var5 = var1.size() - 1; var5 >= 0; --var5) {
         Object var4 = var1.elementAt(var5);
         this.preState.remove(var4);
         this.postState.remove(var4);
      }

   }
}
