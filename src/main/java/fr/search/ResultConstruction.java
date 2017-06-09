/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.search;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import fr.processings.FileToModelGraph;
import fr.search.SetSetReleventFragments.ModelNode;
import java.util.ArrayList;

/**
 *
 * @author hanane
 */
public class ResultConstruction {

    public ResultConstruction() {
    }

	public static ArrayList<ResultNodeModel> main(ArrayList<ArrayList<ModelNode>> cartesienProduct,
			Model model, UndirectedSparseGraph<RDFNode,Statement> undirectedGraph,
			DirectedGraph<RDFNode,Statement> graph)
	{
		ArrayList<ResultNodeModel> resultList = new ArrayList<>();
		ArrayList<String> resultListString = new ArrayList<>();
		for(ArrayList<ModelNode> modelNodes : cartesienProduct)
		{
			Model result = ModelFactory.createDefaultModel();
			ResultNodeModel resultnodemodel = null;
			if( !modelNodes.isEmpty() )
			{
				if(modelNodes.size() > 1)
				{
					// Fixer un noeud et chercher le plus court chemin avec le reste des noeuds
					ModelNode firstModelNode = modelNodes.get(0);
					RDFNode firstNode = firstModelNode.Node;
					for(int indice = 1; indice < modelNodes.size(); indice++)
					{
						ModelNode secondModelNode = modelNodes.get(indice);
						RDFNode secondNode = secondModelNode.Node;
	
						if(firstNode.isLiteral())
						{
							for(RDFNode node : undirectedGraph.getVertices())
							{
								if(node.isLiteral())
								{
									if(node.asLiteral().getString()
											.equals(firstNode.asLiteral().getString()))
									{
										firstNode = node;
										break;
									}
								}
							}
						}
						if(secondNode.isLiteral())
						{
							for(RDFNode node : undirectedGraph.getVertices())
							{
								if(node.isLiteral())
								{
									if(node.asLiteral().getString()
											.equals(secondNode.asLiteral().getString()))
									{
										secondNode = node;
										break;
									}
								}
							}
						}
						DijkstraShortestPath<RDFNode,Statement> shortestPath = new DijkstraShortestPath<>(
								undirectedGraph);
						java.util.List<Statement> path = shortestPath.getPath(firstNode, secondNode);
						if(firstNode == secondNode)
						{
							System.out.println("je suis ici");
						}
						// combiner les plus courts chemins pour construire le résultat
						if(secondModelNode.Type == "model")
						{
							result.add(secondModelNode.Model);
						}
						if(firstModelNode.Type == "model")
						{
							result.add(firstModelNode.Model);
						}
	
						result.add(path);
	
						resultnodemodel = new ResultNodeModel(result, firstNode, "model");
					}
				}
				else
				{
	
					ModelNode firstModelNode = modelNodes.get(0);
					if("model".equals(firstModelNode.Type))
					{
						result.add(firstModelNode.Model);
						Resource resource = FileToModelGraph.ModelToGraph(result).getVertices()
								.iterator().next().asResource();
						resultnodemodel = new ResultNodeModel(result, resource, "model");
					}
					else
					{
						if("uri".equals(firstModelNode.Type))
						{
							Resource x = result
									.createResource(firstModelNode.Node.asResource().getURI());
							resultnodemodel = new ResultNodeModel(result, x, "node");
						}
						else
						{
							Literal x = result
									.createLiteral(firstModelNode.Node.asLiteral().getString());
							resultnodemodel = new ResultNodeModel(result, x, "node");
	
						}
	
					}
				}
				if(resultnodemodel.getTypeResult() == "model")
				{
					if(!resultListString.contains(resultnodemodel.getModel().toString()))
					{
						resultList.add(resultnodemodel);
						resultListString.add(resultnodemodel.Model.toString());
					}
				}
				else
				{
					if(!resultListString.contains(resultnodemodel.getNode().toString()))
					{
						resultList.add(resultnodemodel);
						resultListString.add(resultnodemodel.getNode().toString());
					}
				}
			}
		}

		return resultList;

	}

    public static class ResultNodeModel {

        Model Model;
        RDFNode Node;
        String TypeResult;

        public Model getModel() {
            return Model;
        }

        public RDFNode getNode() {
            return Node;
        }

        public String getTypeResult() {
            return TypeResult;
        }

        /**
         * param ReleventFragment
         *
         * @
         *
         */
        public ResultNodeModel(Model model, RDFNode node, String typeResult) {
            Model = model;

            Node = node;
            TypeResult = typeResult;
        }
    }
}
