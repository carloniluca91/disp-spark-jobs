package it.luca.disp.streaming.model.conduzione;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class ConduzioneRecord {

    private final Double vstockCor;
    private final Double vprdn;
    private final Double viniet;
    private final Double vpcsStocg;
    private final Double vpcsRcp;
    private final Integer pver;
    private final Integer qrecTot;
    private final String dre;
    private final String tipoAggiornamento;
    private final String ccmp;
    private final String ncmp;
    private final String drif;
    private final Double vcnsm;
    private final Double vstockTot;

    @JsonCreator
    public ConduzioneRecord(@JsonProperty("vstockCor") Double vstockCor,
                            @JsonProperty("vprdn") Double vprdn,
                            @JsonProperty("viniet") Double viniet,
                            @JsonProperty("vpcsStocg") Double vpcsStocg,
                            @JsonProperty("vpcsRcp") Double vpcsRcp,
                            @JsonProperty("pver") Integer pver,
                            @JsonProperty("qrecTot") Integer qrecTot,
                            @JsonProperty("dre") String dre,
                            @JsonProperty("tipoAggiornamento") String tipoAggiornamento,
                            @JsonProperty("ccmp") String ccmp,
                            @JsonProperty("ncmp") String ncmp,
                            @JsonProperty("drif") String drif,
                            @JsonProperty("vcnsm") Double vcnsm,
                            @JsonProperty("vstockTot") Double vstockTot) {

        this.vstockCor = vstockCor;
        this.vprdn = vprdn;
        this.viniet = viniet;
        this.vpcsStocg = vpcsStocg;
        this.vpcsRcp = vpcsRcp;
        this.pver = pver;
        this.qrecTot = qrecTot;
        this.dre = dre;
        this.tipoAggiornamento = tipoAggiornamento;
        this.ccmp = ccmp;
        this.ncmp = ncmp;
        this.drif = drif;
        this.vcnsm = vcnsm;
        this.vstockTot = vstockTot;
    }
}