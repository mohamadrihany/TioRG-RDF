package net.rootdev.jenajung;

import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.shared.PrefixMapping;

/**
 * @author pldms
 */
public class Transformers {

    public final static NodeT NODE = new NodeT();
    public final static EdgeT EDGE = new EdgeT();

    private final static String toString(Resource resource) {
        if (resource.isAnon()) return "[]";
        PrefixMapping pmap = resource.getModel();
        String qname = pmap.qnameFor(resource.getURI());
        if (qname != null) return qname;
        return "<" + resource.getURI() + ">";
    }

    public static class NodeT implements Transformer<RDFNode, String> {
        protected Predicate<RDFNode> filters;

        public void setFilters(Predicate<RDFNode> filters) {
            this.filters = filters;
        }

        public String transform(RDFNode input) {
            if (filters != null && !filters.evaluate(input)) return null;
            if (input.isLiteral()) return input.toString();
            Resource resource = (Resource) input;
            if (resource.isURIResource()) return "[" + resource.getLocalName() + "]";
            else if (resource.isResource()) return "@ " + resource.getLocalName();
            return resource.getLocalName();
            //return Transformers.toString((Resource) input);
        }
    }

    public static class EdgeT implements Transformer<Statement, String> {
        protected Predicate<Statement> filters;

        public void setFilters(Predicate<Statement> filters) {
            this.filters = filters;
        }

        public String transform(Statement input) {
            if (filters != null && !filters.evaluate(input)) return null;
            if (input.getPredicate().isAnon()) return "[]";
            PrefixMapping pmap = input.getModel();
            String qname = pmap.qnameFor(input.getPredicate().getLocalName());
            if (qname != null) return qname;
            return "<" + input.getPredicate().getLocalName() + ">";
            //return Transformers.toString(input.getPredicate());
        }

    }
}
