package javax.xml.transform;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.Objects;

public class TransformerException extends Exception {
   private static final long serialVersionUID = 975798773772956428L;
   SourceLocator locator;
   Throwable containedException;

   public SourceLocator getLocator() {
      return this.locator;
   }

   public void setLocator(SourceLocator location) {
      this.locator = location;
   }

   public Throwable getException() {
      return this.containedException;
   }

   public Throwable getCause() {
      return this.containedException == this ? null : this.containedException;
   }

   public synchronized Throwable initCause(Throwable cause) {
      if (this.containedException != null) {
         throw new IllegalStateException("Can't overwrite cause");
      } else if (cause == this) {
         throw new IllegalArgumentException("Self-causation not permitted");
      } else {
         this.containedException = cause;
         return this;
      }
   }

   public TransformerException(String message) {
      this(message, (SourceLocator)null, (Throwable)null);
   }

   public TransformerException(Throwable e) {
      this((String)null, (SourceLocator)null, e);
   }

   public TransformerException(String message, Throwable e) {
      this(message, (SourceLocator)null, e);
   }

   public TransformerException(String message, SourceLocator locator) {
      this(message, locator, (Throwable)null);
   }

   public TransformerException(String message, SourceLocator locator, Throwable e) {
      super(message != null && message.length() != 0 ? message : (e == null ? "" : e.toString()));
      this.containedException = e;
      this.locator = locator;
   }

   public String getMessageAndLocation() {
      StringBuilder sbuffer = new StringBuilder();
      sbuffer.append(Objects.toString(super.getMessage(), ""));
      sbuffer.append(Objects.toString(this.getLocationAsString(), ""));
      return sbuffer.toString();
   }

   public String getLocationAsString() {
      if (this.locator == null) {
         return null;
      } else {
         return System.getSecurityManager() == null ? this.getLocationString() : (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
            public String run() {
               return TransformerException.this.getLocationString();
            }
         }, new AccessControlContext(new ProtectionDomain[]{this.getNonPrivDomain()}));
      }
   }

   private String getLocationString() {
      if (this.locator == null) {
         return null;
      } else {
         StringBuilder sbuffer = new StringBuilder();
         String systemID = this.locator.getSystemId();
         int line = this.locator.getLineNumber();
         int column = this.locator.getColumnNumber();
         if (null != systemID) {
            sbuffer.append("; SystemID: ");
            sbuffer.append(systemID);
         }

         if (0 != line) {
            sbuffer.append("; Line#: ");
            sbuffer.append(line);
         }

         if (0 != column) {
            sbuffer.append("; Column#: ");
            sbuffer.append(column);
         }

         return sbuffer.toString();
      }
   }

   public void printStackTrace() {
      this.printStackTrace(new PrintWriter(System.err, true));
   }

   public void printStackTrace(PrintStream s) {
      this.printStackTrace(new PrintWriter(s));
   }

   public void printStackTrace(PrintWriter s) {
      if (s == null) {
         s = new PrintWriter(System.err, true);
      }

      try {
         String locInfo = this.getLocationAsString();
         if (null != locInfo) {
            s.println(locInfo);
         }

         super.printStackTrace(s);
      } catch (Throwable var7) {
      }

      Throwable exception = this.getException();

      for(int i = 0; i < 10 && null != exception; ++i) {
         s.println("---------");

         try {
            if (exception instanceof TransformerException) {
               String locInfo = ((TransformerException)exception).getLocationAsString();
               if (null != locInfo) {
                  s.println(locInfo);
               }
            }

            exception.printStackTrace(s);
         } catch (Throwable var6) {
            s.println("Could not print stack trace...");
         }

         try {
            Method meth = exception.getClass().getMethod("getException", (Class[])null);
            if (null != meth) {
               Throwable prev = exception;
               exception = (Throwable)meth.invoke(exception, (Object[])null);
               if (prev == exception) {
                  break;
               }
            } else {
               exception = null;
            }
         } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException var8) {
            exception = null;
         }
      }

      s.flush();
   }

   private ProtectionDomain getNonPrivDomain() {
      CodeSource nullSource = new CodeSource((URL)null, (CodeSigner[])null);
      PermissionCollection noPermission = new Permissions();
      return new ProtectionDomain(nullSource, noPermission);
   }
}
