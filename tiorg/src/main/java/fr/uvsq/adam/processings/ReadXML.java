/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uvsq.adam.processings;

import java.io.File;
import java.io.IOException;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

/**
 *
 * @author houk
 */
public class ReadXML {
    static Document document;
       private ReadXML() {
    }

    public static Element main(String path) throws Exception {
       //On crée une instance de SAXBuilder
    
        SAXBuilder sxb = new SAXBuilder();
        try {
            //On crée un nouveau document JDOM avec en argument le fichier XML
            //Le parsing est terminé ;)
             document = sxb.build(new File(path));
        } catch (JDOMException | IOException e) {
        }
        //On initialise un nouvel élément racine avec l'élément racine du document.
        Element racine = document.getRootElement();
        return racine;
}
}
