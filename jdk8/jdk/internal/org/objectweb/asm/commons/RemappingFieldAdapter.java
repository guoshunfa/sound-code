package jdk.internal.org.objectweb.asm.commons;

import jdk.internal.org.objectweb.asm.AnnotationVisitor;
import jdk.internal.org.objectweb.asm.FieldVisitor;
import jdk.internal.org.objectweb.asm.TypePath;

public class RemappingFieldAdapter extends FieldVisitor {
   private final Remapper remapper;

   public RemappingFieldAdapter(FieldVisitor var1, Remapper var2) {
      this(327680, var1, var2);
   }

   protected RemappingFieldAdapter(int var1, FieldVisitor var2, Remapper var3) {
      super(var1, var2);
      this.remapper = var3;
   }

   public AnnotationVisitor visitAnnotation(String var1, boolean var2) {
      AnnotationVisitor var3 = this.fv.visitAnnotation(this.remapper.mapDesc(var1), var2);
      return var3 == null ? null : new RemappingAnnotationAdapter(var3, this.remapper);
   }

   public AnnotationVisitor visitTypeAnnotation(int var1, TypePath var2, String var3, boolean var4) {
      AnnotationVisitor var5 = super.visitTypeAnnotation(var1, var2, this.remapper.mapDesc(var3), var4);
      return var5 == null ? null : new RemappingAnnotationAdapter(var5, this.remapper);
   }
}
