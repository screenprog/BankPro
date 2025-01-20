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
/**
 * Controller for admin, this controller is only accessible by admin.
 * Login is accessible by everyone
 * @since November, 2024
 * @author Asim Ansari
 * */

@RestController
@RequestMapping("/admin")
public class AdminController {

    final private CenteralisedService service;
    final private Logger LOGGER = LoggerFactory.getLogger(AdminController.class);
    public AdminController(CenteralisedService service) {
        this.service = service;
    }

    /**
     * This method is accessible by everyone
     * It's purpose is to log in the user
     * @param user User object, username and password
     * @return Verified object with token and path
     * */
    @PostMapping("login")
    private ResponseEntity<Verified> login(@RequestBody Users user){
        String token = service.verify(user);
        String path = null;
        if(token != null)
            path = service.getPath(user);
        var verified = new Verified(token, path);

        return ResponseEntity.ok(verified);

    }


    /**
     * This method is accessible by admin only
     * @param name Name of the user
     * @deprecated This method is not used anymore, It was intended for testing
     * */
    @Deprecated
    @GetMapping("/hello")
    public ResponseEntity<Map<String, String>> hello(@RequestParam String name){
        Map<String, String> map = new HashMap<>();
        String replace = name.replace(name.charAt(0), String.valueOf(name.charAt(0)).toUpperCase().charAt(0));

        map.put("msg", "Hello, " + replace);
        return ResponseEntity.ok(map);
    }

    /**
     * This method is accessible by admin only
     * @param user User object with username, password and roles
     * @return String message
     * */
    @PostMapping("register")
    public ResponseEntity<String> adminRegister(@RequestBody Users user){
        LOGGER.info("Got into admin register");
        try {
            return ResponseEntity.ok(service.register(user).toString());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Already registered!");
        }
    }

    /**
     * @param staff Staff object
     * @return String message 'Created successfully' */
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
