package sun.reflect.generics.scope;

import java.lang.reflect.TypeVariable;

public class DummyScope implements Scope {
   private static final DummyScope singleton = new DummyScope();

   private DummyScope() {
   }

   public static DummyScope make() {
      return singleton;
   }

   public TypeVariable<?> lookup(String var1) {
      return null;
   }
}
