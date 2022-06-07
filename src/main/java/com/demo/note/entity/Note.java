package com.demo.note.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "note")
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "nid")
    private Integer nid;

    @Column (name = "title")
    private String title;
    @Column (name = "information")
    private String information;
    @Column (name = "sdate")
    private String sdate;

    @Column (name = "uid")
    private int uid;

}
