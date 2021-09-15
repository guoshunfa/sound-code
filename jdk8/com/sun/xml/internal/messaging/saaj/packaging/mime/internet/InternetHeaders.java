package com.sun.xml.internal.messaging.saaj.packaging.mime.internet;

import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.LineInputStream;
import com.sun.xml.internal.messaging.saaj.util.FinalArrayList;
import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractList;
import java.util.List;
import java.util.NoSuchElementException;

public final class InternetHeaders {
   private final FinalArrayList headers = new FinalArrayList();
   private List headerValueView;

   public InternetHeaders() {
   }

   public InternetHeaders(InputStream is) throws MessagingException {
      this.load(is);
   }

   public void load(InputStream is) throws MessagingException {
      LineInputStream lis = new LineInputStream(is);
      String prevline = null;
      StringBuffer lineBuffer = new StringBuffer();

      try {
         String line;
         do {
            line = lis.readLine();
            if (line != null && (line.startsWith(" ") || line.startsWith("\t"))) {
               if (prevline != null) {
                  lineBuffer.append(prevline);
                  prevline = null;
               }

               lineBuffer.append("\r\n");
               lineBuffer.append(line);
            } else {
               if (prevline != null) {
                  this.addHeaderLine(prevline);
               } else if (lineBuffer.length() > 0) {
                  this.addHeaderLine(lineBuffer.toString());
                  lineBuffer.setLength(0);
               }

               prevline = line;
            }
         } while(line != null && line.length() > 0);

      } catch (IOException var7) {
         throw new MessagingException("Error in input stream", var7);
      }
   }

   public String[] getHeader(String name) {
      FinalArrayList v = new FinalArrayList();
      int len = this.headers.size();

      for(int i = 0; i < len; ++i) {
         hdr h = (hdr)this.headers.get(i);
         if (name.equalsIgnoreCase(h.name)) {
            v.add(h.getValue());
         }
      }

      if (v.size() == 0) {
         return null;
      } else {
         return (String[])((String[])v.toArray(new String[v.size()]));
      }
   }

   public String getHeader(String name, String delimiter) {
      String[] s = this.getHeader(name);
      if (s == null) {
         return null;
      } else if (s.length != 1 && delimiter != null) {
         StringBuffer r = new StringBuffer(s[0]);

         for(int i = 1; i < s.length; ++i) {
            r.append(delimiter);
            r.append(s[i]);
         }

         return r.toString();
      } else {
         return s[0];
      }
   }

   public void setHeader(String name, String value) {
      boolean found = false;

      for(int i = 0; i < this.headers.size(); ++i) {
         hdr h = (hdr)this.headers.get(i);
         if (name.equalsIgnoreCase(h.name)) {
            if (found) {
               this.headers.remove(i);
               --i;
            } else {
               int j;
               if (h.line != null && (j = h.line.indexOf(58)) >= 0) {
                  h.line = h.line.substring(0, j + 1) + " " + value;
               } else {
                  h.line = name + ": " + value;
               }

               found = true;
            }
         }
      }

      if (!found) {
         this.addHeader(name, value);
      }

   }

   public void addHeader(String name, String value) {
      int pos = this.headers.size();

      for(int i = this.headers.size() - 1; i >= 0; --i) {
         hdr h = (hdr)this.headers.get(i);
         if (name.equalsIgnoreCase(h.name)) {
            this.headers.add(i + 1, new hdr(name, value));
            return;
         }

         if (h.name.equals(":")) {
            pos = i;
         }
      }

      this.headers.add(pos, new hdr(name, value));
   }

   public void removeHeader(String name) {
      for(int i = 0; i < this.headers.size(); ++i) {
         hdr h = (hdr)this.headers.get(i);
         if (name.equalsIgnoreCase(h.name)) {
            this.headers.remove(i);
            --i;
         }
      }

   }

   public FinalArrayList getAllHeaders() {
      return this.headers;
   }

   public void addHeaderLine(String line) {
      try {
         char c = line.charAt(0);
         if (c != ' ' && c != '\t') {
            this.headers.add(new hdr(line));
         } else {
            hdr h = (hdr)this.headers.get(this.headers.size() - 1);
            h.line = h.line + "\r\n" + line;
         }
      } catch (StringIndexOutOfBoundsException var4) {
         return;
      } catch (NoSuchElementException var5) {
      }

   }

   public List getAllHeaderLines() {
      if (this.headerValueView == null) {
         this.headerValueView = new AbstractList() {
            public Object get(int index) {
               return ((hdr)InternetHeaders.this.headers.get(index)).line;
            }

            public int size() {
               return InternetHeaders.this.headers.size();
            }
         };
      }

      return this.headerValueView;
   }
}
