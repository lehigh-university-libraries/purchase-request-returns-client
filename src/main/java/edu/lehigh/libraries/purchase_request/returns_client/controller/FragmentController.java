package edu.lehigh.libraries.purchase_request.returns_client.controller;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import edu.lehigh.libraries.purchase_request.model.PurchaseRequest;
import edu.lehigh.libraries.purchase_request.returns_client.service.WorkflowService;
import lombok.extern.slf4j.Slf4j;

@Controller
@ConditionalOnWebApplication
@Slf4j
public class FragmentController {
    
    private WorkflowService workflowService;

    public FragmentController(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    @GetMapping("/requestHistory")
	public String historyFragment(Model model, Authentication authentication) {
        log.info("Request: GET /requestHistory");
        String reporterName = authentication.getName();
        List<PurchaseRequest> history = workflowService.getHistory(reporterName);
        model.addAttribute("history", history);
		return "fragments/history";
	}

}
