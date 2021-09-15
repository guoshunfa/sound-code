package com.sun.org.apache.xerces.internal.impl.xs.models;

import com.sun.org.apache.xerces.internal.impl.xs.SubstitutionGroupHandler;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaException;
import com.sun.org.apache.xerces.internal.xni.QName;
import java.util.ArrayList;
import java.util.Vector;

public interface XSCMValidator {
   short FIRST_ERROR = -1;
   short SUBSEQUENT_ERROR = -2;

   int[] startContentModel();

   Object oneTransition(QName var1, int[] var2, SubstitutionGroupHandler var3);

   boolean endContentModel(int[] var1);

   boolean checkUniqueParticleAttribution(SubstitutionGroupHandler var1) throws XMLSchemaException;

   Vector whatCanGoHere(int[] var1);

   ArrayList checkMinMaxBounds();
}
