package se.simonevertsson.gpu;

import org.neo4j.graphdb.*;

import java.util.ArrayList;

/**
 * Created by simon on 2015-05-12.
 */
public class GraphModelConverter {

    private final Iterable<Node> nodes;
    private final LabelDictionary labelDictionary;
    private ArrayList<Integer> labelIndicies;
    private ArrayList<Integer> nodeLabels;
    private ArrayList<Integer> adjacenyIndicies;
    private ArrayList<Long> nodeAdjecencies;

    public GraphModelConverter(Iterable<Node> nodes, LabelDictionary labelDictionary) {
        this.nodes = nodes;
        this.labelDictionary = labelDictionary;
    }

    public GpuGraphModel convert() {
        initializeLists();
        int currentLabelIndex = 0;
        int currentAdjacencyIndex = 0;
        for(Node sourceNode : nodes) {
            currentLabelIndex = addLabels(currentLabelIndex, sourceNode);
            currentAdjacencyIndex = addRelationships(currentAdjacencyIndex, sourceNode);
        }

        appendLastIndexIfNotEmpty(labelIndicies, currentLabelIndex);
        appendLastIndexIfNotEmpty(adjacenyIndicies, currentAdjacencyIndex);
        return new GpuGraphModel(labelIndicies, nodeLabels, adjacenyIndicies, nodeAdjecencies);
    }

    private int addRelationships(int currentAdjacencyIndex, Node sourceNode) {
        Iterable<Relationship> nodeRelationships = sourceNode.getRelationships(Direction.OUTGOING);
        int relationshipCount = addRelationships(nodeAdjecencies, nodeRelationships);
        adjacenyIndicies.add(currentAdjacencyIndex);
        currentAdjacencyIndex += relationshipCount;
        return currentAdjacencyIndex;
    }

    private int addLabels(int currentLabelIndex, Node sourceNode) {
        Iterable<Label> labels = sourceNode.getLabels();
        int labelCount = convertAndAddLabels(nodeLabels, labels);
        labelIndicies.add(currentLabelIndex);
        currentLabelIndex += labelCount;
        return currentLabelIndex;
    }

    private void appendLastIndexIfNotEmpty(ArrayList<Integer> indexList, int lastIndex) {
        if(!indexList.isEmpty()) {
            indexList.add(lastIndex);
        }
    }

    private int addRelationships(ArrayList<Long> nodeAdjecencies, Iterable<Relationship> nodeRelationships) {
        int relationshipCount = 0;
        for(Relationship nodeRelationship : nodeRelationships) {
            nodeAdjecencies.add(nodeRelationship.getEndNode().getId());
            relationshipCount++;
        }
        return relationshipCount;
    }

    private int convertAndAddLabels(ArrayList<Integer> nodeLabels, Iterable<Label> labels) {
        int labelCount = 0;
        for(Label nodeLabel : labels) {
            int nodeLabelId = labelDictionary.insertLabel(nodeLabel.name());
            nodeLabels.add(nodeLabelId);
            labelCount++;
        }
        return labelCount;
    }

    private void initializeLists() {
        this.labelIndicies = new ArrayList<Integer>();
        this.nodeLabels = new ArrayList<Integer>();
        this.adjacenyIndicies = new ArrayList<Integer>();
        this.nodeAdjecencies = new ArrayList<Long>();
    }
}