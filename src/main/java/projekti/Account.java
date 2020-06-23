package projekti;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account extends AbstractPersistable<Long> {

    @Size(min = 4, max = 15)
    private String profilename;
    
    @Size(min = 4, max = 15)
    private String username;
    
    @Size(min = 4)
    private String firstNameLastName;
    
    @Size(min = 4)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> authorities;
    
    @ManyToMany
    @JoinTable(name = "connections", 
            joinColumns = { @JoinColumn(name = "target_id")}, 
            inverseJoinColumns={@JoinColumn(name="sender_id")})
    private List<Account> connections = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "connectionRequests", 
            joinColumns = { @JoinColumn(name = "target_id")}, 
            inverseJoinColumns={@JoinColumn(name="sender_id")})
    private List<Account> connectionRequest = new ArrayList<>();
    
    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] profilepicture;
    
    private String mediaType;
    
    @OneToMany(mappedBy = "ownerAccount")
    private List<Skill> skills = new ArrayList<>();
    

}
