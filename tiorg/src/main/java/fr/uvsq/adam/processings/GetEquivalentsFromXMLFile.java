/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uvsq.adam.processings;

import java.io.*;
import java.util.*;
import org.jdom2.*;

/**
 *
 * @author houk
 */
public class GetEquivalentsFromXMLFile {

   
    static Element racine;

    public static ArrayList<String> GetEquivalent( String element, String type, Element racine1) throws IOException {
     racine=racine1;
       
        ArrayList<String> equivalents=new ArrayList<String>();
        if(type.equals("edge")){
       equivalents = getALLEdges(element);
        }else{
          equivalents = getALLNodes(element,type); 
        }


        return equivalents;
    }

    static ArrayList<String> getALLEdges(String element) {
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

            String xmlElement = courant.getChild("element").getText();

            if (xmlElement.equals(element)) {
                List listEquivalent = courant.getChildren("equivalent");
                Iterator j = listEquivalent.iterator();

                while (j.hasNext()) {
                    Element ele = (Element) j.next();
                    String equivalent = ele.getText();
                    equivalents.add(equivalent);
                }
                break;
            }
        }
        return equivalents;

    }

    
      static ArrayList<String> getALLNodes(String element, String type) {
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

            String xmlElement = courant.getChild("element").getText();

            if (xmlElement.equals(element)) {
                List listEquivalent = courant.getChildren("equivalent");
                Iterator j = listEquivalent.iterator();

                while (j.hasNext()) {
                    Element ele = (Element) j.next();
                    String equivalent = ele.getText();
                    equivalents.add(equivalent);
                }
                break;
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

            String xmlType = courant.getChild("element").getText();

            if (xmlType.equals(type)) {
                List listEquivalent = courant.getChildren("equivalent");
                Iterator j = listEquivalent.iterator();

                while (j.hasNext()) {
                    Element ele = (Element) j.next();
                    String equivalent = ele.getText();
                    equivalents.add(equivalent);
                }
                break;
            }
        }
        return equivalents;

    }  

}
