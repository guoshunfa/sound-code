package javax.lang.model.util;

import java.util.List;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class SimpleAnnotationValueVisitor6<R, P> extends AbstractAnnotationValueVisitor6<R, P> {
   protected final R DEFAULT_VALUE;

   protected SimpleAnnotationValueVisitor6() {
      this.DEFAULT_VALUE = null;
   }

   protected SimpleAnnotationValueVisitor6(R var1) {
      this.DEFAULT_VALUE = var1;
   }

   protected R defaultAction(Object var1, P var2) {
      return this.DEFAULT_VALUE;
   }

   public R visitBoolean(boolean var1, P var2) {
      return this.defaultAction(var1, var2);
   }

   public R visitByte(byte var1, P var2) {
      return this.defaultAction(var1, var2);
   }

   public R visitChar(char var1, P var2) {
      return this.defaultAction(var1, var2);
   }

   public R visitDouble(double var1, P var3) {
      return this.defaultAction(var1, var3);
   }

   public R visitFloat(float var1, P var2) {
      return this.defaultAction(var1, var2);
   }

   public R visitInt(int var1, P var2) {
      return this.defaultAction(var1, var2);
   }

   public R visitLong(long var1, P var3) {
      return this.defaultAction(var1, var3);
   }

   public R visitShort(short var1, P var2) {
      return this.defaultAction(var1, var2);
   }

   public R visitString(String var1, P var2) {
      return this.defaultAction(var1, var2);
   }

   public R visitType(TypeMirror var1, P var2) {
      return this.defaultAction(var1, var2);
   }

   public R visitEnumConstant(VariableElement var1, P var2) {
      return this.defaultAction(var1, var2);
   }

   public R visitAnnotation(AnnotationMirror var1, P var2) {
      return this.defaultAction(var1, var2);
   }

   public R visitArray(List<? extends AnnotationValue> var1, P var2) {
      return this.defaultAction(var1, var2);
   }
}
