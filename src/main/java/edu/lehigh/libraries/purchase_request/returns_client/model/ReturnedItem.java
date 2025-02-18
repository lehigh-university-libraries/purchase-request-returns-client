package edu.lehigh.libraries.purchase_request.returns_client.model;

import javax.validation.constraints.NotNull;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @EqualsAndHashCode @ToString
public class ReturnedItem {

  private String barcode;

    @NotNull
    private String title;
    
    @NotNull
    private String contributor;

    private String isbn;

    private String coverImage;

    private String reporterName;

    private String requesterUsername;

    private String requesterComments;

    private String requestType;

    private String destination;

}
