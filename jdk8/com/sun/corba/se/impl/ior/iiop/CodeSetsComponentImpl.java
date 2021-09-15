package com.sun.corba.se.impl.ior.iiop;

import com.sun.corba.se.impl.encoding.CodeSetComponentInfo;
import com.sun.corba.se.impl.encoding.MarshalInputStream;
import com.sun.corba.se.impl.encoding.MarshalOutputStream;
import com.sun.corba.se.spi.ior.TaggedComponentBase;
import com.sun.corba.se.spi.ior.iiop.CodeSetsComponent;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public class CodeSetsComponentImpl extends TaggedComponentBase implements CodeSetsComponent {
   CodeSetComponentInfo csci;

   public boolean equals(Object var1) {
      if (!(var1 instanceof CodeSetsComponentImpl)) {
         return false;
      } else {
         CodeSetsComponentImpl var2 = (CodeSetsComponentImpl)var1;
         return this.csci.equals(var2.csci);
      }
   }

   public int hashCode() {
      return this.csci.hashCode();
   }

   public String toString() {
      return "CodeSetsComponentImpl[csci=" + this.csci + "]";
   }

   public CodeSetsComponentImpl() {
      this.csci = new CodeSetComponentInfo();
   }

   public CodeSetsComponentImpl(InputStream var1) {
      this.csci = new CodeSetComponentInfo();
      this.csci.read((MarshalInputStream)var1);
   }

   public CodeSetsComponentImpl(ORB var1) {
      if (var1 == null) {
         this.csci = new CodeSetComponentInfo();
      } else {
         this.csci = var1.getORBData().getCodeSetComponentInfo();
      }

   }

   public CodeSetComponentInfo getCodeSetComponentInfo() {
      return this.csci;
   }

   public void writeContents(OutputStream var1) {
      this.csci.write((MarshalOutputStream)var1);
   }

   public int getId() {
      return 1;
   }
}
