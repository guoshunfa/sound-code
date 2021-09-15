package javax.xml.ws;

import java.util.concurrent.Future;

public interface Dispatch<T> extends BindingProvider {
   T invoke(T var1);

   Response<T> invokeAsync(T var1);

   Future<?> invokeAsync(T var1, AsyncHandler<T> var2);

   void invokeOneWay(T var1);
}
