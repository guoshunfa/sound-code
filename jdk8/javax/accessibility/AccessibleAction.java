package javax.accessibility;

public interface AccessibleAction {
   String TOGGLE_EXPAND = new String("toggleexpand");
   String INCREMENT = new String("increment");
   String DECREMENT = new String("decrement");
   String CLICK = new String("click");
   String TOGGLE_POPUP = new String("toggle popup");

   int getAccessibleActionCount();

   String getAccessibleActionDescription(int var1);

   boolean doAccessibleAction(int var1);
}
