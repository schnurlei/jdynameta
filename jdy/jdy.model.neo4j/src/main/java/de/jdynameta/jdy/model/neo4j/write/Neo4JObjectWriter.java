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
package de.jdynameta.jdy.model.neo4j.write;

import de.jdynameta.base.creation.ObjectWriter;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.TypedValueObject;
import de.jdynameta.base.value.ValueObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;

/**
 *
 * @author rschneider
 */
public class Neo4JObjectWriter implements ObjectWriter {

    private GraphDatabaseService db;
    
    
    
    @Override
    public void deleteObjectInDb(ValueObject objToDelete, ClassInfo aInfo) throws JdyPersistentException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public TypedValueObject insertObjectInDb(ValueObject aObjToInsert, ClassInfo aInfo) throws JdyPersistentException {

        ClassInfo baseClass = getBaseClass(aInfo);
        final String nodeLabel = baseClass.getInternalName();
        final String nodeName = "node";
        final String propertyName = "props";

        
        try ( Transaction tx = db.beginTx())
        {
            Map<String, Object> props = new HashMap<>();
            props.put( "name", "Andres" );
            props.put( "position", "Developer" );

            Map<String, Object> params = new HashMap<>();
            params.put( propertyName, props );
            
            String queryString = createInsertQueryString(nodeLabel,nodeName, propertyName);
            Result insertResult = db.execute( queryString, params );
            
            try (ResourceIterator<Node> insertedObjectIterator = insertResult.columnAs(nodeName)) {
                
                Node insertedNode = assertOnlyOneObjectInIterator(insertedObjectIterator);
                Label insertedLabel = assertOnlyOneObjectInIterator(insertedNode.getLabels().iterator());
                assert(insertedLabel.name().equals(nodeLabel));
                for (String propertyKey : insertedNode.getPropertyKeys()) {

                    System.out.println(propertyKey);
                    Object propertyValue = insertedNode.getProperty(propertyKey);
                    System.out.println(propertyValue);
                }
            }
        }
        
        return null;
    }

    private<ITER_TYPE> ITER_TYPE assertOnlyOneObjectInIterator(final Iterator<ITER_TYPE> insertedObjectIterator) {
        ITER_TYPE result = insertedObjectIterator.next();
        assert(!insertedObjectIterator.hasNext());
        return result;
    }

    
    
    private String createInsertQueryString(String nodeLabel, String nodeName, String propertyName) {
        StringBuilder queryBuffer = new StringBuilder();
        queryBuffer.append("CREATE ")
                .append("( ")
                .append(nodeName).append(":").append(nodeLabel)
                .append("{").append(propertyName).append("} ")
                .append(")")
                .append(" RETURN ")
                .append(nodeName);
        return queryBuffer.toString();
    }

    @Override
    public void updateObjectToDb(ValueObject objToModify, ClassInfo aInfo) throws JdyPersistentException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    private ClassInfo getBaseClass(ClassInfo aInfo) {
        
        return (aInfo.getSuperclass() == null) ? aInfo : getBaseClass(aInfo);
    }
}
