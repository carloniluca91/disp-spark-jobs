package it.luca.disp.streaming.model.int002;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class Int002Ciclo {

    public static final String GIORNO_ORA_RIFERIMENTO = "giornoOraRiferimento";
    public static final String UDM1 = "udm1";
    public static final String UDM2 = "udm2";
    public static final String UDM3 = "udm3";
    public static final String UDM4 = "udm4";
    public static final String DESCRIZIONE = "descrizione";
    public static final String TIPOLOGIA = "tipologia";
    public static final String CODICE_REMI = "codiceRemi";
    public static final String VALORE1 = "valore1";
    public static final String PROGRESSIVO1 = "progressivo1";
    public static final String VALORE2 = "valore2";
    public static final String PROGRESSIVO2 = "progressivo2";
    public static final String PCS = "pcs";
    public static final String VALORE3 = "valore3";
    public static final String PROGRESSIVO3 = "progressivo3";
    public static final String VALORE4 = "valore4";
    public static final String PROGRESSIVO4 = "progressivo4";
    public static final String PCS250 = "pcs250";
    public static final String WOBBE2515 = "wobbe2515";
    public static final String WOBBE250 = "wobbe250";

    private final String giornoOraRiferimento;
    private final String udm1;
    private final String udm2;
    private final String udm3;
    private final String udm4;
    private final String descrizione;
    private final String tipologia;
    private final String codiceRemi;
    private final Double valore1;
    private final Double progressivo1;
    private final Double valore2;
    private final Double progressivo2;
    private final Double pcs;
    private final Double valore3;
    private final Double progressivo3;
    private final Double valore4;
    private final Double progressivo4;
    private final Double pcs250;
    private final Double wobbe2515;
    private final Double wobbe250;

    @JsonCreator
    public Int002Ciclo(@JsonProperty(GIORNO_ORA_RIFERIMENTO) String giornoOraRiferimento,
                       @JsonProperty(UDM1) String udm1,
                       @JsonProperty(UDM2) String udm2,
                       @JsonProperty(UDM3) String udm3,
                       @JsonProperty(UDM4) String udm4,
                       @JsonProperty(DESCRIZIONE) String descrizione,
                       @JsonProperty(TIPOLOGIA) String tipologia,
                       @JsonProperty(CODICE_REMI) String codiceRemi,
                       @JsonProperty(VALORE1) Double valore1,
                       @JsonProperty(PROGRESSIVO1) Double progressivo1,
                       @JsonProperty(VALORE2) Double valore2,
                       @JsonProperty(PROGRESSIVO2) Double progressivo2,
                       @JsonProperty(PCS) Double pcs,
                       @JsonProperty(VALORE3) Double valore3,
                       @JsonProperty(PROGRESSIVO3) Double progressivo3,
                       @JsonProperty(VALORE4) Double valore4,
                       @JsonProperty(PROGRESSIVO4) Double progressivo4,
                       @JsonProperty(PCS250) Double pcs250,
                       @JsonProperty(WOBBE2515) Double wobbe2515,
                       @JsonProperty(WOBBE250) Double wobbe250) {

        this.giornoOraRiferimento = giornoOraRiferimento;
        this.udm1 = udm1;
        this.udm2 = udm2;
        this.udm3 = udm3;
        this.udm4 = udm4;
        this.descrizione = descrizione;
        this.tipologia = tipologia;
        this.codiceRemi = codiceRemi;
        this.valore1 = valore1;
        this.progressivo1 = progressivo1;
        this.valore2 = valore2;
        this.progressivo2 = progressivo2;
        this.pcs = pcs;
        this.valore3 = valore3;
        this.progressivo3 = progressivo3;
        this.valore4 = valore4;
        this.progressivo4 = progressivo4;
        this.pcs250 = pcs250;
        this.wobbe2515 = wobbe2515;
        this.wobbe250 = wobbe250;
    }
}