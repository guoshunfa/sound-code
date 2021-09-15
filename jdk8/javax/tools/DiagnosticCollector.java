package javax.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class DiagnosticCollector<S> implements DiagnosticListener<S> {
   private List<Diagnostic<? extends S>> diagnostics = Collections.synchronizedList(new ArrayList());

   public void report(Diagnostic<? extends S> var1) {
      var1.getClass();
      this.diagnostics.add(var1);
   }

   public List<Diagnostic<? extends S>> getDiagnostics() {
      return Collections.unmodifiableList(this.diagnostics);
   }
}
