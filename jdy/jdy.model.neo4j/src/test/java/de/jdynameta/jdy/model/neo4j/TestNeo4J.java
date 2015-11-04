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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import org.junit.After;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.neo4j.test.TestGraphDatabaseFactory;

/**
 *
 * @author rschneider
 */
public class TestNeo4J {

    private GraphDatabaseService graphDb;

    @Before
    public void prepareTestDatabase() {

        graphDb
                = new TestGraphDatabaseFactory()
                .newImpermanentDatabaseBuilder()
                .setConfig(GraphDatabaseSettings.pagecache_memory, "512M")
                .setConfig(GraphDatabaseSettings.string_block_size, "60")
                .setConfig(GraphDatabaseSettings.array_block_size, "300")
                .newGraphDatabase();
    }

    @After
    public void destroyTestDatabase() {
        graphDb.shutdown();
    }

    @Test
    public void exampleTestCase() {
        
        Node n;
        try (Transaction tx = graphDb.beginTx()) {
            n = graphDb.createNode();
            n.setProperty("name", "Nancy");
            tx.success();
        }

        // The node should have a valid id
        assertThat(n.getId(), is(greaterThan(-1L)));

        // Retrieve a node by using the id of the created node. The id's and
        // property should match.
        try (Transaction tx = graphDb.beginTx()) {
            Node foundNode = graphDb.getNodeById(n.getId());
            assertThat(foundNode.getId(), is(n.getId()));
            assertThat((String) foundNode.getProperty("name"), is("Nancy"));
        }
    }

}
