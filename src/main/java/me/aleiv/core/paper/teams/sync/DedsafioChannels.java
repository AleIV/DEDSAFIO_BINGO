package me.aleiv.core.paper.teams.sync;

public enum DedsafioChannels {
    EVENTS, SYNC, AUTH;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }

    /**
     * @return The full pipeline name of the channel
     */
    public String fullName() {
        return "dedsafio:" + this.toString();
    }
}
