package com.sun.xml.internal.bind.v2.schemagen;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.LocalAttribute;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.LocalElement;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.Schema;
import com.sun.xml.internal.txw2.TypedXmlWriter;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.namespace.QName;

enum Form {
   QUALIFIED(XmlNsForm.QUALIFIED, true) {
      void declare(String attName, Schema schema) {
         schema._attribute(attName, "qualified");
      }
   },
   UNQUALIFIED(XmlNsForm.UNQUALIFIED, false) {
      void declare(String attName, Schema schema) {
         schema._attribute(attName, "unqualified");
      }
   },
   UNSET(XmlNsForm.UNSET, false) {
      void declare(String attName, Schema schema) {
      }
   };

   private final XmlNsForm xnf;
   public final boolean isEffectivelyQualified;

   private Form(XmlNsForm xnf, boolean effectivelyQualified) {
      this.xnf = xnf;
      this.isEffectivelyQualified = effectivelyQualified;
   }

   abstract void declare(String var1, Schema var2);

   public void writeForm(LocalElement e, QName tagName) {
      this._writeForm(e, tagName);
   }

   public void writeForm(LocalAttribute a, QName tagName) {
      this._writeForm(a, tagName);
   }

   private void _writeForm(TypedXmlWriter e, QName tagName) {
      boolean qualified = tagName.getNamespaceURI().length() > 0;
      if (qualified && this != QUALIFIED) {
         e._attribute((String)"form", "qualified");
      } else if (!qualified && this == QUALIFIED) {
         e._attribute((String)"form", "unqualified");
      }

   }

   public static Form get(XmlNsForm xnf) {
      Form[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Form v = var1[var3];
         if (v.xnf == xnf) {
            return v;
         }
      }

      throw new IllegalArgumentException();
   }

   // $FF: synthetic method
   Form(XmlNsForm x2, boolean x3, Object x4) {
      this(x2, x3);
   }
}
