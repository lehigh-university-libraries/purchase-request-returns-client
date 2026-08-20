package edu.lehigh.libraries.purchase_request.returns_client.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter 
@ToString
public class BarcodeContainer {

    public static final String BARCODE_PATTERN = "^[A-Za-z0-9-_]+$";

    @NotNull
    @Pattern(regexp = BARCODE_PATTERN)
    private String barcode;

    private String comments;

    private String requestType;
}