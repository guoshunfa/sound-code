package javax.tools;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;

public class ForwardingJavaFileObject<F extends JavaFileObject> extends ForwardingFileObject<F> implements JavaFileObject {
   protected ForwardingJavaFileObject(F var1) {
      super(var1);
   }

   public JavaFileObject.Kind getKind() {
      return ((JavaFileObject)this.fileObject).getKind();
   }

   public boolean isNameCompatible(String var1, JavaFileObject.Kind var2) {
      return ((JavaFileObject)this.fileObject).isNameCompatible(var1, var2);
   }

   public NestingKind getNestingKind() {
      return ((JavaFileObject)this.fileObject).getNestingKind();
   }

   public Modifier getAccessLevel() {
      return ((JavaFileObject)this.fileObject).getAccessLevel();
   }
}
