package javax.swing.text.rtf;

import java.util.Dictionary;
import java.util.Enumeration;
import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;

class MockAttributeSet implements AttributeSet, MutableAttributeSet {
   public Dictionary<Object, Object> backing;

   public boolean isEmpty() {
      return this.backing.isEmpty();
   }

   public int getAttributeCount() {
      return this.backing.size();
   }

   public boolean isDefined(Object var1) {
      return this.backing.get(var1) != null;
   }

   public boolean isEqual(AttributeSet var1) {
      throw new InternalError("MockAttributeSet: charade revealed!");
   }

   public AttributeSet copyAttributes() {
      throw new InternalError("MockAttributeSet: charade revealed!");
   }

   public Object getAttribute(Object var1) {
      return this.backing.get(var1);
   }

   public void addAttribute(Object var1, Object var2) {
      this.backing.put(var1, var2);
   }

   public void addAttributes(AttributeSet var1) {
      Enumeration var2 = var1.getAttributeNames();

      while(var2.hasMoreElements()) {
         Object var3 = var2.nextElement();
         this.backing.put(var3, var1.getAttribute(var3));
      }

   }

   public void removeAttribute(Object var1) {
      this.backing.remove(var1);
   }

   public void removeAttributes(AttributeSet var1) {
      throw new InternalError("MockAttributeSet: charade revealed!");
   }

   public void removeAttributes(Enumeration<?> var1) {
      throw new InternalError("MockAttributeSet: charade revealed!");
   }

   public void setResolveParent(AttributeSet var1) {
      throw new InternalError("MockAttributeSet: charade revealed!");
   }

   public Enumeration getAttributeNames() {
      return this.backing.keys();
   }

   public boolean containsAttribute(Object var1, Object var2) {
      throw new InternalError("MockAttributeSet: charade revealed!");
   }

   public boolean containsAttributes(AttributeSet var1) {
      throw new InternalError("MockAttributeSet: charade revealed!");
   }

   public AttributeSet getResolveParent() {
      throw new InternalError("MockAttributeSet: charade revealed!");
   }
}
