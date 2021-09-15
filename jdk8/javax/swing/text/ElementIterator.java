package javax.swing.text;

import java.util.Enumeration;
import java.util.Stack;

public class ElementIterator implements Cloneable {
   private Element root;
   private Stack<ElementIterator.StackItem> elementStack = null;

   public ElementIterator(Document var1) {
      this.root = var1.getDefaultRootElement();
   }

   public ElementIterator(Element var1) {
      this.root = var1;
   }

   public synchronized Object clone() {
      try {
         ElementIterator var1 = new ElementIterator(this.root);
         if (this.elementStack != null) {
            var1.elementStack = new Stack();

            for(int var2 = 0; var2 < this.elementStack.size(); ++var2) {
               ElementIterator.StackItem var3 = (ElementIterator.StackItem)this.elementStack.elementAt(var2);
               ElementIterator.StackItem var4 = (ElementIterator.StackItem)var3.clone();
               var1.elementStack.push(var4);
            }
         }

         return var1;
      } catch (CloneNotSupportedException var5) {
         throw new InternalError(var5);
      }
   }

   public Element first() {
      if (this.root == null) {
         return null;
      } else {
         this.elementStack = new Stack();
         if (this.root.getElementCount() != 0) {
            this.elementStack.push(new ElementIterator.StackItem(this.root));
         }

         return this.root;
      }
   }

   public int depth() {
      return this.elementStack == null ? 0 : this.elementStack.size();
   }

   public Element current() {
      if (this.elementStack == null) {
         return this.first();
      } else if (!this.elementStack.empty()) {
         ElementIterator.StackItem var1 = (ElementIterator.StackItem)this.elementStack.peek();
         Element var2 = var1.getElement();
         int var3 = var1.getIndex();
         return var3 == -1 ? var2 : var2.getElement(var3);
      } else {
         return null;
      }
   }

   public Element next() {
      if (this.elementStack == null) {
         return this.first();
      } else if (this.elementStack.isEmpty()) {
         return null;
      } else {
         ElementIterator.StackItem var1 = (ElementIterator.StackItem)this.elementStack.peek();
         Element var2 = var1.getElement();
         int var3 = var1.getIndex();
         if (var3 + 1 < var2.getElementCount()) {
            Element var5 = var2.getElement(var3 + 1);
            if (var5.isLeaf()) {
               var1.incrementIndex();
            } else {
               this.elementStack.push(new ElementIterator.StackItem(var5));
            }

            return var5;
         } else {
            this.elementStack.pop();
            if (!this.elementStack.isEmpty()) {
               ElementIterator.StackItem var4 = (ElementIterator.StackItem)this.elementStack.peek();
               var4.incrementIndex();
               return this.next();
            } else {
               return null;
            }
         }
      }
   }

   public Element previous() {
      int var1;
      if (this.elementStack != null && (var1 = this.elementStack.size()) != 0) {
         ElementIterator.StackItem var2 = (ElementIterator.StackItem)this.elementStack.peek();
         Element var3 = var2.getElement();
         int var4 = var2.getIndex();
         if (var4 > 0) {
            --var4;
            return this.getDeepestLeaf(var3.getElement(var4));
         } else if (var4 == 0) {
            return var3;
         } else if (var4 == -1) {
            if (var1 == 1) {
               return null;
            } else {
               ElementIterator.StackItem var5 = (ElementIterator.StackItem)this.elementStack.pop();
               var2 = (ElementIterator.StackItem)this.elementStack.peek();
               this.elementStack.push(var5);
               var3 = var2.getElement();
               var4 = var2.getIndex();
               return var4 == -1 ? var3 : this.getDeepestLeaf(var3.getElement(var4));
            }
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private Element getDeepestLeaf(Element var1) {
      if (var1.isLeaf()) {
         return var1;
      } else {
         int var2 = var1.getElementCount();
         return var2 == 0 ? var1 : this.getDeepestLeaf(var1.getElement(var2 - 1));
      }
   }

   private void dumpTree() {
      Element var1;
      while((var1 = this.next()) != null) {
         System.out.println("elem: " + var1.getName());
         AttributeSet var2 = var1.getAttributes();
         String var3 = "";
         Enumeration var4 = var2.getAttributeNames();

         while(var4.hasMoreElements()) {
            Object var5 = var4.nextElement();
            Object var6 = var2.getAttribute(var5);
            if (var6 instanceof AttributeSet) {
               var3 = var3 + var5 + "=**AttributeSet** ";
            } else {
               var3 = var3 + var5 + "=" + var6 + " ";
            }
         }

         System.out.println("attributes: " + var3);
      }

   }

   private class StackItem implements Cloneable {
      Element item;
      int childIndex;

      private StackItem(Element var2) {
         this.item = var2;
         this.childIndex = -1;
      }

      private void incrementIndex() {
         ++this.childIndex;
      }

      private Element getElement() {
         return this.item;
      }

      private int getIndex() {
         return this.childIndex;
      }

      protected Object clone() throws CloneNotSupportedException {
         return super.clone();
      }

      // $FF: synthetic method
      StackItem(Element var2, Object var3) {
         this(var2);
      }
   }
}
