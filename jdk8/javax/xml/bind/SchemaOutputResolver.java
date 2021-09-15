package javax.xml.bind;

import java.io.IOException;
import javax.xml.transform.Result;

public abstract class SchemaOutputResolver {
   public abstract Result createOutput(String var1, String var2) throws IOException;
}
