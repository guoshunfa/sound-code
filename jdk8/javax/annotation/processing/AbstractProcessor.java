package javax.annotation.processing;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

public abstract class AbstractProcessor implements Processor {
   protected ProcessingEnvironment processingEnv;
   private boolean initialized = false;

   protected AbstractProcessor() {
   }

   public Set<String> getSupportedOptions() {
      SupportedOptions var1 = (SupportedOptions)this.getClass().getAnnotation(SupportedOptions.class);
      return var1 == null ? Collections.emptySet() : arrayToSet(var1.value());
   }

   public Set<String> getSupportedAnnotationTypes() {
      SupportedAnnotationTypes var1 = (SupportedAnnotationTypes)this.getClass().getAnnotation(SupportedAnnotationTypes.class);
      if (var1 == null) {
         if (this.isInitialized()) {
            this.processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "No SupportedAnnotationTypes annotation found on " + this.getClass().getName() + ", returning an empty set.");
         }

         return Collections.emptySet();
      } else {
         return arrayToSet(var1.value());
      }
   }

   public SourceVersion getSupportedSourceVersion() {
      SupportedSourceVersion var1 = (SupportedSourceVersion)this.getClass().getAnnotation(SupportedSourceVersion.class);
      SourceVersion var2 = null;
      if (var1 == null) {
         var2 = SourceVersion.RELEASE_6;
         if (this.isInitialized()) {
            this.processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "No SupportedSourceVersion annotation found on " + this.getClass().getName() + ", returning " + var2 + ".");
         }
      } else {
         var2 = var1.value();
      }

      return var2;
   }

   public synchronized void init(ProcessingEnvironment var1) {
      if (this.initialized) {
         throw new IllegalStateException("Cannot call init more than once.");
      } else {
         Objects.requireNonNull(var1, (String)"Tool provided null ProcessingEnvironment");
         this.processingEnv = var1;
         this.initialized = true;
      }
   }

   public abstract boolean process(Set<? extends TypeElement> var1, RoundEnvironment var2);

   public Iterable<? extends Completion> getCompletions(Element var1, AnnotationMirror var2, ExecutableElement var3, String var4) {
      return Collections.emptyList();
   }

   protected synchronized boolean isInitialized() {
      return this.initialized;
   }

   private static Set<String> arrayToSet(String[] var0) {
      assert var0 != null;

      HashSet var1 = new HashSet(var0.length);
      String[] var2 = var0;
      int var3 = var0.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String var5 = var2[var4];
         var1.add(var5);
      }

      return Collections.unmodifiableSet(var1);
   }
}
