package java.text;

public class Annotation {
   private Object value;

   public Annotation(Object var1) {
      this.value = var1;
   }

   public Object getValue() {
      return this.value;
   }

   public String toString() {
      return this.getClass().getName() + "[value=" + this.value + "]";
   }
}
