package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.annotation.AnnotationReader;
import com.sun.xml.internal.bind.v2.model.annotation.AnnotationSource;
import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import com.sun.xml.internal.bind.v2.runtime.IllegalAnnotationException;
import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSchemaTypes;
import javax.xml.namespace.QName;

final class Util {
   static <T, C, F, M> QName calcSchemaType(AnnotationReader<T, C, F, M> reader, AnnotationSource primarySource, C enclosingClass, T individualType, Locatable src) {
      XmlSchemaType xst = (XmlSchemaType)primarySource.readAnnotation(XmlSchemaType.class);
      if (xst != null) {
         return new QName(xst.namespace(), xst.name());
      } else {
         XmlSchemaTypes xsts = (XmlSchemaTypes)reader.getPackageAnnotation(XmlSchemaTypes.class, enclosingClass, src);
         XmlSchemaType[] values = null;
         if (xsts != null) {
            values = xsts.value();
         } else {
            xst = (XmlSchemaType)reader.getPackageAnnotation(XmlSchemaType.class, enclosingClass, src);
            if (xst != null) {
               values = new XmlSchemaType[]{xst};
            }
         }

         if (values != null) {
            XmlSchemaType[] var8 = values;
            int var9 = values.length;

            for(int var10 = 0; var10 < var9; ++var10) {
               XmlSchemaType item = var8[var10];
               if (reader.getClassValue(item, "type").equals(individualType)) {
                  return new QName(item.namespace(), item.name());
               }
            }
         }

         return null;
      }
   }

   static MimeType calcExpectedMediaType(AnnotationSource primarySource, ModelBuilder builder) {
      XmlMimeType xmt = (XmlMimeType)primarySource.readAnnotation(XmlMimeType.class);
      if (xmt == null) {
         return null;
      } else {
         try {
            return new MimeType(xmt.value());
         } catch (MimeTypeParseException var4) {
            builder.reportError(new IllegalAnnotationException(Messages.ILLEGAL_MIME_TYPE.format(xmt.value(), var4.getMessage()), xmt));
            return null;
         }
      }
   }
}
