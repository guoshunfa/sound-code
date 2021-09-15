package javax.swing.undo;

import java.util.Hashtable;

public interface StateEditable {
   String RCSID = "$Id: StateEditable.java,v 1.2 1997/09/08 19:39:08 marklin Exp $";

   void storeState(Hashtable<Object, Object> var1);

   void restoreState(Hashtable<?, ?> var1);
}
