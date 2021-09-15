package javax.lang.model.element;

import java.util.List;
import javax.lang.model.type.TypeMirror;

public interface AnnotationValueVisitor<R, P> {
   R visit(AnnotationValue var1, P var2);

   R visit(AnnotationValue var1);

   R visitBoolean(boolean var1, P var2);

   R visitByte(byte var1, P var2);

   R visitChar(char var1, P var2);

   R visitDouble(double var1, P var3);

   R visitFloat(float var1, P var2);

   R visitInt(int var1, P var2);

   R visitLong(long var1, P var3);

   R visitShort(short var1, P var2);

   R visitString(String var1, P var2);

   R visitType(TypeMirror var1, P var2);

   R visitEnumConstant(VariableElement var1, P var2);

   R visitAnnotation(AnnotationMirror var1, P var2);

   R visitArray(List<? extends AnnotationValue> var1, P var2);

   R visitUnknown(AnnotationValue var1, P var2);
}
