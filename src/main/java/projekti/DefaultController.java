package projekti;

import java.util.ArrayList;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class DefaultController {

    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @ModelAttribute
    private Account getAccount() {
        return new Account();
    }
    
    @GetMapping("*")
    public String helloWorld(Model model) {
        model.addAttribute("message", "World!");
        return "index";
    }
    
    @GetMapping("/registration")
    public String getReg() {
        return "form";
    }
    
    @PostMapping("/registration")
    public String createAcc(
            @Valid @ModelAttribute Account account,
            BindingResult bindingResult,
            Model model) {
        
        if(bindingResult.hasErrors()) {
            return "registration";
        }
        
        ArrayList<String> authorities = new ArrayList<String>();
        authorities.add("USER");
        account.setAuthorities(authorities);
        String password = account.getPassword();
        account.setPassword(passwordEncoder.encode(password));
        
        
        accountRepository.save(account);
        return "redirect:/success";
    }
}
