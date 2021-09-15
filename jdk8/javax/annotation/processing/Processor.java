package javax.annotation.processing;

import java.util.Set;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

public interface Processor {
   Set<String> getSupportedOptions();

   Set<String> getSupportedAnnotationTypes();

   SourceVersion getSupportedSourceVersion();

   void init(ProcessingEnvironment var1);

   boolean process(Set<? extends TypeElement> var1, RoundEnvironment var2);

   Iterable<? extends Completion> getCompletions(Element var1, AnnotationMirror var2, ExecutableElement var3, String var4);
}
