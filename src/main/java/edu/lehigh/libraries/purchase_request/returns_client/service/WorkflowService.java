package edu.lehigh.libraries.purchase_request.returns_client.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import edu.lehigh.libraries.purchase_request.model.PurchaseRequest;
import edu.lehigh.libraries.purchase_request.returns_client.config.PropertiesConfig;
import edu.lehigh.libraries.purchase_request.returns_client.model.ReturnedItem;
import lombok.extern.slf4j.Slf4j;

@Service
@ConditionalOnWebApplication
@Slf4j
public class WorkflowService {

    @Autowired
    private RestTemplate restTemplate;

    private HttpHeaders headers;
    private PropertiesConfig config;

    private String BASE_URL;

    public WorkflowService(RestTemplate restTemplate, PropertiesConfig config) {
        this.restTemplate = restTemplate;
        this.config = config;
        initHeaders();

        this.BASE_URL = config.getWorkflowServer().getBaseUrl();
    }

    private void initHeaders() {
        this.headers = new HttpHeaders();
        headers.setBasicAuth(
            config.getWorkflowServer().getUsername(),
            config.getWorkflowServer().getPassword());
    }

    public boolean submitRequest(ReturnedItem returnedItem) {
        HttpEntity<Object> request = new HttpEntity<Object>(returnedItem, headers);
        Object result = restTemplate.postForObject(
            BASE_URL + "/purchase-requests", 
            request,
            Object.class);
        log.debug("Submitted request with result " + result);
        return true;
    }

    public List<PurchaseRequest> getHistory(String reporterName) {
        UriComponentsBuilder builder = UriComponentsBuilder
            .fromHttpUrl(BASE_URL + "/search");
        builder = builder.queryParam("reporterName", "\"" + reporterName + "\"");
        HttpEntity<String> requestEntity = new HttpEntity<String>(null, this.headers);
        ResponseEntity<PurchaseRequest[]> responseEntity = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, requestEntity, PurchaseRequest[].class);
        PurchaseRequest[] purchaseRequests = responseEntity.getBody();
        log.debug("Submitted request with result " + purchaseRequests);
        return Arrays.asList(purchaseRequests);
    }

}
