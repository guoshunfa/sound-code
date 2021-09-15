package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.utils.ObjectFactory;
import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.Translet;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.utils.LocaleUtility;
import java.text.Collator;
import java.util.Locale;

public class NodeSortRecordFactory {
   private static int DESCENDING = "descending".length();
   private static int NUMBER = "number".length();
   private final DOM _dom;
   private final String _className;
   private Class _class;
   private SortSettings _sortSettings;
   protected Collator _collator;

   /** @deprecated */
   public NodeSortRecordFactory(DOM dom, String className, Translet translet, String[] order, String[] type) throws TransletException {
      this(dom, className, translet, order, type, (String[])null, (String[])null);
   }

   public NodeSortRecordFactory(DOM dom, String className, Translet translet, String[] order, String[] type, String[] lang, String[] caseOrder) throws TransletException {
      try {
         this._dom = dom;
         this._className = className;
         this._class = translet.getAuxiliaryClass(className);
         if (this._class == null) {
            this._class = ObjectFactory.findProviderClass(className, true);
         }

         int levels = order.length;
         int[] iOrder = new int[levels];
         int[] iType = new int[levels];

         for(int i = 0; i < levels; ++i) {
            if (order[i].length() == DESCENDING) {
               iOrder[i] = 1;
            }

            if (type[i].length() == NUMBER) {
               iType[i] = 1;
            }
         }

         String[] emptyStringArray = null;
         int length;
         if (lang == null || caseOrder == null) {
            length = order.length;
            emptyStringArray = new String[length];

            for(int i = 0; i < length; ++i) {
               emptyStringArray[i] = "";
            }
         }

         if (lang == null) {
            lang = emptyStringArray;
         }

         if (caseOrder == null) {
            caseOrder = emptyStringArray;
         }

         length = lang.length;
         Locale[] locales = new Locale[length];
         Collator[] collators = new Collator[length];

         for(int i = 0; i < length; ++i) {
            locales[i] = LocaleUtility.langToLocale(lang[i]);
            collators[i] = Collator.getInstance(locales[i]);
         }

         this._sortSettings = new SortSettings((AbstractTranslet)translet, iOrder, iType, locales, collators, caseOrder);
      } catch (ClassNotFoundException var16) {
         throw new TransletException(var16);
      }
   }

   public NodeSortRecord makeNodeSortRecord(int node, int last) throws ExceptionInInitializerError, LinkageError, IllegalAccessException, InstantiationException, SecurityException, TransletException {
      NodeSortRecord sortRecord = (NodeSortRecord)this._class.newInstance();
      sortRecord.initialize(node, last, this._dom, this._sortSettings);
      return sortRecord;
   }

   public String getClassName() {
      return this._className;
   }

   private final void setLang(String[] lang) {
   }
}
