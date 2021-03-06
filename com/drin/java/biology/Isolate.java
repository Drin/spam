package com.drin.java.biology;

import com.drin.java.clustering.Clusterable;
import com.drin.java.biology.ITSRegion;
import com.drin.java.biology.Pyroprint;

import java.util.Iterator;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;


//TODO temporary
import java.io.File;
import java.io.FileWriter;

/**
 * Isolate represents a bacterial isolate collected by a biologist.
 *
 */
public class Isolate extends Clusterable<ITSRegion> {
   private static final int ALPHA_NDX = 0, BETA_NDX = 1;
   private Map<String, Float> mComparisonCache;

   private int mIdNum;

   private String mHost, mSource, mLocation, mDate;

   //TODO temporary
   private static FileWriter debugWriter = null, regionWriter = null;
   
   static {
      try {
         debugWriter = new FileWriter(new File("isolate info.csv"));
         regionWriter = new FileWriter(new File("its Region info.csv"));

         debugWriter.write("Iso Id 1, Iso Id 2, Correlation\n");
         regionWriter.write("Iso Id 1, Iso Id 2, ITS Region, Correlation\n");
      }
      catch (Exception err) {
         err.printStackTrace();
         System.exit(1);
      }
   }

   public Isolate(String isoId) {
      this(isoId, 2);
   }

   public Isolate(String isoId, int dataSize) {
      super(isoId, new HashSet<ITSRegion>(dataSize));

      mIdNum = -1;
      mComparisonCache = new HashMap<String, Float>();
   }

   public int getIdNum() { return mIdNum; }
   public void setIdNum(int idNum) { mIdNum = idNum; }

   public void setHost(String host) { mHost = host; }
   public String getHost() { return mHost; }

   public void setSource(String src) { mSource = src; }
   public String getSource() { return mSource; }

   public void setLoc(String loc) { mLocation = loc; }
   public String getLoc() { return mLocation; }

   public void setDate(String date) { mDate = date; }
   public String getDate() { return mDate; }

   public void setCache(Map<String, Float> cache) { mComparisonCache = cache; }
   public Map<String, Float> getCache() { return mComparisonCache; }

   @Override
   public float compareTo(Clusterable<?> otherObj) {
      Iterator<ITSRegion> itrA, itrB;
      float comparison = 0.0f;
      byte numRegions = 0;

      if (otherObj instanceof Isolate) {
         Isolate otherIso = (Isolate) otherObj;

         if (mComparisonCache.containsKey(otherIso.getName())) {
            Float compVal = mComparisonCache.get(otherIso.getName());
            if (compVal != null) { return compVal.floatValue(); }
         }

         else if (otherIso.mComparisonCache.containsKey(this.getName())) {
            Float compVal = otherIso.mComparisonCache.get(this.getName());
            if (compVal != null) { return compVal.floatValue(); }
         }

         else {
            itrA = mData.iterator();

            while (itrA.hasNext()) {
               ITSRegion regionA = itrA.next();

               itrB = (otherIso).getData().iterator();
               while (itrB.hasNext()) {
                  ITSRegion regionB = itrB.next();

                  if (regionA.equals(regionB)) {
                     float regionComparison = regionA.compareTo(regionB);

                     comparison += regionComparison;
                     numRegions++;

                     try {
                        regionWriter.write(String.format(
                           "%s, %s, %s, %.04f\n",
                           this.getName(), otherObj.getName(), regionA.getName(),
                           regionComparison
                        ));
                     }
                     catch (Exception err) {
                        err.printStackTrace();
                        System.exit(1);
                     }

                     break;
                  }
               }
            }

            if (numRegions == 2) {
               comparison = comparison / numRegions;
            }
            else {
               System.err.println("Invalid # of Regions: " + numRegions);
               System.exit(0);
            }

            mComparisonCache.put(otherIso.getName(), new Float(comparison));

            try {
               debugWriter.write(String.format(
                  "%s, %s, %.04f\n", this.getName(), otherObj.getName(), comparison
               ));
            }
            catch (Exception err) {
               err.printStackTrace();
               System.exit(1);
            }

            return comparison;
         }
      }

      return -2;
   }

   public static void closeWriter() {
      try { debugWriter.close(); regionWriter.close(); }
      catch (Exception err) { err.printStackTrace(); System.exit(1); }
   }

   @Override
   public Isolate deepCopy() {
      Isolate newIsolate = new Isolate(mName);

      newIsolate.setHost(mHost);
      newIsolate.setSource(mSource);
      newIsolate.setLoc(mLocation);
      newIsolate.setDate(mDate);

      for (ITSRegion region : mData) {
         newIsolate.getData().add(region.deepCopy());
      }

      return newIsolate;
   }

   @Override
   public String toString() {
      String str = String.format("isolate '%s' [%d regions]:\n",
                                 this.getName(), mData.size());

      for (ITSRegion region : mData) {
         str += String.format("\tregion '%s' [%d pyroprints]:\n",
                              region.getName(), region.getData().size());

         for (Pyroprint pyro : region.getData()) {
            str += String.format("\t\tpyroprint %s\n\n", pyro);
         }
      }

      return str;
   }
}
