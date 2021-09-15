package sun.reflect;

import java.lang.reflect.InvocationTargetException;

public interface MethodAccessor {
   Object invoke(Object var1, Object[] var2) throws IllegalArgumentException, InvocationTargetException;
}
