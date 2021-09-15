package java.beans;

public class IndexedPropertyChangeEvent extends PropertyChangeEvent {
   private static final long serialVersionUID = -320227448495806870L;
   private int index;

   public IndexedPropertyChangeEvent(Object var1, String var2, Object var3, Object var4, int var5) {
      super(var1, var2, var3, var4);
      this.index = var5;
   }

   public int getIndex() {
      return this.index;
   }

   void appendTo(StringBuilder var1) {
      var1.append("; index=").append(this.getIndex());
   }
}
