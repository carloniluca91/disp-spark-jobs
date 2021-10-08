package it.luca.disp.streaming.model.webdisp;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class WebdispPayload {

    public static final String DATA_ORA_INVIO = "dataOraInvio";
    public static final String NOMINE = "nomine";

    private final String dataOraInvio;
    private final List<WebdispNomina> nomine;

    @JsonCreator
    public WebdispPayload(@JsonProperty(DATA_ORA_INVIO) String dataOraInvio,
                          @JsonProperty(NOMINE) List<WebdispNomina> nomine) {

        this.dataOraInvio = dataOraInvio;
        this.nomine = nomine;
    }
}
