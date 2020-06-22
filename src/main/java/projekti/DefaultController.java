package projekti;

import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DefaultController {

    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired CustomUserDetailsService cudservice;
    
    @ModelAttribute
    private Account getAccount() {
        return new Account();
    }
    
    @GetMapping("*")
    public String helloWorld(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/dashboard";
        }

        model.addAttribute("message", "Hello user!");
        return "index";
    }
    
    @GetMapping("/dashboard") 
    public String dashboard(Model model) {
        String name = cudservice.getLoggedInName();

        model.addAttribute("name", "Welcome " + name);
        model.addAttribute("requests", cudservice.getLoggedAcc().getConnectionRequest());
        model.addAttribute("connections", cudservice.getLoggedAcc().getConnections());
        return "dashboard";
    }
    
    @GetMapping("/registration")
    public String getReg() {
        return "form";
    }
    
    @GetMapping("success")
    public String getSuccess() {
        return "success";
    }
    
    @GetMapping("search")
    public String getSearch() {
        return "search";
    }
    
    @PostMapping("/registration")
    public String createAcc(
            @Valid @ModelAttribute Account account,
            BindingResult bindingResult) {
        
        if(bindingResult.hasErrors()) {
            return "registration";
        }
        
        cudservice.create(account);
        return "redirect:/success";
    }
    
    @PostMapping("/search")
    public String search(@RequestParam String searchword, Model model) {
        List<Account> accs = cudservice.search(searchword);

        if (accs == null) { return "search"; }
        model.addAttribute("accounts", accs);
        
        return "search";
    }
    
    @GetMapping("/accounts/{accountProfilename}")
    public String getProfile(@PathVariable String accountProfilename, Model model) {
        Account acc = cudservice.getAccountByProfilename(accountProfilename);
        model.addAttribute("profileName", acc.getFirstNameLastName());
        return "profile";
    }
    
    @PostMapping("/accounts/{accountProfilename}")
    public String sendRequest(@PathVariable String accountProfilename, Model model) {
        String username = cudservice.getLoggedInUsername();
        
        String status = cudservice.sendReq(accountProfilename, username);
        model.addAttribute("status", status);
        return "search";
    }
    
    @PostMapping("/accounts/request/{accountProfilename}")
    public String acceptRequest(@PathVariable String accountProfilename) {
        cudservice.acceptRequest(accountProfilename);
        return "redirect:/dashboard";
    }
}
