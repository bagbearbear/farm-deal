package com.farmdeal.global.img.repository;


import com.farmdeal.global.img.model.Img;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImgRepository extends JpaRepository<Img, Long> {
    List<Img> findByProduct_Id(Long id);
    void deleteByProduct_Id(Long id);
    List<Img> findByUser_id(Long id);

    List<Img> findByUser_Id(Long id);
    List<Img> deleteByUser_id(Long id);
}
