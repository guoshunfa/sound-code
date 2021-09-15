package com.sun.org.apache.xml.internal.resolver.readers;

import com.sun.org.apache.xml.internal.resolver.Catalog;
import com.sun.org.apache.xml.internal.resolver.CatalogEntry;
import com.sun.org.apache.xml.internal.resolver.CatalogException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Stack;
import java.util.Vector;

public class TextCatalogReader implements CatalogReader {
   protected InputStream catfile = null;
   protected int[] stack = new int[3];
   protected Stack tokenStack = new Stack();
   protected int top = -1;
   protected boolean caseSensitive = false;

   public void setCaseSensitive(boolean isCaseSensitive) {
      this.caseSensitive = isCaseSensitive;
   }

   public boolean getCaseSensitive() {
      return this.caseSensitive;
   }

   public void readCatalog(Catalog catalog, String fileUrl) throws MalformedURLException, IOException {
      URL catURL = null;

      try {
         catURL = new URL(fileUrl);
      } catch (MalformedURLException var7) {
         catURL = new URL("file:///" + fileUrl);
      }

      URLConnection urlCon = catURL.openConnection();

      try {
         this.readCatalog(catalog, urlCon.getInputStream());
      } catch (FileNotFoundException var6) {
         catalog.getCatalogManager().debug.message(1, "Failed to load catalog, file not found", catURL.toString());
      }

   }

   public void readCatalog(Catalog catalog, InputStream is) throws MalformedURLException, IOException {
      this.catfile = is;
      if (this.catfile != null) {
         Vector unknownEntry = null;

         try {
            while(true) {
               String token = this.nextToken();
               if (token == null) {
                  if (unknownEntry != null) {
                     catalog.unknownEntry(unknownEntry);
                     unknownEntry = null;
                  }

                  this.catfile.close();
                  this.catfile = null;
                  return;
               }

               String entryToken = null;
               if (this.caseSensitive) {
                  entryToken = token;
               } else {
                  entryToken = token.toUpperCase();
               }

               try {
                  int type = CatalogEntry.getEntryType(entryToken);
                  int numArgs = CatalogEntry.getEntryArgCount(type);
                  Vector args = new Vector();
                  if (unknownEntry != null) {
                     catalog.unknownEntry(unknownEntry);
                     unknownEntry = null;
                  }

                  for(int count = 0; count < numArgs; ++count) {
                     args.addElement(this.nextToken());
                  }

                  catalog.addEntry(new CatalogEntry(entryToken, args));
               } catch (CatalogException var10) {
                  if (var10.getExceptionType() == 3) {
                     if (unknownEntry == null) {
                        unknownEntry = new Vector();
                     }

                     unknownEntry.addElement(token);
                  } else if (var10.getExceptionType() == 2) {
                     catalog.getCatalogManager().debug.message(1, "Invalid catalog entry", token);
                     unknownEntry = null;
                  } else if (var10.getExceptionType() == 8) {
                     catalog.getCatalogManager().debug.message(1, var10.getMessage());
                  }
               }
            }
         } catch (CatalogException var11) {
            if (var11.getExceptionType() == 8) {
               catalog.getCatalogManager().debug.message(1, var11.getMessage());
            }

         }
      }
   }

   protected void finalize() {
      if (this.catfile != null) {
         try {
            this.catfile.close();
         } catch (IOException var2) {
         }
      }

      this.catfile = null;
   }

   protected String nextToken() throws IOException, CatalogException {
      String token = "";
      if (!this.tokenStack.empty()) {
         return (String)this.tokenStack.pop();
      } else {
         int nextch;
         do {
            int ch = this.catfile.read();

            while(ch <= 32) {
               ch = this.catfile.read();
               if (ch < 0) {
                  return null;
               }
            }

            nextch = this.catfile.read();
            if (nextch < 0) {
               return null;
            }

            if (ch != 45 || nextch != 45) {
               this.stack[++this.top] = nextch;
               this.stack[++this.top] = ch;
               ch = this.nextChar();
               if (ch != 34 && ch != 39) {
                  while(ch > 32) {
                     nextch = this.nextChar();
                     if (ch == 45 && nextch == 45) {
                        this.stack[++this.top] = ch;
                        this.stack[++this.top] = nextch;
                        return token;
                     }

                     char[] chararr = new char[]{(char)ch};
                     String s = new String(chararr);
                     token = token.concat(s);
                     ch = nextch;
                  }

                  return token;
               } else {
                  String s;
                  for(int quote = ch; (ch = this.nextChar()) != quote; token = token.concat(s)) {
                     char[] chararr = new char[]{(char)ch};
                     s = new String(chararr);
                  }

                  return token;
               }
            }

            ch = 32;

            for(nextch = this.nextChar(); (ch != 45 || nextch != 45) && nextch > 0; nextch = this.nextChar()) {
               ch = nextch;
            }
         } while(nextch >= 0);

         throw new CatalogException(8, "Unterminated comment in catalog file; EOF treated as end-of-comment.");
      }
   }

   protected int nextChar() throws IOException {
      return this.top < 0 ? this.catfile.read() : this.stack[this.top--];
   }
}
