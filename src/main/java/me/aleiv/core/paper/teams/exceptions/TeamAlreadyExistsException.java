package me.aleiv.core.paper.teams.exceptions;

public class TeamAlreadyExistsException extends Exception {
    public TeamAlreadyExistsException(String teamName) {
        super(String.format("Team %s already exists", teamName));
    }

    public static TeamAlreadyExistsException of(String teamName) {
        return new TeamAlreadyExistsException(teamName);
    }
}
