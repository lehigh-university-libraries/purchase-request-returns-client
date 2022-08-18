package edu.lehigh.libraries.purchase_request.returns_client.reshare;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import edu.lehigh.libraries.purchase_request.returns_client.config.PropertiesConfig;
import edu.lehigh.libraries.purchase_request.returns_client.model.ReturnedItem;
import edu.lehigh.libraries.purchase_request.returns_client.service.ItemNotFoundException;
import edu.lehigh.libraries.purchase_request.returns_client.service.LoanService;
import edu.lehigh.libraries.purchase_request.returns_client.service.LoanServiceException;
import edu.lehigh.libraries.purchase_request.returns_client.service.ReturnedItemService;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@ConditionalOnProperty(name="returns-client.reshare.db.host")
public class ReShareLoanService implements LoanService {

    private final Pattern BARCODE_PATTERN;

    @Autowired
    private ReShareReturnedItemRepository returnedItemRepository;

    ReShareLoanService(ReturnedItemService service, PropertiesConfig config) {
        final String BARCODE_REGEX = config.getReShare().getBarcodeRegex();
        BARCODE_PATTERN = Pattern.compile(BARCODE_REGEX);

        service.addLoanService(this);
        log.info("ReShareLoanService started.");
    }

    @Override
    public boolean handlesBarcode(String barcode) {
        Matcher matcher = BARCODE_PATTERN.matcher(barcode);
        return matcher.matches();
    }

    @Override
    public ReturnedItem getReturnedItem(String barcode) throws LoanServiceException {
        ExampleMatcher barcodeMatcher = ExampleMatcher.matchingAll()
            .withMatcher("barcode", ExampleMatcher.GenericPropertyMatchers.exact());
        Example<ReShareLoanRecord> example = Example.of(ReShareLoanRecord.fromBarcode(barcode), barcodeMatcher);
        ReShareLoanRecord record = returnedItemRepository.findOne(example)
            .orElseThrow(() -> new ItemNotFoundException("Could not get book data from ReShare."));

        ReturnedItem item = record.toReturnedItem();
        return item;
    }

}