package com.recipe.backend;

import com.recipe.backend.domain.recipe.repository.RecipeRepository;
import com.recipe.backend.domain.user.dto.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/")
public class HomeController {
    private final RecipeRepository recipeRepository;

    @GetMapping
    public String home(@ModelAttribute("loginResponse") LoginResponse loginResponse, Model model) {
        if (loginResponse != null && loginResponse.getUsername() != null && loginResponse.getToken() != null) {
            model.addAttribute("username", loginResponse.getUsername());
            model.addAttribute("token", loginResponse.getToken());
        }
        System.out.println(loginResponse.getUsername());
        System.out.println(loginResponse.getToken());
        return "home";
    }
}
