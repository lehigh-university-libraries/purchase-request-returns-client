package edu.lehigh.libraries.purchase_request.returns_client.reshare;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReShareReturnedItemRepository extends JpaRepository<ReShareLoanRecord, Long> {
    
}
