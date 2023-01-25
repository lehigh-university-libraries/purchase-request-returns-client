package edu.lehigh.libraries.purchase_request.returns_client.illiad;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import edu.lehigh.libraries.purchase_request.returns_client.config.PropertiesConfig;
import edu.lehigh.libraries.purchase_request.returns_client.model.ReturnedItem;
import edu.lehigh.libraries.purchase_request.returns_client.service.LoanService;
import edu.lehigh.libraries.purchase_request.returns_client.service.LoanServiceException;
import edu.lehigh.libraries.purchase_request.returns_client.service.ReturnedItemService;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@ConditionalOnProperty(name="returns-client.illiad.base-url")
public class IlliadLoanService implements LoanService {

    private static final String API_KEY_HEADER = "ApiKey";

    private final String ISBN_KEY = "ISSN"; // not a typo, it's ILLiad's schema.
    private final String TITLE_KEY = "LoanTitle";
    private final String CONTRIBUTOR_KEY = "LoanAuthor";
    private final String REQUESTER_KEY = "Username";

    private final PropertiesConfig config;

    private String BASE_URL;
    private CloseableHttpClient client;

    IlliadLoanService(ReturnedItemService service, PropertiesConfig config) {
        this.config = config;

        initConnection();

        service.addLoanService(this);
        log.info("IlliadLoanService started.");
    }

    private void initConnection() {
        BASE_URL = config.getIlliad().getBaseUrl();
        client = HttpClientBuilder.create()
            .build();

    }

    @Override
    public boolean handlesBarcode(String barcode) {
        int barcodeNum;
        try {
            barcodeNum = Integer.parseInt(barcode);
        } catch (NumberFormatException e) {
            // not ILLiad
            return false;
        }

        // Look for 6 digit numbers
        if (barcodeNum > 100000 && barcodeNum <= 999999) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public ReturnedItem getReturnedItem(String barcode) throws LoanServiceException {
        String url = BASE_URL + "/Transaction/" + barcode;
        HttpUriRequest getRequest = RequestBuilder.get()
            .setUri(url)
            .setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
            .setHeader(HttpHeaders.ACCEPT, "application/json; version=1") // API requires "1" not 1.0, 1.1
            .setHeader(API_KEY_HEADER, config.getIlliad().getApiKey())
            .build();

        CloseableHttpResponse response;
        String responseString;
        try {
            response = client.execute(getRequest);
            HttpEntity entity = response.getEntity();
            responseString = EntityUtils.toString(entity);
            log.debug("got response string: " + responseString);
        }
        catch (Exception e) {
            log.error("Could not get book data from ILLiad.", e);
            throw new LoanServiceException("Could not get book data from ILLiad.");
        }
        // int responseCode = response.getStatusLine().getStatusCode();
        JSONObject jsonObject = new JSONObject(responseString);

        ReturnedItem item = new ReturnedItem();
        item.setIsbn(cleanIsbn(getIlliadString(ISBN_KEY, jsonObject)));
        item.setBarcode(barcode);
        item.setTitle(getIlliadString(TITLE_KEY, jsonObject));
        item.setContributor(getIlliadString(CONTRIBUTOR_KEY, jsonObject));
        item.setRequesterUsername(getIlliadString(REQUESTER_KEY, jsonObject));
        return item;
    }

    private String getIlliadString(String key, JSONObject jsonObject){
        if (jsonObject.has(key) && !jsonObject.isNull(key) && jsonObject.getString(key).length() > 0) {
            return jsonObject.getString(key);
        }
        return null;
    }

    private String cleanIsbn(String rawIsbn) {
        if (rawIsbn == null) {
            return null;
        }

        // trim whitespace first
        rawIsbn = rawIsbn.trim();
        
        // clear any format comments after the number
        int spaceIndex = rawIsbn.indexOf(' ');
        if (spaceIndex > -1) {
            return rawIsbn.substring(0, spaceIndex);
        }
        else {
            return rawIsbn;
        }
    }

}
