package com.screenprog.application.controller;

import com.screenprog.application.model.*;
import com.screenprog.application.service.CenteralisedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class AdminController {

    final private CenteralisedService service;
    final private Logger LOGGER = LoggerFactory.getLogger(AdminController.class);
    public AdminController(CenteralisedService service) {
        this.service = service;
    }

    @PostMapping("login")
    private ResponseEntity<Verified> login(@RequestBody Users user, UriComponentsBuilder uriBuilder){
        /*TODO: build uri based on user role and embed it into response entity header*/
        String token = service.verify(user);
        String path = null;
        if(token != null)
            path = service.getPath(user);
        var verified = new Verified(token, path);

        return ResponseEntity.ok(verified);

    }

    @GetMapping("/hello")
    public ResponseEntity<Map<String, String>> hello(@RequestParam String name){
        Map<String, String> map = new HashMap<>();
        String replace = name.replace(name.charAt(0), String.valueOf(name.charAt(0)).toUpperCase().charAt(0));

        map.put("msg", "Hello, " + replace);
        return ResponseEntity.ok(map);
    }

    @PostMapping("register")
    public ResponseEntity<String> adminRegister(@RequestBody Users user){
        LOGGER.info("Got into admin register");
        try {
            return ResponseEntity.ok(service.register(user).toString());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Already registered!");
        }
    }

    @PostMapping("/add-one-staff")
    public ResponseEntity<String> addStaff(@RequestBody Staff staff, UriComponentsBuilder ucb){
        try {
            Staff staff1 = service.addStaff(staff);
            URI locationOfStaff = ucb.path("admin/get-staff/{id}")
                    .buildAndExpand(staff1.getStaffId().toString())
                    .toUri();
            return ResponseEntity.created(locationOfStaff).body("Created successfully");
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @GetMapping("get-staff/{id}")
    public ResponseEntity<Staff> getStaff(@PathVariable Long id){
        Staff staff = service.getStaff(id);
        if(staff == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(staff);
    }

    @GetMapping("/get-all-staff")
    public ResponseEntity<List<Staff>> getAllStaff(){
        return ResponseEntity.ok(service.getAllStaff());
    }


}
