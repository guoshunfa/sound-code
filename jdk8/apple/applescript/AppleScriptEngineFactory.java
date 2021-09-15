package apple.applescript;

import java.awt.Toolkit;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.List;
import javax.script.ScriptEngineFactory;

public class AppleScriptEngineFactory implements ScriptEngineFactory {
   private static volatile boolean initialized = false;
   static final String ENGINE_NAME = "AppleScriptEngine";
   static final String ENGINE_VERSION = "1.1";
   static final String ENGINE_SHORT_NAME = "AppleScriptEngine";
   static final String LANGUAGE = "AppleScript";

   private static native void initNative();

   static void TRACE(String var0) {
   }

   static ScriptEngineFactory getFactory() {
      TRACE("getFactory()");
      return new AppleScriptEngineFactory();
   }

   public AppleScriptEngineFactory() {
      TRACE("<ctor>()");
   }

   public String getEngineName() {
      TRACE("getEngineName()");
      return "AppleScriptEngine";
   }

   public String getEngineVersion() {
      TRACE("getEngineVersion()");
      return "1.1";
   }

   public String getLanguageName() {
      TRACE("getLanguageName()");
      return "AppleScript";
   }

   public String getLanguageVersion() {
      TRACE("getLanguageVersion()");
      return (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
         public String run() {
            AppleScriptEngine var1 = AppleScriptEngineFactory.this.getScriptEngine();
            return var1.getLanguageVersion();
         }
      });
   }

   public List<String> getExtensions() {
      TRACE("getExtensions()");
      return Arrays.asList("scpt", "applescript", "app");
   }

   public List<String> getMimeTypes() {
      TRACE("getMimeTypes()");
      return Arrays.asList("application/x-applescript", "text/plain", "text/applescript");
   }

   public List<String> getNames() {
      TRACE("getNames()");
      return Arrays.asList("AppleScriptEngine", "AppleScript", "OSA");
   }

   public String getMethodCallSyntax(String var1, String var2, String... var3) {
      return null;
   }

   public String getOutputStatement(String var1) {
      return this.getMethodCallSyntax((String)null, "print", var1);
   }

   public Object getParameter(String var1) {
      AppleScriptEngine var2 = this.getScriptEngine();
      return !var2.getBindings(100).containsKey(var1) ? null : var2.getBindings(100).get(var1);
   }

   public String getProgram(String... var1) {
      StringBuilder var2 = new StringBuilder();
      String[] var3 = var1;
      int var4 = var1.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String var6 = var3[var5];
         var2.append(var6 + "\n");
      }

      return var2.toString();
   }

   public AppleScriptEngine getScriptEngine() {
      AppleScriptEngine.checkSecurity();
      ensureInitialized();
      return new AppleScriptEngine(this);
   }

   private static synchronized void ensureInitialized() {
      if (!initialized) {
         initialized = true;
         Toolkit.getDefaultToolkit();
         System.loadLibrary("AppleScriptEngine");
         initNative();
      }

   }
}
