package coffee.common

/**
 *
 * @author Jonas P&ouml;hler
 *
 * @since coffee-fund 1.0
 */
class PurchaseData(
    val productName: String,
    val quantity: Int = 1
) : CommunicationData()