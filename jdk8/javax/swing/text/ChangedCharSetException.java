package javax.swing.text;

import java.io.IOException;

public class ChangedCharSetException extends IOException {
   String charSetSpec;
   boolean charSetKey;

   public ChangedCharSetException(String var1, boolean var2) {
      this.charSetSpec = var1;
      this.charSetKey = var2;
   }

   public String getCharSetSpec() {
      return this.charSetSpec;
   }

   public boolean keyEqualsCharSet() {
      return this.charSetKey;
   }
}
