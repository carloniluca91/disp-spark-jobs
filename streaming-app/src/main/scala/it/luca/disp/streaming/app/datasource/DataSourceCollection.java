package it.luca.disp.streaming.app.datasource;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

@Slf4j
@Getter
public class DataSourceCollection {

    public static final String ALL = "ALL";
    public static final String DATA_SOURCES = "dataSources";

    private final List<StreamingDataSource<?>> dataSources;

    @JsonCreator
    public DataSourceCollection(@JsonProperty(DATA_SOURCES) List<StreamingDataSource<?>> dataSources) {

        this.dataSources = requireNonNull(dataSources, DATA_SOURCES);
    }

    /**
     * Retrieve a collection of {@link StreamingDataSource} to be then triggered
     * @param ids collection of strings representing some {@link StreamingDataSource}'s id
     * @return a collection of {@link StreamingDataSource}
     */

    public List<StreamingDataSource<?>> getDataSourcesForIds(List<String> ids) {

        String dataSourceClass = StreamingDataSource.class.getSimpleName();
        if (ids.size() == 1 && ids.get(0).equalsIgnoreCase(ALL)) {
            log.info("Triggering all of {} available {}(s)", dataSources.size(), dataSourceClass);
            return dataSources;
        } else {
            log.info("Starting to retrieve {} for each given id", dataSourceClass);
            List<StreamingDataSource<?>> matchedDataSources = ids.stream().map(this::getDataSourceWithId).collect(Collectors.toList());
            log.info("Successfully matched {} for each given id", dataSourceClass);
            return matchedDataSources;
        }
    }

    /**
     * Retrieve the {@link StreamingDataSource} related to given id
     * @param id dataSource id
     * @return instance of {@link StreamingDataSource} related to input id
     * @throws IllegalArgumentException if input id does not match any {@link StreamingDataSource}
     */

    protected StreamingDataSource<?> getDataSourceWithId(String id)throws IllegalArgumentException {

        String dataSourceClass = StreamingDataSource.class.getSimpleName();
        Optional<StreamingDataSource<?>> optionalDs = dataSources.stream()
                .filter(x -> x.getId().equalsIgnoreCase(id))
                .findFirst();

        if (optionalDs.isPresent()) {
            log.info("Successfully found {} related to id {}", dataSourceClass, id);
            return optionalDs.get();
        } else {
            throw new IllegalArgumentException(String.format("Illegal %s id: %s", dataSourceClass, id));
        }
    }
}
