package sun.misc;

import java.io.InvalidClassException;
import java.io.ObjectInputStream;

public interface JavaOISAccess {
   void setObjectInputFilter(ObjectInputStream var1, ObjectInputFilter var2);

   ObjectInputFilter getObjectInputFilter(ObjectInputStream var1);

   void checkArray(ObjectInputStream var1, Class<?> var2, int var3) throws InvalidClassException;
}
