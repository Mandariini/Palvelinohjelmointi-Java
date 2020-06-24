package projekti;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DefaultController {
    
    @Autowired 
    CustomUserDetailsService cudservice;
    
    @Autowired 
    SkillService skillService;
    
    
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
    
    @PostMapping("/search")
    public String search(@RequestParam String searchword, Model model) {
        List<Account> accs = cudservice.search(searchword);

        if (accs == null) { return "search"; }
        model.addAttribute("accounts", accs);
        
        return "search";
    }
}
