package projekti;

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

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private AccountRepository accountRepository;
    
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
    
    public void create(Account account) {
        ArrayList<String> authorities = new ArrayList<String>();
        authorities.add("USER");
        account.setAuthorities(authorities);
        String password = account.getPassword();
        account.setPassword(passwordEncoder.encode(password));
        
        accountRepository.save(account);
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
    
    public String getLoggedInName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        if (username == "anonymousUser") { return "notLoggedIn"; }
        String name = accountRepository.findByUsername(username).getFirstNameLastName();
        return name;
    }
    
    public String sendReq(Long accountId, String username) {        

        Account loggedInAcc = accountRepository.findByUsername(username);
        List<Account> reqs = accountRepository.getOne(accountId).getConnectionRequest();
        
        if (Objects.equals(accountId, loggedInAcc.getId())) {
            return "You can't add yourself.";
        }
        
        if (reqs.contains(loggedInAcc)) {
            return "Request already sent.";
        }
        reqs.add(loggedInAcc);
        Account targetAcc = accountRepository.getOne(accountId);
        targetAcc.setConnectionRequest(reqs);
        accountRepository.save(targetAcc);
        return "Request sent.";
    }
}
