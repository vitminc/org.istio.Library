package org.istio.library.controller;

import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.IDToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@Controller
public class LibraryController {

    private final HttpServletRequest request;

    @Autowired
    LibraryAccess libraryAccess;

    public LibraryController(HttpServletRequest request) {
        this.request = request;
    }

    @GetMapping(value = "/")
    public String getHome(Model model) {
        configCommonAttributes(model);
        return "index";
    }

    @RequestMapping(value = {"/search", "/search/{term}"}, method = RequestMethod.GET)
    public String checkOrderStatus(Model model, @PathVariable("term") Optional<String> term) {
        configCommonAttributes(model);
        List<org.istio.library.model.Book> resultList = libraryAccess.findBooks(term.isPresent() ? term.get() : "");
        model.addAttribute("books", resultList);
        return "search";
    }

    @GetMapping(value = "/books")
    public String getBooks(Model model) {
        configCommonAttributes(model);
        model.addAttribute("books", libraryAccess.findBooks(""));
        return "books";
    }

    @GetMapping(value = "/manager")
    public String getManager(Model model) {
        configCommonAttributes(model);
        model.addAttribute("books", libraryAccess.findBooks(""));
        return "manager";
    }

    @GetMapping(value = "/logout")
    public String logout() throws ServletException {
        request.logout();
        return "redirect:/";
    }

    private void configCommonAttributes(Model model) {
        KeycloakSecurityContext keycloakSecurityContext = getKeycloakSecurityContext();
        if (keycloakSecurityContext!=null) {
            String name = keycloakSecurityContext.getIdToken().getPreferredUsername();
            model.addAttribute("name", name);
        }
    }

    private KeycloakSecurityContext getKeycloakSecurityContext() {
        return (KeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());
    }
}
