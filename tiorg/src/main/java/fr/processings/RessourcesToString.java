/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.processings;

import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.shared.PrefixMapping;

/**
 *
 * @author houk
 */
public class RessourcesToString {

    private final static String toString(Resource resource) {
        if (resource.isAnon()) {
            return "[]";
        }
        PrefixMapping pmap = resource.getModel();
        String qname = pmap.qnameFor(resource.getLocalName());
        if (qname != null) {
            return qname;
        }
        return "<" + resource.getLocalName() + ">";
    }

    public static String transform(RDFNode input) {



        if (input.isLiteral()) {
            return "DuTexte ";
        } else {
            return RessourcesToString.toString((Resource) input);
        }
    }

    public static String transform(Statement input) {
        return RessourcesToString.toString(input.getPredicate());
    }
}
