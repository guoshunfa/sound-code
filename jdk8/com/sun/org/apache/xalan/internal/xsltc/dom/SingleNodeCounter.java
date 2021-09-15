package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.Translet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;

public abstract class SingleNodeCounter extends NodeCounter {
   private static final int[] EmptyArray = new int[0];
   DTMAxisIterator _countSiblings = null;

   public SingleNodeCounter(Translet translet, DOM document, DTMAxisIterator iterator) {
      super(translet, document, iterator);
   }

   public SingleNodeCounter(Translet translet, DOM document, DTMAxisIterator iterator, boolean hasFrom) {
      super(translet, document, iterator, hasFrom);
   }

   public NodeCounter setStartNode(int node) {
      this._node = node;
      this._nodeType = this._document.getExpandedTypeID(node);
      this._countSiblings = this._document.getAxisIterator(12);
      return this;
   }

   public String getCounter() {
      int result;
      if (this._value != -2.147483648E9D) {
         if (this._value == 0.0D) {
            return "0";
         } else if (Double.isNaN(this._value)) {
            return "NaN";
         } else if (this._value < 0.0D && Double.isInfinite(this._value)) {
            return "-Infinity";
         } else if (Double.isInfinite(this._value)) {
            return "Infinity";
         } else {
            result = (int)this._value;
            return this.formatNumbers(result);
         }
      } else {
         int next = this._node;
         result = 0;
         boolean matchesCount = this.matchesCount(next);
         if (!matchesCount) {
            while((next = this._document.getParent(next)) > -1 && !this.matchesCount(next)) {
               if (this.matchesFrom(next)) {
                  next = -1;
                  break;
               }
            }
         }

         if (next != -1) {
            int from = next;
            if (!matchesCount && this._hasFrom) {
               while((from = this._document.getParent(from)) > -1 && !this.matchesFrom(from)) {
               }
            }

            if (from != -1) {
               this._countSiblings.setStartNode(next);

               do {
                  if (this.matchesCount(next)) {
                     ++result;
                  }
               } while((next = this._countSiblings.next()) != -1);

               return this.formatNumbers(result);
            }
         }

         return this.formatNumbers(EmptyArray);
      }
   }

   public static NodeCounter getDefaultNodeCounter(Translet translet, DOM document, DTMAxisIterator iterator) {
      return new SingleNodeCounter.DefaultSingleNodeCounter(translet, document, iterator);
   }

   static class DefaultSingleNodeCounter extends SingleNodeCounter {
      public DefaultSingleNodeCounter(Translet translet, DOM document, DTMAxisIterator iterator) {
         super(translet, document, iterator);
      }

      public NodeCounter setStartNode(int node) {
         this._node = node;
         this._nodeType = this._document.getExpandedTypeID(node);
         this._countSiblings = this._document.getTypedAxisIterator(12, this._document.getExpandedTypeID(node));
         return this;
      }

      public String getCounter() {
         int result;
         if (this._value != -2.147483648E9D) {
            if (this._value == 0.0D) {
               return "0";
            }

            if (Double.isNaN(this._value)) {
               return "NaN";
            }

            if (this._value < 0.0D && Double.isInfinite(this._value)) {
               return "-Infinity";
            }

            if (Double.isInfinite(this._value)) {
               return "Infinity";
            }

            result = (int)this._value;
         } else {
            result = 1;
            this._countSiblings.setStartNode(this._node);

            while(this._countSiblings.next() != -1) {
               ++result;
            }
         }

         return this.formatNumbers(result);
      }
   }
}
