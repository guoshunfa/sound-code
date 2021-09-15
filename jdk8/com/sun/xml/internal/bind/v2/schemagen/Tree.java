package com.sun.xml.internal.bind.v2.schemagen;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.ContentModelContainer;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.Occurs;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.Particle;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.TypeDefParticle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

abstract class Tree {
   Tree makeOptional(boolean really) {
      return (Tree)(really ? new Tree.Optional(this) : this);
   }

   Tree makeRepeated(boolean really) {
      return (Tree)(really ? new Tree.Repeated(this) : this);
   }

   static Tree makeGroup(GroupKind kind, List<Tree> children) {
      if (children.size() == 1) {
         return (Tree)children.get(0);
      } else {
         List<Tree> normalizedChildren = new ArrayList(children.size());
         Iterator var3 = children.iterator();

         while(true) {
            while(var3.hasNext()) {
               Tree t = (Tree)var3.next();
               if (t instanceof Tree.Group) {
                  Tree.Group g = (Tree.Group)t;
                  if (g.kind == kind) {
                     normalizedChildren.addAll(Arrays.asList(g.children));
                     continue;
                  }
               }

               normalizedChildren.add(t);
            }

            return new Tree.Group(kind, (Tree[])normalizedChildren.toArray(new Tree[normalizedChildren.size()]));
         }
      }
   }

   abstract boolean isNullable();

   boolean canBeTopLevel() {
      return false;
   }

   protected abstract void write(ContentModelContainer var1, boolean var2, boolean var3);

   protected void write(TypeDefParticle ct) {
      if (this.canBeTopLevel()) {
         this.write((ContentModelContainer)ct._cast(ContentModelContainer.class), false, false);
      } else {
         (new Tree.Group(GroupKind.SEQUENCE, new Tree[]{this})).write(ct);
      }

   }

   protected final void writeOccurs(Occurs o, boolean isOptional, boolean repeated) {
      if (isOptional) {
         o.minOccurs(0);
      }

      if (repeated) {
         o.maxOccurs("unbounded");
      }

   }

   private static final class Group extends Tree {
      private final GroupKind kind;
      private final Tree[] children;

      private Group(GroupKind kind, Tree... children) {
         this.kind = kind;
         this.children = children;
      }

      boolean canBeTopLevel() {
         return true;
      }

      boolean isNullable() {
         Tree[] var1;
         int var2;
         int var3;
         Tree t;
         if (this.kind == GroupKind.CHOICE) {
            var1 = this.children;
            var2 = var1.length;

            for(var3 = 0; var3 < var2; ++var3) {
               t = var1[var3];
               if (t.isNullable()) {
                  return true;
               }
            }

            return false;
         } else {
            var1 = this.children;
            var2 = var1.length;

            for(var3 = 0; var3 < var2; ++var3) {
               t = var1[var3];
               if (!t.isNullable()) {
                  return false;
               }
            }

            return true;
         }
      }

      protected void write(ContentModelContainer parent, boolean isOptional, boolean repeated) {
         Particle c = this.kind.write(parent);
         this.writeOccurs(c, isOptional, repeated);
         Tree[] var5 = this.children;
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            Tree child = var5[var7];
            child.write(c, false, false);
         }

      }

      // $FF: synthetic method
      Group(GroupKind x0, Tree[] x1, Object x2) {
         this(x0, x1);
      }
   }

   private static final class Repeated extends Tree {
      private final Tree body;

      private Repeated(Tree body) {
         this.body = body;
      }

      boolean isNullable() {
         return this.body.isNullable();
      }

      Tree makeRepeated(boolean really) {
         return this;
      }

      protected void write(ContentModelContainer parent, boolean isOptional, boolean repeated) {
         this.body.write(parent, isOptional, true);
      }

      // $FF: synthetic method
      Repeated(Tree x0, Object x1) {
         this(x0);
      }
   }

   private static final class Optional extends Tree {
      private final Tree body;

      private Optional(Tree body) {
         this.body = body;
      }

      boolean isNullable() {
         return true;
      }

      Tree makeOptional(boolean really) {
         return this;
      }

      protected void write(ContentModelContainer parent, boolean isOptional, boolean repeated) {
         this.body.write(parent, true, repeated);
      }

      // $FF: synthetic method
      Optional(Tree x0, Object x1) {
         this(x0);
      }
   }

   abstract static class Term extends Tree {
      boolean isNullable() {
         return false;
      }
   }
}
