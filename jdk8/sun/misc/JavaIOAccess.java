package sun.misc;

import java.io.Console;
import java.nio.charset.Charset;

public interface JavaIOAccess {
   Console console();

   Charset charset();
}
