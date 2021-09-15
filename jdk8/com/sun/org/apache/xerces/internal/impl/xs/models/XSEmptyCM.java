package com.sun.org.apache.xerces.internal.impl.xs.models;

import com.sun.org.apache.xerces.internal.impl.xs.SubstitutionGroupHandler;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaException;
import com.sun.org.apache.xerces.internal.xni.QName;
import java.util.ArrayList;
import java.util.Vector;

public class XSEmptyCM implements XSCMValidator {
   private static final short STATE_START = 0;
   private static final Vector EMPTY = new Vector(0);

   public int[] startContentModel() {
      return new int[]{0};
   }

   public Object oneTransition(QName elementName, int[] currentState, SubstitutionGroupHandler subGroupHandler) {
      if (currentState[0] < 0) {
         currentState[0] = -2;
         return null;
      } else {
         currentState[0] = -1;
         return null;
      }
   }

   public boolean endContentModel(int[] currentState) {
      boolean isFinal = false;
      int state = currentState[0];
      return state >= 0;
   }

   public boolean checkUniqueParticleAttribution(SubstitutionGroupHandler subGroupHandler) throws XMLSchemaException {
      return false;
   }

   public Vector whatCanGoHere(int[] state) {
      return EMPTY;
   }

   public ArrayList checkMinMaxBounds() {
      return null;
   }
}
