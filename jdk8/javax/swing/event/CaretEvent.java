package javax.swing.event;

import java.util.EventObject;

public abstract class CaretEvent extends EventObject {
   public CaretEvent(Object var1) {
      super(var1);
   }

   public abstract int getDot();

   public abstract int getMark();
}
