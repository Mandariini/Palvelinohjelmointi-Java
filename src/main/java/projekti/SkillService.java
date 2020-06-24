package projekti;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class SkillService {
    
    @Autowired
    private SkillRepository skillRepository;
    
    @Autowired
    private AccountRepository accountRepository;
    
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
        int votersSize = skill.getSizeOfVoters() + 1;
        skill.setSizeOfVoters(votersSize);
        skillRepository.save(skill);
        return "Praise succesfully given.";
    }
    
    public List<Skill> getByOwnerAccSorted(Account acc, Pageable pageable) {
        List<Skill> accs = skillRepository.findByOwnerAccount(acc, pageable);
        return accs;
    }
    
    public void addSkill(String text) {
        Account acc = cudservice.getLoggedAcc();
        Skill newSkill = new Skill(text, acc, new ArrayList<>(), 0);
        List<Skill> skills = acc.getSkills();
        skills.add(newSkill);
        
        skillRepository.save(newSkill);
        accountRepository.save(acc);
    }
}
