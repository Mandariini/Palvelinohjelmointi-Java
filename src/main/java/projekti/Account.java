package projekti;

import java.util.List;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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

}
