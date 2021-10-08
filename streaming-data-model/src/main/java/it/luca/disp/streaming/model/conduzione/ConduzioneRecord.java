package it.luca.disp.streaming.model.conduzione;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class ConduzioneRecord {

    public static final String VSTOCK_COR = "vstockCor";
    public static final String VPRDN = "vprdn";
    public static final String VINIET = "viniet";
    public static final String VPCS_STOCG = "vpcsStocg";
    public static final String VPCS_RCP = "vpcsRcp";
    public static final String PVER = "pver";
    public static final String QREC_TOT = "qrecTot";
    public static final String DRE = "dre";
    public static final String TIPO_AGGIORNAMENTO = "tipoAggiornamento";
    public static final String CCMP = "ccmp";
    public static final String NCMP = "ncmp";
    public static final String DRIF = "drif";
    public static final String VCNSM = "vcnsm";
    public static final String VSTOCK_TOT = "vstockTot";

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
    public ConduzioneRecord(@JsonProperty(VSTOCK_COR) Double vstockCor,
                            @JsonProperty(VPRDN) Double vprdn,
                            @JsonProperty(VINIET) Double viniet,
                            @JsonProperty(VPCS_STOCG) Double vpcsStocg,
                            @JsonProperty(VPCS_RCP) Double vpcsRcp,
                            @JsonProperty(PVER) Integer pver,
                            @JsonProperty(QREC_TOT) Integer qrecTot,
                            @JsonProperty(DRE) String dre,
                            @JsonProperty(TIPO_AGGIORNAMENTO) String tipoAggiornamento,
                            @JsonProperty(CCMP) String ccmp,
                            @JsonProperty(NCMP) String ncmp,
                            @JsonProperty(DRIF) String drif,
                            @JsonProperty(VCNSM) Double vcnsm,
                            @JsonProperty(VSTOCK_TOT) Double vstockTot) {

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