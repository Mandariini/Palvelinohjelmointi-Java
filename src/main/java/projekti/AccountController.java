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
public class AccountController {
    
    @Autowired 
    CustomUserDetailsService cudservice;
    
    @Autowired 
    SkillService skillService;
    
    @Transactional
    @GetMapping("/dashboard") 
    public String dashboard(Model model) {
        Account acc = cudservice.getLoggedAcc();
        String name = acc.getFirstNameLastName();
        String profilename = acc.getProfilename();
        Pageable pageable = PageRequest.of(0, 10, Sort.by("sizeOfVoters").descending());

        List<Skill> skills = skillService.getByOwnerAccSorted(acc, pageable);

        model.addAttribute("name", "Welcome " + name);
        model.addAttribute("requests", acc.getConnectionRequest());
        model.addAttribute("connections", acc.getConnections());
        model.addAttribute("profilename", profilename);
        model.addAttribute("skills", skills);
        return "dashboard";
    }
    
    @PostMapping("/registration")
    public String createAcc(
            @Valid @ModelAttribute Account account,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        
        if(bindingResult.hasErrors()) {
            String message = "Check your user information and try again.";
            redirectAttributes.addFlashAttribute("errormessage", message);
            return "redirect:/registration";
        }
        
        cudservice.create(account);
        return "redirect:/success";
    }
    
    @GetMapping(path="/accounts/{accountProfilename}")
    public String getProfile(@PathVariable String accountProfilename, Model model) {
        Account acc = cudservice.getAccountByProfilename(accountProfilename);
        
        Pageable pageable = PageRequest.of(0, 10, Sort.by("sizeOfVoters").descending());

        List<Skill> skills = skillService.getByOwnerAccSorted(acc, pageable);

        model.addAttribute("name", acc.getFirstNameLastName());
        model.addAttribute("profilename", acc.getProfilename());
        model.addAttribute("skills", skills);
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
        cudservice.createProfilePic(file);
        
        return "redirect:/dashboard";
    }
    
    @PostMapping("/skill")
    public String addSkill(@RequestParam String skilltext) {
        skillService.addSkill(skilltext);
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
