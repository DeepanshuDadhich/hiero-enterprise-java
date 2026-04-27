package org.hiero.spring.sample;

import com.hedera.hashgraph.sdk.TopicId;
import java.util.Objects;
import org.hiero.base.HieroException;
import org.hiero.base.TopicClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Sample REST controller demonstrating how to use {@link TopicClient} in a
 * Spring Boot application.
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
 * The {@link TopicClient} bean is automatically provided by the
 * hiero-enterprise-spring module
 * via auto-configuration. No manual wiring is needed.
 */
@RestController
@RequestMapping("/topics")
public class TopicSampleEndpoint {

    private final TopicClient topicClient;

    public TopicSampleEndpoint(final TopicClient topicClient) {
        this.topicClient = Objects.requireNonNull(topicClient, "topicClient must not be null");
    }

    /**
     * Creates a new public topic on the Hiero network.
     *
     * <p>
     * Example: POST /topics?memo=my-first-topic
     *
     * @param memo optional memo to attach to the topic
     * @return the ID of the newly created topic (e.g. "0.0.12345")
     */
    @PostMapping
    public String createTopic(@RequestParam(required = false, defaultValue = "") final String memo) {
        try {
            final TopicId topicId = memo.isEmpty()
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
     * @param topicId the topic ID to submit the message to
     * @param message the text message to submit
     * @return confirmation string
     */
    @PostMapping("/{topicId}/messages")
    public String submitMessage(
            @PathVariable final String topicId,
            @RequestParam final String message) {
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
    @DeleteMapping("/{topicId}")
    public String deleteTopic(@PathVariable final String topicId) {
        try {
            topicClient.deleteTopic(TopicId.fromString(topicId));
            return "Topic " + topicId + " deleted successfully";
        } catch (final HieroException e) {
            throw new RuntimeException("Failed to delete topic " + topicId, e);
        }
    }
}
