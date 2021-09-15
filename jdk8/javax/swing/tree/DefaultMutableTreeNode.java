package javax.swing.tree;

import java.beans.Transient;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.EmptyStackException;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Stack;
import java.util.Vector;

public class DefaultMutableTreeNode implements Cloneable, MutableTreeNode, Serializable {
   private static final long serialVersionUID = -4298474751201349152L;
   public static final Enumeration<TreeNode> EMPTY_ENUMERATION = Collections.emptyEnumeration();
   protected MutableTreeNode parent;
   protected Vector children;
   protected transient Object userObject;
   protected boolean allowsChildren;

   public DefaultMutableTreeNode() {
      this((Object)null);
   }

   public DefaultMutableTreeNode(Object var1) {
      this(var1, true);
   }

   public DefaultMutableTreeNode(Object var1, boolean var2) {
      this.parent = null;
      this.allowsChildren = var2;
      this.userObject = var1;
   }

   public void insert(MutableTreeNode var1, int var2) {
      if (!this.allowsChildren) {
         throw new IllegalStateException("node does not allow children");
      } else if (var1 == null) {
         throw new IllegalArgumentException("new child is null");
      } else if (this.isNodeAncestor(var1)) {
         throw new IllegalArgumentException("new child is an ancestor");
      } else {
         MutableTreeNode var3 = (MutableTreeNode)var1.getParent();
         if (var3 != null) {
            var3.remove(var1);
         }

         var1.setParent(this);
         if (this.children == null) {
            this.children = new Vector();
         }

         this.children.insertElementAt(var1, var2);
      }
   }

   public void remove(int var1) {
      MutableTreeNode var2 = (MutableTreeNode)this.getChildAt(var1);
      this.children.removeElementAt(var1);
      var2.setParent((MutableTreeNode)null);
   }

   @Transient
   public void setParent(MutableTreeNode var1) {
      this.parent = var1;
   }

   public TreeNode getParent() {
      return this.parent;
   }

   public TreeNode getChildAt(int var1) {
      if (this.children == null) {
         throw new ArrayIndexOutOfBoundsException("node has no children");
      } else {
         return (TreeNode)this.children.elementAt(var1);
      }
   }

   public int getChildCount() {
      return this.children == null ? 0 : this.children.size();
   }

   public int getIndex(TreeNode var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("argument is null");
      } else {
         return !this.isNodeChild(var1) ? -1 : this.children.indexOf(var1);
      }
   }

   public Enumeration children() {
      return this.children == null ? EMPTY_ENUMERATION : this.children.elements();
   }

   public void setAllowsChildren(boolean var1) {
      if (var1 != this.allowsChildren) {
         this.allowsChildren = var1;
         if (!this.allowsChildren) {
            this.removeAllChildren();
         }
      }

   }

   public boolean getAllowsChildren() {
      return this.allowsChildren;
   }

   public void setUserObject(Object var1) {
      this.userObject = var1;
   }

   public Object getUserObject() {
      return this.userObject;
   }

   public void removeFromParent() {
      MutableTreeNode var1 = (MutableTreeNode)this.getParent();
      if (var1 != null) {
         var1.remove(this);
      }

   }

   public void remove(MutableTreeNode var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("argument is null");
      } else if (!this.isNodeChild(var1)) {
         throw new IllegalArgumentException("argument is not a child");
      } else {
         this.remove(this.getIndex(var1));
      }
   }

   public void removeAllChildren() {
      for(int var1 = this.getChildCount() - 1; var1 >= 0; --var1) {
         this.remove(var1);
      }

   }

   public void add(MutableTreeNode var1) {
      if (var1 != null && var1.getParent() == this) {
         this.insert(var1, this.getChildCount() - 1);
      } else {
         this.insert(var1, this.getChildCount());
      }

   }

   public boolean isNodeAncestor(TreeNode var1) {
      if (var1 == null) {
         return false;
      } else {
         Object var2 = this;

         while(var2 != var1) {
            if ((var2 = ((TreeNode)var2).getParent()) == null) {
               return false;
            }
         }

         return true;
      }
   }

   public boolean isNodeDescendant(DefaultMutableTreeNode var1) {
      return var1 == null ? false : var1.isNodeAncestor(this);
   }

   public TreeNode getSharedAncestor(DefaultMutableTreeNode var1) {
      if (var1 == this) {
         return this;
      } else if (var1 == null) {
         return null;
      } else {
         int var2 = this.getLevel();
         int var3 = var1.getLevel();
         int var4;
         Object var5;
         Object var6;
         if (var3 > var2) {
            var4 = var3 - var2;
            var5 = var1;
            var6 = this;
         } else {
            var4 = var2 - var3;
            var5 = this;
            var6 = var1;
         }

         while(var4 > 0) {
            var5 = ((TreeNode)var5).getParent();
            --var4;
         }

         while(var5 != var6) {
            var5 = ((TreeNode)var5).getParent();
            var6 = ((TreeNode)var6).getParent();
            if (var5 == null) {
               if (var5 == null && var6 == null) {
                  return null;
               }

               throw new Error("nodes should be null");
            }
         }

         return (TreeNode)var5;
      }
   }

   public boolean isNodeRelated(DefaultMutableTreeNode var1) {
      return var1 != null && this.getRoot() == var1.getRoot();
   }

   public int getDepth() {
      Object var1 = null;

      for(Enumeration var2 = this.breadthFirstEnumeration(); var2.hasMoreElements(); var1 = var2.nextElement()) {
      }

      if (var1 == null) {
         throw new Error("nodes should be null");
      } else {
         return ((DefaultMutableTreeNode)var1).getLevel() - this.getLevel();
      }
   }

   public int getLevel() {
      int var2 = 0;

      for(Object var1 = this; (var1 = ((TreeNode)var1).getParent()) != null; ++var2) {
      }

      return var2;
   }

   public TreeNode[] getPath() {
      return this.getPathToRoot(this, 0);
   }

   protected TreeNode[] getPathToRoot(TreeNode var1, int var2) {
      TreeNode[] var3;
      if (var1 == null) {
         if (var2 == 0) {
            return null;
         }

         var3 = new TreeNode[var2];
      } else {
         ++var2;
         var3 = this.getPathToRoot(var1.getParent(), var2);
         var3[var3.length - var2] = var1;
      }

      return var3;
   }

   public Object[] getUserObjectPath() {
      TreeNode[] var1 = this.getPath();
      Object[] var2 = new Object[var1.length];

      for(int var3 = 0; var3 < var1.length; ++var3) {
         var2[var3] = ((DefaultMutableTreeNode)var1[var3]).getUserObject();
      }

      return var2;
   }

   public TreeNode getRoot() {
      Object var1 = this;

      Object var2;
      do {
         var2 = var1;
         var1 = ((TreeNode)var1).getParent();
      } while(var1 != null);

      return (TreeNode)var2;
   }

   public boolean isRoot() {
      return this.getParent() == null;
   }

   public DefaultMutableTreeNode getNextNode() {
      if (this.getChildCount() != 0) {
         return (DefaultMutableTreeNode)this.getChildAt(0);
      } else {
         DefaultMutableTreeNode var1 = this.getNextSibling();
         if (var1 != null) {
            return var1;
         } else {
            for(DefaultMutableTreeNode var2 = (DefaultMutableTreeNode)this.getParent(); var2 != null; var2 = (DefaultMutableTreeNode)var2.getParent()) {
               var1 = var2.getNextSibling();
               if (var1 != null) {
                  return var1;
               }
            }

            return null;
         }
      }
   }

   public DefaultMutableTreeNode getPreviousNode() {
      DefaultMutableTreeNode var2 = (DefaultMutableTreeNode)this.getParent();
      if (var2 == null) {
         return null;
      } else {
         DefaultMutableTreeNode var1 = this.getPreviousSibling();
         if (var1 != null) {
            return var1.getChildCount() == 0 ? var1 : var1.getLastLeaf();
         } else {
            return var2;
         }
      }
   }

   public Enumeration preorderEnumeration() {
      return new DefaultMutableTreeNode.PreorderEnumeration(this);
   }

   public Enumeration postorderEnumeration() {
      return new DefaultMutableTreeNode.PostorderEnumeration(this);
   }

   public Enumeration breadthFirstEnumeration() {
      return new DefaultMutableTreeNode.BreadthFirstEnumeration(this);
   }

   public Enumeration depthFirstEnumeration() {
      return this.postorderEnumeration();
   }

   public Enumeration pathFromAncestorEnumeration(TreeNode var1) {
      return new DefaultMutableTreeNode.PathBetweenNodesEnumeration(var1, this);
   }

   public boolean isNodeChild(TreeNode var1) {
      boolean var2;
      if (var1 == null) {
         var2 = false;
      } else if (this.getChildCount() == 0) {
         var2 = false;
      } else {
         var2 = var1.getParent() == this;
      }

      return var2;
   }

   public TreeNode getFirstChild() {
      if (this.getChildCount() == 0) {
         throw new NoSuchElementException("node has no children");
      } else {
         return this.getChildAt(0);
      }
   }

   public TreeNode getLastChild() {
      if (this.getChildCount() == 0) {
         throw new NoSuchElementException("node has no children");
      } else {
         return this.getChildAt(this.getChildCount() - 1);
      }
   }

   public TreeNode getChildAfter(TreeNode var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("argument is null");
      } else {
         int var2 = this.getIndex(var1);
         if (var2 == -1) {
            throw new IllegalArgumentException("node is not a child");
         } else {
            return var2 < this.getChildCount() - 1 ? this.getChildAt(var2 + 1) : null;
         }
      }
   }

   public TreeNode getChildBefore(TreeNode var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("argument is null");
      } else {
         int var2 = this.getIndex(var1);
         if (var2 == -1) {
            throw new IllegalArgumentException("argument is not a child");
         } else {
            return var2 > 0 ? this.getChildAt(var2 - 1) : null;
         }
      }
   }

   public boolean isNodeSibling(TreeNode var1) {
      boolean var2;
      if (var1 == null) {
         var2 = false;
      } else if (var1 == this) {
         var2 = true;
      } else {
         TreeNode var3 = this.getParent();
         var2 = var3 != null && var3 == var1.getParent();
         if (var2 && !((DefaultMutableTreeNode)this.getParent()).isNodeChild(var1)) {
            throw new Error("sibling has different parent");
         }
      }

      return var2;
   }

   public int getSiblingCount() {
      TreeNode var1 = this.getParent();
      return var1 == null ? 1 : var1.getChildCount();
   }

   public DefaultMutableTreeNode getNextSibling() {
      DefaultMutableTreeNode var2 = (DefaultMutableTreeNode)this.getParent();
      DefaultMutableTreeNode var1;
      if (var2 == null) {
         var1 = null;
      } else {
         var1 = (DefaultMutableTreeNode)var2.getChildAfter(this);
      }

      if (var1 != null && !this.isNodeSibling(var1)) {
         throw new Error("child of parent is not a sibling");
      } else {
         return var1;
      }
   }

   public DefaultMutableTreeNode getPreviousSibling() {
      DefaultMutableTreeNode var2 = (DefaultMutableTreeNode)this.getParent();
      DefaultMutableTreeNode var1;
      if (var2 == null) {
         var1 = null;
      } else {
         var1 = (DefaultMutableTreeNode)var2.getChildBefore(this);
      }

      if (var1 != null && !this.isNodeSibling(var1)) {
         throw new Error("child of parent is not a sibling");
      } else {
         return var1;
      }
   }

   public boolean isLeaf() {
      return this.getChildCount() == 0;
   }

   public DefaultMutableTreeNode getFirstLeaf() {
      DefaultMutableTreeNode var1;
      for(var1 = this; !var1.isLeaf(); var1 = (DefaultMutableTreeNode)var1.getFirstChild()) {
      }

      return var1;
   }

   public DefaultMutableTreeNode getLastLeaf() {
      DefaultMutableTreeNode var1;
      for(var1 = this; !var1.isLeaf(); var1 = (DefaultMutableTreeNode)var1.getLastChild()) {
      }

      return var1;
   }

   public DefaultMutableTreeNode getNextLeaf() {
      DefaultMutableTreeNode var2 = (DefaultMutableTreeNode)this.getParent();
      if (var2 == null) {
         return null;
      } else {
         DefaultMutableTreeNode var1 = this.getNextSibling();
         return var1 != null ? var1.getFirstLeaf() : var2.getNextLeaf();
      }
   }

   public DefaultMutableTreeNode getPreviousLeaf() {
      DefaultMutableTreeNode var2 = (DefaultMutableTreeNode)this.getParent();
      if (var2 == null) {
         return null;
      } else {
         DefaultMutableTreeNode var1 = this.getPreviousSibling();
         return var1 != null ? var1.getLastLeaf() : var2.getPreviousLeaf();
      }
   }

   public int getLeafCount() {
      int var1 = 0;
      Enumeration var3 = this.breadthFirstEnumeration();

      while(var3.hasMoreElements()) {
         TreeNode var2 = (TreeNode)var3.nextElement();
         if (var2.isLeaf()) {
            ++var1;
         }
      }

      if (var1 < 1) {
         throw new Error("tree has zero leaves");
      } else {
         return var1;
      }
   }

   public String toString() {
      return this.userObject == null ? "" : this.userObject.toString();
   }

   public Object clone() {
      try {
         DefaultMutableTreeNode var1 = (DefaultMutableTreeNode)super.clone();
         var1.children = null;
         var1.parent = null;
         return var1;
      } catch (CloneNotSupportedException var3) {
         throw new Error(var3.toString());
      }
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      Object[] var2;
      if (this.userObject != null && this.userObject instanceof Serializable) {
         var2 = new Object[]{"userObject", this.userObject};
      } else {
         var2 = new Object[0];
      }

      var1.writeObject(var2);
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      Object[] var2 = (Object[])((Object[])var1.readObject());
      if (var2.length > 0 && var2[0].equals("userObject")) {
         this.userObject = var2[1];
      }

   }

   final class PathBetweenNodesEnumeration implements Enumeration<TreeNode> {
      protected Stack<TreeNode> stack;

      public PathBetweenNodesEnumeration(TreeNode var2, TreeNode var3) {
         if (var2 != null && var3 != null) {
            this.stack = new Stack();
            this.stack.push(var3);
            TreeNode var4 = var3;

            while(var4 != var2) {
               var4 = var4.getParent();
               if (var4 == null && var3 != var2) {
                  throw new IllegalArgumentException("node " + var2 + " is not an ancestor of " + var3);
               }

               this.stack.push(var4);
            }

         } else {
            throw new IllegalArgumentException("argument is null");
         }
      }

      public boolean hasMoreElements() {
         return this.stack.size() > 0;
      }

      public TreeNode nextElement() {
         try {
            return (TreeNode)this.stack.pop();
         } catch (EmptyStackException var2) {
            throw new NoSuchElementException("No more elements");
         }
      }
   }

   final class BreadthFirstEnumeration implements Enumeration<TreeNode> {
      protected DefaultMutableTreeNode.BreadthFirstEnumeration.Queue queue;

      public BreadthFirstEnumeration(TreeNode var2) {
         Vector var3 = new Vector(1);
         var3.addElement(var2);
         this.queue = new DefaultMutableTreeNode.BreadthFirstEnumeration.Queue();
         this.queue.enqueue(var3.elements());
      }

      public boolean hasMoreElements() {
         return !this.queue.isEmpty() && ((Enumeration)this.queue.firstObject()).hasMoreElements();
      }

      public TreeNode nextElement() {
         Enumeration var1 = (Enumeration)this.queue.firstObject();
         TreeNode var2 = (TreeNode)var1.nextElement();
         Enumeration var3 = var2.children();
         if (!var1.hasMoreElements()) {
            this.queue.dequeue();
         }

         if (var3.hasMoreElements()) {
            this.queue.enqueue(var3);
         }

         return var2;
      }

      final class Queue {
         DefaultMutableTreeNode.BreadthFirstEnumeration.Queue.QNode head;
         DefaultMutableTreeNode.BreadthFirstEnumeration.Queue.QNode tail;

         public void enqueue(Object var1) {
            if (this.head == null) {
               this.head = this.tail = new DefaultMutableTreeNode.BreadthFirstEnumeration.Queue.QNode(var1, (DefaultMutableTreeNode.BreadthFirstEnumeration.Queue.QNode)null);
            } else {
               this.tail.next = new DefaultMutableTreeNode.BreadthFirstEnumeration.Queue.QNode(var1, (DefaultMutableTreeNode.BreadthFirstEnumeration.Queue.QNode)null);
               this.tail = this.tail.next;
            }

         }

         public Object dequeue() {
            if (this.head == null) {
               throw new NoSuchElementException("No more elements");
            } else {
               Object var1 = this.head.object;
               DefaultMutableTreeNode.BreadthFirstEnumeration.Queue.QNode var2 = this.head;
               this.head = this.head.next;
               if (this.head == null) {
                  this.tail = null;
               } else {
                  var2.next = null;
               }

               return var1;
            }
         }

         public Object firstObject() {
            if (this.head == null) {
               throw new NoSuchElementException("No more elements");
            } else {
               return this.head.object;
            }
         }

         public boolean isEmpty() {
            return this.head == null;
         }

         final class QNode {
            public Object object;
            public DefaultMutableTreeNode.BreadthFirstEnumeration.Queue.QNode next;

            public QNode(Object var2, DefaultMutableTreeNode.BreadthFirstEnumeration.Queue.QNode var3) {
               this.object = var2;
               this.next = var3;
            }
         }
      }
   }

   final class PostorderEnumeration implements Enumeration<TreeNode> {
      protected TreeNode root;
      protected Enumeration<TreeNode> children;
      protected Enumeration<TreeNode> subtree;

      public PostorderEnumeration(TreeNode var2) {
         this.root = var2;
         this.children = this.root.children();
         this.subtree = DefaultMutableTreeNode.EMPTY_ENUMERATION;
      }

      public boolean hasMoreElements() {
         return this.root != null;
      }

      public TreeNode nextElement() {
         TreeNode var1;
         if (this.subtree.hasMoreElements()) {
            var1 = (TreeNode)this.subtree.nextElement();
         } else if (this.children.hasMoreElements()) {
            this.subtree = DefaultMutableTreeNode.this.new PostorderEnumeration((TreeNode)this.children.nextElement());
            var1 = (TreeNode)this.subtree.nextElement();
         } else {
            var1 = this.root;
            this.root = null;
         }

         return var1;
      }
   }

   private final class PreorderEnumeration implements Enumeration<TreeNode> {
      private final Stack<Enumeration> stack = new Stack();

      public PreorderEnumeration(TreeNode var2) {
         Vector var3 = new Vector(1);
         var3.addElement(var2);
         this.stack.push(var3.elements());
      }

      public boolean hasMoreElements() {
         return !this.stack.empty() && ((Enumeration)this.stack.peek()).hasMoreElements();
      }

      public TreeNode nextElement() {
         Enumeration var1 = (Enumeration)this.stack.peek();
         TreeNode var2 = (TreeNode)var1.nextElement();
         Enumeration var3 = var2.children();
         if (!var1.hasMoreElements()) {
            this.stack.pop();
         }

         if (var3.hasMoreElements()) {
            this.stack.push(var3);
         }

         return var2;
      }
   }
}
