package edu.lehigh.libraries.purchase_request.returns_client.service;

public class ItemNotFoundException extends LoanServiceException {

    public ItemNotFoundException(String message) {
        super(message);
    }

}