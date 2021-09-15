package javax.xml.ws;

import java.util.Map;
import java.util.concurrent.Future;

public interface Response<T> extends Future<T> {
   Map<String, Object> getContext();
}
