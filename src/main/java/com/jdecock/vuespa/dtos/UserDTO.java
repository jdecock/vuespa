package com.jdecock.vuespa.dtos;

import com.jdecock.vuespa.entities.User;
import com.jdecock.vuespa.entities.UserRoleType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@NoArgsConstructor
public class UserDTO {
	private Long id;

	@Setter
	private String name;

	@Setter
	private String email;

	@Setter
	private List<UserRoleType> roles;

	@Setter
	// The password field is ONLY used for passing data from the client to the server. The password in the database
	// is hashed and can't be un-hashed.
	private String plainTextPassword;

	@Setter
	private Boolean disabled;

	@Setter
	private String disabledNote;

	private Date creationDate;

	private Date lastModifiedDate;

	public UserDTO(User user) {
		if (user == null)
			return;

		this.id = user.getId();
		this.name = user.getName();
		this.email = user.getEmail();
		this.disabled = user.isDisabled();
		this.disabledNote = user.getDisabledNote();
		this.creationDate = user.getCreationDate();
		this.lastModifiedDate = user.getLastModifiedDate();
		this.roles = Stream.of(user.getRoles().split(",")).map(UserRoleType::valueOf).collect(Collectors.toList());
	}
}
