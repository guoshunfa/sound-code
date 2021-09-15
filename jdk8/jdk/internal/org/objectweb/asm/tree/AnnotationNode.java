package jdk.internal.org.objectweb.asm.tree;

import java.util.ArrayList;
import java.util.List;
import jdk.internal.org.objectweb.asm.AnnotationVisitor;

public class AnnotationNode extends AnnotationVisitor {
   public String desc;
   public List<Object> values;

   public AnnotationNode(String var1) {
      this(327680, var1);
      if (this.getClass() != AnnotationNode.class) {
         throw new IllegalStateException();
      }
   }

   public AnnotationNode(int var1, String var2) {
      super(var1);
      this.desc = var2;
   }

   AnnotationNode(List<Object> var1) {
      super(327680);
      this.values = var1;
   }

   public void visit(String var1, Object var2) {
      if (this.values == null) {
         this.values = new ArrayList(this.desc != null ? 2 : 1);
      }

      if (this.desc != null) {
         this.values.add(var1);
      }

      this.values.add(var2);
   }

   public void visitEnum(String var1, String var2, String var3) {
      if (this.values == null) {
         this.values = new ArrayList(this.desc != null ? 2 : 1);
      }

      if (this.desc != null) {
         this.values.add(var1);
      }

      this.values.add(new String[]{var2, var3});
   }

   public AnnotationVisitor visitAnnotation(String var1, String var2) {
      if (this.values == null) {
         this.values = new ArrayList(this.desc != null ? 2 : 1);
      }

      if (this.desc != null) {
         this.values.add(var1);
      }

      AnnotationNode var3 = new AnnotationNode(var2);
      this.values.add(var3);
      return var3;
   }

   public AnnotationVisitor visitArray(String var1) {
      if (this.values == null) {
         this.values = new ArrayList(this.desc != null ? 2 : 1);
      }

      if (this.desc != null) {
         this.values.add(var1);
      }

      ArrayList var2 = new ArrayList();
      this.values.add(var2);
      return new AnnotationNode(var2);
   }

   public void visitEnd() {
   }

   public void check(int var1) {
   }

   public void accept(AnnotationVisitor var1) {
      if (var1 != null) {
         if (this.values != null) {
            for(int var2 = 0; var2 < this.values.size(); var2 += 2) {
               String var3 = (String)this.values.get(var2);
               Object var4 = this.values.get(var2 + 1);
               accept(var1, var3, var4);
            }
         }

         var1.visitEnd();
      }

   }

   static void accept(AnnotationVisitor var0, String var1, Object var2) {
      if (var0 != null) {
         if (var2 instanceof String[]) {
            String[] var3 = (String[])((String[])var2);
            var0.visitEnum(var1, var3[0], var3[1]);
         } else if (var2 instanceof AnnotationNode) {
            AnnotationNode var6 = (AnnotationNode)var2;
            var6.accept(var0.visitAnnotation(var1, var6.desc));
         } else if (var2 instanceof List) {
            AnnotationVisitor var7 = var0.visitArray(var1);
            if (var7 != null) {
               List var4 = (List)var2;

               for(int var5 = 0; var5 < var4.size(); ++var5) {
                  accept(var7, (String)null, var4.get(var5));
               }

               var7.visitEnd();
            }
         } else {
            var0.visit(var1, var2);
         }
      }

   }
}
