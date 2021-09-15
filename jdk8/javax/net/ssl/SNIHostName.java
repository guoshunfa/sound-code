package javax.net.ssl;

import java.net.IDN;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

public final class SNIHostName extends SNIServerName {
   private final String hostname;

   public SNIHostName(String var1) {
      super(0, (var1 = IDN.toASCII((String)Objects.requireNonNull(var1, (String)"Server name value of host_name cannot be null"), 2)).getBytes(StandardCharsets.US_ASCII));
      this.hostname = var1;
      this.checkHostName();
   }

   public SNIHostName(byte[] var1) {
      super(0, var1);

      try {
         CharsetDecoder var2 = StandardCharsets.UTF_8.newDecoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
         this.hostname = IDN.toASCII(var2.decode(ByteBuffer.wrap(var1)).toString());
      } catch (CharacterCodingException | RuntimeException var3) {
         throw new IllegalArgumentException("The encoded server name value is invalid", var3);
      }

      this.checkHostName();
   }

   public String getAsciiName() {
      return this.hostname;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return var1 instanceof SNIHostName ? this.hostname.equalsIgnoreCase(((SNIHostName)var1).hostname) : false;
      }
   }

   public int hashCode() {
      byte var1 = 17;
      int var2 = 31 * var1 + this.hostname.toUpperCase(Locale.ENGLISH).hashCode();
      return var2;
   }

   public String toString() {
      return "type=host_name (0), value=" + this.hostname;
   }

   public static SNIMatcher createSNIMatcher(String var0) {
      if (var0 == null) {
         throw new NullPointerException("The regular expression cannot be null");
      } else {
         return new SNIHostName.SNIHostNameMatcher(var0);
      }
   }

   private void checkHostName() {
      if (this.hostname.isEmpty()) {
         throw new IllegalArgumentException("Server name value of host_name cannot be empty");
      } else if (this.hostname.endsWith(".")) {
         throw new IllegalArgumentException("Server name value of host_name cannot have the trailing dot");
      }
   }

   private static final class SNIHostNameMatcher extends SNIMatcher {
      private final Pattern pattern;

      SNIHostNameMatcher(String var1) {
         super(0);
         this.pattern = Pattern.compile(var1, 2);
      }

      public boolean matches(SNIServerName var1) {
         if (var1 == null) {
            throw new NullPointerException("The SNIServerName argument cannot be null");
         } else {
            SNIHostName var2;
            if (!(var1 instanceof SNIHostName)) {
               if (var1.getType() != 0) {
                  throw new IllegalArgumentException("The server name type is not host_name");
               }

               try {
                  var2 = new SNIHostName(var1.getEncoded());
               } catch (IllegalArgumentException | NullPointerException var4) {
                  return false;
               }
            } else {
               var2 = (SNIHostName)var1;
            }

            String var3 = var2.getAsciiName();
            return this.pattern.matcher(var3).matches() ? true : this.pattern.matcher(IDN.toUnicode(var3)).matches();
         }
      }
   }
}
