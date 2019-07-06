package coffee.model

import io.ebean.Ebean
import java.util.*
import javax.persistence.Id
import javax.persistence.MappedSuperclass

/**
 *
 * @author Jonas P&ouml;hler
 *
 * @since coffee-fund 1.0
 */
@MappedSuperclass
abstract class BaseModel {

    @Id
    var id: UUID = UUID.randomUUID()

    fun save() {
        Ebean.save(this)
    }

}