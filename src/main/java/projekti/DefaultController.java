package projekti;

import java.io.IOException;
import java.util.List;
import javax.tools.FileObject;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Controller;
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
    private AccountRepository accountRepository;
    
    @Autowired CustomUserDetailsService cudservice;
    
    @Autowired SkillService skillService;
    
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
        String profilename = cudservice.getLoggedAcc().getProfilename();
        
        model.addAttribute("name", "Welcome " + name);
        model.addAttribute("requests", cudservice.getLoggedAcc().getConnectionRequest());
        model.addAttribute("connections", cudservice.getLoggedAcc().getConnections());
        model.addAttribute("profilename", profilename);
        model.addAttribute("skills", cudservice.getLoggedAcc().getSkills());
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
    
    @GetMapping(path="/accounts/{accountProfilename}")
    public String getProfile(@PathVariable String accountProfilename, Model model) {
        System.out.println(accountProfilename);
        Account acc = cudservice.getAccountByProfilename(accountProfilename);
        
        model.addAttribute("name", acc.getFirstNameLastName());
        model.addAttribute("profilename", acc.getProfilename());
        model.addAttribute("skills", acc.getSkills());
        return "profile";
    }
    
    @GetMapping("/accounts/{accountProfilename}/image")
    public ResponseEntity<byte[]> viewFile(@PathVariable(value = "accountProfilename") String accountProfileName) {
        Account acc = cudservice.getAccountByProfilename(accountProfileName);

        final HttpHeaders headers = new HttpHeaders();
        
        if (acc.getProfilepicture() == null) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        
        headers.setContentType(MediaType.parseMediaType(acc.getMediaType()));
        return new ResponseEntity(acc.getProfilepicture(), headers, HttpStatus.CREATED);
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
    
    @DeleteMapping("/accounts/request/{accountProfilename}")
    public String declineRequest(@PathVariable String accountProfilename) {
        cudservice.declineRequest(accountProfilename);
        return "redirect:/dashboard";
    }
    
    @DeleteMapping("/accounts/{accountProfilename}")
    public String deleteConnection(@PathVariable String accountProfilename) {
        cudservice.deleteConnection(accountProfilename);
        return "redirect:/dashboard";
    }
    
    @PostMapping("/files")
    public String create(@RequestParam("file") MultipartFile file) throws IOException {
        Account acc = cudservice.getLoggedAcc();
        acc.setProfilepicture(file.getBytes());
        acc.setMediaType(file.getContentType());
        accountRepository.save(acc);
        
        return "redirect:/dashboard";
    }
    
    @PostMapping("/skill")
    public String addSkill(@RequestParam String skilltext) {
        cudservice.addSkill(skilltext);
        return "redirect:/dashboard";
    }
    
    @PostMapping("accounts/{profilename}/praise/{id}")
    public String givePraise(
            @PathVariable(value = "profilename") String profilename,
            @PathVariable(value = "id") Long skillId,
            RedirectAttributes redirectAttributes) {
        String message = skillService.givePraise(skillId);
        redirectAttributes.addFlashAttribute("message", message);
        String url = "redirect:/accounts/"+profilename;
        return url;
    }
}
