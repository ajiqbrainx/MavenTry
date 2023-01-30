package com.qbrainx.common.validation.methodvalidation;

import javax.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class MyUserController {

    @GetMapping("/users/{userId}")
    public ResponseEntity<MyUser> getUser(@PathVariable @Pattern(regexp = "\\w{2}\\d{8}") String userId) {
        return ResponseEntity.ok(new MyUser("john"));
    }
}