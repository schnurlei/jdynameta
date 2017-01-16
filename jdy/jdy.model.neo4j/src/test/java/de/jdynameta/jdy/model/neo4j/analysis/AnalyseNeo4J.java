/*
 * Copyright 2015 rschneider.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.jdynameta.jdy.model.neo4j.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.neo4j.test.TestGraphDatabaseFactory;

/**
 *
 * @author rschneider
 */
public class AnalyseNeo4J {

    private static enum RelTypes implements RelationshipType {

        KNOWS
    }

    private static void registerShutdownHook(final GraphDatabaseService graphDb) {
        // Registers a shutdown hook for the Neo4j instance so that it
        // shuts down nicely when the VM exits (even if you "Ctrl-C" the
        // running application).
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                graphDb.shutdown();
            }
        });
    }

    private static GraphDatabaseService startDatabase() {

//        File testDirectory = new File("neo4jDb");
//
//        GraphDatabaseService graphDb = new GraphDatabaseFactory()
//                .setUserLogProvider(new Slf4jLogProvider())
//                .newEmbeddedDatabaseBuilder(testDirectory)
//                .setConfig(GraphDatabaseSettings.pagecache_memory, "512M")
//                .setConfig(GraphDatabaseSettings.string_block_size, "60")
//                .setConfig(GraphDatabaseSettings.array_block_size, "300")
//                .newGraphDatabase();
//        registerShutdownHook(graphDb);
        GraphDatabaseService graphDb
                = new TestGraphDatabaseFactory()
                .newImpermanentDatabaseBuilder()
                .setConfig(GraphDatabaseSettings.pagecache_memory, "512M")
                .setConfig(GraphDatabaseSettings.string_block_size, "60")
                .setConfig(GraphDatabaseSettings.array_block_size, "300")
                .newGraphDatabase();

        return graphDb;
    }

    private static void insertHellWorldData(GraphDatabaseService aGraphDb) {
        Node firstNode;
        Node secondNode;

        try (Transaction tx = aGraphDb.beginTx()) {
            firstNode = aGraphDb.createNode();
            firstNode.setProperty("message", "Hello, ");
            secondNode = aGraphDb.createNode();
            secondNode.setProperty("message", "World!");

            Relationship relationship = firstNode.createRelationshipTo(secondNode, RelTypes.KNOWS);
            relationship.setProperty("message", "brave Neo4j ");
            tx.success();
        }

        try (Transaction tx = aGraphDb.beginTx()) {
            firstNode.getSingleRelationship(RelTypes.KNOWS, Direction.OUTGOING).delete();
            firstNode.delete();
            secondNode.delete();
        }
    }

    private static void insertUserData(GraphDatabaseService aGraphDb) {

        try (Transaction tx = aGraphDb.beginTx()) {

            Label label = DynamicLabel.label("User");
            // Create some users
            for (int id = 0; id < 100; id++) {
                Node userNode = aGraphDb.createNode(label);
                userNode.setProperty("username", "user" + id + "@example.org");
            }
            System.out.println("Users created");
            tx.success();
        }
    }

    private static void insertDataWithCypher(GraphDatabaseService aGraphDb) {

        try (Transaction tx = aGraphDb.beginTx()) {
            Map<String, Object> props = new HashMap<>();
            props.put("Text", "TextValue");
            props.put("Long", new Long(771771));
            props.put("Double", new Double(525252.5252));
            props.put("Boolean", Boolean.TRUE);
            props.put("$TYPE", "USER");

            Map<String, Object> params = new HashMap<>();
            params.put("props", props);
            Label label = DynamicLabel.label("User");
            String query = "CREATE (n:" + label + "{props}) RETURN n";
            Result insertResult = aGraphDb.execute(query, params);

            try (ResourceIterator<Node> insertedObjectIterator = insertResult.columnAs("n")) {
                Node result = insertedObjectIterator.next();
                System.out.println(insertedObjectIterator.hasNext());
                for (String property : result.getPropertyKeys()) {
                    System.out.println(property + " - ");
                    Object value = result.getProperty(property);
                    System.out.println(value);
                }
            }
        }
    }

    private static void insertRelationshipWithCypher(GraphDatabaseService aGraphDb) {

        try (Transaction tx = aGraphDb.beginTx()) {
            Label userLabel = DynamicLabel.label("User");
            Label plantLabel = DynamicLabel.label("Plant");

            Node userNodeA = aGraphDb.createNode(userLabel);
            userNodeA.setProperty("username", "userA");
            userNodeA.setProperty("userid", "user1");
            Node userNodeB = aGraphDb.createNode(userLabel);
            userNodeB.setProperty("username", "userB");
            userNodeB.setProperty("userid", "user2");
            Node plantA = aGraphDb.createNode(plantLabel);
            plantA.setProperty("plantname", "plantA");
            Node planteB = aGraphDb.createNode(plantLabel);
            planteB.setProperty("plantname", "plantB");

            tx.success();
        }

        try (Transaction tx = aGraphDb.beginTx()) {

            Map<String, Object> props = new HashMap<>();
            props.put("user$username", "userA");
            props.put("userid", "user1");
            props.put("plantname", "plantA");

            String query = "MATCH (userResult:User)";
            query += ",(plant1:Plant)";
            query += "WHERE userResult.username = {user$username}";
            query += " AND userResult.userid = {userid}";
            query += " AND plant1.plantname = {plantname}";
            query += " CREATE (userResult)-[newRelation:OWNER]->(plant1) ";
            query += " RETURN newRelation";
            Result relationResult = aGraphDb.execute(query, props);
            printResult(relationResult);
//            printNode(relationResult, "plant1");
//            printNode(relationResult, "user1");
            tx.success();
        }

        try (Transaction tx = aGraphDb.beginTx()) {

            Map<String, Object> props = new HashMap<>();
            props.put("user$username", "userA");
            props.put("userid", "user1");
            props.put("plantname", "plantA");

            String query = "MATCH (userResult:User)";
            query += ",(plant1:Plant)";
            query += "WHERE userResult.username = {user$username}";
            query += " AND userResult.userid = {userid}";
            query += " AND plant1.plantname = {plantname}";
            query += " CREATE (userResult)-[newRelation:OWNER]->(plant1) ";
            query += " RETURN newRelation";
            Result relationResult = aGraphDb.execute(query, props);
            printResult(relationResult);
//            printNode(relationResult, "plant1");
//            printNode(relationResult, "user1");
            tx.success();
        }
    }

    private static void printResult(Result result) {

        while (result.hasNext()) {
            Map<String, Object> row = result.next();
            for (Entry<String, Object> column : row.entrySet()) {
                if (column.getValue() instanceof Node) {
                    Node value = (Node) column.getValue();
                    printNode(value);
                } else if (column.getValue() instanceof Relationship) {
                    Relationship value = (Relationship) column.getValue();
                    printRelationship(value);
                }
            }
        }
    }

    private static void printNode(Result queryResult, String colName) {

        ResourceIterator<Node> nodeIterator = queryResult.columnAs(colName);
        if (nodeIterator.hasNext()) {
            Node node = nodeIterator.next();
            printNode(node);
        }
        nodeIterator.close();
    }

    private static void printNode(Node node) {

        System.out.println(node.getLabels());
        System.out.println(node.getPropertyKeys());
        Iterable<Relationship> relations = node.getRelationships();
        for (Relationship relation : relations) {
            System.out.println(relation.toString());
        }
    }

    private static void printRelationship(Relationship relationship) {

        System.out.println("Relationship");
        System.out.println(relationship.getStartNode() +
                " -> " + relationship.getEndNode());
    }

    private static void updateUserData(GraphDatabaseService aGraphDb) {

        try (Transaction tx = aGraphDb.beginTx()) {
            Label label = DynamicLabel.label("User");
            int idToFind = 45;
            String nameToFind = "user" + idToFind + "@example.org";

//            for (Node node : Iterators.loop(iterator)(aGraphDb.findNodes(label, "username", nameToFind))) {
//                node.setProperty("username", "user" + (idToFind + 1) + "@example.org");
//            }
            tx.success();
        }
    }

    private static void deleteUserData(GraphDatabaseService aGraphDb) {

        try (Transaction tx = aGraphDb.beginTx()) {
            Label label = DynamicLabel.label("User");
            int idToFind = 46;
            String nameToFind = "user" + idToFind + "@example.org";

//            for (Node node : loop(aGraphDb.findNodes(label, "username", nameToFind))) {
//                node.delete();
//            }
            tx.success();
        }
    }

    private static void findUserData(GraphDatabaseService aGraphDb) {

        Label label = DynamicLabel.label("User");
        int idToFind = 45;
        String nameToFind = "user" + idToFind + "@neo4j.org";
        try (Transaction tx = aGraphDb.beginTx()) {
            try (ResourceIterator<Node> users
                    = aGraphDb.findNodes(label, "username", nameToFind)) {
                ArrayList<Node> userNodes = new ArrayList<>();
                while (users.hasNext()) {
                    userNodes.add(users.next());
                }

                userNodes.stream().forEach((node) -> {
                    System.out.println("The username of user " + idToFind + " is " + node.getProperty("username"));
                });
            }
            tx.success();
        }
    }

    public static void main(String[] args) {
        GraphDatabaseService graphDb = startDatabase();
//        insertHellWorldData(graphDb);
//        insertUserData(graphDb);
//        insertDataWithCypher(graphDb);
//        updateUserData(graphDb);
//        deleteUserData(graphDb);
        insertRelationshipWithCypher(graphDb);

    }
}
