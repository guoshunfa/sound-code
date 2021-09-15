package com.sun.xml.internal.ws.binding;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;

public class FeatureListUtil {
   @NotNull
   public static WebServiceFeatureList mergeList(WebServiceFeatureList... lists) {
      WebServiceFeatureList result = new WebServiceFeatureList();
      WebServiceFeatureList[] var2 = lists;
      int var3 = lists.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         WebServiceFeatureList list = var2[var4];
         result.addAll(list);
      }

      return result;
   }

   @Nullable
   public static <F extends WebServiceFeature> F mergeFeature(@NotNull Class<F> featureType, @Nullable WebServiceFeatureList list1, @Nullable WebServiceFeatureList list2) throws WebServiceException {
      F feature1 = list1 != null ? list1.get(featureType) : null;
      F feature2 = list2 != null ? list2.get(featureType) : null;
      if (feature1 == null) {
         return feature2;
      } else if (feature2 == null) {
         return feature1;
      } else if (feature1.equals(feature2)) {
         return feature1;
      } else {
         throw new WebServiceException(feature1 + ", " + feature2);
      }
   }

   public static boolean isFeatureEnabled(@NotNull Class<? extends WebServiceFeature> featureType, @Nullable WebServiceFeatureList list1, @Nullable WebServiceFeatureList list2) throws WebServiceException {
      WebServiceFeature mergedFeature = mergeFeature(featureType, list1, list2);
      return mergedFeature != null && mergedFeature.isEnabled();
   }
}
