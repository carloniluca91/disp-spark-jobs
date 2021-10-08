package it.luca.disp.streaming.model.jarvis;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class JarvisPayload {

    public static final String AMBITO_FLUSSO = "ambitoFlusso";
    public static final String NOME_FLUSSO = "nomeFlusso";
    public static final String IMPRESA_MITTENTE = "impresaMittente";
    public static final String DATA_DI_CREAZIONE = "dataDiCreazione";
    public static final String NUMERO_DATI = "numeroDati";
    public static final String DATA_PROCEDURA = "dataProcedura";
    public static final String GIORNO_GAS = "giornoGas";
    public static final String LISTA_CICLI = "listaCicli";

    private final String ambitoFlusso;
    private final String nomeFlusso;
    private final String impresaMittente;
    private final String dataDiCreazione;
    private final Integer numeroDati;
    private final String dataProcedura;
    private final String giornoGas;
    private final List<JarvisCiclo> cicli;

    @JsonCreator
    public JarvisPayload(@JsonProperty(AMBITO_FLUSSO) String ambitoFlusso,
                         @JsonProperty(NOME_FLUSSO) String nomeFlusso,
                         @JsonProperty(IMPRESA_MITTENTE) String impresaMittente,
                         @JsonProperty(DATA_DI_CREAZIONE) String dataDiCreazione,
                         @JsonProperty(NUMERO_DATI) Integer numeroDati,
                         @JsonProperty(DATA_PROCEDURA) String dataProcedura,
                         @JsonProperty(GIORNO_GAS) String giornoGas,
                         @JsonProperty(LISTA_CICLI) List<JarvisCiclo> cicli) {

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
