package javax.tools;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;

public interface JavaFileObject extends FileObject {
   JavaFileObject.Kind getKind();

   boolean isNameCompatible(String var1, JavaFileObject.Kind var2);

   NestingKind getNestingKind();

   Modifier getAccessLevel();

   public static enum Kind {
      SOURCE(".java"),
      CLASS(".class"),
      HTML(".html"),
      OTHER("");

      public final String extension;

      private Kind(String var3) {
         var3.getClass();
         this.extension = var3;
      }
   }
}
