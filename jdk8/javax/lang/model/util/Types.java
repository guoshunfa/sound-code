package javax.lang.model.util;

import java.util.List;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.NullType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.WildcardType;

public interface Types {
   Element asElement(TypeMirror var1);

   boolean isSameType(TypeMirror var1, TypeMirror var2);

   boolean isSubtype(TypeMirror var1, TypeMirror var2);

   boolean isAssignable(TypeMirror var1, TypeMirror var2);

   boolean contains(TypeMirror var1, TypeMirror var2);

   boolean isSubsignature(ExecutableType var1, ExecutableType var2);

   List<? extends TypeMirror> directSupertypes(TypeMirror var1);

   TypeMirror erasure(TypeMirror var1);

   TypeElement boxedClass(PrimitiveType var1);

   PrimitiveType unboxedType(TypeMirror var1);

   TypeMirror capture(TypeMirror var1);

   PrimitiveType getPrimitiveType(TypeKind var1);

   NullType getNullType();

   NoType getNoType(TypeKind var1);

   ArrayType getArrayType(TypeMirror var1);

   WildcardType getWildcardType(TypeMirror var1, TypeMirror var2);

   DeclaredType getDeclaredType(TypeElement var1, TypeMirror... var2);

   DeclaredType getDeclaredType(DeclaredType var1, TypeElement var2, TypeMirror... var3);

   TypeMirror asMemberOf(DeclaredType var1, Element var2);
}
