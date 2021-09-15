package sun.misc;

import java.util.ArrayList;
import java.util.List;

/** @deprecated */
@Deprecated
public abstract class ClassFileTransformer {
   private static final List<ClassFileTransformer> transformers = new ArrayList();

   public static void add(ClassFileTransformer var0) {
      synchronized(transformers) {
         transformers.add(var0);
      }
   }

   public static ClassFileTransformer[] getTransformers() {
      synchronized(transformers) {
         ClassFileTransformer[] var1 = new ClassFileTransformer[transformers.size()];
         return (ClassFileTransformer[])transformers.toArray(var1);
      }
   }

   public abstract byte[] transform(byte[] var1, int var2, int var3) throws ClassFormatError;
}
