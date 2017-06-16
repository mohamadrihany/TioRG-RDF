/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.processings;

import java.io.*;
import java.util.*;
import org.jdom2.*;
import org.jdom2.input.*;

/**
 *
 * @author houk
 */
public class ReadXMLFile {

    static Document document;
    static Element racine;

    public static TripleValorisation _GetLinksWeight(String link) {
        //On crée une instance de SAXBuilder
        SAXBuilder sxb = new SAXBuilder();
        try {
            //On crée un nouveau document JDOM avec en argument le fichier XML
            //Le parsing est terminé ;)
            document = sxb.build(new File(link));
        } catch (JDOMException | IOException e) {
        }

        //On initialise un nouvel élément racine avec l'élément racine du document.
        racine = document.getRootElement();
        //Méthode définie dans la partie 3.2. de cet article
        ArrayList<MyElementWithString> weightsList1 = getALL();
        ArrayList<MyElement> weightsList = new ArrayList<>();
        ArrayList<MyElementWithString> fusionList = new ArrayList<>();
        ArrayList<MyElementWithString> sharedList = new ArrayList<>();
        for (MyElementWithString pair : weightsList1) {
            if ((!"1".equals(pair.weight)) && (!"Fusion".equals(pair.weight)) && (!"Ajout d'arcs".equals(pair.weight)) && (!"Unchanged".equals(pair.weight))) {
                weightsList.add(new MyElement(Integer.parseInt(pair.weight), pair.link));

            } else {
                if ("Fusion".equals(pair.weight)) {
                    fusionList.add(pair);
                }
                if ("Ajout d'arcs".equals(pair.weight)) {
                    sharedList.add(pair);
                }
            }
        }

        TripleValorisation pair = new TripleValorisation(weightsList, fusionList, sharedList);

        return pair;
    }

    public static ArrayList<ElementImportance> readFile(String name) {
        //On crée une instance de SAXBuilder
        SAXBuilder sxb = new SAXBuilder();
        try {
            //On crée un nouveau document JDOM avec en argument le fichier XML
            //Le parsing est terminé ;)
            document = sxb.build(new File(name));
        } catch (JDOMException | IOException e) {
        }

        //On initialise un nouvel élément racine avec l'élément racine du document.
        racine = document.getRootElement();
        //Méthode définie dans la partie 3.2. de cet article
        ArrayList<ElementImportance> weightsList = affiche();
        return weightsList;
    }

    static ArrayList<MyElementWithString> getALL() {
        ArrayList<MyElementWithString> weightsList = new ArrayList();
        List listLinks = racine.getChildren("Link");

        //On crée un Iterator sur notre liste
        Iterator i = listLinks.iterator();
        while (i.hasNext()) {
            //On recrée l'Element courant à chaque tour de boucle afin de
            //pouvoir utiliser les méthodes propres aux Element comme :
            //sélectionner un nœud fils, modifier du texte, etc...
            Element courant = (Element) i.next();
            //On Sauvegarde les noms de liens avec le poids

            String poids = courant.getChild("Pretraitement").getText();
            MyElementWithString element = new MyElementWithString(poids, courant.getChild("Statement").getText());

            weightsList.add(element);
        }
        return weightsList;
    }

    static ArrayList<ElementImportance> affiche() {
        ArrayList<ElementImportance> weightsList = new ArrayList();
        List listLinks = racine.getChildren("Link");

        //On crée un Iterator sur notre liste
        Iterator i = listLinks.iterator();
        while (i.hasNext()) {
            //On recrée l'Element courant à chaque tour de boucle afin de
            //pouvoir utiliser les méthodes propres aux Element comme :
            //sélectionner un nœud fils, modifier du texte, etc...
            Element courant = (Element) i.next();
            //On Sauvegarde les noms de liens avec le poids

            boolean importance = Boolean.parseBoolean(courant.getChild("Importance").getText());
            String statement = courant.getChild("Statement").getText();
            String source = courant.getChild("Source").getText();
            String dist = courant.getChild("Dist").getText();



            //Integer.parseInt(courant.getChild("Weight").getText());
            ElementImportance element = new ElementImportance(importance, statement, source, dist);
            weightsList.add(element);
        }
        return weightsList;
    }

    public static class MyElement {

        public int weight;
        public String link;

        public MyElement(int weight, String link) {
            this.link = link;
            this.weight = weight;
        }
    }

    public static class MyElementWithString {

        public String weight;
        public String link;

        public MyElementWithString(String weight, String link) {
            this.link = link;
            this.weight = weight;
        }
    }

    public static class ElementImportance {

        public boolean importance;
        public String link;
        public String source;
        public String dist;

        public ElementImportance(boolean importance, String link, String source, String dist) {
            this.link = link;
            this.importance = importance;
            this.source = source;
            this.dist = dist;
        }
    }

    public static class TripleValorisation {

        public ArrayList<MyElement> weightsList;
        public ArrayList<MyElementWithString> fusionList;
        public ArrayList<MyElementWithString> sharedList;

        public TripleValorisation(ArrayList<MyElement> WeightsList, ArrayList<MyElementWithString> FusionList, ArrayList<MyElementWithString> SharedList) {
            this.weightsList = WeightsList;
            this.fusionList = FusionList;
            this.sharedList = SharedList;
        }
    }
}
