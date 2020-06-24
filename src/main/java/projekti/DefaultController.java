package projekti;

import java.io.IOException;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
