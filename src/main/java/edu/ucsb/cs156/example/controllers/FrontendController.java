package edu.ucsb.cs156.example.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import edu.ucsb.cs156.example.services.WiremockService;
import wiremock.org.checkerframework.checker.units.qual.A;

@Profile("!development")
@Controller
public class FrontendController {

  @Autowired
  WiremockService wiremockService;

  @GetMapping("/**/{path:[^\\.]*}")
  public String index() {
    return "forward:/index.html";
  }

  @GetMapping("/csrf")
  public ResponseEntity<String> csrf() {
    return ResponseEntity.notFound().build();
  }

}
