package java.beans.beancontext;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class BeanContextMembershipEvent extends BeanContextEvent {
   private static final long serialVersionUID = 3499346510334590959L;
   protected Collection children;

   public BeanContextMembershipEvent(BeanContext var1, Collection var2) {
      super(var1);
      if (var2 == null) {
         throw new NullPointerException("BeanContextMembershipEvent constructor:  changes is null.");
      } else {
         this.children = var2;
      }
   }

   public BeanContextMembershipEvent(BeanContext var1, Object[] var2) {
      super(var1);
      if (var2 == null) {
         throw new NullPointerException("BeanContextMembershipEvent:  changes is null.");
      } else {
         this.children = Arrays.asList(var2);
      }
   }

   public int size() {
      return this.children.size();
   }

   public boolean contains(Object var1) {
      return this.children.contains(var1);
   }

   public Object[] toArray() {
      return this.children.toArray();
   }

   public Iterator iterator() {
      return this.children.iterator();
   }
}
