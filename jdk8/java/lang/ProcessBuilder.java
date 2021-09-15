package java.lang;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class ProcessBuilder {
   private List<String> command;
   private File directory;
   private Map<String, String> environment;
   private boolean redirectErrorStream;
   private ProcessBuilder.Redirect[] redirects;

   public ProcessBuilder(List<String> var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.command = var1;
      }
   }

   public ProcessBuilder(String... var1) {
      this.command = new ArrayList(var1.length);
      String[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String var5 = var2[var4];
         this.command.add(var5);
      }

   }

   public ProcessBuilder command(List<String> var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.command = var1;
         return this;
      }
   }

   public ProcessBuilder command(String... var1) {
      this.command = new ArrayList(var1.length);
      String[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String var5 = var2[var4];
         this.command.add(var5);
      }

      return this;
   }

   public List<String> command() {
      return this.command;
   }

   public Map<String, String> environment() {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkPermission(new RuntimePermission("getenv.*"));
      }

      if (this.environment == null) {
         this.environment = ProcessEnvironment.environment();
      }

      assert this.environment != null;

      return this.environment;
   }

   ProcessBuilder environment(String[] var1) {
      assert this.environment == null;

      if (var1 != null) {
         this.environment = ProcessEnvironment.emptyEnvironment(var1.length);

         assert this.environment != null;

         String[] var2 = var1;
         int var3 = var1.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            String var5 = var2[var4];
            if (var5.indexOf(0) != -1) {
               var5 = var5.replaceFirst("\u0000.*", "");
            }

            int var6 = var5.indexOf(61, 0);
            if (var6 != -1) {
               this.environment.put(var5.substring(0, var6), var5.substring(var6 + 1));
            }
         }
      }

      return this;
   }

   public File directory() {
      return this.directory;
   }

   public ProcessBuilder directory(File var1) {
      this.directory = var1;
      return this;
   }

   private ProcessBuilder.Redirect[] redirects() {
      if (this.redirects == null) {
         this.redirects = new ProcessBuilder.Redirect[]{ProcessBuilder.Redirect.PIPE, ProcessBuilder.Redirect.PIPE, ProcessBuilder.Redirect.PIPE};
      }

      return this.redirects;
   }

   public ProcessBuilder redirectInput(ProcessBuilder.Redirect var1) {
      if (var1.type() != ProcessBuilder.Redirect.Type.WRITE && var1.type() != ProcessBuilder.Redirect.Type.APPEND) {
         this.redirects()[0] = var1;
         return this;
      } else {
         throw new IllegalArgumentException("Redirect invalid for reading: " + var1);
      }
   }

   public ProcessBuilder redirectOutput(ProcessBuilder.Redirect var1) {
      if (var1.type() == ProcessBuilder.Redirect.Type.READ) {
         throw new IllegalArgumentException("Redirect invalid for writing: " + var1);
      } else {
         this.redirects()[1] = var1;
         return this;
      }
   }

   public ProcessBuilder redirectError(ProcessBuilder.Redirect var1) {
      if (var1.type() == ProcessBuilder.Redirect.Type.READ) {
         throw new IllegalArgumentException("Redirect invalid for writing: " + var1);
      } else {
         this.redirects()[2] = var1;
         return this;
      }
   }

   public ProcessBuilder redirectInput(File var1) {
      return this.redirectInput(ProcessBuilder.Redirect.from(var1));
   }

   public ProcessBuilder redirectOutput(File var1) {
      return this.redirectOutput(ProcessBuilder.Redirect.to(var1));
   }

   public ProcessBuilder redirectError(File var1) {
      return this.redirectError(ProcessBuilder.Redirect.to(var1));
   }

   public ProcessBuilder.Redirect redirectInput() {
      return this.redirects == null ? ProcessBuilder.Redirect.PIPE : this.redirects[0];
   }

   public ProcessBuilder.Redirect redirectOutput() {
      return this.redirects == null ? ProcessBuilder.Redirect.PIPE : this.redirects[1];
   }

   public ProcessBuilder.Redirect redirectError() {
      return this.redirects == null ? ProcessBuilder.Redirect.PIPE : this.redirects[2];
   }

   public ProcessBuilder inheritIO() {
      Arrays.fill(this.redirects(), ProcessBuilder.Redirect.INHERIT);
      return this;
   }

   public boolean redirectErrorStream() {
      return this.redirectErrorStream;
   }

   public ProcessBuilder redirectErrorStream(boolean var1) {
      this.redirectErrorStream = var1;
      return this;
   }

   public Process start() throws IOException {
      String[] var1 = (String[])this.command.toArray(new String[this.command.size()]);
      var1 = (String[])var1.clone();
      String[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String var5 = var2[var4];
         if (var5 == null) {
            throw new NullPointerException();
         }
      }

      String var11 = var1[0];
      SecurityManager var12 = System.getSecurityManager();
      if (var12 != null) {
         var12.checkExec(var11);
      }

      String var13 = this.directory == null ? null : this.directory.toString();

      for(int var14 = 1; var14 < var1.length; ++var14) {
         if (var1[var14].indexOf(0) >= 0) {
            throw new IOException("invalid null character in command");
         }
      }

      try {
         return ProcessImpl.start(var1, this.environment, var13, this.redirects, this.redirectErrorStream);
      } catch (IllegalArgumentException | IOException var10) {
         String var6 = ": " + var10.getMessage();
         Object var7 = var10;
         if (var10 instanceof IOException && var12 != null) {
            try {
               var12.checkRead(var11);
            } catch (SecurityException var9) {
               var6 = "";
               var7 = var9;
            }
         }

         throw new IOException("Cannot run program \"" + var11 + "\"" + (var13 == null ? "" : " (in directory \"" + var13 + "\")") + var6, (Throwable)var7);
      }
   }

   public abstract static class Redirect {
      public static final ProcessBuilder.Redirect PIPE = new ProcessBuilder.Redirect() {
         public ProcessBuilder.Redirect.Type type() {
            return ProcessBuilder.Redirect.Type.PIPE;
         }

         public String toString() {
            return this.type().toString();
         }
      };
      public static final ProcessBuilder.Redirect INHERIT = new ProcessBuilder.Redirect() {
         public ProcessBuilder.Redirect.Type type() {
            return ProcessBuilder.Redirect.Type.INHERIT;
         }

         public String toString() {
            return this.type().toString();
         }
      };

      public abstract ProcessBuilder.Redirect.Type type();

      public File file() {
         return null;
      }

      boolean append() {
         throw new UnsupportedOperationException();
      }

      public static ProcessBuilder.Redirect from(final File var0) {
         if (var0 == null) {
            throw new NullPointerException();
         } else {
            return new ProcessBuilder.Redirect() {
               public ProcessBuilder.Redirect.Type type() {
                  return ProcessBuilder.Redirect.Type.READ;
               }

               public File file() {
                  return var0;
               }

               public String toString() {
                  return "redirect to read from file \"" + var0 + "\"";
               }
            };
         }
      }

      public static ProcessBuilder.Redirect to(final File var0) {
         if (var0 == null) {
            throw new NullPointerException();
         } else {
            return new ProcessBuilder.Redirect() {
               public ProcessBuilder.Redirect.Type type() {
                  return ProcessBuilder.Redirect.Type.WRITE;
               }

               public File file() {
                  return var0;
               }

               public String toString() {
                  return "redirect to write to file \"" + var0 + "\"";
               }

               boolean append() {
                  return false;
               }
            };
         }
      }

      public static ProcessBuilder.Redirect appendTo(final File var0) {
         if (var0 == null) {
            throw new NullPointerException();
         } else {
            return new ProcessBuilder.Redirect() {
               public ProcessBuilder.Redirect.Type type() {
                  return ProcessBuilder.Redirect.Type.APPEND;
               }

               public File file() {
                  return var0;
               }

               public String toString() {
                  return "redirect to append to file \"" + var0 + "\"";
               }

               boolean append() {
                  return true;
               }
            };
         }
      }

      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof ProcessBuilder.Redirect)) {
            return false;
         } else {
            ProcessBuilder.Redirect var2 = (ProcessBuilder.Redirect)var1;
            if (var2.type() != this.type()) {
               return false;
            } else {
               assert this.file() != null;

               return this.file().equals(var2.file());
            }
         }
      }

      public int hashCode() {
         File var1 = this.file();
         return var1 == null ? super.hashCode() : var1.hashCode();
      }

      private Redirect() {
      }

      // $FF: synthetic method
      Redirect(Object var1) {
         this();
      }

      public static enum Type {
         PIPE,
         INHERIT,
         READ,
         WRITE,
         APPEND;
      }
   }

   static class NullOutputStream extends OutputStream {
      static final ProcessBuilder.NullOutputStream INSTANCE = new ProcessBuilder.NullOutputStream();

      private NullOutputStream() {
      }

      public void write(int var1) throws IOException {
         throw new IOException("Stream closed");
      }
   }

   static class NullInputStream extends InputStream {
      static final ProcessBuilder.NullInputStream INSTANCE = new ProcessBuilder.NullInputStream();

      private NullInputStream() {
      }

      public int read() {
         return -1;
      }

      public int available() {
         return 0;
      }
   }
}
