package projekti;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Skill extends AbstractPersistable<Long>{
    
    private String text;
    
    @ManyToOne
    private Account ownerAccount;
    
    @ManyToMany
    @JoinTable(name="voters", 
            joinColumns = { @JoinColumn(name = "voterId")}, 
            inverseJoinColumns={@JoinColumn(name="skillId")})
    private List<Account> voters = new ArrayList<>();
}
