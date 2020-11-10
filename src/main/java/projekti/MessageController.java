package projekti;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MessageController {
    @Autowired
    private CustomUserDetailsService cudservice;
    
    @Autowired
    private MessageService messageService;
    
    @GetMapping("/messages")
    public String getMessages(Model model) {
        Pageable pageable = PageRequest.of(0, 25, Sort.by("messageTime").descending());
        
        Account account = cudservice.getLoggedAcc();
        List<Account> accountsToShow = account.getConnections();
        List<Message> messages = new ArrayList<>();
        accountsToShow.add(account);
        
        for (Message message: messageService.getAll()) {
            if (accountsToShow.contains(message.getUser())) {
                messages.add(message);
            }
        }
        // Viestit aikajärjestykseen
        Collections.sort(messages, (Message m1, Message m2) -> m2.getMessageTime().compareTo(m1.getMessageTime()));
        List<Message> reducedMessages = new ArrayList<>();
        if(messages.size() > 25) {
            reducedMessages = messages.subList(0, 25);
        } else {
            reducedMessages = messages;
        }
        model.addAttribute("messages", reducedMessages);
        return "message";
    }
    
    @PostMapping("/messages")
    public String postMessage(@RequestParam String text) {
        Account acc = cudservice.getLoggedAcc();
        Message message = new Message(acc, text, LocalDateTime.now());
        messageService.createMessage(message);
        return "redirect:/messages";
    }
}
