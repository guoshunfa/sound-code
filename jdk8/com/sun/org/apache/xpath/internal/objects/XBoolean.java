package com.sun.org.apache.xpath.internal.objects;

import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;
import javax.xml.transform.TransformerException;

public class XBoolean extends XObject {
   static final long serialVersionUID = -2964933058866100881L;
   public static final XBoolean S_TRUE = new XBooleanStatic(true);
   public static final XBoolean S_FALSE = new XBooleanStatic(false);
   private final boolean m_val;

   public XBoolean(boolean b) {
      this.m_val = b;
   }

   public XBoolean(Boolean b) {
      this.m_val = b;
      this.setObject(b);
   }

   public int getType() {
      return 1;
   }

   public String getTypeString() {
      return "#BOOLEAN";
   }

   public double num() {
      return this.m_val ? 1.0D : 0.0D;
   }

   public boolean bool() {
      return this.m_val;
   }

   public String str() {
      return this.m_val ? "true" : "false";
   }

   public Object object() {
      if (null == this.m_obj) {
         this.setObject(new Boolean(this.m_val));
      }

      return this.m_obj;
   }

   public boolean equals(XObject obj2) {
      if (obj2.getType() == 4) {
         return obj2.equals(this);
      } else {
         try {
            return this.m_val == obj2.bool();
         } catch (TransformerException var3) {
            throw new WrappedRuntimeException(var3);
         }
      }
   }
}
