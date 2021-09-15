package com.sun.xml.internal.bind.v2.model.nav;

import com.sun.xml.internal.bind.v2.runtime.Location;
import java.util.Collection;

public interface Navigator<T, C, F, M> {
   C getSuperClass(C var1);

   T getBaseClass(T var1, C var2);

   String getClassName(C var1);

   String getTypeName(T var1);

   String getClassShortName(C var1);

   Collection<? extends F> getDeclaredFields(C var1);

   F getDeclaredField(C var1, String var2);

   Collection<? extends M> getDeclaredMethods(C var1);

   C getDeclaringClassForField(F var1);

   C getDeclaringClassForMethod(M var1);

   T getFieldType(F var1);

   String getFieldName(F var1);

   String getMethodName(M var1);

   T getReturnType(M var1);

   T[] getMethodParameters(M var1);

   boolean isStaticMethod(M var1);

   boolean isSubClassOf(T var1, T var2);

   T ref(Class var1);

   T use(C var1);

   C asDecl(T var1);

   C asDecl(Class var1);

   boolean isArray(T var1);

   boolean isArrayButNotByteArray(T var1);

   T getComponentType(T var1);

   T getTypeArgument(T var1, int var2);

   boolean isParameterizedType(T var1);

   boolean isPrimitive(T var1);

   T getPrimitive(Class var1);

   Location getClassLocation(C var1);

   Location getFieldLocation(F var1);

   Location getMethodLocation(M var1);

   boolean hasDefaultConstructor(C var1);

   boolean isStaticField(F var1);

   boolean isPublicMethod(M var1);

   boolean isFinalMethod(M var1);

   boolean isPublicField(F var1);

   boolean isEnum(C var1);

   <P> T erasure(T var1);

   boolean isAbstract(C var1);

   boolean isFinal(C var1);

   F[] getEnumConstants(C var1);

   T getVoidType();

   String getPackageName(C var1);

   C loadObjectFactory(C var1, String var2);

   boolean isBridgeMethod(M var1);

   boolean isOverriding(M var1, C var2);

   boolean isInterface(C var1);

   boolean isTransient(F var1);

   boolean isInnerClass(C var1);

   boolean isSameType(T var1, T var2);
}
