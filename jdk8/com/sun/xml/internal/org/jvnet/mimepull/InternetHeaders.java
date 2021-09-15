package com.sun.xml.internal.org.jvnet.mimepull;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

final class InternetHeaders {
   private final FinalArrayList<Hdr> headers = new FinalArrayList();

   InternetHeaders(MIMEParser.LineInputStream lis) {
      String prevline = null;
      StringBuilder lineBuffer = new StringBuilder();

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

      } catch (IOException var6) {
         throw new MIMEParsingException("Error in input stream", var6);
      }
   }

   List<String> getHeader(String name) {
      FinalArrayList<String> v = new FinalArrayList();
      int len = this.headers.size();

      for(int i = 0; i < len; ++i) {
         Hdr h = (Hdr)this.headers.get(i);
         if (name.equalsIgnoreCase(h.name)) {
            v.add(h.getValue());
         }
      }

      return v.size() == 0 ? null : v;
   }

   FinalArrayList<? extends Header> getAllHeaders() {
      return this.headers;
   }

   void addHeaderLine(String line) {
      try {
         char c = line.charAt(0);
         if (c != ' ' && c != '\t') {
            this.headers.add(new Hdr(line));
         } else {
            Hdr h = (Hdr)this.headers.get(this.headers.size() - 1);
            h.line = h.line + "\r\n" + line;
         }
      } catch (StringIndexOutOfBoundsException var4) {
      } catch (NoSuchElementException var5) {
      }

   }
}
