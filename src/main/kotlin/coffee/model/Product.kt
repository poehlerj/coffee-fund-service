package coffee.model

import javax.persistence.Entity

/**
 *
 * @author Jonas P&ouml;hler
 *
 * @since coffee-fund 1.0
 */
@Entity
class Product : BaseModel() {

    var name: String = ""

    var price: Double = 0.0

}
