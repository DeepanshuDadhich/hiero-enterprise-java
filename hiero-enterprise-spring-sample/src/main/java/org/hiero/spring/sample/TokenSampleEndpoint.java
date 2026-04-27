package org.hiero.spring.sample;

import com.hedera.hashgraph.sdk.TokenId;
import java.util.Objects;
import org.hiero.base.FungibleTokenClient;
import org.hiero.base.HieroException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Sample REST controller demonstrating how to use {@link FungibleTokenClient}
 * in a Spring Boot
 * application.
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
 * The {@link FungibleTokenClient} bean is automatically provided by the
 * hiero-enterprise-spring
 * module via auto-configuration. No manual wiring is needed.
 */
@RestController
@RequestMapping("/tokens")
public class TokenSampleEndpoint {

    private final FungibleTokenClient tokenClient;

    public TokenSampleEndpoint(final FungibleTokenClient tokenClient) {
        this.tokenClient = Objects.requireNonNull(tokenClient, "tokenClient must not be null");
    }

    /**
     * Creates a new fungible token on the Hiero network.
     *
     * <p>
     * The operator account is used as both the treasury and supply account.
     *
     * <p>
     * Example: POST /tokens?name=MyToken&symbol=MTK
     *
     * @param name   the full name of the token (e.g. "MyToken")
     * @param symbol the short symbol of the token (e.g. "MTK")
     * @return the ID of the newly created token
     */
    @PostMapping
    public String createToken(
            @RequestParam final String name,
            @RequestParam final String symbol) {
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
     * The operator account must be the supply key holder.
     *
     * <p>
     * Example: POST /tokens/0.0.12345/mint?amount=1000
     *
     * @param tokenId the ID of the token to mint
     * @param amount  the number of units to mint
     * @return the new total supply after minting
     */
    @PostMapping("/{tokenId}/mint")
    public String mintToken(
            @RequestParam final String tokenId,
            @RequestParam final long amount) {
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
     * The recipient account must already be associated with the token before this
     * call.
     *
     * <p>
     * Example: POST /tokens/0.0.12345/transfer?toAccountId=0.0.67890&amount=100
     *
     * @param tokenId     the ID of the token to transfer
     * @param toAccountId the receiving account ID
     * @param amount      the number of token units to transfer
     * @return confirmation string
     */
    @PostMapping("/{tokenId}/transfer")
    public String transferToken(
            @RequestParam final String tokenId,
            @RequestParam final String toAccountId,
            @RequestParam final long amount) {
        try {
            tokenClient.transferToken(TokenId.fromString(tokenId), toAccountId, amount);
            return "Transferred " + amount + " units of token " + tokenId + " to account " + toAccountId;
        } catch (final HieroException e) {
            throw new RuntimeException("Failed to transfer token " + tokenId, e);
        }
    }
}
