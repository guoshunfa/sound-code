package com.sun.xml.internal.messaging.saaj.packaging.mime.internet;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class ParameterList {
   private final HashMap list;

   public ParameterList() {
      this.list = new HashMap();
   }

   private ParameterList(HashMap m) {
      this.list = m;
   }

   public ParameterList(String s) throws ParseException {
      HeaderTokenizer h = new HeaderTokenizer(s, "()<>@,;:\\\"\t []/?=");
      this.list = new HashMap();

      while(true) {
         HeaderTokenizer.Token tk = h.next();
         int type = tk.getType();
         if (type == -4) {
            return;
         }

         if ((char)type != ';') {
            throw new ParseException();
         }

         tk = h.next();
         if (tk.getType() == -4) {
            return;
         }

         if (tk.getType() != -1) {
            throw new ParseException();
         }

         String name = tk.getValue().toLowerCase();
         tk = h.next();
         if ((char)tk.getType() != '=') {
            throw new ParseException();
         }

         tk = h.next();
         type = tk.getType();
         if (type != -1 && type != -2) {
            throw new ParseException();
         }

         this.list.put(name, tk.getValue());
      }
   }

   public int size() {
      return this.list.size();
   }

   public String get(String name) {
      return (String)this.list.get(name.trim().toLowerCase());
   }

   public void set(String name, String value) {
      this.list.put(name.trim().toLowerCase(), value);
   }

   public void remove(String name) {
      this.list.remove(name.trim().toLowerCase());
   }

   public Iterator getNames() {
      return this.list.keySet().iterator();
   }

   public String toString() {
      return this.toString(0);
   }

   public String toString(int used) {
      StringBuffer sb = new StringBuffer();
      Iterator itr = this.list.entrySet().iterator();

      while(itr.hasNext()) {
         Map.Entry e = (Map.Entry)itr.next();
         String name = (String)e.getKey();
         String value = this.quote((String)e.getValue());
         sb.append("; ");
         used += 2;
         int len = name.length() + value.length() + 1;
         if (used + len > 76) {
            sb.append("\r\n\t");
            used = 8;
         }

         sb.append(name).append('=');
         used += name.length() + 1;
         if (used + value.length() > 76) {
            String s = MimeUtility.fold(used, value);
            sb.append(s);
            int lastlf = s.lastIndexOf(10);
            if (lastlf >= 0) {
               used += s.length() - lastlf - 1;
            } else {
               used += s.length();
            }
         } else {
            sb.append(value);
            used += value.length();
         }
      }

      return sb.toString();
   }

   private String quote(String value) {
      return "".equals(value) ? "\"\"" : MimeUtility.quote(value, "()<>@,;:\\\"\t []/?=");
   }

   public ParameterList copy() {
      return new ParameterList((HashMap)this.list.clone());
   }
}
