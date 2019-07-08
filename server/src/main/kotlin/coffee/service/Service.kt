package coffee.service

import coffee.common.PurchaseData
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
import io.ktor.http.content.resource
import io.ktor.http.content.static
import io.ktor.jackson.jackson
import io.ktor.request.accept
import io.ktor.request.receive
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
        install(ContentNegotiation) { jackson { } }

        install(Authentication) {
            basic(name = "coffeeAuth") {
                realm = "FSinfo SSO"
                validate { credentials ->
                    QUser().name.equalTo(credentials.name).findOne()?.let {
                        when (it.authenticationType) {
                            AuthenticationType.INTERNAL -> internalAuthenticate(it, credentials)
                            AuthenticationType.LDAP -> ldapAuthenticate(
                                credentials,
                                "ldap://localhost:1389",
                                "uid=%s,ou=users,dc=fsinfo,dc=fim,dc=uni-passau,dc=de"
                            )
                        }
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
                                script(src = "/static/kotlin.js") {}
                                script(src = "/static/common.js") {}
                                script(src = "/static/web.js") {}
                            }
                        }
                    }
                }
                post("/buy/") {
                    val purchaseData = call.receive<PurchaseData>()

                    val currentUser =
                        QUser().name.equalTo(call.authentication.principal<UserIdPrincipal>()!!.name).findOne()!!
                    val product = QProduct().name.equalTo(purchaseData.productName).findOne()!!

                    val purchase = Purchase(currentUser, product)
                    purchase.quantity = purchaseData.quantity
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
                get("/saldo") {
                    val currentUser =
                        QUser().name.equalTo(call.authentication.principal<UserIdPrincipal>()!!.name).findOne()!!
                    call.respond(QPurchase().user.equalTo(currentUser).findList()
                        .map { it.quantity * it.product.price }
                        .sum())
                }
            }

            static("/static") {
                resource("kotlin.js")
                resource("kotlin.js.map")
                resource("common.js")
                resource("common.js.map")
                resource("web.js")
                resource("web.js.map")
            }
        }
    }.start(wait = true)
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