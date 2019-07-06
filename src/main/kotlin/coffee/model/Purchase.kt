package coffee.model

import io.ebean.annotation.WhenCreated
import java.time.Instant
import javax.persistence.Entity
import javax.persistence.ManyToOne

/**
 *
 * @author Jonas P&ouml;hler
 *
 * @since coffee-fund 1.0
 */
@Entity
class Purchase(user: User, product: Product) : BaseModel() {

    @ManyToOne
    var user: User = user

    @ManyToOne
    var product: Product = product

    var quantity: Int = 1

    @WhenCreated
    lateinit var whenPurchased: Instant

}