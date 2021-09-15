package javax.xml.ws.spi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.xml.ws.WebServiceContext;

public abstract class Invoker {
   public abstract void inject(WebServiceContext var1) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException;

   public abstract Object invoke(Method var1, Object... var2) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException;
}
