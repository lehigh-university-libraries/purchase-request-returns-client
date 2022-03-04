package edu.lehigh.libraries.purchase_request.returns_client.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import edu.lehigh.libraries.purchase_request.returns_client.config.PropertiesConfig;
import edu.lehigh.libraries.purchase_request.returns_client.model.ReturnedItem;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WorkflowService {

    @Autowired
    private RestTemplate restTemplate;

    private HttpHeaders headers;
    private PropertiesConfig config;

    private String URL;

    public WorkflowService(RestTemplate restTemplate, PropertiesConfig config) {
        this.restTemplate = restTemplate;
        this.config = config;
        initHeaders();

        this.URL = config.getWorkflowServer().getPostPurchaseRequestsUrl();
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
            URL, 
            request,
            Object.class);
        log.debug("Submitted request with result " + result);
        return true;
    }

}
