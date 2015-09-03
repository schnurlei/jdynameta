package de.jdynameta.json.client;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.transform.TransformerConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import de.jdynameta.base.creation.ObjectCreator;
import de.jdynameta.base.creation.ObjectReader;
import de.jdynameta.base.metainfo.filter.ClassInfoQuery;
import de.jdynameta.base.objectlist.ChangeableObjectList;
import de.jdynameta.base.objectlist.DefaultObjectList;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ObjectCreationException;
import de.jdynameta.base.value.TypedValueObject;
import de.jdynameta.json.JsonCompactFileWriter;
import de.jdynameta.json.JsonFileReader;
import de.jdynameta.json.JsonFileWriter;
import de.jdynameta.metamodel.filter.AppQuery;
import de.jdynameta.metamodel.filter.FilterCreator;
import de.jdynameta.persistence.manager.PersistentOperation;
import de.jdynameta.persistence.state.ApplicationObj;

public class JsonHttpObjectReader extends JsonHttpObjectHandler implements ObjectReader
{

    private static final long serialVersionUID = 1L;
    private final JsonFileReader fileReader;
    private HttpClient httpclient;

    public JsonHttpObjectReader(String aMetaModelNamespace)
    {
        this("localhost", 8080, "servlet", aMetaModelNamespace);
    }

    public JsonHttpObjectReader(final String aHost, int aPort, final String aBasePath, String aMetaModelNamespace)
    {
        super(aHost, aPort, aBasePath, aMetaModelNamespace);
        this.fileReader = new JsonFileReader();
        httpclient = new DefaultHttpClient();
    }

    @Override
    public <TCreatedObjFromValueObj> ObjectList<TCreatedObjFromValueObj> loadValuesFromDb(
            ClassInfoQuery aFilter, ObjectCreator<TCreatedObjFromValueObj> aObjCreator) throws JdyPersistentException
    {
        String response;
        try
        {
            response = sendJsonGetRequest(createUrlForClassInfo(aFilter));
            System.out.println(response);
        } catch (URISyntaxException | TransformerConfigurationException ex)
        {
            throw new JdyPersistentException(ex.getLocalizedMessage(), ex);
        }

        ObjectList<ApplicationObj> aReadedList = fileReader.readObjectList(new StringReader(response), aFilter.getResultInfo());

        try
        {
            return convertValObjList(aReadedList, aObjCreator);
        } catch (ObjectCreationException ex)
        {
            throw new JdyPersistentException(ex.getLocalizedMessage(), ex);
        }
    }

    public static <TCreatedObjFromValueObj> ObjectList<TCreatedObjFromValueObj> convertValObjList(ObjectList<ApplicationObj> aReadedList, ObjectCreator<TCreatedObjFromValueObj> aObjCreator) throws ObjectCreationException
    {
        ArrayList<TCreatedObjFromValueObj> resultModelColl = new ArrayList<>(20);

        for (TypedValueObject curValObj : aReadedList)
        {
            resultModelColl.add(aObjCreator.createObjectFor(curValObj));
        }

        return new DefaultObjectList<>(resultModelColl);

    }

    /**
     *
     * @param aUri
     * @return
     * @throws JdyPersistentException
     */
    protected String sendJsonGetRequest(URI aUri) throws JdyPersistentException
    {
        httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 50000);
        //httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
        httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 50000);

        HttpGet httpget = new HttpGet(aUri);

//		get.setHeader("Content-Type", "application/json");
//		get.setHeader("Accept", "application/json");
        HttpResponse response = null;
        try
        {
            response = httpclient.execute(httpget);

        } catch (IOException e)
        {
            throw new JdyPersistentException(e.getLocalizedMessage(), e);
        }

        String responseString = "";
        try
        {
            HttpEntity entity = response.getEntity();
            long len = entity.getContentLength();
            responseString = EntityUtils.toString(entity);
        } catch (IOException e)
        {
            throw new JdyPersistentException(e.getLocalizedMessage(), e);
        }

        // Check response code
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
        {
            throw new JdyPersistentException("Received error status " + response.getStatusLine().getStatusCode());
        }

        return responseString;
    }

    protected URI createUrlForClassInfo(ClassInfoQuery aFilter) throws URISyntaxException, JdyPersistentException, TransformerConfigurationException
    {
        String infoPath = JsonHttpObjectWriter.createInfoPath(aFilter.getResultInfo(), this.metaModelNamespace, this.basePath);

        URIBuilder builder = new URIBuilder();
        builder.setScheme("http");
        builder.setHost(this.host);
        builder.setPort(port);
        builder.setPath(infoPath);

        if (aFilter.getFilterExpression() != null)
        {
            FilterCreator creator = new FilterCreator();
            AppQuery appQuery = creator.createAppFilter(aFilter);
            StringWriter writerOut = new StringWriter();
            writExpreToJsonString(appQuery, writerOut);
            builder.setQuery(writerOut.toString());
        }

        return builder.build();
    }

    private void writExpreToJsonString(AppQuery appQuery, StringWriter writerOut)
            throws TransformerConfigurationException, JdyPersistentException
    {
        ObjectList<? extends TypedValueObject> queryColl = new ChangeableObjectList<>(appQuery.getExpr());
        HashMap<String, String> att2AbbrMap = new HashMap<>();
        att2AbbrMap.put("repoName", "rn");
        att2AbbrMap.put("className", "cn");
        att2AbbrMap.put("expr", "ex");
        att2AbbrMap.put("orSubExpr", "ose");
        att2AbbrMap.put("andSubExpr", "ase");
        att2AbbrMap.put("attrName", "an");
        att2AbbrMap.put("operator", "op");
        att2AbbrMap.put("isNotEqual", "ne");
        att2AbbrMap.put("isAlsoEqual", "ae");
        att2AbbrMap.put("longVal", "lv");
        att2AbbrMap.put("textVal", "tv");
        new JsonCompactFileWriter(new JsonFileWriter.WriteAllStrategy(), true, att2AbbrMap)
                .writeObjectList(writerOut, appQuery.getClassInfo(), queryColl, PersistentOperation.Operation.READ);
    }

}
