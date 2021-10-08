package it.luca.disp.streaming.model.int002;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class Int002Ciclo {

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
    public Int002Ciclo(@JsonProperty("giornoOraRiferimento") String giornoOraRiferimento,
                       @JsonProperty("udm1") String udm1,
                       @JsonProperty("udm2") String udm2,
                       @JsonProperty("udm3") String udm3,
                       @JsonProperty("udm4") String udm4,
                       @JsonProperty("descrizione") String descrizione,
                       @JsonProperty("tipologia") String tipologia,
                       @JsonProperty("codiceRemi") String codiceRemi,
                       @JsonProperty("valore1") Double valore1,
                       @JsonProperty("progressivo1") Double progressivo1,
                       @JsonProperty("valore2") Double valore2,
                       @JsonProperty("progressivo2") Double progressivo2,
                       @JsonProperty("pcs") Double pcs,
                       @JsonProperty("valore3") Double valore3,
                       @JsonProperty("progressivo3") Double progressivo3,
                       @JsonProperty("valore4") Double valore4,
                       @JsonProperty("progressivo4") Double progressivo4,
                       @JsonProperty("pcs250") Double pcs250,
                       @JsonProperty("wobbe2515") Double wobbe2515,
                       @JsonProperty("wobbe250") Double wobbe250) {

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