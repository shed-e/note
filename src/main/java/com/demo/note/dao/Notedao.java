package com.demo.note.dao;

import com.demo.note.entity.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface Notedao extends JpaRepository<Note,Integer> {
    Note save(Note note);

    void deleteByNid(int nid);


    List<Note> findByTitleLikeAndUid(String title,int uid);

    Note findByNid(int nid);

    List<Note> findAllByUid(int uid);



    //Page<Note> getpage(int pageNum,int pageSize);

/**@Query(" from Note k where k.uid = ?1")
    Page<Note> getnotepage(int uid, Pageable pageable);**/






}
