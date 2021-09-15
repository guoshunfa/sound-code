package com.sun.org.apache.xerces.internal.util;

import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;

public class URI implements Serializable {
   static final long serialVersionUID = 1601921774685357214L;
   private static final byte[] fgLookupTable = new byte[128];
   private static final int RESERVED_CHARACTERS = 1;
   private static final int MARK_CHARACTERS = 2;
   private static final int SCHEME_CHARACTERS = 4;
   private static final int USERINFO_CHARACTERS = 8;
   private static final int ASCII_ALPHA_CHARACTERS = 16;
   private static final int ASCII_DIGIT_CHARACTERS = 32;
   private static final int ASCII_HEX_CHARACTERS = 64;
   private static final int PATH_CHARACTERS = 128;
   private static final int MASK_ALPHA_NUMERIC = 48;
   private static final int MASK_UNRESERVED_MASK = 50;
   private static final int MASK_URI_CHARACTER = 51;
   private static final int MASK_SCHEME_CHARACTER = 52;
   private static final int MASK_USERINFO_CHARACTER = 58;
   private static final int MASK_PATH_CHARACTER = 178;
   private String m_scheme;
   private String m_userinfo;
   private String m_host;
   private int m_port;
   private String m_regAuthority;
   private String m_path;
   private String m_queryString;
   private String m_fragment;
   private static boolean DEBUG;

   public URI() {
      this.m_scheme = null;
      this.m_userinfo = null;
      this.m_host = null;
      this.m_port = -1;
      this.m_regAuthority = null;
      this.m_path = null;
      this.m_queryString = null;
      this.m_fragment = null;
   }

   public URI(URI p_other) {
      this.m_scheme = null;
      this.m_userinfo = null;
      this.m_host = null;
      this.m_port = -1;
      this.m_regAuthority = null;
      this.m_path = null;
      this.m_queryString = null;
      this.m_fragment = null;
      this.initialize(p_other);
   }

   public URI(String p_uriSpec) throws URI.MalformedURIException {
      this((URI)null, p_uriSpec);
   }

   public URI(String p_uriSpec, boolean allowNonAbsoluteURI) throws URI.MalformedURIException {
      this((URI)null, p_uriSpec, allowNonAbsoluteURI);
   }

   public URI(URI p_base, String p_uriSpec) throws URI.MalformedURIException {
      this.m_scheme = null;
      this.m_userinfo = null;
      this.m_host = null;
      this.m_port = -1;
      this.m_regAuthority = null;
      this.m_path = null;
      this.m_queryString = null;
      this.m_fragment = null;
      this.initialize(p_base, p_uriSpec);
   }

   public URI(URI p_base, String p_uriSpec, boolean allowNonAbsoluteURI) throws URI.MalformedURIException {
      this.m_scheme = null;
      this.m_userinfo = null;
      this.m_host = null;
      this.m_port = -1;
      this.m_regAuthority = null;
      this.m_path = null;
      this.m_queryString = null;
      this.m_fragment = null;
      this.initialize(p_base, p_uriSpec, allowNonAbsoluteURI);
   }

   public URI(String p_scheme, String p_schemeSpecificPart) throws URI.MalformedURIException {
      this.m_scheme = null;
      this.m_userinfo = null;
      this.m_host = null;
      this.m_port = -1;
      this.m_regAuthority = null;
      this.m_path = null;
      this.m_queryString = null;
      this.m_fragment = null;
      if (p_scheme != null && p_scheme.trim().length() != 0) {
         if (p_schemeSpecificPart != null && p_schemeSpecificPart.trim().length() != 0) {
            this.setScheme(p_scheme);
            this.setPath(p_schemeSpecificPart);
         } else {
            throw new URI.MalformedURIException("Cannot construct URI with null/empty scheme-specific part!");
         }
      } else {
         throw new URI.MalformedURIException("Cannot construct URI with null/empty scheme!");
      }
   }

   public URI(String p_scheme, String p_host, String p_path, String p_queryString, String p_fragment) throws URI.MalformedURIException {
      this(p_scheme, (String)null, p_host, -1, p_path, p_queryString, p_fragment);
   }

   public URI(String p_scheme, String p_userinfo, String p_host, int p_port, String p_path, String p_queryString, String p_fragment) throws URI.MalformedURIException {
      this.m_scheme = null;
      this.m_userinfo = null;
      this.m_host = null;
      this.m_port = -1;
      this.m_regAuthority = null;
      this.m_path = null;
      this.m_queryString = null;
      this.m_fragment = null;
      if (p_scheme != null && p_scheme.trim().length() != 0) {
         if (p_host == null) {
            if (p_userinfo != null) {
               throw new URI.MalformedURIException("Userinfo may not be specified if host is not specified!");
            }

            if (p_port != -1) {
               throw new URI.MalformedURIException("Port may not be specified if host is not specified!");
            }
         }

         if (p_path != null) {
            if (p_path.indexOf(63) != -1 && p_queryString != null) {
               throw new URI.MalformedURIException("Query string cannot be specified in path and query string!");
            }

            if (p_path.indexOf(35) != -1 && p_fragment != null) {
               throw new URI.MalformedURIException("Fragment cannot be specified in both the path and fragment!");
            }
         }

         this.setScheme(p_scheme);
         this.setHost(p_host);
         this.setPort(p_port);
         this.setUserinfo(p_userinfo);
         this.setPath(p_path);
         this.setQueryString(p_queryString);
         this.setFragment(p_fragment);
      } else {
         throw new URI.MalformedURIException("Scheme is required!");
      }
   }

   private void initialize(URI p_other) {
      this.m_scheme = p_other.getScheme();
      this.m_userinfo = p_other.getUserinfo();
      this.m_host = p_other.getHost();
      this.m_port = p_other.getPort();
      this.m_regAuthority = p_other.getRegBasedAuthority();
      this.m_path = p_other.getPath();
      this.m_queryString = p_other.getQueryString();
      this.m_fragment = p_other.getFragment();
   }

   private void initialize(URI p_base, String p_uriSpec, boolean allowNonAbsoluteURI) throws URI.MalformedURIException {
      String uriSpec = p_uriSpec;
      int uriSpecLen = p_uriSpec != null ? p_uriSpec.length() : 0;
      if (p_base == null && uriSpecLen == 0) {
         if (allowNonAbsoluteURI) {
            this.m_path = "";
         } else {
            throw new URI.MalformedURIException("Cannot initialize URI with empty parameters.");
         }
      } else if (uriSpecLen == 0) {
         this.initialize(p_base);
      } else {
         int index = 0;
         int colonIdx = p_uriSpec.indexOf(58);
         int startPos;
         if (colonIdx != -1) {
            startPos = colonIdx - 1;
            int slashIdx = p_uriSpec.lastIndexOf(47, startPos);
            int queryIdx = p_uriSpec.lastIndexOf(63, startPos);
            int fragmentIdx = p_uriSpec.lastIndexOf(35, startPos);
            if (colonIdx != 0 && slashIdx == -1 && queryIdx == -1 && fragmentIdx == -1) {
               this.initializeScheme(p_uriSpec);
               index = this.m_scheme.length() + 1;
               if (colonIdx == uriSpecLen - 1 || p_uriSpec.charAt(colonIdx + 1) == '#') {
                  throw new URI.MalformedURIException("Scheme specific part cannot be empty.");
               }
            } else if (colonIdx == 0 || p_base == null && fragmentIdx != 0 && !allowNonAbsoluteURI) {
               throw new URI.MalformedURIException("No scheme found in URI.");
            }
         } else if (p_base == null && p_uriSpec.indexOf(35) != 0 && !allowNonAbsoluteURI) {
            throw new URI.MalformedURIException("No scheme found in URI.");
         }

         if (index + 1 < uriSpecLen && p_uriSpec.charAt(index) == '/' && p_uriSpec.charAt(index + 1) == '/') {
            index += 2;
            startPos = index;

            for(boolean var12 = false; index < uriSpecLen; ++index) {
               char testChar = uriSpec.charAt(index);
               if (testChar == '/' || testChar == '?' || testChar == '#') {
                  break;
               }
            }

            if (index > startPos) {
               if (!this.initializeAuthority(uriSpec.substring(startPos, index))) {
                  index = startPos - 2;
               }
            } else {
               this.m_host = "";
            }
         }

         this.initializePath(uriSpec, index);
         if (p_base != null) {
            this.absolutize(p_base);
         }

      }
   }

   private void initialize(URI p_base, String p_uriSpec) throws URI.MalformedURIException {
      String uriSpec = p_uriSpec;
      int uriSpecLen = p_uriSpec != null ? p_uriSpec.length() : 0;
      if (p_base == null && uriSpecLen == 0) {
         throw new URI.MalformedURIException("Cannot initialize URI with empty parameters.");
      } else if (uriSpecLen == 0) {
         this.initialize(p_base);
      } else {
         int index = 0;
         int colonIdx = p_uriSpec.indexOf(58);
         int startPos;
         if (colonIdx != -1) {
            startPos = colonIdx - 1;
            int slashIdx = p_uriSpec.lastIndexOf(47, startPos);
            int queryIdx = p_uriSpec.lastIndexOf(63, startPos);
            int fragmentIdx = p_uriSpec.lastIndexOf(35, startPos);
            if (colonIdx != 0 && slashIdx == -1 && queryIdx == -1 && fragmentIdx == -1) {
               this.initializeScheme(p_uriSpec);
               index = this.m_scheme.length() + 1;
               if (colonIdx == uriSpecLen - 1 || p_uriSpec.charAt(colonIdx + 1) == '#') {
                  throw new URI.MalformedURIException("Scheme specific part cannot be empty.");
               }
            } else if (colonIdx == 0 || p_base == null && fragmentIdx != 0) {
               throw new URI.MalformedURIException("No scheme found in URI.");
            }
         } else if (p_base == null && p_uriSpec.indexOf(35) != 0) {
            throw new URI.MalformedURIException("No scheme found in URI.");
         }

         if (index + 1 < uriSpecLen && p_uriSpec.charAt(index) == '/' && p_uriSpec.charAt(index + 1) == '/') {
            index += 2;
            startPos = index;

            for(boolean var11 = false; index < uriSpecLen; ++index) {
               char testChar = uriSpec.charAt(index);
               if (testChar == '/' || testChar == '?' || testChar == '#') {
                  break;
               }
            }

            if (index > startPos) {
               if (!this.initializeAuthority(uriSpec.substring(startPos, index))) {
                  index = startPos - 2;
               }
            } else {
               if (index >= uriSpecLen) {
                  throw new URI.MalformedURIException("Expected authority.");
               }

               this.m_host = "";
            }
         }

         this.initializePath(uriSpec, index);
         if (p_base != null) {
            this.absolutize(p_base);
         }

      }
   }

   public void absolutize(URI p_base) {
      if (this.m_path.length() == 0 && this.m_scheme == null && this.m_host == null && this.m_regAuthority == null) {
         this.m_scheme = p_base.getScheme();
         this.m_userinfo = p_base.getUserinfo();
         this.m_host = p_base.getHost();
         this.m_port = p_base.getPort();
         this.m_regAuthority = p_base.getRegBasedAuthority();
         this.m_path = p_base.getPath();
         if (this.m_queryString == null) {
            this.m_queryString = p_base.getQueryString();
            if (this.m_fragment == null) {
               this.m_fragment = p_base.getFragment();
            }
         }

      } else if (this.m_scheme == null) {
         this.m_scheme = p_base.getScheme();
         if (this.m_host == null && this.m_regAuthority == null) {
            this.m_userinfo = p_base.getUserinfo();
            this.m_host = p_base.getHost();
            this.m_port = p_base.getPort();
            this.m_regAuthority = p_base.getRegBasedAuthority();
            if (this.m_path.length() <= 0 || !this.m_path.startsWith("/")) {
               String path = "";
               String basePath = p_base.getPath();
               int index;
               if (basePath != null && basePath.length() > 0) {
                  index = basePath.lastIndexOf(47);
                  if (index != -1) {
                     path = basePath.substring(0, index + 1);
                  }
               } else if (this.m_path.length() > 0) {
                  path = "/";
               }

               path = path.concat(this.m_path);

               for(boolean var7 = true; (index = path.indexOf("/./")) != -1; path = path.substring(0, index + 1).concat(path.substring(index + 3))) {
               }

               if (path.endsWith("/.")) {
                  path = path.substring(0, path.length() - 1);
               }

               index = 1;
               int segIndex = true;
               String tempString = null;

               int segIndex;
               while((index = path.indexOf("/../", index)) > 0) {
                  tempString = path.substring(0, path.indexOf("/../"));
                  segIndex = tempString.lastIndexOf(47);
                  if (segIndex != -1) {
                     if (!tempString.substring(segIndex).equals("..")) {
                        path = path.substring(0, segIndex + 1).concat(path.substring(index + 4));
                        index = segIndex;
                     } else {
                        index += 4;
                     }
                  } else {
                     index += 4;
                  }
               }

               if (path.endsWith("/..")) {
                  tempString = path.substring(0, path.length() - 3);
                  segIndex = tempString.lastIndexOf(47);
                  if (segIndex != -1) {
                     path = path.substring(0, segIndex + 1);
                  }
               }

               this.m_path = path;
            }
         }
      }
   }

   private void initializeScheme(String p_uriSpec) throws URI.MalformedURIException {
      int uriSpecLen = p_uriSpec.length();
      int index = 0;
      String scheme = null;

      for(boolean var5 = false; index < uriSpecLen; ++index) {
         char testChar = p_uriSpec.charAt(index);
         if (testChar == ':' || testChar == '/' || testChar == '?' || testChar == '#') {
            break;
         }
      }

      scheme = p_uriSpec.substring(0, index);
      if (scheme.length() == 0) {
         throw new URI.MalformedURIException("No scheme found in URI.");
      } else {
         this.setScheme(scheme);
      }
   }

   private boolean initializeAuthority(String p_uriSpec) {
      int index = 0;
      int start = 0;
      int end = p_uriSpec.length();
      char testChar = false;
      String userinfo = null;
      if (p_uriSpec.indexOf(64, start) != -1) {
         while(true) {
            if (index < end) {
               char testChar = p_uriSpec.charAt(index);
               if (testChar != '@') {
                  ++index;
                  continue;
               }
            }

            userinfo = p_uriSpec.substring(start, index);
            ++index;
            break;
         }
      }

      String host = null;
      boolean hasPort = false;
      int port;
      if (index < end) {
         if (p_uriSpec.charAt(index) == '[') {
            port = p_uriSpec.indexOf(93, index);
            index = port != -1 ? port : end;
            if (index + 1 < end && p_uriSpec.charAt(index + 1) == ':') {
               ++index;
               hasPort = true;
            } else {
               index = end;
            }
         } else {
            port = p_uriSpec.lastIndexOf(58, end);
            index = port > index ? port : end;
            hasPort = index != end;
         }
      }

      host = p_uriSpec.substring(index, index);
      port = -1;
      if (host.length() > 0 && hasPort) {
         ++index;

         int start;
         for(start = index; index < end; ++index) {
         }

         String portStr = p_uriSpec.substring(start, index);
         if (portStr.length() > 0) {
            try {
               port = Integer.parseInt(portStr);
               if (port == -1) {
                  --port;
               }
            } catch (NumberFormatException var12) {
               port = -2;
            }
         }
      }

      if (this.isValidServerBasedAuthority(host, port, userinfo)) {
         this.m_host = host;
         this.m_port = port;
         this.m_userinfo = userinfo;
         return true;
      } else if (this.isValidRegistryBasedAuthority(p_uriSpec)) {
         this.m_regAuthority = p_uriSpec;
         return true;
      } else {
         return false;
      }
   }

   private boolean isValidServerBasedAuthority(String host, int port, String userinfo) {
      if (!isWellFormedAddress(host)) {
         return false;
      } else if (port >= -1 && port <= 65535) {
         if (userinfo != null) {
            int index = 0;
            int end = userinfo.length();

            for(boolean var6 = false; index < end; ++index) {
               char testChar = userinfo.charAt(index);
               if (testChar == '%') {
                  if (index + 2 >= end || !isHex(userinfo.charAt(index + 1)) || !isHex(userinfo.charAt(index + 2))) {
                     return false;
                  }

                  index += 2;
               } else if (!isUserinfoCharacter(testChar)) {
                  return false;
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private boolean isValidRegistryBasedAuthority(String authority) {
      int index = 0;

      for(int end = authority.length(); index < end; ++index) {
         char testChar = authority.charAt(index);
         if (testChar == '%') {
            if (index + 2 >= end || !isHex(authority.charAt(index + 1)) || !isHex(authority.charAt(index + 2))) {
               return false;
            }

            index += 2;
         } else if (!isPathCharacter(testChar)) {
            return false;
         }
      }

      return true;
   }

   private void initializePath(String p_uriSpec, int p_nStartIndex) throws URI.MalformedURIException {
      if (p_uriSpec == null) {
         throw new URI.MalformedURIException("Cannot initialize path from null string!");
      } else {
         int index = p_nStartIndex;
         int end = p_uriSpec.length();
         char testChar = 0;
         if (p_nStartIndex < end) {
            if (this.getScheme() != null && p_uriSpec.charAt(p_nStartIndex) != '/') {
               for(; index < end; ++index) {
                  testChar = p_uriSpec.charAt(index);
                  if (testChar == '?' || testChar == '#') {
                     break;
                  }

                  if (testChar == '%') {
                     if (index + 2 >= end || !isHex(p_uriSpec.charAt(index + 1)) || !isHex(p_uriSpec.charAt(index + 2))) {
                        throw new URI.MalformedURIException("Opaque part contains invalid escape sequence!");
                     }

                     index += 2;
                  } else if (!isURICharacter(testChar)) {
                     throw new URI.MalformedURIException("Opaque part contains invalid character: " + testChar);
                  }
               }
            } else {
               for(; index < end; ++index) {
                  testChar = p_uriSpec.charAt(index);
                  if (testChar == '%') {
                     if (index + 2 >= end || !isHex(p_uriSpec.charAt(index + 1)) || !isHex(p_uriSpec.charAt(index + 2))) {
                        throw new URI.MalformedURIException("Path contains invalid escape sequence!");
                     }

                     index += 2;
                  } else if (!isPathCharacter(testChar)) {
                     if (testChar != '?' && testChar != '#') {
                        throw new URI.MalformedURIException("Path contains invalid character: " + testChar);
                     }
                     break;
                  }
               }
            }
         }

         this.m_path = p_uriSpec.substring(p_nStartIndex, index);
         int start;
         if (testChar == '?') {
            ++index;
            start = index;

            while(true) {
               if (index < end) {
                  testChar = p_uriSpec.charAt(index);
                  if (testChar != '#') {
                     if (testChar == '%') {
                        if (index + 2 >= end || !isHex(p_uriSpec.charAt(index + 1)) || !isHex(p_uriSpec.charAt(index + 2))) {
                           throw new URI.MalformedURIException("Query string contains invalid escape sequence!");
                        }

                        index += 2;
                     } else if (!isURICharacter(testChar)) {
                        throw new URI.MalformedURIException("Query string contains invalid character: " + testChar);
                     }

                     ++index;
                     continue;
                  }
               }

               this.m_queryString = p_uriSpec.substring(start, index);
               break;
            }
         }

         if (testChar == '#') {
            ++index;
            start = index;

            while(true) {
               if (index >= end) {
                  this.m_fragment = p_uriSpec.substring(start, index);
                  break;
               }

               testChar = p_uriSpec.charAt(index);
               if (testChar == '%') {
                  if (index + 2 >= end || !isHex(p_uriSpec.charAt(index + 1)) || !isHex(p_uriSpec.charAt(index + 2))) {
                     throw new URI.MalformedURIException("Fragment contains invalid escape sequence!");
                  }

                  index += 2;
               } else if (!isURICharacter(testChar)) {
                  throw new URI.MalformedURIException("Fragment contains invalid character: " + testChar);
               }

               ++index;
            }
         }

      }
   }

   public String getScheme() {
      return this.m_scheme;
   }

   public String getSchemeSpecificPart() {
      StringBuilder schemespec = new StringBuilder();
      if (this.m_host != null || this.m_regAuthority != null) {
         schemespec.append("//");
         if (this.m_host != null) {
            if (this.m_userinfo != null) {
               schemespec.append(this.m_userinfo);
               schemespec.append('@');
            }

            schemespec.append(this.m_host);
            if (this.m_port != -1) {
               schemespec.append(':');
               schemespec.append(this.m_port);
            }
         } else {
            schemespec.append(this.m_regAuthority);
         }
      }

      if (this.m_path != null) {
         schemespec.append(this.m_path);
      }

      if (this.m_queryString != null) {
         schemespec.append('?');
         schemespec.append(this.m_queryString);
      }

      if (this.m_fragment != null) {
         schemespec.append('#');
         schemespec.append(this.m_fragment);
      }

      return schemespec.toString();
   }

   public String getUserinfo() {
      return this.m_userinfo;
   }

   public String getHost() {
      return this.m_host;
   }

   public int getPort() {
      return this.m_port;
   }

   public String getRegBasedAuthority() {
      return this.m_regAuthority;
   }

   public String getAuthority() {
      StringBuilder authority = new StringBuilder();
      if (this.m_host != null || this.m_regAuthority != null) {
         authority.append("//");
         if (this.m_host != null) {
            if (this.m_userinfo != null) {
               authority.append(this.m_userinfo);
               authority.append('@');
            }

            authority.append(this.m_host);
            if (this.m_port != -1) {
               authority.append(':');
               authority.append(this.m_port);
            }
         } else {
            authority.append(this.m_regAuthority);
         }
      }

      return authority.toString();
   }

   public String getPath(boolean p_includeQueryString, boolean p_includeFragment) {
      StringBuilder pathString = new StringBuilder(this.m_path);
      if (p_includeQueryString && this.m_queryString != null) {
         pathString.append('?');
         pathString.append(this.m_queryString);
      }

      if (p_includeFragment && this.m_fragment != null) {
         pathString.append('#');
         pathString.append(this.m_fragment);
      }

      return pathString.toString();
   }

   public String getPath() {
      return this.m_path;
   }

   public String getQueryString() {
      return this.m_queryString;
   }

   public String getFragment() {
      return this.m_fragment;
   }

   public void setScheme(String p_scheme) throws URI.MalformedURIException {
      if (p_scheme == null) {
         throw new URI.MalformedURIException("Cannot set scheme from null string!");
      } else if (!isConformantSchemeName(p_scheme)) {
         throw new URI.MalformedURIException("The scheme is not conformant.");
      } else {
         this.m_scheme = p_scheme.toLowerCase();
      }
   }

   public void setUserinfo(String p_userinfo) throws URI.MalformedURIException {
      if (p_userinfo == null) {
         this.m_userinfo = null;
      } else if (this.m_host == null) {
         throw new URI.MalformedURIException("Userinfo cannot be set when host is null!");
      } else {
         int index = 0;
         int end = p_userinfo.length();

         for(boolean var4 = false; index < end; ++index) {
            char testChar = p_userinfo.charAt(index);
            if (testChar == '%') {
               if (index + 2 >= end || !isHex(p_userinfo.charAt(index + 1)) || !isHex(p_userinfo.charAt(index + 2))) {
                  throw new URI.MalformedURIException("Userinfo contains invalid escape sequence!");
               }
            } else if (!isUserinfoCharacter(testChar)) {
               throw new URI.MalformedURIException("Userinfo contains invalid character:" + testChar);
            }
         }

         this.m_userinfo = p_userinfo;
      }
   }

   public void setHost(String p_host) throws URI.MalformedURIException {
      if (p_host != null && p_host.length() != 0) {
         if (!isWellFormedAddress(p_host)) {
            throw new URI.MalformedURIException("Host is not a well formed address!");
         } else {
            this.m_host = p_host;
            this.m_regAuthority = null;
         }
      } else {
         if (p_host != null) {
            this.m_regAuthority = null;
         }

         this.m_host = p_host;
         this.m_userinfo = null;
         this.m_port = -1;
      }
   }

   public void setPort(int p_port) throws URI.MalformedURIException {
      if (p_port >= 0 && p_port <= 65535) {
         if (this.m_host == null) {
            throw new URI.MalformedURIException("Port cannot be set when host is null!");
         }
      } else if (p_port != -1) {
         throw new URI.MalformedURIException("Invalid port number!");
      }

      this.m_port = p_port;
   }

   public void setRegBasedAuthority(String authority) throws URI.MalformedURIException {
      if (authority == null) {
         this.m_regAuthority = null;
      } else if (authority.length() >= 1 && this.isValidRegistryBasedAuthority(authority) && authority.indexOf(47) == -1) {
         this.m_regAuthority = authority;
         this.m_host = null;
         this.m_userinfo = null;
         this.m_port = -1;
      } else {
         throw new URI.MalformedURIException("Registry based authority is not well formed.");
      }
   }

   public void setPath(String p_path) throws URI.MalformedURIException {
      if (p_path == null) {
         this.m_path = null;
         this.m_queryString = null;
         this.m_fragment = null;
      } else {
         this.initializePath(p_path, 0);
      }

   }

   public void appendPath(String p_addToPath) throws URI.MalformedURIException {
      if (p_addToPath != null && p_addToPath.trim().length() != 0) {
         if (!isURIString(p_addToPath)) {
            throw new URI.MalformedURIException("Path contains invalid character!");
         } else {
            if (this.m_path != null && this.m_path.trim().length() != 0) {
               if (this.m_path.endsWith("/")) {
                  if (p_addToPath.startsWith("/")) {
                     this.m_path = this.m_path.concat(p_addToPath.substring(1));
                  } else {
                     this.m_path = this.m_path.concat(p_addToPath);
                  }
               } else if (p_addToPath.startsWith("/")) {
                  this.m_path = this.m_path.concat(p_addToPath);
               } else {
                  this.m_path = this.m_path.concat("/" + p_addToPath);
               }
            } else if (p_addToPath.startsWith("/")) {
               this.m_path = p_addToPath;
            } else {
               this.m_path = "/" + p_addToPath;
            }

         }
      }
   }

   public void setQueryString(String p_queryString) throws URI.MalformedURIException {
      if (p_queryString == null) {
         this.m_queryString = null;
      } else {
         if (!this.isGenericURI()) {
            throw new URI.MalformedURIException("Query string can only be set for a generic URI!");
         }

         if (this.getPath() == null) {
            throw new URI.MalformedURIException("Query string cannot be set when path is null!");
         }

         if (!isURIString(p_queryString)) {
            throw new URI.MalformedURIException("Query string contains invalid character!");
         }

         this.m_queryString = p_queryString;
      }

   }

   public void setFragment(String p_fragment) throws URI.MalformedURIException {
      if (p_fragment == null) {
         this.m_fragment = null;
      } else {
         if (!this.isGenericURI()) {
            throw new URI.MalformedURIException("Fragment can only be set for a generic URI!");
         }

         if (this.getPath() == null) {
            throw new URI.MalformedURIException("Fragment cannot be set when path is null!");
         }

         if (!isURIString(p_fragment)) {
            throw new URI.MalformedURIException("Fragment contains invalid character!");
         }

         this.m_fragment = p_fragment;
      }

   }

   public boolean equals(Object p_test) {
      if (p_test instanceof URI) {
         URI testURI = (URI)p_test;
         if ((this.m_scheme == null && testURI.m_scheme == null || this.m_scheme != null && testURI.m_scheme != null && this.m_scheme.equals(testURI.m_scheme)) && (this.m_userinfo == null && testURI.m_userinfo == null || this.m_userinfo != null && testURI.m_userinfo != null && this.m_userinfo.equals(testURI.m_userinfo)) && (this.m_host == null && testURI.m_host == null || this.m_host != null && testURI.m_host != null && this.m_host.equals(testURI.m_host)) && this.m_port == testURI.m_port && (this.m_path == null && testURI.m_path == null || this.m_path != null && testURI.m_path != null && this.m_path.equals(testURI.m_path)) && (this.m_queryString == null && testURI.m_queryString == null || this.m_queryString != null && testURI.m_queryString != null && this.m_queryString.equals(testURI.m_queryString)) && (this.m_fragment == null && testURI.m_fragment == null || this.m_fragment != null && testURI.m_fragment != null && this.m_fragment.equals(testURI.m_fragment))) {
            return true;
         }
      }

      return false;
   }

   public int hashCode() {
      int hash = 5;
      int hash = 47 * hash + Objects.hashCode(this.m_scheme);
      hash = 47 * hash + Objects.hashCode(this.m_userinfo);
      hash = 47 * hash + Objects.hashCode(this.m_host);
      hash = 47 * hash + this.m_port;
      hash = 47 * hash + Objects.hashCode(this.m_path);
      hash = 47 * hash + Objects.hashCode(this.m_queryString);
      hash = 47 * hash + Objects.hashCode(this.m_fragment);
      return hash;
   }

   public String toString() {
      StringBuilder uriSpecString = new StringBuilder();
      if (this.m_scheme != null) {
         uriSpecString.append(this.m_scheme);
         uriSpecString.append(':');
      }

      uriSpecString.append(this.getSchemeSpecificPart());
      return uriSpecString.toString();
   }

   public boolean isGenericURI() {
      return this.m_host != null;
   }

   public boolean isAbsoluteURI() {
      return this.m_scheme != null;
   }

   public static boolean isConformantSchemeName(String p_scheme) {
      if (p_scheme != null && p_scheme.trim().length() != 0) {
         if (!isAlpha(p_scheme.charAt(0))) {
            return false;
         } else {
            int schemeLength = p_scheme.length();

            for(int i = 1; i < schemeLength; ++i) {
               char testChar = p_scheme.charAt(i);
               if (!isSchemeCharacter(testChar)) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public static boolean isWellFormedAddress(String address) {
      if (address == null) {
         return false;
      } else {
         int addrLength = address.length();
         if (addrLength == 0) {
            return false;
         } else if (address.startsWith("[")) {
            return isWellFormedIPv6Reference(address);
         } else if (!address.startsWith(".") && !address.startsWith("-") && !address.endsWith("-")) {
            int index = address.lastIndexOf(46);
            if (address.endsWith(".")) {
               index = address.substring(0, index).lastIndexOf(46);
            }

            if (index + 1 < addrLength && isDigit(address.charAt(index + 1))) {
               return isWellFormedIPv4Address(address);
            } else if (addrLength > 255) {
               return false;
            } else {
               int labelCharCount = 0;

               for(int i = 0; i < addrLength; ++i) {
                  char testChar = address.charAt(i);
                  if (testChar == '.') {
                     if (!isAlphanum(address.charAt(i - 1))) {
                        return false;
                     }

                     if (i + 1 < addrLength && !isAlphanum(address.charAt(i + 1))) {
                        return false;
                     }

                     labelCharCount = 0;
                  } else {
                     if (!isAlphanum(testChar) && testChar != '-') {
                        return false;
                     }

                     ++labelCharCount;
                     if (labelCharCount > 63) {
                        return false;
                     }
                  }
               }

               return true;
            }
         } else {
            return false;
         }
      }
   }

   public static boolean isWellFormedIPv4Address(String address) {
      int addrLength = address.length();
      int numDots = 0;
      int numDigits = 0;

      for(int i = 0; i < addrLength; ++i) {
         char testChar = address.charAt(i);
         if (testChar != '.') {
            if (!isDigit(testChar)) {
               return false;
            }

            ++numDigits;
            if (numDigits > 3) {
               return false;
            }

            if (numDigits == 3) {
               char first = address.charAt(i - 2);
               char second = address.charAt(i - 1);
               if (first >= '2' && (first != '2' || second >= '5' && (second != '5' || testChar > '5'))) {
                  return false;
               }
            }
         } else {
            if (i > 0 && !isDigit(address.charAt(i - 1)) || i + 1 < addrLength && !isDigit(address.charAt(i + 1))) {
               return false;
            }

            numDigits = 0;
            ++numDots;
            if (numDots > 3) {
               return false;
            }
         }
      }

      return numDots == 3;
   }

   public static boolean isWellFormedIPv6Reference(String address) {
      int addrLength = address.length();
      int index = 1;
      int end = addrLength - 1;
      if (addrLength > 2 && address.charAt(0) == '[' && address.charAt(end) == ']') {
         int[] counter = new int[1];
         int index = scanHexSequence(address, index, end, counter);
         if (index == -1) {
            return false;
         } else if (index == end) {
            return counter[0] == 8;
         } else if (index + 1 < end && address.charAt(index) == ':') {
            if (address.charAt(index + 1) == ':') {
               if (++counter[0] > 8) {
                  return false;
               } else {
                  index += 2;
                  if (index == end) {
                     return true;
                  } else {
                     int prevCount = counter[0];
                     index = scanHexSequence(address, index, end, counter);
                     return index == end || index != -1 && isWellFormedIPv4Address(address.substring(counter[0] > prevCount ? index + 1 : index, end));
                  }
               }
            } else {
               return counter[0] == 6 && isWellFormedIPv4Address(address.substring(index + 1, end));
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private static int scanHexSequence(String address, int index, int end, int[] counter) {
      int numDigits = 0;

      for(int start = index; index < end; ++index) {
         char testChar = address.charAt(index);
         if (testChar == ':') {
            if (numDigits > 0 && ++counter[0] > 8) {
               return -1;
            }

            if (numDigits == 0 || index + 1 < end && address.charAt(index + 1) == ':') {
               return index;
            }

            numDigits = 0;
         } else {
            if (!isHex(testChar)) {
               if (testChar == '.' && numDigits < 4 && numDigits > 0 && counter[0] <= 6) {
                  int back = index - numDigits - 1;
                  return back >= start ? back : back + 1;
               }

               return -1;
            }

            ++numDigits;
            if (numDigits > 4) {
               return -1;
            }
         }
      }

      return numDigits > 0 && ++counter[0] <= 8 ? end : -1;
   }

   private static boolean isDigit(char p_char) {
      return p_char >= '0' && p_char <= '9';
   }

   private static boolean isHex(char p_char) {
      return p_char <= 'f' && (fgLookupTable[p_char] & 64) != 0;
   }

   private static boolean isAlpha(char p_char) {
      return p_char >= 'a' && p_char <= 'z' || p_char >= 'A' && p_char <= 'Z';
   }

   private static boolean isAlphanum(char p_char) {
      return p_char <= 'z' && (fgLookupTable[p_char] & 48) != 0;
   }

   private static boolean isReservedCharacter(char p_char) {
      return p_char <= ']' && (fgLookupTable[p_char] & 1) != 0;
   }

   private static boolean isUnreservedCharacter(char p_char) {
      return p_char <= '~' && (fgLookupTable[p_char] & 50) != 0;
   }

   private static boolean isURICharacter(char p_char) {
      return p_char <= '~' && (fgLookupTable[p_char] & 51) != 0;
   }

   private static boolean isSchemeCharacter(char p_char) {
      return p_char <= 'z' && (fgLookupTable[p_char] & 52) != 0;
   }

   private static boolean isUserinfoCharacter(char p_char) {
      return p_char <= 'z' && (fgLookupTable[p_char] & 58) != 0;
   }

   private static boolean isPathCharacter(char p_char) {
      return p_char <= '~' && (fgLookupTable[p_char] & 178) != 0;
   }

   private static boolean isURIString(String p_uric) {
      if (p_uric == null) {
         return false;
      } else {
         int end = p_uric.length();
         char testChar = false;

         for(int i = 0; i < end; ++i) {
            char testChar = p_uric.charAt(i);
            if (testChar == '%') {
               if (i + 2 >= end || !isHex(p_uric.charAt(i + 1)) || !isHex(p_uric.charAt(i + 2))) {
                  return false;
               }

               i += 2;
            } else if (!isURICharacter(testChar)) {
               return false;
            }
         }

         return true;
      }
   }

   static {
      byte[] var10000;
      int i;
      for(i = 48; i <= 57; ++i) {
         var10000 = fgLookupTable;
         var10000[i] = (byte)(var10000[i] | 96);
      }

      for(i = 65; i <= 70; ++i) {
         var10000 = fgLookupTable;
         var10000[i] = (byte)(var10000[i] | 80);
         var10000 = fgLookupTable;
         var10000[i + 32] = (byte)(var10000[i + 32] | 80);
      }

      for(i = 71; i <= 90; ++i) {
         var10000 = fgLookupTable;
         var10000[i] = (byte)(var10000[i] | 16);
         var10000 = fgLookupTable;
         var10000[i + 32] = (byte)(var10000[i + 32] | 16);
      }

      var10000 = fgLookupTable;
      var10000[59] = (byte)(var10000[59] | 1);
      var10000 = fgLookupTable;
      var10000[47] = (byte)(var10000[47] | 1);
      var10000 = fgLookupTable;
      var10000[63] = (byte)(var10000[63] | 1);
      var10000 = fgLookupTable;
      var10000[58] = (byte)(var10000[58] | 1);
      var10000 = fgLookupTable;
      var10000[64] = (byte)(var10000[64] | 1);
      var10000 = fgLookupTable;
      var10000[38] = (byte)(var10000[38] | 1);
      var10000 = fgLookupTable;
      var10000[61] = (byte)(var10000[61] | 1);
      var10000 = fgLookupTable;
      var10000[43] = (byte)(var10000[43] | 1);
      var10000 = fgLookupTable;
      var10000[36] = (byte)(var10000[36] | 1);
      var10000 = fgLookupTable;
      var10000[44] = (byte)(var10000[44] | 1);
      var10000 = fgLookupTable;
      var10000[91] = (byte)(var10000[91] | 1);
      var10000 = fgLookupTable;
      var10000[93] = (byte)(var10000[93] | 1);
      var10000 = fgLookupTable;
      var10000[45] = (byte)(var10000[45] | 2);
      var10000 = fgLookupTable;
      var10000[95] = (byte)(var10000[95] | 2);
      var10000 = fgLookupTable;
      var10000[46] = (byte)(var10000[46] | 2);
      var10000 = fgLookupTable;
      var10000[33] = (byte)(var10000[33] | 2);
      var10000 = fgLookupTable;
      var10000[126] = (byte)(var10000[126] | 2);
      var10000 = fgLookupTable;
      var10000[42] = (byte)(var10000[42] | 2);
      var10000 = fgLookupTable;
      var10000[39] = (byte)(var10000[39] | 2);
      var10000 = fgLookupTable;
      var10000[40] = (byte)(var10000[40] | 2);
      var10000 = fgLookupTable;
      var10000[41] = (byte)(var10000[41] | 2);
      var10000 = fgLookupTable;
      var10000[43] = (byte)(var10000[43] | 4);
      var10000 = fgLookupTable;
      var10000[45] = (byte)(var10000[45] | 4);
      var10000 = fgLookupTable;
      var10000[46] = (byte)(var10000[46] | 4);
      var10000 = fgLookupTable;
      var10000[59] = (byte)(var10000[59] | 8);
      var10000 = fgLookupTable;
      var10000[58] = (byte)(var10000[58] | 8);
      var10000 = fgLookupTable;
      var10000[38] = (byte)(var10000[38] | 8);
      var10000 = fgLookupTable;
      var10000[61] = (byte)(var10000[61] | 8);
      var10000 = fgLookupTable;
      var10000[43] = (byte)(var10000[43] | 8);
      var10000 = fgLookupTable;
      var10000[36] = (byte)(var10000[36] | 8);
      var10000 = fgLookupTable;
      var10000[44] = (byte)(var10000[44] | 8);
      var10000 = fgLookupTable;
      var10000[59] = (byte)(var10000[59] | 128);
      var10000 = fgLookupTable;
      var10000[47] = (byte)(var10000[47] | 128);
      var10000 = fgLookupTable;
      var10000[58] = (byte)(var10000[58] | 128);
      var10000 = fgLookupTable;
      var10000[64] = (byte)(var10000[64] | 128);
      var10000 = fgLookupTable;
      var10000[38] = (byte)(var10000[38] | 128);
      var10000 = fgLookupTable;
      var10000[61] = (byte)(var10000[61] | 128);
      var10000 = fgLookupTable;
      var10000[43] = (byte)(var10000[43] | 128);
      var10000 = fgLookupTable;
      var10000[36] = (byte)(var10000[36] | 128);
      var10000 = fgLookupTable;
      var10000[44] = (byte)(var10000[44] | 128);
      DEBUG = false;
   }

   public static class MalformedURIException extends IOException {
      static final long serialVersionUID = -6695054834342951930L;

      public MalformedURIException() {
      }

      public MalformedURIException(String p_msg) {
         super(p_msg);
      }
   }
}
