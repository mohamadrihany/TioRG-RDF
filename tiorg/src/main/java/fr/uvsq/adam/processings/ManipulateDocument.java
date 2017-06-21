/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uvsq.adam.processings;

import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import fr.uvsq.adam.tiorg.views.GraphVisualizations.CoordinateXY;
import java.io.*;
import java.util.*;
import org.jdom2.*;
import org.jdom2.input.*;
import org.jdom2.output.*;

/**
 *
 * @author houk
 */
public class ManipulateDocument {

    /**
     *
     * @param elements
     */
    public static void Creation(ArrayList<Triple> elements, String name) {
        Element racine = new Element("Links");
        Document document = new Document(racine);
        for (Triple triple : elements) {

            Element Link = new Element("Link");
            racine.addContent(Link);

            Element Source = new Element("Source");
            Source.setText(triple.Source.asResource().getLocalName());
            Link.addContent(Source);

            Element Dist = new Element("Dist");
            Dist.setText(triple.Dist.asResource().getLocalName());
            Link.addContent(Dist);

            Element Statement = new Element("Statement");
            Statement.setText(triple.Link.getPredicate().getLocalName());
            Link.addContent(Statement);

            Element Importance = new Element("Importance");
            Importance.setText(triple.Importance.toString());
            Link.addContent(Importance);
        }
        enregistre(name, document);
    }

    public static void CreationPaire(ArrayList<Paire> elements, String name) {
        Element racine = new Element("Links");
        Document document = new Document(racine);
        for (Paire paire : elements) {
            Element Link = new Element("Link");
            racine.addContent(Link);

            Element Statement = new Element("Statement");
            Statement.setText(paire.Link);
            Link.addContent(Statement);

            Element Pretraitement = new Element("Pretraitement");
            Pretraitement.setText(paire.Pretraitement);
            Link.addContent(Pretraitement);
        }
        enregistre(name, document);
    }

    public static void Modification(ArrayList<Triple> elements, String name) {
        Element racine = new Element("Links");
        Document document = new Document(racine);
        SAXBuilder sxb = new SAXBuilder();
        try {

            document = sxb.build(new File(name));
        } catch (JDOMException | IOException e) {
        }

        //On initialise un nouvel élément racine avec l'élément racine du document.
        racine = document.getRootElement();
        for (Triple triple : elements) {
            List listLinks = racine.getChildren("Link");

            //On crée un Iterator sur notre liste
            //modifier les elements selectionnes pour attribuer un poids egale à 3
            Iterator i = listLinks.iterator();
            while (i.hasNext()) {
                Element courant = (Element) i.next();
                if (courant.getChild("Source").getText().equals(triple.Source.asResource().getLocalName()) && courant.getChild("Dist").getText().equals(triple.Dist.asResource().getLocalName()) && courant.getChild("Statement").getText().equals(triple.Link.getPredicate().getLocalName())) {
                    courant.getChild("Importance").setText(triple.Importance.toString());
                }
            }
        }
        document.setContent(racine);
        enregistre(name, document);
    }

    public static void ModificationPaire(ArrayList<Paire> elements, String name) {
        Element racine = new Element("Links");
        Document document = new Document(racine);
        SAXBuilder sxb = new SAXBuilder();
        try {

            document = sxb.build(new File(name));
        } catch (JDOMException | IOException e) {
        }

        //On initialise un nouvel élément racine avec l'élément racine du document.
        racine = document.getRootElement();
        for (Paire paire : elements) {
            List listLinks = racine.getChildren("Link");

            //On crée un Iterator sur notre liste
            //modifier les elements selectionnes pour attribuer un poids egale à 3
            Iterator i = listLinks.iterator();
            while (i.hasNext()) {
                Element courant = (Element) i.next();
                if (courant.getChild("Statement").getText().equals(paire.Link)) {
                    courant.getChild("Pretraitement").setText(paire.Pretraitement);
                }
            }
        }
        document.setContent(racine);
        enregistre(name, document);
    }

    private static void enregistre(String nomFechier, Document document) {

        try {

            XMLOutputter sortie = new XMLOutputter(Format.
                    getPrettyFormat());

            sortie.output(document, new FileOutputStream(nomFechier));
        } catch (java.io.IOException e) {
        }
    }

    public static void WriteTextFile(ArrayList<CoordinateXY> coordinates, String name) {
        try {
            FileWriter fw = new FileWriter(name);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.newLine();
            PrintWriter pw = new PrintWriter(bw);
            for (CoordinateXY cor : coordinates) {
                pw.print(cor.getX() + " " + cor.getY() + "\r\n");
            }

            pw.close();
        } catch (IOException e) {
            System.out.println(" Problème à l’écriture du fichier sauvegardant les coordonnées du graphe");
            System.exit(0);
        }
    }

    public static ArrayList<CoordinateXY> ReadTexteFile(String name) {
        //lecture du fichier texte	
        ArrayList<CoordinateXY> coordinates = new ArrayList<>();
        try {

            InputStream ips = new FileInputStream(name);
            InputStreamReader ipsr = new InputStreamReader(ips);
            BufferedReader br = new BufferedReader(ipsr);
            String ligne;
            while ((ligne = br.readLine()) != null) {
                if (!"".equals(ligne)) {
                    String delims = " ";
                    String[] tokens = ligne.split(delims);
                    CoordinateXY cor = new CoordinateXY(Double.parseDouble(tokens[0]), Double.parseDouble(tokens[1]));
                    coordinates.add(cor);
                }
            }
            br.close();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return coordinates;

    }

    public static class Triple {

        RDFNode Source;
        RDFNode Dist;
        Statement Link;
        Boolean Importance;

        /**
         *
         * @param dist
         * @param source
         */
        public Triple(RDFNode source, RDFNode dist, Statement link, Boolean importance) {
            this.Source = source;
            this.Dist = dist;
            this.Link = link;
            this.Importance = importance;

        }
    }

    public static class Paire {

        String Link;
        String Pretraitement;

        /**
         *
         * @param link
         * @param pretraitement
         */
        public Paire(String link, String pretraitement) {
            this.Link = link;
            this.Pretraitement = pretraitement;
        }
    }
}
