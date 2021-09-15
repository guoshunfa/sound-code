package java.net;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.TimeZone;
import sun.misc.JavaNetHttpCookieAccess;
import sun.misc.SharedSecrets;

public final class HttpCookie implements Cloneable {
   private final String name;
   private String value;
   private String comment;
   private String commentURL;
   private boolean toDiscard;
   private String domain;
   private long maxAge;
   private String path;
   private String portlist;
   private boolean secure;
   private boolean httpOnly;
   private int version;
   private final String header;
   private final long whenCreated;
   private static final long MAX_AGE_UNSPECIFIED = -1L;
   private static final String[] COOKIE_DATE_FORMATS = new String[]{"EEE',' dd-MMM-yyyy HH:mm:ss 'GMT'", "EEE',' dd MMM yyyy HH:mm:ss 'GMT'", "EEE MMM dd yyyy HH:mm:ss 'GMT'Z", "EEE',' dd-MMM-yy HH:mm:ss 'GMT'", "EEE',' dd MMM yy HH:mm:ss 'GMT'", "EEE MMM dd yy HH:mm:ss 'GMT'Z"};
   private static final String SET_COOKIE = "set-cookie:";
   private static final String SET_COOKIE2 = "set-cookie2:";
   private static final String tspecials = ",; ";
   static final Map<String, HttpCookie.CookieAttributeAssignor> assignors = new HashMap();
   static final TimeZone GMT;

   public HttpCookie(String var1, String var2) {
      this(var1, var2, (String)null);
   }

   private HttpCookie(String var1, String var2, String var3) {
      this.maxAge = -1L;
      this.version = 1;
      var1 = var1.trim();
      if (var1.length() != 0 && isToken(var1) && var1.charAt(0) != '$') {
         this.name = var1;
         this.value = var2;
         this.toDiscard = false;
         this.secure = false;
         this.whenCreated = System.currentTimeMillis();
         this.portlist = null;
         this.header = var3;
      } else {
         throw new IllegalArgumentException("Illegal cookie name");
      }
   }

   public static List<HttpCookie> parse(String var0) {
      return parse(var0, false);
   }

   private static List<HttpCookie> parse(String var0, boolean var1) {
      int var2 = guessCookieVersion(var0);
      if (startsWithIgnoreCase(var0, "set-cookie2:")) {
         var0 = var0.substring("set-cookie2:".length());
      } else if (startsWithIgnoreCase(var0, "set-cookie:")) {
         var0 = var0.substring("set-cookie:".length());
      }

      ArrayList var3 = new ArrayList();
      if (var2 == 0) {
         HttpCookie var4 = parseInternal(var0, var1);
         var4.setVersion(0);
         var3.add(var4);
      } else {
         List var8 = splitMultiCookies(var0);
         Iterator var5 = var8.iterator();

         while(var5.hasNext()) {
            String var6 = (String)var5.next();
            HttpCookie var7 = parseInternal(var6, var1);
            var7.setVersion(1);
            var3.add(var7);
         }
      }

      return var3;
   }

   public boolean hasExpired() {
      if (this.maxAge == 0L) {
         return true;
      } else if (this.maxAge == -1L) {
         return false;
      } else {
         long var1 = (System.currentTimeMillis() - this.whenCreated) / 1000L;
         return var1 > this.maxAge;
      }
   }

   public void setComment(String var1) {
      this.comment = var1;
   }

   public String getComment() {
      return this.comment;
   }

   public void setCommentURL(String var1) {
      this.commentURL = var1;
   }

   public String getCommentURL() {
      return this.commentURL;
   }

   public void setDiscard(boolean var1) {
      this.toDiscard = var1;
   }

   public boolean getDiscard() {
      return this.toDiscard;
   }

   public void setPortlist(String var1) {
      this.portlist = var1;
   }

   public String getPortlist() {
      return this.portlist;
   }

   public void setDomain(String var1) {
      if (var1 != null) {
         this.domain = var1.toLowerCase();
      } else {
         this.domain = var1;
      }

   }

   public String getDomain() {
      return this.domain;
   }

   public void setMaxAge(long var1) {
      this.maxAge = var1;
   }

   public long getMaxAge() {
      return this.maxAge;
   }

   public void setPath(String var1) {
      this.path = var1;
   }

   public String getPath() {
      return this.path;
   }

   public void setSecure(boolean var1) {
      this.secure = var1;
   }

   public boolean getSecure() {
      return this.secure;
   }

   public String getName() {
      return this.name;
   }

   public void setValue(String var1) {
      this.value = var1;
   }

   public String getValue() {
      return this.value;
   }

   public int getVersion() {
      return this.version;
   }

   public void setVersion(int var1) {
      if (var1 != 0 && var1 != 1) {
         throw new IllegalArgumentException("cookie version should be 0 or 1");
      } else {
         this.version = var1;
      }
   }

   public boolean isHttpOnly() {
      return this.httpOnly;
   }

   public void setHttpOnly(boolean var1) {
      this.httpOnly = var1;
   }

   public static boolean domainMatches(String var0, String var1) {
      if (var0 != null && var1 != null) {
         boolean var2 = ".local".equalsIgnoreCase(var0);
         int var3 = var0.indexOf(46);
         if (var3 == 0) {
            var3 = var0.indexOf(46, 1);
         }

         if (!var2 && (var3 == -1 || var3 == var0.length() - 1)) {
            return false;
         } else {
            int var4 = var1.indexOf(46);
            if (var4 == -1 && (var2 || var0.equalsIgnoreCase(var1 + ".local"))) {
               return true;
            } else {
               int var5 = var0.length();
               int var6 = var1.length() - var5;
               if (var6 == 0) {
                  return var1.equalsIgnoreCase(var0);
               } else if (var6 > 0) {
                  String var7 = var1.substring(0, var6);
                  String var8 = var1.substring(var6);
                  return var7.indexOf(46) == -1 && var8.equalsIgnoreCase(var0);
               } else if (var6 != -1) {
                  return false;
               } else {
                  return var0.charAt(0) == '.' && var1.equalsIgnoreCase(var0.substring(1));
               }
            }
         }
      } else {
         return false;
      }
   }

   public String toString() {
      return this.getVersion() > 0 ? this.toRFC2965HeaderString() : this.toNetscapeHeaderString();
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof HttpCookie)) {
         return false;
      } else {
         HttpCookie var2 = (HttpCookie)var1;
         return equalsIgnoreCase(this.getName(), var2.getName()) && equalsIgnoreCase(this.getDomain(), var2.getDomain()) && Objects.equals(this.getPath(), var2.getPath());
      }
   }

   public int hashCode() {
      int var1 = this.name.toLowerCase().hashCode();
      int var2 = this.domain != null ? this.domain.toLowerCase().hashCode() : 0;
      int var3 = this.path != null ? this.path.hashCode() : 0;
      return var1 + var2 + var3;
   }

   public Object clone() {
      try {
         return super.clone();
      } catch (CloneNotSupportedException var2) {
         throw new RuntimeException(var2.getMessage());
      }
   }

   private static boolean isToken(String var0) {
      int var1 = var0.length();

      for(int var2 = 0; var2 < var1; ++var2) {
         char var3 = var0.charAt(var2);
         if (var3 < ' ' || var3 >= 127 || ",; ".indexOf(var3) != -1) {
            return false;
         }
      }

      return true;
   }

   private static HttpCookie parseInternal(String var0, boolean var1) {
      HttpCookie var2 = null;
      String var3 = null;
      StringTokenizer var4 = new StringTokenizer(var0, ";");

      int var5;
      String var6;
      String var7;
      try {
         var3 = var4.nextToken();
         var5 = var3.indexOf(61);
         if (var5 == -1) {
            throw new IllegalArgumentException("Invalid cookie name-value pair");
         }

         var6 = var3.substring(0, var5).trim();
         var7 = var3.substring(var5 + 1).trim();
         if (var1) {
            var2 = new HttpCookie(var6, stripOffSurroundingQuote(var7), var0);
         } else {
            var2 = new HttpCookie(var6, stripOffSurroundingQuote(var7));
         }
      } catch (NoSuchElementException var8) {
         throw new IllegalArgumentException("Empty cookie header string");
      }

      for(; var4.hasMoreTokens(); assignAttribute(var2, var6, var7)) {
         var3 = var4.nextToken();
         var5 = var3.indexOf(61);
         if (var5 != -1) {
            var6 = var3.substring(0, var5).trim();
            var7 = var3.substring(var5 + 1).trim();
         } else {
            var6 = var3.trim();
            var7 = null;
         }
      }

      return var2;
   }

   private static void assignAttribute(HttpCookie var0, String var1, String var2) {
      var2 = stripOffSurroundingQuote(var2);
      HttpCookie.CookieAttributeAssignor var3 = (HttpCookie.CookieAttributeAssignor)assignors.get(var1.toLowerCase());
      if (var3 != null) {
         var3.assign(var0, var1, var2);
      }

   }

   private String header() {
      return this.header;
   }

   private String toNetscapeHeaderString() {
      return this.getName() + "=" + this.getValue();
   }

   private String toRFC2965HeaderString() {
      StringBuilder var1 = new StringBuilder();
      var1.append(this.getName()).append("=\"").append(this.getValue()).append('"');
      if (this.getPath() != null) {
         var1.append(";$Path=\"").append(this.getPath()).append('"');
      }

      if (this.getDomain() != null) {
         var1.append(";$Domain=\"").append(this.getDomain()).append('"');
      }

      if (this.getPortlist() != null) {
         var1.append(";$Port=\"").append(this.getPortlist()).append('"');
      }

      return var1.toString();
   }

   private long expiryDate2DeltaSeconds(String var1) {
      GregorianCalendar var2 = new GregorianCalendar(GMT);
      int var3 = 0;

      while(var3 < COOKIE_DATE_FORMATS.length) {
         SimpleDateFormat var4 = new SimpleDateFormat(COOKIE_DATE_FORMATS[var3], Locale.US);
         var2.set(1970, 0, 1, 0, 0, 0);
         var4.setTimeZone(GMT);
         var4.setLenient(false);
         var4.set2DigitYearStart(var2.getTime());

         try {
            var2.setTime(var4.parse(var1));
            if (!COOKIE_DATE_FORMATS[var3].contains("yyyy")) {
               int var5 = var2.get(1);
               var5 %= 100;
               if (var5 < 70) {
                  var5 += 2000;
               } else {
                  var5 += 1900;
               }

               var2.set(1, var5);
            }

            return (var2.getTimeInMillis() - this.whenCreated) / 1000L;
         } catch (Exception var6) {
            ++var3;
         }
      }

      return 0L;
   }

   private static int guessCookieVersion(String var0) {
      byte var1 = 0;
      var0 = var0.toLowerCase();
      if (var0.indexOf("expires=") != -1) {
         var1 = 0;
      } else if (var0.indexOf("version=") != -1) {
         var1 = 1;
      } else if (var0.indexOf("max-age") != -1) {
         var1 = 1;
      } else if (startsWithIgnoreCase(var0, "set-cookie2:")) {
         var1 = 1;
      }

      return var1;
   }

   private static String stripOffSurroundingQuote(String var0) {
      if (var0 != null && var0.length() > 2 && var0.charAt(0) == '"' && var0.charAt(var0.length() - 1) == '"') {
         return var0.substring(1, var0.length() - 1);
      } else {
         return var0 != null && var0.length() > 2 && var0.charAt(0) == '\'' && var0.charAt(var0.length() - 1) == '\'' ? var0.substring(1, var0.length() - 1) : var0;
      }
   }

   private static boolean equalsIgnoreCase(String var0, String var1) {
      if (var0 == var1) {
         return true;
      } else {
         return var0 != null && var1 != null ? var0.equalsIgnoreCase(var1) : false;
      }
   }

   private static boolean startsWithIgnoreCase(String var0, String var1) {
      if (var0 != null && var1 != null) {
         return var0.length() >= var1.length() && var1.equalsIgnoreCase(var0.substring(0, var1.length()));
      } else {
         return false;
      }
   }

   private static List<String> splitMultiCookies(String var0) {
      ArrayList var1 = new ArrayList();
      int var2 = 0;
      int var3 = 0;

      int var4;
      for(var4 = 0; var3 < var0.length(); ++var3) {
         char var5 = var0.charAt(var3);
         if (var5 == '"') {
            ++var2;
         }

         if (var5 == ',' && var2 % 2 == 0) {
            var1.add(var0.substring(var4, var3));
            var4 = var3 + 1;
         }
      }

      var1.add(var0.substring(var4));
      return var1;
   }

   static {
      assignors.put("comment", new HttpCookie.CookieAttributeAssignor() {
         public void assign(HttpCookie var1, String var2, String var3) {
            if (var1.getComment() == null) {
               var1.setComment(var3);
            }

         }
      });
      assignors.put("commenturl", new HttpCookie.CookieAttributeAssignor() {
         public void assign(HttpCookie var1, String var2, String var3) {
            if (var1.getCommentURL() == null) {
               var1.setCommentURL(var3);
            }

         }
      });
      assignors.put("discard", new HttpCookie.CookieAttributeAssignor() {
         public void assign(HttpCookie var1, String var2, String var3) {
            var1.setDiscard(true);
         }
      });
      assignors.put("domain", new HttpCookie.CookieAttributeAssignor() {
         public void assign(HttpCookie var1, String var2, String var3) {
            if (var1.getDomain() == null) {
               var1.setDomain(var3);
            }

         }
      });
      assignors.put("max-age", new HttpCookie.CookieAttributeAssignor() {
         public void assign(HttpCookie var1, String var2, String var3) {
            try {
               long var4 = Long.parseLong(var3);
               if (var1.getMaxAge() == -1L) {
                  var1.setMaxAge(var4);
               }

            } catch (NumberFormatException var6) {
               throw new IllegalArgumentException("Illegal cookie max-age attribute");
            }
         }
      });
      assignors.put("path", new HttpCookie.CookieAttributeAssignor() {
         public void assign(HttpCookie var1, String var2, String var3) {
            if (var1.getPath() == null) {
               var1.setPath(var3);
            }

         }
      });
      assignors.put("port", new HttpCookie.CookieAttributeAssignor() {
         public void assign(HttpCookie var1, String var2, String var3) {
            if (var1.getPortlist() == null) {
               var1.setPortlist(var3 == null ? "" : var3);
            }

         }
      });
      assignors.put("secure", new HttpCookie.CookieAttributeAssignor() {
         public void assign(HttpCookie var1, String var2, String var3) {
            var1.setSecure(true);
         }
      });
      assignors.put("httponly", new HttpCookie.CookieAttributeAssignor() {
         public void assign(HttpCookie var1, String var2, String var3) {
            var1.setHttpOnly(true);
         }
      });
      assignors.put("version", new HttpCookie.CookieAttributeAssignor() {
         public void assign(HttpCookie var1, String var2, String var3) {
            try {
               int var4 = Integer.parseInt(var3);
               var1.setVersion(var4);
            } catch (NumberFormatException var5) {
            }

         }
      });
      assignors.put("expires", new HttpCookie.CookieAttributeAssignor() {
         public void assign(HttpCookie var1, String var2, String var3) {
            if (var1.getMaxAge() == -1L) {
               var1.setMaxAge(var1.expiryDate2DeltaSeconds(var3));
            }

         }
      });
      SharedSecrets.setJavaNetHttpCookieAccess(new JavaNetHttpCookieAccess() {
         public List<HttpCookie> parse(String var1) {
            return HttpCookie.parse(var1, true);
         }

         public String header(HttpCookie var1) {
            return var1.header;
         }
      });
      GMT = TimeZone.getTimeZone("GMT");
   }

   interface CookieAttributeAssignor {
      void assign(HttpCookie var1, String var2, String var3);
   }
}
