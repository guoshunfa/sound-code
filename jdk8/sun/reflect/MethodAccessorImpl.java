package sun.reflect;

import java.lang.reflect.InvocationTargetException;

abstract class MethodAccessorImpl extends MagicAccessorImpl implements MethodAccessor {
   public abstract Object invoke(Object var1, Object[] var2) throws IllegalArgumentException, InvocationTargetException;
}
