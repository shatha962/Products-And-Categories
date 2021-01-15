package com.axsos.shatha.controllers;

import com.axsos.shatha.models.User;
import com.axsos.shatha.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
public class Users {
    private final UserService userService;

    public Users(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping("/registration")
    public String registerForm(@ModelAttribute("user") User user) {
        return "registrationPage.jsp";
    }
    @RequestMapping("/login")
    public String login() {
        return "loginPage.jsp";
    }

    @RequestMapping(value="/registration", method= RequestMethod.POST)
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result, HttpSession session) {
        // if result has errors, return the registration page (don't worry about validations just now)
        // else, save the user in the database, save the user id in session, and redirect them to the /home route
        if (result.hasErrors()) {
            return "registrationPage.jsp";
        }
        else{
            User userNEW = userService.registerUser(user);
            session.setAttribute("userid",userNEW.getId());
            return "redirect:/home";

        }


    }

    @RequestMapping(value="/login", method=RequestMethod.POST)
    public String loginUser(@RequestParam("email") String email, @RequestParam("password") String password, Model model, HttpSession session) {
        boolean authenticated = userService.authenticateUser(email, password);
    if(authenticated){
        User u = userService.findByEmail(email);
        session.setAttribute("userid",u.getId());
        return "redirect:/home";
    }
    else {
        model.addAttribute("error","Invalid email!");
        return "loginPage.jsp";
    }
    }

    @RequestMapping("/home")
    public String home(HttpSession session, Model model) {
        // get user from session, save them in the model and return the home page
        Long userid = (Long) session.getAttribute("userid");
        User user = userService.findUserById(userid);
        model.addAttribute("user",user);
        return "homePage.jsp";
    }
    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        // invalidate session
        // redirect to login page
        session.invalidate();
        return "redirect:/login";
    }
}