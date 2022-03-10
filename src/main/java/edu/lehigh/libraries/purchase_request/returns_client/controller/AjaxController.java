package edu.lehigh.libraries.purchase_request.returns_client.controller;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import edu.lehigh.libraries.purchase_request.returns_client.model.BarcodeContainer;
import edu.lehigh.libraries.purchase_request.returns_client.model.ReturnedItem;
import edu.lehigh.libraries.purchase_request.returns_client.service.IncompleteItemException;
import edu.lehigh.libraries.purchase_request.returns_client.service.ItemNotFoundException;
import edu.lehigh.libraries.purchase_request.returns_client.service.LoanServiceException;
import edu.lehigh.libraries.purchase_request.returns_client.service.ReturnedItemService;
import edu.lehigh.libraries.purchase_request.returns_client.service.WorkflowService;
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
    @ResponseBody
    ReturnedItem getReturnedItem(@RequestParam @Pattern(regexp = BarcodeContainer.BARCODE_PATTERN) String barcode) {
        log.info("Request: GET /search/" + barcode);
        ReturnedItem returnedItem = findItem(barcode);
        return returnedItem;
    }

    private ReturnedItem findItem(String barcode) {
        ReturnedItem returnedItem;
        try {
            returnedItem = returnedItemService.findByBarcode(barcode);
            if (returnedItem == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The barcode was not found.");
            }
        }
        catch (ItemNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The barcode was not found.", e);
        }        
        catch (IncompleteItemException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, 
                "The loan service does not have enough description of this item to create a purchase request.", e);
        }
        catch (LoanServiceException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "Error getting data from a loan service.", e);
        }
        return returnedItem;
    }

    @PostMapping("/request")
    ResponseEntity<ReturnedItem> requestItem(@RequestBody @Valid BarcodeContainer container , Authentication authentication) {
        String barcode = container.getBarcode();
        log.info("Request: POST /request " + barcode);
        ReturnedItem returnedItem = findItem(barcode);

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

    @ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="Constraint violation")
    @ExceptionHandler(ConstraintViolationException.class)
    public void error() {
        // no op
        log.debug("found constraint violation");
    }

}