package projekti;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private SkillRepository skillRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsername(username);
        if (account == null) {
            throw new UsernameNotFoundException("No such user: " + username);
        }

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (String authority : account.getAuthorities()) {
            authorities.add(new SimpleGrantedAuthority(authority));
        }

        return new org.springframework.security.core.userdetails.User(
        account.getUsername(),
        account.getPassword(),
        true,
        true,
        true,
        true,
        authorities);
    }
    
    public String create(Account account) {
        if (accountRepository.findByUsername(account.getUsername()) != null || accountRepository.findByProfilename(account.getProfilename()) != null) {
            return "Username or profilename is already in use.";
        }
        
        ArrayList<String> authorities = new ArrayList<String>();
        authorities.add("USER");
        account.setAuthorities(authorities);
        String password = account.getPassword();
        account.setPassword(passwordEncoder.encode(password));
        
        accountRepository.save(account);
        return "Registration succesfull.";
    }
    
    public List<Account> search(String word) {
        List<Account> accs = accountRepository.findByProfilenameContainingIgnoreCase(word);
        return accs;
    }
    
    public String getLoggedInUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return username;
    }
    
    public Account getAccountByProfilename(String profileName) {
        Account acc = accountRepository.findByProfilename(profileName);
        return acc;
    }
    
    public Account getLoggedAcc() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Account acc = accountRepository.findByUsername(username);
        return acc;
    }
    
    public String sendReq(String accountProfilename, String username) {        

        Account loggedInAcc = accountRepository.findByUsername(username);        
        Account targetAcc = accountRepository.findByProfilename(accountProfilename);
        
        List<Account> yourReqs = loggedInAcc.getConnectionRequest();
        List<Account> targetReqs = targetAcc.getConnectionRequest();
        List<Account> yourConnections = loggedInAcc.getConnections();
        
        // Tarkistukset voiko pyyntoa lahettaa.
        if (Objects.equals(targetAcc.getId(), loggedInAcc.getId())) {
            return "You can't add yourself.";
        }
        
        if (yourReqs.contains(targetAcc)) {
            return "This person has already sent you a request.";
        }
        
        if (yourConnections.contains(targetAcc)) {
            return "This person is already in your connections";
        }
        
        if (targetReqs.contains(loggedInAcc)) {
            return "Request already sent.";
        }
        targetReqs.add(loggedInAcc);
        targetAcc.setConnectionRequest(targetReqs);
        accountRepository.save(targetAcc);
        return "Request sent.";
    }
    
    public void acceptRequest(String accountProfilename) {
        Account loggedInAcc = getLoggedAcc();
        Account sender = getAccountByProfilename(accountProfilename);
        
        // Poistetaan requests listalta ja lisätään connectioneihin.
        loggedInAcc.getConnectionRequest().remove(sender);
        List<Account> connections = loggedInAcc.getConnections();
        connections.add(sender);
        loggedInAcc.setConnections(connections);
        
        // Lisätään myös lähettäjän connectioneihin.
        List<Account> senderConnections = sender.getConnections();
        senderConnections.add(loggedInAcc);
        sender.setConnections(senderConnections);
        
        accountRepository.save(loggedInAcc);
        accountRepository.save(sender);
    }
    
    public void declineRequest(String accountProfilename) {
        Account loggedInAcc = getLoggedAcc();
        Account sender = getAccountByProfilename(accountProfilename);
        
        // Poistetaan requests listalta.
        loggedInAcc.getConnectionRequest().remove(sender);
        accountRepository.save(loggedInAcc);
    }
    
    public void deleteConnection(String accountProfilename) {
        Account loggedInAcc = getLoggedAcc();
        Account connection = getAccountByProfilename(accountProfilename);
        
        loggedInAcc.getConnections().remove(connection);
        connection.getConnections().remove(loggedInAcc);
        
        accountRepository.save(loggedInAcc);
        accountRepository.save(connection);
    }
    
    public void createProfilePic(MultipartFile file) throws IOException {
        Account acc = getLoggedAcc();
        acc.setProfilepicture(file.getBytes());
        acc.setMediaType(file.getContentType());
        accountRepository.save(acc);
    }
}
