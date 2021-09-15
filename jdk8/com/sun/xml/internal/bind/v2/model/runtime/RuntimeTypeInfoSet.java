package com.sun.xml.internal.bind.v2.model.runtime;

import com.sun.xml.internal.bind.v2.model.core.TypeInfoSet;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;
import javax.xml.namespace.QName;

public interface RuntimeTypeInfoSet extends TypeInfoSet<Type, Class, Field, Method> {
   Map<Class, ? extends RuntimeArrayInfo> arrays();

   Map<Class, ? extends RuntimeClassInfo> beans();

   Map<Type, ? extends RuntimeBuiltinLeafInfo> builtins();

   Map<Class, ? extends RuntimeEnumLeafInfo> enums();

   RuntimeNonElement getTypeInfo(Type var1);

   RuntimeNonElement getAnyTypeInfo();

   RuntimeNonElement getClassInfo(Class var1);

   RuntimeElementInfo getElementInfo(Class var1, QName var2);

   Map<QName, ? extends RuntimeElementInfo> getElementMappings(Class var1);

   Iterable<? extends RuntimeElementInfo> getAllElements();
}
