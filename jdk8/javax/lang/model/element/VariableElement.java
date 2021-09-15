package javax.lang.model.element;

public interface VariableElement extends Element {
   Object getConstantValue();

   Name getSimpleName();

   Element getEnclosingElement();
}
