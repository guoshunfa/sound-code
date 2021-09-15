package sun.invoke;

import java.lang.invoke.MethodHandle;

public interface WrapperInstance {
   MethodHandle getWrapperInstanceTarget();

   Class<?> getWrapperInstanceType();
}
