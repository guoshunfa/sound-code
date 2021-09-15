package java.util;

public abstract class AbstractSet<E> extends AbstractCollection<E> implements Set<E> {
   protected AbstractSet() {
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof Set)) {
         return false;
      } else {
         Collection var2 = (Collection)var1;
         if (var2.size() != this.size()) {
            return false;
         } else {
            try {
               return this.containsAll(var2);
            } catch (ClassCastException var4) {
               return false;
            } catch (NullPointerException var5) {
               return false;
            }
         }
      }
   }

   public int hashCode() {
      int var1 = 0;
      Iterator var2 = this.iterator();

      while(var2.hasNext()) {
         Object var3 = var2.next();
         if (var3 != null) {
            var1 += var3.hashCode();
         }
      }

      return var1;
   }

   public boolean removeAll(Collection<?> var1) {
      Objects.requireNonNull(var1);
      boolean var2 = false;
      Iterator var3;
      if (this.size() > var1.size()) {
         for(var3 = var1.iterator(); var3.hasNext(); var2 |= this.remove(var3.next())) {
         }
      } else {
         var3 = this.iterator();

         while(var3.hasNext()) {
            if (var1.contains(var3.next())) {
               var3.remove();
               var2 = true;
            }
         }
      }

      return var2;
   }
}
