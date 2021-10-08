package it.luca.disp.streaming.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import static java.util.Objects.requireNonNull;

@Getter
public class MsgWrapper<T> {

    public static final String MESSAGE_TS = "messageTs";
    public static final String MESSAGE_DT = "messageDt";
    public static final String PAYLOAD = "payload";

    private final String messageTs;
    private final String messageDt;
    private final T payload;

    @JsonCreator
    public MsgWrapper(@JsonProperty(MESSAGE_TS) String messageTs,
                      @JsonProperty(MESSAGE_DT) String messageDt,
                      @JsonProperty(PAYLOAD) T payload) {

        this.messageTs = requireNonNull(messageTs, MESSAGE_TS);
        this.messageDt = requireNonNull(messageDt, MESSAGE_DT);
        this.payload = requireNonNull(payload, PAYLOAD);
    }
}

