package javax.xml.soap;

import java.util.Iterator;
import java.util.Vector;

public class MimeHeaders {
   private Vector headers = new Vector();

   public String[] getHeader(String name) {
      Vector values = new Vector();

      for(int i = 0; i < this.headers.size(); ++i) {
         MimeHeader hdr = (MimeHeader)this.headers.elementAt(i);
         if (hdr.getName().equalsIgnoreCase(name) && hdr.getValue() != null) {
            values.addElement(hdr.getValue());
         }
      }

      if (values.size() == 0) {
         return null;
      } else {
         String[] r = new String[values.size()];
         values.copyInto(r);
         return r;
      }
   }

   public void setHeader(String name, String value) {
      boolean found = false;
      if (name != null && !name.equals("")) {
         for(int i = 0; i < this.headers.size(); ++i) {
            MimeHeader hdr = (MimeHeader)this.headers.elementAt(i);
            if (hdr.getName().equalsIgnoreCase(name)) {
               if (!found) {
                  this.headers.setElementAt(new MimeHeader(hdr.getName(), value), i);
                  found = true;
               } else {
                  this.headers.removeElementAt(i--);
               }
            }
         }

         if (!found) {
            this.addHeader(name, value);
         }

      } else {
         throw new IllegalArgumentException("Illegal MimeHeader name");
      }
   }

   public void addHeader(String name, String value) {
      if (name != null && !name.equals("")) {
         int pos = this.headers.size();

         for(int i = pos - 1; i >= 0; --i) {
            MimeHeader hdr = (MimeHeader)this.headers.elementAt(i);
            if (hdr.getName().equalsIgnoreCase(name)) {
               this.headers.insertElementAt(new MimeHeader(name, value), i + 1);
               return;
            }
         }

         this.headers.addElement(new MimeHeader(name, value));
      } else {
         throw new IllegalArgumentException("Illegal MimeHeader name");
      }
   }

   public void removeHeader(String name) {
      for(int i = 0; i < this.headers.size(); ++i) {
         MimeHeader hdr = (MimeHeader)this.headers.elementAt(i);
         if (hdr.getName().equalsIgnoreCase(name)) {
            this.headers.removeElementAt(i--);
         }
      }

   }

   public void removeAllHeaders() {
      this.headers.removeAllElements();
   }

   public Iterator getAllHeaders() {
      return this.headers.iterator();
   }

   public Iterator getMatchingHeaders(String[] names) {
      return new MimeHeaders.MatchingIterator(names, true);
   }

   public Iterator getNonMatchingHeaders(String[] names) {
      return new MimeHeaders.MatchingIterator(names, false);
   }

   class MatchingIterator implements Iterator {
      private boolean match;
      private Iterator iterator;
      private String[] names;
      private Object nextHeader;

      MatchingIterator(String[] names, boolean match) {
         this.match = match;
         this.names = names;
         this.iterator = MimeHeaders.this.headers.iterator();
      }

      private Object nextMatch() {
         label36:
         while(true) {
            if (this.iterator.hasNext()) {
               MimeHeader hdr = (MimeHeader)this.iterator.next();
               if (this.names == null) {
                  return this.match ? null : hdr;
               }

               for(int i = 0; i < this.names.length; ++i) {
                  if (hdr.getName().equalsIgnoreCase(this.names[i])) {
                     if (!this.match) {
                        continue label36;
                     }

                     return hdr;
                  }
               }

               if (this.match) {
                  continue;
               }

               return hdr;
            }

            return null;
         }
      }

      public boolean hasNext() {
         if (this.nextHeader == null) {
            this.nextHeader = this.nextMatch();
         }

         return this.nextHeader != null;
      }

      public Object next() {
         if (this.nextHeader != null) {
            Object ret = this.nextHeader;
            this.nextHeader = null;
            return ret;
         } else {
            return this.hasNext() ? this.nextHeader : null;
         }
      }

      public void remove() {
         this.iterator.remove();
      }
   }
}
