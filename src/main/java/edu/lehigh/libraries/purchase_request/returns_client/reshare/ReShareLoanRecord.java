package edu.lehigh.libraries.purchase_request.returns_client.reshare;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import edu.lehigh.libraries.purchase_request.returns_client.model.ReturnedItem;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "pat_reqs")
@Getter @Setter
public class ReShareLoanRecord {

    public static ReShareLoanRecord fromBarcode(String barcode) {
        ReShareLoanRecord item = new ReShareLoanRecord();
        item.setBarcode(barcode);
        return item;
    }

    @Id
    @Column(name = "lpr_hrid")
    private String barcode;

    @Column(name = "lpr_title")
    private String title;

    @Column(name = "lpr_author")
    private String contributor;

    @Column(name = "lpr_isbn")
    private String isbn;

    @Column(name = "lpr_patron_email")
    private String email;

    public ReturnedItem toReturnedItem() {
        ReturnedItem item = new ReturnedItem();
        item.setBarcode(barcode);
        item.setTitle(title);
        item.setContributor(contributor);
        item.setIsbn(isbn);

        String username = email.substring(0, email.indexOf("@"));
        item.setRequesterUsername(username);
        return item;
    }

}
