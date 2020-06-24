package projekti;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Service
public class MessageService {
    
    @Autowired
    private MessageRepository messageRepository;
    
    public void createMessage(Message message) {
        messageRepository.save(message);
    }
    
    public List<Message> getAll() {
        return messageRepository.findAll();
    }
}
