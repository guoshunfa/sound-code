package javax.annotation.processing;

import java.io.IOException;
import javax.lang.model.element.Element;
import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;

public interface Filer {
   JavaFileObject createSourceFile(CharSequence var1, Element... var2) throws IOException;

   JavaFileObject createClassFile(CharSequence var1, Element... var2) throws IOException;

   FileObject createResource(JavaFileManager.Location var1, CharSequence var2, CharSequence var3, Element... var4) throws IOException;

   FileObject getResource(JavaFileManager.Location var1, CharSequence var2, CharSequence var3) throws IOException;
}
