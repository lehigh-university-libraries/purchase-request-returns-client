package edu.lehigh.libraries.purchase_request.returns_client.service;

public class IncompleteItemException extends LoanServiceException {

    public IncompleteItemException(String message) {
        super(message);
    }

}