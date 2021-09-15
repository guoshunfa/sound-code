package java.util;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import sun.misc.SharedSecrets;

public class HashSet<E> extends AbstractSet<E> implements Set<E>, Cloneable, Serializable {
   static final long serialVersionUID = -5024744406713321676L;
   private transient HashMap<E, Object> map;
   private static final Object PRESENT = new Object();

   public HashSet() {
      this.map = new HashMap();
   }

   public HashSet(Collection<? extends E> var1) {
      this.map = new HashMap(Math.max((int)((float)var1.size() / 0.75F) + 1, 16));
      this.addAll(var1);
   }

   public HashSet(int var1, float var2) {
      this.map = new HashMap(var1, var2);
   }

   public HashSet(int var1) {
      this.map = new HashMap(var1);
   }

   HashSet(int var1, float var2, boolean var3) {
      this.map = new LinkedHashMap(var1, var2);
   }

   public Iterator<E> iterator() {
      return this.map.keySet().iterator();
   }

   public int size() {
      return this.map.size();
   }

   public boolean isEmpty() {
      return this.map.isEmpty();
   }

   public boolean contains(Object var1) {
      return this.map.containsKey(var1);
   }

   public boolean add(E var1) {
      return this.map.put(var1, PRESENT) == null;
   }

   public boolean remove(Object var1) {
      return this.map.remove(var1) == PRESENT;
   }

   public void clear() {
      this.map.clear();
   }

   public Object clone() {
      try {
         HashSet var1 = (HashSet)super.clone();
         var1.map = (HashMap)this.map.clone();
         return var1;
      } catch (CloneNotSupportedException var2) {
         throw new InternalError(var2);
      }
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeInt(this.map.capacity());
      var1.writeFloat(this.map.loadFactor());
      var1.writeInt(this.map.size());
      Iterator var2 = this.map.keySet().iterator();

      while(var2.hasNext()) {
         Object var3 = var2.next();
         var1.writeObject(var3);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      int var2 = var1.readInt();
      if (var2 < 0) {
         throw new InvalidObjectException("Illegal capacity: " + var2);
      } else {
         float var3 = var1.readFloat();
         if (var3 > 0.0F && !Float.isNaN(var3)) {
            int var4 = var1.readInt();
            if (var4 < 0) {
               throw new InvalidObjectException("Illegal size: " + var4);
            } else {
               var2 = (int)Math.min((float)var4 * Math.min(1.0F / var3, 4.0F), 1.07374182E9F);
               SharedSecrets.getJavaOISAccess().checkArray(var1, Map.Entry[].class, HashMap.tableSizeFor(var2));
               this.map = (HashMap)(this instanceof LinkedHashSet ? new LinkedHashMap(var2, var3) : new HashMap(var2, var3));

               for(int var5 = 0; var5 < var4; ++var5) {
                  Object var6 = var1.readObject();
                  this.map.put(var6, PRESENT);
               }

            }
         } else {
            throw new InvalidObjectException("Illegal load factor: " + var3);
         }
      }
   }

   public Spliterator<E> spliterator() {
      return new HashMap.KeySpliterator(this.map, 0, -1, 0, 0);
   }
}
