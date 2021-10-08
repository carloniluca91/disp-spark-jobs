package it.luca.disp.streaming.model.webdisp;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class WebdispNomina {

    public static final String UNITA_MISURA_ENERGIA = "unitaMisuraEnergia";
    public static final String PCS = "pcs";
    public static final String DATA_DECORRENZA = "dataDecorrenza";
    public static final String DATA_ELABORAZIONE = "dataElaborazione";
    public static final String VALORE_VOLUME = "valoreVolume";
    public static final String UNITA_MISURA_VOLUME = "unitaMisuraVolume";
    public static final String VALORE_ENERGIA = "valoreEnergia";
    public static final String CODICE_REMI = "codiceRemi";
    public static final String DESCRIZIONE_REMI = "descrizioneRemi";
    public static final String DESCRIZIONE_PUNTO = "descrizionePunto";
    public static final String TIPO_NOMINA = "tipoNomina";
    public static final String CICLO_NOMINA = "cicloNomina";
    public static final String TIPOLOGIA_PUNTO = "tipologiaPunto";

    private final String unitaMisuraEnergia;
    private final Double pcs;
    private final String dataDecorrenza;
    private final String dataElaborazione;
    private final Double valoreVolume;
    private final String unitaMisuraVolume;
    private final Double valoreEnergia;
    private final String codiceRemi;
    private final String descrizioneRemi;
    private final String descrizionePunto;
    private final String tipoNomina;
    private final String cicloNomina;
    private final String tipologiaPunto;

    @JsonCreator
    public WebdispNomina(@JsonProperty(UNITA_MISURA_ENERGIA) String unitaMisuraEnergia,
                         @JsonProperty(PCS) Double pcs,
                         @JsonProperty(DATA_DECORRENZA) String dataDecorrenza,
                         @JsonProperty(DATA_ELABORAZIONE) String dataElaborazione,
                         @JsonProperty(VALORE_VOLUME) Double valoreVolume,
                         @JsonProperty(UNITA_MISURA_VOLUME) String unitaMisuraVolume,
                         @JsonProperty(VALORE_ENERGIA) Double valoreEnergia,
                         @JsonProperty(CODICE_REMI) String codiceRemi,
                         @JsonProperty(DESCRIZIONE_REMI) String descrizioneRemi,
                         @JsonProperty(DESCRIZIONE_PUNTO) String descrizionePunto,
                         @JsonProperty(TIPO_NOMINA) String tipoNomina,
                         @JsonProperty(CICLO_NOMINA) String cicloNomina,
                         @JsonProperty(TIPOLOGIA_PUNTO) String tipologiaPunto) {

        this.unitaMisuraEnergia = unitaMisuraEnergia;
        this.pcs = pcs;
        this.dataDecorrenza = dataDecorrenza;
        this.dataElaborazione = dataElaborazione;
        this.valoreVolume = valoreVolume;
        this.unitaMisuraVolume = unitaMisuraVolume;
        this.valoreEnergia = valoreEnergia;
        this.codiceRemi = codiceRemi;
        this.descrizioneRemi = descrizioneRemi;
        this.descrizionePunto = descrizionePunto;
        this.tipoNomina = tipoNomina;
        this.cicloNomina = cicloNomina;
        this.tipologiaPunto = tipologiaPunto;
    }
}
