package com.jdecock.vuespa.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Entity
@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	private String email;

	private String roles;

	private String password;

	@Column(name = "passwordSalt")
	private String passwordSalt;

	@Column(name = "confirmationToken")
	private String confirmationToken;

	@Column(name = "confirmationTokenTime")
	private Date confirmationTokenTime;

	@Column(name = "recoveryToken")
	private String recoveryToken;

	@Column(name = "recoveryTokenTime")
	private Date recoveryTokenTime;

	private Boolean disabled;

	@Column(name = "disabledNote")
	private String disabledNote;

	@Column(name = "creationDate")
	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	private Date creationDate;

	@Column(name = "lastModifiedDate")
	@UpdateTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastModifiedDate;
}
