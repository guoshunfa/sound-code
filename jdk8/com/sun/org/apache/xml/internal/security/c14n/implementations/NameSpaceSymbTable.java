package com.sun.org.apache.xml.internal.security.c14n.implementations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;

public class NameSpaceSymbTable {
   private static final String XMLNS = "xmlns";
   private static final SymbMap initialMap = new SymbMap();
   private SymbMap symb;
   private List<SymbMap> level = new ArrayList();
   private boolean cloned = true;

   public NameSpaceSymbTable() {
      this.symb = (SymbMap)initialMap.clone();
   }

   public void getUnrenderedNodes(Collection<Attr> var1) {
      Iterator var2 = this.symb.entrySet().iterator();

      while(var2.hasNext()) {
         NameSpaceSymbEntry var3 = (NameSpaceSymbEntry)var2.next();
         if (!var3.rendered && var3.n != null) {
            var3 = (NameSpaceSymbEntry)var3.clone();
            this.needsClone();
            this.symb.put(var3.prefix, var3);
            var3.lastrendered = var3.uri;
            var3.rendered = true;
            var1.add(var3.n);
         }
      }

   }

   public void outputNodePush() {
      this.push();
   }

   public void outputNodePop() {
      this.pop();
   }

   public void push() {
      this.level.add((Object)null);
      this.cloned = false;
   }

   public void pop() {
      int var1 = this.level.size() - 1;
      Object var2 = this.level.remove(var1);
      if (var2 != null) {
         this.symb = (SymbMap)var2;
         if (var1 == 0) {
            this.cloned = false;
         } else {
            this.cloned = this.level.get(var1 - 1) != this.symb;
         }
      } else {
         this.cloned = false;
      }

   }

   final void needsClone() {
      if (!this.cloned) {
         this.level.set(this.level.size() - 1, this.symb);
         this.symb = (SymbMap)this.symb.clone();
         this.cloned = true;
      }

   }

   public Attr getMapping(String var1) {
      NameSpaceSymbEntry var2 = this.symb.get(var1);
      if (var2 == null) {
         return null;
      } else if (var2.rendered) {
         return null;
      } else {
         var2 = (NameSpaceSymbEntry)var2.clone();
         this.needsClone();
         this.symb.put(var1, var2);
         var2.rendered = true;
         var2.lastrendered = var2.uri;
         return var2.n;
      }
   }

   public Attr getMappingWithoutRendered(String var1) {
      NameSpaceSymbEntry var2 = this.symb.get(var1);
      if (var2 == null) {
         return null;
      } else {
         return var2.rendered ? null : var2.n;
      }
   }

   public boolean addMapping(String var1, String var2, Attr var3) {
      NameSpaceSymbEntry var4 = this.symb.get(var1);
      if (var4 != null && var2.equals(var4.uri)) {
         return false;
      } else {
         NameSpaceSymbEntry var5 = new NameSpaceSymbEntry(var2, var3, false, var1);
         this.needsClone();
         this.symb.put(var1, var5);
         if (var4 != null) {
            var5.lastrendered = var4.lastrendered;
            if (var4.lastrendered != null && var4.lastrendered.equals(var2)) {
               var5.rendered = true;
            }
         }

         return true;
      }
   }

   public Node addMappingAndRender(String var1, String var2, Attr var3) {
      NameSpaceSymbEntry var4 = this.symb.get(var1);
      if (var4 != null && var2.equals(var4.uri)) {
         if (!var4.rendered) {
            var4 = (NameSpaceSymbEntry)var4.clone();
            this.needsClone();
            this.symb.put(var1, var4);
            var4.lastrendered = var2;
            var4.rendered = true;
            return var4.n;
         } else {
            return null;
         }
      } else {
         NameSpaceSymbEntry var5 = new NameSpaceSymbEntry(var2, var3, true, var1);
         var5.lastrendered = var2;
         this.needsClone();
         this.symb.put(var1, var5);
         if (var4 != null && var4.lastrendered != null && var4.lastrendered.equals(var2)) {
            var5.rendered = true;
            return null;
         } else {
            return var5.n;
         }
      }
   }

   public int getLevel() {
      return this.level.size();
   }

   public void removeMapping(String var1) {
      NameSpaceSymbEntry var2 = this.symb.get(var1);
      if (var2 != null) {
         this.needsClone();
         this.symb.put(var1, (NameSpaceSymbEntry)null);
      }

   }

   public void removeMappingIfNotRender(String var1) {
      NameSpaceSymbEntry var2 = this.symb.get(var1);
      if (var2 != null && !var2.rendered) {
         this.needsClone();
         this.symb.put(var1, (NameSpaceSymbEntry)null);
      }

   }

   public boolean removeMappingIfRender(String var1) {
      NameSpaceSymbEntry var2 = this.symb.get(var1);
      if (var2 != null && var2.rendered) {
         this.needsClone();
         this.symb.put(var1, (NameSpaceSymbEntry)null);
      }

      return false;
   }

   static {
      NameSpaceSymbEntry var0 = new NameSpaceSymbEntry("", (Attr)null, true, "xmlns");
      var0.lastrendered = "";
      initialMap.put("xmlns", var0);
   }
}
