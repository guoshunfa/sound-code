package javax.lang.model.element;

import javax.lang.model.UnknownEntityException;

public class UnknownElementException extends UnknownEntityException {
   private static final long serialVersionUID = 269L;
   private transient Element element;
   private transient Object parameter;

   public UnknownElementException(Element var1, Object var2) {
      super("Unknown element: " + var1);
      this.element = var1;
      this.parameter = var2;
   }

   public Element getUnknownElement() {
      return this.element;
   }

   public Object getArgument() {
      return this.parameter;
   }
}
