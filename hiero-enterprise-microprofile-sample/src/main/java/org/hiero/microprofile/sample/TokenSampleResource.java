package org.hiero.microprofile.sample;

import com.hedera.hashgraph.sdk.TokenId;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.hiero.base.FungibleTokenClient;
import org.hiero.base.HieroException;

/**
 * Sample JAX-RS resource demonstrating how to use {@link FungibleTokenClient}
 * in a MicroProfile /
 * Quarkus application.
 *
 * <p>
 * This sample shows:
 * <ul>
 * <li>Creating a fungible token</li>
 * <li>Minting additional supply</li>
 * <li>Transferring tokens between accounts</li>
 * </ul>
 *
 * <p>
 * The {@link FungibleTokenClient} CDI bean is automatically provided by the
 * hiero-enterprise-microprofile module. Inject it directly — no manual setup
 * required.
 */
@Path("/tokens")
@Produces(MediaType.TEXT_PLAIN)
public class TokenSampleResource {

    private final FungibleTokenClient tokenClient;

    @Inject
    public TokenSampleResource(final FungibleTokenClient tokenClient) {
        this.tokenClient = tokenClient;
    }

    /**
     * Creates a new fungible token on the Hiero network.
     *
     * <p>
     * The operator account acts as both treasury and supply account.
     *
     * <p>
     * Example: POST /tokens?name=MyToken&symbol=MTK
     *
     * @param name   the full name of the token
     * @param symbol the short symbol of the token
     * @return the ID of the newly created token
     */
    @POST
    public String createToken(
            @QueryParam("name") final String name,
            @QueryParam("symbol") final String symbol) {
        try {
            final TokenId tokenId = tokenClient.createToken(name, symbol);
            return "Token created with ID: " + tokenId;
        } catch (final HieroException e) {
            throw new RuntimeException("Failed to create token", e);
        }
    }

    /**
     * Mints additional supply for an existing token.
     *
     * <p>
     * Example: POST /tokens/0.0.12345/mint?amount=1000
     *
     * @param tokenId the ID of the token
     * @param amount  the number of units to mint
     * @return the new total supply after minting
     */
    @POST
    @Path("/{tokenId}/mint")
    public String mintToken(
            @PathParam("tokenId") final String tokenId,
            @QueryParam("amount") final long amount) {
        try {
            final long newSupply = tokenClient.mintToken(TokenId.fromString(tokenId), amount);
            return "Minted " + amount + " units. New total supply: " + newSupply;
        } catch (final HieroException e) {
            throw new RuntimeException("Failed to mint token " + tokenId, e);
        }
    }

    /**
     * Transfers tokens from the operator account to a recipient account.
     *
     * <p>
     * The recipient must already be associated with the token.
     *
     * <p>
     * Example: POST /tokens/0.0.12345/transfer?toAccountId=0.0.67890&amount=100
     *
     * @param tokenId     the ID of the token
     * @param toAccountId the receiving account ID
     * @param amount      the number of token units to transfer
     * @return confirmation string
     */
    @POST
    @Path("/{tokenId}/transfer")
    public String transferToken(
            @PathParam("tokenId") final String tokenId,
            @QueryParam("toAccountId") final String toAccountId,
            @QueryParam("amount") final long amount) {
        try {
            tokenClient.transferToken(TokenId.fromString(tokenId), toAccountId, amount);
            return "Transferred " + amount + " units of token " + tokenId + " to " + toAccountId;
        } catch (final HieroException e) {
            throw new RuntimeException("Failed to transfer token " + tokenId, e);
        }
    }
}