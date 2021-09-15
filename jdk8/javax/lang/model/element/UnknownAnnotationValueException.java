package javax.lang.model.element;

import javax.lang.model.UnknownEntityException;

public class UnknownAnnotationValueException extends UnknownEntityException {
   private static final long serialVersionUID = 269L;
   private transient AnnotationValue av;
   private transient Object parameter;

   public UnknownAnnotationValueException(AnnotationValue var1, Object var2) {
      super("Unknown annotation value: " + var1);
      this.av = var1;
      this.parameter = var2;
   }

   public AnnotationValue getUnknownAnnotationValue() {
      return this.av;
   }

   public Object getArgument() {
      return this.parameter;
   }
}
