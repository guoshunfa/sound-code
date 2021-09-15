package java.lang;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.util.Arrays;

public final class StringBuffer extends AbstractStringBuilder implements Serializable, CharSequence {
   private transient char[] toStringCache;
   static final long serialVersionUID = 3388685877147921107L;
   private static final ObjectStreamField[] serialPersistentFields;

   public StringBuffer() {
      super(16);
   }

   public StringBuffer(int var1) {
      super(var1);
   }

   public StringBuffer(String var1) {
      super(var1.length() + 16);
      this.append(var1);
   }

   public StringBuffer(CharSequence var1) {
      this(var1.length() + 16);
      this.append(var1);
   }

   public synchronized int length() {
      return this.count;
   }

   public synchronized int capacity() {
      return this.value.length;
   }

   public synchronized void ensureCapacity(int var1) {
      super.ensureCapacity(var1);
   }

   public synchronized void trimToSize() {
      super.trimToSize();
   }

   public synchronized void setLength(int var1) {
      this.toStringCache = null;
      super.setLength(var1);
   }

   public synchronized char charAt(int var1) {
      if (var1 >= 0 && var1 < this.count) {
         return this.value[var1];
      } else {
         throw new StringIndexOutOfBoundsException(var1);
      }
   }

   public synchronized int codePointAt(int var1) {
      return super.codePointAt(var1);
   }

   public synchronized int codePointBefore(int var1) {
      return super.codePointBefore(var1);
   }

   public synchronized int codePointCount(int var1, int var2) {
      return super.codePointCount(var1, var2);
   }

   public synchronized int offsetByCodePoints(int var1, int var2) {
      return super.offsetByCodePoints(var1, var2);
   }

   public synchronized void getChars(int var1, int var2, char[] var3, int var4) {
      super.getChars(var1, var2, var3, var4);
   }

   public synchronized void setCharAt(int var1, char var2) {
      if (var1 >= 0 && var1 < this.count) {
         this.toStringCache = null;
         this.value[var1] = var2;
      } else {
         throw new StringIndexOutOfBoundsException(var1);
      }
   }

   public synchronized StringBuffer append(Object var1) {
      this.toStringCache = null;
      super.append(String.valueOf(var1));
      return this;
   }

   public synchronized StringBuffer append(String var1) {
      this.toStringCache = null;
      super.append(var1);
      return this;
   }

   public synchronized StringBuffer append(StringBuffer var1) {
      this.toStringCache = null;
      super.append(var1);
      return this;
   }

   synchronized StringBuffer append(AbstractStringBuilder var1) {
      this.toStringCache = null;
      super.append(var1);
      return this;
   }

   public synchronized StringBuffer append(CharSequence var1) {
      this.toStringCache = null;
      super.append(var1);
      return this;
   }

   public synchronized StringBuffer append(CharSequence var1, int var2, int var3) {
      this.toStringCache = null;
      super.append(var1, var2, var3);
      return this;
   }

   public synchronized StringBuffer append(char[] var1) {
      this.toStringCache = null;
      super.append(var1);
      return this;
   }

   public synchronized StringBuffer append(char[] var1, int var2, int var3) {
      this.toStringCache = null;
      super.append(var1, var2, var3);
      return this;
   }

   public synchronized StringBuffer append(boolean var1) {
      this.toStringCache = null;
      super.append(var1);
      return this;
   }

   public synchronized StringBuffer append(char var1) {
      this.toStringCache = null;
      super.append(var1);
      return this;
   }

   public synchronized StringBuffer append(int var1) {
      this.toStringCache = null;
      super.append(var1);
      return this;
   }

   public synchronized StringBuffer appendCodePoint(int var1) {
      this.toStringCache = null;
      super.appendCodePoint(var1);
      return this;
   }

   public synchronized StringBuffer append(long var1) {
      this.toStringCache = null;
      super.append(var1);
      return this;
   }

   public synchronized StringBuffer append(float var1) {
      this.toStringCache = null;
      super.append(var1);
      return this;
   }

   public synchronized StringBuffer append(double var1) {
      this.toStringCache = null;
      super.append(var1);
      return this;
   }

   public synchronized StringBuffer delete(int var1, int var2) {
      this.toStringCache = null;
      super.delete(var1, var2);
      return this;
   }

   public synchronized StringBuffer deleteCharAt(int var1) {
      this.toStringCache = null;
      super.deleteCharAt(var1);
      return this;
   }

   public synchronized StringBuffer replace(int var1, int var2, String var3) {
      this.toStringCache = null;
      super.replace(var1, var2, var3);
      return this;
   }

   public synchronized String substring(int var1) {
      return this.substring(var1, this.count);
   }

   public synchronized CharSequence subSequence(int var1, int var2) {
      return super.substring(var1, var2);
   }

   public synchronized String substring(int var1, int var2) {
      return super.substring(var1, var2);
   }

   public synchronized StringBuffer insert(int var1, char[] var2, int var3, int var4) {
      this.toStringCache = null;
      super.insert(var1, var2, var3, var4);
      return this;
   }

   public synchronized StringBuffer insert(int var1, Object var2) {
      this.toStringCache = null;
      super.insert(var1, String.valueOf(var2));
      return this;
   }

   public synchronized StringBuffer insert(int var1, String var2) {
      this.toStringCache = null;
      super.insert(var1, var2);
      return this;
   }

   public synchronized StringBuffer insert(int var1, char[] var2) {
      this.toStringCache = null;
      super.insert(var1, var2);
      return this;
   }

   public StringBuffer insert(int var1, CharSequence var2) {
      super.insert(var1, var2);
      return this;
   }

   public synchronized StringBuffer insert(int var1, CharSequence var2, int var3, int var4) {
      this.toStringCache = null;
      super.insert(var1, var2, var3, var4);
      return this;
   }

   public StringBuffer insert(int var1, boolean var2) {
      super.insert(var1, var2);
      return this;
   }

   public synchronized StringBuffer insert(int var1, char var2) {
      this.toStringCache = null;
      super.insert(var1, var2);
      return this;
   }

   public StringBuffer insert(int var1, int var2) {
      super.insert(var1, var2);
      return this;
   }

   public StringBuffer insert(int var1, long var2) {
      super.insert(var1, var2);
      return this;
   }

   public StringBuffer insert(int var1, float var2) {
      super.insert(var1, var2);
      return this;
   }

   public StringBuffer insert(int var1, double var2) {
      super.insert(var1, var2);
      return this;
   }

   public int indexOf(String var1) {
      return super.indexOf(var1);
   }

   public synchronized int indexOf(String var1, int var2) {
      return super.indexOf(var1, var2);
   }

   public int lastIndexOf(String var1) {
      return this.lastIndexOf(var1, this.count);
   }

   public synchronized int lastIndexOf(String var1, int var2) {
      return super.lastIndexOf(var1, var2);
   }

   public synchronized StringBuffer reverse() {
      this.toStringCache = null;
      super.reverse();
      return this;
   }

   public synchronized String toString() {
      if (this.toStringCache == null) {
         this.toStringCache = Arrays.copyOfRange((char[])this.value, 0, this.count);
      }

      return new String(this.toStringCache, true);
   }

   private synchronized void writeObject(ObjectOutputStream var1) throws IOException {
      ObjectOutputStream.PutField var2 = var1.putFields();
      var2.put("value", this.value);
      var2.put("count", this.count);
      var2.put("shared", false);
      var1.writeFields();
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      ObjectInputStream.GetField var2 = var1.readFields();
      this.value = (char[])((char[])var2.get("value", (Object)null));
      this.count = var2.get("count", (int)0);
   }

   static {
      serialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("value", char[].class), new ObjectStreamField("count", Integer.TYPE), new ObjectStreamField("shared", Boolean.TYPE)};
   }
}
