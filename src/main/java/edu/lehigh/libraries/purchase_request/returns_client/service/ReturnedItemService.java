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

	public ReturnedItem findByBarcode(String barcode) throws LoanServiceException {
		for (LoanService service : loanServices) {
			if (service.handlesBarcode(barcode)) {
				ReturnedItem item = service.getReturnedItem(barcode);
				checkItemSufficient(item);
				return item;
			}
		}
		throw new ItemNotFoundException("Could not identify a service that handles barcodes like: " + barcode);
	}

	private void checkItemSufficient(ReturnedItem item) throws IncompleteItemException {
		if (item.getIsbn() == null) {
			throw new IncompleteItemException("No ISBN found for item.");
		}
		if (item.getTitle() == null) {
			throw new IncompleteItemException("No title found for item.");
		}
	}

	public void addLoanService(LoanService service) {
		loanServices.add(service);
	}

}
