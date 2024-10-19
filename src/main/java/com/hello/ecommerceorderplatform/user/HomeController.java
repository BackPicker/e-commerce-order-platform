package com.hello.ecommerceorderplatform.user;

import com.hello.ecommerceorderplatform.user.security.UserDetailsImpl;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home(Model model,
                       @AuthenticationPrincipal
                       UserDetailsImpl userDetails) {
        String username = userDetails.getUsername();
        model.addAttribute("username", username);


        return "ok";
    }
}