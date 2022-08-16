package edu.lehigh.libraries.purchase_request.returns_client.folio;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import edu.lehigh.libraries.purchase_request.returns_client.config.PropertiesConfig;
import edu.lehigh.libraries.purchase_request.returns_client.connection.FolioConnection;
import edu.lehigh.libraries.purchase_request.returns_client.model.ReturnedItem;
import edu.lehigh.libraries.purchase_request.returns_client.service.ItemNotFoundException;
import edu.lehigh.libraries.purchase_request.returns_client.service.LoanService;
import edu.lehigh.libraries.purchase_request.returns_client.service.LoanServiceException;
import edu.lehigh.libraries.purchase_request.returns_client.service.ReturnedItemService;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@ConditionalOnProperty(name="returns-client.folio.okapiBaseUrl")
public class FolioLoanService implements LoanService {
    
    private static final String INSTANCES_PATH = "/inventory/instances";

    private final Pattern BARCODE_PATTERN;

    private final PropertiesConfig config;
    private final FolioConnection folioConnection;

    FolioLoanService(ReturnedItemService service, PropertiesConfig config) throws Exception {
        this.config = config;

        final String BARCODE_REGEX = config.getFolio().getBarcodeRegex();
        BARCODE_PATTERN = Pattern.compile(BARCODE_REGEX);

        this.folioConnection = new FolioConnection(config);

        service.addLoanService(this);
        log.info("FolioLoanService started.");
    }

    @Override
    public boolean handlesBarcode(String barcode) {
        Matcher matcher = BARCODE_PATTERN.matcher(barcode);
        return matcher.matches();
    }

    @Override
    public ReturnedItem getReturnedItem(String barcode) throws LoanServiceException {
        if (barcode == null) {
            log.error("Cannot find match, no barcode");
            throw new LoanServiceException("Cannot find match, no barcode");
        }

        log.debug("Looking for match with " + barcode);

        String url = config.getFolio().getOkapiBaseUrl() + INSTANCES_PATH;
        String queryString = "(identifiers =/@value '" + barcode + "')";
        JSONObject responseObject;
        try {
            responseObject = folioConnection.executeGet(url, queryString);
        }
        catch (Exception e) {
            throw new LoanServiceException("Cannot search for barcode " + barcode);
        }

        if (responseObject.getInt("totalRecords") == 0) {
            throw new ItemNotFoundException("No match found for barcode " + barcode);
        }

        ReturnedItem item = new ReturnedItem();
        item.setBarcode(barcode);
        item.setTitle(responseObject.query("/instances/0/title").toString());
        return item;
    }

}
