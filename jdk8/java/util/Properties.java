package java.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import jdk.internal.util.xml.BasicXmlPropertiesProvider;
import sun.util.spi.XmlPropertiesProvider;

public class Properties extends Hashtable<Object, Object> {
   private static final long serialVersionUID = 4112578634029874840L;
   protected Properties defaults;
   private static final char[] hexDigit = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

   public Properties() {
      this((Properties)null);
   }

   public Properties(Properties var1) {
      this.defaults = var1;
   }

   public synchronized Object setProperty(String var1, String var2) {
      return this.put(var1, var2);
   }

   public synchronized void load(Reader var1) throws IOException {
      this.load0(new Properties.LineReader(var1));
   }

   public synchronized void load(InputStream var1) throws IOException {
      this.load0(new Properties.LineReader(var1));
   }

   private void load0(Properties.LineReader var1) throws IOException {
      char[] var2 = new char[1024];

      int var3;
      while((var3 = var1.readLine()) >= 0) {
         boolean var6 = false;
         int var4 = 0;
         int var5 = var3;
         boolean var7 = false;

         char var11;
         for(boolean var8 = false; var4 < var3; ++var4) {
            var11 = var1.lineBuf[var4];
            if ((var11 == '=' || var11 == ':') && !var8) {
               var5 = var4 + 1;
               var7 = true;
               break;
            }

            if ((var11 == ' ' || var11 == '\t' || var11 == '\f') && !var8) {
               var5 = var4 + 1;
               break;
            }

            if (var11 == '\\') {
               var8 = !var8;
            } else {
               var8 = false;
            }
         }

         for(; var5 < var3; ++var5) {
            var11 = var1.lineBuf[var5];
            if (var11 != ' ' && var11 != '\t' && var11 != '\f') {
               if (var7 || var11 != '=' && var11 != ':') {
                  break;
               }

               var7 = true;
            }
         }

         String var9 = this.loadConvert(var1.lineBuf, 0, var4, var2);
         String var10 = this.loadConvert(var1.lineBuf, var5, var3 - var5, var2);
         this.put(var9, var10);
      }

   }

   private String loadConvert(char[] var1, int var2, int var3, char[] var4) {
      if (var4.length < var3) {
         int var5 = var3 * 2;
         if (var5 < 0) {
            var5 = Integer.MAX_VALUE;
         }

         var4 = new char[var5];
      }

      char[] var6 = var4;
      int var7 = 0;
      int var8 = var2 + var3;

      while(true) {
         while(true) {
            while(var2 < var8) {
               char var11 = var1[var2++];
               if (var11 == '\\') {
                  var11 = var1[var2++];
                  if (var11 == 'u') {
                     int var9 = 0;

                     for(int var10 = 0; var10 < 4; ++var10) {
                        var11 = var1[var2++];
                        switch(var11) {
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                           var9 = (var9 << 4) + var11 - 48;
                           break;
                        case ':':
                        case ';':
                        case '<':
                        case '=':
                        case '>':
                        case '?':
                        case '@':
                        case 'G':
                        case 'H':
                        case 'I':
                        case 'J':
                        case 'K':
                        case 'L':
                        case 'M':
                        case 'N':
                        case 'O':
                        case 'P':
                        case 'Q':
                        case 'R':
                        case 'S':
                        case 'T':
                        case 'U':
                        case 'V':
                        case 'W':
                        case 'X':
                        case 'Y':
                        case 'Z':
                        case '[':
                        case '\\':
                        case ']':
                        case '^':
                        case '_':
                        case '`':
                        default:
                           throw new IllegalArgumentException("Malformed \\uxxxx encoding.");
                        case 'A':
                        case 'B':
                        case 'C':
                        case 'D':
                        case 'E':
                        case 'F':
                           var9 = (var9 << 4) + 10 + var11 - 65;
                           break;
                        case 'a':
                        case 'b':
                        case 'c':
                        case 'd':
                        case 'e':
                        case 'f':
                           var9 = (var9 << 4) + 10 + var11 - 97;
                        }
                     }

                     var6[var7++] = (char)var9;
                  } else {
                     if (var11 == 't') {
                        var11 = '\t';
                     } else if (var11 == 'r') {
                        var11 = '\r';
                     } else if (var11 == 'n') {
                        var11 = '\n';
                     } else if (var11 == 'f') {
                        var11 = '\f';
                     }

                     var6[var7++] = var11;
                  }
               } else {
                  var6[var7++] = var11;
               }
            }

            return new String(var6, 0, var7);
         }
      }
   }

   private String saveConvert(String var1, boolean var2, boolean var3) {
      int var4 = var1.length();
      int var5 = var4 * 2;
      if (var5 < 0) {
         var5 = Integer.MAX_VALUE;
      }

      StringBuffer var6 = new StringBuffer(var5);

      for(int var7 = 0; var7 < var4; ++var7) {
         char var8 = var1.charAt(var7);
         if (var8 > '=' && var8 < 127) {
            if (var8 == '\\') {
               var6.append('\\');
               var6.append('\\');
            } else {
               var6.append(var8);
            }
         } else {
            switch(var8) {
            case '\t':
               var6.append('\\');
               var6.append('t');
               continue;
            case '\n':
               var6.append('\\');
               var6.append('n');
               continue;
            case '\f':
               var6.append('\\');
               var6.append('f');
               continue;
            case '\r':
               var6.append('\\');
               var6.append('r');
               continue;
            case ' ':
               if (var7 == 0 || var2) {
                  var6.append('\\');
               }

               var6.append(' ');
               continue;
            case '!':
            case '#':
            case ':':
            case '=':
               var6.append('\\');
               var6.append(var8);
               continue;
            }

            if ((var8 < ' ' || var8 > '~') & var3) {
               var6.append('\\');
               var6.append('u');
               var6.append(toHex(var8 >> 12 & 15));
               var6.append(toHex(var8 >> 8 & 15));
               var6.append(toHex(var8 >> 4 & 15));
               var6.append(toHex(var8 & 15));
            } else {
               var6.append(var8);
            }
         }
      }

      return var6.toString();
   }

   private static void writeComments(BufferedWriter var0, String var1) throws IOException {
      var0.write("#");
      int var2 = var1.length();
      int var3 = 0;
      int var4 = 0;

      for(char[] var5 = new char[]{'\\', 'u', '\u0000', '\u0000', '\u0000', '\u0000'}; var3 < var2; ++var3) {
         char var6 = var1.charAt(var3);
         if (var6 > 255 || var6 == '\n' || var6 == '\r') {
            if (var4 != var3) {
               var0.write(var1.substring(var4, var3));
            }

            if (var6 > 255) {
               var5[2] = toHex(var6 >> 12 & 15);
               var5[3] = toHex(var6 >> 8 & 15);
               var5[4] = toHex(var6 >> 4 & 15);
               var5[5] = toHex(var6 & 15);
               var0.write(new String(var5));
            } else {
               var0.newLine();
               if (var6 == '\r' && var3 != var2 - 1 && var1.charAt(var3 + 1) == '\n') {
                  ++var3;
               }

               if (var3 == var2 - 1 || var1.charAt(var3 + 1) != '#' && var1.charAt(var3 + 1) != '!') {
                  var0.write("#");
               }
            }

            var4 = var3 + 1;
         }
      }

      if (var4 != var3) {
         var0.write(var1.substring(var4, var3));
      }

      var0.newLine();
   }

   /** @deprecated */
   @Deprecated
   public void save(OutputStream var1, String var2) {
      try {
         this.store(var1, var2);
      } catch (IOException var4) {
      }

   }

   public void store(Writer var1, String var2) throws IOException {
      this.store0(var1 instanceof BufferedWriter ? (BufferedWriter)var1 : new BufferedWriter(var1), var2, false);
   }

   public void store(OutputStream var1, String var2) throws IOException {
      this.store0(new BufferedWriter(new OutputStreamWriter(var1, "8859_1")), var2, true);
   }

   private void store0(BufferedWriter var1, String var2, boolean var3) throws IOException {
      if (var2 != null) {
         writeComments(var1, var2);
      }

      var1.write("#" + (new Date()).toString());
      var1.newLine();
      synchronized(this) {
         Enumeration var5 = this.keys();

         while(true) {
            if (!var5.hasMoreElements()) {
               break;
            }

            String var6 = (String)var5.nextElement();
            String var7 = (String)this.get(var6);
            var6 = this.saveConvert(var6, true, var3);
            var7 = this.saveConvert(var7, false, var3);
            var1.write(var6 + "=" + var7);
            var1.newLine();
         }
      }

      var1.flush();
   }

   public synchronized void loadFromXML(InputStream var1) throws IOException, InvalidPropertiesFormatException {
      Properties.XmlSupport.load(this, (InputStream)Objects.requireNonNull(var1));
      var1.close();
   }

   public void storeToXML(OutputStream var1, String var2) throws IOException {
      this.storeToXML(var1, var2, "UTF-8");
   }

   public void storeToXML(OutputStream var1, String var2, String var3) throws IOException {
      Properties.XmlSupport.save(this, (OutputStream)Objects.requireNonNull(var1), var2, (String)Objects.requireNonNull(var3));
   }

   public String getProperty(String var1) {
      Object var2 = super.get(var1);
      String var3 = var2 instanceof String ? (String)var2 : null;
      return var3 == null && this.defaults != null ? this.defaults.getProperty(var1) : var3;
   }

   public String getProperty(String var1, String var2) {
      String var3 = this.getProperty(var1);
      return var3 == null ? var2 : var3;
   }

   public Enumeration<?> propertyNames() {
      Hashtable var1 = new Hashtable();
      this.enumerate(var1);
      return var1.keys();
   }

   public Set<String> stringPropertyNames() {
      Hashtable var1 = new Hashtable();
      this.enumerateStringProperties(var1);
      return var1.keySet();
   }

   public void list(PrintStream var1) {
      var1.println("-- listing properties --");
      Hashtable var2 = new Hashtable();
      this.enumerate(var2);

      String var4;
      String var5;
      for(Enumeration var3 = var2.keys(); var3.hasMoreElements(); var1.println(var4 + "=" + var5)) {
         var4 = (String)var3.nextElement();
         var5 = (String)var2.get(var4);
         if (var5.length() > 40) {
            var5 = var5.substring(0, 37) + "...";
         }
      }

   }

   public void list(PrintWriter var1) {
      var1.println("-- listing properties --");
      Hashtable var2 = new Hashtable();
      this.enumerate(var2);

      String var4;
      String var5;
      for(Enumeration var3 = var2.keys(); var3.hasMoreElements(); var1.println(var4 + "=" + var5)) {
         var4 = (String)var3.nextElement();
         var5 = (String)var2.get(var4);
         if (var5.length() > 40) {
            var5 = var5.substring(0, 37) + "...";
         }
      }

   }

   private synchronized void enumerate(Hashtable<String, Object> var1) {
      if (this.defaults != null) {
         this.defaults.enumerate(var1);
      }

      Enumeration var2 = this.keys();

      while(var2.hasMoreElements()) {
         String var3 = (String)var2.nextElement();
         var1.put(var3, this.get(var3));
      }

   }

   private synchronized void enumerateStringProperties(Hashtable<String, String> var1) {
      if (this.defaults != null) {
         this.defaults.enumerateStringProperties(var1);
      }

      Enumeration var2 = this.keys();

      while(var2.hasMoreElements()) {
         Object var3 = var2.nextElement();
         Object var4 = this.get(var3);
         if (var3 instanceof String && var4 instanceof String) {
            var1.put((String)var3, (String)var4);
         }
      }

   }

   private static char toHex(int var0) {
      return hexDigit[var0 & 15];
   }

   private static class XmlSupport {
      private static final XmlPropertiesProvider PROVIDER = loadProvider();

      private static XmlPropertiesProvider loadProviderFromProperty(ClassLoader var0) {
         String var1 = System.getProperty("sun.util.spi.XmlPropertiesProvider");
         if (var1 == null) {
            return null;
         } else {
            try {
               Class var2 = Class.forName(var1, true, var0);
               return (XmlPropertiesProvider)var2.newInstance();
            } catch (IllegalAccessException | InstantiationException | ClassNotFoundException var3) {
               throw new ServiceConfigurationError((String)null, var3);
            }
         }
      }

      private static XmlPropertiesProvider loadProviderAsService(ClassLoader var0) {
         Iterator var1 = ServiceLoader.load(XmlPropertiesProvider.class, var0).iterator();
         return var1.hasNext() ? (XmlPropertiesProvider)var1.next() : null;
      }

      private static XmlPropertiesProvider loadProvider() {
         return (XmlPropertiesProvider)AccessController.doPrivileged(new PrivilegedAction<XmlPropertiesProvider>() {
            public XmlPropertiesProvider run() {
               ClassLoader var1 = ClassLoader.getSystemClassLoader();
               XmlPropertiesProvider var2 = Properties.XmlSupport.loadProviderFromProperty(var1);
               if (var2 != null) {
                  return var2;
               } else {
                  var2 = Properties.XmlSupport.loadProviderAsService(var1);
                  return (XmlPropertiesProvider)(var2 != null ? var2 : new BasicXmlPropertiesProvider());
               }
            }
         });
      }

      static void load(Properties var0, InputStream var1) throws IOException, InvalidPropertiesFormatException {
         PROVIDER.load(var0, var1);
      }

      static void save(Properties var0, OutputStream var1, String var2, String var3) throws IOException {
         PROVIDER.store(var0, var1, var2, var3);
      }
   }

   class LineReader {
      byte[] inByteBuf;
      char[] inCharBuf;
      char[] lineBuf = new char[1024];
      int inLimit = 0;
      int inOff = 0;
      InputStream inStream;
      Reader reader;

      public LineReader(InputStream var2) {
         this.inStream = var2;
         this.inByteBuf = new byte[8192];
      }

      public LineReader(Reader var2) {
         this.reader = var2;
         this.inCharBuf = new char[8192];
      }

      int readLine() throws IOException {
         int var1 = 0;
         boolean var2 = false;
         boolean var3 = true;
         boolean var4 = false;
         boolean var5 = true;
         boolean var6 = false;
         boolean var7 = false;
         boolean var8 = false;

         while(true) {
            while(true) {
               char var11;
               while(true) {
                  do {
                     if (this.inOff >= this.inLimit) {
                        this.inLimit = this.inStream == null ? this.reader.read(this.inCharBuf) : this.inStream.read(this.inByteBuf);
                        this.inOff = 0;
                        if (this.inLimit <= 0) {
                           if (var1 != 0 && !var4) {
                              if (var7) {
                                 --var1;
                              }

                              return var1;
                           }

                           return -1;
                        }
                     }

                     if (this.inStream != null) {
                        var11 = (char)(255 & this.inByteBuf[this.inOff++]);
                     } else {
                        var11 = this.inCharBuf[this.inOff++];
                     }

                     if (!var8) {
                        break;
                     }

                     var8 = false;
                  } while(var11 == '\n');

                  if (!var3) {
                     break;
                  }

                  if (var11 != ' ' && var11 != '\t' && var11 != '\f' && (var6 || var11 != '\r' && var11 != '\n')) {
                     var3 = false;
                     var6 = false;
                     break;
                  }
               }

               if (var5) {
                  var5 = false;
                  if (var11 == '#' || var11 == '!') {
                     var4 = true;
                     continue;
                  }
               }

               if (var11 != '\n' && var11 != '\r') {
                  this.lineBuf[var1++] = var11;
                  if (var1 == this.lineBuf.length) {
                     int var9 = this.lineBuf.length * 2;
                     if (var9 < 0) {
                        var9 = Integer.MAX_VALUE;
                     }

                     char[] var10 = new char[var9];
                     System.arraycopy(this.lineBuf, 0, var10, 0, this.lineBuf.length);
                     this.lineBuf = var10;
                  }

                  if (var11 == '\\') {
                     var7 = !var7;
                  } else {
                     var7 = false;
                  }
               } else if (!var4 && var1 != 0) {
                  if (this.inOff >= this.inLimit) {
                     this.inLimit = this.inStream == null ? this.reader.read(this.inCharBuf) : this.inStream.read(this.inByteBuf);
                     this.inOff = 0;
                     if (this.inLimit <= 0) {
                        if (var7) {
                           --var1;
                        }

                        return var1;
                     }
                  }

                  if (!var7) {
                     return var1;
                  }

                  --var1;
                  var3 = true;
                  var6 = true;
                  var7 = false;
                  if (var11 == '\r') {
                     var8 = true;
                  }
               } else {
                  var4 = false;
                  var5 = true;
                  var3 = true;
                  var1 = 0;
               }
            }
         }
      }
   }
}
