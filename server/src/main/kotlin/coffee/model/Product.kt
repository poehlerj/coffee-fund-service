package coffee.model

import io.ebean.annotation.Index
import javax.persistence.Entity

/**
 *
 * @author Jonas P&ouml;hler
 *
 * @since coffee-fund 1.0
 */
@Entity
class Product : BaseModel() {

    @Index(unique = true)
    var name: String = ""

    var price: Double = 0.0

}
