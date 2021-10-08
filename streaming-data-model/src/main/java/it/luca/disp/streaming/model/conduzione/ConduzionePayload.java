package it.luca.disp.streaming.model.conduzione;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class ConduzionePayload {

    public static final String RECORDS = "records";

    private final List<ConduzioneRecord> records;

    @JsonCreator
    public ConduzionePayload(@JsonProperty(RECORDS) List<ConduzioneRecord> records) {

        this.records = records;
    }
}
