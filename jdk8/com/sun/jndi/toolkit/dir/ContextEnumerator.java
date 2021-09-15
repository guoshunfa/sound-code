package com.sun.jndi.toolkit.dir;

import java.util.NoSuchElementException;
import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

public class ContextEnumerator implements NamingEnumeration<Binding> {
   private static boolean debug = false;
   private NamingEnumeration<Binding> children;
   private Binding currentChild;
   private boolean currentReturned;
   private Context root;
   private ContextEnumerator currentChildEnum;
   private boolean currentChildExpanded;
   private boolean rootProcessed;
   private int scope;
   private String contextName;

   public ContextEnumerator(Context var1) throws NamingException {
      this(var1, 2);
   }

   public ContextEnumerator(Context var1, int var2) throws NamingException {
      this(var1, var2, "", var2 != 1);
   }

   protected ContextEnumerator(Context var1, int var2, String var3, boolean var4) throws NamingException {
      this.children = null;
      this.currentChild = null;
      this.currentReturned = false;
      this.currentChildEnum = null;
      this.currentChildExpanded = false;
      this.rootProcessed = false;
      this.scope = 2;
      this.contextName = "";
      if (var1 == null) {
         throw new IllegalArgumentException("null context passed");
      } else {
         this.root = var1;
         if (var2 != 0) {
            this.children = this.getImmediateChildren(var1);
         }

         this.scope = var2;
         this.contextName = var3;
         this.rootProcessed = !var4;
         this.prepNextChild();
      }
   }

   protected NamingEnumeration<Binding> getImmediateChildren(Context var1) throws NamingException {
      return var1.listBindings("");
   }

   protected ContextEnumerator newEnumerator(Context var1, int var2, String var3, boolean var4) throws NamingException {
      return new ContextEnumerator(var1, var2, var3, var4);
   }

   public boolean hasMore() throws NamingException {
      return !this.rootProcessed || this.scope != 0 && this.hasMoreDescendants();
   }

   public boolean hasMoreElements() {
      try {
         return this.hasMore();
      } catch (NamingException var2) {
         return false;
      }
   }

   public Binding nextElement() {
      try {
         return this.next();
      } catch (NamingException var2) {
         throw new NoSuchElementException(var2.toString());
      }
   }

   public Binding next() throws NamingException {
      if (!this.rootProcessed) {
         this.rootProcessed = true;
         return new Binding("", this.root.getClass().getName(), this.root, true);
      } else if (this.scope != 0 && this.hasMoreDescendants()) {
         return this.getNextDescendant();
      } else {
         throw new NoSuchElementException();
      }
   }

   public void close() throws NamingException {
      this.root = null;
   }

   private boolean hasMoreChildren() throws NamingException {
      return this.children != null && this.children.hasMore();
   }

   private Binding getNextChild() throws NamingException {
      Binding var1 = (Binding)this.children.next();
      Binding var2 = null;
      if (var1.isRelative() && !this.contextName.equals("")) {
         NameParser var3 = this.root.getNameParser("");
         Name var4 = var3.parse(this.contextName);
         var4.add(var1.getName());
         if (debug) {
            System.out.println("ContextEnumerator: adding " + var4);
         }

         var2 = new Binding(var4.toString(), var1.getClassName(), var1.getObject(), var1.isRelative());
      } else {
         if (debug) {
            System.out.println("ContextEnumerator: using old binding");
         }

         var2 = var1;
      }

      return var2;
   }

   private boolean hasMoreDescendants() throws NamingException {
      if (!this.currentReturned) {
         if (debug) {
            System.out.println("hasMoreDescendants returning " + (this.currentChild != null));
         }

         return this.currentChild != null;
      } else if (this.currentChildExpanded && this.currentChildEnum.hasMore()) {
         if (debug) {
            System.out.println("hasMoreDescendants returning true");
         }

         return true;
      } else {
         if (debug) {
            System.out.println("hasMoreDescendants returning hasMoreChildren");
         }

         return this.hasMoreChildren();
      }
   }

   private Binding getNextDescendant() throws NamingException {
      if (!this.currentReturned) {
         if (debug) {
            System.out.println("getNextDescedant: simple case");
         }

         this.currentReturned = true;
         return this.currentChild;
      } else if (this.currentChildExpanded && this.currentChildEnum.hasMore()) {
         if (debug) {
            System.out.println("getNextDescedant: expanded case");
         }

         return this.currentChildEnum.next();
      } else {
         if (debug) {
            System.out.println("getNextDescedant: next case");
         }

         this.prepNextChild();
         return this.getNextDescendant();
      }
   }

   private void prepNextChild() throws NamingException {
      if (this.hasMoreChildren()) {
         try {
            this.currentChild = this.getNextChild();
            this.currentReturned = false;
         } catch (NamingException var2) {
            if (debug) {
               System.out.println((Object)var2);
            }

            if (debug) {
               var2.printStackTrace();
            }
         }

         if (this.scope == 2 && this.currentChild.getObject() instanceof Context) {
            this.currentChildEnum = this.newEnumerator((Context)((Context)this.currentChild.getObject()), this.scope, this.currentChild.getName(), false);
            this.currentChildExpanded = true;
            if (debug) {
               System.out.println("prepNextChild: expanded");
            }
         } else {
            this.currentChildExpanded = false;
            this.currentChildEnum = null;
            if (debug) {
               System.out.println("prepNextChild: normal");
            }
         }

      } else {
         this.currentChild = null;
      }
   }
}
