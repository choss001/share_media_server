package com.media.share.model;


import jakarta.persistence.*;

@Entity
@Table(name = "sec_role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column
    private ERole name;

    public ERole getName() {return name;}
}
