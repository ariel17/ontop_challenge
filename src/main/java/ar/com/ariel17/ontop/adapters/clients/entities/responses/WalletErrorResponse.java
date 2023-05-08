package ar.com.ariel17.ontop.adapters.clients.entities.responses;

/**
 * Wallet API error response body.
 * @param code The error code.
 * @param message The error description.
 */
public record WalletErrorResponse(String code, String message) {
}
