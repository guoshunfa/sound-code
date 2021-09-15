package sun.reflect;

import java.lang.reflect.InvocationTargetException;

public interface ConstructorAccessor {
   Object newInstance(Object[] var1) throws InstantiationException, IllegalArgumentException, InvocationTargetException;
}
