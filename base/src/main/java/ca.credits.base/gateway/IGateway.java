package ca.credits.base.gateway;

import ca.credits.base.IExecutive;

/**
 * Created by chenwen on 16/8/26.
 */
public interface IGateway {

    /**
     * suggest next event
     * @param executive this is current event
     * @return
     */
    GatewaySuggest suggest(IExecutive executive);

    /**
     * gateway suggest
     */
    enum GatewaySuggest{
        /**
         * undo
         */
        UNDO,
        /**
         * next
         */
        NEXT,
        /**
         * exception
         */
        EXCEPTION;

        /**
         * this || gatewaySuggest
         * @param gatewaySuggest gatewaySuggest
         * @return GatewaySuggest
         */
        public GatewaySuggest or(GatewaySuggest gatewaySuggest){
            return this == GatewaySuggest.NEXT || gatewaySuggest == GatewaySuggest.NEXT
                    ? GatewaySuggest.NEXT : this == GatewaySuggest.EXCEPTION && gatewaySuggest == GatewaySuggest.EXCEPTION
                    ? GatewaySuggest.EXCEPTION : GatewaySuggest.UNDO;
        }

        /**
         * this && gatewaySuggest
         * @param gatewaySuggest gatewaySuggest
         * @return GatewaySuggest
         */
        public GatewaySuggest and(GatewaySuggest gatewaySuggest){
            return this == GatewaySuggest.NEXT && gatewaySuggest == GatewaySuggest.NEXT
                    ? GatewaySuggest.NEXT : this == GatewaySuggest.EXCEPTION || gatewaySuggest == GatewaySuggest.EXCEPTION
                    ? GatewaySuggest.EXCEPTION : GatewaySuggest.UNDO;
        }
    }
}
