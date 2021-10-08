package it.luca.disp.streaming.app.job;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.luca.disp.streaming.core.job.Consumer;
import it.luca.disp.streaming.core.job.StreamingJob;
import lombok.Getter;

import static java.util.Objects.requireNonNull;

@Getter
public class StreamingDataSource<T> {

    public static final String BOOTSTRAP_SERVERS = "bootstrapServers";
    public static final String GROUP_ID = "groupId";
    public static final String TOPIC = "topic";
    public static final String TOPIC_PARTITION = "topicPartition";
    public static final String AUTO_OFFSET_RESET = "autoOffsetReset";
    public static final String MAX_POLL_RECORDS = "maxPollRecords";
    public static final String DATA_CLASS = "dataClass";
    public static final String STREAMING_JOB_CLASS = "streamingJobClass";
    public static final String CONSUMER_CLASS = "consumerClass";

    private final String bootstrapServers;
    private final String groupId;
    private final String topic;
    private final Integer topicPartition;
    private final String autoOffsetReset;
    private final Integer maxPollRecords;
    private final Class<T> dataClass;
    private final Class<? extends StreamingJob<T>> streamingJobClass;
    private final Class<? extends Consumer<T>> consumerClass;

    @SuppressWarnings("unchecked")
    @JsonCreator
    public StreamingDataSource(@JsonProperty(BOOTSTRAP_SERVERS) String bootstrapServers,
                               @JsonProperty(GROUP_ID) String groupId,
                               @JsonProperty(TOPIC) String topic,
                               @JsonProperty(TOPIC_PARTITION) Integer topicPartition,
                               @JsonProperty(AUTO_OFFSET_RESET) String autoOffsetReset,
                               @JsonProperty(MAX_POLL_RECORDS) Integer maxPollRecords,
                               @JsonProperty(DATA_CLASS) String dataClass,
                               @JsonProperty(STREAMING_JOB_CLASS) String streamingJobClass,
                               @JsonProperty(CONSUMER_CLASS) String consumerClass) throws ClassNotFoundException {

        this.bootstrapServers = requireNonNull(bootstrapServers, BOOTSTRAP_SERVERS);
        this.groupId = requireNonNull(groupId, GROUP_ID);
        this.topic = requireNonNull(topic, TOPIC);
        this.topicPartition = requireNonNull(topicPartition, TOPIC_PARTITION);
        this.autoOffsetReset = requireNonNull(autoOffsetReset, AUTO_OFFSET_RESET);
        if (requireNonNull(maxPollRecords, MAX_POLL_RECORDS) <= 0) {
            throw new IllegalArgumentException(String.format("Illegal number of maximum polled records: %s", maxPollRecords));
        }

        this.maxPollRecords = maxPollRecords;
        this.dataClass = (Class<T>) Class.forName(requireNonNull(dataClass, DATA_CLASS));
        this.streamingJobClass = (Class<? extends StreamingJob<T>>) Class.forName(requireNonNull(streamingJobClass, STREAMING_JOB_CLASS));
        this.consumerClass = (Class<? extends Consumer<T>>) Class.forName(requireNonNull(consumerClass, CONSUMER_CLASS));
    }
}
