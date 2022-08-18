package edu.lehigh.libraries.purchase_request.returns_client.reshare;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import edu.lehigh.libraries.purchase_request.returns_client.model.ReturnedItem;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "loans")
@Getter @Setter
public class ReShareLoanRecord {

    public static ReShareLoanRecord fromBarcode(String barcode) {
        ReShareLoanRecord item = new ReShareLoanRecord();
        item.setBarcode(barcode);
        return item;
    }

    @Id
    private String id;

    private String barcode;

    private String title;

    private String contributor;

    private String isbn;

    private String username;

    public ReturnedItem toReturnedItem() {
        ReturnedItem item = new ReturnedItem();
        item.setBarcode(barcode);
        item.setTitle(title);
        item.setContributor(contributor);
        item.setIsbn(isbn);
        item.setRequesterUsername(username);
        return item;
    }

}
