package io.bacta.login.server.auth;

import io.bacta.login.server.auth.model.ForgotPasswordRequest;
import io.bacta.login.server.auth.model.UserRegistrationRequest;
import io.bacta.login.server.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class AuthController {
    private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);
    @Autowired
    private AccountRepository repository;

    @Autowired
    private PasswordEncoder userPasswordEncoder;

    @GetMapping("/register")
    public String register(Model model, String error) {
        model.addAttribute("errorMsg", error);

        return "register";
    }

    @PostMapping("/register")
    public RedirectView register(UserRegistrationRequest request) {
        BactaAccount account = repository.findByUsername(request.getUsername());

        if (account != null) {
            return new RedirectView("/register?error=username-exists");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return new RedirectView("/register?error=password-mismatch");
        }

        final String encodedPassword = userPasswordEncoder.encode(request.getPassword());
        account = new BactaAccount(request.getUsername(), encodedPassword);

        account = repository.save(account);

        LOG.debug("Registered new bacta account (id: {}, username: {})",
                account.getId(),
                account.getUsername());

        return new RedirectView("/register/success");
    }

    @GetMapping("/register/success")
    public String registerSuccess() {
        return "registration-success";
    }

    @GetMapping("/login")
    public String login(Model model, String error, String logout) {
        if (error != null)
            model.addAttribute("errorMsg", "Your username and password are invalid.");

        if (logout != null)
            model.addAttribute("msg", "You have been logged out successfully.");

        return "login";
    }

    @GetMapping("/forgot-password")
    public String forgotPassword(Model model, String success) {
        if (success != null)
            model.addAttribute("success", true);

        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public RedirectView forgotPassword(ForgotPasswordRequest request) {
        return new RedirectView("/forgot-password?success");
    }

    @RequestMapping("/")
    public String dashboard() {
        return "index";
    }
}