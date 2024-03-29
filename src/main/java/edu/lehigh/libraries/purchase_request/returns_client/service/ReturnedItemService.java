package edu.lehigh.libraries.purchase_request.returns_client.service;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import edu.lehigh.libraries.purchase_request.returns_client.model.ReturnedItem;

@Service
@Validated
public class ReturnedItemService {

	List<LoanService> loanServices;

    @Autowired
    private CoverImagesService coverImages;

	public ReturnedItemService() {
		loanServices = new LinkedList<LoanService>();
	}

	public boolean isValid(String barcode) {
		for (LoanService service : loanServices) {
			if (service.handlesBarcode(barcode)) {
				return true;
			}
		}
		return false;
	}

	public ReturnedItem findByBarcode(String barcode) throws LoanServiceException {
		for (LoanService service : loanServices) {
			if (service.handlesBarcode(barcode)) {
				ReturnedItem item = service.getReturnedItem(barcode);
				checkItemSufficient(item);
				enrichItem(item);
				return item;
			}
		}
		throw new ItemNotFoundException("Could not identify a service that handles barcodes like: " + barcode);
	}

	private void checkItemSufficient(ReturnedItem item) throws IncompleteItemException {
		if (item.getTitle() == null) {
			throw new IncompleteItemException("No title found for item.");
		}
	}

	private void enrichItem(ReturnedItem item) {
		// Add cover image
		if (item.getIsbn() != null) {
			item.setCoverImage(coverImages.getCoverImage(item.getIsbn()));
		}
	}

	public void addLoanService(LoanService service) {
		loanServices.add(service);
	}

}
