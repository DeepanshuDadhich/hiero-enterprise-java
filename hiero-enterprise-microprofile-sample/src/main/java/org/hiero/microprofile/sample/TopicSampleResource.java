package org.hiero.microprofile.sample;

import com.hedera.hashgraph.sdk.TopicId;
import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.hiero.base.HieroException;
import org.hiero.base.TopicClient;

/**
 * Sample JAX-RS resource demonstrating how to use {@link TopicClient} in a
 * MicroProfile /
 * Quarkus application.
 *
 * <p>
 * This sample shows:
 * <ul>
 * <li>Creating a public topic (anyone can submit messages)</li>
 * <li>Submitting a message to a topic</li>
 * <li>Deleting a topic</li>
 * </ul>
 *
 * <p>
 * The {@link TopicClient} CDI bean is automatically provided by the
 * hiero-enterprise-microprofile module. Inject it directly — no manual setup
 * required.
 */
@Path("/topics")
@Produces(MediaType.TEXT_PLAIN)
public class TopicSampleResource {

    private final TopicClient topicClient;

    @Inject
    public TopicSampleResource(final TopicClient topicClient) {
        this.topicClient = topicClient;
    }

    /**
     * Creates a new public topic on the Hiero network.
     *
     * <p>
     * Example: POST /topics?memo=my-first-topic
     *
     * @param memo optional memo to attach to the topic
     * @return the ID of the newly created topic
     */
    @POST
    public String createTopic(@QueryParam("memo") @DefaultValue("") final String memo) {
        try {
            final var topicId = memo.isEmpty()
                    ? topicClient.createTopic()
                    : topicClient.createTopic(memo);
            return "Topic created with ID: " + topicId;
        } catch (final HieroException e) {
            throw new RuntimeException("Failed to create topic", e);
        }
    }

    /**
     * Submits a text message to an existing topic.
     *
     * <p>
     * Example: POST /topics/0.0.12345/messages?message=hello
     *
     * @param topicId the topic ID
     * @param message the text message to submit
     * @return confirmation string
     */
    @POST
    @Path("/{topicId}/messages")
    public String submitMessage(
            @PathParam("topicId") final String topicId,
            @QueryParam("message") final String message) {
        try {
            topicClient.submitMessage(TopicId.fromString(topicId), message);
            return "Message submitted to topic " + topicId;
        } catch (final HieroException e) {
            throw new RuntimeException("Failed to submit message to topic " + topicId, e);
        }
    }

    /**
     * Deletes an existing topic from the Hiero network.
     *
     * <p>
     * Example: DELETE /topics/0.0.12345
     *
     * @param topicId the topic ID to delete
     * @return confirmation string
     */
    @DELETE
    @Path("/{topicId}")
    public String deleteTopic(@PathParam("topicId") final String topicId) {
        try {
            topicClient.deleteTopic(TopicId.fromString(topicId));
            return "Topic " + topicId + " deleted successfully";
        } catch (final HieroException e) {
            throw new RuntimeException("Failed to delete topic " + topicId, e);
        }
    }
}
