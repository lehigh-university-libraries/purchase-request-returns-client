package edu.lehigh.libraries.purchase_request.returns_client;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class ReturnsController {

	@GetMapping("/")
	public String welcomeSimple(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
		log.debug("called welcomeSimple");
		model.addAttribute("name", name);
		return "index";
	}

}
