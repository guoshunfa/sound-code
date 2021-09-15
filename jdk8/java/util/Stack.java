package java.util;

public class Stack<E> extends Vector<E> {
   private static final long serialVersionUID = 1224463164541339165L;

   public E push(E var1) {
      this.addElement(var1);
      return var1;
   }

   public synchronized E pop() {
      int var2 = this.size();
      Object var1 = this.peek();
      this.removeElementAt(var2 - 1);
      return var1;
   }

   public synchronized E peek() {
      int var1 = this.size();
      if (var1 == 0) {
         throw new EmptyStackException();
      } else {
         return this.elementAt(var1 - 1);
      }
   }

   public boolean empty() {
      return this.size() == 0;
   }

   public synchronized int search(Object var1) {
      int var2 = this.lastIndexOf(var1);
      return var2 >= 0 ? this.size() - var2 : -1;
   }
}
