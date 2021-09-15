package javax.annotation.processing;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

public interface Messager {
   void printMessage(Diagnostic.Kind var1, CharSequence var2);

   void printMessage(Diagnostic.Kind var1, CharSequence var2, Element var3);

   void printMessage(Diagnostic.Kind var1, CharSequence var2, Element var3, AnnotationMirror var4);

   void printMessage(Diagnostic.Kind var1, CharSequence var2, Element var3, AnnotationMirror var4, AnnotationValue var5);
}
