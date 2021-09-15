package javax.tools;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import javax.lang.model.SourceVersion;

public interface Tool {
   int run(InputStream var1, OutputStream var2, OutputStream var3, String... var4);

   Set<SourceVersion> getSourceVersions();
}
