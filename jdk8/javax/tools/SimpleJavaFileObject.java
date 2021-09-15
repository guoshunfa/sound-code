package javax.tools;

import java.io.CharArrayReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.net.URI;
import java.nio.CharBuffer;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;

public class SimpleJavaFileObject implements JavaFileObject {
   protected final URI uri;
   protected final JavaFileObject.Kind kind;

   protected SimpleJavaFileObject(URI var1, JavaFileObject.Kind var2) {
      var1.getClass();
      var2.getClass();
      if (var1.getPath() == null) {
         throw new IllegalArgumentException("URI must have a path: " + var1);
      } else {
         this.uri = var1;
         this.kind = var2;
      }
   }

   public URI toUri() {
      return this.uri;
   }

   public String getName() {
      return this.toUri().getPath();
   }

   public InputStream openInputStream() throws IOException {
      throw new UnsupportedOperationException();
   }

   public OutputStream openOutputStream() throws IOException {
      throw new UnsupportedOperationException();
   }

   public Reader openReader(boolean var1) throws IOException {
      CharSequence var2 = this.getCharContent(var1);
      if (var2 == null) {
         throw new UnsupportedOperationException();
      } else {
         if (var2 instanceof CharBuffer) {
            CharBuffer var3 = (CharBuffer)var2;
            if (var3.hasArray()) {
               return new CharArrayReader(var3.array());
            }
         }

         return new StringReader(var2.toString());
      }
   }

   public CharSequence getCharContent(boolean var1) throws IOException {
      throw new UnsupportedOperationException();
   }

   public Writer openWriter() throws IOException {
      return new OutputStreamWriter(this.openOutputStream());
   }

   public long getLastModified() {
      return 0L;
   }

   public boolean delete() {
      return false;
   }

   public JavaFileObject.Kind getKind() {
      return this.kind;
   }

   public boolean isNameCompatible(String var1, JavaFileObject.Kind var2) {
      String var3 = var1 + var2.extension;
      return var2.equals(this.getKind()) && (var3.equals(this.toUri().getPath()) || this.toUri().getPath().endsWith("/" + var3));
   }

   public NestingKind getNestingKind() {
      return null;
   }

   public Modifier getAccessLevel() {
      return null;
   }

   public String toString() {
      return this.getClass().getName() + "[" + this.toUri() + "]";
   }
}
