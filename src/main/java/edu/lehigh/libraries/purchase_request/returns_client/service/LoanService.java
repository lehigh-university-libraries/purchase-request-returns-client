package edu.lehigh.libraries.purchase_request.returns_client.service;

import edu.lehigh.libraries.purchase_request.returns_client.model.ReturnedItem;

public interface LoanService {
  
    boolean handlesBarcode(String barcode);

    ReturnedItem getReturnedItem(String barcode);

}
