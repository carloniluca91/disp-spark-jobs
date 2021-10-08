package it.luca.disp.streaming.model.jarvis;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class JarvisPayload {

    private final String ambitoFlusso;
    private final String nomeFlusso;
    private final String impresaMittente;
    private final String dataDiCreazione;
    private final Integer numeroDati;
    private final String dataProcedura;
    private final String giornoGas;
    private final List<JarvisCiclo> cicli;

    @JsonCreator
    public JarvisPayload(@JsonProperty("ambitoFlusso") String ambitoFlusso,
                         @JsonProperty("nomeFlusso") String nomeFlusso,
                         @JsonProperty("impresaMittente") String impresaMittente,
                         @JsonProperty("dataDiCreazione") String dataDiCreazione,
                         @JsonProperty("numeroDati") Integer numeroDati,
                         @JsonProperty("dataProcedura") String dataProcedura,
                         @JsonProperty("giornoGas") String giornoGas,
                         @JsonProperty("listaCicli") List<JarvisCiclo> cicli) {

        this.ambitoFlusso = ambitoFlusso;
        this.nomeFlusso = nomeFlusso;
        this.impresaMittente = impresaMittente;
        this.dataDiCreazione = dataDiCreazione;
        this.numeroDati = numeroDati;
        this.dataProcedura = dataProcedura;
        this.giornoGas = giornoGas;
        this.cicli = cicli;
    }
}
