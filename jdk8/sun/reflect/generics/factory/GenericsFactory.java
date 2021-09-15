package sun.reflect.generics.factory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import sun.reflect.generics.tree.FieldTypeSignature;

public interface GenericsFactory {
   TypeVariable<?> makeTypeVariable(String var1, FieldTypeSignature[] var2);

   ParameterizedType makeParameterizedType(Type var1, Type[] var2, Type var3);

   TypeVariable<?> findTypeVariable(String var1);

   WildcardType makeWildcard(FieldTypeSignature[] var1, FieldTypeSignature[] var2);

   Type makeNamedType(String var1);

   Type makeArrayType(Type var1);

   Type makeByte();

   Type makeBool();

   Type makeShort();

   Type makeChar();

   Type makeInt();

   Type makeLong();

   Type makeFloat();

   Type makeDouble();

   Type makeVoid();
}
