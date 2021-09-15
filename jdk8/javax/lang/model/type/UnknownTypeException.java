package javax.lang.model.type;

import javax.lang.model.UnknownEntityException;

public class UnknownTypeException extends UnknownEntityException {
   private static final long serialVersionUID = 269L;
   private transient TypeMirror type;
   private transient Object parameter;

   public UnknownTypeException(TypeMirror var1, Object var2) {
      super("Unknown type: " + var1);
      this.type = var1;
      this.parameter = var2;
   }

   public TypeMirror getUnknownType() {
      return this.type;
   }

   public Object getArgument() {
      return this.parameter;
   }
}
