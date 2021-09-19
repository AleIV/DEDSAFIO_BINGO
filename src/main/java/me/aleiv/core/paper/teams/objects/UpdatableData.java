package me.aleiv.core.paper.teams.objects;

/**
 * Interface for general methods to update data, locally and remotely, and to
 * process such updates as they are recieved.
 * 
 * Note: currently not being used, using interfaces would've made the dev
 * process harder. Will implement later ofc.
 */
public interface UpdatableData {

    void updateField(String field, Object value);

    void sendUpdate(String channel, Object value);

    void processUpdate(String channel, Object value);

}
