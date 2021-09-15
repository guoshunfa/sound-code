package javax.tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;

public class ForwardingFileObject<F extends FileObject> implements FileObject {
   protected final F fileObject;

   protected ForwardingFileObject(F var1) {
      var1.getClass();
      this.fileObject = var1;
   }

   public URI toUri() {
      return this.fileObject.toUri();
   }

   public String getName() {
      return this.fileObject.getName();
   }

   public InputStream openInputStream() throws IOException {
      return this.fileObject.openInputStream();
   }

   public OutputStream openOutputStream() throws IOException {
      return this.fileObject.openOutputStream();
   }

   public Reader openReader(boolean var1) throws IOException {
      return this.fileObject.openReader(var1);
   }

   public CharSequence getCharContent(boolean var1) throws IOException {
      return this.fileObject.getCharContent(var1);
   }

   public Writer openWriter() throws IOException {
      return this.fileObject.openWriter();
   }

   public long getLastModified() {
      return this.fileObject.getLastModified();
   }

   public boolean delete() {
      return this.fileObject.delete();
   }
}
