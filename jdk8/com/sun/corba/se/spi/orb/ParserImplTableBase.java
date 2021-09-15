package com.sun.corba.se.spi.orb;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class ParserImplTableBase extends ParserImplBase {
   private final ParserData[] entries;

   public ParserImplTableBase(ParserData[] var1) {
      this.entries = var1;
      this.setDefaultValues();
   }

   protected PropertyParser makeParser() {
      PropertyParser var1 = new PropertyParser();

      for(int var2 = 0; var2 < this.entries.length; ++var2) {
         ParserData var3 = this.entries[var2];
         var3.addToParser(var1);
      }

      return var1;
   }

   protected void setDefaultValues() {
      ParserImplTableBase.FieldMap var1 = new ParserImplTableBase.FieldMap(this.entries, true);
      this.setFields(var1);
   }

   public void setTestValues() {
      ParserImplTableBase.FieldMap var1 = new ParserImplTableBase.FieldMap(this.entries, false);
      this.setFields(var1);
   }

   private static class FieldMap extends AbstractMap {
      private final ParserData[] entries;
      private final boolean useDefault;

      public FieldMap(ParserData[] var1, boolean var2) {
         this.entries = var1;
         this.useDefault = var2;
      }

      public Set entrySet() {
         return new AbstractSet() {
            public Iterator iterator() {
               return new Iterator() {
                  int ctr = 0;

                  public boolean hasNext() {
                     return this.ctr < FieldMap.this.entries.length;
                  }

                  public Object next() {
                     ParserData var1 = FieldMap.this.entries[this.ctr++];
                     ParserImplTableBase.MapEntry var2 = new ParserImplTableBase.MapEntry(var1.getFieldName());
                     if (FieldMap.this.useDefault) {
                        var2.setValue(var1.getDefaultValue());
                     } else {
                        var2.setValue(var1.getTestValue());
                     }

                     return var2;
                  }

                  public void remove() {
                     throw new UnsupportedOperationException();
                  }
               };
            }

            public int size() {
               return FieldMap.this.entries.length;
            }
         };
      }
   }

   private static final class MapEntry implements Map.Entry {
      private Object key;
      private Object value;

      public MapEntry(Object var1) {
         this.key = var1;
      }

      public Object getKey() {
         return this.key;
      }

      public Object getValue() {
         return this.value;
      }

      public Object setValue(Object var1) {
         Object var2 = this.value;
         this.value = var1;
         return var2;
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof ParserImplTableBase.MapEntry)) {
            return false;
         } else {
            ParserImplTableBase.MapEntry var2 = (ParserImplTableBase.MapEntry)var1;
            return this.key.equals(var2.key) && this.value.equals(var2.value);
         }
      }

      public int hashCode() {
         return this.key.hashCode() ^ this.value.hashCode();
      }
   }
}
