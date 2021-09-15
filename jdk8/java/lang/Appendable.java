package java.lang;

import java.io.IOException;

public interface Appendable {
   Appendable append(CharSequence var1) throws IOException;

   Appendable append(CharSequence var1, int var2, int var3) throws IOException;

   Appendable append(char var1) throws IOException;
}
