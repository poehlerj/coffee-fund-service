package coffee.model

import io.ebean.annotation.Index
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated

/**
 *
 * @author Jonas P&ouml;hler
 *
 * @since coffee-fund 1.0
 */
@Entity
class User : BaseModel() {

    @Index(unique = true)
    var name: String = ""

    @Enumerated(EnumType.STRING)
    var authenticationType: AuthenticationType = AuthenticationType.INTERNAL

    var password: String? = null

}

enum class AuthenticationType {
    INTERNAL,
    LDAP
}

