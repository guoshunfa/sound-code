package javax.script;

public class ScriptException extends Exception {
   private static final long serialVersionUID = 8265071037049225001L;
   private String fileName;
   private int lineNumber;
   private int columnNumber;

   public ScriptException(String var1) {
      super(var1);
      this.fileName = null;
      this.lineNumber = -1;
      this.columnNumber = -1;
   }

   public ScriptException(Exception var1) {
      super((Throwable)var1);
      this.fileName = null;
      this.lineNumber = -1;
      this.columnNumber = -1;
   }

   public ScriptException(String var1, String var2, int var3) {
      super(var1);
      this.fileName = var2;
      this.lineNumber = var3;
      this.columnNumber = -1;
   }

   public ScriptException(String var1, String var2, int var3, int var4) {
      super(var1);
      this.fileName = var2;
      this.lineNumber = var3;
      this.columnNumber = var4;
   }

   public String getMessage() {
      String var1 = super.getMessage();
      if (this.fileName != null) {
         var1 = var1 + " in " + this.fileName;
         if (this.lineNumber != -1) {
            var1 = var1 + " at line number " + this.lineNumber;
         }

         if (this.columnNumber != -1) {
            var1 = var1 + " at column number " + this.columnNumber;
         }
      }

      return var1;
   }

   public int getLineNumber() {
      return this.lineNumber;
   }

   public int getColumnNumber() {
      return this.columnNumber;
   }

   public String getFileName() {
      return this.fileName;
   }
}
