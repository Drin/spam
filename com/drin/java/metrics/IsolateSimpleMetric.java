package com.drin.java.metrics;

import com.drin.java.biology.Isolate;
import com.drin.java.metrics.DataMetric;

import com.drin.java.util.Logger;

import java.util.Map;
import java.util.HashMap;

public class IsolateSimpleMetric extends DataMetric<Isolate> {
   protected Map<String, Map<String, Map<String, Float>>> mRegionMap;

   public IsolateSimpleMetric(Map<String, Map<String, Map<String, Float>>> corrMap) {
      super();

      mRegionMap = corrMap;
   }

   @Override
   public void apply(Isolate elem_A, Isolate elem_B) {
      for (String region : mRegionMap.keySet()) {
         Map<String, Map<String, Float>> corrMap = mRegionMap.get(region);

         /*
         System.out.printf("elem_A: '%s' elem_B: '%s'\n", elem_A.getName(), elem_B.getName());
         System.out.printf("elem_A -> elem_B: '%s' elem_B -> elem_A: '%s'\n",
                           corrMap.containsKey(elem_A.getName()),
                           corrMap.containsKey(elem_B.getName()));
         */

         if (corrMap.containsKey(elem_A.getName())) {
            Map<String, Float> tmp_map = corrMap.get(elem_A.getName());

            //System.out.printf("%s\n", elem_A.getName());

            if (tmp_map.containsKey(elem_B.getName())) {
               //System.out.printf("%s\n", elem_B.getName());
               mResult += tmp_map.get(elem_B.getName()).floatValue();
            }
            else {
               setError(-1);
               System.out.printf("no mapping to %s\n", elem_B.getName());
            }
         }

         else if (corrMap.containsKey(elem_B.getName())) {
            Map<String, Float> tmp_map = corrMap.get(elem_B.getName());

            //System.out.printf("else %s\n", elem_B.getName());

            if (tmp_map.containsKey(elem_A.getName())) {
               //System.out.printf("%s\n", elem_A.getName());
               mResult += tmp_map.get(elem_A.getName()).floatValue();
            }
            else {
               setError(-1);
               System.out.printf("no mapping to %s\n", elem_B.getName());
            }
         }

         Logger.error(getError(), String.format("Could not find correlation " +
                                                "value between %s and %s\n",
                                                elem_A.getName(), elem_B.getName()));
      }
   }

   @Override
   public float result() {
      float result = mResult / mRegionMap.size();

      //Logger.debug(String.format("comparison: [%.05f]", result));
      this.reset();
      return result;
   }
}
