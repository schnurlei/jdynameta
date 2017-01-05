/*
 * Copyright 2016 rschneider.
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
package de.jdynameta.jdy.model.mongodb.analyse;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongoCmdOptionsBuilder;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.config.Storage;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.io.file.Files;
import de.flapdoodle.embed.process.runtime.Network;
import java.io.File;
import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.bson.Document;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import static java.util.Arrays.asList;

/**
 *
 * @author rschneider
 */
public class AnalyseMongodb {

    private static final int PORT = 12345;
    private static MongodExecutable _mongodExe;
    private static MongodProcess _mongod;

    public AnalyseMongodb() {
    }

    @BeforeClass
    public static void setUpClass() throws IOException {

        final String DB_FOLDER_NAME = "c:/tmp/testdbdir";
        
        try {
            Files.forceDelete(new File(DB_FOLDER_NAME));
        } catch (Exception e) {

        }

        new File(DB_FOLDER_NAME).mkdirs();

        IMongodConfig mongodConfig = new MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .configServer(false)
                .replication(new Storage(DB_FOLDER_NAME, null, 0))
                .net(new Net(PORT, Network.localhostIsIPv6()))
                .cmdOptions(new MongoCmdOptionsBuilder()
                        .syncDelay(0)
                        .useNoPrealloc(true)
                        .useSmallFiles(true)
                        .useNoJournal(true)
                        .build())
                .build();           
           MongodStarter starter = MongodStarter.getDefaultInstance();

        _mongodExe = starter.prepare(mongodConfig);
        _mongod = _mongodExe.start();
    }

    @AfterClass
    public static void tearDownClass() {
        _mongod.stop();
        _mongodExe.stop();
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void hello() throws ParseException {


        try (MongoClient mongoClient = new MongoClient("localhost", PORT)) {
            MongoDatabase db = mongoClient.getDatabase("jdy");
            insertData(db);
            showRestaurants(mongoClient);
        }
    }

    private void showRestaurants(MongoClient mongoClient) {
        
        MongoDatabase database = mongoClient.getDatabase("jdy");
        MongoCollection<Document> restaurants =  database.getCollection("restaurants");
        System.out.println("Restaurants " + restaurants.count());
    }
    
    private void insertData(MongoDatabase db) throws ParseException {

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
        db.getCollection("restaurants").insertOne(
                new Document("address",
                        new Document()
                        .append("street", "2 Avenue")
                        .append("zipcode", "10075")
                        .append("building", "1480")
                        .append("coord", asList(-73.9557413, 40.7720266)))
                .append("borough", "Manhattan")
                .append("cuisine", "Italian")
                .append("grades", asList(
                        new Document()
                        .append("date", format.parse("2014-10-01T00:00:00Z"))
                        .append("grade", "A")
                        .append("score", 11),
                        new Document()
                        .append("date", format.parse("2014-01-16T00:00:00Z"))
                        .append("grade", "B")
                        .append("score", 17)))
                .append("name", "Vella")
                .append("restaurant_id", "41704620"));
    }

}
