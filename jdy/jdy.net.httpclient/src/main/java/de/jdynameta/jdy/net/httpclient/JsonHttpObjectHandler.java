package de.jdynameta.jdy.net.httpclient;

public class JsonHttpObjectHandler
{

    protected String host;
    protected int port;
    protected String basePath;
    protected final String metaModelNamespace;

    public JsonHttpObjectHandler(String host, int port, String basePath, String metaModelNamespace)
    {
        super();
        this.host = host;
        this.port = port;
        this.basePath = basePath;
        this.metaModelNamespace = metaModelNamespace;
    }

}
