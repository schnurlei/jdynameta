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
package de.jdynameta.jdy.model.neo4j;

import java.io.File;
import java.util.ArrayList;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import static org.neo4j.helpers.collection.IteratorUtil.loop;
import org.neo4j.logging.slf4j.Slf4jLogProvider;

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

        File testDirectory = new File("neo4jDb");

        GraphDatabaseService graphDb = new GraphDatabaseFactory()
                .setUserLogProvider(new Slf4jLogProvider())
                .newEmbeddedDatabaseBuilder(testDirectory)
                .setConfig(GraphDatabaseSettings.pagecache_memory, "512M")
                .setConfig(GraphDatabaseSettings.string_block_size, "60")
                .setConfig(GraphDatabaseSettings.array_block_size, "300")
                .newGraphDatabase();
        registerShutdownHook(graphDb);

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

    private static void updateUserData(GraphDatabaseService aGraphDb) {

        try (Transaction tx = aGraphDb.beginTx()) {
            Label label = DynamicLabel.label("User");
            int idToFind = 45;
            String nameToFind = "user" + idToFind + "@example.org";

            for (Node node : loop(aGraphDb.findNodes(label, "username", nameToFind))) {
                node.setProperty("username", "user" + (idToFind + 1) + "@example.org");
            }
            tx.success();
        }
    }

    private static void deleteUserData(GraphDatabaseService aGraphDb) {

        try (Transaction tx = aGraphDb.beginTx()) {
            Label label = DynamicLabel.label("User");
            int idToFind = 46;
            String nameToFind = "user" + idToFind + "@example.org";

            for (Node node : loop(aGraphDb.findNodes(label, "username", nameToFind))) {
                node.delete();
            }
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
        insertHellWorldData(graphDb);
        insertUserData(graphDb);
        updateUserData(graphDb);
        deleteUserData(graphDb);

    }
}
