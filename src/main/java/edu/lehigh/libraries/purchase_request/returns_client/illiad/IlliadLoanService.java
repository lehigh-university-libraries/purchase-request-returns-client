package edu.lehigh.libraries.purchase_request.returns_client.illiad;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.lehigh.libraries.purchase_request.returns_client.model.ReturnedItem;
import edu.lehigh.libraries.purchase_request.returns_client.service.CoverImagesService;
import edu.lehigh.libraries.purchase_request.returns_client.service.LoanService;
import edu.lehigh.libraries.purchase_request.returns_client.service.ReturnedItemService;

@Service
public class IlliadLoanService implements LoanService {

    @Autowired
    private CoverImagesService coverImages;

    IlliadLoanService(ReturnedItemService service) {
        service.addLoanService(this);
    }

    @Override
    public boolean handlesBarcode(String barcode) {
      // TODO Depends on barcode syntax?
      return true;
    }

    @Override
    public ReturnedItem getReturnedItem(String barcode) {
      // TODO Implement for real
      ReturnedItem item = new ReturnedItem();
      item.setBarcode(barcode);
      item.setTitle("Station Eleven");
      item.setContributor("Emily St. John Mandel");
      String isbn ="0804172447"; 
      item.setIsbn(isbn);
      item.setCoverImage(coverImages.getCoverImage(isbn));
      return item;
    }

}
