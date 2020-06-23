package projekti;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SkillService {
    
    @Autowired
    private SkillRepository skillRepository;
    
    @Autowired
    private CustomUserDetailsService cudservice;
    
    public String givePraise(Long skillId) {
        Account acc = cudservice.getLoggedAcc();
        Skill skill = skillRepository.getOne(skillId);
        
        if (skill.getVoters().contains(acc)) {
            return "You have already given praise for this skill.";
        }
        List<Account> voters = skill.getVoters();
        voters.add(acc);
        skill.setVoters(voters);
        skillRepository.save(skill);
        return "Praise succesfully given.";
    }
}
