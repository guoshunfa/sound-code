package apple.applescript;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import javax.script.SimpleScriptContext;

public class AppleScriptEngine implements ScriptEngine {
   private final ScriptEngineFactory factory;
   private ScriptContext context;

   private static native void initNative();

   private static native long createContextFrom(Object var0);

   private static native Object createObjectFrom(long var0);

   private static native void disposeContext(long var0);

   private static native long evalScript(String var0, long var1);

   private static native long evalScriptFromURL(String var0, long var1);

   static void checkSecurity() {
      SecurityManager var0 = System.getSecurityManager();
      if (var0 != null) {
         var0.checkExec("/usr/bin/osascript");
      }

   }

   static void TRACE(String var0) {
   }

   protected static String getEngine() {
      TRACE("getEngine()");
      return "AppleScriptEngine";
   }

   protected static String getEngineVersion() {
      TRACE("getEngineVersion()");
      return "1.1";
   }

   protected static String getName() {
      TRACE("getName()");
      return "AppleScriptEngine";
   }

   protected static String getLanguage() {
      TRACE("getLanguage()");
      return "AppleScript";
   }

   public AppleScriptEngine() {
      TRACE("<ctor>()");
      this.factory = AppleScriptEngineFactory.getFactory();
      this.setContext(new SimpleScriptContext());
      this.put("javax.script.argv", "");
      this.init();
   }

   public AppleScriptEngine(ScriptEngineFactory var1) {
      this.factory = var1;
      this.setContext(new SimpleScriptContext());
      this.put("javax.script.argv", "");
      this.init();
   }

   private void init() {
      TRACE("init()");
      this.put("javax.script.filename", "");
      this.put("javax.script.engine", getEngine());
      this.put("javax.script.engine_version", getEngineVersion());
      this.put("javax.script.name", getName());
      this.put("javax.script.language", getLanguage());
      this.put("javax.script.language_version", this.getLanguageVersion());
      this.put("THREADING", (Object)null);
   }

   protected String getLanguageVersion() {
      TRACE("AppleScriptEngine.getLanguageVersion()");

      try {
         Object var1 = this.eval("get the version of AppleScript");
         if (var1 instanceof String) {
            return (String)var1;
         }
      } catch (ScriptException var2) {
         var2.printStackTrace();
      }

      return "unknown";
   }

   public ScriptEngineFactory getFactory() {
      return this.factory;
   }

   public ScriptContext getContext() {
      return this.context;
   }

   public void setContext(ScriptContext var1) {
      this.context = var1;
   }

   public Bindings createBindings() {
      return new SimpleBindings();
   }

   public Bindings getBindings(int var1) {
      return this.context.getBindings(var1);
   }

   public void setBindings(Bindings var1, int var2) {
      this.context.setBindings(var1, var2);
   }

   public void put(String var1, Object var2) {
      this.getBindings(100).put(var1, var2);
   }

   public Object get(String var1) {
      return this.getBindings(100).get(var1);
   }

   public Object eval(Reader var1) throws ScriptException {
      return this.eval(var1, this.getContext());
   }

   public Object eval(Reader var1, Bindings var2) throws ScriptException {
      Bindings var3 = this.getContext().getBindings(100);
      this.getContext().setBindings(var2, 100);
      Object var4 = this.eval(var1);
      this.getContext().setBindings(var3, 100);
      return var4;
   }

   public Object eval(Reader var1, ScriptContext var2) throws ScriptException {
      checkSecurity();

      try {
         File var3 = Files.createTempFile("AppleScriptEngine.", ".scpt").toFile();
         FileWriter var4 = new FileWriter(var3);

         int var5;
         while((var5 = var1.read()) != -1) {
            var4.write(var5);
         }

         var4.close();
         long var6 = this.scriptContextToNSDictionary(var2);

         Object var11;
         try {
            long var8 = evalScriptFromURL("file://" + var3.getCanonicalPath(), var6);
            Object var10 = var8 == 0L ? null : createObjectFrom(var8);
            disposeContext(var8);
            var11 = var10;
         } finally {
            disposeContext(var6);
            var3.delete();
         }

         return var11;
      } catch (IOException var16) {
         throw new ScriptException(var16);
      }
   }

   public Object eval(String var1) throws ScriptException {
      return this.eval(var1, this.getContext());
   }

   public Object eval(String var1, Bindings var2) throws ScriptException {
      Bindings var3 = this.getContext().getBindings(100);
      this.getContext().setBindings(var2, 100);
      Object var4 = this.eval(var1);
      this.getContext().setBindings(var3, 100);
      return var4;
   }

   public Object eval(String var1, ScriptContext var2) throws ScriptException {
      checkSecurity();
      long var3 = this.scriptContextToNSDictionary(var2);

      Object var8;
      try {
         long var5 = evalScript(var1, var3);
         Object var7 = var5 == 0L ? null : createObjectFrom(var5);
         disposeContext(var5);
         var8 = var7;
      } finally {
         disposeContext(var3);
      }

      return var8;
   }

   private long scriptContextToNSDictionary(ScriptContext var1) throws ScriptException {
      HashMap var2 = new HashMap();
      Iterator var3 = var1.getBindings(100).entrySet().iterator();

      while(var3.hasNext()) {
         Map.Entry var4 = (Map.Entry)var3.next();
         var2.put(((String)var4.getKey()).replaceAll("\\.", "_"), var4.getValue());
      }

      return createContextFrom(var2);
   }

   static {
      System.loadLibrary("AppleScriptEngine");
      initNative();
      TRACE("<static-init>");
   }
}
