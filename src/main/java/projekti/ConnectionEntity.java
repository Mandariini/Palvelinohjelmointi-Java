package projekti;
/*
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConnectionEntity extends AbstractPersistable<Long>{
    
    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    Account accountSender;
    
    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    Account accountTarget;
}
