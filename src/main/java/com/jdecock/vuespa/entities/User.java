package com.jdecock.vuespa.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Getter @Setter
	private Long id;

	@Getter @Setter
	private String name;

	@Getter @Setter
	private String email;

	@Getter @Setter
	private String password;

	@Getter @Setter
	private String passwordSalt;

	@Getter @Setter
	private String confirmationToken;

	@Getter @Setter
	private Date confirmationTokenTime;

	@Getter @Setter
	private String recoveryToken;

	@Getter @Setter
	private Date recoveryTokenTime;

	@Getter @Setter
	private Boolean disabled;

	@Getter @Setter
	private String disabledNote;

	@Getter @Setter
	private Date creationDate;

	@Getter @Setter
	private Date lastModifiedDate;
}
