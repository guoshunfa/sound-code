package java.lang.reflect;

public interface Type {
   default String getTypeName() {
      return this.toString();
   }
}
