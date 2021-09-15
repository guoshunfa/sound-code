package java.awt;

public abstract class FocusTraversalPolicy {
   public abstract Component getComponentAfter(Container var1, Component var2);

   public abstract Component getComponentBefore(Container var1, Component var2);

   public abstract Component getFirstComponent(Container var1);

   public abstract Component getLastComponent(Container var1);

   public abstract Component getDefaultComponent(Container var1);

   public Component getInitialComponent(Window var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("window cannot be equal to null.");
      } else {
         Object var2 = this.getDefaultComponent(var1);
         if (var2 == null && var1.isFocusableWindow()) {
            var2 = var1;
         }

         return (Component)var2;
      }
   }
}
