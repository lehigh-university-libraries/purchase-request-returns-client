package edu.lehigh.libraries.purchase_request.returns_client.service;

import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Service;

import edu.lehigh.libraries.purchase_request.returns_client.model.ReturnedItem;

@Service
public class ReturnedItemService {

	List<LoanService> loanServices;

	public ReturnedItemService() {
		loanServices = new LinkedList<LoanService>();
	}

	public ReturnedItem findByBarcode(String barcode) {
		for (LoanService service : loanServices) {
			if (service.handlesBarcode(barcode)) {
				ReturnedItem item = service.getReturnedItem(barcode);
				return item;
			}
		}
		return null;
	}

	public void addLoanService(LoanService service) {
		loanServices.add(service);
	}

}
