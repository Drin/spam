package com.drin.ontology;

import com.drin.clustering.Cluster;

import com.drin.ontology.OntologyTerm;
import com.drin.ontology.OntologyParser;

import java.io.File;
import java.util.Scanner;

import java.util.List;
import java.util.ArrayList;

import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class Ontology {
   private OntologyTerm mRoot;

   public Ontology() {
      mRoot = null;
   }

   public OntologyTerm getRoot() {
      return mRoot;
   }

   @Override
   public String toString() {
      return Ontology.printOntology(mRoot, "root", "");
   }

   public void addData(Cluster element) {
      boolean dataAdded = mRoot.addData(element);
      if (System.getenv().containsKey("DEBUG") && dataAdded) {
         System.out.printf("added element: '%s' to Ontology \n",
                           element.getName());
      }
   }

   public void addTerm(OntologyTerm newTerm) {
      if (System.getenv().containsKey("DEBUG")) {
         System.out.printf("adding new term:\n\t%s\n", newTerm.toString());
         System.out.printf("term table: %s\tterm column: %s\n",
                           newTerm.getTableName(), newTerm.getColName());
      }

      if (mRoot != null) {
         Ontology.addTerm(mRoot, newTerm);
      }

      else { mRoot = new OntologyTerm(newTerm); }
   }

   private static void addTerm(OntologyTerm root, OntologyTerm newTerm) {
      Map<String, OntologyTerm> partitionMap = root.getPartitions();

      for (Map.Entry<String, OntologyTerm> partition : partitionMap.entrySet()) {
         if (partition.getValue() == null) {
            partition.setValue(new OntologyTerm(newTerm));
         }

         else { Ontology.addTerm(partition.getValue(), newTerm); }
      }
   }

   private static String printOntology(OntologyTerm term, String partitionName, String prefix) {
      String ontologyStr = String.format("%s%s:\n", prefix, partitionName);

      if (term != null) {
         if (term.getData() != null) {
            for (Cluster element : term.getData()) {
               ontologyStr += String.format("%s%s,", prefix + "   ", element.getName());
            }

            ontologyStr += "\n";
         }

         if (term.getPartitions() != null) {
            for (Map.Entry<String, OntologyTerm> feature : term.getPartitions().entrySet()) {
               ontologyStr += Ontology.printOntology(feature.getValue(), feature.getKey(), prefix + "   ");
            }
         }
      }

      return ontologyStr;
   }

   public String printClusters() {
      return Ontology.printClusters(mRoot, "root", "");
   }

   public static String printClusters(OntologyTerm term, String partitionName, String prefix) {
      if (term == null) { return ""; }

      String ontologyStr = String.format("%s%s:\n", prefix, partitionName);

      if (term.getClusters() != null) {
         for (Cluster element : term.getClusters()) {
            ontologyStr += element.prettyPrint(prefix + "   ");
         }

         ontologyStr += "\n";
      }

      if (term.getPartitions() != null) {
         for (Map.Entry<String, OntologyTerm> feature : term.getPartitions().entrySet()) {
            ontologyStr += Ontology.printClusters(feature.getValue(), feature.getKey(), prefix + "   ");
         }
      }

      return ontologyStr;
   }

   public static Ontology constructOntology(String ontologyStr) {
      Ontology ont = new Ontology();
      OntologyParser parser = new OntologyParser();
      Scanner termScanner = new Scanner(ontologyStr).useDelimiter("\n");

      while (termScanner.hasNextLine()) {
         String term = termScanner.nextLine();
   
         if (parser.matchString(term)) { ont.addTerm(parser.getTerm()); }
      }

      return ont;
   }

   public static Ontology createOntology(String fileName) {
      if (fileName == null) { return null; }

      Ontology ont = new Ontology();
      OntologyParser parser = new OntologyParser();
      Scanner termScanner = null;

      try {
         termScanner = new Scanner(new File(fileName)).useDelimiter("\n");
      }
      catch (java.io.FileNotFoundException fileErr) {
         System.out.printf("Could not find file '%s'\n", fileName);
         fileErr.printStackTrace();
      }

      while (termScanner.hasNextLine()) {
         String term = termScanner.nextLine();
   
         if (parser.matchString(term)) { ont.addTerm(parser.getTerm()); }
      }

      return ont;
   }

   /*
    * old test:
    * String testOntology = String.format("%s\n%s\n%s",
    * "Host(): cw, sw;",
    * "Location():R1, R2, MorroBay;",
    * "Day(TimeSensitive):     1,2,3,\t4,5,6,7,10;"
    * );
    */
   public static void main(String[] args) {
      String testOntology = String.format("%s\n%s\n%s\n",
         "Isolates.commonName():;",
         "Isolates.hostID(): ;",
         "Pyroprints.date_pyroPrintedDate(TimeSensitive): \t;"
      );

      Ontology ont = Ontology.constructOntology(testOntology);
      System.out.println("ontology :\n" + ont);
   }
}