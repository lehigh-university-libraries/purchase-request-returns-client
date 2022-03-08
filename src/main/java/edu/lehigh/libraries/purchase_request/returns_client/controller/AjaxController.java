package edu.lehigh.libraries.purchase_request.returns_client.controller;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.lehigh.libraries.purchase_request.returns_client.model.ReturnedItem;
import edu.lehigh.libraries.purchase_request.returns_client.service.ReturnedItemService;
import edu.lehigh.libraries.purchase_request.returns_client.service.WorkflowService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@RestController
@ConditionalOnWebApplication
@Validated
@Slf4j
public class AjaxController {
    
    private ReturnedItemService returnedItemService;
    private WorkflowService workflowService;

    public AjaxController(ReturnedItemService returnedItemService, WorkflowService workflowService) {
        this.returnedItemService = returnedItemService;
        this.workflowService = workflowService;
    }

    @GetMapping("/search")
    ResponseEntity<ReturnedItem> getReturnedItem(@RequestParam String barcode) {
        log.info("Request: GET /search/" + barcode);
        ReturnedItem returnedItem = returnedItemService.findByBarcode(barcode);
        if (returnedItem == null) {
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<ReturnedItem>(returnedItem, HttpStatus.OK);
    }

    @Getter
    @Setter
    static class BarcodeContainer {
        private String barcode;
    }

    @PostMapping("/request")
    ResponseEntity<ReturnedItem> requestItem(@RequestBody BarcodeContainer container , Authentication authentication) {
        String barcode = container.getBarcode();
        log.info("Request: POST /request " + barcode);
        ReturnedItem returnedItem = returnedItemService.findByBarcode(barcode);
        if (returnedItem == null) {
            return ResponseEntity.notFound().build();
        }

        String reporterName = authentication.getName();
        returnedItem.setReporterName(reporterName);
        log.info("Reporter " + reporterName + " requesting purchase: " + returnedItem);

        try {
            workflowService.submitRequest(returnedItem);
        }
        catch (Exception e) {
            log.error("Could not submit purchase to workflow server: " + returnedItem, e);
            return ResponseEntity.internalServerError().build();
        }

        return new ResponseEntity<ReturnedItem>(returnedItem, HttpStatus.CREATED);
    }

}