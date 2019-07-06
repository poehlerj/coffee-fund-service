package coffee.service

import coffee.model.AuthenticationType
import coffee.model.Product
import coffee.model.Purchase
import coffee.model.User
import coffee.model.query.QProduct
import coffee.model.query.QPurchase
import coffee.model.query.QUser
import io.ebean.Ebean
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.*
import io.ktor.auth.ldap.ldapAuthenticate
import io.ktor.features.ContentNegotiation
import io.ktor.html.respondHtml
import io.ktor.http.auth.HeaderValueEncoding
import io.ktor.http.content.resource
import io.ktor.http.content.static
import io.ktor.http.httpDateFormat
import io.ktor.jackson.jackson
import io.ktor.request.*
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.html.*
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

/**
 *
 * @author Jonas P&ouml;hler
 *
 * @since coffee-fund 1.0
 */
fun main() {
    Ebean.getDefaultServer()

    val bier = Product()
    bier.price = 1.0
    bier.name = "Bier"
    bier.save()

    val kaffee = Product()
    kaffee.price = 0.25
    kaffee.name = "Kaffee"
    kaffee.save()

    val spezi = Product()
    spezi.price = 0.75
    spezi.name = "Spezi"
    spezi.save()

    val jonas = User()
    jonas.name = "poehler"
    jonas.authenticationType = AuthenticationType.LDAP
    jonas.save()

    val testUser = User()
    testUser.name = "testUser"
    testUser.authenticationType = AuthenticationType.INTERNAL
    testUser.password = "superSecretCow"
    testUser.save()


    embeddedServer(Netty, port = 8081, host = "127.0.0.1") {
        install(ContentNegotiation) {
            jackson {

            }
        }

        install(Authentication) {
            basic(name = "coffeeAuth") {
                realm = "FSinfo SSO"
                validate { credentials ->
                    val user = QUser().name.equalTo(credentials.name).findOne()
                    if (user != null) {
                        when (user.authenticationType) {
                            AuthenticationType.INTERNAL -> internalAuthenticate(user, credentials)
                            AuthenticationType.LDAP -> ldapAuthenticate(
                                credentials,
                                "ldap://localhost:1389",
                                "uid=%s,ou=users,dc=fsinfo,dc=fim,dc=uni-passau,dc=de"
                            )
                        }
                    } else {
                        null
                    }
                }
            }
        }

        routing {
            get("/") {
                call.respondHtml {
                    head {
                        title("This is your Coffee service!")
                    }
                    body {
                        text("nothing to see here")
                        script(src = "/static/coffee-fund.js") {}
                    }
                }
            }

            authenticate("coffeeAuth") {
                get("/prices") {
                    val type = context.request.queryParameters["type"]

                    val products = QProduct().findList()

                    if ("json" == type) {
                        call.respond(products)
                    } else {
                        call.respondHtml {
                            head {
                                title("Prices")
                            }
                            body {
                                table {
                                    thead {
                                        tr {
                                            th {
                                                text("Wat?")
                                            }
                                            th {
                                                text("Price")
                                            }
                                        }
                                    }

                                    tbody {
                                        for (product in products) {
                                            tr {
                                                td {
                                                    text(product.name)
                                                }
                                                td {
                                                    text(product.price)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                post("/buy/") {
                    val buy = call.receive<Buy>()

                    val currentUser =
                        QUser().name.equalTo(call.authentication.principal<UserIdPrincipal>()!!.name).findOne()!!
                    val product = QProduct().name.equalTo(buy.productName).findOne()!!

                    val purchase = Purchase(currentUser, product)
                    purchase.quantity = buy.quantity
                    purchase.save()
                    call.respond("OK")
                }
                get("/purchases") {
                    val accept = call.request.accept()
                    val purchases = QPurchase().findList()
                    if (accept != null && accept.contains("application/json")) {
                        call.respond(purchases)
                    } else {
                        call.respondHtml {
                            head {
                                title("Prices")
                            }
                            body {
                                table {
                                    thead {
                                        tr {
                                            th {
                                                text("Wat?")
                                            }
                                            th {
                                                text("Wia vui?")
                                            }
                                            th {
                                                text("Einzelpreis")
                                            }
                                            th {
                                                text("Gesamt")
                                            }
                                            th {
                                                text("Wann?")
                                            }
                                        }
                                    }

                                    tbody {
                                        for (purchase in purchases) {
                                            tr {
                                                td {
                                                    text(purchase.product.name)
                                                }
                                                td {
                                                    text(purchase.quantity)
                                                }
                                                td {
                                                    text(purchase.product.price)
                                                }
                                                td {
                                                    text(purchase.product.price * purchase.quantity)
                                                }
                                                td {
                                                    text(
                                                        DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                                                            .withLocale(Locale.GERMANY)
                                                            .withZone(ZoneId.systemDefault()).format(
                                                                purchase.whenPurchased
                                                            )
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            static("/static") {
                resource("coffee-fund.js")
            }
        }
    }.start(wait = true)
}

class Buy {

    var productName: String = ""
    val quantity: Int = 1

}

private fun internalAuthenticate(
    user: User,
    credentials: UserPasswordCredential
): UserIdPrincipal? {
    return if (user.password.equals(credentials.password)) {
        UserIdPrincipal(credentials.name)
    } else {
        null
    }
}