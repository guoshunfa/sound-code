package javax.accessibility;

public abstract class AccessibleHyperlink implements AccessibleAction {
   public abstract boolean isValid();

   public abstract int getAccessibleActionCount();

   public abstract boolean doAccessibleAction(int var1);

   public abstract String getAccessibleActionDescription(int var1);

   public abstract Object getAccessibleActionObject(int var1);

   public abstract Object getAccessibleActionAnchor(int var1);

   public abstract int getStartIndex();

   public abstract int getEndIndex();
}
