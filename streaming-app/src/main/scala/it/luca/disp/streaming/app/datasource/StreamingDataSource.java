package it.luca.disp.streaming.app.datasource;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.luca.disp.streaming.core.job.Consumer;
import it.luca.disp.streaming.core.job.StreamingJob;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.spark.sql.SparkSession;

import java.sql.Connection;
import java.util.Collections;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * Class representing all information required in order to initialize a streaming dataSource
 * @param <T> type of data to consume
 */

@Slf4j
@Getter
public class StreamingDataSource<T> {

    public static final String ID = "id";
    public static final String BOOTSTRAP_SERVERS = "bootstrapServers";
    public static final String GROUP_ID = "groupId";
    public static final String TOPIC_NAME = "topicName";
    public static final String TOPIC_PARTITION = "topicPartition";
    public static final String AUTO_OFFSET_RESET = "autoOffsetReset";
    public static final String MAX_POLL_RECORDS = "maxPollRecords";
    public static final String DATA_CLASS = "dataClass";
    public static final String STREAMING_JOB_CLASS = "streamingJobClass";
    public static final String CONSUMER_CLASS = "consumerClass";

    public static final String DEFAULT_AUTOOFFSET_RESET = "earliest";
    public static final Integer DEFAULT_MAX_POLL_RECORDS = 30;

    private final String id;
    private final String bootstrapServers;
    private final String groupId;
    private final String topicName;
    private final Integer topicPartition;
    private final String autoOffsetReset;
    private final Integer maxPollRecords;
    private final Class<T> dataClass;
    private final Class<? extends StreamingJob<T>> streamingJobClass;
    private final Class<? extends Consumer<T>> consumerClass;

    @SuppressWarnings("unchecked")
    @JsonCreator
    public StreamingDataSource(@JsonProperty(ID) String id,
                               @JsonProperty(BOOTSTRAP_SERVERS) String bootstrapServers,
                               @JsonProperty(GROUP_ID) String groupId,
                               @JsonProperty(TOPIC_NAME) String topicName,
                               @JsonProperty(TOPIC_PARTITION) Integer topicPartition,
                               @JsonProperty(AUTO_OFFSET_RESET) String autoOffsetReset,
                               @JsonProperty(MAX_POLL_RECORDS) Integer maxPollRecords,
                               @JsonProperty(DATA_CLASS) String dataClass,
                               @JsonProperty(STREAMING_JOB_CLASS) String streamingJobClass,
                               @JsonProperty(CONSUMER_CLASS) String consumerClass) throws ClassNotFoundException {

        this.id = id;
        this.bootstrapServers = requireNonNull(bootstrapServers, BOOTSTRAP_SERVERS);
        this.groupId = requireNonNull(groupId, GROUP_ID);
        this.topicName = requireNonNull(topicName, TOPIC_NAME);
        this.topicPartition = requireNonNull(topicPartition, TOPIC_PARTITION);
        this.autoOffsetReset = Optional.ofNullable(autoOffsetReset).orElse(DEFAULT_AUTOOFFSET_RESET);
        this.maxPollRecords = Optional.ofNullable(maxPollRecords)
                .filter(x -> x != 0)
                .orElse(DEFAULT_MAX_POLL_RECORDS);

        this.dataClass = (Class<T>) Class.forName(requireNonNull(dataClass, DATA_CLASS));
        this.streamingJobClass = (Class<? extends StreamingJob<T>>) Class.forName(requireNonNull(streamingJobClass, STREAMING_JOB_CLASS));
        this.consumerClass = (Class<? extends Consumer<T>>) Class.forName(requireNonNull(consumerClass, CONSUMER_CLASS));
    }

    /**
     * Initialize a concrete {@link Consumer} related to this instance
     * @param sparkSession {@link SparkSession} for streaming job initialization
     * @param connection Impala {@link Connection} for streaming job initialization
     * @param properties {@link PropertiesConfiguration} for streaming job initialization
     * @return concrete instance of {@link Consumer}
     * @throws Exception if initialization fails
     */

    public Consumer<T> initConsumer(SparkSession sparkSession,
                                    Connection connection,
                                    PropertiesConfiguration properties) throws Exception {


        StreamingJob<T> streamingJob = streamingJobClass
                .getDeclaredConstructor(SparkSession.class, Connection.class, PropertiesConfiguration.class)
                .newInstance(sparkSession, connection, properties);

        log.info("Successfully initialized instance of {}", streamingJobClass.getSimpleName());
        KafkaConsumer<String, String> kafkaConsumer = initKafkaConsumer();

        Consumer<T> tConsumer = consumerClass
                .getDeclaredConstructor(KafkaConsumer.class, streamingJobClass)
                .newInstance(kafkaConsumer, streamingJob);

        log.info("Successfully initialized instance of {}", consumerClass.getSimpleName());
        return tConsumer;
    }

    /**
     * Init a {@link KafkaConsumer} using Kafka properties from this instance
     * @return instance of {@link KafkaConsumer} with assigned {@link TopicPartition}
     */

    protected KafkaConsumer<String, String> initKafkaConsumer() {

        // Consumer properties
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, String.valueOf(maxPollRecords));

        // Topic partition to assign
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);
        TopicPartition assignedTopic = new TopicPartition(topicName, topicPartition);
        consumer.assign(Collections.singleton(assignedTopic));

        String propertiesSummary = properties.keySet().stream().map(x -> {
                    String key = String.valueOf(x);
                    String value = properties.getProperty(key);
                    return String.format("  %s = %s", key, value);
                }).collect(Collectors.joining("\n"))
                .concat("\n")
                .concat(String.format("  %s = %s\n", "topic.name", topicName))
                .concat(String.format("  %s = %s\n", "topic.partition", topicPartition));

        log.info("Successfully created {} with following properties\n\n{}", KafkaConsumer.class.getSimpleName(), propertiesSummary);
        return consumer;
    }
}
