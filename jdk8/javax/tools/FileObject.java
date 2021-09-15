package javax.tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;

public interface FileObject {
   URI toUri();

   String getName();

   InputStream openInputStream() throws IOException;

   OutputStream openOutputStream() throws IOException;

   Reader openReader(boolean var1) throws IOException;

   CharSequence getCharContent(boolean var1) throws IOException;

   Writer openWriter() throws IOException;

   long getLastModified();

   boolean delete();
}
