/*
 * Copyright 2015 rainer.
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
package de.jdynameta.jdy.jaxrs;

import de.jdynameta.base.creation.AbstractReflectionCreator;
import de.jdynameta.base.creation.ObjectTransformator;
import de.jdynameta.base.metainfo.AssociationInfo;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ClassRepository;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.metainfo.filter.ClassInfoQuery;
import de.jdynameta.base.metainfo.filter.defaultimpl.DefaultClassInfoQuery;
import de.jdynameta.base.metainfo.primitive.LongType;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.value.ClassNameCreator;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ObjectCreationException;
import de.jdynameta.base.value.TypedValueObject;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.base.value.defaultimpl.ReflectionChangeableValueObject;
import de.jdynameta.base.value.defaultimpl.TypedWrappedValueObject;
import de.jdynameta.jdy.model.json.JsonCompactFileReader;
import de.jdynameta.jdy.model.json.JsonFileWriter;
import de.jdynameta.jdy.net.httpclient.JsonHttpObjectReader;
import de.jdynameta.metamodel.filter.AppFilterExpr;
import de.jdynameta.metamodel.filter.AppQuery;
import de.jdynameta.metamodel.filter.FilterCreator;
import de.jdynameta.metamodel.filter.FilterRepository;
import de.jdynameta.persistence.manager.PersistentOperation;
import de.jdynameta.persistence.state.ApplicationObj;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.UriInfo;
import javax.xml.transform.TransformerConfigurationException;

@Path("/entry-point")
public class EntryPoint {

    private MetadataManager metaManager;
    private JsonFileWriter fileWriter = new JsonFileWriter(new JsonFileWriter.WriteAllDependentStrategy(), true);

    
    @GET
    @Path("{paths: .+}")
    public String getElement(@PathParam("paths") List<PathSegment> segments, @Context UriInfo uriInfo) {

        MetadataPathInfoJaxrs pathInfo = MetadataPathInfoJaxrs.createFromPath(segments, uriInfo);

        ClassRepository repo = null;
        ClassInfo classInfo = repo.getClassForName(pathInfo.getClassName());

        if (classInfo != null) {
            try {
                ClassInfoQuery query = createQueryForString(pathInfo.getFilter(), repo, pathInfo.getClassName());
                ObjectList<? extends TypedValueObject> objects;
                objects = metaManager.loadObjectsFromDb(query);
                StringWriter writer = new StringWriter();
                fileWriter.writeObjectList(writer, classInfo, objects, PersistentOperation.Operation.READ);

                
            } catch (JdyPersistentException | ObjectCreationException | TransformerConfigurationException ex) {
                Logger.getLogger(EntryPoint.class.getName()).log(Level.SEVERE, null, ex);
                throw new BadRequestException(ex);
            }
            
        } else {
            throw new NotFoundException("No Type foind for " + pathInfo.getClassName());
        }
        return null;
    }
    
    public static ClassInfoQuery createQueryForString(String aFilterExpr, ClassRepository repo, String aClassName) throws JdyPersistentException, ObjectCreationException
    {
        if (aFilterExpr != null)
        {
            JsonCompactFileReader.GeneratedValueCreator valueGenerator = new JsonCompactFileReader.GeneratedValueCreator()
            {
                public long nextValue = 0;

                @Override
                public Object createValue(ClassInfo aClassInfo, AttributeInfo aAttrInfo)
                {
                    return nextValue++;
                }

                @Override
                public boolean canGenerateValue(ClassInfo aClassInfo, AttributeInfo aAttrInfo)
                {
                    return (aAttrInfo instanceof PrimitiveAttributeInfo)
                            && ((PrimitiveAttributeInfo) aAttrInfo).getType() instanceof LongType
                            && ((PrimitiveAttributeInfo) aAttrInfo).isGenerated();
                }
            };

            HashMap<String, String> att2AbbrMap = FilterCreator.createAbbreviationMap();

            JsonCompactFileReader reader = new JsonCompactFileReader(att2AbbrMap, FilterRepository.getSingleton().getRepoName(), valueGenerator);
            ObjectList<ApplicationObj> result = reader.readObjectList(new StringReader(aFilterExpr), FilterRepository.getSingleton().getInfoForType(FilterRepository.TypeName.AppFilterExpr));

            FilterTransformator transformator = new FilterTransformator(FilterRepository.NAME_CREATOR);
            ObjectList<ReflectionChangeableValueObject> convertedList = JsonHttpObjectReader.convertValObjList(result, transformator);

            AppFilterExpr expr = (AppFilterExpr) convertedList.get(0);
            AppQuery newAppQuery = new AppQuery();
            newAppQuery.setExpr(expr);
            newAppQuery.setRepoName(repo.getRepoName());
            newAppQuery.setClassName(aClassName);

            FilterCreator creator = new FilterCreator();
            return creator.createMetaFilter(newAppQuery, repo);
        } else
        {
            ClassInfo classInfo = repo.getClassForName(aClassName);
            if (classInfo == null)
            {
                throw new JdyPersistentException("Invlaid class name " + aClassName);
            }
            return new DefaultClassInfoQuery(classInfo);
        }

    }
    
    @SuppressWarnings("serial")
    private static class FilterTransformator extends AbstractReflectionCreator<ReflectionChangeableValueObject>
            implements ObjectTransformator<ValueObject, ReflectionChangeableValueObject>
    {

        public FilterTransformator(ClassNameCreator aNameCreator)
        {
            super(aNameCreator);
        }

        @Override
        public TypedValueObject getValueObjectFor(ClassInfo aClassinfo,
                ValueObject aObjectToTransform)
        {
            return new TypedWrappedValueObject(aObjectToTransform, aClassinfo);
        }

        @Override
        protected ReflectionChangeableValueObject createProxyObjectFor(
                TypedValueObject aObjToHandle)
        {
            return null;
        }

        @Override
        protected void setProxyListForAssoc(AssociationInfo aCurAssocInfo,
                ReflectionChangeableValueObject aObjoSetVals,
                TypedValueObject aObjToGetVals) throws ObjectCreationException
        {

        }
    }
}
