package edu.lehigh.libraries.purchase_request.returns_client.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.lehigh.libraries.purchase_request.returns_client.model.ReturnedItem;
import edu.lehigh.libraries.purchase_request.returns_client.service.ReturnedItemService;

@RestController
@Validated
public class AjaxController {
    
    private final ReturnedItemService service;

    public AjaxController(ReturnedItemService service) {
        this.service = service;
    }

    @GetMapping("/search")
    ResponseEntity<ReturnedItem> getReturnedItem(@RequestParam String barcode) {
        ReturnedItem returnedItem = service.findByBarcode(barcode);
        if (returnedItem == null) {
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<ReturnedItem>(returnedItem, HttpStatus.OK);
    }

}