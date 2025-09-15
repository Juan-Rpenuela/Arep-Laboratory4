package edu.escuelaing.arep.app.microspringboot.controllers;

import edu.escuelaing.arep.app.microspringboot.annotations.GetMapping;
import edu.escuelaing.arep.app.microspringboot.annotations.RequestParam;
import edu.escuelaing.arep.app.microspringboot.annotations.RestController;



@RestController
public class AppController {

	@GetMapping("/greeting")
	public static String greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
		return "Hola "+ name;
	}

	@GetMapping("/")
	public static String index() {
		return "Welcome to the home page!";
	}

}
 