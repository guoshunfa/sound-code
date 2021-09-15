package java.lang;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public final class StringBuilder extends AbstractStringBuilder implements Serializable, CharSequence {
   static final long serialVersionUID = 4383685877147921099L;

   public StringBuilder() {
      super(16);
   }

   public StringBuilder(int var1) {
      super(var1);
   }

   public StringBuilder(String var1) {
      super(var1.length() + 16);
      this.append(var1);
   }

   public StringBuilder(CharSequence var1) {
      this(var1.length() + 16);
      this.append(var1);
   }

   public StringBuilder append(Object var1) {
      return this.append(String.valueOf(var1));
   }

   public StringBuilder append(String var1) {
      super.append(var1);
      return this;
   }

   public StringBuilder append(StringBuffer var1) {
      super.append(var1);
      return this;
   }

   public StringBuilder append(CharSequence var1) {
      super.append(var1);
      return this;
   }

   public StringBuilder append(CharSequence var1, int var2, int var3) {
      super.append(var1, var2, var3);
      return this;
   }

   public StringBuilder append(char[] var1) {
      super.append(var1);
      return this;
   }

   public StringBuilder append(char[] var1, int var2, int var3) {
      super.append(var1, var2, var3);
      return this;
   }

   public StringBuilder append(boolean var1) {
      super.append(var1);
      return this;
   }

   public StringBuilder append(char var1) {
      super.append(var1);
      return this;
   }

   public StringBuilder append(int var1) {
      super.append(var1);
      return this;
   }

   public StringBuilder append(long var1) {
      super.append(var1);
      return this;
   }

   public StringBuilder append(float var1) {
      super.append(var1);
      return this;
   }

   public StringBuilder append(double var1) {
      super.append(var1);
      return this;
   }

   public StringBuilder appendCodePoint(int var1) {
      super.appendCodePoint(var1);
      return this;
   }

   public StringBuilder delete(int var1, int var2) {
      super.delete(var1, var2);
      return this;
   }

   public StringBuilder deleteCharAt(int var1) {
      super.deleteCharAt(var1);
      return this;
   }

   public StringBuilder replace(int var1, int var2, String var3) {
      super.replace(var1, var2, var3);
      return this;
   }

   public StringBuilder insert(int var1, char[] var2, int var3, int var4) {
      super.insert(var1, var2, var3, var4);
      return this;
   }

   public StringBuilder insert(int var1, Object var2) {
      super.insert(var1, var2);
      return this;
   }

   public StringBuilder insert(int var1, String var2) {
      super.insert(var1, var2);
      return this;
   }

   public StringBuilder insert(int var1, char[] var2) {
      super.insert(var1, var2);
      return this;
   }

   public StringBuilder insert(int var1, CharSequence var2) {
      super.insert(var1, var2);
      return this;
   }

   public StringBuilder insert(int var1, CharSequence var2, int var3, int var4) {
      super.insert(var1, var2, var3, var4);
      return this;
   }

   public StringBuilder insert(int var1, boolean var2) {
      super.insert(var1, var2);
      return this;
   }

   public StringBuilder insert(int var1, char var2) {
      super.insert(var1, var2);
      return this;
   }

   public StringBuilder insert(int var1, int var2) {
      super.insert(var1, var2);
      return this;
   }

   public StringBuilder insert(int var1, long var2) {
      super.insert(var1, var2);
      return this;
   }

   public StringBuilder insert(int var1, float var2) {
      super.insert(var1, var2);
      return this;
   }

   public StringBuilder insert(int var1, double var2) {
      super.insert(var1, var2);
      return this;
   }

   public int indexOf(String var1) {
      return super.indexOf(var1);
   }

   public int indexOf(String var1, int var2) {
      return super.indexOf(var1, var2);
   }

   public int lastIndexOf(String var1) {
      return super.lastIndexOf(var1);
   }

   public int lastIndexOf(String var1, int var2) {
      return super.lastIndexOf(var1, var2);
   }

   public StringBuilder reverse() {
      super.reverse();
      return this;
   }

   public String toString() {
      return new String(this.value, 0, this.count);
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeInt(this.count);
      var1.writeObject(this.value);
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.count = var1.readInt();
      this.value = (char[])((char[])var1.readObject());
   }
}
