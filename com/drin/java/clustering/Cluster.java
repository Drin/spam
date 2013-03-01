package com.drin.java.clustering;

import com.drin.java.ontology.Labelable;
import com.drin.java.ontology.OntologyLabel;

import com.drin.java.clustering.Clusterable;
import com.drin.java.clustering.dendogram.Dendogram;

import com.drin.java.metrics.DataMetric;

import com.drin.java.util.Logger;

import java.util.Set;
import java.util.HashSet;

public abstract class Cluster implements Labelable {
   private static int CLUST_ID = 1;
   private String mName;

   protected OntologyLabel mLabel;
   protected DataMetric<Cluster> mMetric;
   protected Dendogram mDendogram;
   protected Set<Clusterable<?>> mElements;
   protected double mDiameter, mMean;

   public Cluster(DataMetric<Cluster> metric) { this(CLUST_ID++, metric); }

   public Cluster(int clustId, DataMetric<Cluster> metric) {
      mName = String.format("%d", clustId);
      mMetric = metric;
      
      mElements = new HashSet<Clusterable<?>>();
      mDendogram = null;
      mLabel = new OntologyLabel();

      mDiameter = -2;
      mMean = -2;
   }

   public static void resetClusterIDs() { Cluster.CLUST_ID = 1; }
   public String getName() { return mName; }
   public int size() { return mElements.size(); }
   public double getDiameter() { return mDiameter; }
   public double getMean() { return mMean; }

   public abstract void computeStatistics();
   public abstract Cluster join(Cluster otherClust);

   public Dendogram getDendogram() { return mDendogram; }
   public Set<Clusterable<?>> getElements() { return mElements; }
   public void add(Clusterable<?> element) { mElements.add(element); }

   /*
    * This is for ontological labels. Clusters should have a set of labels that
    * is a superset of the labels of its data points.
    */
   public void addLabel(String label) { mLabel.addLabel(label); }
   public boolean hasLabel(String label) { return mLabel.hasLabel(label); }

   @Override
   public boolean equals(Object otherObj) {
      if (otherObj instanceof Cluster) {
         Cluster otherClust = (Cluster) otherObj;

         for (Clusterable<?> elem : mElements) {
            if (!otherClust.mElements.contains(elem)) { return false; }
         }

         return true;
      }

      return false;
   }

   public double compareTo(Cluster otherClust) {
      mMetric.apply(this, otherClust);
      double comparison = mMetric.result();

      Logger.error(mMetric.getError(), String.format("error computing metric" +
                                       " between '%s' and '%s'\n", this.mName,
                                       otherClust.mName));

      return comparison;
   }

   /**
    * Compares two clusters to determine if they are <b>similar</b>.
    * Similar is defined based on the data points contained in the cluster.
    * If each pair of data points between the clusters are similar, then the 
    * clusters are similar. However, if any two data points are
    * not similar, then the two clusters are not considered similar.
    *
    * @param otherClust The cluster being compared to.
    * @return True if this cluster is similar to the other cluster. False
    * otherwise.
    */
   public boolean isSimilar(Cluster otherClust) {
      for (Clusterable<?> elem_A : mElements) {
         for (Clusterable<?> elem_B : otherClust.mElements) {

            if (!elem_A.isSimilar(elem_B)) { return false; }
         }
      }

      return true;
   }

   /**
    * Compares two clusters to determine if they are <b>different</b>.
    * Different is defined based on the data points contained in the cluster.
    * If each pair of data points between the clusters are different, then the 
    * clusters are considered different. However, if any two data points are
    * not different, then the two clusters are not considered different.
    *
    * @param otherClust The cluster being compared to.
    * @return True if this cluster is different from the other cluster. False
    * otherwise.
    */
   public boolean isDifferent(Cluster otherClust) {
      for (Clusterable<?> elem_A : mElements) {
         for (Clusterable<?> elem_B : otherClust.mElements) {

            if (!elem_A.isDifferent(elem_B)) { return false; }
         }
      }

      return true;
   }

   @Override
   public String toString() {
      String str = this.getName() + ": ";

      for (Clusterable<?> element : mElements) {
         str += String.format("%s, ", element);
      }

      return str;
   }

   public String prettyPrint(String prefix) {
      String str = this.getName() + ":\n";

      for (Clusterable<?> element : mElements) {
         str += String.format("%s\n", element);
      }

      return str;
   }

}
