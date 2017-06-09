/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.processings;

import java.io.*;
import java.util.*;
import org.jdom2.*;

/**
 *
 * @author houk
 */
public class GetAllEquivalenceFromXMLFile {

   
    static Element racine;

    public static ArrayList<String> GetEquivalent(  String type, Element racine1) throws IOException {
     racine=racine1;
       
        ArrayList<String> equivalents=new ArrayList<String>();
        if(type.equals("edge")){
       equivalents = getALLEdges();
        }else{
          equivalents = getALLNodes(); 
        }


        return equivalents;
    }

    static ArrayList<String> getALLEdges() {
        Element edges = racine.getChild("Edges");
        List listLinks = edges.getChildren("Pattern");
        ArrayList<String> equivalents = new ArrayList<String>();
        //On crée un Iterator sur notre liste
        Iterator i = listLinks.iterator();
        while (i.hasNext()) {
            //On recrée l'Element courant à chaque tour de boucle afin de
            //pouvoir utiliser les méthodes propres aux Element comme :
            //sélectionner un nœud fils, modifier du texte, etc...
            Element courant = (Element) i.next();
            //On Sauvegarde les noms de liens avec le poids

            
                List listEquivalent = courant.getChildren("equivalent");
                Iterator j = listEquivalent.iterator();

                while (j.hasNext()) {
                    Element ele = (Element) j.next();
                    String equivalent = ele.getText();
                    equivalents.add(equivalent);
                }
          
        }
        return equivalents;

    }

    
      static ArrayList<String> getALLNodes( ) {
        Element nodes = racine.getChild("Nodes");
         Element personalized = nodes.getChild("personalized");
       
        List listLinks = personalized.getChildren("Pattern");
        ArrayList<String> equivalents = new ArrayList<String>();
        //On crée un Iterator sur notre liste
        Iterator i = listLinks.iterator();
        while (i.hasNext()) {
            //On recrée l'Element courant à chaque tour de boucle afin de
            //pouvoir utiliser les méthodes propres aux Element comme :
            //sélectionner un nœud fils, modifier du texte, etc...
            Element courant = (Element) i.next();
            //On Sauvegarde les noms de liens avec le poids

            
                List listEquivalent = courant.getChildren("equivalent");
                Iterator j = listEquivalent.iterator();

                while (j.hasNext()) {
                    Element ele = (Element) j.next();
                    String equivalent = ele.getText();
                    equivalents.add(equivalent);
                
            }
        }
          Element systematic = nodes.getChild("systematic");
          List listTypes = systematic.getChildren("Pattern");
       // ArrayList<String> systEquivalents = new ArrayList<String>();
        //On crée un Iterator sur notre liste
        Iterator k = listTypes.iterator();
        while (k.hasNext()) {
            //On recrée l'Element courant à chaque tour de boucle afin de
            //pouvoir utiliser les méthodes propres aux Element comme :
            //sélectionner un nœud fils, modifier du texte, etc...
            Element courant = (Element) k.next();
            //On Sauvegarde les noms de liens avec le poids

         
                List listEquivalent = courant.getChildren("equivalent");
                Iterator j = listEquivalent.iterator();

                while (j.hasNext()) {
                    Element ele = (Element) j.next();
                    String equivalent = ele.getText();
                    equivalents.add(equivalent);
               
            }
        }
        return equivalents;

    }  

}
