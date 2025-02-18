package edu.lehigh.libraries.purchase_request.returns_client.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.lehigh.libraries.purchase_request.returns_client.config.PropertiesConfig;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class PageController {

    private PropertiesConfig config;

    public PageController(PropertiesConfig config) {
        this.config = config;
    }

	@GetMapping("/")
	public String home(String name, Model model) {
		model.addAttribute("name", name);
		if (config.getUi() != null && config.getUi().getFontAwesomeUrl() != null) {
			model.addAttribute("fontawesomeUrl", config.getUi().getFontAwesomeUrl());
		}

		return "index";
	}

}
