package io.bacta.game.object.universe.guild;

import lombok.Getter;

public class Guild {
	@Getter private int id;
	@Getter private String abbreviation;
	@Getter private String name;
	@Getter private Long leaderId;
}
