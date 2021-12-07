package com.tcs.portfolio.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class NavigationController {
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index() {
		return "index";
	}
	
	@RequestMapping(value = "/projects", method = RequestMethod.GET)
	public String projects() {
		return "projects";
	}
	
	@RequestMapping(value = "/resume", method = RequestMethod.GET)
	public String resume() {
		return "resume";
	}
	
	@RequestMapping(value = "/contactInfo", method = RequestMethod.GET)
	public String contactInfo() {
		return "contactInfo";
	}
}
