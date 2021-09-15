package javax.accessibility;

public interface AccessibleValue {
   Number getCurrentAccessibleValue();

   boolean setCurrentAccessibleValue(Number var1);

   Number getMinimumAccessibleValue();

   Number getMaximumAccessibleValue();
}
