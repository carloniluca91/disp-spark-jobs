package it.luca.disp.streaming.model.int002;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class Int002Payload {

    public static final String CICLI = "cicli";

    private final List<Int002Ciclo> cicli;

    @JsonCreator
    public Int002Payload(@JsonProperty(CICLI) List<Int002Ciclo> cicli) {

        this.cicli = cicli;
    }
}
