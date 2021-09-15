package java.util;

import java.io.Serializable;

public class LinkedHashSet<E> extends HashSet<E> implements Set<E>, Cloneable, Serializable {
   private static final long serialVersionUID = -2851667679971038690L;

   public LinkedHashSet(int var1, float var2) {
      super(var1, var2, true);
   }

   public LinkedHashSet(int var1) {
      super(var1, 0.75F, true);
   }

   public LinkedHashSet() {
      super(16, 0.75F, true);
   }

   public LinkedHashSet(Collection<? extends E> var1) {
      super(Math.max(2 * var1.size(), 11), 0.75F, true);
      this.addAll(var1);
   }

   public Spliterator<E> spliterator() {
      return Spliterators.spliterator((Collection)this, 17);
   }
}
