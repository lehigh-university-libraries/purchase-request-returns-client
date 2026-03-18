package edu.lehigh.libraries.purchase_request.returns_client.connection;

import java.io.IOException;
import java.net.URI;

import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpVersion;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.net.URIBuilder;
import org.json.JSONObject;

import edu.lehigh.libraries.purchase_request.returns_client.config.PropertiesConfig;
import lombok.extern.slf4j.Slf4j;

// TODO Refactor this class with the server app version in FolioLocalHoldingsEnrichment and into a standalone module
@Slf4j
public class FolioConnection {

    private static final String LOGIN_PATH = "/authn/login";

    private static final String TENANT_HEADER = "x-okapi-tenant";
    private static final String TOKEN_HEADER = "x-okapi-token";

    private final PropertiesConfig config;

    private CloseableHttpClient client;
    private String token;

    public FolioConnection(PropertiesConfig config) throws Exception {
        this.config = config;

        initConnection();
        initToken();

        log.debug("FOLIO connection ready");
    }

    private void initConnection() {
        BasicCredentialsProvider provider = new BasicCredentialsProvider();
        provider.setCredentials(new AuthScope(null, -1),
            new UsernamePasswordCredentials(config.getFolio().getUsername(), config.getFolio().getPassword().toCharArray()));
        client = HttpClientBuilder.create()
            .setDefaultCredentialsProvider(provider)
            .build();                
    }

    private void initToken() throws Exception {
        String url = config.getFolio().getOkapiBaseUrl() + LOGIN_PATH;
        URI uri = new URIBuilder(url).build();

        JSONObject postData = new JSONObject();
        postData.put("username", config.getFolio().getUsername());
        postData.put("password", config.getFolio().getPassword());
        postData.put("tenant", config.getFolio().getTenantId());

        HttpPost post = new HttpPost(uri);
        post.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
        post.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
        post.setVersion(HttpVersion.HTTP_1_1);
        post.setHeader(TENANT_HEADER, config.getFolio().getTenantId());
        post.setEntity(new StringEntity(postData.toString(), ContentType.APPLICATION_JSON));
        token = client.execute(post, HttpClientContext.create(), response -> {
            String responseString = EntityUtils.toString(response.getEntity());
            int responseCode = response.getCode();
            log.debug("got auth response from folio with response code: " + responseCode);
            if (responseCode > 399) {
                throw new IOException(responseString);
            }
            return response.getFirstHeader(TOKEN_HEADER).getValue();
        });
    }

    public JSONObject executeGet(String url, String queryString) throws Exception {
        URI fullUri = new URIBuilder(url).addParameter("query", queryString).build();
        HttpGet getRequest = new HttpGet(fullUri);
        getRequest.setHeader(TENANT_HEADER, config.getFolio().getTenantId());
        getRequest.setHeader(TOKEN_HEADER, token);

        return client.execute(getRequest, HttpClientContext.create(), response -> {
            if (response.getCode() > 399) {
                throw new IOException("Cannot execute get: " + response);
            }
            String responseString = EntityUtils.toString(response.getEntity());
            log.debug("Got response with code " + response.getCode() + " and entity " + response.getEntity());
            return new JSONObject(responseString);
        });
    }



}
