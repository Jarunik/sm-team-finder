package org.slos.splinterlands.settings;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SMSettings {
    private int lastBlock;
    private int transfer_cooldown_blocks;

    public SMSettings(@JsonProperty("last_block") int lastBlock, @JsonProperty("transfer_cooldown_blocks") int transferCooldownBlocks) {
        this.lastBlock = lastBlock;
        this.transfer_cooldown_blocks = transferCooldownBlocks;
    }

    public int getLastBlock() {
        return lastBlock;
    }

    public int getTransfer_cooldown_blocks() {
        return transfer_cooldown_blocks;
    }
}
