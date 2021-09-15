package javax.swing;

public class ComponentInputMap extends InputMap {
   private JComponent component;

   public ComponentInputMap(JComponent var1) {
      this.component = var1;
      if (var1 == null) {
         throw new IllegalArgumentException("ComponentInputMaps must be associated with a non-null JComponent");
      }
   }

   public void setParent(InputMap var1) {
      if (this.getParent() != var1) {
         if (var1 == null || var1 instanceof ComponentInputMap && ((ComponentInputMap)var1).getComponent() == this.getComponent()) {
            super.setParent(var1);
            this.getComponent().componentInputMapChanged(this);
         } else {
            throw new IllegalArgumentException("ComponentInputMaps must have a parent ComponentInputMap associated with the same component");
         }
      }
   }

   public JComponent getComponent() {
      return this.component;
   }

   public void put(KeyStroke var1, Object var2) {
      super.put(var1, var2);
      if (this.getComponent() != null) {
         this.getComponent().componentInputMapChanged(this);
      }

   }

   public void remove(KeyStroke var1) {
      super.remove(var1);
      if (this.getComponent() != null) {
         this.getComponent().componentInputMapChanged(this);
      }

   }

   public void clear() {
      int var1 = this.size();
      super.clear();
      if (var1 > 0 && this.getComponent() != null) {
         this.getComponent().componentInputMapChanged(this);
      }

   }
}
